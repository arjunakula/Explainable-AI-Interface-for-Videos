package TextParser;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import sparql.CartesianMetricPoint;
import sparql.LocationData;
import sparql.OrientationData;
import sparql.SimpleBoundingBox;
import sparql.TimeData;
import sparql.Parsedata.Homography;


public class CObjectData {

	public String id;
	public Vector<CPositionData> positionVt = new Vector<CPositionData>();
	

	public String observer_id = "unknown";
	public String view_id = "unknown"; // NOTE: If this default value is changed, then the method isTheViewCorrect in MseeDataset must be modified.
	public String data_uri = "unknown";
	
	// public String label = "u"; 
	
	public int frame_min = -1;
	public int frame_max = -1; 
	
//	static int frame_offset = -700;
	
	static int frame_offset = 00;
	
	// scene centric time; which can be computed given the video start frame and frame-rate
	public Date mSceneTime_Start = null;
	public Date mSceneTime_End = null;
	
	
	CObjectData(String a_id, String a_observer_id, String a_view_id, String a_data_uri) 
	{
		id = a_id;
		observer_id = a_observer_id;
		view_id = a_view_id;
		data_uri =  a_data_uri;
		
	}
	
	public CPositionData GetPositionDataAtFrame(int iframe)
	{	
		if (positionVt.size() ==1)
		{
			CPositionData data = positionVt.elementAt(0);
			return data;
		}
		
		if (false)
		{
			System.err.println("DEBUG GetPositionDataAtFrame : positionVt.size(): " + positionVt.size());
			System.err.println("DEBUG GetPositionDataAtFrame : iframe: " + iframe);
			System.err.println("DEBUG GetPositionDataAtFrame : this.id: " + this.id);
			System.err.println("DEBUG GetPositionDataAtFrame : this.observer_id: " + this.observer_id);
			System.err.println("DEBUG GetPositionDataAtFrame : this.view_id: " + this.view_id);
		}
		
		
		// compute position with interpolation 
		CPositionData data_best_before = null;
		CPositionData data_best_after = null;
		
		int best_frame_before_diff = 1000000;
		int best_frame_after_diff = 1000000;
		
		
		
		for (int i = 0; i <positionVt.size(); i++ )
		{
			CPositionData data = positionVt.elementAt(i);
			

			// if frame number matches exactly, then return ;
			if (data.frame == iframe)
				return data;
				
				
			int diff = Math.abs(data.frame - iframe);
			
			if (data.frame < iframe )
			{
				if ((data_best_before == null) || (diff < best_frame_before_diff))
				{
					best_frame_before_diff = diff;
					data_best_before = data; 
				}
			} else
			{
				if ((data_best_after == null) || (diff < best_frame_after_diff))
				{
					best_frame_after_diff = diff;
					data_best_after = data; 
				}			
			}			
		}	
		
		if (data_best_before == null)
			return data_best_after;
		
		if (data_best_after == null)
			return data_best_before;
		
		if (data_best_before.contour.length != data_best_after.contour.length )
		{
			System.err.println("ERROR CObjectData GetPositionDataAtFrame, mismatched contour length ");
			
			// contour length not equal, unable to do interpolation 
			if (best_frame_before_diff < best_frame_after_diff)
			{
				return data_best_before;
			} else
			{
				return data_best_after;
			}
		}
		
		// do interpolation;
		CPositionData data_best = new CPositionData(data_best_before, best_frame_before_diff, data_best_after, best_frame_after_diff, iframe);
				
		
		
		return data_best;		
	}
	
	public void Printf()
	{	
		System.out.printf("\t id: " + id  + 
				" frame min " +frame_min + 
				" max "  + frame_max + 
				" obs_id " +observer_id + 
				" view_id " +view_id + " \n");
		
		/*
		for (int i = 0; i <positionVt.size(); i++ )
		{
			CPositionData data = positionVt.elementAt(i);
			System.out.printf("\t position " +i +": " );
			data.Printf();
		}
		*/ 		
	}
	
	public void AddPosition(
			String str_contour, 
			String str_facing, 
			String str_frame, 
			double frameRateScale, 
			String str_status 
			)		// if there is no frame information, then use the max frame of the view
	{
		int frame = -1;
		if (str_frame.length()>0)
		{	frame = Integer.parseInt(str_frame) + frame_offset;
			
			if (frameRateScale != 1.0)
			{
				frame = (int) Math.round((double) frame * frameRateScale);
			}
			if ((frame_min==-1)  || (frame < frame_min))
			{
				frame_min = frame;
			}
			
			if ((frame_max==-1)  || (frame > frame_max))
			{
				frame_max = frame;
			}
			positionVt.add(new CPositionData(str_contour, str_facing, frame, str_status));

		} else
		{	positionVt.add(new CPositionData(str_contour, str_facing, frame, str_status));
			positionVt.add(new CPositionData(str_contour, str_facing, frame, str_status));
		}
			
			
	}
	

