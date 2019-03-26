package sparql.translator.location;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.utilities.Global;

/**
 * The <code>GeodeticPoint</code> class is a point on the surface of the earth that is represented by its 
 * latitude and longitude.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class GeodeticPoint extends SceneCentricOutdoorLocation {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * <code>latitude</code> is a geographic coordinate that specifies the north-south position of this point 
	 * on the Earth's surface.
	 */
	Double latitude;

	/**
	 * <code>longitude</code> is a geographic coordinate that specifies the west-east position of this point 
	 * on the Earth's surface.
	 */
	Double longitude;

	/**
	 * <code>elevation</code> is a geographic coordinate that specifies how high this point is relative to
	 * sea level. The elevation may not be specified.
	 */
	Double elevation;

	
	/**
	 * The <code>GeodeticPoint</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 * @param latitudeIn is the north-south position of this point.
	 * @param longitudeIn is the west-east position of this point.
	 * @param elevationIn is the up-down position of this point.
	 */
	public GeodeticPoint(String idIn, Double latitudeIn, Double longitudeIn, Double elevationIn) {
		super(idIn);
		latitude = latitudeIn;
		longitude = longitudeIn;
		elevation = elevationIn;
	}
	
	/**
	 * The <code>GeodeticPoint</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 */
	public GeodeticPoint(String idIn) {
		this(idIn,null,null,null);
	}

	/**
	 * The <code>setArguments</code> method parses a list of arguments and stores them in the fields of this
	 * object.
	 *
	 * @param arguments is a list of arguments in XML.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void setArguments(NodeList arguments, String owner) {
		Node argument;					//One of the arguments
		String argumentName;			//The name of that argument
		Integer count;					//For counting iterations of a loop
		String warning;					//A message to let the user know there might be a problem

		for (count = 0; count < arguments.getLength(); count++) {
			argument = arguments.item(count);
			if (argument.getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
				argumentName = argument.getNodeName(); 
				if (argumentName.equals("latitude")) {
					setLatitude(Double.valueOf(argument.getTextContent().trim()));
				} else if (argumentName.equals("longitude")) {
					setLongitude(Double.valueOf(argument.getTextContent().trim()));
				} else if (argumentName.equals("elevation")) {
					setElevation(Double.valueOf(argument.getTextContent().trim()));
				} else {
					warning = 
							"WARNING in GeodeticPoint.setArguments in " + owner + 
							": Found an unexpected tag, <" + argumentName + 
							">, in a <GeodeticPoint>.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
			}
		}
	}

	/**
	 * The <code>setLatitude</code> setter changes the value of the global variable,
	 * <code>latitude</code>, a <b><code>Double</code></b>.
	 *
	 * @param latitudeIn is the new value that should be assigned to <code>latitude</code>.
	 */
	public void setLatitude(Double latitudeIn) {
		latitude = latitudeIn;
	}

	/**
	 * The <code>setLongitude</code> setter changes the value of the global variable,
	 * <code>longitude</code>, a <b><code>Double</code></b>.
	 *
	 * @param longitudeIn is the new value that should be assigned to <code>longitude</code>.
	 */
	public void setLongitude(Double longitudeIn) {
		longitude = longitudeIn;
	}

	/**
	 * The <code>setElevation</code> setter changes the value of the global variable,
	 * <code>elevation</code>, a <b><code>Double</code></b>.
	 *
	 * @param elevationIn is the new value that should be assigned to <code>elevation</code>.
	 */
	public void setElevation(Double elevationIn) {
		elevation = elevationIn;
	}

	/**
	 * The <code>getLongitude</code> getter returns the value of the global variable,
	 * <code>longitude</code>, a <b><code>Double</code></b>.
	 *
	 * @return the value of <code>longitude</code>.
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this location.
	 *
	 * @return the coordinates of this point.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (elevation == null) {
			return latitude + "," + longitude;
		}
		return latitude + "," + longitude + "," + elevation;
	}
}