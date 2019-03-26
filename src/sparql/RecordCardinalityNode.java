package sparql;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import TextParser.CEventData;
import TextParser.CObjectData;

import sparql.ColorFunctions.Color;
import sparql.LocationData.LocationType;
import sparql.LogicalFunctions.LogicalOperatorType;
import sparql.MseeFunction.ArgType;
import sparql.TimeData.TimeDataType;

/**
 * @author mlee
 *
 * RecordCardinalityNode is a node in the argument list of the RecordCardinality Function.
 * 
 * The argument list has a tree structure; hence the argument list has to be parsed and organized into this tree structure
 * and each node may have a list of child node
 * 
 * Each node has a type, defined in RecordCardinalityNodeType
 */
public class RecordCardinalityNode {
	
	public enum RecordCardinalityNodeType { 
		SET,
		OBJECT,
		EVENT,
		TIME,
		LOCATION,
		FUNCTION,
		COLOR,
		LOGICAL_OPERATOR,
		};
		
	public enum FilterFunctionType {
			CARRYING,
			CLEAR_LINE_OF_SIGHT,
			CLOSER,
			CROSSING,
			COLOR,
			DISMOUNTING,
			DRIVING,
			ENTERING,
			EXITING,
			FACING,
			FACING_OPPOSITE,
			FARTHER,
			FOLLOWING,
			INSIDE,
			LOADING,
			MAKING_UTURN,
			MOUNTING,
			MOVING,
			OCCLUDING,
			ON,
			OPPOSITE_MOTION,
			OUTSIDE,
			PASSING,	
			SAME_MOTION,
			SAME_OBJECT,
			STARTING,
			STOPPING,
			TOGETHER,
			TURNING,			
			TURNING_LEFT,
			TURNING_RIGHT,
			UNKNOWN,
			UNLOADING
			};
			


	
	RecordCardinalityNodeType type;
	ArrayList< RecordCardinalityNode > childNodeList = new ArrayList< RecordCardinalityNode >();
	
	String setName;
	String objectId;
	String eventId;
	TimeData timeData;
	LocationData locationData;
	String functionName;
	FilterFunctionType	functionType = FilterFunctionType.UNKNOWN;
	String logicalOperatorName;
	LogicalOperatorType logicalOperatorType = LogicalOperatorType.UNKNOWN;
	
	
	// this is the list of CardinalityData (time interval) for which the query condition is true
	// ArrayList< CardinalityData > cardinalityDataList = new ArrayList< CardinalityData >();
	
	CardinalityData cardinalityData = new CardinalityData();
	
	private Color colorType = Color.COLOR_NA;
	private String colorName;
		
	ArgType input_arg_type = ArgType.UNKNOWN;
	String input_arg_str = "unknown";
	