	public void AddPosition(int x1, int y1, int x2, int y2, int a_frame,
			double frameRateScale) {
		int frame = a_frame + frame_offset;
		
		if (frameRateScale != 1.0)
		{
			frame = (int) Math.round((double) frame * frameRateScale);
		}
		if ((frame_min==-1)  || (frame < frame_min))
		{
			frame_min = frame;
		}
		
		if ((frame_max==-1)  || (frame > frame_max))
		{
			frame_max = frame;
		}
		positionVt.add(new CPositionData(x1,y1,x2,y2, frame));
	}	

	
	public void ComputeSceneCentricTime(Date videoTime_Start, Date videoTime_End, double frameRate) {
				
		mSceneTime_Start = new Date(); 
		mSceneTime_End = new Date();
		
		if ((frame_min != -1) && (frame_max != -1))
		{		
			long start_msec = Math.round((double) frame_min * 1000.0 / frameRate );
			long end_msec = Math.round((double) frame_max * 1000.0 / frameRate );
			
			mSceneTime_Start.setTime(videoTime_Start.getTime() + start_msec);
			mSceneTime_End.setTime(videoTime_Start.getTime() + end_msec);
			
			for (CPositionData data : this.positionVt)
			{	data.ComputeSceneCentricTime(videoTime_Start, frameRate) ;
			}
			
		} else
		{
			
			mSceneTime_Start.setTime(videoTime_Start.getTime() );
			mSceneTime_End.setTime(videoTime_End.getTime() );
			
			if (this.positionVt.size() ==2)
			{	
				positionVt.elementAt(0).SetSceneCentricTime(mSceneTime_Start);
				positionVt.elementAt(1).SetSceneCentricTime(mSceneTime_End);				
			} else
			{
				System.err.println("ERROR CObjectData ComputeSceneCentricTime positionVt.size()  not equal to 2 :" + positionVt.size() );
			}
			
	
		}
		
		
		/* debug
		System.out.println("DEBUG videoTime_Start " + videoTime_Start.toString());
		System.out.println("DEBUG start frame " + begin_frame + " end frame " + end_frame + " frame rate "+ frameRate);
		System.out.println("DEBUG mSceneTime_Start " + mSceneTime_Start.toString());
		System.out.println("DEBUG mSceneTime_End " + mSceneTime_End.toString());
		*/ 		
		


		
	}

	public void ComputeSceneCentricLocation(Homography hView2Map, SimpleBoundingBox map_bbox) {
		for (CPositionData data : this.positionVt)
		{	data.ComputeSceneCentricLocation(hView2Map, map_bbox);
		}
		
	}

	public TimeData GetTimeData() {
		
		TimeData timeData = new TimeData();	
		
		timeData.SetViewFramePeriod(
				this.frame_min,
				this.frame_max
				);
		
		if 	((this.mSceneTime_Start != null) &&
			(this.mSceneTime_End != null)) {
			timeData.SetSceneTimePeriod(this.mSceneTime_Start, this.mSceneTime_End);
		}
		return timeData;		
	}

	// return the ground position of object given a scene time; do interpolation if necessary
	public LocationData GetGroundPosAtSceneTimeWithInterpolation(
			Date time_test) {
		
		if (	(time_test.compareTo(this.mSceneTime_Start) < 0) || 
				(time_test.compareTo(this.mSceneTime_End) > 0) )
		{
			
			return null;
		}
												
		
		CPositionData	posdata_before = null;
		CPositionData	posdata_after = null;
		
		for (CPositionData data : this.positionVt)
		{	 
			if (data.mSceneTime == null)
			{
				System.err.printf("UNEXPECTED CObjectData GetGroundPosAtSceneTimeWithInterpolation; data.mSceneTime is null");
				continue; 
			}
			
			 
			if (data.groundpos == null)
			{
				System.err.printf("UNEXPECTED CObjectData GetGroundPosAtSceneTimeWithInterpolation; data.groundpos is null");
				continue; 
			}
			
			
			long time_diff = time_test.getTime() - data.mSceneTime.getTime();
			if (time_diff ==0 )
			{
				// found exact time, return position
				return data.groundpos;
				
			} else if (time_diff < 0 )
			{
				if ((posdata_before == null) ||
				    (data.mSceneTime.compareTo(posdata_before.mSceneTime) > 0 ))
				{
					posdata_before = data; 
				}				
			} else if (time_diff > 0 )
			{
				if ((posdata_after == null) ||
				    (data.mSceneTime.compareTo(posdata_after.mSceneTime) < 0 ))
				{
					posdata_after = data; 
				}				
			}				
		}
		
		
		if ((posdata_before == null) && (posdata_after == null))
		{	return null;		
		}
		if (posdata_before == null) {
			return posdata_after.groundpos;
		} else if (posdata_after== null) {
			return posdata_before.groundpos;
		} 
		
		// do interpolation 
		double d1 = time_test.getTime() - posdata_before.mSceneTime.getTime();
		double d2 = posdata_after.mSceneTime.getTime() - time_test.getTime();  
		double r1 = d2 / (d1 + d2);
		double r2 = d1 / (d1 + d2);
		
		return LocationData.GetWeightedAverageCartesianMetricPoint(
				r1, posdata_before.groundpos,
				r2, posdata_after.groundpos);				
	}

