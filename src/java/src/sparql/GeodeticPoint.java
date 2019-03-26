package sparql;

public class GeodeticPoint {

	

	public double latitude = 0;
	public double longitude = 0; 
	double elevation = 0;

	

	public GeodeticPoint() {
	}
	
	
	public GeodeticPoint(double lat, double lon) {
		// 
		latitude = lat;
		longitude = lon;
		elevation = 0; 
	}
	
	public GeodeticPoint(double lat, double lon, double ele) {
		latitude = lat;
		longitude = lon;
		elevation = ele; 
	}

	public GeodeticPoint(String locationStr) throws MseeException {

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
		if (str_arr.length == 2 )
		{	latitude = Double.parseDouble(str_arr[0]);
			longitude = Double.parseDouble(str_arr[1]);
			elevation = 0; 
			
		} else if (str_arr.length == 3 )
		{	latitude = Double.parseDouble(str_arr[0]);
			longitude = Double.parseDouble(str_arr[1]);
			elevation = Double.parseDouble(str_arr[2]);
		} else
		{	throw new MseeException("ERROR GeodeticPoint unable to parse string " + locationStr);
		}
	}


	public SimpleBoundingBox GetEnclosingBox() {
		// TODO Auto-generated method stub
		return new SimpleBoundingBox(latitude, longitude);
		
	}

	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getElevation() {
		return elevation;
	}
		
}
