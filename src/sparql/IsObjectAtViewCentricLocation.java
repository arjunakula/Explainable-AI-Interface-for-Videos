package sparql;

//import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;



/**
 * The <code>IsObjectInViewCentricRegion</code> class has a function that determines whether or not an object, 
 * represented by a polygon, is completely covered by a region, also represented as a polygon. The coordinates
 * of the polygon are pixels from a given view.
 *
 * @author Ken Samuel
 * @version 1.0, Aug 19, 2013
 * @since 1.6
 */
public class IsObjectAtViewCentricLocation extends FunctionBase {

	/** 
	 * <code>TIMES_AND_LOCATIONS_FILE</code> is the name of the file that has the location of each object at 
	 * each time. Its format is expected to be XML. 
	 */
	private static final String TIMES_AND_LOCATIONS_FILE = 
	"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\EvaluationFramework\\___\\scene-centric.xml";

	
	/**
	 * The <code>IsObjectInViewCentricRegion</code> constructor simply runs the constructor of its superclass.
	 */
	public IsObjectAtViewCentricLocation() {
		super();
	}
	
	/**
	 * The <code>exec</code> method determines whether or not an object is completely
	 * enclosed in a region. If any part of the object is the same as any point on the boundary of the
	 * region, then the object is NOT completely enclosed in the region.
	 *
	 * @param input is a list that has all of the input information. The first thing in the list is the name
	 * of the object (such as "obj-person1"). The second thing is the name of the view (such as "view_video2")
	 * The rest of the things in the list are the coordinates of the points of the region.
	 * Each point is given as an x-coordinate followed by a y-coordinate, based on the given view. The points 
	 * are listed in the order of a traversal around the borders of a polygon, stopping just before returning 
	 * to  the first point. (In other words, each point is only listed once.) 
	 * 
	 * @return <b><code>true</code></b> if the object is completely enclosed in the given region, 
	 * and return <code><b>false</b></code> otherwise.
	 * @throws IllegalArgumentException if this method cannot interpret the input.
	 */
	@Override
	public NodeValue exec(List<NodeValue> input) throws IllegalArgumentException {
		String objectName;							//The name of the object
		String viewName;							//The name of the view
		ArrayList<ViewCentricPoint> region;			//The points of the region
		ArrayList<ViewCentricPoint> object;			//The points of the object
		Double x,y;									//The coordinates of a view-centric point
		Integer index;								//For iterating through the input list
		Boolean returnValue;
		
		if ((input.size() % 2) != 0) {				//There should be an even number of elements in the list
			throw new IllegalArgumentException(
					"ERROR in IsObjectInViewCentricRegion.exec: The input must consist of an object name, " +
					"a view name, and the x- and y-coordinates of each point of the region, in order of " +
					"a traversal around the region. But instead, the input is: \n" + input);
		}

		/* Initialization of variables */
		objectName = input.get(0).getString();
		viewName = input.get(1).getString();
		region = new ArrayList<ViewCentricPoint>((input.size() - 2)/2);
		returnValue = null;

		index = 2;		//Run through the coordinates of points in the input
		while (index < input.size()) {
			x = input.get(index).getDouble();
			index++;
			y = input.get(index).getDouble();
			index++;
			region.add(new ViewCentricPoint(x,y));
		}
		if (region.size() < 3) {
			throw new IllegalArgumentException(
					"ERROR in IsObjectInViewCentricRegion.exec: The region must have at least three points.");
		}

		//FIXME: Open the TIMES_AND_LOCATIONS_FILE file
		//FIXME: Read lines from the file until objectName is found
		while (returnValue == null) {
			object = null; //FIXME: Read one line of coordinates from the file and store it in object
			if (object == null) {					//No more coordinates
				returnValue = false;
			} else if (isPolygonInPolygon(object,region)) {
				returnValue = true;
			}
		}
		//FIXME: Close the file
		
		return NodeValue.makeBoolean(returnValue);
	}
	
