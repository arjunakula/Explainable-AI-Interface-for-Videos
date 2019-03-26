package sparql;

import java.util.ArrayList;

public class GeodeticPolygon {

	ArrayList< GeodeticPoint > points = new ArrayList<GeodeticPoint>();  
	
	public GeodeticPolygon() {
		
	}
	
	public GeodeticPolygon(String locationStr) throws MseeException {

		/*
		 * reference:
	IsAtLocation("VIEW_CENTRIC_POINT","0,0;?view1","OBJECT_ID",?object1)
	IsAtLocation("VIEW_CENTRIC_POLYGON","0,0;100,0;100,100;0,100;?view1","OBJECT_ID",?object1)
	IsAtLocation(“CARTESIAN_METRIC_POINT","0,0,0","OBJECT_ID",?object1)
	IsAtLocation(“CARTESIAN_METRIC_POLYGON","0,0,0;5.25,0,0;5.25,4.32,10;0,4.32,10","OBJECT_ID",?object1)
	IsAtLocation(“GEODETIC_POINT","39.778661,-84.079399,0","OBJECT_ID",?object1)
	IsAtLocation(“GEODETIC_POLYGON","39.778661,-84.079399,0;39.778958,-84.085214,0;39.784647,-84.085064,10","OBJECT_ID",?object1)
		 */		
		String [] str_arr =  locationStr.split(";");
		for (String str : str_arr)
		{
			points.add( new GeodeticPoint(str));
		}
			
	}



	public SimpleBoundingBox GetEnclosingBox() {
		// TODO Auto-generated method stub
		
		if (points.isEmpty())
			return null;
		
		SimpleBoundingBox bbox = new SimpleBoundingBox(points.get(0).latitude, points.get(0).longitude);
		
		for (GeodeticPoint pt:points)
		{
			bbox.Union(pt.latitude, pt.longitude);
		}
		
		return bbox;		
	}
	
	public ArrayList<GeodeticPoint> getPoints() {
		return points;
	}

	public void AddPoint(GeodeticPoint pt) {
		points.add( pt );
	}
}
