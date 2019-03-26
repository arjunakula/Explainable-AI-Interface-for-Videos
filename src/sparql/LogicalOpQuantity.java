package sparql;

import java.sql.Date;
import java.util.ArrayList;

import com.hp.hpl.jena.sparql.expr.NodeValue;

import sparql.LogicalFunctions.LogicalOperatorType;
import sparql.MseeFunction.ArgType;
import sparql.RecordCardinalityNode.RecordCardinalityNodeType;

public class LogicalOpQuantity implements TemporalRelationChildNode {

	LogicalOperatorType logicalOperatorType = LogicalOperatorType.UNKNOWN;
	
	ArrayList<TemporalRelationChildNode> childnode_list = new ArrayList<TemporalRelationChildNode>();
	
		
	// the set of time intervals for each the set quantity condition is valid
	ArrayList< TimeData > timeDataList = new ArrayList< TimeData >();
	
	
	@Override
	public int ParseArgList(ArrayList<ArgType> argType_list,
			ArrayList<String> argStr_list, int l1) {
		// TODO Auto-generated method stub

		if (l1 >= argType_list.size())
			return l1;
		
		int l2 = l1; 
		
		// first arg must be the set name		
		if (argType_list.get(l2) == ArgType.ARG_LOGICAL_OPERATOR)
		{
			this.logicalOperatorType = LogicalFunctions.GetLogicalOperatorType(argStr_list.get(l2));
			l2++;  // go to next argument list
		} else
		{
			System.err.println("ERROR LogicalOpQuantity:ParseArgList first arg is not ARG_LOGICAL_OPERATOR");
			return l2; 			
		}
		
		int num_arg_needed = LogicalFunctions.GetNumArgumentForLogicalOperator(this.logicalOperatorType);
		int num_arg_found= 0; 		
		
		while (l2 < argType_list.size())
		{
			if (argType_list.get(l2) == ArgType.ARG_SET_NAME)
    		{	// this child node is a set
    			if (num_arg_found == num_arg_needed) // enough argument found, exit loop
				{
    				return l2;
				}
    			childnode_list.add((TemporalRelationChildNode) new SetQuantity());
    			num_arg_found++;
    			l2 = childnode_list.get(num_arg_found-1).ParseArgList(argType_list, argStr_list,  l2);
    			if (num_arg_found == num_arg_needed) // enough argument found, exit loop
				{
    				return l2;
				}
    		} 
    		if (argType_list.get(l2) == ArgType.ARG_LOGICAL_OPERATOR)
    		{
    			// this child node is a logical operation (and/or/not)
    			if (num_arg_found == num_arg_needed) // enough argument found, exit loop
				{
    				return l2;
				}
    			childnode_list.add((TemporalRelationChildNode) new LogicalOpQuantity());
    			num_arg_found++;
    			l2 = childnode_list.get(num_arg_found-1).ParseArgList(argType_list, argStr_list,  l2);
    			if (num_arg_found == num_arg_needed) // enough argument found, exit loop
				{
    				return l2;
				}
    		} else
    		{
    			return l2;
    		}    		
		}	
		return l2;	
		 
	}

