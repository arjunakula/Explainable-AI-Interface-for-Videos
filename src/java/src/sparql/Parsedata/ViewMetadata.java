package sparql.Parsedata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import TextParser.TimeParser;
import sparql.MseeException;
import sparql.translator.utilities.Global;


/*
 * 
 * <?xml version="1.0" encoding="utf-8"?>
<Metadata xmlns="http://www.siginnovations.com/MSEE"
           version="1.0">

    <Header>
        <SceneCentricTimePeriod>
            <StartTime>2013-09-28T13:29:37.832Z</StartTime>
            <EndTime>2013-09-28T13:40:17.454Z</EndTime>
        </SceneCentricTimePeriod>
    </Header>

    <Record>
        <Time>2013-09-28T13:29:37.832Z</Time>
        <SensorPosition>
            <GeodeticPoint>
                <latitude>35.876798</latitude>
                <longitude>-78.840332</longitude>
                <elevation>91</elevation>
            </GeodeticPoint>
        </SensorPosition>
    </Record>
    
</Metadata>

 */
public class ViewMetadata {
	public Date mSceneTime_Start = null;
	Date mSceneTime_End = null;
	
	String strSceneStartTime ="";
	String strSceneEndTime ="";
	

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
	
	
	public void LoadXml(String fnXml) throws MseeException, IOException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub

		DocumentBuilderFactory dbFactory;	//To get a DocumentBuilder object
		DocumentBuilder dBuilder;			//Used to parse the XML file
	
		Document xML;
		
		try
    	{
			String xmlString = loadXMLString(fnXml);
			
		    dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(true);
		    dBuilder = dbFactory.newDocumentBuilder();

			xML = dBuilder.parse(new InputSource(new StringReader(xmlString)));	//Change the query to a DOM object
			xML.getDocumentElement().normalize();					//This step is recommended
			
			NodeList viewsNodes;	
			Node viewsNode;						//The only "Times" nodes in the XML query
			NodeList viewNodes;						//The time definitions in the XML query
			Node viewNode;	
	
			viewsNodes = Global.getElementsByTagNameCaseInsensitive(xML,"SceneCentricTimePeriod");
			if (viewsNodes.getLength() > 0) {		//There might not be a "Times" node
				if (viewsNodes.getLength() != 1) {		//There should only be 1 <Times>
					System.err.println(
							"Found more than one <SceneCentricTimePeriod> in xml.");
				}
				Element viewElement = (Element)viewsNodes.item(0);

				NodeList nodelist = viewElement.getChildNodes();
				for (int count = 0; count < nodelist.getLength(); count++) {
					Node node = nodelist.item(count);
					if (node.getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
						String node_name = node.getNodeName(); 
						if (node_name.equals("StartTime")) {
							this.strSceneStartTime = node.getTextContent().trim();
							
							try {
								mSceneTime_Start = TimeParser.getTimeParser().parse(strSceneStartTime);
							} catch (ParseException e) {
								System.err.println("ERROR ViewMetadata; unable to parse time string " + strSceneStartTime);
								// e.printStackTrace();								
								throw new MseeException("ERROR ViewMetadata; unable to parse time string " + strSceneStartTime);
							}
						} else if (node_name.equals("EndTime")) {
							this.strSceneEndTime = node.getTextContent().trim();		
							
							try {
								mSceneTime_End = TimeParser.getTimeParser().parse(strSceneEndTime);
							} catch (ParseException e) {
								System.err.println("ERROR ViewMetadata; unable to parse time string " + strSceneEndTime);
								// e.printStackTrace();								
								throw new MseeException("ERROR ViewMetadata; unable to parse time string " + strSceneEndTime);
							}
						}
						else {
							System.err.println(
									"WARNING in ViewMetadata, unrecognize xml node " +
									"argument, \"" + node_name + "\", was found.");
						}
					}
				}
				
			}		
			
    	} catch (FileNotFoundException e) {
			// e.printStackTrace();
    		System.err.println("ERROR Viewmetadata file not found " + fnXml);
		} 
		
		// debug
    	// Printf();
		
	}


	private void Printf() {

		
		System.out.println("ViewMetadata strSceneStartTime "+ strSceneStartTime);
		System.out.println("ViewMetadata strSceneEndTime "+ strSceneEndTime);
		if (mSceneTime_Start != null) {
			System.out.println("ViewMetadata mSceneTime_Start "+ mSceneTime_Start.toString());
		}
		if (mSceneTime_End != null) {
			System.out.println("ViewMetadata mSceneTime_End "+ mSceneTime_End.toString());
		}		
	}
	
	
	

}
