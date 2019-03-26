package sparql.Parsedata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sparql.LocationData;
import sparql.LocationData.LocationType;
import sparql.MseeException;
import sparql.MseeFunction;
import sparql.SimpleBoundingBox;
import sparql.TimeData;
import sparql.TimeFunctions;
import sparql.translator.utilities.Global;
import TextParser.CEventData;
import TextParser.CObjectData;
import TextParser.TPLib;
import TextParser.TimeParser;

public class ParsedDataDescriptor {
	
	public Map<String,Map<String,String>> idMappings = new LinkedHashMap<String,Map<String,String>>();
	public List<String> queryFilters = new ArrayList<String>();
	
	private Map<String,List<SimpleBoundingBox>> viewSubsetLocations = new LinkedHashMap<String,List<SimpleBoundingBox>>();
	private Map<String,List<String>> viewSubsetKeywordsHigh = new LinkedHashMap<String,List<String>>();
	private Map<String,List<String>> viewSubsetKeywordsLow = new LinkedHashMap<String,List<String>>();
		
	public String rdf_set_select = null;
		
	public List<View> views = new ArrayList<View>();
	public AreaOfResponsibility areaOfResponsibility;
	
	public boolean outdoorScene = true;
	
	public Date	minSceneTime = null;
	public Date	maxSceneTime = null;
	private String socId = "unknown_socID";
	
	public double framerate = 29.97; 
	
	// this is used by the cypher query to relate frame number to scene time, it should be parsed from the config file
	public Date SceneStartTime = null; 
	
	
	public PartOfMapping partOfMapping = new PartOfMapping(); 
	
	private String rdf_folder;
	
	public ArrayList<RdfSet> rdfSetList = new ArrayList<RdfSet>();  

	private Document doc;
	
	public ParsedDataDescriptor() {		
		SetSceneStartTime("2014-09-20T22:20:00.000Z");		// default value
	}

