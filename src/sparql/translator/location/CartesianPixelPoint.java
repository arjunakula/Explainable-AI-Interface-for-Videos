package sparql.translator.location;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.utilities.Global;

/**
 * The <code>CartesianPixelPoint</code> class is a representation of a point on a graph.
 *
 * @author Ken Samuel
 * @version 1.0, Aug 19, 2013
 * @since 1.6
 */
public class CartesianPixelPoint extends ViewCentricLocation {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;
	
	/** <code>x</code> is the horizontal coordinate of this point. */
	private Double x;
	
	/** <code>y</code> is the vertical coordinate of this point. */
	private Double y;
	
	
	/**
	 * The <code>CartesianPixelPoint</code> constructor sets the coordinates of this point.
	 *
	 * @param idIn is the identifier to be assigned to this time period.
	 * @param xIn is the x-coordinate of this point.
	 * @param yIn is the y-coordinate of this point.
	 */
	public CartesianPixelPoint(String idIn, Double xIn, Double yIn) {
		super(idIn);
		x = xIn;
		y = yIn;
	}

	/**
	 * The <code>CartesianPixelPoint</code> constructor sets the coordinates of this point.
	 *
	 * @param idIn is the identifier to be assigned to this time period.
	 */
	public CartesianPixelPoint(String idIn) {
		this(idIn,null,null);
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
		String warning;							//A message to let the user know there might be a problem

		for (count = 0; count < arguments.getLength(); count++) {
			argument = arguments.item(count);
			if (argument.getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
				argumentName = argument.getNodeName(); 
				if (argumentName.equals("x")) {
					setX(Double.valueOf(argument.getTextContent().trim()));
				} else if (argumentName.equals("y")) {
					setY(Double.valueOf(argument.getTextContent().trim()));
				} else {
					warning = 
							"WARNING in CartesianPixelPoint.setArguments in " + owner + 
							": Found an unexpected tag, <" + argumentName + 
							">, in a <CartesianPixelPoint>.";
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
	 * The <code>toString</code> method returns the SPARQL version of this location.
	 *
	 * @return the coordinates of this point.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return x + "," + y;
	}
}