package sparql;

import java.util.ArrayList;

import sparql.LocationData.LocationType;
import sparql.MseeFunction.ArgType;
import sparql.Parsedata.Homography;
import sparql.Parsedata.ParsedDataDescriptor;
import sparql.Parsedata.View;
import TextParser.CActionData;
import TextParser.CEventData;
import TextParser.CObjectData;
import TextParser.CPositionData;

public class MseeDataset 
{	
	public static ParsedDataDescriptor parsedDataDescriptor = new ParsedDataDescriptor();

	/**
	 * The parameters below are used as follows, when determining whether a non-stationary object that was observed at times [t0, t1, ..., tk] (potentially
	 * subsampled) or an event that is observed between times t0 and tk exists within the time interval [ts, tf] specified in a query.
	 * For ordinary queries (that are not object specifications):
	 * If tf - ts < min_unmodified_time_interval, then time_margin is subtracted from ts and added to tf, to form ts' and tf'.
	 * If tf' < t0 or ts' > tk, then the object or event is said to not exist at the time [ts, tf].
	 * Otherwise, for events, the event is said to exist at the time [ts, tf], because we assume that the event's duration is uninterrupted within [t0, tk].  For objects, 
	 * which may disappear and then reappear within [t0, tk],
	 * time_margin_internal is subtracted from ts' and added to tf', to form ts'' and tf''.  This helps to deal with internal gaps in object trajectories.
	 * If there exists at least one i (for 0 <= i <= k) such that ts'' <= ti <= tf'', then the object is said to exist at the time [ts, tf].
	 * For object specification queries:
	 * time_margin_object_spec is always subtracted from ts and added to tf (where ts == tf is expected to hold); time_margin_internal is never used.  Apart from that, whether an object
	 * is said to exist at the time [ts, tf] is determined as described above.
	 * Note: Given that our frame rate (in frames per second) is f, and our sampling rate (as specified by the subsample parameter in View) is s,
	 * our time resolution (the amount of time between two consecutive frame samples) is s / f.  Then, time_margin_object_spec
	 * should be at least s / 2f (otherwise, if [ts, tf] is between two frames, then neither frame might overlap with it).
     * All this assumes, of course, that times are represented precisely, and that there is no major roundoff error (e.g., to the nearest second).
	 */
		
	public static int min_unmodified_time_interval = 1000;  // 1000
	
	public static int time_margin = 500; //  500;
	
	public static int time_margin_internal = 1000;   // 15000
	
	
	public static int time_margin_object_spec_default = 1000;
	
	public static int time_margin_object_spec = time_margin_object_spec_default;
	
	public static TimeData lastConditionTime = null;		// record the last condition time used. 
	
		
	// munwai 20140816: COMMENT OUT; not used at all
	// public static ArrayList<String> listObjectNotFound = new ArrayList<String>();   
	// public static ArrayList<String> listEventNotFound = new ArrayList<String>();   
	
	static boolean DEBUG_THIS = false;
	
	public MseeDataset() { }	
	
	
	public static boolean IsEntityAtTime(
			MseeFunction.ArgType entityType,		// entity
			String	entityStr,
			MseeFunction.ArgType timeType,			// condition 
			String	timeStr			
			) throws MseeException
	{
		
		TimeData conditionTime = GetTimeData(timeType, timeStr);
		
		
		
		if (conditionTime != null)
		{
			
			lastConditionTime = conditionTime;
			
			if (conditionTime.hasValidSceneTime() )
			{
				if (MseeFunction.objectSpecQuery)
				{
					conditionTime.AddMargin_msec(time_margin_object_spec);
				}
				else if (conditionTime.GetSceneTimeInterval_Msec() < min_unmodified_time_interval)
				{
					conditionTime.AddMargin_msec(time_margin);
				}
			} 
		}
		
		if (conditionTime == null)
		{
			System.err.println("DEBUG MseeDataset IsEntityAtTime conditionTime is null : "+timeStr);
			return false;	
		}

		
		switch (entityType)
		{
			case OBJECT_ID:
				 // 
				 return MseeDataset.IsObjectAtTime(entityStr, conditionTime);
				 
			case EVENT_ID:
				return MseeDataset.IsEventAtTime(entityStr, conditionTime);
			case ENTITY_ID:
				
				return MseeDataset.IsObjectOrEventAtTime(entityStr, conditionTime);
					 				
			default:
				System.err.println("ERROR MseeDataset IsEntityAtTime invalid entityType " + entityType.toString());
				//throw new MseeException("DEBUG ERROR GetTimeData invalid argtype " + argType.toString());		
				return false; 
		}
		
		
		
	}
	


	private static boolean IsEventAtTime(String event_id,
			TimeData conditionTime) {
		
				
		ArrayList<CEventData> event_list = MseeDataset.FindEventDataList(MseeDataset.RemoveNamespace(event_id));
		
		// System.err.println("DEBUG MseeDataset IsEventAtTime event_list.size()"  + event_list.size());
		// System.err.print("\tDEBUG MseeDataset IsEventAtTime conditionTime \t" );
		// conditionTime.PrintTimeData();
		
		
		if (event_list.size()==0)
		{
			System.out.println("WARN IsEventAtTime event not found; return true. Event id "+event_id);
			return true;
		}
		for (CEventData data : event_list)
		{			
			if (MseeDataset.IsEventAtTime(data, conditionTime))				
			{
				//System.err.println("\t DEBUG MseeDataset IsEventAtTime return true");
				return true; 
			}
		}

		//System.err.println("\t DEBUG MseeDataset IsEventAtTime return false");
		return false; 
	}


	private static boolean IsEventAtTime(CEventData event, TimeData conditionTime) {
	
		// check if in current query observer subset; this is to avoid overcounting 
		if (false == MseeFunction.IsInObsSubset(event.GetObsId()))
		{
			return false;
		}		
		
		if(false == isTheViewCorrect(event.id, event.GetViewId(), conditionTime))
		{
			return false;
		}

		if (conditionTime == null)
		{
			//System.err.println("DEBUG MseeDataset IsEventAtTime conditionTime is null : ");
			return false;	
		}
		
		Boolean b =  false;
		
		for (CActionData action : event.actionDataVt)
		{
			// if any action data statisfied, then return true;
			if (IsActionAtTime(action, conditionTime))
			{	
				b = true; 
				break;
			}
		}
		
		return b;
		
	}