	public RecordCardinalityNode(ArgType argType, String string) throws MseeException {
		// TODO Auto-generated constructor stub
		this.input_arg_type = argType;
		this.input_arg_str = string;
		switch (argType)
		{
			case ARG_SET_NAME:
				this.type = RecordCardinalityNodeType.SET;
				this.setName = string; 
				break;
			case OBJECT_ID:
				this.type = RecordCardinalityNodeType.OBJECT;
				this.objectId = string; 
				break;
			case EVENT_ID:
				this.type = RecordCardinalityNodeType.EVENT;
				this.eventId = string; 
				break;
			case SCENE_CENTRIC_TIME_PERIOD:
				this.type = RecordCardinalityNodeType.TIME;
				timeData = new TimeData(TimeDataType.SCENE_CENTRIC_TIME, string);				
				break;
			case VIEW_CENTRIC_TIME_PERIOD:				
				this.type = RecordCardinalityNodeType.TIME;
				timeData = new TimeData(TimeDataType.VIEW_CENTRIC_TIME, string);				
				break;
			case ARG_VIEW_CENTRIC_POINT:
				this.type = RecordCardinalityNodeType.LOCATION;
				locationData = new LocationData(LocationType.VIEW_CENTRIC_POINT, string);
				break;
			case ARG_VIEW_CENTRIC_POLYGON:
				this.type = RecordCardinalityNodeType.LOCATION;
				locationData = new LocationData(LocationType.VIEW_CENTRIC_POLYGON, string);
				break;
			case ARG_CARTESIAN_METRIC_POINT:
				this.type = RecordCardinalityNodeType.LOCATION;
				locationData = new LocationData(LocationType.CARTESIAN_METRIC_POINT, string);
				break;
			case ARG_CARTESIAN_METRIC_POLYGON:
				this.type = RecordCardinalityNodeType.LOCATION;
				locationData = new LocationData(LocationType.CARTESIAN_METRIC_POLYGON, string);
				break;
			case ARG_GEODETIC_POINT:
				this.type = RecordCardinalityNodeType.LOCATION;
				locationData = new LocationData(LocationType.GEODETIC_POINT, string);
				break;
			case ARG_GEODETIC_POLYGON:
				this.type = RecordCardinalityNodeType.LOCATION;
				locationData = new LocationData(LocationType.GEODETIC_POLYGON, string);
				break;
			case ARG_FUNCTION:
				this.type = RecordCardinalityNodeType.FUNCTION;
				this.functionName = string;
				this.functionType = GetFunctionType(string);
				break;
			case ARG_LOGICAL_OPERATOR:
				this.type = RecordCardinalityNodeType.LOGICAL_OPERATOR;
				this.logicalOperatorName = string; 
				this.logicalOperatorType = LogicalFunctions.GetLogicalOperatorType(string);
				break;
			case ARG_COLOR:
				this.type = RecordCardinalityNodeType.COLOR;
				this.colorName = string; 
				this.colorType = ColorFunctions.GetColor(string);
				break;
				
				
		}
	}			

	
	private FilterFunctionType GetFunctionType(String string) {
		// TODO Auto-generated method stub
		if (string.equals("IsCarrying")) {  return FilterFunctionType.CARRYING; }
		else if (string.equals("IsClearLineOfSight")) {  return FilterFunctionType.CLEAR_LINE_OF_SIGHT; }
		else if (string.equals("IsCloser")) {  return FilterFunctionType.CLOSER; }
		else if (string.equals("IsColor")) {  return FilterFunctionType.COLOR; }
		else if (string.equals("IsCrossing")) {  return FilterFunctionType.CROSSING; }
		else if (string.equals("IsDismounting")) {  return FilterFunctionType.DISMOUNTING; }
		else if (string.equals("IsDriving")) {  return FilterFunctionType.DRIVING; }
		else if (string.equals("IsEntering")) {  return FilterFunctionType.ENTERING; }
		else if (string.equals("IsExiting")) {  return FilterFunctionType.EXITING; }
		else if (string.equals("IsFacing")) {  return FilterFunctionType.FACING; }
		else if (string.equals("IsFacingOpposite")) {  return FilterFunctionType.FACING_OPPOSITE; }
		else if (string.equals("IsFarther")) {  return FilterFunctionType.FARTHER; }		
		else if (string.equals("IsFollowing")) {  return FilterFunctionType.FOLLOWING; }
		else if (string.equals("IsInside")) {  return FilterFunctionType.INSIDE; }
		else if (string.equals("IsLoading")) {  return FilterFunctionType.LOADING; }
		else if (string.equals("IsMakeUTurn")) {  return FilterFunctionType.MAKING_UTURN; }
		else if (string.equals("IsMounting")) {  return FilterFunctionType.MOUNTING; }
		else if (string.equals("IsMoving")) {  return FilterFunctionType.MOVING; }
		else if (string.equals("IsOccluding")) {  return FilterFunctionType.OCCLUDING; }
		else if (string.equals("IsOn")) {  return FilterFunctionType.ON; }
		else if (string.equals("IsOppositeMotion")) {  return FilterFunctionType.OPPOSITE_MOTION; }
		else if (string.equals("IsOutside")) {  return FilterFunctionType.OUTSIDE; }
		else if (string.equals("IsPassing	")) {  return FilterFunctionType.PASSING	; }
		else if (string.equals("IsSameMotion")) {  return FilterFunctionType.SAME_MOTION; }
		else if (string.equals("IsSameObject")) {  return FilterFunctionType.SAME_OBJECT; }
		else if (string.equals("IsStarting")) {  return FilterFunctionType.STARTING; }
		else if (string.equals("IsStopping")) {  return FilterFunctionType.STOPPING; }
		else if (string.equals("IsTogether")) {  return FilterFunctionType.TOGETHER; }
		else if (string.equals("IsTurning			")) {  return FilterFunctionType.TURNING; }
		else if (string.equals("IsTurningLeft")) {  return FilterFunctionType.TURNING_LEFT; }		
		else if (string.equals("IsTurningRight")) {  return FilterFunctionType.TURNING_RIGHT; }
		else if (string.equals("IsUnloading")) {  return FilterFunctionType.UNLOADING; }
		
		System.out.println("DEBUG ERROR GetFunctionType; unrecognized string " + string);
		
		return FilterFunctionType.UNKNOWN;	
		
	}

