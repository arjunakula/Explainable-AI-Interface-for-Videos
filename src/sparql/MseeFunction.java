package sparql;

import java.util.ArrayList;

import sparql.MseeFunction.ArgType;
import TextParser.CObjectData;

import com.hp.hpl.jena.sparql.expr.NodeValue;

public class MseeFunction 
{
	public static enum ArgType { 
		OBJECT_ID, 
		EVENT_ID,
		ENTITY_ID,
		ARG_VIEW_CENTRIC_POINT,
		ARG_VIEW_CENTRIC_POLYGON,
		ARG_CARTESIAN_METRIC_POINT,
		ARG_CARTESIAN_METRIC_POLYGON, 
		ARG_GEODETIC_POINT,
		ARG_GEODETIC_POLYGON,		
		VIEW_CENTRIC_TIME_PERIOD,
		SCENE_CENTRIC_TIME_PERIOD, 
		ARG_COLOR,
		ARG_TEMPORAL_RELATION,
		ARG_EQ,
		ARG_GT,
		ARG_LT,
		ARG_GTE,
		ARG_LTE,
		ARG_SET_NAME,
		ARG_FUNCTION,
		ARG_LOGICAL_OPERATOR,
		ARG_MIN_QUANTITY,
		AGR_MAX_QUANTITY,		
		AGR_GT_QUANTITY,
		AGR_LT_QUANTITY,
		AGR_NOT_QUANTITY,		
		AGR_OBSERVER,
		UNKNOWN };
		
	final static String ARG_STR_OBJECT_ID = "OBJECT_ID";
	final static String ARG_STR_EVENT_ID = "EVENT_ID";
	
	final static String ARG_STR_ENTITY_ID = "ENTITY_ID";
	
	
	final static String ARG_STR_VIEW_CENTRIC_POINT = "VIEW_CENTRIC_POINT";
	final static String ARG_STR_VIEW_CENTRIC_POLYGON = "VIEW_CENTRIC_POLYGON";
	
	final static String ARG_STR_CARTESIAN_METRIC_POINT = "CARTESIAN_METRIC_POINT";
	final static String ARG_STR_CARTESIAN_METRIC_POLYGON = "CARTESIAN_METRIC_POLYGON";
	final static String ARG_STR_GEODETIC_POINT = "GEODETIC_POINT";
	final static String ARG_STR_GEODETIC_POLYGON = "GEODETIC_POLYGON";
	
	// final static String ARG_STR_SCENE_CENTRIC_POLYGON = "SCENE_CENTRIC_POLYGON";
	
	final static String ARG_STR_VIEW_CENTRIC_TIME_PERIOD = "VIEW_CENTRIC_TIME_PERIOD";
	final static String ARG_STR_SCENE_CENTRIC_TIME_PERIOD = "SCENE_CENTRIC_TIME_PERIOD";
	
	final static String ARG_STR_COLOR = "COLOR";
	final static String ARG_STR_TEMPORAL_RELATION = "TEMPORAL_RELATION";
	
	final static String ARG_STR_EQ = "EQ";
	final static String ARG_STR_GT = "GT";
	final static String ARG_STR_LT = "LT";
	final static String ARG_STR_GTE = "GTE";
	final static String ARG_STR_LTE = "LTE";
	
	final static String ARG_STR_SET_NAME = "SET_NAME";
	final static String ARG_STR_FUNCTION = "FUNCTION";
	final static String ARG_STR_LOGICAL_OPERATOR = "LOGICAL_OPERATOR";

	
	final static String ARG_STR_MIN_QUANTITY = "MIN_QUANTITY";
	final static String AGR_STR_MAX_QUANTITY = "MAX_QUANTITY";
	final static String ARG_STR_GT_QUANTITY = "GT_QUANTITY";
	final static String AGR_STR_LT_QUANTITY = "LT_QUANTITY";
	final static String AGR_STR_NOT_QUANTITY = "NOT_QUANTITY";
	
	final static String AGR_STR_OBSERVER = "OBSERVER";
		
	final static String ARG_STR_UNKNOWN = "UNKNOWN";
	
	public static boolean bVerbose = false;
	
	// list of subset of observer id, on which current query should use when determine location condition; this is to avoid overcount from different camera view. 
	public static ArrayList<String>  obsSubset = null;
	
	// Whether or not this is an object specification query.
	public static boolean objectSpecQuery = false;
	
		
	public MseeFunction() { }
	
	public static boolean IsEntityType(ArgType  arg_type)
	{
		if ((arg_type == ArgType.OBJECT_ID) ||
			(arg_type == ArgType.EVENT_ID ) ||
			(arg_type == ArgType.ENTITY_ID )
			)
		{
			return true;
		} else
			return false;		
	}
	
	public static boolean IsObjectType(ArgType  arg_type)
	{
		if ((arg_type == ArgType.OBJECT_ID))
		{
			return true;
		} else
			return false;		
	}

	public static boolean IsEventType(ArgType arg_type) {
		if (arg_type == ArgType.EVENT_ID)
		{
			return true;
		} else
			return false;
	}
	