	/**
	 * Clear existing soc-specific data, and loads the configuration (including view subset information). This should be called when the SOC is changed. 
	 */
	private void Reset( String configuration_xml, String rdf_folder ) {
		views.clear();
		partOfMapping.Reset();
		areaOfResponsibility = new AreaOfResponsibility();
		outdoorScene = true;
				
		minSceneTime = null;
		maxSceneTime = null;
		socId = "unknown_socID";
		
		queryFilters.clear();
		idMappings.clear();
		
    	viewSubsetLocations.clear();
		viewSubsetKeywordsHigh.clear();
		viewSubsetKeywordsLow.clear();
		
		this.rdf_folder = rdf_folder;
		
		rdf_set_select = null;
		
		doc = null;
				
		rdfSetList.clear();
		
		if ( configuration_xml == null ) {
			System.err.println( "Warning: no view selection XML file defined." );
			return;
		}
		    	
    	try {
    		DocumentBuilderFactory dbFactory;
    		DocumentBuilder dBuilder;
    	    dbFactory = DocumentBuilderFactory.newInstance();
    		dbFactory.setNamespaceAware(true);
    	    dBuilder = dbFactory.newDocumentBuilder();
    		doc = dBuilder.parse(new InputSource(new StringReader(TPLib.GetFileContents(configuration_xml))));
    		doc.getDocumentElement().normalize();
    	}
    	catch ( ParserConfigurationException | IOException | SAXException e ) {
    		System.err.println(e);
    	}

    	if ( doc == null ) {
    		System.err.println( "Warning: could not parse the XML file " + configuration_xml + "." );
    		return;
    	}

    	// Here, we parse parameters, if there are any.
    	
    	NodeList parameters = doc.getDocumentElement().getElementsByTagName("Parameters");
    	
    	if ( parameters.getLength() > 1 ) {
    		System.err.println( "Warning: multiple Parameters nodes are defined in " + configuration_xml + "; only the first one will be used." );
    	}
    	
    	if ( parameters.getLength() >= 1 ) {
    		parameters = ((Element)parameters.item(0)).getElementsByTagName("Parameter");
    		
    		for ( int i = 0; i < parameters.getLength(); i++ ) {
    			Element parameter = (Element)parameters.item(i);
    			if ( parameter.getAttribute("name").equals("subsample") ) {
    				try {
    					View.subsample = Integer.parseInt( parameter.getAttribute("value") );
    					System.out.println( "Set subsample = " + View.subsample );
    				}
    				catch ( NumberFormatException e ) {
    					System.err.println( "Warning: could not parse the parameter subsample = " + parameter.getAttribute("value") );
    				}
    			}
    		}
    	}
    	
    	// Here, we parse id mappings.
    	
    	NodeList mappings = doc.getDocumentElement().getElementsByTagName("Mappings");
    	
    	if ( mappings.getLength() > 1 ) {
    		System.err.println( "Warning: multiple Mappings nodes are defined in " + configuration_xml + "; only the first one will be used." );
    	}
    	
    	if ( mappings.getLength() >= 1 ) {
    		NodeList cameras = ((Element)mappings.item(0)).getElementsByTagName("Camera");
    		
    		for ( int i = 0; i < cameras.getLength(); i++ ) {
    			Element camera = (Element)cameras.item(i);
    			
    			Map<String,String> mappingsForCamera = new LinkedHashMap<String,String>();
    			idMappings.put( camera.getAttribute("id"), mappingsForCamera );
    			
    			NodeList maps = camera.getElementsByTagName("Map");
    			
    			for ( int j = 0; j < maps.getLength(); j++ ) {
    				Element map = (Element)maps.item(j);
    				mappingsForCamera.put( map.getAttribute("from"), map.getAttribute("to") );
    				System.out.println( "Mapped " + map.getAttribute("from") + " to " + map.getAttribute("to") + " for " + camera.getAttribute("id") );
    			}
    		}
    	}
    	
    	// Here, we parse query filters.
    	
    	NodeList filters = doc.getDocumentElement().getElementsByTagName("Filters");
    	
    	if ( filters.getLength() > 1 ) {
    		System.err.println( "Warning: multiple Filters nodes are defined in " + configuration_xml + "; only the first one will be used." );
    	}
    	
    	if ( filters.getLength() >= 1 ) {
    		filters = ((Element)filters.item(0)).getElementsByTagName("Filter");
    		
    		for ( int i = 0; i < filters.getLength(); i++ ) {
    			Element filter = (Element)filters.item(i);
    			
    			String filterText = filter.getTextContent().trim();
    			
    			if ( filterText.startsWith("<") && filterText.endsWith(">") ) {
        			queryFilters.add( filterText );
        			
        			System.out.println( "Added the filter: " + filterText );
    			}
    			else {
    				System.err.println( "Warning: invalid filter: " + filterText );
    			}
    		}
    	}
    	
    	// Here, we find all the view subsets defined in the XML, as well as the locations and keywords that are associated with them.
    	    	
    	NodeList viewSelection = doc.getDocumentElement().getElementsByTagName("View_Selection");
    	
    	if ( viewSelection.getLength() == 0 ) {
    		System.err.println( "Warning: no View_Selection node is defined in " + configuration_xml + "." );
    		return;
    	}
    	
    	if ( viewSelection.getLength() > 1 ) {
    		System.err.println( "Warning: multiple View_Selection nodes are defined in " + configuration_xml + "; only the first one will be used." );
    	}

    	NodeList viewSubsets = ((Element)viewSelection.item(0)).getElementsByTagName("View_Subset");
    	
    	for ( int i = 0; i < viewSubsets.getLength(); i++ ) {
    		Element viewSubset = (Element)viewSubsets.item(i);
    		
    		List<String> keywordsListHigh = new ArrayList<String>();
    		viewSubsetKeywordsHigh.put( viewSubset.getAttribute("id"), keywordsListHigh );
    		
    		List<String> keywordsListLow = new ArrayList<String>();
    		viewSubsetKeywordsLow.put( viewSubset.getAttribute("id"), keywordsListLow );
    		
    		NodeList keywordsNodes = viewSubset.getElementsByTagName("Keywords");
    		
    		for ( int j = 0; j < keywordsNodes.getLength(); j++ ) {
    			Element keywordsNode = (Element)keywordsNodes.item(j);
    			
    			NodeList keywords = keywordsNode.getElementsByTagName("Keyword");
    			
    			for ( int k = 0; k < keywords.getLength(); k++ ) {
    				Element keyword = (Element)keywords.item(k);
    				
    				String keywordString = keyword.getTextContent().trim();
    				
    				// System.out.println( "Keyword: " + keywordString );
    				
    				if ( keyword.getAttribute("priority").equals("high") ) {
    					keywordsListHigh.add( keywordString );
    				}
    				
    				if ( keyword.getAttribute("priority").equals("low") ) {
    					keywordsListLow.add( keywordString );
    				}
    			}
    		}
    		
    		NodeList polygons = viewSubset.getElementsByTagName("CartesianMetricPolygon");
    		
    		List<SimpleBoundingBox> locationsList = new ArrayList<SimpleBoundingBox>();
    		viewSubsetLocations.put( viewSubset.getAttribute("id"), locationsList );
    		
    		boolean first = true;
    		
    		for ( int j = 0; j < polygons.getLength(); j++ ) {
    			Element polygon = (Element)polygons.item(j);
    			    			
    			NodeList points = polygon.getElementsByTagName("CartesianMetricPoint");
    			
    			StringBuffer coordinates = new StringBuffer();
    			
    			for ( int k = 0; k < points.getLength(); k++ ) {
    				Element point = (Element)points.item(k);
    				
    				NodeList xs = point.getElementsByTagName("x");
    				NodeList ys = point.getElementsByTagName("y");
    				
    				if ( xs.getLength() > 0 && ys.getLength() > 0 ) {
    					if ( first ) {
    						first = false;
    					}
    					else {
    						coordinates.append(";");
    					}
    					coordinates.append( xs.item(0).getTextContent().trim() + "," + ys.item(0).getTextContent().trim() );
    				}
    			}
    			
    			try {
        			LocationData locationData = new LocationData(LocationType.CARTESIAN_METRIC_POLYGON, coordinates.toString());
        			locationsList.add( locationData.GetEnclosingBox() );
        			System.out.println( "Defined a polygon for " + viewSubset.getAttribute("id") + ":" );
        			locationData.Printf();
    			}
    			catch ( MseeException e ) {
    				System.err.println( "Ill-defined polygon for " + viewSubset.getAttribute("id") + "." );
    				e.printStackTrace();
    			}
    		}
    	}
	}
	
