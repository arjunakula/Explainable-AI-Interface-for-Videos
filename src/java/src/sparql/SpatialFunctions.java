package sparql;

import java.sql.Date;
import java.util.ArrayList;

import msee.online.OnlineRequestType;
import sparql.ColorFunctions.Color;
import sparql.LocationData.LocationType;
import sparql.Parsedata.View;
import TextParser.CObjectData;
import TextParser.CPositionData;

public class SpatialFunctions {

	static boolean warning_incompatible_type = false;
	static boolean warning_not_implemented = false;
	
	static double touching_expand_bbox = 0.2;
	
	static double  inside_outside_margin_bbox = 0.5;
	public static double action_Location_Margin = 1.0;
	
	
	public static double object_Location_Margin_carte = 0.1;   // 0.1  ... 0.128
	public static double object_Location_Margin_view = 00.0;
	public static double object_Location_Margin_view_default = 20.0;
	public static double object_Location_Margin_geo = 0.00015; // 0.001
	

	public static double Face_dot_product_threshold = 0.5;
	
	public static boolean IsAtLocation(LocationData loc1,
			LocationData loc2, double at_location_Margin) {
		// TODO Auto-generated method stub
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG SpatialFunctions IsAtLocation starting");
		}
	
		if ( loc1 == null )
		{	System.err.println("DEBUG ERROR SpatialFunctions IsAtLocation loc1 is null ");
			return true;
		}
		
		if ( loc2 == null)
		{	System.err.println("DEBUG ERROR SpatialFunctions IsAtLocation loc2 is null ");
			return true;
		}
		
		if ( LocationData.IsTypeComparable(loc1.location_type, loc2.location_type) == false)
		{	
			if (!warning_incompatible_type)
			{	System.err.println("WARN SpatialFunctions IsAtLocation incomparable type; return true for now.");
				warning_incompatible_type  = true;
			}
			return true;
		}
		
		if (false) // debug
		{
			System.err.println("DEBUG SpatialFunction IsAtLocation loc1 ");
			loc1.Printf();
	
			System.err.println("DEBUG SpatialFunction IsAtLocation loc2 ");
			loc2.Printf();
		}
		
