package sparql.translator.definitions;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.location.CartesianMetricPoint;
import sparql.translator.location.CartesianMetricPolygon;
import sparql.translator.location.CartesianPixelPoint;
import sparql.translator.location.CartesianPixelPolygon;
import sparql.translator.location.GeodeticPoint;
import sparql.translator.location.GeodeticPolygon;
import sparql.translator.location.Location;
import sparql.translator.location.ViewCentricPoint;
import sparql.translator.location.ViewCentricPolygon;
import sparql.translator.utilities.ArgumentType;
import sparql.translator.utilities.Global;

/**
 * The <code>LocationDefinition</code> class holds important information about a location definition in an XML
 * query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class LocationDefinition extends Definition {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>location</code>  is an object that holds all of the information about the location that is 
	 * defined in this class. 
	 */
	private Location location;
	
	/** <code>definitionStringInSPARQL</code> is this definition in the SPARQL query. */
	private StringBuffer definitionStringInSPARQL;

	
	/**
	 * The <code>LocationDefinition</code> constructor initializes the global variables.
	 *
	 * @param locationNode is the XML definition of the location.
	 * variable name without the number at the end) points to the number of variables with that prefix.
	 */
	public LocationDefinition(Node locationNode) {
		super(locationNode);
		location = null;
		definitionStringInSPARQL = new StringBuffer();
	}

	/**
	 * The <code>translateLocation</code> method translates this location from XML to SPARQL. As input, 
	 * it uses the value of the global variable <code>definitionInXML</code>, and it saves the result in the 
	 * global variable <code>definitionInSPARQL</code>.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void translateLocation(String owner) {
		String locationType;			//Specifies the format of the location
		String locationTypeSPARQL;		//That format translated into its name in a SPARQL filter function
		String warning;					//A message to let the user know there might be a problem
		NodeList locationArguments;		//Information about this location

		warning = "";
		locationType = definitionInXML.getNodeName();
		if (Global.LOCATION_TYPES.keySet().contains(locationType)) {	//Is it legitimate?
			locationTypeSPARQL = Global.LOCATION_TYPES.get(locationType);
			definitionStringInSPARQL.append("\"" + locationTypeSPARQL + "\",");
			Global.changedDefinition(this);
			locationArguments = definitionInXML.getChildNodes();
			if (id.equals("")) {
				warning = "a <" + locationType + "> in ";
			} else {
				warning = "the location, \"" + id + "\", in ";
			}
			if (locationType.equals("ViewCentricPoint")) {
				location = new ViewCentricPoint(id);
				location.setLocationType(ArgumentType.VIEW_CENTRIC_LOCATION);
				location.setLocationShape(ArgumentType.POINT);
				((ViewCentricPoint)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("ViewCentricPolygon")) {
				location = new ViewCentricPolygon(id);
				location.setLocationType(ArgumentType.VIEW_CENTRIC_LOCATION);
				location.setLocationShape(ArgumentType.POLYGON);
				((ViewCentricPolygon)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("CartesianMetricPoint")) {
				location = new CartesianMetricPoint(id);
				location.setLocationType(ArgumentType.SCENE_CENTRIC_INDOOR_LOCATION);
				location.setLocationShape(ArgumentType.POINT);
				((CartesianMetricPoint)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("CartesianMetricPolygon")) {
				location = new CartesianMetricPolygon(id);
				location.setLocationType(ArgumentType.SCENE_CENTRIC_INDOOR_LOCATION);
				location.setLocationShape(ArgumentType.POLYGON);
				((CartesianMetricPolygon)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("CartesianPixelPoint")) {
				location = new CartesianPixelPoint(id);
				location.setLocationType(ArgumentType.VIEW_CENTRIC_LOCATION);
				location.setLocationShape(ArgumentType.POINT);
				((CartesianPixelPoint)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("CartesianPixelPolygon")) {
				location = new CartesianPixelPolygon(id);
				location.setLocationType(ArgumentType.VIEW_CENTRIC_LOCATION);
				location.setLocationShape(ArgumentType.POLYGON);
				((CartesianPixelPolygon)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("GeodeticPoint")) {
				location = new GeodeticPoint(id);
				location.setLocationType(ArgumentType.SCENE_CENTRIC_OUTDOOR_LOCATION);
				location.setLocationShape(ArgumentType.POINT);
				((GeodeticPoint)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("GeodeticPolygon")) {
				location = new GeodeticPolygon(id);
				location.setLocationType(ArgumentType.SCENE_CENTRIC_OUTDOOR_LOCATION);
				location.setLocationShape(ArgumentType.POLYGON);
				((GeodeticPolygon)location).setArguments(
						locationArguments,warning+owner);
			} else if (locationType.equals("Volume")) {
				warning = 
						"WARNING in LocationDefinition.translateLocation in " + owner + 
						": I don't know how to translate <Volume>.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				//FIXME Volume
				return;
			} else {
				warning = 
						"WARNING in LocationDefinition.translateLocation in the location, \"" + id +
						"\", in " + owner + ": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return;
			}
			definitionStringInSPARQL.append("\"");
			definitionStringInSPARQL.append(location.toString());
			definitionStringInSPARQL.append("\"");
		} else {
			warning = 
					"WARNING in LocationDefinition.translateLocation in " + owner + 
					": Found an unexpected location type, <" + locationType + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}

	/**
	 * The <code>getLocation</code> getter returns the value of the global variable,
	 * <code>location</code>, a <b><code>Location</code></b>.
	 *
	 * @return the value of <code>location</code>.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * The <code>getDefinitionInSPARQL</code> getter returns the value of the global variable,
	 * <code>definitionInSPARQL</code> as a <b><code>String</code></b>.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 */
	public String getDefinitionInSPARQL() {
		return definitionInSPARQL.toString();
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this definition.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return definitionStringInSPARQL.toString();
	}
}