	private int GetNumArgumentForFunction(FilterFunctionType functionType) {
		// TODO Auto-generated method stub
		switch (functionType)
		{
		case CARRYING: return 2;
		case CLEAR_LINE_OF_SIGHT: return 2;
		case CLOSER: return 3;
		case COLOR: return 1;
		case CROSSING: return 2;
		case DISMOUNTING: return 2;
		case DRIVING: return 2;
		case ENTERING: return 2;
		case EXITING: return 2;
		case FACING: return 2;
		case FACING_OPPOSITE: return 2;
		case FARTHER: return 3;
		case FOLLOWING: return 2;
		case INSIDE: return 2;
		case LOADING: return 2;
		case MAKING_UTURN: return 1;
		case MOVING: return 1;
		case MOUNTING: return 2;
		case OCCLUDING: return 3;
		case ON: return 2;
		case OPPOSITE_MOTION: return 2;
		case OUTSIDE: return 2;
		case PASSING	: return 2;
		case SAME_MOTION: return 2;
		case SAME_OBJECT: return 2;
		case STARTING: return 1;
		case STOPPING: return 1;
		case TOGETHER: return 2;
		case TURNING: return 1;
		case TURNING_LEFT: return 1;
		case TURNING_RIGHT: return 1;
		case UNLOADING: return 2;
		case UNKNOWN: 
			System.err.println("ERROR GetNumArgumentForFunction invalid type " + functionType );
			return 0;
		default:
			System.err.println("ERROR GetNumArgumentForFunction invalid type " + functionType );
			return 0;		
		}
	}		
	 
	// parse input argument, knowing that this is a root node
	public void SetRootNode(ArrayList<ArgType> argType_list,
			ArrayList<String> argStr_list) throws MseeException {
		// TODO Auto-generated method stub
		//System.out.println("DEBUG SetRootNode");
		this.type = RecordCardinalityNodeType.SET;		
		setName = argStr_list.get(0);		
		
		ArrayList<RecordCardinalityNode> nodelist  = new ArrayList<RecordCardinalityNode>();
		
		// RecordCardinality can have just one argument with the set-name.
		if (argType_list.size() >1)
		{
			for (int i = 1; i < argType_list.size(); i++)
			{
				nodelist.add(new RecordCardinalityNode(argType_list.get(i), argStr_list.get(i)));
			}		
			
			int l1 = 0;
			int l2 = this.ParseNodeList(nodelist, l1);	
		}
	}

