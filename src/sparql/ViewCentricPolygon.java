package sparql;

import java.util.ArrayList;

public class ViewCentricPolygon {

	private String view_id = ""; // NOTE: If this default value is changed, then the method isTheViewCorrect in MseeDataset must be modified.
	ArrayList< ViewCentricPoint > points = new ArrayList<ViewCentricPoint>();
	
	public ViewCentricPolygon(String locationStr) throws MseeException {

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
		String [] str_arr2 =  locationStr.split(",");
		
		// for (String str : str_arr)
		
		if (( str_arr.length ==1) && (str_arr2.length>1))
		{
	// this might be for parsing pt.xml in this format  position="1121,456,1349,456,1349,876,1121,876" 
			
			for (int i = 0; i < str_arr2.length-1; i+=2)	
			{
				String str1 = str_arr2[i];
				String str2 = str_arr2[i+1];
				points.add( new ViewCentricPoint(Double.parseDouble(str1),Double.parseDouble(str2)));				
			}			
		}
		else
		{
			for (int i = 0; i < str_arr.length; i++)			
			{
				String str = str_arr[i];
				
				if ((i == str_arr.length-1) && (str.indexOf(',')==-1))
				{	// the last string might be the view_id
					view_id = str;
				} else
				{
					points.add( new ViewCentricPoint(str));
				}
			}
		}
	}

	public ViewCentricPolygon() {
		// TODO Auto-generated constructor stub
	}

	public SimpleBoundingBox GetEnclosingBox() {
		// TODO Auto-generated method stub
		if (points.isEmpty())
			return null;
		
		SimpleBoundingBox bbox = new SimpleBoundingBox(points.get(0).x(), points.get(0).y());
		
		for (ViewCentricPoint pt:points)
		{
			bbox.Union(pt.x(), pt.y());
		}
		return bbox;
	}

	public void AddPoint(double i, double j) {
		// TODO Auto-generated method stub
		points.add( new ViewCentricPoint(i,j));		
	}

	public String GetViewId() {
		return this.view_id;
	}
	
	public void SetViewId(String str_view_id) 
	{
		view_id = str_view_id;
	}
	
	public ViewCentricPoint GetLowestPoint() {
		if  (points.size()==0)
			return null;
		
		// System.out.println("DEBUG GetLowestPoint points.size() "+ points.size());
		ViewCentricPoint pt2 = points.get(0);
		
		for (ViewCentricPoint pt:points)
		{
			if (pt.y() > pt2.y())
			{
				pt2 = pt;
			}			
		}
		return pt2;		
	}
	
	public ArrayList<ViewCentricPoint> getPoints() {
		return points;
	}
	
}
