package sparql.translator.location;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sparql.translator.utilities.Global;

/**
 * The <code>ViewCentricPolygon</code> class is a 2-dimensional region, as represented in the view of an 
 * observer (such as a camera).
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class ViewCentricPolygon extends ViewCentricLocation implements Iterable<CartesianPixelPoint> {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>points</code> is a list of the points of this polygon's boundary listed in order going around the
	 * polygon. 
	 */
	ArrayList<CartesianPixelPoint> points;

	
	/**
	 * The <code>ViewCentricPolygon</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 * @param viewIdIn is the name of the view (such as a camera's view) with the coordinate system that 
	 * corresponds to the pixel coordinates of this location.
	 */
	public ViewCentricPolygon(String idIn, String viewIdIn) {
		super(idIn,viewIdIn);
		points = new ArrayList<CartesianPixelPoint>();
	}

	/**
	 * The <code>ViewCentricPolygon</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 * corresponds to the pixel coordinates of this location.
	 */
	public ViewCentricPolygon(String idIn) {
		this(idIn,null);
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
					if ( ! vertexId.equals("")) {
						System.err.println(
								"WARNING in ViewCentricPolygon.setArguments in vertex \"" + vertexId + 				
								"\" in " + owner + ": This system cannot handle named vertices like \"" + 
								vertexId + "\" if they are referenced elsewhere.");
					}
					vertex = new CartesianPixelPoint(vertexId);
					vertex.setArguments(argument.getChildNodes(),"a <CartesianPixelPoint> in "+owner);
					addPoint(vertex);
				} else if (argumentName.equals("ViewCentricPoint")) {
					vertexId = ((Element)argument).getAttribute("id");
					if ( ! vertexId.equals("")) {
						System.err.println(
								"WARNING in ViewCentricPolygon.setArguments in vertex \"" + vertexId + 				
								"\" in " + owner + ": This system cannot handle named vertices like \"" + 
								vertexId + "\" if they are referenced elsewhere.");
					}
					vertex = new ViewCentricPoint(vertexId);
					vertex.setArguments(argument.getChildNodes(),"a <ViewCentricPoint> in "+owner);
					addPoint(vertex);
				} else if (argumentName.equals("ViewId")) {
					setViewId(argument.getTextContent().trim(),"<ViewId> in "+owner);
				} else {
					if (id.equals("")) {
						warning = "a <ViewCentricPolygon> in ";
					} else {
						warning = "the location, \"" + id + "\", in ";
					}
					warning = 
							"WARNING in ViewCentricPolygon.setArguments in " + owner + 
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
	 * @param point is a point, as represented in the view of an observer (such as a camera).
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
		returnValue.append(semicolon + getViewId());
		return returnValue.toString();
	}
}