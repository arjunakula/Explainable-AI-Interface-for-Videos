package sparql;

import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase2 ;

public class area extends FunctionBase2
{
	public area() { super() ; }
	
    @Override
	public NodeValue exec(NodeValue nv1, NodeValue nv2)
	{
		// see	http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
		//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html

		System.out.println("DEBUG my sparql area 2");
		
		int i1 =0, i2 =0;

		
		i1 = nv1.getInteger().intValue() ;
		i2 = nv2.getInteger().intValue() ;

		/* mw: not working	
		if (nv1.getInteger()!=null)
		{
			System.out.println("DEBUG my sparql area try integer");
			i1 = nv1.getInteger().intValue() ;
			i2 = nv2.getInteger().intValue() ;
		} 

		else
		{
			System.out.println("DEBUG my sparql area; s1 = nv1.asString()");
			String s1 = nv1.asString();
			String s2 = nv2.asString();
			if (s1!=null)
			{
				System.out.println("DEBUG my sparql area try string");
				i1 = Integer.parseInt(s1);
				i2 = Integer.parseInt(s2);
			} else
			{
				System.out.println("DEBUG my sparql area; s1 is null");
			}
			
		}
		*/ 
		
		System.out.println("DEBUG my sparql  area l1 l2 " + i1 + i2);
		return NodeValue.makeInteger(i1 * i2); 			 
	}
}
