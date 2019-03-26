package sparql;

import java.util.ArrayList;
import java.util.List;

import sparql.MseeFunction.ArgType;
import sparql.RecordCardinalityNode.RecordCardinalityNodeType;
import sparql.TimeFunctions.TemporalRelation;

import TextParser.CEventData;
import TextParser.CObjectData;

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

  
  FILTER (fn:ComputeWhen("SET_NAME","set-person-1","MIN_QUANTITY","1.0")) .
  

 */



public class ComputeWhen extends FunctionBase
{
	

	public static ArrayList<TimeData>   nonpolar_when_answers = new ArrayList<TimeData>();
// 	public static ArrayList<LocationData>   nonpolar_where_answers = new ArrayList<LocationData>();


	public ComputeWhen() { super() ; }
	
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
    	if (MseeFunction.bVerbose)
    	{
    		System.out.println("DEBUG ComputeWhen running");
    	}
		
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
        
        Boolean bRes = false;		

		
        if (argType_list.get(0) != ArgType.ARG_SET_NAME)
        {
        	System.err.println("ERROR ComputeWhen invalid first argument ");
    		return NodeValue.makeBoolean(bRes); 	
        }
        
        
        if (argType_list.get(0) != ArgType.ARG_SET_NAME)
        {
        	System.err.println("ERROR ComputeWhen invalid first argument ");
    		return NodeValue.makeBoolean(bRes); 	
        }
        
        SetQuantity setQuantity_data = new SetQuantity();
        
        setQuantity_data.ParseArgList(argType_list, argStr_list,  0);
        
        setQuantity_data.ApplyQuantitiesConditions();
        
        ArrayList< TimeData > time_data_list = setQuantity_data.GetTimeDataList();
        
        
        System.out.println("DEBUG ComputeWhen time_data_list.size(): " + time_data_list.size());
        
        for (TimeData time_data : time_data_list)
        {
        	nonpolar_when_answers.add(time_data);
        	bRes = true; 
        }        
		
		// return 
		return NodeValue.makeBoolean(bRes); 
	}

}
