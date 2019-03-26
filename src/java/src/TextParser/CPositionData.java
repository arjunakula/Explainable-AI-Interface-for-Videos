package TextParser;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

import java.util.Date;
import java.util.Vector;

import sparql.LocationData;
import sparql.LocationData.LocationType;
import sparql.MseeDataset;
import sparql.MseeException;
import sparql.OrientationData;
import sparql.SimpleBoundingBox;
import sparql.ViewCentricPoint;
import sparql.ViewCentricPolygon;
import sparql.Parsedata.Homography;

// this is for frame coordinate only 
public class CPositionData {

	public int[] contour = null;
	
	// cartesian coordinate. can be lat-lon, or can be map 
	public LocationData groundpos = null; 
	public LocationData groundpolygon = null;		// this is used for inside/outside/entering/exiting operation. e.g. for a room  
		
	public int frame = -1; 	

	public Date mSceneTime = null;
	
	public int iClosed0_Open1  = 0;
	
	OrientationData orientation = null; 
	
	
	CPositionData(String str_contour, String str_facing, int a_frame, String str_status) 
	{		
		frame = a_frame ;		
		String str[] = str_contour.split(",");
		
		if ( str.length > 0 )
		{
			contour = new int[str.length];
		
			for (int i = 0; i < str.length; i++)
			{
				contour[i] = Integer.parseInt(str[i]);
			}		
		}	
		
		
		// System.out.println("DEBUG str_facing.length(): " +  str_facing.length() );
		
		if (str_facing.length() >0)
		{
			String str2[] = str_facing.split(",");
		
			// facing should contain two numbers in scene coordinates 
			if ( str2.length > 0 )
			{
				if ( str2.length ==2 )
				{				
					
				
					double fx = Double.parseDouble(str2[0]);
					double fy = Double.parseDouble(str2[1]);
					
					orientation = new OrientationData(fx, fy);
				} else
				{
					System.err.println("ERROR CPositionData,  str2.length is not 2: " +  str2.length);
					System.err.println(str_facing);
					
					
				}
			}
		}		
		
		if (str_status.contentEquals("Open") )
		{
			iClosed0_Open1 = 1;
			//System.err.println("debug position data : Open");
		}
	}	
	


	public CPositionData(int x1, int y1, int x2, int y2, int frame2) {
		frame = frame2;	
		
		contour = new int[8];
		contour[0] = x1;
		contour[1] = y1;
		contour[2] = x2;
		contour[3] = y1;
		contour[4] = x2;
		contour[5] = y2;
		contour[6] = x1;
		contour[7] = y2;		
		
	}
	
	public CPositionData(
			CPositionData data_1,
			int diff_1, 			// this is the proximity to data_1, therefore this should be the weight for data_2
			CPositionData data_2,
			int diff_2, 			// this is the proximity to data_2, therefore this should be the weight for data_1
			int iframe) {
		// TODO Auto-generated constructor stub
		// initialize a new position by interpolating two data
		frame = iframe ;	
		
		if (data_1.contour.length != data_2.contour.length)
		{
			System.err.println("ERROR CPositionData interpolation, mismatched contour length ");
			return;
		}
		
		int contour_len = data_1.contour.length;
		
		contour = new int[contour_len];
		for (int i = 0; i < contour_len; i++)
		{
			// apply weighted average
			contour[i] = ((diff_2 * data_1.contour[i])		
						+ (diff_1 * data_2.contour[i])) / (diff_1 + diff_2);
			
			// System.out.println("DEBUG CPositionData interpolation  " + contour[i]  + " data_1 " + data_1.contour[i] + " data_2 " + data_2.contour[i] 
			//		+ " diff_1 " + diff_1 + " diff_2 " + diff_2);
		}			
	}

	public void Printf()
	{	
		for (int i = 0; i <contour.length; i++ )
		{
			System.out.printf(contour[i]+",");			
		}		
		System.out.printf("  frame:" + frame +"\n");		
	}

	public void ComputeSceneCentricLocation(Homography hView2Map, SimpleBoundingBox map_bbox) {
		// TODO Auto-generated method stub
		if (contour.length <2)
			return; 
		
		// get bottom coordinate
		double x=0; 
		double y=0; 
		for (int i = 0; i <contour.length; i+=2  )
		{
			if ((i ==0 ) || (contour[i+1] > y))
			{
				x = contour[i];
				y = contour[i+1];
			} else if (contour[i+1] == y)
			{
				x = 0.5 *(x + contour[i]);  // take average of x
			}			
		}
		
		
		if (MseeDataset.parsedDataDescriptor.outdoorScene)
		{
			this.groundpos = hView2Map.Transform(LocationType.GEODETIC_POINT, x,y, map_bbox);
		} else
		{
			this.groundpos = hView2Map.Transform(LocationType.CARTESIAN_METRIC_POINT, x,y, map_bbox);
		}
				
		
		if (contour.length >4)
		{
			if (MseeDataset.parsedDataDescriptor.outdoorScene)
			{
				this.groundpolygon = new LocationData(LocationType.GEODETIC_POLYGON);
			} else
			{
				this.groundpolygon = new LocationData(LocationType.CARTESIAN_METRIC_POLYGON);
			}
			
			
			for (int i = 0; i <contour.length; i+=2  )
			{
				x = contour[i];
				y = contour[i+1];
				
				if (MseeDataset.parsedDataDescriptor.outdoorScene)
				{
					groundpolygon.AddPointToGeodeticPolygon(hView2Map.Transform2Geodetic( x,y));
				} else
				{
					groundpolygon.AddPointToCartesianMetricPolygon(hView2Map.Transform( x,y));
				}						
						
			}
		}		
		
	}

	public void ComputeSceneCentricTime(Date videoTime_Start, double frameRate) {

		if (mSceneTime != null)
		{
			System.err.println("WARN: CPositionData, ComputeSceneCentricTime, mSceneTime already initialized.");
		}
		mSceneTime = new Date();
		
		if (this.frame!=-1)
		{
			long msec = Math.round((double) this.frame * 1000.0 / frameRate );
			mSceneTime.setTime(videoTime_Start.getTime() + msec);	
		} else
		{
			mSceneTime.setTime(videoTime_Start.getTime() );
		}
		
		
	}

	public void SetSceneCentricTime(Date sceneTime) {
		mSceneTime = new Date();
		mSceneTime .setTime(sceneTime.getTime() );
	}

	public LocationData GetLocationData(LocationType target_type, String view_id) {
		if ( ( target_type == LocationType.CARTESIAN_METRIC_POLYGON	||			
				target_type == LocationType.GEODETIC_POLYGON )
				&& this.groundpolygon != null )
		{
			return this.groundpolygon;				
		} 
		else if ( ( target_type == LocationType.CARTESIAN_METRIC_POINT  
				||	target_type == LocationType.CARTESIAN_METRIC_POLYGON
				||	target_type == LocationType.GEODETIC_POLYGON
				||	target_type == LocationType.GEODETIC_POINT )
				&& this.groundpos != null )
		{
			return this.groundpos;
		} else
		{	
			LocationData loc_data = new LocationData(target_type , this, view_id);
			return loc_data ;
			
			/*
			System.err.println("DEBUG CPositionData GetLocationData unhandled type target_type " + target_type);
			
			if (this.groundpolygon == null)
			{
				System.err.println("DEBUG CPositionData GetLocationData groundpolygon == null ");				
			}
			
			if (this.groundpos == null)
			{
				System.err.println("DEBUG CPositionData GetLocationData groundpos == null ");				
			}
			return null;
			*/ 
		}
		
	}

	
}

