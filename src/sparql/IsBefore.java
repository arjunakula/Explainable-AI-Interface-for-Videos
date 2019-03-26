package sparql;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp ;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;


// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsBefore extends FunctionBase4
{
	public IsBefore() { super() ; }
	
    @Override
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{
		System.out.println("DEBUG IsBefore");

		// get argument and string
		MseeFunction.ArgType argTypeA = MseeFunction.GetArgType(nv1.getString());
		MseeFunction.ArgType argTypeB = MseeFunction.GetArgType(nv3.getString());
		
		System.out.println("DEBUG IsBefore argTypeA " + argTypeA.toString());
		System.out.println("DEBUG IsBefore argTypeB " + argTypeB.toString());
		
		String strA = MseeFunction.GetStringFromNodeValue(argTypeA, nv2);
		String strB = MseeFunction.GetStringFromNodeValue(argTypeB, nv4);
		
		System.out.println("DEBUG IsBefore strA " + strA );
		System.out.println("DEBUG IsBefore strB " + strB );
				
		// always return true for now
		Boolean b = true;
		
		// return 
		System.out.println("DEBUG IsBefore ending");
		
		return NodeValue.makeBoolean(b); 
	}
}
