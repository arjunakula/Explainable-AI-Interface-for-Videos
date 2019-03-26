package sparql;

import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;


// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsAtLocation extends FunctionBase4
{
	public IsAtLocation() { super() ; }
	
    @Override
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{
		// get argument and string
		MseeFunction.ArgType argTypeA = MseeFunction.GetArgType(nv1.getString());
		MseeFunction.ArgType argTypeB = MseeFunction.GetArgType(nv3.getString());
				
		String strA = MseeFunction.GetStringFromNodeValue(argTypeA, nv2);
		String strB = MseeFunction.GetStringFromNodeValue(argTypeB, nv4);
		
		if (MseeFunction.bVerbose)
		{	System.out.println("DEBUG IsAtLocation");
			System.out.println("DEBUG IsAtLocation argTypeA " + argTypeA.toString());
			System.out.println("DEBUG IsAtLocation argTypeB " + argTypeB.toString());
		
			System.out.println("DEBUG IsAtLocation strA " + strA );
			System.out.println("DEBUG IsAtLocation strB " + strB );
		}
		
				
		Boolean b = false;
		
		if (strB.contains("Unknown") == true)
		{
			// System.out.println("DEBUG IsAtTime object is Unknown; return true;  " + strB );
			b = true;
		}
		else
		{
			// check location condition (apply AND) 
			try {
				b = MseeDataset.IsEntityAtLocation(
						argTypeB, strB,
						argTypeA, strA ); 
			} catch (MseeException e) {			
				System.err.println("WARNING in IsAtTimeLocation, IsEntityAtLocation, MseeException");
			    
			}
		}
		
		
		// return 
		if (MseeFunction.bVerbose)
    	{
			System.out.println("DEBUG IsAtLocation ending b " + b);
    	}
		
		return NodeValue.makeBoolean(b); 
	}
}