	private static boolean IsActionAtTime(CActionData action,
			TimeData conditionTime) {
		
	
		if ((	(action.mSceneTime_Start.compareTo(conditionTime.mSceneTime_End) > 0) || 
				(conditionTime.mSceneTime_Start.compareTo(action.mSceneTime_End) > 0) ))
		{
			return  false;
		} else
		{	return true;
		} 
		
		
	}


	


	private static boolean IsObjectAtTime(String entityStr,
			TimeData conditionTime) {
	
		ArrayList<CObjectData> object_list = MseeDataset.FindObjectDataList_or_parents(MseeDataset.RemoveNamespace(entityStr));
		
		if (object_list.size()==0)
		{
			System.out.println("WARN IsObjectAtTime object not found; return true. entityStr "+entityStr);
			return false;
		}		
		
		for (CObjectData object_data : object_list)
		{
			
			if (MseeDataset.IsObjectAtTime(object_data, conditionTime))				
			{
				return true; 
			}
		}
		return false; 				
	}
	

	private static boolean IsObjectOrEventAtTime(String entityStr,
			TimeData conditionTime) {
	
		ArrayList<CObjectData> object_list = MseeDataset.FindObjectDataList_or_parents(MseeDataset.RemoveNamespace(entityStr));
		
		
		if (object_list.size() >0)
		{
			for (CObjectData object_data : object_list)
			{
				
				if (MseeDataset.IsObjectAtTime(object_data, conditionTime))				
				{
					return true; 
				}
			}
			return false; 	// this is an object, but not at the time
		}
		
		String event_id = RemoveNamespace(entityStr);
		ArrayList<CEventData> event_list = MseeDataset.FindEventDataList(MseeDataset.RemoveNamespace(event_id));
		
		
		if (event_list.size()>0)
		{
			for (CEventData data : event_list)
			{			
				if (MseeDataset.IsEventAtTime(data, conditionTime))				
				{
					return true; 
				}
			}
		
			return false; 
		}
		
		// neither object or event, return true;
		return true; 				
	}
	
	
	


	/**
	 * Check is an entity (object or event) is at a given location
	 * 
	 * @param entityType
	 * @param entityStr
	 * @param locationType
	 * @param locationStr
	 * @return
	 * @throws MseeException
	 */
	public static boolean IsEntityAtLocation(ArgType entityType, String entityStr,
			ArgType locationType, String locationStr) throws MseeException
		{
		// TODO Auto-generated method stub		
		LocationData condition_loc; 

		condition_loc = GetLocationData(locationType, locationStr, false);
		

		switch (entityType)
		{
			case OBJECT_ID:
				 return IsObjectAtLocation(entityStr, condition_loc);
				 
			case EVENT_ID:
				return IsEventAtLocation(entityStr, condition_loc);
			
			case ENTITY_ID:
				
				return MseeDataset.IsObjectOrEventAtLocation(entityStr, condition_loc);
				 			 
			default:
				System.err.println("DEBUG ERROR IsEntityAtLocation invalid argtype " + entityType.toString());
				return false;
				// throw new MseeException("DEBUG ERROR IsEntityAtLocation invalid argtype " + entityType.toString());				 
		}		
	}

	private static boolean IsObjectOrEventAtLocation(String entityStr,
			LocationData condition_loc) throws MseeException {
		
		String object_id = RemoveNamespace(entityStr);
		ArrayList<CObjectData> objectDataList = MseeDataset.FindObjectDataList_or_parents(object_id);
		
		if (objectDataList.size() >0)
		{
			if (true == IsObjectAtLocation(entityStr, condition_loc))
				return true;
			else
				return false;
		}		
		
			
		ArrayList<CEventData> event_list = MseeDataset.FindEventDataList(MseeDataset.RemoveNamespace(object_id));
		
		if (event_list.size() >0)
		{
			if (true == IsEventAtLocation(object_id, condition_loc))
				return true;
			else
				return false;
		}
		
		// neither object or event, return true; 
		return true;
	}


	private static boolean IsEventAtLocation(
			String a_event_id,
			LocationData condition_loc) throws MseeException {		
		
		
		String event_id = RemoveNamespace(a_event_id);
		
		ArrayList<CEventData> event_list = MseeDataset.FindEventDataList(MseeDataset.RemoveNamespace(event_id));
		
		
		// System.out.println("DEBUG MseeDataset IsEventAtLocation event_list.size()"  + event_list.size());
		for (CEventData data : event_list)
		{			
			if (MseeDataset.IsEventAtLocation(data, condition_loc))				
			{
				return true; 
			}
		}
		return false; 
		
	}


	private static boolean IsEventAtLocation(CEventData eventData,
			LocationData condition_loc) {

		// check if in current query observer subset; this is to avoid overcounting 
		if (false == MseeFunction.IsInObsSubset(eventData.GetObsId()))
		{
			return false;
		}				
		
		if(false == isTheViewCorrect(eventData.id, eventData.GetViewId(), condition_loc))
		{
			return false;
		}
		
		if (condition_loc == null)
		{	System.err.println("ERROR IsEventAtLocation eventData == null");
			return false; 
		}		
		
		
		Boolean b =  false;
		
		for (CActionData action : eventData.actionDataVt)
		{
			// if any action data statisfied, then return true;
			if (IsActionAtLocation(action, condition_loc))
			{	b = true; 
				break;
			}
		}
		
		return b;
		
	}
	
		
		/* old
		LocationData loc_event = GetEventLocation(eventData, condition_loc.location_type);
		
	    if (MseeFunction.bVerbose)
	    {
			System.out.println("DEBUG MseeDataset IsEventAtLocation " + eventData.id  );
			System.out.println("\tDEBUG MseeDataset IsEventAtLocation loc_event " );
			loc_event.Printf();
			System.out.println("\tDEBUG MseeDataset IsEventAtLocation condition_loc " );
			condition_loc.Printf();
	    }
		
		
		if (SpatialFunctions.IsAtLocation(loc_event, condition_loc))
		{	// if any pos data is at location, then return true
			return true;
		} else
		{	return false;
		}
		*/
		
		
		
