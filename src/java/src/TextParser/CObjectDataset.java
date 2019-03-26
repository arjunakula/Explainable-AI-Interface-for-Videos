package TextParser;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import sparql.MseeDataset;
import sparql.SimpleBoundingBox;
import sparql.TimeFunctions;
import sparql.Parsedata.Homography;


public class CObjectDataset {

	public Vector<CObjectData> objectVt = new Vector<CObjectData>();

	public void Printf()
	{	
		for (int i = 0; i <objectVt.size(); i++ )
		{
			CObjectData data = objectVt.elementAt(i);
			System.out.printf("data "+i +": " + data.id  + "\n");
			data.Printf();
		}		
	}
	
	public CObjectData FindObjectData(String str_id)
	{

		String str_tmp = str_id;
		
		// remove linefeed
		// System.out.printf("before str_tmp -" + str_tmp + "-\n");		
		
		if (str_tmp.charAt(str_tmp.length()-1 ) == '\n')
			str_tmp =str_tmp.substring(0, str_tmp.length()-1 );
		if (str_tmp.charAt(str_tmp.length()-1 ) == '\r')
			str_tmp =str_tmp.substring(0, str_tmp.length()-1 );
		
		// System.out.printf("after str_tmp -" + str_tmp + "-\n");		
		
		for (int i = 0; i <objectVt.size(); i++ )
		{
			CObjectData data = objectVt.elementAt(i);
			// if (data.id.equals(str_id))
			
						
			/*
			if (str_tmp.length() > data.id.length())
			{
				if (true)
				{
					// different length, the string id may have some line end char at the end.
					// so we make the length to be same before comparing
					
					if (str_tmp.charAt([str_tmp.length()-1 ) == '\n')
					{
						str_tmp.replace('\n', '\0');
					}
						
					// str_tmp = str_tmp.substring(0,data.id.length());
				} 
			}	
			*/ 		
			
			// if (str_tmp.equals(data.id))
			if (str_tmp.contentEquals(data.id))
			{
				return data;
			}
		
		}	
		return null;
	}

	
	public void LoadXml(String fn_xml, double frameRateScale, String obs_id, String view_id, String data_uri)
	{
		System.out.println("loading file " + fn_xml);
    	try
    	{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
			InputStream in = new FileInputStream(fn_xml);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			
			CEventData eventdata = null; 
			
			while (eventReader.hasNext()) 
			{
				XMLEvent event = eventReader.nextEvent();
				

				
				/*
					-<object id="19fe98f8-04a1-51fb-b86c-0c001ce14709"> 
						<position contour="178,149,178,490,484,490,484,149" frame="3733"/> 
						<position contour="177,148,177,491,484,491,484,148" frame="3734"/> 
						<position contour="176,146,176,491,483,491,483,146" frame="3735"/> 
						<position contour="175,143,175,489,481,489,481,143" frame="3736"/>
				 */
				
				if (event.isStartElement()) 
				{
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart() == ("object")) 
					{					
						String id="unknownid";						
						String str_label="unknown_label";	
						
						Iterator iterator = startElement.getAttributes();
					    while (iterator.hasNext()) 
					    {
					          Attribute attribute = (Attribute) iterator.next();
					          QName name = attribute.getName();
					          String value = attribute.getValue();
					          
					          if (name.getLocalPart() == ("id"))
					          {	  id = value;
					          } else if (name.getLocalPart() == ("label"))
					          {	  str_label = value;	
					          }
					          
					     }				      
					    // objectVt.add(new CObjectData(id, obs_id, str_label));		
					    
					    // Sort the positions in the previous object, by frame, in case they are not sorted in the tracking data file.
					    if ( objectVt.size() > 0 ) {
					    	objectVt.lastElement().sortPositions();
					    }
					    
					    objectVt.add(new CObjectData(id,  obs_id,  view_id, data_uri));	
					    					    
					} else if (startElement.getName().getLocalPart() == ("position")) 
					{					
						
						// format: 
						// 	<position contour="170,325,170,643,295,643,295,325" facing=”0.7,0.5” frame="0" />
						
						String str_contour="";		
						String str_frame="";		
						String str_facing="";
						String str_status="Closed";
						Iterator iterator = startElement.getAttributes();
					    while (iterator.hasNext()) 
					    {
					          Attribute attribute = (Attribute) iterator.next();
					          QName name = attribute.getName();
					          String value = attribute.getValue();
					          
					          if (name.getLocalPart() == ("contour"))
					          {	  str_contour = value;
					          }  else if (name.getLocalPart() == ("facing"))
					          {	  str_facing = value;
					          } else if (name.getLocalPart() == ("frame"))
					          {	  str_frame = value;
					          }  else if (name.getLocalPart() == ("status"))
					          {	  str_status = value;
					          } 					        
					     }				      
					    if (objectVt.size() > 0)
					    {
					    	CObjectData objectData = objectVt.lastElement();
					    	objectData.AddPosition(str_contour, str_facing, str_frame, frameRateScale, str_status);		
					    }
					}
				}
					
			}
			
    	} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.err.println("WARN file not found:" + fn_xml );
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} 
		
	    // Sort the positions in the last object, by frame, in case they are not sorted in the tracking data file.
	    if ( objectVt.size() > 0 ) {
	    	objectVt.lastElement().sortPositions();
	    }
    	
		// Printf();		
	}

	

	public void LoadObjectFromTextFile(String input_file, double frameRateScale, String obs_id, String view_id, String data_uri) 
	{

		// format
		// 0 597 578 1090 1069 0 0 0 0 "PERSON" "MALE" "STANDING"
		
		System.out.println("loading file " + input_file);
		System.err.println("DEBUG   LoadObjectFromTextFile : frameRateScale " + frameRateScale);
		
		
		Scanner input;
		try {
			input = new Scanner(new FileReader(input_file));
		} catch (FileNotFoundException e) {
			System.out.println("WARN in DistortionParam; file not found " + input_file);
			return; 
		}
		int cur_id = -1;
		while (true)
		{
			int id,x1,y1,x2,y2,frame;
			try {	
				 id = input.nextInt();
				 x1 = input.nextInt();
				 y1 = input.nextInt();
				 x2 = input.nextInt();
				 y2 = input.nextInt();
				 frame = input.nextInt();
			} catch (NoSuchElementException  e) {
				break;			
			}
	
			
			// System.out.println("DEBUG LoadObjectFromTextFile " + id + " " + x1 + " "+ y1 + " "+ x2 + " "+ y2 + " " + frame);
				
			if (id!=cur_id)
			{
				String str_id = null;
				
				Map<String,String> mappings = MseeDataset.parsedDataDescriptor.idMappings.get(obs_id);
				
				if ( mappings != null ) {
					str_id = mappings.get("" + id);
				}
				
				if ( str_id  == null ) {
					str_id = "unknown";
				}
				
			    // Sort the positions in the previous object, by frame, in case they are not sorted in the tracking data file.
			    if ( objectVt.size() > 0 ) {
			    	objectVt.lastElement().sortPositions();
			    }
				
			    objectVt.add(new CObjectData(str_id,  obs_id,  view_id, data_uri));
			    cur_id = id;
			}
			
			CObjectData objectData = objectVt.lastElement();
	    	objectData.AddPosition(x1,y1,x2,y2,frame, frameRateScale);	
			
			try {	
				input.nextLine();
			} catch (NoSuchElementException  e) {
				break;			
			}
		}
		
	    // Sort the positions in the last object, by frame, in case they are not sorted in the tracking data file.
	    if ( objectVt.size() > 0 ) {
	    	objectVt.lastElement().sortPositions();
	    }
		
		return; 

	}
	
	public void ComputeSceneCentricTime(Date sceneTime_Start, Date sceneTime_End,  double frameRate) {
		// TODO Auto-generated method stub
		for (CObjectData data : this.objectVt)
		{	
			data.ComputeSceneCentricTime(sceneTime_Start, sceneTime_End, frameRate);
		
		}	
	}

	public void ComputeSceneCentricLocation(Homography hView2Map, SimpleBoundingBox map_bbox) {
		// TODO Auto-generated method stub
		for (CObjectData data : this.objectVt)
		{	data.ComputeSceneCentricLocation(hView2Map, map_bbox);
		}
		
	}

	public Date GetMinSceneTime() {
		// TODO Auto-generated method stub
		if (this.objectVt.isEmpty()) 
			return null;
		
		Date date = null;
		for (CObjectData data : this.objectVt)
		{	date = TimeFunctions.MinDate(date, data.mSceneTime_Start);
		}
		return date;
	}		
	

	public Date GetMaxSceneTime() {
		// TODO Auto-generated method stub
		if (this.objectVt.isEmpty()) 
			return null;
		
		Date date = null;
		for (CObjectData data : this.objectVt)
		{	date = TimeFunctions.MaxDate(date, data.mSceneTime_End);
		}
		return date;
	}

	public void SubsamplePos(int i) {
		for (CObjectData data : this.objectVt)
		{	data.SubsamplePos(i);
		}
	}		
}
