

/**
 * Test Jena and sparql code
 *
 * Ref: http://jena.sourceforge.net/ARQ/app_api.html
 */

// package jena.examples.rdf ;

package sparql;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.graph.*;
// import com.hp.hpl.jena.graph.query.*;
// import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.sparql.function.FunctionBase;
// import com.hp.hpl.jena.sparql.function.FunctionBase4;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp; 
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
// import com.hp.hpl.jena.sparql.function;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.sparql.util.Utils;

import java.io.*;
import java.lang.Math;
import java.util.List;



//obsolete: public class approach extends FunctionBase4

// implement approach filter 
public class approach extends FunctionBase
{
	public approach() { super(); }


	private static final int DEBUG_DATA = 0; 

	@Override
    public void checkBuild(String uri, ExprList args)
    {
        if ( args.size() != 6)
            throw new QueryBuildException("Function takes six arguments") ;
    }

	//public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4, NodeValue nv5, NodeValue nv6 )
	@Override
	public NodeValue exec( java.util.List args)
	{
		// see	http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
		//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html
		//		http://grepcode.com/file/repo1.maven.org/maven2/com.hp.hpl.jena/arq/2.8.2/com/hp/hpl/jena/sparql/function/library/strSubstring.java


		if ( args.size() != 6 )
            throw new ExprEvalException("substring: Wrong number of arguments: "+
                                       args.size()+" : [wanted 6]") ;
       

		NodeValue nv1 = (NodeValue) args.get(0);
		NodeValue nv2 = (NodeValue) args.get(1);
		NodeValue nv3 = (NodeValue) args.get(2);
		NodeValue nv4 = (NodeValue) args.get(3);
		NodeValue nv5 = (NodeValue) args.get(4);
		NodeValue nv6 = (NodeValue) args.get(5);

		// pt1
		double x1 = nv1.getDouble() ;
		double y1 = nv2.getDouble() ;

		// pt2
		double x2 = nv3.getDouble() ;
		double y2 = nv4.getDouble() ;

		// pt3
		double x3 = nv5.getDouble();
		double y3 = nv6.getDouble();

		// check if vector pt1-to-pt2 is approaching pt3

		// vector pt1 to pt2
		double dx12 = x2 - x1; 
		double dy12 = y2 - y1;

		// vector pt1 to pt3;
		double dx13 = x3 - x1;
		double dy13 = y3 - y1;

		double cosA = ((dx12 * dx13) + (dy12 * dy13)) / Math.sqrt((dx12 * dx12 + dy12 * dy12) * (dx13 * dx13 + dy13 * dy13));
		double angle_in_rad = Math.acos(cosA);

		

		Boolean bIsApproach = (Math.abs(angle_in_rad) < (45 * 0.017453));  // 45 deg angle 
		
		if (DEBUG_DATA  > 0 )
		{
			System.out.println("x1: " + x1 + "  y1: " + y1);
			System.out.println("x2: " + x2 + "  y2: " + y2);
			System.out.println("x3: " + x3 + "  y3: " + y3);
			System.out.println("angle_in_rad: " + angle_in_rad);
			System.out.println("bIsApproach: " + bIsApproach);
		}


		// ref:  return NodeValue.makeDouble(dist);
		return NodeValue.makeBoolean(bIsApproach);
		
	}
}