	private static boolean IsActionAtLocation(CActionData action,
			LocationData condition_loc) {

		// LocationData loc_event = GetEventLocation(eventData, condition_loc.location_type);
		LocationData loc_action = GetActionLocation(action, condition_loc.location_type);
		
		
	    if ((MseeFunction.bVerbose))
	    {
			System.out.println("\tDEBUG MseeDataset IsActionAtLocation loc_event " );
			loc_action.Printf();
			System.out.println("\tDEBUG MseeDataset IsActionAtLocation condition_loc " );
			condition_loc.Printf();
	    }
		
		
		if (SpatialFunctions.IsAtLocation(loc_action, condition_loc, SpatialFunctions.action_Location_Margin))
		{	// if any pos data is at location, then return true
			return true;
		} else
		{	return false;
		}

	}

	public static CEventData FindEventData(String a_event_id) {
		// TODO Auto-generated method stub
		
		String event_id = RemoveNamespace(a_event_id);
		
		return MseeDataset.parsedDataDescriptor.FindEventData(event_id);
	}

	
	private static LocationData GetEventLocation(CEventData eventData, LocationType location_type) {
		// TODO Auto-generated method stub
		// TODO assume event data only have frame coordinate for now
		
		return eventData.GetLocationData(location_type);
		
	}

	

	private static LocationData GetActionLocation(CActionData action,
				LocationType location_type) {
		return action.GetLocationData(location_type);
	}


	private static boolean IsObjectAtLocation(String a_object_id,
			LocationData condition_loc) throws MseeException {
		// TODO Auto-generated method stub
		
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG MseeDataset IsObjectAtLocation starting" );
		}


		boolean b  = false;
		
		String object_id = RemoveNamespace(a_object_id);
		
		
		// DEPRECATED		
		// CObjectData objectData = MseeDataset.objectDataset.FindObjectData(object_id);
		ArrayList<CObjectData> objectDataList = MseeDataset.FindObjectDataList_or_parents(object_id);

		if ((objectDataList == null) || objectDataList.isEmpty())
		{	
			if (MseeFunction.bVerbose || true)
			{	
				System.err.println("DEBUG ERROR IsObjectAtLocation unable to find object " + object_id );
			}
			//return false; 
			
			if (MseeFunction.bVerbose)
			{	
				System.out.println("DEBUG MseeDataset IsObjectAtLocation lookfor partof  " + object_id );
			}
			/*
			String parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(object_id);
			while (parent_id != null)
			{
				b = IsObjectAtLocation(parent_id , condition_loc);

				//System.out.println("DEBUG MseeDataset IsObjectAtLocation partof check PARENT  " + parent_id  + " result " + b);
				
				if (b)
				{ break;
				} 
				object_id = parent_id;
				parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(object_id);
			}*/ 
			
			
		}		
		// public Vector<CPositionData> positionVt = new Vector<CPositionData>();
		
		if (!b )
		{
			for (CObjectData objectData : objectDataList)
			{
				b = IsObjectAtLocation(objectData,condition_loc);
			
				if (b)
					break;
				
				if ( b == false)
				{
					if (MseeFunction.bVerbose)
					{	
						System.out.println("DEBUG MseeDataset IsObjectAtLocation lookfor partof  " + objectData.id );
					}
					String parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(objectData.id);
					while (parent_id != null)
					{
						b = IsObjectAtLocation(parent_id , condition_loc);
						if (b)
						{ break;
						} 
						object_id = parent_id;
						parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(object_id);
					}
					
				
				}
			}
		}
			
		/*
		// testing only
		
		System.err.println("DEBUG MseeDataset IsObjectAtLocation lookfor partof  " + objectData.id );
		String parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(objectData.id);
		boolean b_parent  = false;
		if (parent_id != null)
		{
			System.err.println("DEBUG MseeDataset IsObjectAtLocation partof check PARENT  " + parent_id );
			b_parent = IsObjectAtLocation(parent_id , condition_loc);
		}
		b = (b || b_parent);
		*/
		