	@Override
	public void ApplyQuantitiesConditions() {
		// TODO Auto-generated method stub
		
		System.out.println("DEBUG LogicalOpQuantity: ApplyQuantitiesConditions running" );
		
		
		timeDataList.clear();
		
		// apply quantities conditions in child nodes first
		for ( TemporalRelationChildNode node : childnode_list)
		{
			node.ApplyQuantitiesConditions();
		}
		
		// apply the logical operator (and, or, not)
		switch (this.logicalOperatorType)
		{
			case AND:
				if (this.childnode_list.size() == 2)
				{
					for ( TimeData data1 : childnode_list.get(0).GetTimeDataList())
						for ( TimeData data2 : childnode_list.get(1).GetTimeDataList() )
						{	
							// check if the two time data have intersection; if so, add to the new data
							TimeData new_data = TimeFunctions.GetSceneTimeIntersect(data1, data2);
							if (new_data != null)
							{
								this.timeDataList.add(new_data);
							}
						}
		 			
		 			// after adding pair wise intersection, the order might be unsorted
		 			TimeFunctions.SortTimeData(this.timeDataList);
					
				} else
				{
					System.err.println("ERROR LogicalOpQuantity ApplyQuantitiesConditions  AND operator; expect two children; but found " 
							+ this.childnode_list.size());
				}		 			
				break;
			case OR:
				// take union
				if (this.childnode_list.size() == 2)
				{
					for ( TimeData data1 : childnode_list.get(0).GetTimeDataList())
					{
						this.timeDataList.add(data1);
					}
					
					for ( TimeData data2 : childnode_list.get(1).GetTimeDataList() )
					{
						this.timeDataList.add(data2);
					}
								 			
		 			// sort and merge overlapping time intervals
		 			TimeFunctions.SortAndMergeOverlappingTimeData(this.timeDataList);
		 			
				} else
				{
					System.err.println("ERROR LogicalOpQuantity ApplyQuantitiesConditions  OR operator; expect two children; but found " 
							+ this.childnode_list.size());
				}	
				break;
			case NOT:
				// we expect only one child here
				if (this.childnode_list.size() == 1)
				{
					TimeData sceneTimeRange = MseeDataset.GetDatasetSceneTimeRange();
					

					// use date_m to keep track of when the next valid time interval should starts
					Date date_m = (Date) sceneTimeRange.mSceneTime_Start.clone();
					
					for (TimeData d : childnode_list.get(0).GetTimeDataList())
					{
						if (d.hasValidSceneTime() == false)
							continue;
						
						if (d.mSceneTime_Start.compareTo(d.mSceneTime_End) >=0)
						{
							// either the time is an instance or has invalid time interval, ignore
							continue;
						}		
						
						if (date_m.compareTo(d.mSceneTime_Start) >= 0 )
						{
							// date_m meets with (or is later) than the start of the time interval
							// no need to create a new time interval, start search from the end
							date_m = (Date) TimeFunctions.MaxDate(date_m, d.mSceneTime_End);
							continue;
						} else
						{
							// here, d starts after date_m, so we can create a new valid time_interval
							TimeData new_time = new TimeData();
							new_time.SetSceneTimePeriod(date_m, d.mSceneTime_Start);
							this.timeDataList.add(new_time);
							
							// start search again from mSceneTime_End
							date_m = (Date) d.mSceneTime_End.clone();
						}
					}
					
					// check if we need to add one last time interview
					if (date_m.compareTo(sceneTimeRange.mSceneTime_End) < 0 )
					{
						TimeData new_time = new TimeData();
						new_time.SetSceneTimePeriod(date_m, sceneTimeRange.mSceneTime_End);
						this.timeDataList.add(new_time);			
					}	
				} else
				{
					System.err.println("ERROR LogicalOpQuantity ApplyQuantitiesConditions  OR operator; expect two children; but found " 
							+ this.childnode_list.size());
				}	
				break;
			default:
				System.err.println("ERROR LogicalOpQuantity ApplyQuantitiesConditions unrecognized/unhandled operator; " + this.logicalOperatorType);
				break;
		}		
		
		System.out.println("DEBUG LogicalOpQuantity: ApplyQuantitiesConditions ending" );
		
	}

	@Override
	public ArrayList<TimeData> GetTimeDataList() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		return this.timeDataList;
	}

	@Override
	public TimeData GetTimeRange() {
		if (this.timeDataList.isEmpty())
			return null;
		
		TimeData data = this.timeDataList.get(0).clone();
		for (TimeData t : this.timeDataList)
		{
			data.ExtendSceneTimeRange(t);		
		}		
		return data;
	}

}
