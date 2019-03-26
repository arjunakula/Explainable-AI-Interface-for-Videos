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
 * 
 IsNever("SET_NAME","___")

*/
			 
public class IsNever extends FunctionBase
{

	public IsNever() { super() ; }
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
		// TODO implement this
        if ( args.size()  != 2 )
           throw new QueryBuildException("Function '"+Utils.className(this)+"' takes two arguments") ;
      
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	// if (MseeFunction.bVerbose)
    	if (true)
    	{
    		System.out.println("DEBUG IsNever running");
    	}
		
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }

		Boolean bRes = true;		

		// IsNever only accept SET_NAME as first argument
        if ( argType_list.get(0) != ArgType.ARG_SET_NAME)
        {
        	System.err.println("ERROR IsNever invalid first argument ");
    		return NodeValue.makeBoolean(bRes); 	
        }
        
        
        // create a set
        SetQuantity set_quantity = new SetQuantity();
           	
    	set_quantity.FindSet(argStr_list.get(0));
    	 
    	
    	if (set_quantity.timeDataList.isEmpty()) 
    	{
    		bRes = true;
    	} else
    	{
			bRes = false;			
    	} 		
    	
		// result
		// TODO test this
		
    	// if (MseeFunction.bVerbose)
    	if (true)
    	{
			System.out.println("DEBUG IsNever ending " + bRes);
    	}
		
		// return 
		return NodeValue.makeBoolean(bRes); 
	}
}
