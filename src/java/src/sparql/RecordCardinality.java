package sparql;

import java.util.ArrayList;
import java.util.List;

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


 */
public class RecordCardinality extends FunctionBase
{

	public RecordCardinality() { super() ; }
	
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
    	//if (MseeFunction.bVerbose)
    	if (true)	
    	{
    		System.err.println("DEBUG RecordCardinality running");
    	}
		
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
    	/* debug MWL  */
        RecordCardinalityNode rc_root;
        
		try {
			rc_root = new RecordCardinalityNode(MseeFunction.ArgType.ARG_SET_NAME, argStr_list.get(0));

	        rc_root.SetRootNode(argType_list, argStr_list);
	        
	        rc_root.ComputeCardinalityData();
	        
	        CardinalityRecordList.InsertRecordCardinality(rc_root);
	        
		} catch (MseeException e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR RecordCardinality; unable to set up node.");
		}
        /* */ 
        
              
		// result; recordCardinality should always return true
		Boolean b = true;		

		
		// if (MseeFunction.bVerbose)
		if (true)
    	{
			
			System.out.println("DEBUG RecordCardinality ending");
			System.out.println("DEBUG RecordCardinality size " + CardinalityRecordList.cardinalityRecordList.size());
			for (CardinalityRecord record : CardinalityRecordList.cardinalityRecordList)
			{
				System.out.println("\t DEBUG CardinalityRecord  "  + record.record_name + " ctr data " + record.cardinalityDataList.size());
			}
    	}
    	
		
		// return 
		return NodeValue.makeBoolean(b); 
	}

}
