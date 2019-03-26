

/**
 * Test Jena and sparql code
 *
 * Ref: http://jena.sourceforge.net/ARQ/app_api.html
 */

// package jena.examples.rdf ;

package sparql;

// import com.hp.hpl.jena.graph.query.*;
// import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.sparql.function.FunctionBase4;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import java.lang.Math; 

public class distance extends FunctionBase4
{
	public distance() { super() ; }
	public NodeValue exec(NodeValue nv1, NodeValue nv2, NodeValue nv3, NodeValue nv4)
	{
		// see	http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
		//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html

		double radius_earth = 6378100;
		double deg2Rad = 3.1415927 / 180.0;

		double lonA = deg2Rad * nv1.getDouble() ;
		double latA = deg2Rad * nv2.getDouble() ;
		double lonB = deg2Rad * nv3.getDouble() ;
		double latB = deg2Rad * nv4.getDouble() ;

		double lat_d = latB - latA;
		double lon_d = lonB - lonA;

		// reference: http://www.movable-type.co.uk/scripts/latlong.html

		double a = (Math.sin(lat_d/2.0) * Math.sin(lat_d/2.0)) + (Math.cos(latA) * Math.cos(latB) * Math.sin(lon_d / 2.0) * Math.sin(lon_d / 2.0)); 
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 -a ));
		double d = radius_earth * c ; 		

		return NodeValue.makeDouble(d); 			 
	}
}
