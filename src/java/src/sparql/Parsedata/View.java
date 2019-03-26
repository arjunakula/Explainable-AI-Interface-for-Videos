package sparql.Parsedata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sparql.MseeDataset;
import sparql.MseeException;
import sparql.SimpleBoundingBox;
import sparql.TimeFunctions;
import TextParser.CEventData;
import TextParser.CEventDataset;
import TextParser.CObjectData;
import TextParser.CObjectDataset;


public class View {

	public String id ="";
	String observer_id="";
	String data_uri="";
	String metadata_uri="";
	
	String track_data="";
	String event_data="";
	
	String pathPrefix="";
	
	String hview2map_uri = "";
	String distortion_file = "";
	
	public double frameRate = 30.0;
	double frameRateTrackData = 0.0;
	double imageWidthScale = 1.0;
	
	double hScale = 1.0; 		// scale factor for homography;
	
	// bounding box in cartesian coordinates; this is used for a room
	double CarteMinX = -1.0; 	
	double CarteMaxX = -1.0; 	
	double CarteMinY = -1.0; 		
	double CarteMaxY = -1.0; 	
	
	
	public static int subsample = 24; 
	
	public CEventDataset eventDataset = new CEventDataset();
	public CObjectDataset objectDataset = new CObjectDataset();
	public ViewMetadata metadata = new ViewMetadata();
	
	public Homography hView2Map = new Homography();
	
	public DistortionParam distortion_param = null; 
	
