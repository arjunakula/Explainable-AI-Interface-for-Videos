package sparql;

public class SimpleBoundingBox {

	public double x1=0;
	public double y1=0;
	public double x2=0;
	public double y2=0;
	
	public SimpleBoundingBox(double x, double y) {
		x1=x;
		y1=y;
		x2=x;
		y2=y;	
	}
	
	public SimpleBoundingBox(double x1, double y1, double x2, double y2) {
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;	
	}


	public SimpleBoundingBox(int[] contour) {
		if (contour == null)
		{
			System.err.println("ERROR SimpleBoundingBox; contour is null");
			return;			
		}
		
		if (contour.length == 0)
		{
			System.err.println("ERROR SimpleBoundingBox; contour length is zero");
			return;			
		}
		
		
		for (int i = 0; i <contour.length; i+=2  )
		{
			if (i ==0 )
			{	// set bounding box as first point
				x1 = contour[i];
				y1 = contour[i+1];
				x2 = x1;
				y2 = y1;
				
			} else 
			{
				Union((double)contour[i], (double) contour[i+1] );
				/* replaced
				if (contour[i] < x1) { x1 = contour[i];	}
				if (contour[i] > x2) { x2 = contour[i];	}
				if (contour[i+1] < y1) { y1 = contour[i+1];	}
				if (contour[i+1] > y2) { y2 = contour[i+1];	}		
				*/ 		
			}			
		}
	}

	public void Union(double x, double y) {
		x1 = Math.min(x1,x);
		y1 = Math.min(y1,y);
		x2 = Math.max(x2,x);
		y2 = Math.max(y2,y);		
	}

	public boolean IsIntersect(SimpleBoundingBox bbox2) {
		
		if (	(bbox2.x1 > x2) ||
				(bbox2.y1 > y2) ||
				(bbox2.x2 < x1) ||
				(bbox2.y2 < y1) 
			)
		{
			return false;
		} else
		{
			return true;
		}
	}
	

	public double GetOverlapRatio(SimpleBoundingBox bbox2) {
		
		if (IsIntersect(bbox2) == false)
			return 0;
		
		double i_x1 = Math.max(x1,bbox2.x1);
		double i_x2 = Math.min(x2,bbox2.x2);
		double i_y1 = Math.max(y1,bbox2.y1);
		double i_y2 = Math.min(y2,bbox2.y2);
		
		double area = (i_x2 - i_x1 ) * (i_y2 - i_y1 ) ;
		double overlapRatio = 2.0 * area / (this.GetArea() + bbox2.GetArea());
		return overlapRatio; 

	}

	private double GetArea() {
		return GetHeight() * GetWidth();
	}

	public boolean IsIntersect_InX(SimpleBoundingBox bbox2) {

		if (	(bbox2.x1 > x2) ||
				(bbox2.x2 < x1)
			)
		{
			return false;
		} else
		{
			return true;
		}
	}

	public double GetCenterY() {	
		return 0.5 *(y1+y2);
	}

	public void Printf() {
		System.out.println(" Box " + x1 + " " + y1 + " " + x2 + " " + y2);
				
	}

	public double GetWidth() {
		return (x2 - x1);
	}

	public void Expand(double f) {
		x1 -= f;
		x2 += f;
		y1 -= f;
		y2 += f;
		
		
	}

	public void Subtract(double f) {
		x1 += f;
		x2 -= f;
		y1 += f;
		y2 -= f;	
		
	}
	
	public double GetHeight() {
		return (y2 - y1);
	}	
	
}
