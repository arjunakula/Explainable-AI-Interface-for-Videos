package mergeData.mergeGraphs.compatibleGraphs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * The <code>CompatibleGraphs</code> class transforms an rdf graph that was produced by running 
 * <code>TextParserApp</code> on sentences into a form that is compatible with the rdf graphs produced by 
 * UCLA's automatic video system. Then the <code>GraphMerger</code> program can be applied to the graphs.
 *
 * @author Ken Samuel
 * @version 1.0, Apr 9, 2015
 * @since 1.6
 */
public class CompatibleGraphs {
	
	/**
	 * The <code>CompatibleGraphs</code> constructor initializes the program.
	 */
	public CompatibleGraphs() {
		Global.initialize();
	}

	/**
	 * The <code>modifyGraph</code> method transforms an rdf graph that was produced by running 
	 * <code>TextParserApp</code> on sentences into a form that is compatible with the rdf graphs produced by 
	 * UCLA's automatic video system.
	 *
	 * @param inputFile is the name of the file with the original version of the graph.
	 * @param outputFile is the name of the file to which the modified graph should be saved.
	 * @throws FileNotFoundException if there are any problems with the files.
	 * @throws IOException if there are any problems with the files.
	 */
	public void modifyGraph(String inputFile, String outputFile) throws FileNotFoundException, IOException {
		Global.graph = Global.loadGraph(inputFile);
		if (Global.D_BUG) {
			System.out.println("THE ORIGINAL GRAPH");
			System.out.println("==================");
			Global.printGraph(Global.graph);
			System.out.println();
		}
		modifyGraph(Global.graph);
		if (Global.D_BUG) {
			System.out.println("THE MODIFIED GRAPH");
			System.out.println("==================");
			Global.printGraph(Global.graph);
			System.out.println();
		}
		Global.saveGraph(Global.graph,outputFile);
	}
	