	private void SetSocId(String a_soc_id) {
		this.socId  = a_soc_id;
	}
	
	// add to rdfSetList if there is any time overlap between the query time interval, and view time interval 
	private void AddRdfSetIfAnyTimeOverlap(
			TimeData query_time_data,		// time data in the query
			String rdf_file, 				// rdf filename
			String obs_id, 					// observation id
			String view_id)					// view id
	{
		
		View view =GetView(view_id);			// not handling garden MC  moving camera video. 
		
		if (view == null)
		{	System.err.println("ERROR AddRdfSetIfTimeOverlap view not found: " + view_id);
			return; 
		}
		
		// get time data of view
		TimeData view_time_data = new TimeData();
		Date sceneTime_Start = view.GetSceneTimeStart();
		Date sceneTime_End = view.GetSceneTimeEnd();		
		view_time_data.SetSceneTimePeriod(sceneTime_Start, sceneTime_End);
		
		//IsSameTimeAs is true, when there is any time that is in both time intervals
		if (TimeFunctions.IsSameTimeAs(query_time_data, view_time_data))
		{
			System.err.println("CHECK AddRdfSetIfAnyTimeOverlap is true " + view_id );
			System.err.print("\t CHECK query_time_data : ");	query_time_data.PrintTimeData();
			System.err.print("\n\t CHECK view_time_data : ");		view_time_data.PrintTimeData();
			System.err.print("\n");
			
			rdfSetList.add(new RdfSet());
			rdfSetList.get(rdfSetList.size() -1).SetRdf(rdf_folder + File.separator + rdf_file);
			rdfSetList.get(rdfSetList.size() -1).AddObsId(obs_id);
			rdfSetList.get(rdfSetList.size() -1).AddView(view_id);			
		 }				
	}
	