	// parse input argument, 
	private int ParseNodeList(ArrayList<RecordCardinalityNode> nodelist, int l1) {
		// TODO Auto-generated method stub
		
		//System.out.println("DEBUG ParseNodeList l1 " + l1 + " of " + nodelist.size() 
		//		+ " type " + this.type 
		//		+ " arg_str " + this.input_arg_str);		
		
		if (l1 >= nodelist.size())
			return l1;
		
		int l2 = l1; 
		
		switch (this.type)
		{
			case SET:
				while (l2 < nodelist.size())
				{
					if  ((nodelist.get(l2).type == RecordCardinalityNodeType.OBJECT) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.EVENT) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.FUNCTION) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.LOGICAL_OPERATOR) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.LOCATION) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.TIME) 					
					)
					{
						RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);		
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);						
					}	else
					{
						System.err.println("ERROR ParseNodeList parsing SET; unrecognized type " + nodelist.get(l2).type + " l2 " +l2  );
						return l2;
					}
				}
				return l2;
				
			case OBJECT:
				while (l2 < nodelist.size())
				{
					if  ((nodelist.get(l2).type == RecordCardinalityNodeType.LOCATION) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.TIME) )
					{
						RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);
						
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
						
					}	else
					{
						return l2;
					}
				}
				return l2;
				
			case EVENT:	
				while (l2 < nodelist.size())
				{
					if  ((nodelist.get(l2).type == RecordCardinalityNodeType.LOCATION) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.TIME) || 
						(nodelist.get(l2).type == RecordCardinalityNodeType.OBJECT) )
					{
						RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);	
						
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
					}	else
					{						
						return l2;
					}
				}
				return l2;
			
			case TIME:
				// no further production
				return l2;

			
			case LOCATION:
				// no further production
				return l2;

			case COLOR:
				// no further production
				return l2;
		
			case FUNCTION:
				int num_arg_needed = GetNumArgumentForFunction(this.functionType);
				int num_arg_found = 0; 
				
				while (l2 < nodelist.size()) 
				{
					if  ((nodelist.get(l2).type == RecordCardinalityNodeType.LOCATION) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.TIME))						
					{
						RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);		
						
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
						
					} else if ((nodelist.get(l2).type == RecordCardinalityNodeType.EVENT) ||
							(nodelist.get(l2).type == RecordCardinalityNodeType.OBJECT))
					{	 
						if (num_arg_found == num_arg_needed) // enough argument found, exit loop
						{
							return l2;
						}
						
						RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);
						
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
						
						num_arg_found++;									
					}  else if (nodelist.get(l2).type == RecordCardinalityNodeType.COLOR)
					{	RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);			
						
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
					} 									
					else
					{
						return l2;
					}
				}
				
				return l2;
		
			case LOGICAL_OPERATOR:				
				int num_arg_needed2 = LogicalFunctions.GetNumArgumentForLogicalOperator(this.logicalOperatorType);
				int num_arg_found2= 0; 				

				while (l2 < nodelist.size()) 
				{
					if  ((nodelist.get(l2).type == RecordCardinalityNodeType.LOCATION) ||
							(nodelist.get(l2).type == RecordCardinalityNodeType.TIME))						
						{
							RecordCardinalityNode node = nodelist.get(l2);
							l2 = node.ParseNodeList(nodelist, l2+1);					
							this.childNodeList.add(node);	
							
							//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
						} 
					else if ((nodelist.get(l2).type == RecordCardinalityNodeType.EVENT) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.FUNCTION) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.LOGICAL_OPERATOR) ||
						(nodelist.get(l2).type == RecordCardinalityNodeType.OBJECT))
					{	
												 
						if (num_arg_found2 == num_arg_needed2) // enough argument found, exit loop
						{
							return l2;
						}
						
						RecordCardinalityNode node = nodelist.get(l2);
						l2 = node.ParseNodeList(nodelist, l2+1);					
						this.childNodeList.add(node);
						
						//System.out.println("\t DEBUG add child node l1 " + l1 + " l2 " + l2);
						
						num_arg_found2++;									
					} else
					{
						return l2;
					}
				}				
				return l2;		

			default:
				System.err.println("ERROR ParseNodeList unhandled type" + this.type);
		}
		return l2;
	}