		return b; 
	}

	static boolean IsObjectAtLocation(CObjectData objectData,
			LocationData condition_loc) 
	{
		// check if in current query observer subset; this is to avoid overcounting 
		if (false == MseeFunction.IsInObsSubset(objectData.observer_id))
		{
			return false;
		}		
		
		if(false == isTheViewCorrect(objectData.id, objectData.GetViewId(), condition_loc))
		{
			return false;
		}
		
		return checkSamplesForLocation(objectData, condition_loc, 0, objectData.positionVt.size()-1, 1);

	}
	
	static boolean checkSamplesForLocation(CObjectData objectData,
			LocationData condition_loc, int istart, int iend, int iskip) {		
		for (int i = istart; i <= iend; i+= iskip)
		{
			
			CPositionData pos_data  = objectData.positionVt.elementAt(i);
			
			LocationData loc_data = null;
			
			if (	(condition_loc.location_type  == LocationType.CARTESIAN_METRIC_POINT) ||
					(condition_loc.location_type  == LocationType.CARTESIAN_METRIC_POLYGON) ) 
			 {
				loc_data  = pos_data.groundpos;
				
			 }
			
			if (loc_data == null)
			{
				// LocationData loc_data = new LocationData(condition_loc.location_type, pos_data);
				loc_data = pos_data.GetLocationData(condition_loc.location_type, objectData.GetViewId());
			}
			
			
			if (loc_data!= null)
			{			
				
				double loc_margin = 0;
				
				switch (condition_loc.location_type)
				{
					case GEODETIC_POINT:
					case GEODETIC_POLYGON:
						loc_margin = SpatialFunctions.object_Location_Margin_geo; break; 
					case CARTESIAN_METRIC_POINT:
					case CARTESIAN_METRIC_POLYGON:
						loc_margin = SpatialFunctions.object_Location_Margin_carte; break; 
					case VIEW_CENTRIC_POINT:
					case VIEW_CENTRIC_POLYGON:	
						loc_margin = SpatialFunctions.object_Location_Margin_view; break; 
									
					
				}
					
				
				if (SpatialFunctions.IsAtLocation(loc_data, condition_loc, loc_margin))
				{	// if any pos data is at location, then return true
			       if (MseeFunction.bVerbose)
					{
						System.out.println("DEBUG checkSamplesForLocation return true" );
				   }
					return true;
				}		
			} else
			{
				System.err.println("DEBUG MseeDataset checkSamplesForLocation loc_data == null;  objectData.id "  + objectData.id);
				pos_data.Printf();
			}
					
		}
		return false;
	}
	
	/* OBSOLETE */
	/**
	 * @param object_id
	 * @return
	 * 
	 * \todo call to this function should be replaced by FindObjectDataList()
	 */
	public static CObjectData FindObjectData(String object_id) {
		// TODO Auto-generated method stub
		
		String str = MseeDataset.RemoveNamespace(object_id);
		if (MseeFunction.bVerbose)
		{
			System.out.println("\tMseeDataset FindObjectData " + str);
		}
				
		CObjectData data = MseeDataset.parsedDataDescriptor.FindObjectData(str);
		if (data == null)
		{
			PrintObjectNotFoundMessage("MseeDataset FindObjectData", str);

			
		} else
		{
			if (MseeFunction.bVerbose)
			{
				System.out.println("\t\tMseeDataset FindObjectData  FOUND:" + str);
			}
		}
		return data;
	}
	
	
	/**/ 
	

	public static ArrayList<CObjectData> FindObjectDataList(String object_id) {

		String str = MseeDataset.RemoveNamespace(object_id);
		if (MseeFunction.bVerbose)
		{
			System.out.println("\tMseeDataset FindObjectData " + str);
		}
				
		ArrayList<CObjectData>  list = MseeDataset.parsedDataDescriptor.FindObjectDataList(str);
		if (list.isEmpty())
		{
			PrintObjectNotFoundMessage("MseeDataset FindObjectData", str);
					
		} else
		{
			if (MseeFunction.bVerbose)
			{
				System.out.println("\t\tMseeDataset FindObjectData  FOUND:" + str);
			}
		}
		return list;
	}	
	

	public static ArrayList<CObjectData> FindObjectDataList_or_parents(String object_id) {

		String str = MseeDataset.RemoveNamespace(object_id);
		if (MseeFunction.bVerbose)
		{
			System.out.println("\tMseeDataset FindObjectData " + str);
		}
				
		ArrayList<CObjectData>  list = MseeDataset.parsedDataDescriptor.FindObjectDataList(str);
		if (list.isEmpty())
		{
			PrintObjectNotFoundMessage("MseeDataset FindObjectData", str);
			
			// keep finding parents
			String parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(object_id);
			
			
			while (parent_id != null)
			{
				
				// System.out.println("DEBUG FindObjectDataList_or_parents  parent " + parent_id + " object: " + object_id);
								
				list = MseeDataset.parsedDataDescriptor.FindObjectDataList(parent_id);				 
				
				if (!list.isEmpty())
				{ break;
				} 
				object_id = parent_id;
				parent_id = (String) MseeDataset.parsedDataDescriptor.partOfMapping.partOfMap.get(object_id);
			}			
		} else
		{
			if (MseeFunction.bVerbose)
			{
				System.out.println("\t\tMseeDataset FindObjectData  FOUND:" + str);
			}
		}
		return list;
	}
	
	
	public static ArrayList<CEventData> FindEventDataList(String event_id) {

		String str = MseeDataset.RemoveNamespace(event_id);
		if (MseeFunction.bVerbose)
		{
			System.out.println("\tMseeDataset FindEventDataList " + str);
		}
				
		ArrayList<CEventData>  list = MseeDataset.parsedDataDescriptor.FindEventDataList(str);
		if (list.isEmpty())
		{
			PrintEventNotFoundMessage("MseeDataset FindEventDataList", str);
			
		} else
		{
			if (MseeFunction.bVerbose)
			{
				System.out.println("\t\tMseeDataset FindEventDataList  FOUND:" + str + " list.size() " + list.size());
			}
		}
		return list;
	}
	

	private static void PrintObjectNotFoundMessage(String prefix, String str2) {
		
		/*
		if (FindStringInList(listObjectNotFound, str2) == true)
		{
			// already print out for this object; ignore
			// System.err.println("\t\tMseeDataset FindObjectData NOT FOUND: " + str + " already in list" );	
		} else
		{	System.err.println("\t" + prefix + " MseeDataset Object NOT FOUND:" + str2);
			listObjectNotFound.add(new String(str2));			
		}
		*/				
	}

	private static void PrintEventNotFoundMessage(String prefix, String str2) {
		/*
		if (FindStringInList(listEventNotFound, str2) == true)
		{
			// already print out for this event; ignore
			// System.err.println("\t\tMseeDataset FindEventData NOT FOUND: " + str + " already in list" );	
		} else
		{	
			System.err.println("\t" + prefix + " MseeDataset Event NOT FOUND:" + str2);
			listEventNotFound.add(new String(str2));			
		}		
		*/		
	}
	
	

	private static boolean FindStringInList(
			ArrayList<String> myList, String search_str) {
		for(String str: myList) {
		    if(str.trim().contains(search_str))
		       return true;
		}
		return false;
	}


	// this should only be used for location is the query
	public static LocationData GetLocationData(ArgType argType,
			String locationStr
			) throws MseeException {
		return MseeDataset.GetLocationData(argType, locationStr, true);
	}

	// this should only be used for location is the query
	public static LocationData GetLocationData(ArgType argType,
			String locationStr,
			boolean bConvertViewToScene 
			) throws MseeException {
		// TODO Auto-generated method stub
		
			
		LocationData loc_data = new LocationData();
		switch (argType)
		{
			case ARG_GEODETIC_POINT:
				loc_data.Set(LocationType.GEODETIC_POINT, locationStr); 
				 break;
			case ARG_GEODETIC_POLYGON:
				loc_data.Set(LocationType.GEODETIC_POLYGON, locationStr); 
				 break;
			case ARG_VIEW_CENTRIC_POINT:
				loc_data.Set(LocationType.VIEW_CENTRIC_POINT, locationStr); 
				if (bConvertViewToScene)
				{	loc_data = loc_data.ConvertViewToSceneCentric();
				
				}
				break;
			case ARG_VIEW_CENTRIC_POLYGON:
				loc_data.Set(LocationType.VIEW_CENTRIC_POLYGON, locationStr); 
				if (bConvertViewToScene)
				{	loc_data = loc_data.ConvertViewToSceneCentric();
				}
				break;
			case ARG_CARTESIAN_METRIC_POINT:
				loc_data.Set(LocationType.CARTESIAN_METRIC_POINT, locationStr); 
				 break;
			case ARG_CARTESIAN_METRIC_POLYGON:
				loc_data.Set(LocationType.CARTESIAN_METRIC_POLYGON, locationStr); 
				 break;			 
			default:
				System.err.println("DEBUG ERROR GetLocationData invalid argtype " + argType.toString());
				throw new MseeException("DEBUG ERROR GetLocationData invalid argtype " + argType.toString());				 
		}
		

		return loc_data;
	}


	/**
	 * @param a_object_id contains the object id
	 * @return the time period for which this object exists  
	 */
	public static TimeData GetObjectTimeData(String a_object_id)
	{
		String object_id = RemoveNamespace(a_object_id);
		// DEPRECATED		
		ArrayList<CObjectData> objectDataList = MseeDataset.FindObjectDataList(object_id);
	
		if ((objectDataList == null) || objectDataList.isEmpty()) 
		{	
			 
			PrintObjectNotFoundMessage("MseeDataset GetObjectTimeData ", object_id);
			return null;
		}	else
		{

			TimeData timeData = objectDataList.get(0).GetTimeData().clone();
			if (MseeFunction.bVerbose)
			{
				System.out.print("DEBUG MseeDataset TimeData GetObjectTimeData \t");
				timeData.PrintTimeData();
			}
			
			int i = 0; 
			
			for (CObjectData objectdata : objectDataList)
			{
				TimeData tmp = objectdata.GetTimeData();

				if (MseeFunction.bVerbose)
				{
					System.out.println("\t DEBUG MseeDataset TimeData GetObjectTimeData  tmp i  \t" + i + "\t");
					tmp.PrintTimeData();
				}
				
				timeData.ExtendSceneTimeRange(tmp);
				if (MseeFunction.bVerbose)
				{
					System.out.println("\t DEBUG MseeDataset TimeData GetObjectTimeData  Extended \t");
					timeData.PrintTimeData();			
				}
				
				i++;
			}
			
			return timeData;		
		}
	}
	
	/**
	 *  Node variable has the form "http://msee.data/#object_1. 
	 *  This function remove the http namespace, the return the ID used in XML file	 
	 *  
	 * @param a_entity_id contains in input string (e.g. an object id or event id) 
	 * @return a new string without the "http://../#" namespace. 
	 */
	public static String RemoveNamespace(String a_entity_id) {
		String entity_id;
		
		entity_id = a_entity_id;
		if ((entity_id.startsWith("\"")) && (entity_id.endsWith("\""))) {	//If surrounded by double quotes
			entity_id = entity_id.substring(1,entity_id.length() - 1);			//Strip them off
		}
		if (entity_id.contains("http://"))
		{
			int index = entity_id.indexOf("#");
			if (index == -1)
			{
				return entity_id;
			} else
			{
				return entity_id.substring(index+1);
			}
		} else
			return entity_id;
	}

	/**/
	public static TimeData GetEventTimeData(String a_event_id)
	{
		String event_id = RemoveNamespace(a_event_id);
		
		TimeData timeData = new TimeData();
		
		// DEPRECATED		
		// CEventData eventData = MseeDataset.eventDataset.FindEventData(event_id);
		CEventData eventData = MseeDataset.FindEventData(event_id);
				
		if (eventData  != null)
		{	timeData = GetEventTimeData(eventData);
		} else
		{	System.out.println("DEBUG ERROR GetEventTimeData unable to find event " + event_id );
		}		
		return timeData;		
	}
	
	
	/**/
	public static TimeData GetEventTimeData(CEventData eventData)
	{
		TimeData timeData = new TimeData();		
		
		if (eventData  != null)
		{	timeData.SetViewFramePeriod(
				eventData.begin_frame,
				eventData.end_frame
					);
		
			if 	((eventData.mSceneTime_Start != null) &&
					(eventData.mSceneTime_End != null)) {
					timeData.SetSceneTimePeriod(eventData.mSceneTime_Start, eventData.mSceneTime_End);
				}
		} else
		{	System.out.println("DEBUG ERROR invalid eventData is null" );
		}		
		return timeData;		
	}
	
	
	private static TimeData GetActionTimeData(CActionData action) {
		TimeData timeData = new TimeData();		
		
		if (action  != null)
		{	timeData.SetViewFramePeriod(
				action.begin_frame,
				action.end_frame
					);
		
			if 	((action.mSceneTime_Start != null) &&
					(action.mSceneTime_End != null)) {
					timeData.SetSceneTimePeriod(action.mSceneTime_Start, action.mSceneTime_End);
				}
			else
			{
				System.err.println("ERROR MseeDataset GetActionTimeData action has no scene time" );
			}
		} else
		{	System.err.println("ERROR MseeDataset GetActionTimeData invalid action is null" );
		}		
		return timeData;	
		
	}
	
	/**
	 * @param argType states what argument type argStr is 
	 * @param argStr contains the entity to get time data, e.g. object_id, event_id or a string descriptin the time period 
	 * @return the time period
	 * @throws MseeException if there is an error parsing the input string 
	 */
	public static TimeData GetTimeData(
			MseeFunction.ArgType argType,
			String	argStr) throws MseeException
	{

		TimeData timeData = new TimeData();
		switch (argType)
		{
			case OBJECT_ID:
				 timeData = GetObjectTimeData(argStr);
				 break;
			case EVENT_ID:
				 timeData = GetEventTimeData(argStr);
				 break;				
			case VIEW_CENTRIC_TIME_PERIOD:
				timeData.SetViewFramePeriod(argStr);
				 break;				
			case SCENE_CENTRIC_TIME_PERIOD:
				timeData.SetSceneTimePeriod(argStr);
				 break;				
			 
			default:
				System.err.println("DEBUG ERROR GetTimeData invalid argtype " + argType.toString());
				//throw new MseeException("DEBUG ERROR GetTimeData invalid argtype " + argType.toString());				 
		}
		
		return timeData;
	}


	public static TimeData GetDatasetSceneTimeRange() {
		// TODO Auto-generated method stub
		TimeData d = new TimeData();
		d.SetSceneTimePeriod(	MseeDataset.parsedDataDescriptor.GetMinSceneTime(),
				MseeDataset.parsedDataDescriptor.GetMaxSceneTime());
				
		return d;
	}


	public static View GetView(String viewId) {
		return MseeDataset.parsedDataDescriptor.GetView(viewId);
	}


	public static Homography GetHomographyView2Map(String str_view_id) {
		 View view = GetView(str_view_id);
		 if (view == null)
		 {
			 System.err.println("ERROR MseeDataset GetHomographyView2Map; view not found " + str_view_id);
			 return null;
		 } else
		 {
			 // DEBUG
			 System.out.println("DEBUG MseeDataset GetHomographyView2Map; view found " + str_view_id);
		 }
		 
		 return view.hView2Map;
	}


	public static boolean IsObjectAtTime(CObjectData objectdata,
			TimeData conditionTime) {
		
		/* replaced to optimize
		TimeData entityTime = objectdata.GetTimeData();
		return TimeFunctions.IsAtTime(entityTime, conditionTime);	
		*/
		// check if in current query observer subset; this is to avoid overcounting 
		
		
		if (false == MseeFunction.IsInObsSubset(objectdata.GetObsId()))
		{
			//System.out.println("DEBUG IsObjectAtTime  IsInObsSubset ------------------ return false");
			return false;
		}
		
		
		
		if(false == isTheViewCorrect(objectdata.id, objectdata.GetViewId(), conditionTime))
		{
			//System.out.println("DEBUG IsObjectAtTime  isTheViewCorrect ------------------ return false");
			
			return false;
		}		
		if (DEBUG_THIS)
		{
			System.out.println("DEBUG IsObjectAtTime ");
			System.out.println("DEBUG IsObjectAtTime conditionTime");
			conditionTime.PrintTimeData();
			
			System.out.println("DEBUG IsObjectAtTime objectdata.mSceneTime_Start");
			System.out.println(objectdata.mSceneTime_Start.toString());
			
			System.out.println("DEBUG IsObjectAtTime objectdata.mSceneTime_End");
			System.out.println(objectdata.mSceneTime_End.toString());
		}
		

		if (conditionTime.mbValidViewFrame)
		{
		
			if ((objectdata.frame_min == -1) || (objectdata.frame_max == -1))
			{
				// static object
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------   objectdata.frame_min == -1 return false");
				}
				return false;
			}
			
			if (objectdata.view_id.equals(conditionTime.mViewId) == false)
			{
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------ view id different, return false : " + objectdata.view_id + " vs " + conditionTime.mViewId);
				}
				return false;
			}
			
			if ((objectdata.frame_min > conditionTime.mViewFrame_End)
				|| (objectdata.frame_max < conditionTime.mViewFrame_Start))
			{	// no overlap
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------ frame, return false");
				}
				return false;
			}
			else
			{
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------ frame, return true");
				}
				return true;
			}			
			
		} 
		
		if (conditionTime.hasValidSceneTime())
		{
		
			if ((objectdata.mSceneTime_Start == null) || (objectdata.mSceneTime_End == null))
			{
				// static object
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------   static object return true");
				}
				return true;
			}
			
			if ((	(objectdata.mSceneTime_Start.compareTo(conditionTime.mSceneTime_End) > 0) || 
					(conditionTime.mSceneTime_Start.compareTo(objectdata.mSceneTime_End) > 0) ))
			{
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------ return false");
				}
				return  false;
			} else
			{
				if (DEBUG_THIS)
				{
					System.out.println("DEBUG IsObjectAtTime  ------------------ return true");
				}
				return true;
				//int[] indices = findSamplesForTime(objectdata, conditionTime);
				//return indices[0] <= indices[1];			
			} 
		}
		System.err.println("ERROR IsObjectAtTime  ------------------ invalid conditionTime, return false : " );
		System.out.println("DEBUG IsObjectAtTime conditionTime");
		conditionTime.PrintTimeData();
		return false;
	
	}

	


	private static Boolean IsObjectAtTimeLocation(String object_id, TimeData time_data, 
			LocationData condition_loc) {	

		ArrayList<CObjectData> list_agent = MseeDataset.FindObjectDataList_or_parents(MseeDataset.RemoveNamespace(object_id));
	
		for (CObjectData object_agent : list_agent)
		{	if (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_agent, time_data, condition_loc) )
				return true;
		}
		return false;				
	}
	
	// Returns a two-element array that contains the start index and the end index of all positions (samples) in CObjectData that fall within
	// the interval in time_data (increased by time_margin_internal).  If none exist, then the start index will be greater than the end index.
	public static int[] findSamplesForTime( CObjectData objectData, TimeData time_data ) {
		if ( !MseeFunction.objectSpecQuery && time_margin_internal > 0 ) {
			time_data = time_data.clone();
			time_data.AddMargin_msec(time_margin_internal);
		}
		
		
		//System.out.println("DEBUG findSamplesForTime query time_data ");
		//time_data.PrintTimeData();
		
		int istart = 0;
		int iend = objectData.positionVt.size()-1;
		
		boolean stationaryObject = false;
		
		// If the frame number is not given for an object, then we assume that it is stationary, and is always present
		// within the time interval [objectData.positionVt.get(0).mSceneTime, objectData.positionVt.get(1).mSceneTime].
		// In this case, we check to see if there is any overlap between this time interval and
		// [time_data.mSceneTime_Start, time_data.mSceneTime_End].
		if ( objectData.positionVt.size() > 0 && objectData.positionVt.get(0).frame < 0 ) {
			stationaryObject = true;
			
			if ( objectData.positionVt.size() != 2 ) {
				System.err.println( "WARN a stationary object " + objectData.id + " has " + objectData.positionVt.size() + " positions defined." );
			}
			
			// The time intervals don't overlap.
			if ( objectData.positionVt.get(iend).mSceneTime.compareTo( time_data.mSceneTime_Start ) < 0 ) {
				istart = 1;
				iend = 0;
			}
			
			// The time intervals don't overlap.
			if ( objectData.positionVt.get(istart).mSceneTime.compareTo( time_data.mSceneTime_End ) > 0 ) {
				istart = 1;
				iend = 0;
			}
		}
		
		// Otherwise, we determine the subset of positions that fall within
		// [time_data.mSceneTime_Start, time_data.mSceneTime_End].
		while (!stationaryObject && istart < objectData.positionVt.size())
		{
			CPositionData pos_data = objectData.positionVt.elementAt(istart);
			if (pos_data.mSceneTime.compareTo(time_data.mSceneTime_Start) < 0)
			{
		
				istart = istart+1;
				/*
				System.err.println("DEBUG MseeDataset IsObjectAtTimeLocation");
				System.err.println("\tDEBUG MseeDataset IsObjectAtTimeLocation istart " + istart);
				System.err.println("\tDEBUG MseeDataset IsObjectAtTimeLocation pos_data.mSceneTime " + pos_data.mSceneTime.toString());
				System.err.println("\tDEBUG MseeDataset IsObjectAtTimeLocation time_data.mSceneTime_Start " + time_data.mSceneTime_Start.toString());
				*/ 
				
			} else
				break; 		
		}
		

		while (!stationaryObject && iend >= 0)
		{
			CPositionData pos_data = objectData.positionVt.elementAt(iend);
			if (pos_data.mSceneTime.compareTo(time_data.mSceneTime_End) > 0)
			{
				iend = iend-1;	
				/*
				System.err.println("DEBUG MseeDataset IsObjectAtTimeLocation");
				System.err.println("\tDEBUG MseeDataset IsObjectAtTimeLocation iend " + iend);
				System.err.println("\tDEBUG MseeDataset IsObjectAtTimeLocation pos_data.mSceneTime " + pos_data.mSceneTime.toString());
				System.err.println("\tDEBUG MseeDataset IsObjectAtTimeLocation time_data.mSceneTime_End " + time_data.mSceneTime_End.toString());
				*/
				
			} else
				break; 		
		}
		
		return new int[] { istart, iend };
	}
	
	// check if object is at a location at a given time
	public static boolean IsObjectAtTimeLocation(
			CObjectData objectData,
			TimeData time_data, 
			LocationData condition_loc) {

		
		// check if in current query observer subset; this is to avoid overcounting 
		if (false == MseeFunction.IsInObsSubset(objectData.observer_id))
		{
			return false;
		}
		
		if(false == isTheViewCorrect(objectData.id, objectData.GetViewId(), time_data))
		{
			return false;
		}
		if(false == isTheViewCorrect(objectData.id, objectData.GetViewId(), condition_loc))
		{
			return false;
		}
			
		if ((objectData.mSceneTime_Start == null) || (objectData.mSceneTime_End == null))
		{
			// static object
			return true;
		}
		
		if ((	(objectData.mSceneTime_Start.compareTo(time_data.mSceneTime_End) > 0) || 
				(time_data.mSceneTime_Start.compareTo(objectData.mSceneTime_End) > 0) ))
		{
			return  false;
		}		    
		
		// get enclosing index of CPositionData
		int[] indices = findSamplesForTime(objectData, time_data);
		int istart = indices[0];
		int iend = indices[1];
		
		if ( istart > iend ) {
			if ( MseeFunction.bVerbose ) {
				System.out.println("DEBUG IsObjectAtTimeLocation no valid times found" );
			}
			return false;
		}
		
		return checkSamplesForLocation(objectData, condition_loc, istart, iend, 1);
	}


	public static boolean CheckObjectList_AtTimeLocation_AllowNull(	ArrayList<CObjectData> object_list, TimeData time_data, LocationData loc_data)
	{
		boolean bOkay = false;			
		for (CObjectData object_data : object_list)
		{				
			if (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_data, time_data, loc_data))				
			{
				bOkay = true; 
				break;
			}
		}		
		return bOkay;
	}
	
	
	
	public static boolean CheckObjectAtTimeLocation_AllowNull(
			CObjectData object, TimeData time_data, LocationData loc_data) {		
		
		//boolean bTimeLocOkay = true;
		if ((time_data != null) && (loc_data != null)) 
		{
			if ((false ==MseeDataset.IsObjectAtTimeLocation(object, time_data, loc_data)) 
					)
			    {   
			    	System.out.println("DEBUG MseeDataset CheckObjectAtTimeLocation_AllowNull IsObjectAtTimeLocation fails");
			    	return false;
			    }
		} else if ((time_data != null) )			
		{	if (false == MseeDataset.IsObjectAtTime(object, time_data))			 
			    {  			
			    	System.out.println("DEBUG MseeDataset CheckObjectAtTimeLocation_AllowNull  IsObjectAtTime fails");
			    	return false;
			    }
		} else if ((loc_data != null) )			
		{
			if (false ==MseeDataset.IsObjectAtLocation(object, loc_data)) 
			    {  	
			    	System.out.println("DEBUG MseeDataset CheckObjectAtTimeLocation_AllowNull  IsObjectAtLocation fails");
			    	return false;
			    }
		}
		return true;
		
	}


	public static Boolean IsObjectAtTimeLocation(
			 String object_id,
			MseeFunction.ArgType timeType,			// condition 
			String	timeStr,		
			ArgType locationType, 
			String locationStr) 	{
		
			TimeData conditionTime = null;
			try {
				conditionTime = GetTimeData(timeType, timeStr);
			} catch (MseeException e) {
				System.err.println("ERROR MseeDataset IsObjectAtTimeLocation unable to parse timestr" +timeStr);
			}
			LocationData condition_loc = null; 
			try {
				condition_loc = GetLocationData(locationType, locationStr, false);
			} catch (MseeException e) {
				System.err.println("ERROR MseeDataset IsObjectAtTimeLocation unable to parse locationStr" +locationStr);
			}
			
			if (conditionTime != null)
			{
				if (conditionTime.hasValidSceneTime() )
				{
					if (MseeFunction.objectSpecQuery)
					{
						conditionTime.AddMargin_msec(time_margin_object_spec);
					}
					else if (conditionTime.GetSceneTimeInterval_Msec() < min_unmodified_time_interval)
					{
						conditionTime.AddMargin_msec(time_margin);
					}
				} 
			}
		
			return IsObjectAtTimeLocation(object_id ,conditionTime, condition_loc);					 
		}

	public static boolean isTheViewCorrect( String id, String view_id, LocationData condition_loc ) {
		String view = null;
		
		if ( condition_loc.GetType() == LocationData.LocationType.VIEW_CENTRIC_POINT ) {
			if ( condition_loc.view_centric_point != null ) {
				view = condition_loc.view_centric_point.GetViewId();
			}
		}
		else if ( condition_loc.GetType() == LocationData.LocationType.VIEW_CENTRIC_POLYGON ) {
			if ( condition_loc.view_centric_polygon != null ) {
				view = condition_loc.view_centric_polygon.GetViewId();
			}
		}
		else {
			return true;
		}
		
		if ( view == null || view.equals("") ) {
			System.err.println("WARN no view was found for a LocationData object of type " + condition_loc.GetType() );
			return true;
		}
		
		if ( view_id == null || view_id.equals("unknown") ) {
			System.err.println("WARN no view was found for object " + id );
			return true;
		}
		
		// remove these words before comparing view-id 
		//-single-frames
		//-video
		
		view = view.trim();
		view_id = view_id.trim();
		
		int i = Math.min(view.length(), view_id.length());
		int m = view.indexOf("-single-frames");
		if ((m!= -1) && (m <i))
		{
			i = m;
		}
		
		 m = view_id.indexOf("-video");
		if ((m!= -1) && (m <i))
		{
			i = m;
		}
			
		return view.substring(0, i).equals( view_id.substring(0, i));
	}
	
	public static boolean isTheViewCorrect( String id, String view_id, TimeData conditionTime ) {
		String view = null;
		
		if ( conditionTime.mbValidViewFrame ) {
			if ( conditionTime.mViewId != null ) {
				view = conditionTime.mViewId;
			}
		}
		else {
			return true;
		}
		
		if ( view == null || view.equals("") ) {
			System.err.println("WARN no view was found for a view-centric TimeData object");
			return true;
		}
		
		if ( view_id == null || view_id.equals("unknown") ) {
			System.err.println("WARN no view was found for object " + id );
			return true;
		}
		
		boolean b =  view.trim().equals( view_id.trim() );
		
		// debug
		if (b== false)
		{
			// System.out.println("DEBUG isTheViewCorrect return false "  +  view   + "  view_id  "  + view_id);
		}
		
		return b;
	}
	
	// check that given view id is the same as observer id, but need to take out "obs-" and "view-"
	public static boolean isView_Obs_Correct( String a_obs_str, String a_view_id) {
		if ((a_obs_str.startsWith("\"")) && (a_obs_str.endsWith("\""))) {	//If surrounded by double quotes
			a_obs_str = a_obs_str.substring(1,a_obs_str.length() - 1);			//Strip them off
		}
		
		if ( a_obs_str.equals(a_view_id) ) {
			System.out.println( "INFO view id, rather than observer id, is specified in a where query." );
			return true;
		}
		
		View view = MseeDataset.parsedDataDescriptor.GetView(a_view_id);
		
		if ( view == null ) {
			System.err.println( "WARNING view " + a_view_id + " does not exist." );
			return false;
		}
		
		return a_obs_str.equals( view.GetObserverId() );
//		
//		
//		//System.out.println("DEBUG isView_Obs_Correct a_obs_str "  +  a_obs_str);
//		//System.out.println("DEBUG isView_Obs_Correct a_view_id "  +  a_view_id);
//		
//		String key_obs = "obs-";
//		String key_view = "view-";
//		
//		String obs_str = a_obs_str; 
//		
//		if ((obs_str.startsWith("\"")) && (obs_str.endsWith("\""))) {	//If surrounded by double quotes
//			obs_str = obs_str.substring(1,obs_str.length() - 1);			//Strip them off
//		}
//		
//		
//		if (obs_str.startsWith(key_obs))
//		{
//			obs_str = obs_str.substring(key_obs.length());
//		}
//		
//		if (obs_str.startsWith(key_view))
//		{
//			obs_str = obs_str.substring(key_view.length());
//		}				
//		
//		String view_id = a_view_id; 
//		if (a_view_id.startsWith(key_view))
//		{
//			view_id = a_view_id.substring(key_view.length());
//		}
//		
//		
//		//System.out.println("DEBUG isView_Obs_Correct obs_str "  +  obs_str);
//		//System.out.println("DEBUG isView_Obs_Correct view_id "  +  view_id);
//		
//		
//		boolean b =  obs_str.trim().equals( view_id.trim() );
//		
//		//System.out.println("DEBUG isView_Obs_Correct b "  +  b);
//		
//		return b;
	}
	
}
