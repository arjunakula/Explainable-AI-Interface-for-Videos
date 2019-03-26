/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

package TextParser;

import java.util.Date;
import java.util.Vector;

import sparql.LocationData;
import sparql.MseeException;
import sparql.LocationData.LocationType;
import sparql.MseeFunction;
import sparql.SimpleBoundingBox;
import sparql.TimeData;
import sparql.Parsedata.Homography;


public class CEventData {
	
	public String id = "unknownID";
	public String observer_id = "unknown";
	public String view_id = "unknown"; // NOTE: If this default value is changed, then the method isTheViewCorrect in MseeDataset must be modified.
	public String data_uri = "unknown";
	
	
	public int begin_frame = 0; 
	public int end_frame = 0;
	
	public Date mSceneTime_Start = null;
	public Date mSceneTime_End = null;
	
//	public String str_position ="";  // obsolete
	public String str_text = "";
	
	/* move to CActionData
	// scene centric time; which can be computed given the video start frame and frame-rate
	public Date mSceneTime_Start = null;
	public Date mSceneTime_End = null;
	
	public LocationData locationData_View = null; 
	public LocationData locationData_Cartesian = null; 
	*/
	
	public Vector<CActionData> actionDataVt = new Vector<CActionData>();
	
	
	CEventData(String a_observer_id, String a_view_id, String a_data_uri, String a_id, String a_str_position, int b_end_frame, int a_begin_frame) 
	{	
		observer_id = a_observer_id;
		view_id = a_view_id;
		data_uri = a_data_uri;
		id = a_id;						
		begin_frame = a_begin_frame; 
		end_frame = b_end_frame;
		
		if (a_str_position!= null)
		{
			this.AddActionData(a_begin_frame, b_end_frame, a_str_position);
		}
		
		/*
		str_position  = a_str_position ;
		
		if (str_position != null)
		{
			locationData_View = new LocationData();
			try {
				locationData_View.Set(LocationType.VIEW_CENTRIC_POINT, str_position) ;
			} catch (MseeException e) {
				// TODO Auto-generated catch block
				System.err.println("WARN CEventData; unable to parse str_position " + str_position);
				// e.printStackTrace();
			}
		}
		*/ 
	}		
		



	public void AddActionData(int begin_frame2, int end_frame2,
			String str_position2) {
		
		// update frame range first
		if (actionDataVt.size()==0)
		{
			begin_frame = begin_frame2;
			end_frame = end_frame2;
		} else
		{
			begin_frame = Math.min(begin_frame, begin_frame2);
			end_frame = Math.max(end_frame, end_frame2);
		}
		
		actionDataVt.add(new CActionData(begin_frame2, end_frame2, str_position2));
	}	
	

	public void AddText(String str)
	{
		str_text = str;
	}

	public void ComputeSceneCentricTime(Date videoTime_Start, double frameRate) {
		// TODO Auto-generated method stub
		
		
		mSceneTime_Start = new Date(); 
		mSceneTime_End = new Date();
		
		long start_msec = Math.round((double) begin_frame * 1000.0 / frameRate );
		long end_msec = Math.round((double) end_frame * 1000.0 / frameRate );
		
		mSceneTime_Start.setTime(videoTime_Start.getTime() + start_msec);
		mSceneTime_End.setTime(videoTime_Start.getTime() + end_msec);
		
		if (false)  // if (MseeFunction.bVerbose)
	    {
	    	System.out.println("\tDEBUG CEventData ComputeSceneCentricTime id:" + this.id);
			System.out.println("\tDEBUG CEventData ComputeSceneCentricTime frameRate:" + frameRate);
			
			System.out.println("\t\tDEBUG CEventData ComputeSceneCentricTime begin_frame :" + begin_frame);
			System.out.println("\t\tDEBUG CEventData ComputeSceneCentricTime end_frame :" + end_frame);		
			System.out.println("\t\tDEBUG CEventData ComputeSceneCentricTime mSceneTime_Start :" + mSceneTime_Start.toString());
			System.out.println("\t\tDEBUG CEventData ComputeSceneCentricTime mSceneTime_End :" + mSceneTime_End.toString());	
	    }
		
		/* debug
		System.out.println("DEBUG videoTime_Start " + videoTime_Start.toString());
		System.out.println("DEBUG start frame " + begin_frame + " end frame " + end_frame + " frame rate "+ frameRate);
		System.out.println("DEBUG mSceneTime_Start " + mSceneTime_Start.toString());
		System.out.println("DEBUG mSceneTime_End " + mSceneTime_End.toString());
		*/ 		
		
		for (CActionData data : this.actionDataVt)
		{	data.ComputeSceneCentricTime(videoTime_Start, frameRate);
		}	
		
	}

	public void ComputeSceneCentricLocation(Homography hView2Map, SimpleBoundingBox map_bbox) {
		
		for (CActionData data : this.actionDataVt)
		{	data.ComputeSceneCentricLocation(hView2Map, map_bbox);
		}			
		
	}

	public TimeData GetTimeData() {
		TimeData timeData = new TimeData();	
		
		timeData.SetViewFramePeriod(
				this.begin_frame,
				this.end_frame
				);
		if 	((this.mSceneTime_Start != null) &&
			(this.mSceneTime_End != null)) {
			timeData.SetSceneTimePeriod(this.mSceneTime_Start, this.mSceneTime_End);
		}
		return timeData;
	}

	public LocationData GetLocationData(LocationType location_type) {
		// TODO Auto-generated method stub
		
		if (this.actionDataVt.size() == 0)
		{
			return null;
		} 
		int index = actionDataVt.size()/2;		// pick center action
		return actionDataVt.elementAt(index).GetLocationData(location_type);
		
	}

	public void SubsamplePos(int i) {

		if (actionDataVt.size() < 5)
			return; 
		
		int size = actionDataVt.size() ;
		
		for (int j = size-3; j >=3; j-- )
		{
			if (j % i != 0)
			{
				actionDataVt.remove(j);
			}
			
		}	
		
	}
	
	public String GetObsId() {
		return this.observer_id;
	}	
	
	public String GetViewId() {
		return this.view_id;
	}
	
}