    private void SetUpRdfSubset( TimeData query_time_data ) {    	
		rdfSetList.clear();
		
    	NodeList viewSelection = doc.getDocumentElement().getElementsByTagName("View_Selection");
    	
    	if ( viewSelection.getLength() == 0 ) {
    		return; // A warning would have been printed earlier.
    	}
		    	
    	NodeList viewSubsets = ((Element)viewSelection.item(0)).getElementsByTagName("View_Subset");

    	for ( int i = 0; i < viewSubsets.getLength(); i++ ) {
    		Element viewSubset = (Element)viewSubsets.item(i);

    		if ( !viewSubset.getAttribute("id").equals(this.rdf_set_select) ) {
    			continue;
    		}

    		NodeList rdfs = viewSubset.getElementsByTagName("RDF");

    		if ( rdfs.getLength() == 0 ) {
    			System.err.println( "Warning: no RDF files are defined for " + viewSubset.getAttribute("id") + "." );
    			return;
    		}

    		for ( int j = 0; j < rdfs.getLength(); j++ ) {
    			Element rdf = (Element)rdfs.item(j);

    			if ( !rdf.getAttribute("time_dependent").equals("true") ) {
    				if ( rdf.getAttribute("time_dependent").equals("false") && query_time_data != null ) {
    					continue;
    				}
    				
    				RdfSet rdfSet = new RdfSet();
    				rdfSetList.add(rdfSet);

    				rdfSet.SetRdf( rdf_folder + File.separator + rdf.getAttribute("file") );
    				
    				NodeList cameras = rdf.getElementsByTagName("Camera");
    				
    				for ( int k = 0; k < cameras.getLength(); k++ ) {
    					Element camera = (Element)cameras.item(k);
    					rdfSet.AddObsId( camera.getAttribute("id") );
    				}
    				
    				NodeList views = rdf.getElementsByTagName("View");

    				for ( int k = 0; k < views.getLength(); k++ ) {
    					Element view = (Element)views.item(k);
    					rdfSet.AddView( view.getAttribute("id") );						
    				}
    			}
    			else {
    				if ( query_time_data == null ) {
    					continue;
    				}
    				
    				NodeList cameras = rdf.getElementsByTagName("Camera");
    				
    				if ( cameras.getLength() == 0 ) {
    					System.err.println( "Warning: no cameras are defined for time-dependent " + rdf.getAttribute("file") + " for " + viewSubset.getAttribute("id") + "." );
    					continue;
    				}
    				if ( cameras.getLength() > 1 ) {
    					System.err.println( "Warning: multiple cameras are defined for time-dependent " + rdf.getAttribute("file") + " for " + viewSubset.getAttribute("id") + "; only the first one will be used." );
    				}
    				
    				NodeList views = rdf.getElementsByTagName("View");

    				if ( views.getLength() == 0 ) {
    					System.err.println( "Warning: no views are defined for time-dependent " + rdf.getAttribute("file") + " for " + viewSubset.getAttribute("id") + "." );
    					continue;
    				}
    				if ( views.getLength() > 1 ) {
    					System.err.println( "Warning: multiple views are defined for time-dependent " + rdf.getAttribute("file") + " for " + viewSubset.getAttribute("id") + "; only the first one will be used." );
    				}

    				Element view = (Element)views.item(0);
    				Element camera = (Element)cameras.item(0);
    				AddRdfSetIfAnyTimeOverlap(query_time_data, rdf.getAttribute("file"), camera.getAttribute("id"), view.getAttribute("id"));
    			}
    		}
    		    		
    		return;
    	}
    }
	
	public static String loadXMLString(String filename)  throws IOException {
		BufferedReader inputStream;
		String line;
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();
		inputStream = new BufferedReader(new FileReader(filename));
		line = inputStream.readLine();
		while (line != null) {
			returnValue.append(line + "\n");
			line = inputStream.readLine();			
		}
		inputStream.close();
		
		return returnValue.toString();
	}
	

	public void LoadXml(String view_selection_xml, String rdf_folder, String fn_xml, String pathPrefix) throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbFactory;	//To get a DocumentBuilder object
		DocumentBuilder dBuilder;			//Used to parse the XML file
	
		Document xML;
		
		Reset(view_selection_xml, rdf_folder);
		
