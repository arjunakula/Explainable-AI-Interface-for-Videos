package TextParser;

import java.util.Date;

import sparql.LocationData;
import sparql.SimpleBoundingBox;
import sparql.ViewCentricPoint;
import sparql.ViewCentricPolygon;
import sparql.LocationData.LocationType;
import sparql.MseeException;
import sparql.Parsedata.Homography;

public class CActionData {
	
	public int begin_frame = 0; 
	public int end_frame = 0;
	
	
	public Date mSceneTime_Start = null;
	public Date mSceneTime_End = null;
	
	public LocationData locationData_View = null; 
	public LocationData locationData_Cartesian = null; 
	

	public CActionData(int begin_frame2, int end_frame2, String a_str_position) {
		// str_position  = a_str_position ;
		
		begin_frame = begin_frame2;
		end_frame = end_frame2; 
		
		if (a_str_position != null)
		{
			locationData_View = new LocationData();
			try {
				locationData_View.Set(LocationType.VIEW_CENTRIC_POLYGON, a_str_position) ;
				// munwai  20150421  to fix problem with event location:
				// locationData_View.Set(LocationType.VIEW_CENTRIC_POINT, a_str_position) ;
				
				
			} catch (MseeException e) {
				// TODO Auto-generated catch block
				System.err.println("WARN CActionData; unable to parse str_position " + a_str_position);
				// e.printStackTrace();
			}
		}
	}


	public void ComputeSceneCentricTime(Date videoTime_Start, double frameRate) {
		mSceneTime_Start = new Date(); 
		mSceneTime_End = new Date();
		
		long start_msec = Math.round((double) begin_frame * 1000.0 / frameRate );
		long end_msec = Math.round((double) end_frame * 1000.0 / frameRate );
		
		mSceneTime_Start.setTime(videoTime_Start.getTime() + start_msec);
		mSceneTime_End.setTime(videoTime_Start.getTime() + end_msec);
		
	}


	public void ComputeSceneCentricLocation(Homography hView2Map, SimpleBoundingBox map_bbox) {
		if (this.locationData_View != null)
		{
			if (this.locationData_View.view_centric_point == null)
			{
				if (this.locationData_View.view_centric_polygon != null)
				{
					locationData_View.view_centric_point= locationData_View.view_centric_polygon.GetLowestPoint() ;
				}
			}
			if (this.locationData_View.view_centric_point != null)
			{
				this.locationData_Cartesian = hView2Map.Transform(
							LocationType.CARTESIAN_METRIC_POINT, 
							locationData_View.view_centric_point.x(),
							locationData_View.view_centric_point.y(), map_bbox);		
				
				
			}		
			
		}	else
		{
			System.err.println("ERROR CActionData ComputeSceneCentricLocation locationData_View is null");
		}
		
	}


	public LocationData GetLocationData(LocationType location_type) {
		if ((location_type == LocationType.CARTESIAN_METRIC_POLYGON)					
				|| (location_type == LocationType.CARTESIAN_METRIC_POINT)
				||	(location_type == LocationType.GEODETIC_POLYGON)
				||	(location_type == LocationType.GEODETIC_POINT)
				)
		{		
			return locationData_Cartesian ; 
		}		
		else if (((location_type == LocationType.VIEW_CENTRIC_POINT)  
				||	(location_type == LocationType.VIEW_CENTRIC_POLYGON) )				
				)
		{
			return locationData_View ;
		} else
		{				
			System.err.println("ERROR CActionData; GetLocationData: unknown location_type " + location_type );
			return null;
		}
		
		
	}
	
	

}
