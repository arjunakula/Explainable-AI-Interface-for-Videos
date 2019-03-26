package sparql.Parsedata;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import sparql.GeodeticPoint;
import sparql.LocationData.LocationType;
import sparql.CartesianMetricPoint;
import sparql.LocationData;
import sparql.MseeException;
import sparql.SimpleBoundingBox;

public class Homography {
	
	double[][] hmatrix = new double[3][3];
	
	boolean valid = false; 

	@SuppressWarnings("unused")
	public void LoadFile(double scale, String input_file) {
		// TODO Auto-generated method stub
		
		Scanner input;
		try {
			input = new Scanner(new FileReader(input_file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("WARN in Homography; file not found " + input_file);
			return; 
			// e.printStackTrace();
		}

	    for (int i = 0; i < hmatrix.length; i++) {
	        for (int j = 0; j < hmatrix[i].length; j++) {
	        	hmatrix[i][j] = input.nextDouble();
	        }
		    }
	    
	    // apply scale only for the first two rows
	    for (int i = 0; i < 2; i++) {
	        for (int j = 0; j < hmatrix[i].length; j++) {
	        	hmatrix[i][j] = scale * hmatrix[i][j] ;
	        }
		    }
	    
	    valid = true;
	    
	    if (false) 
	    {	// debug
		    System.out.println("  matrix: " + input_file);
		    for (int i = 0; i < hmatrix.length; i++) {
		        for (int j = 0; j < hmatrix[i].length; j++) {
		        	System.out.print("  matrix: " + i + " " + j + " " + hmatrix[i][j]);
		        }
		        System.out.println("");
			    }
	    }
	}

	public sparql.LocationData Transform(LocationType loc_type, double x,
			double y, SimpleBoundingBox map_bbox) {
		sparql.LocationData  data = new sparql.LocationData();

		
		double[] vec = new double[3] ;
		
	    for (int i = 0; i < 3; i++) {
	    	vec[i] = (hmatrix[i][0] * x) + (hmatrix[i][1] * y)+ hmatrix[i][2];
	    }
	    
	    if (vec[2] ==0) {
	    	vec[2]  = 0.0000001;
	    }
	    
	    double fx = vec[0] / vec[2];
	    double fy = vec[1] / vec[2];
	    
	    // System.out.println("DEBUG Homography fx:"+ fx + " fy:" + fy);
	    
	    
	    // apply limit to the range for x,y if applicable; this is used for e.g. object we know that should be confined within a room 
	    if (map_bbox != null)
	    {
	     	if (false) // debug 
	    	{	System.out.println("DEBUG Transform fx:"+ fx + " fy:" + fy);
	    	}
	     	
	    	fx = Math.max(fx,  map_bbox.x1);
	    	fx = Math.min(fx,  map_bbox.x2);
	    	fy = Math.max(fy,  map_bbox.y1);
	    	fy = Math.min(fy,  map_bbox.y2);
	    	
	    	if (false) // debug 
	    	{	System.out.println("DEBUG Transform map_bbox");
	    		map_bbox.Printf();
	    		System.out.println("DEBUG Transform fx:"+ fx + " fy:" + fy);
	    	}
	    }
	   
	    data.SetPoint(loc_type, fx, fy);
		return data;
	}
	

	public CartesianMetricPoint Transform(double x, double y) {
		/*
		 * double[] vec = new double[3] ;
		 
		
	    for (int i = 0; i < 3; i++) {
	    	vec[i] = (hmatrix[i][0] * x) + (hmatrix[i][1] * y)+ hmatrix[i][2];
	    }
	    
	    if (vec[2] ==0) {
	    	vec[2]  = 0.0000001;
	    }
	    
	    double fx = vec[0] / vec[2];
	    double fy = vec[1] / vec[2];
	    */
	    
	    double vec0;
	    double vec1;
	    double vec2;
	    
		
	    // for (int i = 0; i < 3; i++) {
	    vec0 = (hmatrix[0][0] * x) + (hmatrix[0][1] * y)+ hmatrix[0][2];
	    vec1 = (hmatrix[1][0] * x) + (hmatrix[1][1] * y)+ hmatrix[1][2];
	    vec2 = (hmatrix[2][0] * x) + (hmatrix[2][1] * y)+ hmatrix[2][2];
	    
	    
	    if (vec2 ==0) {
	    	vec2  = 0.0000001;
	    }
	    
	    double fx = vec0 / vec2;
	    double fy = vec1 / vec2;
	    
	    return new CartesianMetricPoint(fx,fy);
	}

	

	public GeodeticPoint Transform2Geodetic(double x, double y) {		
	    
	    double vec0;
	    double vec1;
	    double vec2;
	    
		
	    // for (int i = 0; i < 3; i++) {
	    vec0 = (hmatrix[0][0] * x) + (hmatrix[0][1] * y)+ hmatrix[0][2];
	    vec1 = (hmatrix[1][0] * x) + (hmatrix[1][1] * y)+ hmatrix[1][2];
	    vec2 = (hmatrix[2][0] * x) + (hmatrix[2][1] * y)+ hmatrix[2][2];
	    
	    
	    if (vec2 ==0) {
	    	vec2  = 0.0000001;
	    }
	    
	    double fx = vec0 / vec2;
	    double fy = vec1 / vec2;
	    
	    return new GeodeticPoint(fx,fy);
	}
}