	/**
	 * The <code>isPolygonInPolygon</code> method determines whether or not a polygon is completely enclosed 
	 * in another polygon. Each polygon is represented by a list of points listed in an ordering
	 * that traverses along the edges of the polygon. Each point of a polygon is included exactly once in 
	 * the list, and it is assumed that there is an edge connecting the last point to the first point.
	 *
	 * @param innerPolygon is a list of the points of the first polygon, which is the polygon that is being 
	 * tested to see if it is enclosed by the other polygon.
	 * @param outerPolygon is a list of the points of the second polygon, which is the polygon that is being 
	 * tested to see if it encloses the other polygon.
	 * @return <code><b>true</b></code> if <code>innerPolygon</code> is completely enclosed in 
	 * <code>outerPolygon</code>, and return <code><b>false</b></code> otherwise.
	 */
	public Boolean isPolygonInPolygon(
			ArrayList<ViewCentricPoint> innerPolygon, ArrayList<ViewCentricPoint> outerPolygon) {
		if (
				(doesInnerExtendBeyondOuter(innerPolygon,outerPolygon)) ||	//First test
				(doAnyEdgesIntersect(innerPolygon,outerPolygon))) {		//Second test
			return false;
		}
		return true;				//The polygons passed both tests, so return "true"
	}
	
	/**
	 * The <code>doesInnerExtendBeyondOuter</code> method determines whether any of the points of the first
	 * given polygon lies farther left, right, above, or below all of the points of the second given polygon.
	 *
	 * @param innerPolygon is a list of the points of the first polygon, which is the polygon that is being 
	 * tested to see if it is enclosed in the other polygon.
	 * @param outerPolygon is a list of the points of the second polygon, which is the polygon that is being 
	 * tested to see if it encloses the other polygon.
	 * @return <code><b>true</b></code> if any of <code>innerPolygon</code>'s points lies beyond all of 
	 * <code>outerPolygon</code>'s points along the x-axis or the y-axis, and return <code><b>false</b></code> 
	 * otherwise.
	 */
	private static Boolean doesInnerExtendBeyondOuter(
			ArrayList<ViewCentricPoint> innerPolygon, ArrayList<ViewCentricPoint> outerPolygon) {
		ArrayList<ArrayList<ViewCentricPoint>> polygons;	//The two polygons
													//The first dimension of the list specifies which polygon
													//The second dimension holds all of the polygon's points
		ArrayList<ViewCentricPoint> polygon;		//One of the polygons
		ArrayList<Double> minX, minY, maxX, maxY;	//The minimum/maximum x/y-coordinates of each polygon
		ViewCentricPoint point;						//A point of a polygon
		Double x,y;									//Coordinates of a point
		Integer polygonNum;							//Specifies which polygon is being worked on
		Integer pointIndex;							//For iterating through a polygon's points

		/* Initialization of variables */
		polygons = new ArrayList<ArrayList<ViewCentricPoint>>(2);//There are two polygons
		polygons.add(innerPolygon);								//The points of the first polygon
		polygons.add(outerPolygon);								//The points of the second polygon
		minX = new ArrayList<Double>(2);						//There are two polygons
		minX.add(Double.MAX_VALUE);								//Initialize the min x of the first polygon
		minX.add(Double.MAX_VALUE);								//Initialize the min x of the second polygon
		minY = new ArrayList<Double>(2);						//There are two polygons
		minY.add(Double.MAX_VALUE);								//Initialize the min y of the first polygon
		minY.add(Double.MAX_VALUE);								//Initialize the min y of the second polygon
		maxX = new ArrayList<Double>(2);						//There are two polygons
		maxX.add(Double.MIN_VALUE);								//Initialize the max x of the first polygon
		maxX.add(Double.MIN_VALUE);								//Initialize the max x of the second polygon
		maxY = new ArrayList<Double>(2);						//There are two polygons
		maxY.add(Double.MIN_VALUE);								//Initialize the max y of the first polygon
		maxY.add(Double.MIN_VALUE);								//Initialize the max y of the second polygon

		/* Iterate through the points of each polygon */
		for (polygonNum = 0; polygonNum < 2; polygonNum++) {	//For each polygon
			polygon = polygons.get(polygonNum);
			for (pointIndex = 0; pointIndex < polygon.size(); pointIndex++) {//For each point
				point = polygon.get(pointIndex);
				x = point.x();
				y = point.y();
				if (x < minX.get(polygonNum)) {				//A new minimum
					minX.set(polygonNum,x);
				}
				if (y < minY.get(polygonNum)) {				//A new minimum
					minY.set(polygonNum,y);
				}
				if (x > maxX.get(polygonNum)) {				//A new maximum
					maxX.set(polygonNum,x);
				}
				if (y > maxY.get(polygonNum)) {				//A new maximum
					maxY.set(polygonNum,y);
				}
			}
		}
		
		/* Test each end of each dimension */
		if (
				(minX.get(0) < minX.get(1)) ||
				(minY.get(0) < minY.get(1)) ||
				(maxX.get(0) > maxX.get(1)) ||
				(maxY.get(0) > maxY.get(1))) {
			return true;						//Return "true" if any of the basic tests are true
		}
		return false;
	}
	
