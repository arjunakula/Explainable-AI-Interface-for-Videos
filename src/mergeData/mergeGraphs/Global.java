package mergeData.mergeGraphs;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


/**
 * The <code>Global</code> class consists of things that are used throughout the program (the global 
 * constants, variables, and methods).
 *
 * @author Ken Samuel
 * @version 1.0, Mar 6, 2015
 * @since 1.6
 */
public class Global {

	/** <code>D_BUG</code> specifies whether or not to print information to help with debugging. */
	public static final Boolean D_BUG = true;
	
	/**
	 * The <code>removeNamespace</code> method returns the part of the given string that follows the pound 
	 * sign (#).
	 *
	 * @param name is the full name, including the namespace.
	 * @return <code>name</code> with the namespace removed.
	 */
	public static String removeNamespace(String name) {
		String returnValue;
		
		returnValue = name.replaceAll(".*#","");
		return returnValue;
	}
	
	/**
	 * The <code>printGraph</code> method displays a graph of type <code>Model</code> in a pretty format.
	 *
	 * @param graph is the graph to print.
	 */
	public static void printGraph(Model graph) {
		StmtIterator triples;					//To loop through the triples
		Statement triple;						//One of the triples

		triples = graph.listStatements();
		while (triples.hasNext()) {
			triple = triples.next();
			System.out.print("\t");
			printTriple(triple);
			System.out.println();
		}
	}

	/**
	 * The <code>printTriple</code> method displays a triple of type <code>Statement</code> in a pretty 
	 * format.
	 *
	 * @param triple is the triple to print.
	 */
	public static void printTriple(Statement triple) {
		Resource subject;						//The subject of that triple
		Property predicate;						//The predicate of that triple
		String subjectName;						//The name of the subject
		RDFNode object;							//The object of that triple

		subject = triple.getSubject();
		predicate = triple.getPredicate();
		object = triple.getObject();
		subjectName = subject.getURI();
		System.out.print(
				"<" + Global.removeNamespace(subjectName) + 
				"," + Global.removeNamespace(predicate.toString()) + 
				"," + Global.removeNamespace(object.toString()) + ">");
	}
}