	public static boolean IsObserver(ArgType  arg_type)
	{
		if ((arg_type == ArgType.AGR_OBSERVER))
		{
			return true;
		} else
			return false;		
	}
	
	
	public static boolean IsTimeType(ArgType  arg_type)
	{
		if ((arg_type == ArgType.VIEW_CENTRIC_TIME_PERIOD) ||
			(arg_type == ArgType.SCENE_CENTRIC_TIME_PERIOD))
		{
			return true;
		} else
			return false;		
	}

	public static boolean IsLocationType(ArgType  arg_type)
	{
		if ((arg_type == ArgType.ARG_VIEW_CENTRIC_POINT) ||
			(arg_type == ArgType.ARG_VIEW_CENTRIC_POLYGON) ||
			(arg_type == ArgType.ARG_CARTESIAN_METRIC_POINT) ||
			(arg_type == ArgType.ARG_CARTESIAN_METRIC_POLYGON) ||
			(arg_type == ArgType.ARG_GEODETIC_POINT) ||
			(arg_type == ArgType.ARG_GEODETIC_POLYGON)			
			)
		{
			return true;
		} else
			return false;		
	}
	

	public static boolean IsColorType(ArgType arg_type) {
		if (arg_type == ArgType.ARG_COLOR)
			{
				return true;
			} else
				return false;		
	}


	public static boolean IsTemporalRelationType(ArgType arg_type) {
		if (arg_type == ArgType.ARG_TEMPORAL_RELATION)
		{
			return true;
		} else
			return false;	
	}


	
	public static ArgType GetArgType(String str_arg_type)
	{
		if (str_arg_type.equals(ARG_STR_OBJECT_ID)) {  return ArgType.OBJECT_ID; }
		else if (str_arg_type.equals(ARG_STR_EVENT_ID)) {  return ArgType.EVENT_ID; }
		else if (str_arg_type.equals(ARG_STR_ENTITY_ID)) {  return ArgType.ENTITY_ID; }
		
		
		
		else if (str_arg_type.equals(ARG_STR_VIEW_CENTRIC_POINT)) {  return ArgType.ARG_VIEW_CENTRIC_POINT; }		
		else if (str_arg_type.equals(ARG_STR_VIEW_CENTRIC_POLYGON)) {  return ArgType.ARG_VIEW_CENTRIC_POLYGON; }				
		else if (str_arg_type.equals(ARG_STR_CARTESIAN_METRIC_POINT)) {  return ArgType.ARG_CARTESIAN_METRIC_POINT; }
		else if (str_arg_type.equals(ARG_STR_CARTESIAN_METRIC_POLYGON)) {  return ArgType.ARG_CARTESIAN_METRIC_POLYGON; }
		else if (str_arg_type.equals(ARG_STR_GEODETIC_POINT)) {  return ArgType.ARG_GEODETIC_POINT; }
		else if (str_arg_type.equals(ARG_STR_GEODETIC_POLYGON)) {  return ArgType.ARG_GEODETIC_POLYGON; }
		
		else if (str_arg_type.equals(ARG_STR_VIEW_CENTRIC_TIME_PERIOD)) {  return ArgType.VIEW_CENTRIC_TIME_PERIOD; }
		else if (str_arg_type.equals(ARG_STR_SCENE_CENTRIC_TIME_PERIOD)) {  return ArgType.SCENE_CENTRIC_TIME_PERIOD; }

		else if (str_arg_type.equals(ARG_STR_COLOR)) {  return ArgType.ARG_COLOR; }
		else if (str_arg_type.equals(ARG_STR_TEMPORAL_RELATION)) {  return ArgType.ARG_TEMPORAL_RELATION; }

		else if (str_arg_type.equals(ARG_STR_EQ)) {  return ArgType.ARG_EQ; }
		else if (str_arg_type.equals(ARG_STR_GT)) {  return ArgType.ARG_GT; }
		else if (str_arg_type.equals(ARG_STR_LT)) {  return ArgType.ARG_LT; }
		else if (str_arg_type.equals(ARG_STR_GTE)) {  return ArgType.ARG_GTE; }
		else if (str_arg_type.equals(ARG_STR_LTE)) {  return ArgType.ARG_LTE; }

		else if (str_arg_type.equals(ARG_STR_SET_NAME)) {  return ArgType.ARG_SET_NAME; }
		
		else if (str_arg_type.equals(ARG_STR_FUNCTION)) {  return ArgType.ARG_FUNCTION; }
		else if (str_arg_type.equals(ARG_STR_LOGICAL_OPERATOR)) {  return ArgType.ARG_LOGICAL_OPERATOR; }
		

		else if (str_arg_type.equals(ARG_STR_MIN_QUANTITY)) {  return ArgType.ARG_MIN_QUANTITY; }
		else if (str_arg_type.equals(AGR_STR_MAX_QUANTITY)) {  return ArgType.AGR_MAX_QUANTITY; }
		
		else if (str_arg_type.equals(ARG_STR_GT_QUANTITY)) {  return ArgType.AGR_GT_QUANTITY; }
		else if (str_arg_type.equals(AGR_STR_LT_QUANTITY)) {  return ArgType.AGR_LT_QUANTITY; }
		else if (str_arg_type.equals(AGR_STR_NOT_QUANTITY)) {  return ArgType.AGR_NOT_QUANTITY; }
		
		else if (str_arg_type.equals(AGR_STR_OBSERVER)) {  return ArgType.AGR_OBSERVER; }			
				
		else if (str_arg_type.equals(ARG_STR_UNKNOWN)) {  return ArgType.UNKNOWN; }
		
		System.out.println("DEBUG ERROR MseeFunction:GetArgType unrecognized str_arg_type " + str_arg_type);
		
		return ArgType.UNKNOWN;		
	}	

	
	public static String GetStringFromNodeValue(ArgType argtype, NodeValue nv)
	{
		String strA = null;		
		switch (argtype)
		{
			case OBJECT_ID:
			case EVENT_ID:
			case ENTITY_ID:
			case AGR_OBSERVER:
				strA = nv.getNode().toString();
				break;		
				
			case ARG_EQ:
			case ARG_GT:
			case ARG_LT:
			case ARG_GTE:
			case ARG_LTE:
			case ARG_MIN_QUANTITY:
			case AGR_MAX_QUANTITY:
			case AGR_GT_QUANTITY:
			case AGR_LT_QUANTITY:
			case AGR_NOT_QUANTITY:			

				strA = nv.getString();
				
				break;		
									
			case ARG_VIEW_CENTRIC_POINT:
			case ARG_VIEW_CENTRIC_POLYGON:				
			case ARG_CARTESIAN_METRIC_POINT:
			case ARG_CARTESIAN_METRIC_POLYGON:
			case ARG_GEODETIC_POINT:
			case ARG_GEODETIC_POLYGON:			
				
			case VIEW_CENTRIC_TIME_PERIOD:
			case SCENE_CENTRIC_TIME_PERIOD:
			
			case ARG_COLOR:
			case ARG_TEMPORAL_RELATION:
				
			case ARG_SET_NAME:				
			case ARG_FUNCTION:
			case ARG_LOGICAL_OPERATOR:
									
				strA = nv.getString();
				break;
				
			default:
				System.out.println("DEBUG ERROR MseeFunction:GetStringFromNodeValue unhandled argtype");
				break;				 
		}
		return strA;
	}