	/**
	 * The <code>doAnyEdgesIntersect</code> method determines whether any of the edges of one polygon 
	 * intersect with any of the edges of another polygon.
	 *
	 * @param polygon1 is one of the polygons.
	 * @param polygon2 is the other polygon.
	 * @return <b><code>true</code></b> if an edge of <code>polygon1</code> intersects with an edge of 
	 * <code>polygon2</code>, and return <b><code>false</code></b> otherwise.
	 */
	private static Boolean doAnyEdgesIntersect(
			ArrayList<ViewCentricPoint> polygon1, ArrayList<ViewCentricPoint> polygon2) {
		ArrayList<ViewCentricPoint> polygon1edge;		//An edge of polygon1, defined by two endpoints
		ArrayList<ViewCentricPoint> polygon2edge;		//An edge of polygon2, defined by two endpoints
		Integer polygon1pointIndex, polygon2pointIndex;	//For iterating through each polygon's points
		
		
		/* Initialization of variables */
		polygon1edge = new ArrayList<ViewCentricPoint>(2);		//Each of polygon1's edges has two points		
		polygon2edge = new ArrayList<ViewCentricPoint>(2);		//Each of polygon2's edges has two points

		polygon1edge.add(polygon1.get(0));						//Start with polygon1's first point
		polygon1edge.add(null);
		polygon1pointIndex = 1;									//Next is polygon1's second point
		
		/* Loop through the edges of polygon1 */
		while (polygon1pointIndex < polygon1.size()) {	//Does polygon1 have any more points?
			polygon1edge.set(1,polygon1.get(polygon1pointIndex));		//Copy the next point to the edge
			
			/* Initialization of variables */
			polygon2edge.add(polygon2.get(0));					//Start with polygon2's first point
			polygon2edge.add(null);
			polygon2pointIndex = 1;									//Next is polygon2's second point

			/* Loop through the edges of polygon2 */
			while (polygon2pointIndex < polygon2.size()) {	//Does polygon2 have any more points?
				polygon2edge.set(1,polygon2.get(polygon2pointIndex));		//Copy the next point to the edge
				if (doIntersect(polygon1edge, polygon2edge)) {			//If the two edges intersect
					return false;										//Then return "false"
				}
				polygon2edge.set(0,polygon2edge.get(1));	//Last edge's last point is next edge's 1st point
				polygon2pointIndex++;						//Go to the next point in polygon2
			}
			polygon1edge.set(0,polygon1edge.get(1));	//Last edge's last point is next edge's 1st point
			polygon1pointIndex++;						//Go to the next point in polygon1
		}
		return true;				//No intersections were found
	}
	
	/**
	 * The <code>doIntersect</code> method decides whether two segments intersect.
	 *
	 * @param segment1 is one of the segments to test, specified by its two endpoints.
	 * @param segment2 is the other segment, also specified by its two endpoints.
	 * @return <b><code>true</code></b> if the given edges intersect, and return <b><code>false</code></b> 
	 * otherwise.
	 */
	private static Boolean doIntersect(
			ArrayList<ViewCentricPoint> segment1, ArrayList<ViewCentricPoint> segment2) {
		Boolean returnValue;
		
		returnValue = false;    	//FIXME
		
		return returnValue;
	}
	
	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.function.FunctionBase#checkBuild(
	 * java.lang.String, com.hp.hpl.jena.sparql.expr.ExprList)
	 */
	@Override
	public void checkBuild(String uri, ExprList args)  {
		//FIXME: What is this method supposed to do?
	}

	/**
	 * The <code>main</code> method is used for testing.
	 *
	 * @param args are ignored.
	 */
	public static void main(String[] args) {
		IsObjectAtViewCentricLocation me;
		List<NodeValue> input;
		
		me = new IsObjectAtViewCentricLocation();
		input = new ArrayList<NodeValue>();		
		input = Arrays.asList(new NodeValue[] { //query-CY03
				NodeValue.makeString("dog1"),
				NodeValue.makeString("view-soc1_video2"),
				NodeValue.makeDouble(0),
				NodeValue.makeDouble(240),
				NodeValue.makeDouble(320),
				NodeValue.makeDouble(240),
				NodeValue.makeDouble(320),
				NodeValue.makeDouble(479),
				NodeValue.makeDouble(0),
				NodeValue.makeDouble(479)
		});
		System.out.println(me.exec(input));
	}
}