	public View(Node viewNode, String arg_id, String arg_pathPrefix) {		
		this.id = arg_id; 
		this.pathPrefix = arg_pathPrefix;
		
		
		Element viewElement= (Element)viewNode;
		
		NodeList nodelist = viewElement.getChildNodes();
		for (int count = 0; count < nodelist.getLength(); count++) {
			Node node = nodelist.item(count);
			if (node.getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
				String node_name = node.getNodeName(); 
				if (node_name.equals("ObserverId")) {
					this.observer_id = node.getTextContent().trim();					
				} else if (node_name.equals("DataURI")) {
					this.data_uri = node.getTextContent().trim();					
				}
				else if (node_name.equals("MetadataURI")) {
					this.metadata_uri = node.getTextContent().trim();					
				}
				else if (node_name.equals("TrackData")) {
					this.track_data = node.getTextContent().trim();					
				}
				else if (node_name.equals("EventData")) {
					this.event_data = node.getTextContent().trim();					
				}
				else if (node_name.equals("FrameRate")) {
					this.frameRate = Double.parseDouble(node.getTextContent().trim());					
				}
				else if (node_name.equals("FrameRateTrackData")) {
					this.frameRateTrackData = Double.parseDouble(node.getTextContent().trim());					
				}				
				else if (node_name.equals("ImageWidthScale")) {
					this.imageWidthScale = Double.parseDouble(node.getTextContent().trim());					
				}					
				else if (node_name.equals("Hview2map")) {
					this.hview2map_uri = node.getTextContent().trim();					
				}				
				else if (node_name.equals("Distortion")) {
					this.distortion_file = node.getTextContent().trim();					
				}			
				
				else if (node_name.equals("HScale")) {
					this.hScale = Double.parseDouble(node.getTextContent().trim());					
				}
				else if (node_name.equals("CarteMinX")) {
					this.CarteMinX = Double.parseDouble(node.getTextContent().trim());					
				}
				else if (node_name.equals("CarteMaxX")) {
					this.CarteMaxX = Double.parseDouble(node.getTextContent().trim());					
				}	
				else if (node_name.equals("CarteMinY")) {
					this.CarteMinY = Double.parseDouble(node.getTextContent().trim());					
				}	
				else if (node_name.equals("CarteMaxY")) {
					this.CarteMaxY = Double.parseDouble(node.getTextContent().trim());					
				}
				
				
				else {
					System.err.println(
							"WARNING in View, unrecognize xml node " +
							"argument, \"" + node_name + "\", was found.");
				}
			}
		}
		
			
		/*
		// debug
		System.out.println("DEBUG View id: " + this.id);
		System.out.println("DEBUG View observer_id: " + this.observer_id);
		System.out.println("DEBUG View data_uri: " + this.data_uri);
		System.out.println("DEBUG View metadata_uri: " + this.metadata_uri);
		System.out.println("DEBUG View track_data: " + this.track_data);
		System.out.println("DEBUG View event_data: " + this.event_data);			
		System.out.println("DEBUG View frameRate: " + this.frameRate);
		*/ 
		
		
		// load tracking data
		
		double frameRateScale = 1.0;
		if (this.frameRateTrackData!=0)
		{
			frameRateScale = (double) this.frameRate / frameRateTrackData;
			System.out.println("DEBUG frameRateScale " + frameRateScale);
		}
		
		if (this.track_data.length() > 0)
		{
			if (track_data.contains(".xml"))
			{
				this.objectDataset.LoadXml(pathPrefix + "\\" + track_data, frameRateScale, observer_id, id, data_uri);
			} else
			{	this.objectDataset.LoadObjectFromTextFile(pathPrefix + "\\" + track_data, frameRateScale, observer_id, id, data_uri);
			}
			
		}
		if (this.event_data.length() > 0)
		{
			this.eventDataset.LoadXml(pathPrefix + "\\" + event_data, frameRateScale, observer_id, id, data_uri);
		}
		
		
		if (this.hview2map_uri.length() > 0)
		{
			this.hView2Map.LoadFile(this.hScale, pathPrefix + "\\" + hview2map_uri);
		}
		
		if (this.distortion_file.length() > 0)
		{
			this.distortion_param = new DistortionParam(pathPrefix + "\\" + distortion_file);
		}	
		
		
		if (this.metadata_uri.length() >0)
		{
			try {
				this.metadata.LoadXml(pathPrefix + "\\" + metadata_uri);
			} catch (MseeException e) {
				System.err.println("ERROR View; Loadxml; MseeException" + e.toString());
			} catch (IOException e) {
				System.err.println("ERROR View; Loadxml; IOException" + e.toString());
			} catch (ParserConfigurationException e) {
				System.err.println("ERROR View; Loadxml; ParserConfigurationException" + e.toString());
				e.printStackTrace();
			} catch (SAXException e) {
				System.err.println("ERROR View; Loadxml; SAXException" + e.toString());
				e.printStackTrace();
			}			
		}		
		
		if ((true) && (subsample>1))
		{
			//subsample
			objectDataset.SubsamplePos(subsample);
			// eventDataset.SubsamplePos(subsample);
		}
		
		if (metadata.mSceneTime_Start!=null)
		{
			eventDataset.ComputeSceneCentricTime(metadata.mSceneTime_Start, this.frameRate);
			objectDataset.ComputeSceneCentricTime(metadata.mSceneTime_Start, metadata.mSceneTime_End, this.frameRate);
		}
		
		if (this.hView2Map.valid)
		{
			SimpleBoundingBox map_bbox = null;
			
			// set a range for the cartesian coordinates if specified. 
			if (this.CarteMaxX!=-1)
			{
				map_bbox = new SimpleBoundingBox(CarteMinX, CarteMinY, CarteMaxX, CarteMaxY);
			}
			
			eventDataset.ComputeSceneCentricLocation(this.hView2Map, map_bbox );
			objectDataset.ComputeSceneCentricLocation(this.hView2Map, map_bbox );
		}
	}

	public CEventData FindEventData(String event_id) {
		return this.eventDataset.FindEventData(event_id);
	}
	
	public ArrayList<CEventData> FindEventDataList(String event_id) {
		return this.eventDataset.FindEventDataList(event_id);
	}

	public CObjectData FindObjectData(String object_id) {
		return this.objectDataset.FindObjectData(object_id);
	}

	public Date GetMinSceneTime() {
		return TimeFunctions.MinDate(
				this.objectDataset.GetMinSceneTime(), 
				this.eventDataset.GetMinSceneTime());
	}
	
	public Date GetMaxSceneTime() {
		return TimeFunctions.MaxDate(
				this.objectDataset.GetMaxSceneTime(), 
				this.eventDataset.GetMaxSceneTime());
	}

	public Date GetSceneTimeStart() {
		return (Date) this.metadata.mSceneTime_Start.clone();
	}
	
	public Date GetSceneTimeEnd() {
		return (Date) this.metadata.mSceneTime_End.clone();
	}

	
	public double GetFrameRate() {
		return this.frameRate;

	}
	public double GetFrameRateTrackData() {
		return this.frameRateTrackData;
	}
	
	public double GetImageWidthScale() {
		return this.imageWidthScale;
	}

	public String GetObserverId() {
		return this.observer_id;
	}


}
