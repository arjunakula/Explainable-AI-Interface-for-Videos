package sparql;

public class OrientationData {
	
	double fx = 0;
	double fy = 0;
	
	public OrientationData(double fx2, double fy2) {
		this.fx = fx2;
		this.fy = fy2;
	}

	public double DotProduct(OrientationData orient2) {
		
		return 	(fx * orient2.fx) + (fy * orient2.fy); 
	}
}