		try
    	{
			String xmlString = loadXMLString(fn_xml);
			
		    dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(true);
		    dBuilder = dbFactory.newDocumentBuilder();

			xML = dBuilder.parse(new InputSource(new StringReader(xmlString)));	//Change the query to a DOM object
			xML.getDocumentElement().normalize();					//This step is recommended
			
			// get id			
			Element socElement  =xML.getDocumentElement();					
			String soc_id = socElement.getAttribute("id");
			
			this.SetSocId(soc_id);
			System.out.println("ParsedDataDescriptor: socid: " + this.socId);
			
			/* not working 
			NodeList socNodes = Global.getElementsByTagNameCaseInsensitive(xML,"SOC");
			
			if (socNodes.getLength() != 1) 
			{	System.err.println("ParsedDataDescriptor: SOC tag count not equal to one:" + socNodes.getLength() );
			} else
			{
				Node socNode = socNodes.item(0);
				Element socElement  = (Element)socNode;						
				String id = socElement.getAttribute("id");
				this.socId  = id; 
				System.out.println("ParsedDataDescriptor: socid: " + this.socId);
			}
			*/ 
			
			// load part-of
			NodeList partOfNodes = Global.getElementsByTagNameCaseInsensitive(xML,"PartOfData");
			if (partOfNodes.getLength() != 1) 
			{	System.err.println("ParsedDataDescriptor: PartOfData  tag count not equal to one:" + partOfNodes.getLength() );
				System.err.println("ParsedDataDescriptor: fn_xml:" + fn_xml );
			
			} else
			{
				Node partOfNode = partOfNodes.item(0);
				String partoffile = pathPrefix + "\\" + partOfNode.getTextContent().trim();	
				System.out.println("DEBUG ParsedDataDescriptor: partoffile:" + partoffile);
				partOfMapping.LoadMap(partoffile);				
			}
			
			// determine whether the scene is indoor or outdoor
			NodeList sceneTypeNodes = Global.getElementsByTagNameCaseInsensitive(xML, "SceneType");
			if (sceneTypeNodes.getLength() != 1)
			{	System.err.println("ParsedDataDescriptor: SceneType tag count not equal to one: " + sceneTypeNodes.getLength() + "; defaulting to outdoor." );
			}
			else {
				String sceneType = ( (Element)sceneTypeNodes.item(0) ).getTextContent().trim();
				if ( "outdoor".equals(sceneType) ) {
					outdoorScene = true;
				}
				else if ( "indoor".equals(sceneType) ) {
					outdoorScene = false;
				}
				else {
					System.err.println("ParsedDataDescriptor: Invalid scene type " + sceneType + "; defaulting to outdoor." );
				}
			}
			
			// load AOR
			NodeList aorNodes = Global.getElementsByTagNameCaseInsensitive(xML,"AreaOfResponsibility");
			if (aorNodes.getLength() != 1) 
			{	System.err.println("ParsedDataDescriptor: AreaOfResponsibility tag count not equal to one:" + aorNodes.getLength() );
			}
			else
			{
				areaOfResponsibility = new AreaOfResponsibility();
				Node aorNode = aorNodes.item(0);
				NodeList aorChildNodelist = ((Element)aorNode).getChildNodes();
				for (int count = 0; count < aorChildNodelist.getLength(); count++) {
					Node aorChildNode = aorChildNodelist.item(count);
					if (aorChildNode.getNodeType() != Node.ELEMENT_NODE) 
					{	continue; //Skip the text nodes
					}
					if (aorChildNode.getNodeName().equals("SceneCentricTimePeriod")) {
						NodeList sceneTimeChildNodelist = ((Element)aorChildNode).getChildNodes();
						String str_starttime  = null;
						String str_endtime  = null; 
						for (int count_time_child = 0; count_time_child < sceneTimeChildNodelist.getLength(); count_time_child++) 
						{	// get scene centric time 
							Node sceneTimeChildNode = sceneTimeChildNodelist.item(count_time_child);
							if (sceneTimeChildNode.getNodeType() != Node.ELEMENT_NODE) 
							{	continue; //Skip the text nodes
							}					
							if (sceneTimeChildNode.getNodeName().equals("StartTime")) {
							
								str_starttime = sceneTimeChildNode.getTextContent().trim();	
								
							} else if (sceneTimeChildNode.getNodeName().equals("EndTime")) 
							{							
								str_endtime = sceneTimeChildNode.getTextContent().trim();
							}
						}		
						if ((str_starttime!=null) && (str_endtime!=null))
						{
							areaOfResponsibility.SetSceneTime(str_starttime, str_endtime );
						} else
						{
							System.err.println("WARN ParsedDataDescriptor: unable to get scene time");
						}
//						this.observer_id = node.getTextContent().trim();					
					} else if (aorChildNode.getNodeName().equals("Volume")) {
						
					     NodeList childNodes_vol = aorChildNode.getChildNodes();
					     for (int i_vol = 0; i_vol <childNodes_vol.getLength(); i_vol++ )
					     {
					    	Node cNode_vol = childNodes_vol.item(i_vol);
					    	if (cNode_vol.getNodeType() != Node.ELEMENT_NODE) 
							{	continue; //Skip the text nodes
							}					
							if (cNode_vol.getNodeName().equals("CartesianMetricPolygon")) 
							{
								NodeList cNodes_poly = cNode_vol.getChildNodes();
								for (int i_poly = 0; i_poly <cNodes_poly.getLength(); i_poly++ )
								{
									Node cNode_poly = cNodes_poly.item(i_poly);
									if (cNode_poly.getNodeType() != Node.ELEMENT_NODE) 
									{	continue; //Skip the text nodes
									}
									if (cNode_poly.getNodeName().equals("CartesianMetricPoint")) 
									{										
										 double x = -1;
										 double y = -1; 
									     NodeList cNodes_pt = cNode_poly.getChildNodes();
									     for (int i_pt = 0; i_pt <cNodes_pt.getLength(); i_pt++ )
									     {
									    	Node cNode_pt = cNodes_pt.item(i_pt);
									    	if (cNode_pt.getNodeType() != Node.ELEMENT_NODE) 
											{	continue; //Skip the text nodes
											}
									    	if (cNode_pt.getNodeName().equals("x")) 
											{
									    		x = Double.parseDouble(cNode_pt.getTextContent().trim());
											} else if (cNode_pt.getNodeName().equals("y")) 
											{
									    		y = Double.parseDouble(cNode_pt.getTextContent().trim());
											} 
									     }
									     areaOfResponsibility.AddCartesianMetricPolygon(x,y);										
									} 
								}							
							} else if (cNode_vol.getNodeName().equals("GeodeticPolygon")) 
							{	NodeList cNodes_poly = cNode_vol.getChildNodes();
								for (int i_poly = 0; i_poly <cNodes_poly.getLength(); i_poly++ )
								{
									Node cNode_poly = cNodes_poly.item(i_poly);
									if (cNode_poly.getNodeType() != Node.ELEMENT_NODE) 
									{	continue; //Skip the text nodes
									}
									if (cNode_poly.getNodeName().equals("GeodeticPoint")) 
									{										
										 double x = -1;
										 double y = -1; 
									     NodeList cNodes_pt = cNode_poly.getChildNodes();
									     for (int i_pt = 0; i_pt <cNodes_pt.getLength(); i_pt++ )
									     {
									    	Node cNode_pt = cNodes_pt.item(i_pt);
									    	if (cNode_pt.getNodeType() != Node.ELEMENT_NODE) 
											{	continue; //Skip the text nodes
											}
									    	if (cNode_pt.getNodeName().equals("latitude")) 
											{
									    		x = Double.parseDouble(cNode_pt.getTextContent().trim());
											} else if (cNode_pt.getNodeName().equals("longitude")) 
											{
									    		y = Double.parseDouble(cNode_pt.getTextContent().trim());
											} 
									     }
									     areaOfResponsibility.AddCartesianMetricPolygon(x,y);	
									     
									} 
								}					
								
							}								
					    	 
					     }
					     areaOfResponsibility.locData.Printf();
					}				
				}				
			}
			
			
			// load views
			NodeList viewsNodes;	
			Node viewsNode;						
			NodeList viewNodes;					
			Node viewNode;	
	
			viewsNodes = Global.getElementsByTagNameCaseInsensitive(xML,"Views");
			if (viewsNodes.getLength() > 0) {		//There might not be a "Times" node
				if (viewsNodes.getLength() != 1) {		//There should only be 1 <Times>
					System.err.println(
							"Found more than one <Views> in xml.");
				}
				viewsNode = viewsNodes.item(0);
				viewNodes = viewsNode.getChildNodes();
				for (int count = 0; count < viewNodes.getLength(); count++) {
					viewNode = viewNodes.item(count);
					if (viewNode.getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
						
						Element viewElement  = (Element)viewNode;
						
						String id = viewElement.getAttribute("id");
						
						View view = new View(viewNode, id, pathPrefix);
						
						this.views.add(view);											
						
					}
				}
			}		
			
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
    	
    	ComputeSceneTimeRange();    	
		
		// Printf();
		
	}




