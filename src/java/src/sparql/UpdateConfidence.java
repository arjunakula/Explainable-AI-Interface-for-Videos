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


			 
public class UpdateConfidence extends FunctionBase
{

	public UpdateConfidence() { super() ; }
	
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
    		System.out.println("DEBUG UpdateConfidence running");
    	}
		
    	String strA = args.get(0).getString();
    	if (strA == null)
    	{
    		System.err.println("ERROR UpdateConfidence strA is null");
    	} else
    	{
	    	Double f = Double.parseDouble(strA);
	    	// float f = args.get(0).getFloat();
	    	System.out.println("DEBUG UpdateConfidence : Confidence  " + f);
	    	
	    	AnswerConfidence.UpdateConfidence(f);
    	}
    	
		Boolean bRes = true;		

		
    	System.out.println("DEBUG UpdateConfidence ending " + bRes);
    	
    	
    	
		// return 
		return NodeValue.makeBoolean(bRes); 
	}
}
