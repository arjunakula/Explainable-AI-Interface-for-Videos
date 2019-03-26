package sparql;

import java.util.ArrayList;
import java.util.List;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp ;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;
import com.hp.hpl.jena.sparql.util.Utils;

// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsConcurrenceN extends FunctionBase
{
    @Override
    public void checkBuild(String uri, ExprList args)
    { 
        if ( args.size() %2 != 0 )
            throw new QueryBuildException("Function '"+Utils.className(this)+"' takes even number of arguments") ;
    }
    
	public IsConcurrenceN() { super() ; }
	

    @Override
    public final NodeValue exec(List<NodeValue> args)
    {
		System.out.println("DEBUG IsConcurrenceN running");
		
        if ( args == null )
            // The contract on the function interface is that this should not happen.
            throw new ARQInternalErrorException(Utils.className(this)+": Null args list") ;
        
        if ( args.size() %2  != 0 )
            throw new ExprEvalException(Utils.className(this)+": Wrong number of arguments: Wanted even number, got "+args.size()) ;
        
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
        
        
        
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
        
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	System.out.println("DEBUG IsConcurrenceN i " + Integer.toString(i) 
        			+ " argtype " + argType_list.get(i).toString());
        }
        
        for (int i = 0; i < args.size()/2 ; i++)
        {  	System.out.println("DEBUG IsConcurrenceN i " + Integer.toString(i) 
        			+ " string " + argStr_list.get(i) );
        }
        
		// always return true for now
		Boolean bIsConcurrence = true;
		

		// return 
		System.out.println("DEBUG IsConcurrenceN ending");
		
		return NodeValue.makeBoolean(bIsConcurrence); 
    }    
}
