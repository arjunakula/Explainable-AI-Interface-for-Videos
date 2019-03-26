package sparql.translator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.definitions.Definition;
import sparql.translator.definitions.EventDefinition;
import sparql.translator.definitions.LocationDefinition;
import sparql.translator.definitions.ObjectDefinition;
import sparql.translator.definitions.SetDefinition;
import sparql.translator.definitions.TimePeriodDefinition;
import sparql.translator.query.SPARQLQuery;
import sparql.translator.restrictions.QueryRestriction;
import sparql.translator.restrictions.Restriction;
import sparql.translator.restrictions.UnionRestriction;
import sparql.translator.utilities.Global;
import sparql.translator.utilities.TranslateCondition;
import sparql.translator.utilities.TranslateConditionReturnValue;
import sparql.translator.utilities.UnableToRespondException;
import sparql.translator.utilities.XMLException;
import sparql.translator.utilities.XMLType;

/**
 * The <code>XMLToSPARQLTranslator</code> class is used to translate an MSEE query from its
 * original XML form into SPARQL.
 *
 * @author Ken Samuel
 * @version 1.0, Oct 15, 2013
 * @since 1.6
 */
public class XMLToSPARQLTranslator {
		
	/** <code>xML</code> is the XML query in a DOM tree format. */
	private static Document xML;
	
	/** <code>sPARQL</code> is the information in <code>xML</code> that has been translated into SPARQL. */
	private static SPARQLQuery sPARQL;

	/** 
	 * <code>setDefinitions</code> is a list of the set definitions of all of the sets that are defined in 
	 * the XML query. 
	 */
	private static ArrayList<SPARQLQuery> setDefinitions;

	/** 
	 * <code>setsIncludedInTheQuery</code> is the set of the names of all sets referenced in the XML query 
	 * that have already been accounted for in the SPARQL query. 
	 */
	private static HashSet<String> setsIncludedInTheQuery;
	
	/** <code>currentObject</code> is the object whose definition is currently being processed. */
	private static ObjectDefinition currentObject;
	
	/**
	 *  <code>whatObjects</code> is a list of EES objects that were found when running a &lt;what&gt; query 
	 *  on the database. 
	 */
	private static ArrayList<ObjectDefinition> whatObjects;
	
	/** <code>whatAnswer</code> is the response to send to the EES for a &lt;what&gt; query. */
	private static StringBuffer whatAnswer;
	
	/**
	 * The <code>XMLToSPARQLTranslator</code> constructor  initializes the global variables. The persistent
	 * names will not be saved on the disk. 
	 * 
	 * @throws FileNotFoundException if there is a problem initializing the variables.
	 * @throws IOException if there's a problem initializing the variables.
	 */
	public XMLToSPARQLTranslator() throws FileNotFoundException, IOException {
		this(null);
	}
	
	/**
	 * The <code>XMLToSPARQLTranslator</code> constructor  initializes the global variables. 
	 *
	 * @param dataFilename is the name of the file where the data that should be 
	 * remembered for future queries is stored. If it is <b><code>null</code></b>, then the data
	 * will not be saved on the disk.
	 * @throws FileNotFoundException if there is a problem with the file name.
	 * @throws IOException if there's a problem reading from or writing to the file.
	 */
	public XMLToSPARQLTranslator(String dataFilename) throws FileNotFoundException, IOException {
		//Initialize variables
		xML = null;
		sPARQL = null;
		setDefinitions = null;
		setsIncludedInTheQuery = null;
		currentObject = null;
		whatObjects = null;
		whatAnswer = null;
		Global.initializeVariables(dataFilename);
	}

	/**
	 * The <code>loadXMLQuery</code> method loads a file that has a query in its original XML form and returns
	 * the contents of that file as a <code>String</code>.
	 *
	 * @param filename is the name of the file to load.
	 * @return the XML query in that file.
	 * @throws IOException if there's a problem reading the file.
	 */
	public static String loadXMLQuery(String filename)  throws IOException {
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

	/**
	 * The <code>translateQuery</code> method translates the given XML query into SPARQL
	 * format, saving the result in <code>sPARQL</code>.
	 * 
	 * @param xMLQuery is the query to translate, in its original XML form.
	 * @throws IOException if there's a problem loading the persistent data from disk.
	 * @throws ClassNotFoundException if there's a problem with the file.
	 * @throws UnableToRespondException if the system cannot respond to the query.
	 */
	public void translateQuery(String xMLQuery) 
			throws IOException, ClassNotFoundException, UnableToRespondException {
		String warning;		//A message to let the user know there might be a problem

		if (xMLQuery == null) {
			Global.newStoryline();
			return;
		}
		Global.unknownObjects = new HashSet<String>();											//Initialize
		Global.inputType = null;																//Initialize
		setDefinitions = new ArrayList<SPARQLQuery>(4);											//Initialize
		setsIncludedInTheQuery = new HashSet<String>(5);										//Initialize
		Global.loadData();																		//Initialize
		try {
			xML = Global.createXMLObject(xMLQuery);
		} catch (XMLException exception) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateQuery: The query is not in proper XML" +
					"format: " + exception.getMessage() + "\nThe query was \"\n" + xMLQuery + "\n\"";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
	    	throw new UnableToRespondException(Global.constructUnableToRespondMessage("none"));
		}
		translateQuery();
		if (
				(Global.unableToRespondMessage.size() > 0) && 
				(Global.inputType != XMLType.Object)) {		//FIXME: If the object specification has an <UnableToRespond>, then we want to skip it and go on to the next object specification
			throw new UnableToRespondException(Global.constructUnableToRespondMessage(toString()));
		}
	}
		
	/**
	 * The <code>translateQuery</code> method translates the XML query in <code>xML</code> into SPARQL
	 * format, saving the result in <code>sPARQL</code>.
	 * @throws IOException if there's a problem saving the persistent data to disk.
	 */
	private static void translateQuery() throws IOException {
		String outermostTag;				//The top-level XML tag in xML

		Global.nameTranslations = new HashMap<String,String>();					//Initialize
		Global.useSubsetOfViews = false;										//Initialize
		Global.hasStandardQueryStatement = false;								//Initialize
		Global.mathematicalComparisonOperator = null;							//Initialize
		Global.number = null;													//Initialize
		sPARQL = new SPARQLQuery();												//Initialize
		outermostTag = getOutermostTag();

		sPARQL.addPrefixes();
		translateTimes("the " + outermostTag);
		translateLocations("the " + outermostTag);
		translateSets("the " + outermostTag);
		translateEvents("the " + outermostTag);
		singleFrame("the " + outermostTag);
		translateQueryPart2();
	}

	/**
	 * The <code>translateQueryPart2</code> method continues the process of translating the XML query in 
	 * <code>xML</code> into SPARQL format, saving the result in <code>sPARQL</code>.
	 * 
	 * @throws IOException if there's a problem saving the persistent data to disk.
	 */
	private static void translateQueryPart2() throws IOException {
		String outermostTag;				//The top-level XML tag in xML
		String warning;						//A message to let the user know there might be a problem
		
		outermostTag = getOutermostTag();
		if (translateObjects("the " + outermostTag) == false) {
			if (outermostTag.equals("<Query>")) {
				translateQueryStatement("the " + outermostTag);
			} else if (outermostTag.equals("<NonPolarQuery>")) {
				translateNonPolarQueryStatement("the " + outermostTag);
			} else if (outermostTag.equals("<SceneDescriptiveText>")) {
				translateStatement("the " + outermostTag);
			} else {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateQueryPart2: " +
								"Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
				Global.unableToRespondMessage.add(warning);
				Global.inputType = XMLType.Query;
			}

			sPARQL = sPARQL.reorderRestrictions("the " + outermostTag);
			sPARQL.sortQuery("the " + outermostTag);
		}
		Global.saveData();												//Save the persistent data
	}
	