	/**
	 * The <code>modifyGraph</code> method transforms an rdf graph that was produced by running 
	 * <code>TextParserApp</code> on sentences into a form that is compatible with the rdf graphs produced by 
	 * UCLA's automatic video system.
	 *
	 * @param graph is the graph to be modified. It will be changed by this method.
	 */
	public void modifyGraph(Model graph) {
		StmtIterator triples;					//To loop through the triples in the input graph
		Statement triple;						//One of those triples
		Resource subject;						//The subject of that triple
		Property predicate;						//The predicate of that triple
		RDFNode object;							//The object of that triple
		String subjectAbsoluteName;				//The name of the subject with its namespace
		String predicateAbsoluteName;			//The name of the predicate with its namespace
		String objectAbsoluteName;				//The name of the object with its namespace
		String subjectName;						//The name of the subject
		String predicateName;					//The name of the predicate
		String objectName;						//The name of the object
		HashSet<Statement> modifiedTriples;		//The triples for the output
		Statement modifiedTriple;				//One of those triples
		Resource modifiedSubject;				//The subject of that triple
		Property modifiedPredicate;				//The predicate of that triple
		RDFNode modifiedObject;					//The object of that triple
		String mseeType;						//The type of a node in the original graph
		String mseeModifiedType;				//The type of the corresponding node in the modified graph

		//FIRST PASS: Acquire information from the graph.
		triples = graph.listStatements();
		while (triples.hasNext()) {
			triple = triples.next();
			subject = triple.getSubject();
			predicate = triple.getPredicate();
			object = triple.getObject();
			subjectAbsoluteName = subject.getURI();
			predicateAbsoluteName = predicate.toString();
			objectAbsoluteName = object.toString();
			subjectName = Global.removeNamespace(subjectAbsoluteName);
			predicateName = Global.removeNamespace(predicateAbsoluteName);
			objectName = Global.removeNamespace(objectAbsoluteName);
			if (predicateName.equals("hasName")) {
				if (Global.mseeObjectNames.containsKey(objectAbsoluteName)) { //Is it already there?
					//We make the daring assumption that all objects with the same name are the same object.
					Global.modifiedIdentifiers.put(
							subjectAbsoluteName,
							Global.mseeObjectNames.get(objectAbsoluteName));
				} else {
					Global.mseeObjectNames.put(objectAbsoluteName,subjectAbsoluteName);
				}
			} else if (predicateName.equals("hasType")) {
				Global.mseeEventTypes.put(subjectAbsoluteName,objectAbsoluteName);
			}
		}

		//SECOND PASS: Create the modified triples
		modifiedTriples = new HashSet<Statement>((int)graph.size());
		triples = graph.listStatements();
		while (triples.hasNext()) {
			triple = triples.next();
			subject = triple.getSubject();
			predicate = triple.getPredicate();
			object = triple.getObject();
			subjectAbsoluteName = subject.getURI();
			predicateAbsoluteName = predicate.toString();
			objectAbsoluteName = object.toString();
			subjectName = Global.removeNamespace(subjectAbsoluteName);
			predicateName = Global.removeNamespace(predicateAbsoluteName);
			objectName = Global.removeNamespace(objectAbsoluteName);
			if ( ! predicateName.equals("hasType")) {					//Remove the "hasType" triples
				modifiedSubject = graph.createResource(subjectAbsoluteName);		//Don't modify it yet
				modifiedPredicate = graph.createProperty(predicateAbsoluteName);	//Don't modify it yet
				if (object.isLiteral()) {										//Don't modify it yet
					modifiedObject = graph.createLiteral(objectAbsoluteName);
				} else {
					modifiedObject = graph.createResource(objectAbsoluteName);
				}
				if (Global.modifiedIdentifiers.containsKey(subjectAbsoluteName)) {
					subjectAbsoluteName = Global.modifiedIdentifiers.get(subjectAbsoluteName);
					modifiedSubject = graph.createResource(subjectAbsoluteName);		//Modify the subject
				}
				if (Global.modifiedIdentifiers.containsKey(objectAbsoluteName)) {
					objectAbsoluteName = Global.modifiedIdentifiers.get(objectAbsoluteName);
//					modifiedObject = graph.createLiteral(objectName);	//Modify the object
					modifiedObject = graph.createResource(objectAbsoluteName);			//Modify the object
				}
				if (predicate.equals(RDF.type)) {
					if (objectName.equals("Event")) {
						if (Global.mseeEventTypes.containsKey(subjectAbsoluteName)) {
							mseeType = Global.mseeEventTypes.get(subjectAbsoluteName);
							if (Global.MSEE_EVENT_TRANSLATIONS.containsKey(mseeType)) {
								mseeModifiedType = Global.MSEE_EVENT_TRANSLATIONS.get(mseeType);
 								mseeModifiedType = Global.copyNamespace(objectAbsoluteName,mseeModifiedType);
								modifiedObject = graph.createResource(mseeModifiedType);
							} else {
								System.err.println(
										"WARNING in CompatibleGraphs.modifyGraph: Found an unexpected type " +
										"of event, \"" + mseeType + "\".");
							}
						} else {
							System.err.println(
									"WARNING in CompatibleGraphs.modifyGraph: Found an identifier that has " +
									"no type, \"" + subjectName + "\".");
						}
					} else if ((objectName.equals("Object")) || (objectName.equals("location"))) {
						mseeType = Global.removeNumber(subjectAbsoluteName);
						if (Global.MSEE_OBJECT_TRANSLATIONS.containsKey(mseeType)) {
							mseeModifiedType = Global.MSEE_OBJECT_TRANSLATIONS.get(mseeType);
							mseeModifiedType = Global.copyNamespace(objectAbsoluteName,mseeModifiedType);
							modifiedObject = graph.createResource(mseeModifiedType);
						} else {
							System.err.println(
									"WARNING in CompatibleGraphs.modifyGraph: Found an unexpected type " +
									"of object, \"" + mseeType + "\".");
						}
					} else if (Global.MSEE_OBJECT_TRANSLATIONS.containsKey(objectName)) {
						mseeModifiedType = Global.MSEE_OBJECT_TRANSLATIONS.get(objectName);
						mseeModifiedType = Global.copyNamespace(objectAbsoluteName,mseeModifiedType);
						modifiedObject = graph.createResource(mseeModifiedType);
					}
				}
				modifiedTriple = graph.createStatement(modifiedSubject,modifiedPredicate,modifiedObject);
				modifiedTriples.add(modifiedTriple);
			}
		}

		
		
		//THIRD PASS: Replace the triples in the graph with the modified triples
		graph.removeAll();
		for (Statement aModifiedTriple : modifiedTriples) {
			graph.add(aModifiedTriple);
		}
	}	
	
	/**
	 * The <code>main</code> method is where the program begins.
	 *
	 * @param args are ignored.
	 * @throws Exception if there are any problems.
	 */
	public static void main(String[] args) throws Exception {
		RunTests.go(args);
	}
}