	private void ComputeSceneTimeRange() {
		for (View v : this.views)
		{
			Date start = v.GetMinSceneTime();
			Date end = v.GetMaxSceneTime();
			minSceneTime = TimeFunctions.MinDate(minSceneTime, start);
			maxSceneTime = TimeFunctions.MaxDate(maxSceneTime, end);
		}			
	}

	public CEventData FindEventData(String event_id) {
		for (View view :this.views)
		{
			CEventData data = view.FindEventData(event_id);
			if (data != null)
				return data; 
		}			
		
		return null;
	}

	/* OBSOLETE this function is obsolete and should not be called*/ 
	/**
	 * @param object_id
	 * @return
	 * 
	 * \todo Calls to this function should be replaced by FindObjectDataList, which return a list instead of a single object
	 */
	public CObjectData FindObjectData(String object_id) {
		
		
		CObjectData  best_view_data = null;
		
		for (View view :this.views)
		{
			CObjectData data = view.FindObjectData(object_id);
			
			if (data != null)
			{
				if (best_view_data == null)
				{
					best_view_data = data;
				} else
				{
					if (data.GetSceneDurationInMsec() > best_view_data.GetSceneDurationInMsec())
					{
						// pick the view where the object has longest duration
						best_view_data = data;
						// System.err.println("ERROR ParsedDataDescriptor FindObjectData OBSOLETE function that return only one object when there are multple views with same ids ");
					}
				}
			}
		}	
			
		
		return best_view_data;
	}
	/**/ 