	/**
	 * The <code>reportResults</code> method is used to acquire information that needs to be remembered.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @return a string that tells the caller what it should do next.
	 * @throws IOException if any IO errors occur.
	 * @throws UnableToRespondException if the system cannot respond to the query.
	 */
	public String reportResults(String results) 
			throws IOException, UnableToRespondException {
		String cleanResults;				//The results without a newline character at the end
		StringBuffer warning;				//A message to let the user know there might be a problem
		String returnValue;
		
		returnValue = null;												//Initialize
		cleanResults = Global.removeNewlineCharacter(results);
		if (results == null) {
			warning = new StringBuffer(
					"WARNING in XMLToSPARQLTranslator.reportResults in an input to reportResults(): " +
					"reportResults() was called with a null argument.");
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning.toString());
		} else if (Global.inputType == null) {
			warning = new StringBuffer(
					"WARNING in XMLToSPARQLTranslator.reportResults in an input to reportResults(): " +
					"Ignoring a call to reportResults(), because the query had no <NonPolarQueryStatement>.");
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning.toString());
			//returnValue is null;
		} else {
			switch (Global.inputType) {
			case Query: 
				returnValue = reportResultsQuery(cleanResults);
				break;
			case What:
				returnValue = reportResultsWhat(cleanResults);
				break;
			case When:
				reportResultsWhen(cleanResults);
				break;
			case Where:
				reportResultsWhere(cleanResults);
				break;
			case Object:
				returnValue = reportResultsObject(cleanResults);
				break;
			case ObjectType:
				returnValue = reportResultsObjectType(cleanResults);
				break;
			case SceneDescriptiveText:
				reportResultsSceneDescriptiveText(cleanResults);
				break;
			default:
				warning = new StringBuffer(
						"WARNING in XMLToSPARQLTranslator.reportResults in an input to reportResults(): " +
						"Bug detected. Contact Ken Samuel.");
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning.toString());
				break;
			}
		}
		Global.saveData();												//Save the persistent data
		if (Global.unableToRespondMessage.size() == 0) {
			return returnValue;
		}
		throw new UnableToRespondException(Global.constructUnableToRespondMessage(returnValue));
	}
	
	/**
	 * The <code>reportResultsQuery</code> method is used to acquire the <b><code>true</code></b> or 
	 * <b><code>false</code></b> answer to a polar (boolean) query.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @return a string that tells the caller what it should do next.
	 */
	public String reportResultsQuery(String results) {
		return "<Answer>" + results + "</Answer>\n";	
	}
	
	/**
	 * The <code>reportResultsWhat</code> method is used to acquire the list of objects in the answer to a
	 * &lt;what&gt; query.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @return a string that tells the caller what it should do next.
	 */
	public String reportResultsWhat(String results) {
		String[] lines;						//The separated lines of the input value
		ObjectDefinition whatObject;		//An object that is one of the answers in a <WhatAnswer>
		String whatObjectXMLId;				//The new identifier assigned to that object for XML queries
		String whatObjectDatabaseId;		//The object identifier found in the database

		Global.inputType = XMLType.ObjectType;	//The next query (if there is one) will request an object type
		whatAnswer = new StringBuffer();
		whatAnswer.append("<WhatAnswer>\n");
		lines = results.split("\n");			 
		if ((lines.length == 0) || (lines[0].equals(""))) {		//No values
			whatAnswer.append("</WhatAnswer>\n");
			return whatAnswer.toString();
		}
		whatObjects = new ArrayList<ObjectDefinition>(lines.length);
		for (String line : lines) {
			whatObjectDatabaseId = Global.cleanJenaOutput(line);//Remove the leading '#' and trailing ','
			if (Global.databaseIdToXMLId.get(whatObjectDatabaseId) != null) {
				whatObjectXMLId = Global.databaseIdToXMLId.get(whatObjectDatabaseId);
				whatObject = (ObjectDefinition)Global.getDefinition(
						whatObjectXMLId,
						"an input to reportResults()");
				whatAnswer.append(
						"    <SUTObject id=\"" + whatObject.getId() + 
						"\" type=\"" + whatObject.getXMLtype() + "\" />\n");

			} else {			//Need to make a new object id
				whatObject = new ObjectDefinition(null,whatObjectDatabaseId);//No id until we get the typw
				whatObjects.add(whatObject);
			}
		}
		if (whatObjects.isEmpty()) {		//No more objects left
			whatAnswer.append("</WhatAnswer>\n");
			return whatAnswer.toString();
		}
		return "<Query>\n" + composeObjectTypeQuery(whatObjects.get(0)) + "\n</Query>\n";
	}
	
	/**
	 * The <code>reportResultsWhen</code> method is used to acquire the list of time periods in the answer
	 * to a &lt;when&gt; query.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @throws IOException if any IO errors occur.
	 */
	public void reportResultsWhen(String results) throws IOException {
		Document xMLResults;				//The results in the form of an XML object
		TimePeriodDefinition time;			//One of the times specified in a <WhenAnswer>
		NodeList outermostNodes;			//A list with one element, the outermost node in xMLResults
		Node outermostNode;					//The outermost tag in the form of a Node object
		Element outermostElement;			//The outermost tag in the form of a Element object
		String outermostNodeName;			//The name of the outermost tag
		StringBuffer warning;				//A message to let the user know there might be a problem

		if (( ! results.equals("true")) && ( ! results.equals("false"))) {
			try {
				xMLResults = Global.createXMLObject(results);
			} catch (XMLException exception) {
				warning = new StringBuffer(
						"WARNING in XMLToSPARQLTranslator.reportResultsWhen in an input to " +
						"reportResults(): reportResults(): Expecting the argument of reportResults() to be " +
						"in XML format starting with a <WhenAnswer> tag.");
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning.toString());
				return;
			}
			outermostNodes = xMLResults.getChildNodes();
			outermostNode = Global.getNode(outermostNodes,"an input to reportResults()");
			outermostElement = (Element)outermostNode;
			outermostNodeName = outermostElement.getNodeName();
			if (outermostNodeName.equals("WhenAnswer")) {
				for (Node timeXML : Global.getChildNodes(outermostNode,"an input to reportResults()")) {
					time = new TimePeriodDefinition(timeXML);
					time.translateTimePeriod("an input to reportResults()");
					time.setPersists(true);
					Global.storeDefinition(time,"an input to reportResults()");
				}
			} else {
				warning = new StringBuffer(
						"WARNING in XMLToSPARQLTranslator.reportResultsWhen in an input to " +
						"reportResults(): Input to reportResults() should begin with a <WhenAnswer> tag, " +
						"not <" + outermostNodeName + ">.");
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning.toString());
			}
		}		
	}
	
	/**
	 * The <code>reportResultsWhere</code> method is used to acquire the bounding box that is the answer
	 * to a &lt;where&gt; query.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @throws IOException if any IO errors occur.
	 */
	public void reportResultsWhere(String results) throws IOException {
		Document xMLResults;				//The results in the form of an XML object
		NodeList outermostNodes;			//A list with one element, the outermost node in xMLResults
		Node outermostNode;					//The outermost tag in the form of a Node object
		Element outermostElement;			//The outermost tag in the form of a Element object
		String outermostNodeName;			//The name of the outermost tag
		LocationDefinition location;		//One of the locations specified in the results
		StringBuffer warning;				//A message to let the user know there might be a problem
		
		if (( ! results.equals("true")) && ( ! results.equals("false"))) {
			try {
				xMLResults = Global.createXMLObject(results);
			} catch (XMLException exception) {
				warning = new StringBuffer(
						"WARNING in XMLToSPARQLTranslator.reportResultsWhere in an input to " +
						"reportResults(): Expecting the argument of reportResults() to be in XML format " +
						"starting with a <WhereAnswer> tag.");
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning.toString());
				return;
			}
			outermostNodes = xMLResults.getChildNodes();
			outermostNode = Global.getNode(outermostNodes,"an input to reportResults()");
			outermostElement = (Element)outermostNode;
			outermostNodeName = outermostElement.getNodeName();
			if (outermostNodeName.equals("WhereAnswer")) {
				for
				  (Node locXML : Global.getChildNodes(outermostNode,"an input to reportResults()")) {
					location = new LocationDefinition(locXML);
					location.translateLocation("an input to reportResults()");
					location.setPersists(true);
					Global.storeDefinition(location,"an input to reportResults()");
				}
			} else {
				warning = new StringBuffer(
						"WARNING in XMLToSPARQLTranslator.reportResultsWhere in an input to " +
						"reportResults(): Input to reportResults() should begin with a <WhereAnswer> tag, " +
						"not <" + outermostNodeName + ">.");
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning.toString());
			}
		}
	}
	
	/**
	 * The <code>reportResultsObject</code> method is used to acquire the list of objects that satisfies an
	 * object specification (&lt;object-spec&gt;).
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @return a string that tells the caller what it should do next.
	 * @throws IOException if any IO errors occur.
	 */
	public String reportResultsObject(String results) throws IOException {
		String[] lines;						//The separated lines of the input value
		String objectId;					//The object identifier found for an object specification
		Integer unableToRespondMessageSize;	//The number of <UnableToRespond> reasons before translating more
		String comma;						//Specified whether to print a comma
		StringBuffer warning;				//A message to let the user know there might be a problem
		
		lines = results.split("\n");
		if ((lines.length == 0) || (lines[0].equals(""))) {		//No values --- Bad
			Global.unknownObjects.add(currentObject.getId());
			warning = new StringBuffer(
					"WARNING in XMLToSPARQLTranslator.reportResultsObject in " + "an input to reportResults()" + 
					": Unable to find an id in the database for the XML id \"" + 
					currentObject.getId() + "\": ");
			System.err.println(warning);
			if (Global.hasDefinition(currentObject.getId())) {
				Global.removeDefinition(currentObject,"an input to reportResults()");
			}
		} else if (lines.length == 1) {							//One value --- Good
			objectId = lines[0];
			objectId = Global.cleanJenaOutput(objectId);//Remove the leading '#' and trailing ','
			if ( ! objectId.equals("")) {
				currentObject.setObjectId(objectId,"an input to reportResults()");
				if (Global.typeOfSpecifiedObject == null) {
					warning = new StringBuffer(
							"WARNING in XMLToSPARQLTranslator.reportResultsObject in an input to " +
							"reportResults(): Bug detected. Contact Ken Samuel.");
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning.toString());
				} else {
					Global.mseeTypes.put("data:"+objectId,Global.typeOfSpecifiedObject);	//TODO: Should I also do: Global.definitions.put(currentObject.getId(),objectId)
				}
			} else {
				warning = new StringBuffer(
						"WARNING in XMLToSPARQLTranslator.reportResultsObject in an input to " +
						"reportResults(): Bug detected. Contact Ken Samuel.");
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning.toString());
			}
		} else {		//More than one value --- Bad
			Global.unknownObjects.add(currentObject.getId());
			warning = new StringBuffer(
					"WARNING in XMLToSPARQLTranslator.reportResultsObject in an input to " +
					"reportResults(): Found more than one id in the database for the XML id \"" + 
					currentObject.getId() + "\": ");
			comma = "";
			for (String line : lines) {
				objectId = Global.cleanJenaOutput(line);//Remove the leading '#' and trailing ','
				warning.append(comma + "\"" + objectId + "\"");
				comma = ", ";
			}
			warning.append(".");
			System.err.println(warning);
			if (Global.hasDefinition(currentObject.getId())) {
				Global.removeDefinition(currentObject,"an input to reportResults()");
			}
		}
		while (true) {
			sPARQL = new SPARQLQuery();	//Initialize
			sPARQL.addPrefixes();					//Add the prefixes
			unableToRespondMessageSize = Global.unableToRespondMessage.size();
			translateQueryPart2();
			if (Global.inputType != XMLType.Object) {
				if (Global.unknownObjects.size() > 0) {
					return constructUnknownObjectsResponse();
				}
				return "<Query>\n" + sPARQL.toString() + "\n</Query>\n"; //Send the new query
			} else if (Global.unableToRespondMessage.size() == unableToRespondMessageSize) {
				return "<Query>\n" + sPARQL.toString() + "\n</Query>\n"; //Send the new query
			} //Unable to respond for the new object specification, so continue translating
		}
	}

	/**
	 * The <code>reportResultsObjectType</code> method is used to acquire the type of an object.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 * @return a string that tells the caller what it should do next.
	 */
	public String reportResultsObjectType(String results) {
		String[] lines;						//The separated lines of the input value
		ObjectDefinition whatObject;		//An object identifier found in the database for a <What> query
		String objectType;					//The type of an MSEE object in the database, such as "Human"
		String xMLObjectType;				//The type of an MSEE object in XML queries, such as "person"
		String xMLObjectId;					//A new identifier for a new object
		String xMLObjectIdPrefix;			//The part of that identifier without the number at the end
		Integer objectSUTIdCount;			//Number of object names with a specific prefix that exist 
		String cleanLine;					//A line of text with no '#' at the beginning or ',' at the end
		StringBuffer warning;				//A message to let the user know there might be a problem
		
		objectType = null;								//Initialize
		whatObject = whatObjects.get(0);
		lines = results.split("\n");
		for (String line : lines) {		//Get the object type from the result
			cleanLine = Global.cleanJenaOutput(line);//Remove the leading '#' and trailing ','
			if ( ! ((cleanLine.equals("")) || (cleanLine.equals("NamedIndividual")))) {
				objectType = cleanLine;
			}
		}
		if (objectType == null) {
			warning = new StringBuffer(
					"WARNING in XMLToSPARQLTranslator.reportResultsObjectType in an input to " +
					"reportResults(): There is no object type in the input to reportResults().");
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning.toString());
			xMLObjectType = "";
		} else {
			xMLObjectType = Global.OBJECT_TYPES_REVERSED.get(objectType);
		}
		whatObject.setXMLtype(xMLObjectType);
		xMLObjectIdPrefix = "obj-sut-" + xMLObjectType;
		objectSUTIdCount = Global.getNameCount(xMLObjectIdPrefix);
		objectSUTIdCount++;
		xMLObjectId = xMLObjectIdPrefix + "-" + objectSUTIdCount;
		whatObject.setId(xMLObjectId);
		Global.updateNameCount(xMLObjectIdPrefix);
		Global.databaseIdToXMLId.put(whatObject.getObjectId(),xMLObjectId);
		Global.storeDefinition(whatObject,"an input to reportResults()");
		whatAnswer.append("    <SUTObject id=\"" + xMLObjectId + "\" type=\"" + xMLObjectType + "\" />\n");
		Global.mseeTypes.put("data:"+whatObject.getName(),xMLObjectType);
		whatObjects.remove(0);
		if (whatObjects.isEmpty()) {		//No more objects left
			whatAnswer.append("</WhatAnswer>\n");
			return whatAnswer.toString();
		}
		return "<Query>\n" + composeObjectTypeQuery(whatObjects.get(0)) + "\n</Query>\n";
	}
	
	/**
	 * The <code>reportResultsSceneDescriptiveText</code> method is used to acquire the 
	 * <b><code>true</code></b> or <b><code>false</code></b> answer to a scene-descriptive text
	 * &lt;Statement-spec&gt;, which is supposed to be <b><code>true</code></b>.
	 *
	 * @param results is the results returned by the database as a response to the last query.
	 */
	public void reportResultsSceneDescriptiveText(String results) {
		if (results.equals("false")) {
			System.err.println(
					"WARNING in XMLToSPARQLTranslator.reportResultsSceneDescriptiveText in an input to " +
					"reportResults(): A <Statement> in a <SceneDescriptiveText> is false.");
		}
	}
	
	/**
	 * The <code>translateTimes</code> method processes everything found within the &lt;Times&gt; tag in the 
	 * XML query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateTimes(String owner) {
		Node timesNode;						//The only <Times> nodes in the XML query
		ArrayList<Node> times;				//The time definitions in the XML query
		TimePeriodDefinition time;			//One of the time definitions

		timesNode = Global.getNode(xML,"Times",owner);
		if (timesNode != null) {					//There might not be a <Times> node
			times = Global.getChildNodes(timesNode,owner);
			for (Node timeNode : times) {
				time = new TimePeriodDefinition(timeNode);
				time.translateTimePeriod("the <Times> in "+owner);
				Global.storeDefinition(time,"the <Times> in "+owner);
			}
		}
	}

	/**
	 * The <code>translateLocations</code> method processes everything found within the &lt;Locations&gt; tag
	 * in the XML query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateLocations(String owner) {
		Node locationsNode;					//The only <Locations> nodes in the XML query
		ArrayList<Node> locations;			//The location definitions in the XML query
		LocationDefinition location;		//One of the location definitions
		String locationId;					//The identifier of that location

		locationsNode = Global.getNode(xML,"Locations",owner);
		if (locationsNode != null) {					//There might not be a <Locations> node
			locations = Global.getChildNodes(locationsNode,owner);
			for (Node locationNode : locations) {
				location = new LocationDefinition(locationNode);
				location.translateLocation("the <Locations> in "+owner);	//FIXME: What if one of a polygon's points has an id
				locationId = location.getId();
				if (! locationId.equals("")) {
					Global.storeDefinition(location,"the <Locations> in "+owner);
				}
			}
		}
	}
		
	/**
	 * The <code>translateSets</code> method processes everything found within the &lt;Sets&gt; tag in the XML
	 * query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateSets(String owner) {
		Node setsNode;						//The only <Sets> nodes in the XML query
		ArrayList<Node> sets;				//The set definitions in the XML query
		SetDefinition set;					//One of the set definitions

		setsNode = Global.getNode(xML,"Sets",owner);
		if (setsNode != null) {					//There might not be a <Sets> node
			sets = Global.getChildNodes(setsNode,owner);
			for (Node setNode : sets) {
				set = new SetDefinition(setNode);
				set.translateObjectOrSet(false,"the <Sets> in "+owner);
				Global.storeDefinition(set,"the <Sets> in "+owner);
			}
		}
	}

	/**
	 * The <code>translateEvents</code> method processes everything found within the &lt;Events&gt; tag in the
	 * XML query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateEvents(String owner) {
		Node eventsNode;						//The only <Events> nodes in the XML query
		ArrayList<Node> events;					//The event definitions in the XML query
		EventDefinition event;					//One of the event definitions
		String warning;							//Information to put in a warning message

		eventsNode = Global.getNode(xML,"Events",owner);
		if (eventsNode != null) {					//There might not be an <Events> node
			events = Global.getChildNodes(eventsNode,owner);
			for (Node eventNode : events) {
				event = new EventDefinition(eventNode);
				if (event.getId().equals("")) {
					warning = "an <event> in ";
				} else {
					warning = "the event, \"" + event.getId() + "\", in ";
				}
				warning += "the <Events> in ";
				event.translateEvent(warning+owner);
				Global.storeDefinition(event,warning+owner);
			}
		}
	}

	/**
	 * The <code>translateObjects</code> method processes everything found within the &lt;Objects&gt; tag in 
	 * the XML query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return <b><code>true</code></b> if and only if at least one object specification is found. Otherwise,
	 * return <b><code>false</code></b>.
	 */
	private static Boolean translateObjects(String owner) {
		Node objectsNode;					//The only <Objects> node in the XML query
		Node objectNode;					//An <object-spec> node in the <Objects> node
		String warning;						//Information to put in a warning message

		objectsNode = Global.getNode(xML,"Objects",owner);
		if (objectsNode == null) {					//There might not be an <Objects> node
			return false;
		}
		objectNode = Global.removeFirstChild(objectsNode);
		if (objectNode == null) {
			return false;
		}
		Global.inputType = XMLType.Object;
		currentObject = new ObjectDefinition(objectNode);
		if ((currentObject.getId() == null) || (currentObject.getId().equals(""))) {
			warning = "an <object-spec> in ";
		} else {
			warning = "the object, \"" + currentObject.getId() + "\", in ";
		}
		warning += "the <Objects> in ";
		currentObject.translateObjectOrSet(true,warning+owner);
		Global.storeDefinition(currentObject,warning+owner);
		sPARQL.setSelect(currentObject.getSelect());
		sPARQL.setWhere(currentObject.getWhere());
		return true;
	}

	/**
	 * The <code>singleFrame</code> method processes everything found within the &lt;SingleFrame&gt; tag in 
	 * the XML query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void singleFrame(String owner) {
		Node singleFrameNode;					//The only <SingleFrame> node in the XML query

		singleFrameNode = Global.getNode(xML,"SingleFrame",owner);
		if (singleFrameNode != null) {					//There might not be a <SingleFrame> node
			//TODO: Decide what to do with <SingleFrame> tags.
			System.err.println(
					"WARNING in XMLToSPARQLTranslator.singleFrame in " + owner + 
					": Found a <SingleFrame> in a query.");
		}
	}

	/**
	 * The <code>translateQueryStatement</code> method processes everything found within the 
	 * &lt;QueryStatement&gt; tag in the XML query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateQueryStatement(String owner) {
		Node queryStatementNode;				//The only <QueryStatement> part of the query
		NodeList nodes;							//A list of the nodes in the <QueryStatement>
		Node node;								//The node in that list of nodes
		String warning;							//A message to let the user know there might be a problem
		
		Global.inputType = XMLType.Query;
		queryStatementNode = Global.getNode(xML,"QueryStatement",owner);
		if (queryStatementNode == null) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.queryStatement in " + owner + 
					": Unable to find a <QueryStatement> in a <Query>.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return;
		}
		Global.timePeriodDefinitionsUsedInLastQuery = new HashMap<String, Definition>();	//Initialize
		Global.locationDefinitionsUsedInLastQuery = new HashMap<String, Definition>();		//Initialize
		nodes = queryStatementNode.getChildNodes();
		node = Global.getNode(nodes,"the <QueryStatement> in "+owner);
		if (node != null) {
			translateQueryStatementCondition(node,sPARQL,"the <QueryStatement> in "+owner);
		}
	}

	/**
	 * The <code>translateNonPolarQueryStatement</code> method processes everything found within the 
	 * &lt;NonPolarQueryStatement&gt; tag in the XML non-polar query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateNonPolarQueryStatement(String owner) {
		Node nonPolarQueryStatementNode;		//The only <NonPolarQueryStatement> part of the query
		NodeList nodes;							//A list of the nodes in the <QueryStatement>
		Node node;								//The node in that list of nodes
		String warning;							//A message to let the user know there might be a problem

		nonPolarQueryStatementNode = Global.getNode(xML,"NonPolarQueryStatement",owner);
		if (nonPolarQueryStatementNode == null) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.nonPolarQueryStatement in " + owner + 
					": Unable to find a <NonPolarQueryStatement> in a <NonPolarQuery>.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return;
		}
		Global.timePeriodDefinitionsUsedInLastQuery = new HashMap<String, Definition>();	//Initialize
		Global.locationDefinitionsUsedInLastQuery = new HashMap<String, Definition>();		//Initialize
		nodes = nonPolarQueryStatementNode.getChildNodes();
		node = Global.getNode(nodes,"the <NonPolarQueryStatement> in "+owner);
		if (node != null) {
			translateNonPolarQueryStatementQuestion(node,"the <NonPolarQueryStatement> in "+owner);
		}
	}

	/**
	 * The <code>translateStatement</code> method processes everything found within the &lt;Statement&gt; tag
	 * in the XML scene descriptive text.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the scene 
	 * descriptive text.
	 */
	private static void translateStatement(String owner) {
		Node statementNode;						//The only <Statement> part of the scene descriptive text
		String warning;							//A message to let the user know there might be a problem

		Global.inputType = XMLType.SceneDescriptiveText;
		statementNode = Global.getNode(xML,"Statement",owner);
		if (statementNode == null) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.statement in " + owner + 
					": Unable to find a <Statement> in a <SceneDescriptiveText>.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return;
		}
		Global.timePeriodDefinitionsUsedInLastQuery = new HashMap<String, Definition>();	//Initialize
		Global.locationDefinitionsUsedInLastQuery = new HashMap<String, Definition>();		//Initialize
		translateStatement(statementNode.getChildNodes(),"the <Statement> in "+owner);
	}

	/**
	 * The <code>translateStatement</code> method translates the 
	 * <code>&lt;Statement&gt;</code> portion of an XML scene descriptive text into SPARQL, storing the 
	 * result in the field, <code>sPARQL</code>.
	 *
	 * @param nodes is a list of the nodes at the top level of the statement. It should include only
	 * one condition, which is something that returns a boolean value, such as (x >= 1).
	 * @param owner is a description of the last XML tag that was found in the XML version of the scene 
	 * descriptive text.
	 */
	private static void translateStatement(NodeList nodes, String owner) {
		Node node;					//The node in the given list of nodes

		node = Global.getNode(nodes,owner);
		if (node != null) {
			translateQueryStatementCondition(node,sPARQL,owner);
		}
	}

	/**
	 * The <code>translateQueryStatementCondition</code> method translates a condition in the query 
	 * statement from XML to SPARQL.
	 *
	 * @param node is the root node of the XML structure that should be processed.
	 * @param sPARQLQuery is the query where the translation of the condition should be stored. The variable's
	 * value may be changed in this method.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the name of a newly created "answer" variable, if any.
	 */
	private static String translateQueryStatementCondition(
			Node node, SPARQLQuery sPARQLQuery, String owner) {
		String tagName;						//The name of an XML tag
		String operator;					//A binary operator in SPARQL format (>, etc.)
		String operand1, operand2;			//The operator's two operands in SPARQL format (?num1, 0, etc.)
		ArrayList<Node> operands;			//A list of nodes that includes the operands of an operator
		Node negatedCondition;				//A node that might have the condition inside a <not>
		SPARQLQuery tempQuery;				//A query for temporary use
		SPARQLQuery subSelect;				//A nested SELECT clause
		String formula;						//A formula that compares the two operands with the operator
		String variable;					//The name of a SPARQL variable
		String negativeVariable;			//The name of a SPARQL that has the negated value of variable
		EventDefinition event1,event2;		//The arguments of a temporal operator
		String arguments;					//The arguments of a call to IsTemporalRelationWithQuantities
		Boolean isDoneExistsBVar;			//Specifies whether we have finished the <bvar> in <exists>
		Boolean isDoneExistsCondition;		//Specifies whether we have finished the <condition> in <exists>
		ArrayList<Node> existsNodes;		//The <bvar> and <condition> nodes in an <exists> block
		String existsType;					//Which of those two
		Node existsConditionNode;			//One of those conditions
		String warning;						//A message to let the user know there might be a problem
		TranslateConditionReturnValue returnedValue;	//A value returned by a translateCondition()

		tagName = node.getNodeName();
		tagName = tagName.toLowerCase();
		if (Global.LOGICAL_OPERATORS.contains(tagName)) {		//Is it a logical operator?
			if ((tagName.equalsIgnoreCase("and")) || (tagName.equalsIgnoreCase("or"))) {
				formula = "";
				operator = "";
				operands = Global.getChildNodes(node,owner);
				for (Node operand : operands) {
					subSelect = new SPARQLQuery();									
					variable = translateQueryStatementCondition(
							operand,subSelect,"an <" + tagName + "> in "+owner);
					if (variable != null) {
						sPARQLQuery.addRestriction(new QueryRestriction(subSelect,false));
						formula += operator + variable;
						if (tagName.equalsIgnoreCase("and")) {
							operator = " && ";
						} else {
							operator = " || ";
						}
					}
					setsIncludedInTheQuery = new HashSet<String>(5);	//Reset for the next conjunct
				}
				if (formula.equals("")) {
					warning = 
							"WARNING in XMLToSPARQLTranslator.translateQueryStatementCondition in " + 
							owner + ": Found an empty <" + tagName + "> block.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
					formula = "true";
				}
				variable = Global.createNewVariable("answer");
				sPARQLQuery.addSelectFormula(formula, variable);
				return variable;
			}
			if (tagName.equalsIgnoreCase("not")) {
				negatedCondition = Global.getChildNode(node,owner);
				if (negatedCondition != null) {
					tempQuery = new SPARQLQuery();									
					variable = translateQueryStatementCondition(
							negatedCondition,tempQuery,"a <not> in "+owner);
					sPARQLQuery.addRestriction(new QueryRestriction(tempQuery,false));
					negativeVariable = Global.createNewVariable("answer");
					formula = (" ! " + variable);
					sPARQLQuery.addSelectFormula(formula,negativeVariable);
					return negativeVariable;
				}
				variable = Global.createNewVariable("answer");		//There was no negated condition
				formula = "false";
				sPARQLQuery.addSelectFormula(formula, variable);
				return variable;
			}
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateQueryStatementCondition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return null;
		} else if (Global.NUMERICAL_COMPARISON_OPERATORS.keySet().contains(tagName)) {
			operator = Global.NUMERICAL_COMPARISON_OPERATORS.get(tagName);
			operands = Global.getChildNodes(node,owner);
			operand1 = null;											//Initialize
			operand2 = null;											//Initialize
			if (operands.size() > 0) {
				operand1 = translateNumericExpression(
						operands.get(0),sPARQLQuery,"a <"+tagName+"> in "+owner);
			}
			if (operands.size() > 1) {
				operand2 = translateNumericExpression(
						operands.get(1),sPARQLQuery,"a <"+tagName+"> in "+owner);
			}
			if (operands.size() > 2) {
				warning = 
						"WARNING in XMLToSPARQLTranslator." +
						"translateQueryStatementCondition in a <" + tagName + "> in " + owner + 
						": <" + tagName + "> is a binary operator, so it cannot have more than 2 arguments."; 
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			if (operand2 == null) {
				warning = 
						"WARNING in XMLToSPARQLTranslator." +
						"translateQueryStatementCondition in a <" + tagName + "> in " + owner + ": <" + 
						tagName + "> is a binary operator, so it needs 2 arguments."; 
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				operand1 = "0";
				operand2 = "0";
			}
			if (
					(
							( ! Global.isNumber(operand1)) &&		//A variable
							(
									(operator.equals("=")) ||
									(operator.equals("<")) ||
									(operator.equals("<=")))) ||
					(
							( ! Global.isNumber(operand2)) &&		//A variable
							(
									(operator.equals("=")) ||
									(operator.equals(">")) ||
									(operator.equals(">="))))) {
				Global.useSubsetOfViews = true;
			}
			if ((Global.isNumber(operand1)) && ( ! Global.isNumber(operand2))) {
				Global.hasStandardQueryStatement = true;
				Global.mathematicalComparisonOperator = Global.reverseOperator(operator);
				Global.number = Double.valueOf(operand1);
			} else if ((Global.isNumber(operand2)) && ( ! Global.isNumber(operand1))) {
				Global.hasStandardQueryStatement = true;
				Global.mathematicalComparisonOperator = operator;
				Global.number = Double.valueOf(operand2);
			}
			formula = operand1 + " " + operator + " " + operand2;
			variable = Global.createNewVariable("answer");
			sPARQLQuery.addSelectFormula(formula,variable);
			return variable;
		} else if (Global.TEMPORAL_OPERATORS.keySet().contains(tagName)) {	//Is it a temporal operator?
			event1 = null;										//Initialize
			event2 = null;										//Initialize
			operator = Global.TEMPORAL_OPERATORS.get(tagName);
			operands = Global.getChildNodes(node,owner);
			if (operands.size() > 0) {
				event1 = translateEvent(operands.get(0),sPARQLQuery,"a <"+tagName+"> in "+owner);
			}
			if (operands.size() > 1) {
				event2 = translateEvent(operands.get(1),sPARQLQuery,"a <"+tagName+"> in "+owner);
			}
			if (operands.size() > 2) {
				warning = 
						"WARNING in XMLToSPARQLTranslator." +
						"translateQueryStatementCondition in a <" + tagName + "> in " + owner + 
						": <" + tagName + "> is a binary operator, so it cannot have 3 arguments.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			variable = Global.createNewVariable("answer");
			arguments = addIsTemporalRelationWithQuantities(
					tagName,event1,event2,sPARQLQuery,variable,"a <"+tagName+"> in "+owner);
			if (arguments != null) {
				arguments = "\"TEMPORAL_RELATION\",\"" + operator + "\"," + arguments;
				sPARQLQuery.addRestriction(
						"FILTER (fn:IsTemporalRelationWithQuantities(" + arguments + ")) .",false);
				sPARQLQuery.addSelectFormula("COUNT(*) > 0",variable);
			}
			return variable;
		} else if (Global.QUANTIFIERS.contains(tagName)) {
			variable = null;					//Initialize
			if (tagName.equalsIgnoreCase("exists")) {
				isDoneExistsBVar = false;			//Initialize
				isDoneExistsCondition = false;		//Initialize
				existsNodes = Global.getChildNodes(node,owner);
				for (Node existsNode : existsNodes) {
					existsType = existsNode.getNodeName();
					existsType = existsType.toLowerCase();
					if (existsType.equalsIgnoreCase("bvar")) {
						if (isDoneExistsBVar == false) {
							Global.translateBoundVariables(
									existsNode,"the <bvar> in an <exists> in "+owner);
							//There's no need to do anything with the bound variables
							isDoneExistsBVar = true;
						} else {
							System.err.println(
									"WARNING in XMLToSPARQLTranslator." +
									"translateQueryStatementCondition in an <exists> in " + 
									owner + ": Found more than 1 <bvar> in an <exists> block."); 
						}
					} else if (existsType.equalsIgnoreCase("condition")) {
						if (isDoneExistsCondition == false) {
							existsConditionNode = Global.getChildNode(existsNode,"an <exists> in "+owner);
							if (existsConditionNode != null) {
								variable = Global.createNewVariable("answer");
								sPARQLQuery.addSelectFormula("COUNT(*) > 0",variable);
								returnedValue = TranslateCondition.translateCondition(
										existsConditionNode,
										null,					
										"the <condition> in an <exists> in "+owner);
								sPARQLQuery.addRestrictions(returnedValue.getSPARQLQuery());
								isDoneExistsCondition = true;
							} else {
								System.err.println(
										"WARNING in XMLToSPARQLTranslator." +
										"translateQueryStatementCondition in the <condition> in an " +
										"<exists> in " + owner + ": Found an empty condition " +
										"in an <exists> block in the <QueryStatement>.");
							}
						} else {
							warning = 
									"WARNING in XMLToSPARQLTranslator." +
									"translateQueryStatementCondition in an <exists> in " + owner + 
									": Found more than one <condition> in an <exists> block in the " +
									"<QueryStatement>.";
							System.err.println(warning);
					    	Global.unableToRespondMessage.add(warning);
						}
					} else {
						warning = 
								"WARNING in XMLToSPARQLTranslator.translateQueryStatementCondition in " +
								"an <exists> in " + owner + ": Found an unexpected tag, <" +
								existsType + ">, in an <exists> block in the <QueryStatement>.";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
				}
				return variable;
			}
			if (tagName.equalsIgnoreCase("forall")) {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateQueryStatementCondition in " + owner + 
						": <forall> has been removed from the formal language specification.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return null;
			}
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateQueryStatementCondition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return variable;
		} else if (
				(Global.OBJECT_TYPES.containsKey(tagName)) || 
				(Global.COLORS.containsKey(tagName)) || 
				(Global.PREDICATES.containsKey(tagName)) || 
				(Global.PREDICATES_FILTER_FUNCTIONS.containsKey(tagName)) ||
				(Global.SPECIAL_RELATIONSHIPS.contains(tagName))) {
			variable = Global.createNewVariable("answer");
			sPARQLQuery.addSelectFormula("COUNT(*) > 0",variable);
			returnedValue = TranslateCondition.translateCondition(node,null,owner);
			sPARQLQuery.addRestrictions(returnedValue.getSPARQLQuery());
			return variable;
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateQueryStatementCondition in " + owner + 
					": Found an unexpected tag, <" + tagName + ">, in the <QueryStatement>.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return null;
		}
	}


	/**
	 * The <code>translateNonPolarQueryStatementQuestion</code> method translates a question in the 
	 * non-polar query statement from XML to SPARQL.
	 *
	 * @param node is the root node of the XML structure that should be processed.
	 * value may be changed in this method.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateNonPolarQueryStatementQuestion(Node node, String owner) {
		String tagName;							//The name of an XML tag
		String warning;							//A message to let the user know there might be a problem
		
		tagName = node.getNodeName();
		tagName = tagName.toLowerCase();
		if (tagName.equalsIgnoreCase("what")) {
			translateWhat(node,"a <what> in "+owner);
		} else if (tagName.equalsIgnoreCase("when")) {
			translateWhen(node,"a <when> in "+owner);
		} else if (tagName.equalsIgnoreCase("where")) {
			translateWhere(node,"a <where> in "+owner);
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateNonPolarQueryStatementQuestion in " + owner + 
					": Found an unexpected tag in a <NonPolarQueryStatement>, <" + tagName + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}

	/**
	 * The <code>translateWhat</code> method translates a &lt;what&gt; question in the 
	 * non-polar query statement from XML to SPARQL.
	 *
	 * @param node is the &lt;what&gt; node that should be processed.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateWhat(Node node, String owner) {
		String setName;										//The name of the set in the <what>
		SetDefinition set;									//The set's definition object
		ArrayList<String> variables;						//The bound variables in the set
		String warning;										//A message to let the user know there might be a problem
		
		Global.inputType = XMLType.What;
		setName = ((Element)node).getTextContent().trim();
		set = translateSet(node,owner);
		variables = set.getBoundVariables();
		if (variables.size() == 0) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateWhat in " + owner + 
					": The set, \"" + setName + "\" has no bound variables.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return;
		}
		if (variables.size() > 1) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateWhat in " + owner + 
					": The set in a <what> should have only one bound variable, but \"" + 
					setName + "\" has " + variables.size() + ".";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		sPARQL = set.getSPARQL();
		sPARQL.addPrefixes();
	}
	
	/**
	 * The <code>translateWhen</code> method translates a &lt;when&gt; question in the 
	 * non-polar query statement from XML to SPARQL.
	 *
	 * @param node is the &lt;when&gt; node that should be processed.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateWhen(Node node, String owner) {
		Global.inputType = XMLType.When;
		translateEvent(node,sPARQL,owner);
	}

	/**
	 * The <code>translateWhere</code> method translates a &lt;where&gt; question in the 
	 * non-polar query statement from XML to SPARQL.
	 *
	 * @param node is the &lt;where&gt; node that should be processed.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateWhere(Node node, String owner) {
		ArrayList<Node> whereNodes;		//The nodes inside the <where> block
		String whereNodeName;			//The name of one of those nodes
		ObjectDefinition object;		//The object specified in the <where> block
		String objectId;				//The database identifier of the object specified in the <where> block
		TimePeriodDefinition time;		//The time specified in the <where> block
		String observer;				//The observer specified in the <where> block (optional)
		String xMLId;					//An identifier in the XML query
		StringBuffer whereRestriction;	//The filter function call to ComputeWhere()
		String newVariable;				//A variable for the SELECT line of the SPARQL query
		String comma;					//Specifies whether to print a comma
		String warning;					//A message to let the user know there might be a problem
		
		Global.inputType = XMLType.Where;
		objectId = null;							//Initialize
		time = null;								//Initialize
		observer = null;							//Initialize
		whereRestriction = new StringBuffer();		//Initialize
		whereNodes = Global.getChildNodes(node,owner);
		for (Node whereNode : whereNodes) {
			whereNodeName = whereNode.getNodeName();
			whereNodeName.toLowerCase();
			if (whereNodeName.equals("object")) {
				xMLId = whereNode.getTextContent().trim();
				object = (ObjectDefinition)Global.getDefinition(xMLId,owner);
				if (object != null) {
					objectId = object.getObjectId();
					Global.unableToRespondMessage.addAll(object.getUnableToRespondMessage());
				}
			} else if (whereNodeName.equals("time")) {
				xMLId = whereNode.getTextContent().trim();
				time = (TimePeriodDefinition)Global.getDefinition(xMLId,owner);
			} else if (whereNodeName.equals("observer")) {
				observer = whereNode.getTextContent().trim();
			} else {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateWhere in " + owner + 
						": Found an unexpected tag, <" + whereNodeName + ">, in a <where> block.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
		}
		comma = "";
		whereRestriction.append("FILTER (fn:ComputeWhere(");
		if (observer != null) {
			whereRestriction.append("\"OBSERVER\",\"" + observer + "\"");
			comma = ",";
		}
		if (time != null) {
			whereRestriction.append(comma + time.toString());
			comma = ",";
		}
		if (objectId == null) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateWhere in " + owner + 
					": Found a <where> block with no <object> in it.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		} else {
			whereRestriction.append(comma + "\"OBJECT_ID\",\"" + objectId + "\"");
		}
		whereRestriction.append(")) .");
		sPARQL.addRestriction(whereRestriction.toString(),false);
		newVariable = Global.createNewVariable("answer");
		sPARQL.addSelectFormula("COUNT(*) > 0",newVariable);
	}
	
	/**
	 * The <code>addIsTemporalRelationWithQuantities</code> method creates a call to the filter function,
	 * isTemporalRelationWithQuantities, and adds it to the given query.
	 *
	 * @param operator is the temporal operator for this filter function.
	 * @param event1 is one of the arguments of the temporal operator.
	 * @param event2 is the other argument.
	 * @param sPARQLQuery is the query where the translation of the condition should be stored.
	 * @param variable is the name of the "answer" variable that will be used in the SELECT block of 
	 * <code>sPARQLQuery</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the arguments of the IsTemporalRelationWithQuantities function, except for the first
	 * one, the temporal operator.
	 */
	private static String addIsTemporalRelationWithQuantities(
			String operator, EventDefinition event1, EventDefinition event2, SPARQLQuery sPARQLQuery,
			String variable, String owner) {
		Pattern temporalOperator;			//Used for matching lists of temporal operators
		String event1Arguments,event2Arguments;	//Arguments of the IsTemporalRelationWithQuantities() function
		String warning;							//A message to let the user know there might be a problem

		if ((event1 == null) || (event2 == null)) {
			warning = 
					"WARNING in XMLToSPARQLTranslator.addIsTemporalRelationWithQuantities in " + owner + 
					": <" + operator + "> is a binary operator, so it needs 2 arguments."; 
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return null;
		}
		temporalOperator = Pattern.compile(".*" + operator + ".*", Pattern.CASE_INSENSITIVE);
		if (
				(event1.isGuaranteedEqual(false)) 
				|| 
				(event2.isGuaranteedEqual(false)) 
				||
				((event1.isGuaranteedEqual(true)) && 
				temporalOperator.matcher(
						"temporal-precedes|temporal-meets|temporal-overlaps|temporal-starts|" +
						"temporal-strictly-before").matches())
				||
				((event2.isGuaranteedEqual(true)) && 
				temporalOperator.matcher(
						"temporal-precedes|temporal-meets|temporal-overlaps|temporal-finished-by|" +
						"temporal-contains|temporal-before|temporal-strictly-before|temporal-after").
						matches())
				) {
			sPARQLQuery.deleteAllRestrictions();		//Restrictions are not needed in this case
			sPARQLQuery.addSelectFormula("COUNT(*) = -1",variable);	//Always false
			return null;
		}
		if ((event1.isGuaranteedEqual(true)) && (event2.isGuaranteedEqual(true))) { 
			sPARQLQuery.deleteAllRestrictions();		//Restrictions are not needed in this case
			sPARQLQuery.addSelectFormula("COUNT(*) = 1",variable);	//Always true
			return null;
		}
		if ((event1.isGuaranteedEqual(true)) || (event2.isGuaranteedEqual(true))) {
			addAlwaysTrueRestriction(sPARQLQuery);
		}
		event1Arguments = getEventArguments(event1,owner);
		event2Arguments = getEventArguments(event2,owner);
		return event1Arguments + "," + event2Arguments;
	}
	
	/**
	 * The <code>translateSet</code> method translates an XML expression that represents a set into the
	 * SPARQL lines that define the same set.
	 *
	 * @param set is an XML expression that specifies a set.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a structure that has information about that set.
	 */
	private static SetDefinition translateSet(Node set, String owner) {
		String setId;							//The name of a predefined set
		String setNodeName;						//The name of the node at the head of the set
		ArrayList<Node> subsets;				//Some sets that are used to create the set
		ArrayList<String> boundVariables;		//The bound variables of the given set
		ArrayList<String> subsetBoundVariables;	//The bound variables of a subset of the given set
		ArrayList<String> subsetsBoundVariables;//The bound variables in combined subsets of the given set
		Integer numSubsets;						//The number of sets that are used to create the set
		Integer numBoundVariables;				//The number of bound variables in a set
		Integer subsetNumBoundVariables;		//The number of bound variables in that subset
		UnionRestriction restriction;			//One subset in a <union> block
		SetDefinition subsetSPARQL;				//The SPARQL translation of that subset
		String s1,s2;							//Specifies whether an "s" should be printed
		Integer count;							//For counting iterations of loops
		String warning;							//A message to let the user know there might be a problem
		SetDefinition returnValue;
		
		returnValue = new SetDefinition();
		setNodeName = set.getNodeName();
		setNodeName = setNodeName.toLowerCase();
		if ((setNodeName.equalsIgnoreCase("ci")) || (setNodeName.equalsIgnoreCase("what"))) {
			setId = set.getTextContent().trim();
			if (Global.isSetId(setId)) {			//Just make sure
				if (Global.hasDefinition(setId)) {
					returnValue = (SetDefinition)Global.getDefinition(setId,"a <"+setNodeName+"> in "+owner);
					Global.unableToRespondMessage.addAll(returnValue.getUnableToRespondMessage());
					findDefinitionsInSet(returnValue);
				} else {
					warning = 
							"WARNING in XMLToSPARQLTranslator.translateSet in a <" + setNodeName + 
							"> in " + owner + ": Found an undefined set name, \"" + setId + "\".";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
			} else {
				System.err.println(
						"WARNING in XMLToSPARQLTranslator.translateSet in a <" + setNodeName +
						"> in " + owner + ": The set name, \"" + setId + "\", should begin with \"set-\".");
			}
		} else if (Global.SET_OPERATORS.contains(setNodeName)) {		//Is it a set operator?
			if (setNodeName.equalsIgnoreCase("intersection")) {
				numSubsets = 0;									//Initialize
				numBoundVariables = 0;							//Initialize
				subsets = Global.getChildNodes(set,owner);
				for (Node subset : subsets) {
					returnValue = Global.getUnion(
							returnValue,
							translateSet(subset,"an <intersection> in "+owner));
					if (numSubsets == 0) {
						numBoundVariables = returnValue.getBoundVariables().size();
					} else {
						subsetNumBoundVariables = 
								returnValue.getBoundVariables().size() - numBoundVariables*numSubsets;
						if (numBoundVariables != subsetNumBoundVariables) {
							if (numBoundVariables == 1) { s1 = ""; }
							else { s1 = "s"; }
							if (subsetNumBoundVariables == 1) { s2 = ""; }
							else { s2 = "s"; }
							warning = 
									"WARNING in XMLToSPARQLTranslator.translateSet in an " +
									"<intersection> in " + owner + ": It's not possible " + 
									"to intersect a set that has " + numBoundVariables + 
									" bound variable" + s1 + " with a set that has " + 
									subsetNumBoundVariables + " bound variable" + s2 + ".";
							System.err.println(warning);
					    	Global.unableToRespondMessage.add(warning);
							numBoundVariables = Math.min(numBoundVariables,subsetNumBoundVariables);
						}
						subsetsBoundVariables = returnValue.getBoundVariables();
						for (count = 0; count < numBoundVariables; count++) {
							returnValue.addRestriction(
									"FILTER (" + subsetsBoundVariables.get(count) + " = " + 
									subsetsBoundVariables.get(count + (numBoundVariables * numSubsets)) + 
									") .",
									false);
						}
					}
					numSubsets++;
				}
			} else if (setNodeName.equalsIgnoreCase("union")) {
				numSubsets = 0;									//Initialize
				numBoundVariables = 0;							//Initialize
				subsetBoundVariables = new ArrayList<String>();	//Initialize
				boundVariables = new ArrayList<String>();		//Initialize
				restriction = new UnionRestriction();			//Initialize
				subsets = Global.getChildNodes(set,owner);
				for (Node subset : subsets) {
					subsetSPARQL = translateSet(subset,"a <union> in "+owner);
					if (numSubsets == 0) {
						boundVariables = subsetSPARQL.getBoundVariables();
						numBoundVariables = boundVariables.size();
						returnValue.addBoundVariables(boundVariables);
					} else {
						subsetBoundVariables = subsetSPARQL.getBoundVariables();
						subsetNumBoundVariables = subsetBoundVariables.size();
						if (numBoundVariables != subsetNumBoundVariables) {
							if (numBoundVariables == 1) { s1 = ""; }
							else { s1 = "s"; }
							if (subsetNumBoundVariables == 1) { s2 = ""; }
							else { s2 = "s"; }
							warning = 
									"WARNING in XMLToSPARQLTranslator.translateSet in a <union> in " + 
									owner + ": It's not possible to form the union of a set that has " + 
									numBoundVariables + " bound variable" + s1 + " and a set that has " + 
									subsetNumBoundVariables + " bound variable" + s2 + ".";
							System.err.println(warning);
					    	Global.unableToRespondMessage.add(warning);
							numBoundVariables = Math.min(numBoundVariables,subsetNumBoundVariables);
						}
						for (count = 0; count < numBoundVariables; count++) {
							subsetSPARQL.replaceVariable(
									subsetBoundVariables.get(count),boundVariables.get(count),owner);
						}
					}
					numSubsets++;
					restriction.addRestrictionsGroup(subsetSPARQL.getRestrictions());
				}
				returnValue.addRestriction(restriction);
			} else if (setNodeName.equalsIgnoreCase("cardinality")) {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateSet in " + owner + 
						": Found a <cardinality> node where a set should be specified.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			} else {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateSet in " + owner + 
						": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateSet in " + owner + 
					": Found a <" + setNodeName + "> where a set should be specified.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		return returnValue;
	}

	/**
	 * The <code>translateNumericExpression</code> method translates a portion of the XML query that 
	 * represents a number in a &lt;QueryStatement&gt; into SPARQL.
	 *
	 * @param numericExpression is a portion of the XML query that returns a number.
	 * @param sPARQLQuery is the query where the translation of the condition should be stored.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the SPARQL text that returns the same number.
	 */
	private static String translateNumericExpression(
			Node numericExpression, SPARQLQuery sPARQLQuery, String owner) {
		String numericExpressionName;			//The name of the head node
		String newVariable;						//A variable to hold the value of a numeric expression
		SPARQLQuery subSelect,subSubSelect;		//Nested SELECT clauses
		Node set;								//One of those nodes that might have the set
		String warning;							//A message to let the user know there might be a problem
		String returnValue;

		returnValue = new String();
		numericExpressionName = numericExpression.getNodeName();
		numericExpressionName = numericExpressionName.toLowerCase();
		if (numericExpressionName.equalsIgnoreCase("cn")) {	//It's simply a constant number
			returnValue = numericExpression.getTextContent().trim();
		} else if (numericExpressionName.equalsIgnoreCase("cardinality")) {			//A set's cardinality
			newVariable = Global.createNewVariable("number");
			subSelect = new SPARQLQuery();
			sPARQLQuery.addSubselect(subSelect);
			subSelect.addSelectFormula("COUNT(*)",newVariable);
			subSubSelect = null;
			set = Global.getChildNode(numericExpression,owner);
			if (set != null) {
				subSubSelect = translateSet(set,"a <cardinality> in "+owner).getSPARQL();
			} else {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateNumericExpression in a " +
						"<cardinality> in " + owner + ": Found an empty <cardinality> block.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return "0";
			}
			subSelect.addSubselect(subSubSelect);
			if (! setDefinitions.contains(subSubSelect)) {
				setDefinitions.add(subSubSelect);
			}
			returnValue = newVariable;
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateNumericExpression in " + owner + 
					": Found a <" + numericExpressionName + 
					"> node where a numeric expression should be specified.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return "0";
		}
		return returnValue;
	}

	/**
	 * The <code>translateEvent</code> method translates a portion of the XML query that specifies an event.
	 *
	 * @param eventNode is the XML node that contains the event.
	 * @param sPARQLQuery is the query that should be modified by the translation.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return an object with the definition of the event.
	 */
	private static EventDefinition translateEvent(Node eventNode, SPARQLQuery sPARQLQuery, String owner) {
		String eventNodeName;				//The type of the given node
		String eventId;						//The name of the event
		EventDefinition eventDefinition;	//The definition of that event
		String variable;					//The name of a SPARQL variable
		String arguments;					//The arguments of a call to ComputeWhen
		String warning;						//A message to let the user know there might be a problem
		
		eventDefinition = null;						//Initialize
		eventNodeName = eventNode.getNodeName();
		eventNodeName = eventNodeName.toLowerCase();
		if (eventNodeName.equalsIgnoreCase("ci")) {	
			eventId = eventNode.getTextContent().trim();
			if (! Global.isEventId(eventId)) {			//Just make sure
				System.err.println(
						"WARNING in XMLToSPARQLTranslator.translateEvent in a <ci> in " + owner + 
						": The event name, \"" + eventId + "\", should begin with \"event-\".");
			}
			if (Global.hasDefinition(eventId)) {
				eventDefinition = (EventDefinition)Global.getDefinition(eventId,owner);
				Global.unableToRespondMessage.addAll(eventDefinition.getUnableToRespondMessage());
				translateEventDefinition(eventDefinition,eventId,sPARQLQuery,owner);
			} else {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateEvent in a <ci> in " + owner +
						": Found an undefined event name, \"" + eventId + "\".";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return null;
			}
		} else if (eventNodeName.equalsIgnoreCase("when")) {
			eventId = eventNode.getTextContent().trim();
			if (! Global.isEventId(eventId)) {			//Just make sure
				System.err.println(
						"WARNING in XMLToSPARQLTranslator.translateEvent in " + owner + 
						": The event name, \"" + eventId + "\", should begin with \"event-\".");
			}
			if (Global.hasDefinition(eventId)) {
				eventDefinition = (EventDefinition)Global.getDefinition(eventId,owner);
				Global.unableToRespondMessage.addAll(eventDefinition.getUnableToRespondMessage());
				translateEventDefinition(eventDefinition,eventId,sPARQLQuery,owner);
				variable = Global.createNewVariable("answer");
				arguments = getEventArguments(eventDefinition,"a <when> in "+owner);
				if (arguments != null) {
					sPARQLQuery.addRestriction(
							"FILTER (fn:ComputeWhen(" + arguments + ")) .",false);
					sPARQLQuery.addSelectFormula("COUNT(*) > 0",variable);
				}
			} else {
				warning = 
						"WARNING in XMLToSPARQLTranslator.translateEvent in " + owner +
						": Found an undefined event name, \"" + eventId + "\"."; 
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return null;
			}
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.translateEvent in " + owner + 
					": Found an unexpected tag, <" + eventNodeName + 
					">, where an event should be specified.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return null;
		}
		return eventDefinition;
	}
	
	/**
	 * The <code>translateEventDefinition</code> method translates an event that is required to translate a 
	 * portion of the XML query that specifies an event. This might be the event specified in the XML query, 
	 * or it might be a subevent of that event.
	 *
	 * @param eventDefinition is the definition of the event that should be translated.
	 * @param eventId is the name of the event specified in the XML query.
	 * @param sPARQLQuery is the query that should be modified by the translation.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private static void translateEventDefinition(
			EventDefinition eventDefinition, String eventId, SPARQLQuery sPARQLQuery, String owner) {
		String setId;						//The name of one of the sets in the event definition
		SPARQLQuery subQuery;				//A query that is a restriction in the given query
		EventDefinition subEvent1,subEvent2;//Event definitions within the given event definition
		String logicalOperator;				//The logical operator in the event definition, if there is one
		String warning;						//A message to let the user know there might be a problem

		logicalOperator = eventDefinition.getLogicalOperator();
		if (
				(logicalOperator == null) && 
				(eventDefinition.getIsGuaranteed() == null)) {
			setId = eventDefinition.getSetId1();
			if (setId != null) {
				if (Global.isSetId(setId)) {			//Just make sure
					if (Global.hasDefinition(setId)) {
						findDefinitionsInSet((SetDefinition)Global.getDefinition(setId,owner));
					} else {
						warning = 
								"WARNING in XMLToSPARQLTranslator.translateEventDefinition " +
								"in the definition of the event, \"" + eventId + "\", in " + owner + 
								": Found an undefined set name, \"" + setId + "\".";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
				}
			}
			if ( ! setsIncludedInTheQuery.contains(setId)) {		//Have we already added it?
				subQuery = createRecordCardinalitySubQuery(
						setId,
						"the definition of the event, \"" + eventId + "\", in " + owner);
				if (subQuery != null) {
					sPARQLQuery.addRestriction(new QueryRestriction(subQuery,true));
					setsIncludedInTheQuery.add(setId);
				}
			}
			setId = eventDefinition.getSetId2();
			if (setId != null) {
				if (Global.isSetId(setId)) {			//Just make sure
					if (Global.hasDefinition(setId)) {
						findDefinitionsInSet((SetDefinition)Global.getDefinition(setId,owner));
					} else {
						warning = 
								"WARNING in XMLToSPARQLTranslator.translateEventDefinition " +
								"in the definition of the event, \"" + eventId + "\", in " +
								owner + ": Found an undefined set name, \"" + setId + "\".";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
				}
			}
			if ( ! setsIncludedInTheQuery.contains(setId)) {		//Have we already added it?
				subQuery = createRecordCardinalitySubQuery(
						setId,
						"the definition of the event, \"" + eventId + "\", in " + owner);
				if (subQuery != null) {
					sPARQLQuery.addRestriction(new QueryRestriction(subQuery,false));
					setsIncludedInTheQuery.add(setId);
				}
			}
		} else if (logicalOperator != null) {
			subEvent1 = eventDefinition.getEvent1();
			if (subEvent1 != null) {
				translateEventDefinition(subEvent1,eventId,sPARQLQuery,owner);
				if (! logicalOperator.equals("not")) {
					subEvent2 = eventDefinition.getEvent2();
					if (subEvent2 != null) {		//"not" has only one operand
						translateEventDefinition(subEvent2,eventId,sPARQLQuery,owner);
					} else {
						System.err.println(
								"WARNING in XMLToSPARQLTranslator.translateEventDefinition in the event, \"" + 
								eventId + "\", in " + owner + ": <" + logicalOperator + 
								"> should have at least two operands.");
					}
				}
			} else {
				System.err.println(
						"WARNING in XMLToSPARQLTranslator.translateEventDefinition in the event, \"" + 
						eventId + "\", in " + owner + ": <" + logicalOperator + "> requires an operand.");
			}
		}		//Method addIsTemporalRelationWithQuantities() handles the isGuaranteed() cases
	}
	
	/**
	 * The <code>createRecordCardinalitySubQuery</code> method creates a query that calls the filter function
	 * RecordCardinality for each member of the given set and returns that query.
	 *
	 * @param setId is the name of the set whose members must be counted.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a new SPARQL query that counts the number of elements in <code>setId</code>. If 
	 * <code>setId</code> is <b><code>null</code></b>, then this method returns <b><code>null</code></b>.
	 */
	private static SPARQLQuery createRecordCardinalitySubQuery(String setId, String owner) {
		SetDefinition setDefinition;		//The definition of the given set
		SPARQLQuery setSPARQL;				//The SPARQL translation of that set
		String warning;							//A message to let the user know there might be a problem
		SPARQLQuery returnValue;

		returnValue = new SPARQLQuery();						//Initialize
		if (setId == null) {
			return null;
		}
		if (Global.hasDefinition(setId)) {
			setDefinition = (SetDefinition)Global.getDefinition(setId,owner);
			if (! setsIncludedInTheQuery.contains(setId)) {		//Don't count the cardinality of a set twice
				setSPARQL = setDefinition.getSPARQL();
				returnValue.addRestrictions(setSPARQL.getRestrictions());
				returnValue.addRecordCardinalityRestriction(setDefinition);
				if (! setDefinitions.contains(setSPARQL)) {
					setDefinitions.add(setSPARQL);
				}
			}
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.createRecordCardinalitySubQuery in " + owner +
					": " + "Found an undefined set name, \"" + setId + "\".";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		return returnValue;
	}	

	/**
	 * The <code>composeObjectTypeQuery</code> method creates a SPARQL query that asks for the type of the
	 * given MSEE object and stores it in <code>sPARQL</code>.
	 *
	 * @param object is the MSEE object that we need to know the type of.
	 * @return a SPARQL query designed to acquire the type of <code>object</code>.
	 */
	private static SPARQLQuery composeObjectTypeQuery(ObjectDefinition object) {
		String variable;						//The variable in the SELECT line of the new query
		SPARQLQuery returnValue;
		
		returnValue = new SPARQLQuery();						//Initialize
		returnValue.addPrefixes();
		returnValue.distinct();
		variable = Global.createNewVariable("type");
		returnValue.addSelectVariable(variable);
		returnValue.addRestriction("data:" + object.getObjectId() + " rdf:type " + variable + " .",false);
		return returnValue;
	}
	
	/**
	 * The <code>constructUnknownObjectsResponse</code> method returns an &lt;UnknownObjects&gt; response that
	 * should be sent to the EES. If this method is called, it is assumed that there is at least one unknown
	 * object.
	 *
	 * @return a list of object identifiers within an &lt;UnknownObjects&gt; tag.
	 */
	private static String constructUnknownObjectsResponse() {
		String comma;					//Specifies whether to print a comma
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();
		returnValue.append("<UnknownObjects>");
		comma = "";
		for (String unknownObject : Global.unknownObjects) {
			returnValue.append(comma + unknownObject);
			comma = ",";
		}
		returnValue.append("</UnknownObjects>");
		return returnValue.toString();
	}
	
	/**
	 * The <code>addAlwaysTrueRestriction</code> method adds a new restriction to the query that is designed
	 * to create a RecordCardinality() set that occurs once at all times. (The 
	 * IsTemporalRelationWithQuantities() function will test that this special set has a quantity of at least
	 * one, so it will be true at all times.)
	 * 
	 * @param sPARQLQuery is the query where the restriction should be stored.
	 */
	private static void addAlwaysTrueRestriction(SPARQLQuery sPARQLQuery) {
		String trueRestriction;						//The special restriction
		String newVariable;							//A new variable to put in the SELECT line
		SPARQLQuery trueQuery;						//It's in a query
		QueryRestriction trueQueryRestriction; 		//Which becomes a query restriction
		LinkedHashSet<Restriction> trueRestrictions;		//Which is the only restriction in a restriction group
		UnionRestriction trueUnionRestriction;		//And that's in a union restriction
		
		trueRestriction = "FILTER (fn:RecordCardinality(\"SET_NAME\",\"set--TRUE\")) .";
		trueQuery = new SPARQLQuery();
		newVariable = Global.createNewVariable("number");
		trueQuery.addSelectFormula("COUNT(*)",newVariable);
		trueQuery.addRestriction(trueRestriction,false);
		trueQueryRestriction = new QueryRestriction(trueQuery,false);
		trueRestrictions = new LinkedHashSet<Restriction>(1);		//There is only one restriction
		trueRestrictions.add(trueQueryRestriction);
		trueUnionRestriction = new UnionRestriction();
		trueUnionRestriction.addRestrictionsGroup(trueRestrictions);
		sPARQLQuery.addRestriction(trueUnionRestriction);
	}
	
	/**
	 * The <code>setDefinitionsToStrings</code> method returns a list of SPARQL queries, each of which is 
	 * designed to return the values of the bound variables of a set that is defined in the XML query.
	 *
	 * @return a SPARQL query for each set defined in the XML query.
	 */
	public ArrayList<String> setDefinitionsToStrings() {
		ArrayList<String> returnValue;
		SPARQLQuery setDefinitionCopy;			//Copy so that the original doesn't get any prefixes
		
		returnValue = new ArrayList<String>(setDefinitions.size());
		setDefinitionCopy = new SPARQLQuery();									//Initialization
		for (SPARQLQuery setDefinition : setDefinitions) {
			setDefinitionCopy.copy(setDefinition);
			setDefinitionCopy.addPrefixes();
			returnValue.add(setDefinitionCopy.toString());
		}
		return returnValue;
	}

	/**
	 * The <code>getOutermostTag</code> method returns the top-level XML tag in the variable <code>xML</code>.
	 *
	 * @return the outermost tag of <code>xML</code>, including the angled brackets, as a string.
	 */
	private static String getOutermostTag() {
		NodeList outermostNodes;		//A list with one element, the outermost node
		Node outermostNode;				//The outermost tag in the form of a Node object
		Element outermostElement;		//The outermost tag in the form of a Element object
		String returnValue;

		outermostNodes = xML.getChildNodes();
		outermostNode = Global.getNode(outermostNodes,"the given XML structure");
		outermostElement = (Element)outermostNode;
		returnValue = "<" + outermostElement.getNodeName() + ">";
		
		return returnValue;
	}
	
	/**
	 * The <code>getEventArguments</code> method returns a string that has arguments for calling the 
	 * <code>FILTER</code> function, <code>IsTemporalRelationWithQuantities()</code> or 
	 * <code>ComputeWhen()</code>. The string will have the arguments that correspond to the given function.
	 *
	 * @param event has the information specifying what the argument values should be.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a string to insert into a IsTemporalRelationWithQuantities() function call.
	 */
	private static String getEventArguments(EventDefinition event, String owner) {
		String set, min, max;					//Arguments of the IsTemporalRelationWithQuantities() function
		String logicalOperator;					//A logical operator (and/or/not)
		EventDefinition event1,event2;			//Arguments of a logical operator
		String eventId;							//The identification code of an event
		String operand1Args,operand2Args;		//Arguments of the operands of a logical operator
		String warning;							//Information to put in a warning message

		logicalOperator = event.getLogicalOperator();
		if (logicalOperator == null) {
			set = "\"SET_NAME\"";								//Initialize
			min = "";											//Initialize
			max = "";											//Initialize
			if (event.isGuaranteedEqual(true)) {
				return set + ",\"set--TRUE\",\"MIN_QUANTITY\",\"1\"";	//A special set that is always true 
			} 
			if (event.getSetId() != null) {
				set += ",\"" + event.getSetId() + "\"";
			} else {
				set += ",\"no-set-found\"";
				eventId = event.getId();
				if (eventId.equals("")) {
					warning = "An";
				} else {
					warning = "The \"" + eventId + "\"";
				}
				warning = 
						"WARNING in XMLToSPARQLTranslator.getEventArguments in " + owner +
						": " + warning + " event was defined incorrectly.";
				System.err.println(warning);	//translateEvent() should have returned null
		    	Global.unableToRespondMessage.add(warning);
			}
			if (event.getMinimum() != null) {
				min = "," + event.getMinimum();
			} else {
				min += ",\"-1\"";
			}
			if (event.getMaximum() == null) {
				max = "";
			} else {
				max = "," + event.getMaximum();
				Global.useSubsetOfViews = true;
			}
			return set + min + max;
		}
		event1 = event.getEvent1();
		if (event1 != null) {
			operand1Args = getEventArguments(event1,owner);	
			if (event.getLogicalOperator().equals("not")) {			//"not" has only one operand
				return "\"LOGICAL_OPERATOR\",\"not\"," + operand1Args;
			}
			event2 = event.getEvent2();
			if (event2 != null) {
				operand2Args = getEventArguments(event2,owner);
				return 
						"\"LOGICAL_OPERATOR\",\"" + event.getLogicalOperator() + "\"," + 
						operand1Args + "," + operand2Args;
			}
		}
		return "";
	}

	/**
	 * The <code>findDefinitionsInSet</code> method finds all of the time period and location definitions that
	 * are referenced in the definition of the given set and saves them to 
	 * <code>Global.timePeriodDefinitionsUsedInLastQuery</code> and 
	 * <code>Global.locationDefinitionsUsedInLastQuery</code>, respectively.
	 *
	 * @param setDefinition is the definition of the set in which to search for time periods and locations.
	 */
	private static void findDefinitionsInSet(SetDefinition setDefinition) {
		HashMap<String,TimePeriodDefinition> timePeriodDefinitions;		//The time period definitions found
		HashMap<String,LocationDefinition> locationDefinitions;			//The location definitions found
		
		timePeriodDefinitions = setDefinition.getTimePeriodDefinitions();
		for (String timePeriodId : timePeriodDefinitions.keySet()) {
			Global.timePeriodDefinitionsUsedInLastQuery.put(
					timePeriodId,
					timePeriodDefinitions.get(timePeriodId));
		}
		locationDefinitions = setDefinition.getLocationDefinitions();
		for (String locationId : locationDefinitions.keySet()) {
			Global.locationDefinitionsUsedInLastQuery.put(
					locationId,
					locationDefinitions.get(locationId));
		}
	}
	
	/**
	 * The <code>toString</code> method returns the SPARQL translation of the query.
	 * 
	 * @return the query in SPARQL format.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sPARQL.toString("","") + "\n";
	}
	
	/**
	 * The <code>main</code> method is where the program begins.
	 *
	 * @param args are ignored.
	 * @throws Exception if there are any problems.
	 */
	public static void main(String[] args) throws Exception {
		RunTests.go();
	}
}