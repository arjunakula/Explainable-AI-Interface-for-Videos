package sparql;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp ;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;

// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsConcurrence extends FunctionBase4
{
	public IsConcurrence() { super() ; }
	
    @Override
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{
		System.out.println("DEBUG IsConcurrence running");
		
		// get argument and string
		MseeFunction.ArgType argTypeA = MseeFunction.GetArgType(nv1.getString());
		MseeFunction.ArgType argTypeB = MseeFunction.GetArgType(nv3.getString());
		
		System.out.println("DEBUG IsConcurrence argTypeA " + argTypeA.toString());
		System.out.println("DEBUG IsConcurrence argTypeB " + argTypeB.toString());
		
		String strA = MseeFunction.GetStringFromNodeValue(argTypeA, nv2);
		String strB = MseeFunction.GetStringFromNodeValue(argTypeB, nv4);
		
		System.out.println("DEBUG IsConcurrence strA " + strA );
		System.out.println("DEBUG IsConcurrence strB " + strB );
		
		// always return true for now
		Boolean bIsConcurrence = true;
		

		// return 
		System.out.println("DEBUG IsConcurrence ending");
		
		return NodeValue.makeBoolean(bIsConcurrence); 
	}
}