	public ArrayList<CObjectData> FindObjectDataList(String object_id) {
		
		ArrayList<CObjectData> list = new ArrayList<CObjectData>();
				
		for (View view :this.views)
		{
			if (false == MseeFunction.IsInObsSubset(view.GetObserverId()))
			{				
				continue;
			}									
			
			CObjectData data = view.FindObjectData(object_id);
			
			if (data != null)
			{
				list.add(data);
				
			}
		}			
		
		return list;
	}


	public Date GetMinSceneTime() {
		return (Date) this.minSceneTime.clone();
	}	

	public Date GetMaxSceneTime() {
		return (Date) this.maxSceneTime.clone();
	}

	// find the view with the given ID
	public View GetView(String viewId) {
		for (View v : this.views)
		{
			if (v.id.compareTo(viewId) ==0)
			{
				return v;
			}			
		}	
		return null;
	}

	public String GetSocId() {
		return this.socId;
	}


	public ArrayList<CEventData> FindEventDataList(String event_id) {
		
		ArrayList<CEventData> list = new ArrayList<CEventData>();
		
		for (View view :this.views)
		{
			// check if in current query observer subset; this is to avoid overcounting 
			if (false == MseeFunction.IsInObsSubset(view.GetObserverId()))
			{				
				continue;
			}				
					
			
			// CEventData data = view.FindEventData(event_id);
			ArrayList<CEventData> list_tmp =  view.FindEventDataList(event_id);
			
			if (list_tmp != null)
			{
				for (CEventData data: list_tmp)
				{
					list.add(data);
				}
				
			}
		}			
		
		return list;
	}


