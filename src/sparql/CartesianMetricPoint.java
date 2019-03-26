package sparql;

public class CartesianMetricPoint {

	public double x = 0; 
	public double y = 0;

	public CartesianMetricPoint() {
		
	}
	public CartesianMetricPoint(String locationStr) throws MseeException {
		/*
		 * reference:
	IsAtLocation("VIEW_CENTRIC_POINT","0,0;?view1","OBJECT_ID",?object1)
	IsAtLocation("VIEW_CENTRIC_POLYGON","0,0;100,0;100,100;0,100;?view1","OBJECT_ID",?object1)
	IsAtLocation(“CARTESIAN_METRIC_POINT","0,0,0","OBJECT_ID",?object1)
	IsAtLocation(“CARTESIAN_METRIC_POLYGON","0,0,0;5.25,0,0;5.25,4.32,10;0,4.32,10","OBJECT_ID",?object1)
	IsAtLocation(“GEODETIC_POINT","39.778661,-84.079399,0","OBJECT_ID",?object1)
	IsAtLocation(“GEODETIC_POLYGON","39.778661,-84.079399,0;39.778958,-84.085214,0;39.784647,-84.085064,10","OBJECT_ID",?object1)
		 */
		
		String [] str_arr =  locationStr.split(",");					
		if (str_arr.length == 2 || str_arr.length == 3)
		{	x = Double.parseDouble(str_arr[0]);
			y = Double.parseDouble(str_arr[1]);
			
			if ( str_arr.length == 3 && Double.parseDouble(str_arr[2]) != 0 ) {
				System.err.println( "WARN Ignoring the third coordinate in the location " + locationStr );
			}
					
		} else
		{	throw new MseeException("ERROR CartesianMetricPoint unable to parse string " + locationStr);
		}
	}

	public CartesianMetricPoint(double fx, double fy) {
		this.x = fx;
		this.y = fy;
	}
	
	public CartesianMetricPoint(CartesianMetricPoint pt) {	
		this.x = pt.x;
		this.y = pt.y;
	}


	public SimpleBoundingBox GetEnclosingBox() {
		return new SimpleBoundingBox(x, y);		
	}
	public void Set(double fx, double fy) {		
		this.x = fx;
		this.y = fy; 
		
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