		if (IsIntersect(loc1, loc2, at_location_Margin))
		{
			if ((MseeFunction.bVerbose))
			{
				System.out.println("DEBUG SpatialFunctions IsAtLocation return true" );
			}
			return true;
		}
		if ((MseeFunction.bVerbose))
		{
			System.out.println("DEBUG SpatialFunctions IsAtLocation return false" );
		}
		return false;
	}

	private static boolean IsIntersect(LocationData loc1, LocationData loc2, double margin) {
		// TODO Auto-generated method stub
		
		// TODO use enclosing box for now; 
		SimpleBoundingBox bbox1 = loc1.GetEnclosingBox();
		SimpleBoundingBox bbox2 = loc2.GetEnclosingBox();
		
		bbox2.Expand(margin);
		
		
		if (false)
		{
			System.out.println("debug IsIntersect: " );
			bbox1.Printf();
			bbox2.Printf();
		}
		
		return bbox1.IsIntersect(bbox2);		
	}

	public static Boolean IsPassing(
			TimeData time_data,			// optional  
			LocationData loc_data,		// optional 
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsPassing(time_data, loc_data, object_agent, object_patient).size() > 0;
			
		/* not implemented fully; this should be c++ instead
		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return false
			return false;
		}
		if (time_data != null)
		{
			// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest
			return false;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
			
		
		
		// get speed
		// check speed of agent is faster than object
		// check distance is small, relative to speed
		// for a set of sample before and after nearest distance
			// check that agent is getting closer from patient
			// check that agent is getting further from patient
		
		*/
	}

	public static Boolean IsSameMotion(
			TimeData time_data,
			LocationData loc_data, 
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsSameMotion(time_data, loc_data, object_agent, object_patient).size() > 0;
	}

	public static Boolean IsBelow(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		ArrayList<TimeData> time_list = GetTime_IsBelow(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}
	
	public static Boolean IsCarrying(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		ArrayList<TimeData> time_list = GetTime_IsCarrying(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}
	



	public static Boolean IsClearLineOfSight(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {
		return GetTime_IsClearLineOfSight(time_data, loc_data, object_agent, object_patient).size() > 0;
	}

	public static Boolean IsCloser(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object1, CObjectData object2) {
		return GetTime_IsCloser(time_data, loc_data, object_agent, object1, object2).size() > 0;
	}

	public static Boolean IsCrossing(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsCrossing(time_data, loc_data, object_agent, object_patient).size() > 0;
	}

	public static Boolean IsEntering(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {

		ArrayList<TimeData> time_list = GetTime_IsEntering(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsExiting(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		
		ArrayList<TimeData> time_list = GetTime_IsExiting(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsFacing(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {

		ArrayList<TimeData> time_list = GetTime_IsFacing(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsFacingOpposite(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {
		ArrayList<TimeData> time_list = GetTime_IsFacingOpposite(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsFarther(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object1, CObjectData object2) {
		return GetTime_IsFarther(time_data, loc_data, object_agent, object1, object2).size() > 0;
	}
	
	public static Boolean IsFollowing(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsFollowing(time_data, loc_data, object_agent, object_patient).size() > 0;
	}

	public static Boolean IsInside(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {

		ArrayList<TimeData> time_list = GetTime_IsInside(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}
	

	public static Boolean IsOccluding(TimeData time_data,
			LocationData loc_data, CObjectData object_agent, CObjectData object1, CObjectData object2) {
		return GetTime_IsOccluding(time_data, loc_data, object_agent, object1, object2).size() > 0;
	}

	public static Boolean IsOn(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		
		ArrayList<TimeData> time_list = GetTime_IsOn(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}
	
	

	public static Boolean IsOppositeMotion(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {
		return GetTime_IsOppositeMotion(time_data, loc_data, object_agent, object_patient).size() > 0;
	}
	

	public static Boolean IsOutside(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {

		ArrayList<TimeData> time_list = GetTime_IsOutside(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}
	
	public static Boolean IsSameObject(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {
		
		/*
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction IsSameObject not implemented");
			warning_not_implemented  = true;
		}
		*/
	
		return (object_agent.id.contentEquals(object_patient.id));
	}

	public static Boolean IsTogether(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsTogether(time_data, loc_data, object_agent, object_patient).size() > 0;
	}

	public static Boolean IsStarting(TimeData time_data, LocationData loc_data,
			CObjectData object_agent) {
		ArrayList<TimeData> time_list = GetTime_IsStarting(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsMakingUTurn(TimeData time_data,
			LocationData loc_data, CObjectData object_agent) {
		ArrayList<TimeData> time_list = GetTime_IsMakeUTurn(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsDriving(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsDriving(time_data, loc_data, object_agent, object_patient).size() > 0;
	}

	public static Boolean IsLoading(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsLoading(time_data, loc_data, object_agent, object_patient).size() > 0;
	}
	
	public static Boolean IsUnloading(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsUnloading(time_data, loc_data, object_agent, object_patient).size() > 0;
	}
	
	public static Boolean IsMounting(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsMounting(time_data, loc_data, object_agent, object_patient).size() > 0;
	}
	
	public static Boolean IsDismounting(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {
		return GetTime_IsDismounting(time_data, loc_data, object_agent, object_patient).size() > 0;
	}
	
	public static Boolean IsMoving(TimeData time_data, LocationData loc_data,
			CObjectData object_agent) {
		
		
		ArrayList<TimeData> time_list = GetTime_IsMoving(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);
	}

	public static Boolean IsStopping(TimeData time_data, LocationData loc_data,
			CObjectData object_agent) {
		ArrayList<TimeData> time_list = GetTime_IsStopping(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);
	}
	

	public static Boolean IsTouching(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, CObjectData object_patient) {

		ArrayList<TimeData> time_list = GetTime_IsTouching(time_data, loc_data,
				object_agent, object_patient);
				
		return (time_list.size() > 0);
	}


	public static Boolean IsTurning(TimeData time_data, LocationData loc_data,
			CObjectData object_agent) {
		ArrayList<TimeData> time_list = GetTime_IsTurning(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);		
	}

	public static Boolean IsTurningLeft(TimeData time_data,
			LocationData loc_data, CObjectData object_agent) {
		ArrayList<TimeData> time_list = GetTime_IsTurningLeft(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);	
	}

	public static Boolean IsTurningRight(TimeData time_data,
			LocationData loc_data, CObjectData object_agent) {
		ArrayList<TimeData> time_list = GetTime_IsTurningRight(time_data, loc_data,
				object_agent);
				
		return (time_list.size() > 0);	
	}
	

	public static ArrayList<TimeData> GetTime_IsBelow(TimeData time_data,
			LocationData loc_data, 
			CObjectData object_agent,
			CObjectData object_patient) 
	{
		long time_sample_msec = 500; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return list;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return list;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bIsBelow = false;
		
		Date valid_start = null;
		Date valid_end = null;		
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			int[] agent_contour = object_agent.GetViewContourAtNearestSceneTime(time_test);
			int[] patient_contour = object_patient.GetViewContourAtNearestSceneTime(time_test);
			
			boolean test_isBelow = IsBelow(agent_contour, patient_contour);
			if (test_isBelow)
			{
				if (bIsBelow)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bIsBelow = true; 					
				}				
			}	else
			{
				if (bIsBelow)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);
					
					bIsBelow = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	

		if (bIsBelow)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bIsBelow = false;
		} 
		
		
		
		return list;
	}

	private static boolean IsBelow(int[] agent_contour, int[] patient_contour) {
		
		if ((agent_contour == null) ||(agent_contour == null))
		{
			System.err.println("ERROR IsOn, argument is null");
			return false;
		}
		
		SimpleBoundingBox agent_box = new SimpleBoundingBox(agent_contour);
		SimpleBoundingBox patient_box = new SimpleBoundingBox(patient_contour);
		
		if ((agent_box.IsIntersect_InX(patient_box)) &&
			(agent_box.GetCenterY() > patient_box.GetCenterY()))
		{
			return true;
		}
		return false;			
	}

	public static ArrayList<TimeData> GetTime_IsCarrying(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
		
		
	}

	public static ArrayList<TimeData> GetTime_IsClearLineOfSight(
			TimeData time_data, LocationData loc_data, CObjectData objectData,
			CObjectData objectData2) {
		
		//System.err.println("DEBUG SpatialFunction GetTime_IsClearLineOfSight running");
		
		// if got parent, then use parent
		
		
		ArrayList<TimeData> time_list =  OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_CLOS, 
				time_data, loc_data, objectData, objectData2 );
		if (time_list.isEmpty())
		{
			//System.err.println("DEBUG SpatialFunction GetTime_IsClearLineOfSight try with parents");
			
			boolean bParent_found = false;
			
			CObjectData new_object_1=objectData;
			CObjectData new_object_2=objectData2;
			
			// try parent
			String current_id_1 = objectData.id;
			while (true)
			{
				//System.err.println("\t DEBUG SpatialFunction GetTime_IsClearLineOfSight current_id_1 " + current_id_1);
				
				
				String parent_id_1 = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(current_id_1);
				if (parent_id_1 == null)
				{	break;					
				}	
				//System.err.println("\t DEBUG SpatialFunction GetTime_IsClearLineOfSight parent_id_1 " + parent_id_1);
				
				
				CObjectData tmp_object_1  = MseeDataset.FindObjectData(parent_id_1);
				if (tmp_object_1 == null)
				{	break;
				}
				
				//System.err.printf("DEBUG SpatialFunction GetTime_IsClearLineOfSight 1 FOUND PARENT " + current_id_1 + " parent " +parent_id_1 );
				
				new_object_1 = tmp_object_1;
				current_id_1 = parent_id_1;		
				bParent_found = true;									
			}
			
			String current_id_2 = objectData2.id;
			while (true)
			{

				//System.err.println("\t DEBUG SpatialFunction GetTime_IsClearLineOfSight current_id_2 " + current_id_2);
				
				
				String parent_id_2 = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(current_id_2);
				if (parent_id_2 == null)
				{	break;					
				}	
				
				
				//System.err.println("\t DEBUG SpatialFunction GetTime_IsClearLineOfSight parent_id_2 " + parent_id_2);
				
				
				CObjectData tmp_object_2  = MseeDataset.FindObjectData(parent_id_2);
				if (tmp_object_2 == null)
				{	break;
				}
				//System.err.printf("DEBUG SpatialFunction GetTime_IsClearLineOfSight 1 FOUND PARENT " + current_id_2 + " parent " +parent_id_2 );
				
				new_object_2 = tmp_object_2;
				current_id_2 = parent_id_2;		
				bParent_found = true;									
			}
			
			
			if (bParent_found)
			{	
				System.out.printf("DEBUG SpatialFunction GetTime_IsClearLineOfSight 2 RETRY WITH PARENT ");			
				time_list =  OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_CLOS, time_data, loc_data, new_object_1, new_object_2);
			}		
		}
		
		return time_list;
		
	}

	public static ArrayList<TimeData> GetTime_IsCloser(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2, CObjectData cObjectData3) {
		return OnlineComputingInterface.getInterface().OnlineRequestTrinary( OnlineRequestType.ONLINE_TRI_CLOSER, time_data, loc_data, cObjectData, cObjectData2, cObjectData3 );
	}

	public static ArrayList<TimeData> GetTime_IsCrossing(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		// Note: We assume that loc_data will encode the road contour; cObjectData2 is ignored.
		
		LocationData loc_patient  = null;
		if (time_data!= null)
		{
			CPositionData nearestPosition = cObjectData2.GetPositionDataAtNearestSceneTime(time_data.mSceneTime_Start, false);
			if ( nearestPosition != null ) {
				loc_patient = new LocationData( LocationType.VIEW_CENTRIC_POLYGON, nearestPosition, cObjectData.GetViewId() );				
			}
		}
		
		return OnlineComputingInterface.getInterface().OnlineRequestUnary( OnlineRequestType.ONLINE_UNI_CROSSING, time_data, loc_patient, cObjectData );
	}

	public static ArrayList<TimeData> GetTime_IsDriving(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_DRIVING, time_data, loc_data, cObjectData, cObjectData2 );
	}
	
	public static ArrayList<TimeData> GetTime_IsLoading(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_LOADING, time_data, loc_data, cObjectData, cObjectData2 );
	}
	
	public static ArrayList<TimeData> GetTime_IsUnloading(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_UNLOADING, time_data, loc_data, cObjectData, cObjectData2 );
	}
	
	public static ArrayList<TimeData> GetTime_IsMounting(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_MOUNTING, time_data, loc_data, cObjectData, cObjectData2 );
	}
	
	public static ArrayList<TimeData> GetTime_IsDismounting(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_DISMOUNTING, time_data, loc_data, cObjectData, cObjectData2 );
	}

	public static ArrayList<TimeData> GetTime_IsEntering(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {
		
		long time_sample_msec = 100; //500; 
		long time_gap_msec = 1000; //500; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return list;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return list;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
	
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec - time_sample_msec ; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test1 = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			Date   time_test2 = new Date(time_overlap.mSceneTime_Start.getTime() + Math.min(time_diff_msec,time_offset_msec + time_gap_msec) );
						
			LocationData agent_loc1 = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test1);
			LocationData patient_loc1 = object_patient.GetGroundPolygonAtNearestSceneTime(time_test1);
			
			LocationData agent_loc2 = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test2);
			LocationData patient_loc2 = object_patient.GetGroundPolygonAtNearestSceneTime(time_test2);
			
			
			if ((agent_loc1!= null) &&
					(patient_loc1!= null) &&
					(agent_loc2!= null) &&
					(patient_loc2!= null))
				{
				
					boolean b_outside = IsOutside(agent_loc1, patient_loc1);
					boolean b_inside = IsInside(agent_loc2, patient_loc2);
				
					/*
					 * debug *
					agent_loc1.Printf();
					agent_loc2.Printf();
					patient_loc1.Printf();
					patient_loc2.Printf();
				
			
					
					
				
					System.out.println("b_outside " + b_outside);
					System.out.println("b_inside " + b_inside);
						System.out.println("");
						/**/
					
				if (( b_outside ) &&
					( b_inside ) )
				{		
					// outside first, then become inside
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(time_test1, time_test2);
					list.add(new_valid_interval);
					
					System.out.println("IsEntering true ");
				}
			}
		}	
		
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsExiting(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {

		long time_sample_msec = 100; //500; 
		long time_gap_msec = 500; //500; 
		
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return list;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return list;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
	
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec - time_sample_msec ; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test1 = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			Date   time_test2 = new Date(time_overlap.mSceneTime_Start.getTime() + Math.min(time_diff_msec,time_offset_msec + time_gap_msec) );
				

			LocationData agent_loc1 = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test1);
			LocationData patient_loc1 = object_patient.GetGroundPolygonAtNearestSceneTime(time_test1);
			
			LocationData agent_loc2 = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test2);
			LocationData patient_loc2 = object_patient.GetGroundPolygonAtNearestSceneTime(time_test2);
			
			if ((agent_loc1!= null) &&
			(patient_loc1!= null) &&
			(agent_loc2!= null) &&
			(patient_loc2!= null))
			{
				
				if (( IsInside(agent_loc1, patient_loc1) ) &&
					( IsOutside(agent_loc2, patient_loc2) ) )
				{		
					// outside first, then become inside
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(time_test1, time_test2);
					list.add(new_valid_interval);
				}
			}
		}	
		
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsFacing(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {
		
		long time_sample_msec = 500; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_overlap = GetSceneTimeOverlap(object_agent, object_patient, time_data);
				
		if (time_overlap == null)
			return list;
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bValid = false;
		
		Date valid_start = null;
		Date valid_end = null;		
		
		boolean bHasAgentOrient = false; 
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			LocationData agent_loc = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test);
			OrientationData agent_orient = object_agent.GetOrientationDataAtNearestSceneTime(time_test);			
			LocationData patient_loc = object_patient.GetGroundPosAtSceneTimeWithInterpolation(time_test);
			OrientationData patient_orient = object_patient.GetOrientationDataAtNearestSceneTime(time_test);	
			
			if (agent_orient == null)
			{
				// we need agent_orient to be set, before we can determine facing
				//  patient_orient can be null, e.g. door		
				

				continue;
			}
			
			bHasAgentOrient  = true; 
			
			boolean test_isOn = IsFacing(agent_loc, agent_orient, patient_loc, patient_orient);
			
			if (test_isOn)
			{
				if (bValid)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bValid = true; 					
				}				
			}	else
			{
				if (bValid)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);
					
					bValid = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		} // for
		
		if (bHasAgentOrient == false)
		{
			System.err.println("WARN SpatialFunction GetTime_IsFacing agent_orient is always null, id "  + object_agent.id);
		}	
		
		if (bValid)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bValid = false;
		} 
		
		
		
		return list;
	}

	// note: only works for CARTESIAN_METRIC_POINT locations
	private static boolean IsFacing(LocationData agent_loc,
			OrientationData agent_orient, LocationData patient_loc,
			OrientationData patient_orient) {
		
		if (agent_orient == null)
		{
			System.err.print("ERROR IsFacing: agent_orient is null");
			return false; 
		}	
			
		if (agent_loc.location_type!= LocationType.CARTESIAN_METRIC_POINT)
		{
			System.err.print("ERROR IsFacing: agent_loc is not CARTESIAN_METRIC_POINT"); 
			return false; 
		}
		if (patient_loc.location_type!= LocationType.CARTESIAN_METRIC_POINT)
		{
			System.err.print("ERROR IsFacing: patient_loc is not CARTESIAN_METRIC_POINT");
			return false; 
		}
		
		// check if agent is facing patient
		boolean bValid = IsFacing(agent_loc.cartesian_metric_point, agent_orient, patient_loc.cartesian_metric_point);
		
		if (bValid == false)
		{
			return false; 			
		}
			
				
		if (patient_orient == null)
		{
			// if patient has no orientation (e.g. door), then ignore when patient is facing agent 
			return bValid;
		}
		
		// check if patient is facing agent
		bValid = IsFacing(patient_loc.cartesian_metric_point, patient_orient, agent_loc.cartesian_metric_point);
		
		return bValid;	
		
	}

	private static boolean IsFacing(
			CartesianMetricPoint point1,
			OrientationData orient1,
			CartesianMetricPoint point2) {
		
		if ((point1 == null) ||
			(point2 == null) ||
			(orient1 == null))
		{
			System.err.printf("ERROR IsFacing: argument is null");
		}
				
		OrientationData  orient2 = new OrientationData(point2.x - point1.x, point2.y - point1.y);
		
		double dot = orient1.DotProduct(orient2);
		
		
		return (dot > Face_dot_product_threshold);
	}

	public static ArrayList<TimeData> GetTime_IsFacingOpposite(
			TimeData time_data, 
			LocationData loc_data, 
			CObjectData object_agent,
			CObjectData object_patient) {

		long time_sample_msec = 500; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_overlap = GetSceneTimeOverlap(object_agent, object_patient, time_data);
				
		if (time_overlap == null)
		{
			return list;
		}
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bValid = false;
		
		Date valid_start = null;
		Date valid_end = null;		
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			LocationData agent_loc = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test);
			OrientationData agent_orient = object_agent.GetOrientationDataAtNearestSceneTime(time_test);			
			LocationData patient_loc = object_patient.GetGroundPosAtSceneTimeWithInterpolation(time_test);
			OrientationData patient_orient = object_patient.GetOrientationDataAtNearestSceneTime(time_test);	
			
			if (agent_orient == null)
			{
				// we need agent_orient to be set, before we can determine facing
				//  patient_orient can be null, e.g. door				
				continue;
			}
			
			boolean test_isOn = IsFacingOpposite(agent_loc, agent_orient, patient_loc, patient_orient);
			
			if (test_isOn)
			{
				if (bValid)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bValid = true; 					
				}				
			}	else
			{
				if (bValid)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);
					
					bValid = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	
		
		if (bValid)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bValid = false;
		} 
		
		
		return list; 
	}

	private static boolean IsFacingOpposite(LocationData agent_loc,
			OrientationData agent_orient, LocationData patient_loc,
			OrientationData patient_orient) {
		
		if (agent_orient == null)
		{
			System.err.print("ERROR IsFacingOpposite: agent_orient is null");
			return false; 
		}	
			
		if (agent_loc.location_type!= LocationType.CARTESIAN_METRIC_POINT)
		{
			System.err.print("ERROR IsFacingOpposite: agent_loc is not CARTESIAN_METRIC_POINT"); 
			return false; 
		}
		if (patient_loc.location_type!= LocationType.CARTESIAN_METRIC_POINT)
		{
			System.err.print("ERROR IsFacingOpposite: patient_loc is not CARTESIAN_METRIC_POINT");
			return false; 
		}
		
		// check if agent is facing opposite patient
		boolean bValid = IsFacingOpposite(agent_loc.cartesian_metric_point, agent_orient, patient_loc.cartesian_metric_point);
		
		if (bValid == false)
		{
			return false; 			
		}
			
				
		if (patient_orient == null)
		{
			// if patient has no orientation (e.g. door), then ignore when patient is facing agent 
			return bValid;
		}
		
		// check if patient is facing opposite agent
		bValid = IsFacingOpposite(patient_loc.cartesian_metric_point, patient_orient, agent_loc.cartesian_metric_point);
		
		return bValid;	
	}

	private static boolean IsFacingOpposite(
			CartesianMetricPoint point1,
			OrientationData orient1,
			CartesianMetricPoint point2) {
		
		if ((point1 == null) ||
				(point2 == null) ||
				(orient1 == null))
			{
				System.err.printf("ERROR IsFacingOpposite: argument is null");
			}
					
			OrientationData  orient2 = new OrientationData(point2.x - point1.x, point2.y - point1.y);
			
			double dot = orient1.DotProduct(orient2);
						
			return (dot < 0.0);
	}

	public static ArrayList<TimeData> GetTime_IsFarther(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2, CObjectData cObjectData3) {
		return OnlineComputingInterface.getInterface().OnlineRequestTrinary( OnlineRequestType.ONLINE_TRI_FARTHER, time_data, loc_data, cObjectData, cObjectData2, cObjectData3 );
	}

	public static ArrayList<TimeData> GetTime_IsFollowing(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_FOLLOWING, time_data, loc_data, cObjectData, cObjectData2 );
	}

	public static ArrayList<TimeData> GetTime_IsInside(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {


		long time_sample_msec = 100; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return list;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return list;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bInside = false;
		
		Date valid_start = null;
		Date valid_end = null;		
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			LocationData agent_loc = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test);
			LocationData patient_loc = object_patient.GetGroundPolygonAtNearestSceneTime(time_test);
			
			boolean test_isInside = IsInside(agent_loc, patient_loc);
			if (test_isInside)
			{
				if (bInside)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bInside = true; 					
				}				
			}	else
			{
				if (bInside)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);
					
					bInside = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	
		
		if (bInside)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bInside = false;
		} 
		
		
		
		return list;
	}

	
	// check if a cartesian_metric_point  is inside a  cartesian_metric_polygon
	private static boolean IsInside(LocationData agent_loc,
			LocationData patient_loc) {
	
		if (agent_loc.cartesian_metric_point == null)
		{
			System.err.println("ERROR IsInside  agent_loc.cartesian_metric_point == null");
			return false; 	
		}
		
		if (patient_loc.cartesian_metric_polygon == null)
		{
			System.err.println("ERROR IsInside  patient_loc.cartesian_metric_polygon == null");
			return false; 			
		}
		
		
		SimpleBoundingBox bbox1 = patient_loc.GetEnclosingBox();
		bbox1.Expand(inside_outside_margin_bbox);  // make bigger
		//bbox1.Printf();
		
		if ((agent_loc.cartesian_metric_point.x >= bbox1.x1) &&
			(agent_loc.cartesian_metric_point.x <= bbox1.x2) &&
			(agent_loc.cartesian_metric_point.y >= bbox1.y1) &&
			(agent_loc.cartesian_metric_point.y <= bbox1.y2))
		{
			return true;
		}
			
		return false;
	}

	
	public static ArrayList<TimeData> GetTime_IsMakeUTurn(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsMoving(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData) {
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction GetTime_IsMoving not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;		
	}

	public static ArrayList<TimeData> GetTime_IsOccluding(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2, CObjectData cObjectData3) {
		return OnlineComputingInterface.getInterface().OnlineRequestTrinary( OnlineRequestType.ONLINE_TRI_OCCLUDING, time_data, loc_data, cObjectData, cObjectData2, cObjectData3 );
	}

	public static ArrayList<TimeData> GetTime_IsOn(TimeData time_data,
			LocationData loc_data, 
			CObjectData object_agent,
			CObjectData object_patient) {		
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_overlap = GetSceneTimeOverlap(object_agent, object_patient, time_data);
		
		if (time_overlap ==null)
			return list;
		
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		//System.out.println("DEBUG GetTime_IsOn time_diff_msec " + time_diff_msec);
		
		boolean bOn = false;
		
		Date valid_start = null;
		Date valid_end = null;	
		
		// long time_sample_msec = 500; 
		
		int num_sample = 10; 
		long time_sample_msec = time_diff_msec / num_sample;
		time_sample_msec  = Math.max(500,  time_sample_msec ); 
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			int[] agent_contour = object_agent.GetViewContourAtNearestSceneTime(time_test);
			int[] patient_contour = object_patient.GetViewContourAtNearestSceneTime(time_test);
			
			boolean test_isOn = IsOn(agent_contour, patient_contour);
			
			//System.out.println("\t DEBUG GetTime_IsOn time_offset_msec "
			//		+ time_offset_msec + " test_isOn " + test_isOn);	
			
			
			if (test_isOn)
			{
				if (bOn)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bOn = true; 					
				}				
			}	else
			{
				if (bOn)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);		
					
					bOn = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	

		if (bOn)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bOn = false;
		} 
		
		//System.out.println("DEBUG GetTime_IsOn ending list size " + list.size());
		
		return list;
	}

	
	
	

	public static ArrayList<TimeData> GetTime_IsClosed0_Open1(TimeData time_data,
			LocationData loc_data, 
			CObjectData object_agent,
			int a_iClosed0_Open1) {		
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_overlap = GetSceneTimeOverlap(object_agent, time_data);
		
		if (time_overlap ==null)
			return list;
		
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bResult = false;
		
		Date valid_start = null;
		Date valid_end = null;	
		
		// long time_sample_msec = 500; 
		
		int num_sample = 10; 
		long time_sample_msec = time_diff_msec / num_sample;
		time_sample_msec  = Math.max(500,  time_sample_msec ); 
		time_sample_msec  = Math.min(5000,  time_sample_msec ); 
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			CPositionData pos_data = object_agent.GetPositionDataAtNearestSceneTime(time_test, false);
			
			boolean test_isTrue = (pos_data.iClosed0_Open1 ==a_iClosed0_Open1);
			
			if (test_isTrue)
			{
				if (bResult)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bResult = true; 					
				}				
			}	else
			{
				if (bResult)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);		
					
					bResult = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	

		if (bResult)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bResult = false;
		} 
		
		
		return list;
	}

	
	// get time of overlap between two object, and with an optional time data ;
	// return null if there is no overlap
	private static TimeData GetSceneTimeOverlap(CObjectData object_1,
			CObjectData object_2, TimeData time_data) {		

		TimeData time_overlap = null;
		
		TimeData time_agent = object_1.GetTimeData();
		TimeData time_patient = object_2.GetTimeData();
		
		time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return null;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return null;
		}
		
		return time_overlap.clone();
	}
	
	

	// get time of overlap between an object, and with an optional time data ;
	// return null if there is no overlap
	private static TimeData GetSceneTimeOverlap(CObjectData object_1,
			TimeData time_data)
	{		

		TimeData time_overlap = null;
		
		TimeData time_agent = object_1.GetTimeData();
		
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_data);
		} else
		{
			time_overlap = time_agent;
		}
		
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return null;
		}
		
		return time_overlap.clone();
	}


	// determine if a contour is above another. These are view-based contour
	private static boolean IsOn(int[] agent_contour, int[] patient_contour) {
				
		if ((agent_contour == null) ||(agent_contour == null))
		{
			System.err.println("ERROR IsOn, argument is null");
			return false;
		}
		
		SimpleBoundingBox agent_box = new SimpleBoundingBox(agent_contour);
		SimpleBoundingBox patient_box = new SimpleBoundingBox(patient_contour);
		/*
		System.out.println("DEBUG SpatialFunction IsOn agent_box \t " );
		agent_box.Printf();
		System.out.println("DEBUG SpatialFunction IsOn patient_box \t " );
		patient_box.Printf();
		*/ 
		
		boolean b = false;
		if ((agent_box.IsIntersect_InX(patient_box)) &&
			(agent_box.GetCenterY() < patient_box.GetCenterY()))
		{
			b = true;
		}
		//System.out.println("DEBUG SpatialFunction IsOn ending \t "  + b);
			
		return b;
			
		
		
	}

	public static ArrayList<TimeData> GetTime_IsOppositeMotion(
			TimeData time_data, LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_OPPOSITE_MOTION, time_data, loc_data, cObjectData, cObjectData2 );
	}

	public static ArrayList<TimeData> GetTime_IsOutside(TimeData time_data,
			LocationData loc_data, CObjectData object_agent,
			CObjectData object_patient) {

		long time_sample_msec = 100; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return list;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return list;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bOutside = false;
		
		Date valid_start = null;
		Date valid_end = null;		
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			LocationData agent_loc = object_agent.GetGroundPosAtSceneTimeWithInterpolation(time_test);
			LocationData patient_loc = object_patient.GetGroundPolygonAtNearestSceneTime(time_test);
			
			boolean test_isOutside = IsOutside(agent_loc, patient_loc);
			if (test_isOutside)
			{
				if (bOutside)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bOutside = true; 					
				}				
			}	else
			{
				if (bOutside)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);
					
					bOutside = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	
		
		if (bOutside)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bOutside = false;
		} 
		
		return list;
	}


	// check if a cartesian_metric_point  is outside a  cartesian_metric_polygon
	private static boolean IsOutside(LocationData agent_loc,
			LocationData patient_loc) {
	
		if (agent_loc.cartesian_metric_point == null)
		{
			System.err.println("ERROR IsOutside  agent_loc.cartesian_metric_point == null");
			return false; 	
		}
		
		if (patient_loc.cartesian_metric_polygon == null)
		{
			System.err.println("ERROR IsOutside  patient_loc.cartesian_metric_polygon == null");
			return false; 			
		}
		
		
		SimpleBoundingBox bbox1 = patient_loc.GetEnclosingBox();
		bbox1.Subtract(inside_outside_margin_bbox);		// make smaller
		//bbox1.Printf();
		
		if ((agent_loc.cartesian_metric_point.x < bbox1.x1) ||
			(agent_loc.cartesian_metric_point.x > bbox1.x2) ||
			(agent_loc.cartesian_metric_point.y < bbox1.y1) ||
			(agent_loc.cartesian_metric_point.y > bbox1.y2))
		{
			return true;
		}
			
		return false;
	}

	
	public static ArrayList<TimeData> GetTime_IsPassing(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_PASSING, time_data, loc_data, cObjectData, cObjectData2 );
	}

	public static ArrayList<TimeData> GetTime_IsSameMotion(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_SAME_MOTION, time_data, loc_data, cObjectData, cObjectData2 );
	}

	public static ArrayList<TimeData> GetTime_IsSameObject(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsStarting(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsStopping(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsTogether(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData,
			CObjectData cObjectData2) {
		return OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_TOGETHER, time_data, loc_data, cObjectData, cObjectData2 );
	}
	

	public static ArrayList<TimeData> GetTime_IsTouching(TimeData time_data,
			LocationData loc_data, 
			CObjectData object_agent,
			CObjectData object_patient) {

		long time_sample_msec = 500; 
		
		ArrayList<TimeData> list = new ArrayList<TimeData>();			

		TimeData time_agent = object_agent.GetTimeData();
		TimeData time_patient = object_patient.GetTimeData();
		
		TimeData time_overlap = TimeFunctions.GetSceneTimeIntersect(time_agent, time_patient);
		if (time_overlap == null)
		{	// no time overlap between two objects, return empty list
			return list;
		}
		if (time_data != null)
		{	// get overlap with time of interest 
			time_overlap = TimeFunctions.GetSceneTimeIntersect(time_overlap.clone(), time_data);
		}
		if (time_overlap == null)
		{	// no overlap with time of interest,  return empty 
			return list;
		}
		
		// get time of nearest distance between objects
		long time_diff_msec = time_overlap.GetSceneTimeInterval_Msec();
		
		boolean bTouching = false;
		
		Date valid_start = null;
		Date valid_end = null;		
		/*
		System.out.println("DEBUG SpatialFunction  GetTime_IsTouching time_diff_msec " + time_diff_msec);
		System.out.println("DEBUG SpatialFunction  GetTime_IsTouching object_agent id  " + object_agent.id);
		System.out.println("DEBUG SpatialFunction  GetTime_IsTouching object_patient id  " + object_patient.id);
		*/
		
		for (long time_offset_msec = 0; time_offset_msec <= time_diff_msec; time_offset_msec += time_sample_msec)
		{
			
			Date   time_test = new Date(time_overlap.mSceneTime_Start.getTime() + time_offset_msec );
			
			int[] agent_contour = object_agent.GetViewContourAtNearestSceneTime(time_test);
			int[] patient_contour = object_patient.GetViewContourAtNearestSceneTime(time_test);
			
			boolean test_isTouching = IsTouching(agent_contour, patient_contour);
			

			
		//	System.out.println("\t DEBUG SpatialFunction time_diff_msec GetTime_IsTouching  test_isTouching " + test_isTouching);
			
			if (test_isTouching)
			{
				if (bTouching)
				{
					valid_end = time_test;			// extend the current valid time interval		
				} else
				{
					valid_start = time_test;		// start a new valid time interval
					valid_end = time_test;	
					bTouching = true; 					
				}				
			}	else
			{
				if (bTouching)
				{	// close current valid time
					
					TimeData new_valid_interval = new TimeData();
					new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
					list.add(new_valid_interval);
					
					bTouching = false;
				} else
				{	// still invalid, do nothing
				}					
			}
		}	

		if (bTouching)
		{	// close current valid time			
			TimeData new_valid_interval = new TimeData();
			new_valid_interval.SetSceneTimePeriod(valid_start, valid_end);
			list.add(new_valid_interval);			
			bTouching = false;
		} 
		
		//System.out.println("DEBUG SpatialFunction  GetTime_IsTouching list.size  " + list.size());
		
		
		return list;
	}

	// determine if a contour is touching another. These are view-based contour
	private static boolean IsTouching(int[] agent_contour, int[] patient_contour) {
				
		if ((agent_contour == null) ||(agent_contour == null))
		{
			System.err.println("ERROR IsTouching, argument is null");
			return false;
		}
		
		SimpleBoundingBox agent_box = new SimpleBoundingBox(agent_contour);
		SimpleBoundingBox patient_box = new SimpleBoundingBox(patient_contour);
		
		/*
		System.out.println("DEBUG SpatialFunction IsTouching agent_box \t " );
		agent_box.Printf();
		System.out.println("DEBUG SpatialFunction IsTouching patient_box \t " );
		patient_box.Printf();
		*/
		
		agent_box.Expand(touching_expand_bbox * Math.min(agent_box.GetWidth(), agent_box.GetHeight()));
		patient_box.Expand(touching_expand_bbox * Math.min(patient_box.GetWidth(), patient_box.GetHeight()));
		
			/*	
		System.out.println("DEBUG SpatialFunction IsTouching EXPANDED agent_box \t " );
		agent_box.Printf();
		System.out.println("DEBUG SpatialFunction IsTouching EXPANDED  patient_box \t " );
		patient_box.Printf();
		*/
		
		boolean b = false;
		
		if (agent_box.IsIntersect(patient_box))
		{
			b=  true;
		}
		

		//System.out.println("DEBUG SpatialFunction IsTouching b  \t "  + b);
		
		return b;		
		
	}
	


	public static ArrayList<TimeData> GetTime_IsTurning(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsTurningLeft(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsTurningRight(
			TimeData time_data, LocationData loc_data, CObjectData cObjectData) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static ArrayList<TimeData> GetTime_IsColor(TimeData time_data,
			LocationData loc_data, CObjectData cObjectData, Color color_type) {
		// TODO Auto-generated method stub
		if (!warning_not_implemented)
		{	System.err.println("WARN SpatialFunction not implemented");
			warning_not_implemented  = true;
		}
		ArrayList<TimeData> list = new ArrayList<TimeData>();
		return list;
	}

	public static Boolean IsClearLineOfSight_WithObserver(TimeData time_data,
			LocationData loc_data, String id_observer, String a_id_patient) {
		// TODO Auto-generated method stub
		
		String id_patient = MseeDataset.RemoveNamespace(a_id_patient);
		
		//System.out.println("\tDEBUG IsClearLineOfSight_WithObserver id_patient " + id_patient );
		
		boolean bViewfound = false;
		
		for (View view :MseeDataset.parsedDataDescriptor.views 	)
		{
			if (view.GetObserverId().compareTo(id_observer) != 0)
				continue; 
			
			bViewfound = true;
			
			System.out.println("DEBUG IsClearLineOfSight_WithObserver found view id_observer " + id_observer);
			
			CObjectData objectdata = view.FindObjectData(id_patient);
			
			if (objectdata == null)
			{	//System.out.println("DEBUG IsClearLineOfSight_WithObserver objectdata not found " + id_patient);
				continue;
			}
			//System.out.println("DEBUG IsClearLineOfSight_WithObserver objectdata found " + id_patient);
			
			if (loc_data != null)
			{	
				if (MseeDataset.IsObjectAtLocation(objectdata, loc_data) == false)
					continue; 
			}
			
			if (time_data != null)
			{
				if (MseeDataset.IsObjectAtTime(objectdata, time_data) == false)
				{	//System.out.println("\tDEBUG IsClearLineOfSight_WithObserver IsObjectAtTime fails " );
				
					continue; 
				}
				/*
				long time_diff_msec = objectdata.GetTimeDifference_NearestSceneTime(time_data);
				
				System.out.println("\tDEBUG IsClearLineOfSight_WithObserver time_diff_msec " + time_diff_msec);
								
				if (Math.abs(time_diff_msec) < 1000)
				{
					return true; 
				}*/
				
				return true;
				
			} else
			{
				// no time condition, return true; 
				return true;
			}		
			
		}	
		

		if (!bViewfound)
		{
			System.err.println("WARN IsClearLineOfSight_WithObserver  view id_observer NOT FOUND " + id_observer);
		}
		
		
		return false;
	}

	public static Boolean IsClosed(TimeData time_data, LocationData loc_data,
			CObjectData object_agent) {

		ArrayList<TimeData> time_list = GetTime_IsClosed0_Open1(time_data, loc_data,
				object_agent, 0);		
		return (time_list.size() > 0);
	}
	
	public static Boolean IsOpen(TimeData time_data, LocationData loc_data,
			CObjectData object_agent) {

		ArrayList<TimeData> time_list = GetTime_IsClosed0_Open1(time_data, loc_data,
				object_agent, 1);
		
		return (time_list.size() > 0);
	}

}
