package sparql.Parsedata;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import sparql.ViewCentricPoint;


/*
 * 
 * 
Xp = (j-cx)/fx;
Yp = (i-cy)/fy;
 
% radius squared for the radial distortion
r2 = Xp.^2 + Yp.^2;
 
% variable change for ease of reading the equations below
x=Xp;
y=Yp;
 
% apply the distortion
x = x.*(1+k1*r2 + k2*r2.^2 + k3*r2.^3) + 2*p1.*x.*y + p2*(r2 + 2*x.^2);
y = y.*(1+k1*r2 + k2*r2.^2 + k3*r2.^3) + 2*p2.*x.*y + p1*(r2 + 2*y.^2);
 
% convert back to image coordinates. These are the distorted locations.
u = fx*x+cx;
v = fy*y+cy;

 */
public class DistortionParam {

	double fx = 0; 
	double fy = 0;
	double cx = 0; 
	double cy = 0;
	double k1 = 0;
	double k2 = 0;
	double k3 = 0; 
	double p1 = 0;
	double p2 = 0; 
	
	Boolean bInit = false;
	
	public DistortionParam(String input_file) {
		Scanner input;
		try {
			input = new Scanner(new FileReader(input_file));
		} catch (FileNotFoundException e) {
			System.out.println("WARN in DistortionParam; file not found " + input_file);
			return; 
		}
		
		fx = input.nextDouble();
		fy = input.nextDouble();
		cx = input.nextDouble();
		cy = input.nextDouble();
		k1 = input.nextDouble();
		k2 = input.nextDouble();
		k3 = input.nextDouble();
		p1 = input.nextDouble();
		p2 = input.nextDouble();
		
		bInit = true;

		this.Printf();
	}

	private void Printf() {
		System.out.println("Distortion");
		System.out.println("\tfx " + fx);
		System.out.println("\tfy " + fy);
		System.out.println("\tcx " + cx);
		System.out.println("\tcy " + cy);
		System.out.println("\tk1 " + k1);
		System.out.println("\tk2 " + k2);
		System.out.println("\tk3 " + k3);
		System.out.println("\tp1 " + p1);
		System.out.println("\tp2 " + p2);		
	}

	public ViewCentricPoint Undistort(ViewCentricPoint new_pt) {
		
		if (bInit)
		{
			double x = (new_pt.x() - cx ) / fx;
			double y = (new_pt.y() - cy ) / fy;
			
			double r2_distorted = (x*x) + (y*y); 		// this is the dist_sqr in the distorted domain
			double r2_undistorted = r2_distorted  / (1 * (2 * k1));  // this is an approximation
			double scale_distorted = (1 + (k1 * r2_undistorted));  // scaling of x and y, from undistorted to distorted
			x = x / scale_distorted ;  // convert to undistorted
			y = y / scale_distorted ;
			
			// conver to image
			x = (x * fx) + cx; 
			y = (y * fy) + cy;
			return new ViewCentricPoint(new_pt.GetViewId(), x, y); 
		} else
			return new_pt;
				
	}
	
}
