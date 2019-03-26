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

/*
 * 
CardinalityAtTime("VIEW_CENTRIC_TIME_PERIOD","___","OBJECT_ID",?object1,"EQ",?number1)
CardinalityAtTime("VIEW_CENTRIC_TIME_PERIOD","___","OBJECT_ID",?object1,"GT",?number1)
CardinalityAtTime("VIEW_CENTRIC_TIME_PERIOD","___","OBJECT_ID",?object1,"LT",?number1)
CardinalityAtTime("VIEW_CENTRIC_TIME_PERIOD","___","OBJECT_ID",?object1,"GTE",?number1)
CardinalityAtTime("VIEW_CENTRIC_TIME_PERIOD","___","OBJECT_ID",?object1,"LTE",?number1)
CardinalityAtLocation("VIEW_CENTRIC_POINT","___","OBJECT_ID",?object1,"___",?number1)
CardinalityAtTimeLocation("VIEW_CENTRIC_TIME_PERIOD","___","VIEW_CENTRIC_POINT","___","OBJECT_ID",?object1,"___",?number1)

 */
public class CardinalityAtTime extends FunctionBase
{

	public CardinalityAtTime() { super() ; }
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
		// TODO implement this
		// 
   //     if ( args.size()  != 6 )
   //         throw new QueryBuildException("Function '"+Utils.className(this)+"' takes six arguments") ;
      
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	if (MseeFunction.bVerbose)
    	{
    		System.out.println("DEBUG CardinalityAtTime running");
    	}
		
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
        
       
		// TODO implement this
		// result
		Boolean b = true;		

		if (MseeFunction.bVerbose)
    	{
			System.out.println("DEBUG CardinalityAtTime ending");
    	}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}
