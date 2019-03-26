package sparql;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp ;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;


// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsAfter extends FunctionBase4
{
	public IsAfter() { super() ; }
	
    @Override
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{

		System.out.println("DEBUG IsAfter");

		// get argument and string
		MseeFunction.ArgType argTypeA = MseeFunction.GetArgType(nv1.getString());
		MseeFunction.ArgType argTypeB = MseeFunction.GetArgType(nv3.getString());
		
		System.out.println("DEBUG IsAfter argTypeA " + argTypeA.toString());
		System.out.println("DEBUG IsAfter argTypeB " + argTypeB.toString());
		
		String strA = MseeFunction.GetStringFromNodeValue(argTypeA, nv2);
		String strB = MseeFunction.GetStringFromNodeValue(argTypeB, nv4);
		
		System.out.println("DEBUG IsAfter strA " + strA );
		System.out.println("DEBUG IsAfter strB " + strB );
				
		// always return true for now
		Boolean b = false;
		
		// return 
		System.out.println("DEBUG IsAfter ending");
		
		return NodeValue.makeBoolean(b); 
	}
}
