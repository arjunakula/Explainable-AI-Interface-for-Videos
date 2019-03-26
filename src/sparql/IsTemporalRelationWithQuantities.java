package sparql;

import java.util.ArrayList;
import java.util.List;

import sparql.MseeFunction.ArgType;
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
 * 
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","PRECEDES","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","MEETS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","OVERLAPS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","FINISHED_BY","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","CONTAINS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","STARTS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","EQUALS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","BEFORE","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","STRICTLY_BEFORE","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","AFTER","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","SAME_TIME_AS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","SAME_TIME_AS","SET_NAME","___","MIN_QUANTITY",?number1,"SET_NAME","___","MIN_QUANTITY",?number3,"MAX_QUANTITY",?number4)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","SAME_TIME_AS","SET_NAME","___","MIN_QUANTITY",?number1,"MAX_QUANTITY",?number2,"SET_NAME","___","MIN_QUANTITY",?number3)
IsTemporalRelationWithQuantities("TEMPORAL_RELATION","SAME_TIME_AS","SET_NAME","___","MIN_QUANTITY",?number1,"MAX_QUANTITY",?number2,"SET_NAME","___","MIN_QUANTITY",?number3,"MAX_QUANTITY",?number4)

*/
			 
public class IsTemporalRelationWithQuantities extends FunctionBase
{

	public IsTemporalRelationWithQuantities() { super() ; }
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
		// TODO implement this
        //if ( args.size()  != 6 )
        //   throw new QueryBuildException("Function '"+Utils.className(this)+"' takes six arguments") ;
      
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	// if (MseeFunction.bVerbose)
    	if (true)
    	{
    		System.out.println("DEBUG IsTemporalRelationWithQuantities running");
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

		
        if ((argType_list.get(0) != ArgType.ARG_TEMPORAL_RELATION) ||
        	(argType_list.get(1) != ArgType.ARG_SET_NAME))
        {
        	System.err.println("ERROR IsTemporalRelationWithQuantities invalid first argument ");
    		return NodeValue.makeBoolean(bRes); 	
        }
        
    	TemporalRelation temporal_relation = TimeFunctions.GetTemporalRelation(argStr_list.get(0)) ;
    	
    	//ArrayList<SetQuantity> set_list = new ArrayList<SetQuantity>();
    	ArrayList<TemporalRelationChildNode> childnode_list = new ArrayList<TemporalRelationChildNode>();
    	
    	
    	// create two sets
    	//set_list.add(new SetQuantity());
    	//set_list.add(new SetQuantity());
    	
    	// get name of first set
    	//set_list.get(0).FindSet(argStr_list.get(1));
    	    	
    	// scan the other arguments to get quantities condition  and the 2nd set
    	int iarg = 1;
    	// int iset = 0;
    	
    	int num_arg_needed = 2;
		int num_arg_found = 0; 
    	
    	while (iarg < argType_list.size() )
    	{
    	
    		if (argType_list.get(iarg) == ArgType.ARG_SET_NAME)
    		{	// this child node is a set
    			if (num_arg_found == num_arg_needed) // enough argument found, exit loop
				{
    			  	System.err.println("ERROR IsTemporalRelationWithQuantities extra set argument found");
    	    		return NodeValue.makeBoolean(bRes); 
				}
    			childnode_list.add((TemporalRelationChildNode) new SetQuantity());
    			num_arg_found++;
    			iarg = childnode_list.get(num_arg_found-1).ParseArgList(argType_list, argStr_list,  iarg);
    		} else
    		if (argType_list.get(iarg) == ArgType.ARG_LOGICAL_OPERATOR)
    		{
    			// this child node is a logical operation (and/or/not)
    			if (num_arg_found == num_arg_needed) // enough argument found, exit loop
				{
    			  	System.err.println("ERROR IsTemporalRelationWithQuantities extra set argument found");
    	    		return NodeValue.makeBoolean(bRes); 
				}
    			childnode_list.add((TemporalRelationChildNode) new LogicalOpQuantity());
    			num_arg_found++;
    			iarg = childnode_list.get(num_arg_found-1).ParseArgList(argType_list, argStr_list,  iarg);
    		} else
    		{
    		  	System.err.println("ERROR IsTemporalRelationWithQuantities, unhandled argType " +
    		  			argType_list.get(iarg) );
    		  	return NodeValue.makeBoolean(bRes); 
    		}
    		
    			
    	}
    	
    	if (num_arg_found != num_arg_needed)
    	{	System.err.println("ERROR IsTemporalRelationWithQuantities: expect two set arguments; but not found");
			return NodeValue.makeBoolean(bRes); 	
    	}
    	
    	System.out.println("DEBUG IsTemporalRelationWithQuantities: ApplyQuantitiesConditions 0" );
    	
    	childnode_list.get(0).ApplyQuantitiesConditions();
    	System.out.println("DEBUG IsTemporalRelationWithQuantities: ApplyQuantitiesConditions 1" );
    	childnode_list.get(1).ApplyQuantitiesConditions();
    	
    	
    	System.out.println("DEBUG IsTemporalRelationWithQuantities: GetTimeDataList" );
    	
//    	if ((set_list.get(0).timeDataList.isEmpty()) ||
//  		(set_list.get(1).timeDataList.isEmpty()))
    	if ((childnode_list.get(0).GetTimeDataList().isEmpty()) ||
    	    (childnode_list.get(1).GetTimeDataList().isEmpty()))    		
    	{
    		System.out.println("DEBUG IsTemporalRelationWithQuantities: timeDataList empty");
			
    		bRes = false;
    	} else
    	{
    		
    		System.out.println("DEBUG IsTemporalRelationWithQuantities: GetTimeRange" );
    		
			bRes = true;
			TimeData t1 = childnode_list.get(0).GetTimeRange();
			TimeData t2 = childnode_list.get(1).GetTimeRange();
			
			System.out.println("DEBUG IsTemporalRelationWithQuantities: t1.mSceneTime_Start" + t1.mSceneTime_Start.toString());
			System.out.println("DEBUG IsTemporalRelationWithQuantities: t1.mSceneTime_End" + t1.mSceneTime_End.toString());
			System.out.println("DEBUG IsTemporalRelationWithQuantities: t2.mSceneTime_Start" + t2.mSceneTime_Start.toString());
			System.out.println("DEBUG IsTemporalRelationWithQuantities: t2.mSceneTime_End" + t2.mSceneTime_End.toString());
			
			// apply to the time range, not individual time intervals
			try {
				
				System.out.println("DEBUG IsTemporalRelationWithQuantities:  TimeFunctions.IsTemporalRelation" );
				
				bRes = TimeFunctions.IsTemporalRelation(temporal_relation, t1, t2);
			} catch (MseeException e) {
				System.err.println("ERROR IsTemporalRelationWithQuantities exception.");
			}
    	} 		
    	
		// result
		// TODO test this
		
    	// if (MseeFunction.bVerbose)
    	if (true)
    	{
			System.out.println("DEBUG IsTemporalRelationWithQuantities ending " + bRes);
    	}
		
    	
    
    	// TEST ONLY 
    	/*
    	CardinalityRecordList.debug_ctr++;
    	System.out.println("DEBUG CardinalityRecordList.debug_ctr " + CardinalityRecordList.debug_ctr);
    	
    	if (CardinalityRecordList.debug_ctr  < 5)
    	{
    		bRes = true;
    	}    	
    	System.out.println("DEBUG IsTemporalRelationWithQuantities ending " + bRes);
    	*/
    	
    	
		// return 
		return NodeValue.makeBoolean(bRes); 
	}
}
