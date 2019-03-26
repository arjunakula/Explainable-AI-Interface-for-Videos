package sparql;

import java.util.ArrayList;
import java.util.List;

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

public class IsTemporalRelation extends FunctionBase
{
	
	public IsTemporalRelation() { super() ; }
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
        if ( args.size()  != 6 )
            throw new QueryBuildException("Function '"+Utils.className(this)+"' takes six arguments") ;
      
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	if (MseeFunction.bVerbose)
    	{
    		System.out.println("DEBUG IsTemporalRelation running");
    	}
		
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
        
        int index_temporal_relation = -1;
        int index_event1 = -1; 
        int index_event2 = -1;
        
        for (int i = 0; i < args.size()/2 ; i++)
        {	if (MseeFunction.IsEventType(argType_list.get(i)))
        	{	
        		if (index_event1==-1)
        		{	index_event1 = i;
        		} else
        		{
        			index_event2 = i;
        		}
        	} else if (MseeFunction.IsTemporalRelationType(argType_list.get(i)))
        	{	index_temporal_relation = i;
        	} else
        	{  throw new ARQInternalErrorException(Utils.className(this)+": IsTemporalRelation: unable to parse arguments") ;
        	}
        }
            
		if ((index_temporal_relation ==-1) || (index_event1 ==-1)  || (index_event2 ==-1))
		{
		    throw new ARQInternalErrorException(Utils.className(this)+": IsTemporalRelation: unable to parse arguments") ;
	    }
		
		TemporalRelation temporal_relation = TemporalRelation.UNKNOWN; 

		// get temporal relation
		if (index_temporal_relation != -1)
		{	temporal_relation = TimeFunctions.GetTemporalRelation(argStr_list.get(index_temporal_relation));		
		}	
	
		
		// get objects
		CEventData event1 = MseeDataset.FindEventData(MseeDataset.RemoveNamespace(argStr_list.get(index_event1)));
		CEventData event2 = MseeDataset.FindEventData(MseeDataset.RemoveNamespace(argStr_list.get(index_event2)));

		// result
		Boolean b = false;		

		if ((event1 != null) && (event2 != null))
		{
			// get time data
			TimeData timeData1 = MseeDataset.GetEventTimeData(event1);
			TimeData timeData2 = MseeDataset.GetEventTimeData(event2);
				 
			try {
				b = TimeFunctions.IsTemporalRelation(temporal_relation, timeData1,timeData2);
			} catch (MseeException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR IsTemporalRelation exception");
				e.printStackTrace();
			}
		}	else
		{
			if (MseeFunction.bVerbose)
	    	{
				System.out.println("DEBUG IsTemporalRelation unable to find event");
	    	}
		}
		
		if (MseeFunction.bVerbose)
    	{
			System.out.println("DEBUG IsTemporalRelation ending");
    	}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}
