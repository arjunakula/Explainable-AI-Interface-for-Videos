package sparql;

/**
 * The <code>ViewCentricPoint</code> class is a representation of a point on a graph.
 *
 * @author Ken Samuel
 * @version 1.0, Aug 19, 2013
 * @since 1.6
 */
public class ViewCentricPoint {
	
	/** <code>X</code> is the horizontal coordinate of this point. */
	private double X =0;
	
	/** <code>Y</code> is the vertical coordinate of this point. */
	private double Y =0;
	
	private String view_id = ""; // NOTE: If this default value is changed, then the method isTheViewCorrect in MseeDataset must be modified.
	

	public ViewCentricPoint() {
	}

	
	/**
	 * The <code>ViewCentricPoint</code> constructor sets the coordinates of this point.
	 *
	 * @param x is the x-coordinate of this point.
	 * @param y is the y-coordinate of this point.
	 */
	

	public ViewCentricPoint(String a_ViewId, double x2, double y2) {
		view_id = a_ViewId;
		X = x2;
		Y = y2;
	}

	
	public ViewCentricPoint(double x, double y) {
		X = x;
		Y = y;
	}

	public ViewCentricPoint(String locationStr) throws MseeException {
		
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
		if (str_arr.length == 2 )
		{	
			view_id = str_arr[1];
		}
		if ((str_arr.length == 1 ) ||(str_arr.length == 2 ))
		{
			String [] str_arr2 =  str_arr[0].split(",");		
			if (str_arr2.length == 2)
			{
				X = Double.parseDouble(str_arr2[0]);
				Y = Double.parseDouble(str_arr2[1]);
			} else if (str_arr2.length > 2)
			{	
				double sum_x = Double.parseDouble(str_arr2[0]);
				double max_y = Double.parseDouble(str_arr2[1]); 
				int ctr = 1; 
				for (int i = 2; i < str_arr2.length-1; i+=2 )
				{
					sum_x += Double.parseDouble(str_arr2[i]);
					max_y = Math.max(max_y, Double.parseDouble(str_arr2[i+1]));
					ctr++;					
				}
				this.X = sum_x / ctr;
				this.Y = max_y;				
			} 
		} else
		{	System.err.println("ERROR ViewCentricPoint unable to parse string " + locationStr);
		}
	}


	/**
	 * The <code>x</code> function returns the value of the global variable,
	 * <code>X</code>, a <b><code>double</code></b>.
	 *
	 * @return the value of <code>X</code>.
	 */
	public double x() {
		return X;
	}

	/**
	 * The <code>y</code> function returns the value of the global variable,
	 * <code>Y</code>, a <b><code>double</code></b>.
	 *
	 * @return the value of <code>Y</code>.
	 */
	public double y() {
		return Y;
	}

	public SimpleBoundingBox GetEnclosingBox() {
		return new SimpleBoundingBox(X, Y);		
	}


	public String GetViewId() {
		return this.view_id;
	}


	public void Printf() {
		System.out.printf("\t id " +  view_id + " x " + X + " y "+Y);		
	}


	public void SetViewId(String view_id2) {
		this.view_id = view_id2;
		
	}
}