/* obsolete
 * 	public CardinalityData GetCardinalityData() {
 *
		// TODO Auto-generated method stub
		
		System.err.println("WARN RecordCardinalityNode GetCardinalityData not implemented");
		CardinalityData data = new CardinalityData(); 
		
		
		return data ; 		
	}

	
	
	public CardinalityData GetCardinalityData() {
		// TODO Auto-generated method stub
		ComputeCardinalityData();		
		
		return this.cardinalityData;		
	}
	*/ 


	void ComputeCardinalityData() {
		// TODO Auto-generated method stub
		// System.err.println("WARN. RecordCardinalityNode ComputeCardinalityData NOT Fully Tested \n");
		
		//System.out.println("DEBUG RecordCardinalityNode ComputeCardinalityData "
		//		+ " type " + this.type 
		//		+ " arg_str " + this.input_arg_str);	
		
		// clear list first
		// this.cardinalityDataList.clear();
		
		// set to valid first
		this.cardinalityData.SetAlwaysValid();
		
		// compute child nodes first
		for (RecordCardinalityNode  node : this.childNodeList)
		{
			node.ComputeCardinalityData();
		}
		

		switch (this.type)
		{
			case SET:
				
				for (RecordCardinalityNode  node : this.childNodeList)
				{
					// always take intersection
					this.cardinalityData.Intersect(node.cardinalityData);
				}
				break;				
				
			case OBJECT:
				// find object first				
				CObjectData objectData = MseeDataset.FindObjectData(this.objectId);
				if (objectData == null)
				{	
					//System.out.println("\tDEBUG RecordCardinalityNode OBJECT not found " + this.objectId);
					this.cardinalityData.SetAlwaysInvalid();				
					break;  
				}	else
				{
					this.cardinalityData.Intersect(new CardinalityData(objectData.GetTimeData()));
				}
				
				// intersect with children
				for (RecordCardinalityNode  node : this.childNodeList)
				{
					// always take intersection
					this.cardinalityData.Intersect(node.cardinalityData);
				}				
				break;
								
			case EVENT:	
				// find event first				
				CEventData eventData = MseeDataset.FindEventData(this.eventId);
				if (eventData == null)
				{	
					this.cardinalityData.SetAlwaysInvalid();				
					break;  
				}	else
				{
					this.cardinalityData.Intersect(new CardinalityData(eventData.GetTimeData()));
				}
				
				// intersect with children
				for (RecordCardinalityNode  node : this.childNodeList)
				{
					// always take intersection
					this.cardinalityData.Intersect(node.cardinalityData);
				}				
				break;
							
			case TIME:
				this.cardinalityData.Intersect(new CardinalityData(this.timeData));				
				break; 
			
			case LOCATION:
				// do nothing, always valid
				break; 
				
			case COLOR:
				// do nothing, always valid
				break; 
				
			case FUNCTION:
				ComputeCardinalityData_FilterFunction();
				
				break; 
		
			case LOGICAL_OPERATOR:				
				int num_arg_needed = LogicalFunctions.GetNumArgumentForLogicalOperator(this.logicalOperatorType);
				int num_arg_found = 0; 
				
				switch (this.logicalOperatorType)
				{
					case AND:
						for (RecordCardinalityNode  node : this.childNodeList)
						{
							// always take intersection
							this.cardinalityData.Intersect(node.cardinalityData);
						}
						break;
					case OR:
						// take union
						for (RecordCardinalityNode  node : this.childNodeList)
						{	if ((node.type == RecordCardinalityNodeType.EVENT) ||
								(node.type == RecordCardinalityNodeType.FUNCTION))
							{
								// for children that are event or function, we take the union
								this.cardinalityData.Union(node.cardinalityData);
							} else
							{
								// this could be a time condition, hence, we still take intersection here
								this.cardinalityData.Intersect(node.cardinalityData);
							}
						}
						break;
					case NOT:
						// we expect only one child here
						if (this.childNodeList.size() == 1)
						{
							this.cardinalityData.Negation(this.childNodeList.get(0).cardinalityData);
						} else
						{
							System.err.println("ERROR RecordCardinalityNode ComputeCardinalityData NOT operator; expect only one child; but found " + this.childNodeList.size());
						}	
						break;
					default:
						System.err.println("ERROR RecordCardinalityNode ComputeCardinalityData unrecognized/unhandled operator; " + this.logicalOperatorName);
						this.cardinalityData.SetAlwaysInvalid();
						break;
				}
				
				break;	
		}		
		
		
		//System.out.println("\t\t DEBUG RecordCardinalityNode ComputeCardinalityData type " + this.type
		//		+ " arg_type " + this.input_arg_type 
		//		+ " arg_str " + this.input_arg_str + "\n");		
		//System.out.println("\t number of child " + this.childNodeList.size());
		
		//this.cardinalityData.Print();	
	}
	
	private void ComputeCardinalityData_FilterFunction() {		
		
		TimeData time_data  = null;
		LocationData loc_data = null; 
		Color color_type = Color.COLOR_NA;
		ArrayList<CObjectData> object_data_list = new ArrayList<CObjectData>();		
		
		int object_arg_needed = GetNumArgumentForFunction(this.functionType);
				
		// get arguments
		for (RecordCardinalityNode  child_node : this.childNodeList)
		{
			switch (child_node.type)
			{
				case OBJECT:
					if (object_data_list.size() < object_arg_needed)
					{
						CObjectData objectData = MseeDataset.FindObjectData(child_node.objectId);
						if (objectData != null)
						{
							object_data_list.add(objectData);							  
						}	else
						{
							System.err.println("ERROR recordcardinality ComputeCardinalityData_FilterFunction Object not found " + child_node.objectId);
						}						
					} else
					{
						System.err.println("ERROR recordcardinality ComputeCardinalityData_FilterFunction More object found in children than is required");
					}
					break;
				case TIME:
					time_data = child_node.timeData;
					break;					
				case LOCATION:
					loc_data = child_node.locationData;
					break;
				case COLOR:
					color_type = child_node.colorType;
					break;
				default:
					System.err.println("ERROR recordcardinality ComputeCardinalityData_FilterFunction invalid child node type." + child_node.type);
					break;	
			}				
		}		
		
		ArrayList< TimeData > timeDataList = null;
		if (object_data_list.size() != object_arg_needed )
		{
			System.err.println("ERROR recordcardinality ComputeCardinalityData_FilterFunction object found (" 
					+object_data_list.size() 
					+ ") not equal to objects required ("
					+ object_arg_needed + ")");
			return; 
		}
		
						
		switch (this.functionType)
		{
			case CARRYING :
				timeDataList = SpatialFunctions.GetTime_IsCarrying(time_data, loc_data, 
						object_data_list.get(0), object_data_list.get(1));
				break; 

			case CLEAR_LINE_OF_SIGHT : timeDataList = SpatialFunctions.GetTime_IsClearLineOfSight(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case CLOSER : timeDataList = SpatialFunctions.GetTime_IsCloser(time_data, loc_data, object_data_list.get(0), object_data_list.get(1), object_data_list.get(2));				break;
			case COLOR : timeDataList = SpatialFunctions.GetTime_IsColor(time_data, loc_data, object_data_list.get(0), color_type);				break;
			case CROSSING : timeDataList = SpatialFunctions.GetTime_IsCrossing(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break;
			case DISMOUNTING : timeDataList = SpatialFunctions.GetTime_IsDismounting(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break;
			case DRIVING : timeDataList = SpatialFunctions.GetTime_IsDriving(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case ENTERING : timeDataList = SpatialFunctions.GetTime_IsEntering(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case EXITING : timeDataList = SpatialFunctions.GetTime_IsExiting(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case FACING : timeDataList = SpatialFunctions.GetTime_IsFacing(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case FACING_OPPOSITE : timeDataList = SpatialFunctions.GetTime_IsFacingOpposite(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case FARTHER : timeDataList = SpatialFunctions.GetTime_IsFarther(time_data, loc_data, object_data_list.get(0), object_data_list.get(1), object_data_list.get(2));				break; 
			case FOLLOWING : timeDataList = SpatialFunctions.GetTime_IsFollowing(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case INSIDE : timeDataList = SpatialFunctions.GetTime_IsInside(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break;
			case LOADING : timeDataList = SpatialFunctions.GetTime_IsLoading(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break;
			case MAKING_UTURN : timeDataList = SpatialFunctions.GetTime_IsMakeUTurn(time_data, loc_data, object_data_list.get(0));				break;
			case MOUNTING : timeDataList = SpatialFunctions.GetTime_IsMounting(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break;
			case MOVING : timeDataList = SpatialFunctions.GetTime_IsMoving(time_data, loc_data, object_data_list.get(0));				break; 
			case OCCLUDING : timeDataList = SpatialFunctions.GetTime_IsOccluding(time_data, loc_data, object_data_list.get(0), object_data_list.get(1), object_data_list.get(2));				break; 
			case ON : timeDataList = SpatialFunctions.GetTime_IsOn(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case OPPOSITE_MOTION : timeDataList = SpatialFunctions.GetTime_IsOppositeMotion(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case OUTSIDE : timeDataList = SpatialFunctions.GetTime_IsOutside(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case PASSING	 : timeDataList = SpatialFunctions.GetTime_IsPassing(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case SAME_MOTION : timeDataList = SpatialFunctions.GetTime_IsSameMotion(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case SAME_OBJECT : timeDataList = SpatialFunctions.GetTime_IsSameObject(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case STARTING : timeDataList = SpatialFunctions.GetTime_IsStarting(time_data, loc_data, object_data_list.get(0));				break; 
			case STOPPING : timeDataList = SpatialFunctions.GetTime_IsStopping(time_data, loc_data, object_data_list.get(0));				break; 
			case TOGETHER : timeDataList = SpatialFunctions.GetTime_IsTogether(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break; 
			case TURNING  : timeDataList = SpatialFunctions.GetTime_IsTurning(time_data, loc_data, object_data_list.get(0));				break; 
			case TURNING_LEFT : timeDataList = SpatialFunctions.GetTime_IsTurningLeft(time_data, loc_data, object_data_list.get(0));				break; 
			case TURNING_RIGHT  : timeDataList = SpatialFunctions.GetTime_IsTurningRight(time_data, loc_data, object_data_list.get(0));				break; 
			case UNLOADING : timeDataList = SpatialFunctions.GetTime_IsUnloading(time_data, loc_data, object_data_list.get(0), object_data_list.get(1));				break;
				default:
				System.err.println("ERROR recordcardinality ComputeCardinalityData_FilterFunction unrecognize filter function");
				break;				
		}
		
		if (timeDataList != null)
		{
			for (TimeData timedata : timeDataList)
			{
				this.cardinalityData.AddTimeData(timedata);
			}
			this.cardinalityData.SortAndMergeOverlappingTimeData();
		}
		
	}
}