	// return the ground polygon at the nearest scene time; 	
	public LocationData GetGroundPolygonAtNearestSceneTime(
			Date time_test)
	{
		CPositionData	posdata_nearest = GetPositionDataAtNearestSceneTime(time_test, true);
		if (posdata_nearest == null) return null;
		return posdata_nearest.groundpolygon;
	}

	// get the positiondata with time nearest to a given scene time
	public CPositionData GetPositionDataAtNearestSceneTime(Date time_test, boolean checkForGroundPolygon) {
		CPositionData	posdata_nearest = null;
		
		long time_diff_nearest = 0;
		
		for (CPositionData data : this.positionVt)
		{	 
			if (data.mSceneTime == null)
			{
				System.err.printf("UNEXPECTED CObjectData GetGroundPolygonAtNearestSceneTime; data.mSceneTime is null");
				continue; 
			}
			if (checkForGroundPolygon && data.groundpolygon == null)
			{
				System.err.printf("UNEXPECTED CObjectData GetGroundPolygonAtNearestSceneTime; data.groundpolygon is null");
				continue; 
			}
			
			
			long time_diff = Math.abs(time_test.getTime() - data.mSceneTime.getTime());
			if (time_diff ==0 )
			{
				// found exact time, return position
				return data;
				
			} 
			
			if ((posdata_nearest == null) ||
				(time_diff < time_diff_nearest))
			{	posdata_nearest = data;
				time_diff_nearest = time_diff;
				
			}				
		} // for
		
		return posdata_nearest;
	}

	public int[] GetViewContourAtNearestSceneTime(Date time_test) {
		CPositionData	posdata_nearest = GetPositionDataAtNearestSceneTime(time_test, true);
		if (posdata_nearest == null) return null;
		return posdata_nearest.contour;
	}

	public long GetSceneDurationInMsec() {
		if ((mSceneTime_Start == null) || (mSceneTime_End == null))
		{
			System.err.println("ERROR GetSceneDurationInMsec: Scene time is null ");
			return 0;
		}
		return mSceneTime_End.getTime() - mSceneTime_Start.getTime();
	}

	
	public OrientationData GetOrientationDataAtNearestSceneTime(
			Date time_test) {
			CPositionData	posdata_nearest = GetPositionDataAtNearestSceneTime(time_test, true);
			if (posdata_nearest == null) return null;
			return posdata_nearest.orientation;
	}

	public void SubsamplePos(int i) {
		
		if (positionVt.size() < 5)
			return; 
		
		int size = positionVt.size() ;
		
		for (int j = size-2; j >=1;j-- )
		{
			if (j % i != 0)
			{
				positionVt.remove(j);
			}
			
		}	
		
	}
	
	/**
	 * Sorts the positions for this object, by frame number.
	 */
	public void sortPositions( ) {
		Collections.sort( positionVt, new Comparator<CPositionData>() {
			@Override
			public int compare(CPositionData o1,
					CPositionData o2) {
				if ( o1 == null || o2 == null ) {
					System.err.println( "WARN null CPositionData." );
					return 0;
				}
				return o1.frame - o2.frame;
			}
		} );
	}

	public long GetTimeDifference_NearestSceneTime(TimeData time_data) {
		CPositionData	posdata_1 = GetPositionDataAtNearestSceneTime(time_data.mSceneTime_Start, true);
		long diff_1 = time_data.mSceneTime_Start.getTime() - posdata_1.mSceneTime.getTime();
		
		if (time_data.mSceneTime_Start.compareTo(time_data.mSceneTime_End) ==0)
		{
			return diff_1;
		}
		else
		{
			CPositionData	posdata_2 = GetPositionDataAtNearestSceneTime(time_data.mSceneTime_End, true);
			long diff_2 = time_data.mSceneTime_End.getTime() - posdata_2.mSceneTime.getTime();	
			return Math.min(diff_1,diff_2);
		}		
	}

	public String GetObsId() {
		return this.observer_id;
	}
	
	public String GetViewId() {
		return this.view_id;
	}

}
