package sparql.translator.location;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.utilities.Global;

/**
 * The <code>CartesianPixelPolygon</code> class is the same as <code>ViewCentricPolygon</code>, except that it
 * doesn't have a "ViewId".
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class CartesianPixelPolygon extends ViewCentricLocation 
implements Iterable<CartesianPixelPoint> {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>points</code> is a list of the points of this polygon's boundary listed in order going around the
	 * polygon. 
	 */
	ArrayList<CartesianPixelPoint> points;

	
	/**
	 * The <code>CartesianPixelPolygon</code> constructor initializes this class's fields.
	 *
	 * @param nameIn is the identifier to be assigned to this location.
	 */
	public CartesianPixelPolygon(String nameIn) {
		super(nameIn);
		points = new ArrayList<CartesianPixelPoint>();
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
		CartesianPixelPoint vertex;		//One of the vertices of this polygon
		String vertexId;				//That vertex's identifier
		String warning;					//A message to let the user know there might be a problem
		Integer count;					//For counting iterations of a loop

		for (count = 0; count < arguments.getLength(); count++) {
			argument = arguments.item(count);
			if (argument.getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
				argumentName = argument.getNodeName(); 
				if (argumentName.equals("CartesianPixelPoint")) {
					vertexId = ((Element)argument).getAttribute("id");
					vertex = new CartesianPixelPoint(vertexId);
					vertex.setArguments(argument.getChildNodes(),"a <CartesianPixelPoint> in "+owner);
					addPoint(vertex);
				} else if (argumentName.equals("ViewCentricPoint")) {
					vertexId = ((Element)argument).getAttribute("id");
					vertex = new ViewCentricPoint(vertexId);
					vertex.setArguments(argument.getChildNodes(),"a <ViewCentricPoint> in "+owner);
					addPoint(vertex);
				} else {
					if (id.equals("")) {
						warning = "a <CartesianPixelPolygon> in ";
					} else {
						warning = "the location, \"" + id + "\", in ";
					}
					warning = 
							"WARNING in CartesianPixelPolygon.setArguments in " + owner + 
							": Found an unexpected tag, <" + argumentName + 
							">, in " + warning + "<Locations>.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
			}
		}
	}
	
	/**
	 * The <code>addPoint</code> method adds a new point to the end of the list of this polygon's points.
	 *
	 * @param point is a point on the surface of the earth that is represented by its x-coordinate and its
	 * y-coordinate.
	 */
	public void addPoint(CartesianPixelPoint point) {
		points.add(point);
	}
	
	/**
	 * The <code>iterator</code> method returns an iterator that can loop through the points on this polygon's
	 * boundary.
	 *
	 * @return the iterator for the <code>points</code> list.
     * @see java.lang.Iterable#iterator()
	 */
    @Override
	public Iterator<CartesianPixelPoint> iterator() {        
        return points.iterator();
    }

    /**
	 * The <code>toString</code> method returns the SPARQL version of this location.
	 *
	 * @return the coordinates of this polygon.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String semicolon;			//Specifies whether a semicolon should be added
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();		//Initialize
		semicolon = "";							//Initialize
		for (CartesianPixelPoint point : points) {
			returnValue.append(semicolon + point.toString());
			semicolon = ";";
		}
		return returnValue.toString();
	}
}