	public static boolean IsTwoObjectsBothAtTime(
			CObjectData object_agent, CObjectData object_patient,
			TimeData time_data) {

		if (time_data!=null)
		{
			if (false == TimeFunctions.IsAtTime(object_agent.GetTimeData(), time_data))
				return false; 
			if (false == TimeFunctions.IsAtTime(object_patient.GetTimeData(), time_data))
				return false; 				
		}	
	
		return true; 
	}
	
	public static boolean IsTwoObjectsBothAtTimeLocation(
			CObjectData object_agent, CObjectData object_patient,
			TimeData time_data, LocationData loc_data) {

		if (time_data!=null)
		{
			if (false == TimeFunctions.IsAtTime(object_agent.GetTimeData(), time_data))
				return false; 
			if (false == TimeFunctions.IsAtTime(object_patient.GetTimeData(), time_data))
				return false; 				
		}
		
		if (loc_data!=null)
		{
			if (false == MseeDataset.IsObjectAtLocation(object_agent,loc_data))
				return false; 
			if (false == MseeDataset.IsObjectAtLocation(object_patient,loc_data))
				return false; 				
		}
		return true; 
	}

	public static boolean IsTwoObjectsHaveSameObsId(CObjectData object_agent,
			CObjectData object_patient) {
		boolean b =  ( object_agent.GetObsId().compareTo(object_patient.GetObsId()) == 0);
		/*
		System.err.println("DEBUG MseeFunction IsTwoObjectsHaveSameObsId obs_id " 
			+ object_agent.GetObsId()  + " "+ object_patient.GetObsId() + " " 
			+ " id "+ object_agent.id  + " "+ object_patient.id + " "
			+ b);
			*/ 
		return b;
	}

	public static void SetObsSubset(ArrayList<String> arrayList) {
		MseeFunction.obsSubset = arrayList;		
		
		for (String str : MseeFunction.obsSubset)
		{
			System.out.println("\tMseeFunction.obsSubset " + str);
		}
		
	}

	public static boolean IsInObsSubset(String observer_id) {
		if (MseeFunction.obsSubset == null || MseeFunction.obsSubset.size() == 0)		// subset is not set, return true;
			return true;
		
		for (String obs_str : MseeFunction.obsSubset)
		{
			if (obs_str.compareTo(observer_id) ==0)
			{	return true;
			}			
		}
		
		// System.out.println("DEBUG IsInObsSubset   ------------------ return false   observer_id "+ observer_id);
		
		
		
		
		return false; 		
	}	
}
	