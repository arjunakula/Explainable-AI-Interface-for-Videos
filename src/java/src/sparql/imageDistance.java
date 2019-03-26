

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
import com.hp.hpl.jena.sparql.function.FunctionBase4;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp; 
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
// import com.hp.hpl.jena.sparql.function;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;

import java.io.*;
import java.lang.Math; 

// image Euclidean distance 
public class imageDistance extends FunctionBase4
{
	public imageDistance() { super(); }
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{
		// see	http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
		//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html

		double x1 = nv1.getDouble() ;
		double y1 = nv2.getDouble() ;
		double x2 = nv3.getDouble() ;
		double y2 = nv4.getDouble() ;

		double dx = x2 - x1; 
		double dy = y2 - y1;

		double dist = Math.sqrt((dx * dx) + (dy * dy)); 

		return NodeValue.makeDouble(dist);
	}
}