	public void UserSelectRdfSet(TimeData query_time_data ) {
		
		Scanner in = new Scanner(System.in); 
		
		String select  = rdf_set_select;
		
		System.out.println("UserSelectRdfSet: Current rdf_set_select :  " +  rdf_set_select );
		
		boolean bConfirm = false;			    
		
		while (!bConfirm)
		{
			// print options
		    int enum_size = viewSubsetLocations.keySet().size();
			System.out.println("RDF set  option ... :  ");
			int i =0; 
		    for(String value: viewSubsetLocations.keySet() ){
			    System.out.println("\t i " + i + " name="+value);
			    i++;
			}
		    		

			int iSelect =-1;
		    while ((iSelect < 0) || (iSelect >= enum_size))
		    {
			    System.out.printf("\nSelect RDF set  ... :  ");
			    
			    String s = in.nextLine();

			    try{
			    	iSelect =  Integer.parseInt(s);
			    }
			    catch(NumberFormatException ex){
			    	iSelect = -1;
			        System.out.println("Its not a valid Integer");
			    }		    
			    
			}
		    
			i =0; 
		    for(String value: viewSubsetLocations.keySet() ){
		    	if ( i == iSelect ) {
		    		select = value;
		    		break;
		    	}
			    i++;
			}		    
		    		    
		    System.out.println("\tSelected RDF set : " + select);
		    System.out.printf("\nConfirm by entering Y:  ");				
		
		    String str = in.nextLine();
			if (str.equalsIgnoreCase("y"))
			{ 	bConfirm = true;
			}
	  
		}

	    System.out.println("\tFinal Selected RDF set  : " + select);
	    
	    this.SetRdfSet(select, query_time_data );
	}
		
	public boolean AutomaticSelectRdfSet_FromKeywords( String xmlQuery, TimeData query_time_data, boolean highPriority ) {
		Map<String,List<String>> viewSubsetKeywords = highPriority ? viewSubsetKeywordsHigh : viewSubsetKeywordsLow;
				
		for ( String viewSubset : viewSubsetLocations.keySet() ) {
			List<String> keywords = viewSubsetKeywords.get(viewSubset);
			
			for ( String keyword : keywords ) {
				if ( xmlQuery.contains(keyword) ) {
				    this.SetRdfSet(viewSubset, query_time_data);
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Simply pick the very first view subset defined.
	public void SelectDefaultRdfSet( String xmlQuery, TimeData query_time_data ) {
		for ( String viewSubset : viewSubsetLocations.keySet() ) {
			this.SetRdfSet(viewSubset, query_time_data);
			break;
		}
	}

	public boolean AutomaticSelectRdfSet_FromLocation(String xmlQuery,
			LocationData query_loc_data,
			 TimeData query_time_data ) {
		
		if (query_loc_data == null)
			return false;


		if ((query_loc_data.GetType() != LocationType.CARTESIAN_METRIC_POINT) &&
				(query_loc_data.GetType() != LocationType.CARTESIAN_METRIC_POLYGON))
		{
			return false; 
		}             


		SimpleBoundingBox bbox1 = query_loc_data.GetEnclosingBox();
		
		String selectedViewSubset = null;

		double max_overlap_ratio  = 0; 
		
		for ( String viewSubset : viewSubsetLocations.keySet() ) {
			for ( SimpleBoundingBox box : viewSubsetLocations.get(viewSubset) ) {
				double overlap = bbox1.GetOverlapRatio(box);
				if ( overlap > max_overlap_ratio ) {
					selectedViewSubset = viewSubset;
					max_overlap_ratio = overlap;
				}
			}
		}
				
		if ( selectedViewSubset == null ) 
		{
			return false; 
		} else {

			System.out.println("\tFinal Selected RDF set (from location): " + selectedViewSubset);

	    this.SetRdfSet(selectedViewSubset, query_time_data);
			return true;
		}
		/**/
	}

	
	private void SetRdfSet(String select, TimeData query_time_data ) {
		rdf_set_select = select;
		SetUpRdfSubset(query_time_data );
	}	


	public Date GetSceneStartTime() {
		return SceneStartTime; 
	}

	public void SetSceneStartTime(String str_scene_start_time) {
		try {
			SceneStartTime = TimeParser.getTimeParser().parse(str_scene_start_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
