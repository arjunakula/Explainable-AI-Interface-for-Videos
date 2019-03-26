package sparql.translator.location;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.utilities.Global;

/**
 * The <code>CartesianMetricPoint</code> class is a point on the surface of the earth with two 
 * coordinates that represent distances from an origin point in meters.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class CartesianMetricPoint extends SceneCentricOutdoorLocation {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * <code>x</code> is this point's horizontal coordinate. 
	 */
	Double x;

	/**
	 * <code>y</code> is this point's vertical coordinate. 
	 */
	Double y;

	/**
	 * <code>z</code> is this point's vertical coordinate. 
	 */
	Double z;

	
	/**
	 * The <code>CartesianMetricPoint</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this time period.
	 * @param xIn is the north-south position of this point.
	 * @param yIn is the west-east position of this point.
	 * @param zIn is the up-down position of this point.
	 */
	public CartesianMetricPoint(String idIn, Double xIn, Double yIn, Double zIn) {
		super(idIn);
		x = xIn;
		y = yIn;
		z = zIn;
	}

	/**
	 * The <code>CartesianMetricPoint</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this time period.
	 */
	public CartesianMetricPoint(String idIn) {
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
				if (argumentName.equals("x")) {
					setX(Double.valueOf(argument.getTextContent().trim()));
				} else if (argumentName.equals("y")) {
					setY(Double.valueOf(argument.getTextContent().trim()));
				} else if (argumentName.equals("z")) {
					setZ(Double.valueOf(argument.getTextContent().trim()));
				} else {
					warning = 
							"WARNING in CartesianMetricPoint.setArguments in " + owner + 
							": Found an unexpected tag, <" + argumentName + 
							">, in a <CartesianMetricPoint>.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
			}
		}
	}

	/**
	 * The <code>setX</code> setter changes the value of the global variable,
	 * <code>x</code>, a <b><code>Double</code></b>.
	 *
	 * @param xIn is the new value that should be assigned to <code>x</code>.
	 */
	public void setX(Double xIn) {
		x = xIn;
	}

	/**
	 * The <code>setY</code> setter changes the value of the global variable,
	 * <code>y</code>, a <b><code>Double</code></b>.
	 *
	 * @param yIn is the new value that should be assigned to <code>y</code>.
	 */
	public void setY(Double yIn) {
		y = yIn;
	}

	/**
	 * The <code>setZ</code> setter changes the value of the global variable,
	 * <code>z</code>, a <b><code>Double</code></b>.
	 *
	 * @param zIn is the new value that should be assigned to <code>z</code>.
	 */
	public void setZ(Double zIn) {
		z = zIn;
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this location.
	 *
	 * @return the coordinates of this point.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (z == null) {
			return x + "," + y;
		}
		return x + "," + y + "," + z;
	}
}