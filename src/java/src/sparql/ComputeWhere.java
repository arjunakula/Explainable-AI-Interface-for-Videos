package sparql;

import java.util.ArrayList;
import java.util.List;

import sparql.LocationData.LocationType;
import sparql.RecordCardinalityNode.RecordCardinalityNodeType;
import sparql.TimeFunctions.TemporalRelation;

import TextParser.CEventData;
import TextParser.CObjectData;
import TextParser.CPositionData;

import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.util.Utils;

// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html

/*
 
RecordCardinality("SET-NAME","set-woman-picking-up-box","EVENT_ID",?behavior1,"AGENT",?object1)
RecordCardinality("SET-NAME","set-___","EVENT_ID",?action,"AGENT",?object1,"PATIENT",?object2)
RecordCardinality("SET-NAME","set-___","EVENT_ID",?puttingin1,"AGENT",?object1,"PATIENT",?object2,"DESTINATION",?object3)
RecordCardinality("SET-NAME","set-___","EVENT_ID",?relationship1,"AGENT",?object1,"PATIENT",?object2)
?relationship1 = same-object, part-of, on, inside, outside, below, or same-motion

RecordCardinality("SET-NAME","set-___","VIEW_CENTRIC_TIME_PERIOD","___","EVENT_ID",?behavior1,"AGENT",?object1)
RecordCardinality("SET-NAME","set-___","VIEW_CENTRIC_POINT","___","EVENT_ID",?behavior1,"AGENT",?object1)
RecordCardinality("SET-NAME","set-___","VIEW_CENTRIC_TIME_PERIOD","___","VIEW_CENTRIC_POINT","___","EVENT_ID",?behavior1,"AGENT",?object1)


  FILTER (fn:ComputeWhere("OBSERVER","obs-HC3","VIEW_CENTRIC_TIME_PERIOD","37503;37703;view-HC3","OBJECT_ID","05afe2e2-fba7-4c45-9351-74aced1f088b")) .
  FILTER (fn:ComputeWhere("VIEW_CENTRIC_TIME_PERIOD","37503;37703;view-HC3","OBJECT_ID","05afe2e2-fba7-4c45-9351-74aced1f088b")) .
    
 */



public class ComputeWhere extends FunctionBase
{	

	// public static ArrayList<TimeData>   nonpolar_when_answers = new ArrayList<TimeData>();
 	public static ArrayList<LocationData>   nonpolar_where_answers = new ArrayList<LocationData>();

	public ComputeWhere() { super() ; }
	
	// obsolete; this cause a bug; move to CardinalityRecordList Classes
	// public static  ArrayList< CardinalityRecord > cardinalityRecordList = new ArrayList< CardinalityRecord >(); 
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
 //       if ( args.size()  != 6 )
 //           throw new QueryBuildException("Function '"+Utils.className(this)+"' takes six arguments") ;
       
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	// if (MseeFunction.bVerbose)
    	if (true)
    	{
    		System.out.println("DEBUG ComputeWhere running");
    		System.out.println("DEBUG ComputeWhere args.size(): " + args.size());
    	}
		
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
        
        
        
        // get index of argument by functions
        int index_object = -1;
        int index_time = -1;
        int index_observer = -1; 
        
        for (int i = 0; i < args.size()/2 ; i++)
        {	if (MseeFunction.IsObjectType(argType_list.get(i)))
        	{	index_object = i;
        	} else if (MseeFunction.IsTimeType(argType_list.get(i)))
        	{	index_time = i;
        	} else if (MseeFunction.IsObserver(argType_list.get(i)))
        	{	index_observer = i;
        	}        
        }
         
        /* mw replaced
		if ((index_object ==-1) || (index_time ==-1) || (index_observer ==-1)  )
		{
		    throw new ARQInternalErrorException(Utils.className(this)+": ComputeWhere: unable to parse arguments") ;
	    }*/ 
		
		if ((index_object ==-1) || (index_time ==-1)  )
		{
		    throw new ARQInternalErrorException(Utils.className(this)+": ComputeWhere: unable to parse arguments") ;
	    }
		
		String str_object_id = argStr_list.get(index_object);
        String str_observer = null;
        if (index_observer !=-1)
        {	str_observer = argStr_list.get(index_observer);
        }
        
        System.out.println("DEBUG ComputeWhere str_object_id: " + str_object_id);
        
        
        TimeData conditionTime ;
		try {
			conditionTime = MseeDataset.GetTimeData(argType_list.get(index_time), argStr_list.get(index_time));
		} catch (MseeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			 throw new ARQInternalErrorException(Utils.className(this)+": ComputeWhere: unable to get time condition ") ;
		}
		
		System.out.println("DEBUG ComputeWhere conditionTime: " );
		conditionTime.PrintTimeData();
        	
        
        ArrayList<CObjectData> objectDataList = MseeDataset.FindObjectDataList_or_parents(str_object_id);
        

        System.out.println("DEBUG ComputeWhere objectDataList.size(): "  + objectDataList.size());
        
        
        // 	result; recordCardinality should always return true
        Boolean b = false;	
	
		for (CObjectData object_data : objectDataList)
		{
			if (false == MseeFunction.IsInObsSubset(object_data.GetObsId()))
			{
				//System.out.println("DEBUG IsObjectAtTime  IsInObsSubset ------------------ return false");
				continue;
			}
			
						
		/*	
		 * mw: disable this, because the object's view id, need not be the same as in condition time.  
		 * if(false == MseeDataset.isTheViewCorrect(object_data.id, object_data.GetViewId(), conditionTime))
			{
				continue;
			}
			*/ 
			
			
			// if str_observer is specified, then check that the view id is the same as observer id, but need to take out "obs-" and "view-"
			if (str_observer!= null)
			{
			
				if(false == MseeDataset.isView_Obs_Correct(str_observer, object_data.GetViewId()))
				{
					continue;
				}
			}
			
			// get enclosing index of CPositionData
			int[] indices = MseeDataset.findSamplesForTime(object_data, conditionTime);
			int istart = indices[0];
			int iend = indices[1];
			
			System.out.println("DEBUG ComputeWhere istart:  " + istart);
			System.out.println("DEBUG ComputeWhere iend:  " + iend);
			
			if ( istart > iend ) {
				if ( MseeFunction.bVerbose ) {
					System.out.println("DEBUG ComputeWhere: no valid times found" );
				}
				continue;
			}
			
			int imid = (istart+iend) /2;
			System.out.println("DEBUG ComputeWhere imid:  " + imid);
			
			CPositionData pos_data  = object_data.positionVt.elementAt( Math.min(istart+10, iend));		// use start for sig demo 
			// CPositionData pos_data  = object_data.positionVt.elementAt(imid);
			
			LocationData loc_data   =null;
			if (str_observer!= null)
			{
				loc_data  = pos_data.GetLocationData(LocationType.VIEW_CENTRIC_POLYGON, object_data.GetViewId());
			} else
			{
				loc_data  = pos_data.GetLocationData(LocationType.CARTESIAN_METRIC_POINT, object_data.GetViewId());				
			}
			nonpolar_where_answers.add(loc_data);
			b = true; 
			// break; 
		}
        
	

		
		// if (MseeFunction.bVerbose)
		if (true)
    	{
			
			System.out.println("DEBUG ComputeWhere ending");
			System.out.println("DEBUG nonpolar_where_answers size " + nonpolar_where_answers.size());
			for (LocationData loc : nonpolar_where_answers)
			{
				loc.Printf();				
			}
    	}
    	
		System.out.println("DEBUG ComputeWhere ended");
		
		// return 
		return NodeValue.makeBoolean(b); 
	}

}
