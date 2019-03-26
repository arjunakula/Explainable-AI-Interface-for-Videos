package sparql;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp ;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;


// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsAtTime extends FunctionBase4
{
	static boolean bVerboseThis = false;
	public IsAtTime() { super() ; }
	
    @Override
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{

		// get argument and string
		MseeFunction.ArgType argTypeA = MseeFunction.GetArgType(nv1.getString());		//  condition first, entity second
		MseeFunction.ArgType argTypeB = MseeFunction.GetArgType(nv3.getString());

	
		String strA = MseeFunction.GetStringFromNodeValue(argTypeA, nv2);
		String strB = MseeFunction.GetStringFromNodeValue(argTypeB, nv4);
		
		// always return true for now
		Boolean b = false;
		
		b = IsAtTime.IsAtTime_Simple(argTypeA, strA, argTypeB, strB);
		
		
				
		// return 
		// System.out.println("DEBUG IsAtTime ending: "+ b);
		
		return NodeValue.makeBoolean(b); 
	}

	static public Boolean IsAtTime_Simple(ArgType argTypeA, String strA,
			ArgType argTypeB, String strB) {
		
		Boolean b = false;
		// debug 
		if (MseeFunction.bVerbose || bVerboseThis)
		{	// debug only 
			System.out.println("DEBUG IsAtTime");
			System.out.println("DEBUG IsAtTime argTypeA " + argTypeA.toString());
			System.out.println("DEBUG IsAtTime argTypeB " + argTypeB.toString());		
			System.out.println("DEBUG IsAtTime strA " + strA );
			System.out.println("DEBUG IsAtTime strB " + strB );
		}
				

		
		if (strB.contains("Unknown") == true)
		{
			System.out.println("DEBUG IsAtTime object is Unknown; return true;  " + strB );
			b = true;
		}
		else
		{
			try {
				b = MseeDataset.IsEntityAtTime(argTypeB, strB, argTypeA, strA);		//  entity first, condition second
			} catch (MseeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (MseeFunction.bVerbose || bVerboseThis)
		{	// debug only 
			System.out.println("DEBUG IsAtTime return " + b);
		}
		return b;
	}
}
