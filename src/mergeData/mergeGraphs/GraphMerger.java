package mergeData.mergeGraphs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * The <code>GraphMerger</code> system is designed to merge two RDF graphs by merging nodes that appear to
 * refer to the same thing.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 4, 2015
 * @since 1.6
 */
public class GraphMerger {

	/** <code>UNWANTED_TYPES</code> is a list of the node types that we are not interested in. */
	private static final HashSet<String> UNWANTED_TYPES = new HashSet<String>(Arrays.asList(
			"AnnotationProperty",
			"Class",
			"NamedIndividual",
			"Ontology",
			"Thing",
			"Object"));

	/** <code>videoGraph</code> is a graph that was built from analyzing videos. */
	private Model videoGraph;
	
	/** <code>textGraph</code> is a graph that was built from analyzing text. */
	private Model textGraph;

	/** <code>ontology</code> is the ontology that the other graphs use. */
	private OntModel ontology;

	/** 
	 * <code>inputNodes</code> is all of the nodes in the two input graphs. The keys specify which input 
	 * graph each set of nodes is in. And each value is a map from a node's name to the corresponding object. 
	 */
	HashMap<GraphType,HashMap<String,IAINode>> inputNodes;
	
	/** <code>outputNodes</code> is all of the nodes in the output merged graph. */
	private HashSet<IAINode> outputNodes;
	
	/** 
	 * <code>nodeTypeMatches</code> is a set with every pair of nodes, one from each graph, that have 
	 * compatible types. Two types are compatible if they are equivalent or one is a subtype of the other. 
	 */
	HashSet<NodeTypeMatch> nodeTypeMatches;

	/**
	 * The <code>GraphMerger</code> constructor initializes the global fields.
	 */
	public GraphMerger() {
		videoGraph = null;
		textGraph = null;
		inputNodes = new HashMap<GraphType, HashMap<String,IAINode>>(2);
		inputNodes.put(GraphType.VIDEO_GRAPH,new HashMap<String,IAINode>()); 
		inputNodes.put(GraphType.TEXT_GRAPH,new HashMap<String,IAINode>()); 
		outputNodes = new HashSet<IAINode>();
		nodeTypeMatches = new HashSet<NodeTypeMatch>();
	}

	/**
	 * The <code>mergeGraphs</code> method merges a graph generated from video data with a graph generated
	 * from text data to produce a merged graph.
	 *
	 * @param videoGraphFilename is the name of the input file with the graph that was built from 
	 * analyzing videos. It should be in RDF format.
	 * @param textGraphFilename is the name of the input file with the graph that was built from 
	 * analyzing text. It should be in RDF format.
	 * @param mergedGraphFilename is the name of the output file with the graph that will be produced
	 * by merging <code>videoGraphFilename</code> and <code>textGraphFilename</code>. It will be in RDF 
	 * format.
	 * @param ontologyFilename is the name of the input file with the ontology that is used by the other
	 * graphs. It should be in OWL format.
	 * @param minNumTripleMatches is the minimum number of triple matches that two nodes must share in 
	 * order to merge the nodes. 
	 * @throws FileNotFoundException if one of the input files or the output folder cannot be found.
	 * @throws IOException if there's a problem with one of the file streams.
	 * @see IAINode
	 * @see IAITriple
	 */
	public void mergeGraphs(
			String videoGraphFilename, String textGraphFilename, String mergedGraphFilename, 
			String ontologyFilename, Integer minNumTripleMatches) throws FileNotFoundException, IOException {
//		ArrayList<Model> possibleMergedGraphs;			//Possible results of merging the two graphs
		Model mergedGraph;								//The final result of merging the two graphs
		
		videoGraph = loadGraph(videoGraphFilename);
		textGraph = loadGraph(textGraphFilename);
		ontology = loadOntology(ontologyFilename);
		readTriples(videoGraph,GraphType.VIDEO_GRAPH);
		readTriples(textGraph,GraphType.TEXT_GRAPH);
		findNodeTypeMatches();
		findTripleMatches();
		mergeNodes(minNumTripleMatches);
		mergeTriples();
		mergedGraph = constructMergedGraph();
		if (Global.D_BUG) {
			System.out.println();
			System.out.println("DEBUG in GraphMerger.mergedGraphs: mergedGraph = ");
			Global.printGraph(mergedGraph);
		}
		saveGraph(mergedGraph,mergedGraphFilename);
	}

	/**
	 * The <code>readTriples</code> method reads all of the triples in a graph and stores the information in
	 * in other objects.
	 *
	 * @param graph is one of the input graphs.
	 * @param graphType specifies which of the graphs it is.
	 */
	private void readTriples(Model graph, GraphType graphType) {
		StmtIterator triples;					//To loop through the triples
		Statement triple;						//One of the triples
		Resource subject;						//The subject of that triple
		Property predicate;						//The predicate of that triple
		String subjectName, objectName;			//The names of the subject and object
		RDFNode object;							//The object of that triple
		IAINode iAISubject, iAIObject;			//The subject and object as IAINodes
		IAITriple iAITriple;					//The triple as an IAITriple
		ArrayList<Statement> triplesToRemove;	//The triples that will be removed from the graph
		OntResource ontologyResource;		//A node's type in the form of an OntResource
//		System.out.println("\nBeginning readTriples()");
//		triples = graph.listStatements(null,RDF.type,(RDFNode)null);

		triplesToRemove = new ArrayList<Statement>((int)graph.size());			//Initialize
		triples = graph.listStatements();
		while (triples.hasNext()) {
			triple = triples.next();
			subject = triple.getSubject();
			predicate = triple.getPredicate();
			object = triple.getObject();
			subjectName = subject.getURI();
			if (Global.D_BUG) {
				System.out.println(
						"DEBUG in GraphMerger.readTriples: triple = <" + Global.removeNamespace(subjectName) + 
						"," + Global.removeNamespace(predicate.toString()) + 
						"," + Global.removeNamespace(object.toString()) + ">");
			}
			if (predicate.equals(RDF.type)) {		//Specifies the node type of the subject
				if ( ! UNWANTED_TYPES.contains(Global.removeNamespace(object.toString()))) {
					iAISubject = getIAINode(graphType,subjectName);
					if (iAISubject.getNodeType() == null) {
						iAISubject.setNodeType(object.toString());
//						Resource c1 = s1.getResource();
						ontologyResource = ontology.getOntResource((Resource)object);
//						ontologyResource = ontology.getOntResource(object.toString());
						if (ontologyResource == null) {
							System.err.println(
									"WARNING in GraphMerger.readTriples: Bug detected. Contact Ken Samuel.");
						} else if (ontologyResource.isClass()) {
							iAISubject.setOntologyNodeType(ontologyResource.asClass());
						} else {
							System.err.println(
									"WARNING in GraphMerger.readTriples: Bug detected. Contact Ken Samuel. --- " + triple);
						}
					} else {		//A node should not have more than one type
						System.err.println(
								"WARNING in GraphMerger.readTriples: Bug detected. Contact Ken Samuel.");
					}
					triplesToRemove.add(triple);
					if (Global.D_BUG) {
						System.out.println(
								"DEBUG in GraphMerger.readTriples: Removing triple.");
					}
				}
			} else if ( ! predicate.getLocalName().equals("subClassOf")){
				iAISubject = getIAINode(graphType,subjectName);
				if (object instanceof Literal) {
					objectName = object.toString();
				} else {			//The object is a resource (a regular node)
					objectName = ((Resource)object).getURI();
				}
				iAIObject = getIAINode(graphType,objectName);
				iAITriple = new IAITriple(iAISubject,predicate,iAIObject);
				iAISubject.addSubjectTriple(iAITriple);
				iAIObject.addObjectTriple(iAITriple);
				triplesToRemove.add(triple);
				if (Global.D_BUG) {
					System.out.println(
							"DEBUG in GraphMerger.readTriples: Removing triple.");
				}
			}
		}
		graph.remove(triplesToRemove);
		if (Global.D_BUG) {
			System.out.println();
			System.out.println("DEBUG in GraphMerger.readTriples: Now graph = ");
			Global.printGraph(graph);
			System.out.println();
		}
	}

	/**
	 * The <code>findNodeTypeMatches</code> method checks every pair of nodes, one from each input graph, to
	 * see if they have compatible types. Two types are compatible if they are equivalent or one is a subtype 
	 * of the other. Those that do have compatible types are added to <code>nodeTypeMatches</code>.
	 */
	private void findNodeTypeMatches() {
		NodeTypeMatch nodeTypeMatch;			//Two nodes with compatible types
		
		for (IAINode videoNode : inputNodes.get(GraphType.VIDEO_GRAPH).values()) {
			for (IAINode textNode : inputNodes.get(GraphType.TEXT_GRAPH).values()) {
				if (areCompatibleTypes(videoNode,textNode)) {
					nodeTypeMatch = new NodeTypeMatch(videoNode,textNode,ontology);
					nodeTypeMatches.add(nodeTypeMatch);
					videoNode.addNodeTypeMatch(nodeTypeMatch);
					textNode.addNodeTypeMatch(nodeTypeMatch);
				}
			}
		}
	}
	
	/**
	 * The <code>findTripleMatches</code> method goes through all of the node pairs in 
	 * <code>nodeTypeMatches</code>, finding the triple matches that they are in.
	 * 
	 * @see NodeTypeMatch
	 * @see TripleMatch
	 */
	private void findTripleMatches() {
		for (NodeTypeMatch nodeTypeMatch : nodeTypeMatches) {
			findTripleMatches(nodeTypeMatch);
		}
	}
	
	/**
	 * The <code>findTripleMatches</code> method finds the triple matches that the given node type match is 
	 * in.
	 * 
	 * @param nodeTypeMatch is the pair of nodes that must be in a triple match for it to be selected.
	 * @see NodeTypeMatch
	 * @see TripleMatch
	 */
	private static void findTripleMatches(NodeTypeMatch nodeTypeMatch) {
		IAINode videoNode, textNode;					//The two nodes in the given node type match
		HashSet<TripleMatch> tripleMatches;				//Some triple matches with the given node type match

		videoNode = nodeTypeMatch.getVideoNode();
		textNode  = nodeTypeMatch.getTextNode();
		tripleMatches = findTripleMatches(videoNode.getSubjectTriples(),textNode.getSubjectTriples());
		tripleMatches.addAll(findTripleMatches(videoNode.getObjectTriples(),textNode.getObjectTriples()));
		nodeTypeMatch.setTripleMatches(tripleMatches);
	}
	
	/**
	 * The <code>findTripleMatches</code> method finds the triple matches in the two given sets of triples.
	 * Each triple match has one triple from each set.
	 * 
	 * @param set1 is a set of triples.
	 * @param set2 is a set of triples that will be compared with the triples in <code>set1</code>.
	 * @return the set of triple matches that are found in the search.
	 * @see IAITriple
	 * @see TripleMatch
	 */
	private static HashSet<TripleMatch> findTripleMatches(HashSet<IAITriple> set1, HashSet<IAITriple> set2) {
		TripleMatch tripleMatch;				//The result of merging two triples
		HashSet<TripleMatch> returnValue;
		
		returnValue = new HashSet<TripleMatch>();
		for (IAITriple triple1 : set1) {
			for (IAITriple triple2 : set2) {
				tripleMatch = mergeTriples(triple1,triple2);
				if (tripleMatch != null) {
					returnValue.add(tripleMatch);
				}
			}
		}
		return returnValue;
	}

	/**
	 * The <code>mergeTriples</code> method determines whether the two given triples can be combined to make
	 * a triple match, and, if so, returns the resulting triple match. If not, <b><code>null</code></b> is
	 * returned.
	 *
	 * @param triple1 is one of the triples to compare.
	 * @param triple2 is the other triple.
	 * @return the result of merging those two triples if and only if the subjects of the triples are in a node type match, 
	 * their predicates are identical, and their objects are in a node type match.
	 * @see IAITriple
	 * @see TripleMatch
	 */
	private static TripleMatch mergeTriples(IAITriple triple1, IAITriple triple2) {
		IAINode subject1, subject2, object1, object2;	//The nodes in the triples
		Property predicate1, predicate2;			//The predicates in the triples
		HashSet<NodeTypeMatch> subjectMatches1, subjectMatches2;	//The node type matches that the subjects
		HashSet<NodeTypeMatch> objectMatches1, objectMatches2;		//and objects are in
		HashSet<NodeTypeMatch> subjectMatches, objectMatches;		//The ones that they both have
		
		//Compare the two triples
		subject1 = triple1.getSubject();
		predicate1 = triple1.getPredicate();
		object1 = triple1.getObject();
		subject2 = triple2.getSubject();
		predicate2 = triple2.getPredicate();
		object2 = triple2.getObject();
		if ( ! predicate1.getLocalName().equals(predicate2.getLocalName())) {
			return null;
		}
		subjectMatches1 = subject1.getNodeTypeMatches();
		subjectMatches2 = subject2.getNodeTypeMatches();
		subjectMatches = getIntersection(subjectMatches1, subjectMatches2);
		if (subjectMatches.size() == 0) {
			return null;
		}
		objectMatches1 = object1.getNodeTypeMatches();
		objectMatches2 = object2.getNodeTypeMatches();
		objectMatches = getIntersection(objectMatches1, objectMatches2);
		if (objectMatches.size() == 0) {
			return null;
		}
		
		//Merge the two triples
		return new TripleMatch(
				(NodeTypeMatch)subjectMatches.toArray()[0],
				predicate1,
				(NodeTypeMatch)objectMatches.toArray()[0]);
	}
	
	/**
	 * The <code>mergeNodes</code> method decides which nodes should be merged and then merges them.
	 *
	 * @param minNumTripleMatches is the minimum number of triple matches that two nodes must share in 
	 * order to merge the nodes.
	 * @see IAINode
	 */
	private void mergeNodes(Integer minNumTripleMatches) {
		
		for (NodeTypeMatch nodeTypeMatch : nodeTypeMatches) {
			if (Global.D_BUG) {
				System.out.println("DEBUG in GraphMerger.mergeNodes: nodeTypeMatch = " + nodeTypeMatch);
			}
			if (										//Do they have enough in characteristics in common?
					nodeTypeMatch.getNumTripleMatches() >= minNumTripleMatches) {
					mergeNodes(nodeTypeMatch);
			}
		}
		
	}
	
	/**
	 * The <code>mergeNodes</code> method combines two nodes, one from each input graph, and adds the 
	 * resulting node to the output graph.
	 *
	 * @param nodes are the two nodes to be merged.
	 * @see IAINode
	 */
	private void mergeNodes(NodeTypeMatch nodes) {
		IAINode videoNode,textNode;		//The two nodes that are being merged
		IAINode mergedNode;				//The node that results from merging the nodes
		NodeTypeMatch subject;			//The subject of that predicate, if the new node is the object
		NodeTypeMatch object;			//The object of that predicate, if the new node is the subject

		videoNode = nodes.getVideoNode();
		textNode = nodes.getTextNode();
		if (Global.D_BUG) {
			System.out.println(
					"DEBUG in GraphMerger.mergeNodes: Testing " + videoNode + 
					" and " + textNode + ".");
		}
		if ((videoNode.wasMerged()) || (textNode.wasMerged())) {
			return;
		}
		if (Global.D_BUG) {
			System.out.println(
					"DEBUG in GraphMerger.mergeNodes: Merging " + videoNode + 
					" and " + textNode + ".");
		}
		mergedNode = new IAINode(null,GraphType.MERGED_GRAPH);
		mergedNode.setNodeType(nodes.getNodeType());	//TODO: Do I need to set the ontology node type?
//		mergedNode.setNodeName(nodes.getTextNode().getNodeName());//Prefer the text node's name
		mergedNode.setNodeName(nodes.getVideoNode().getNodeName());//Prefer the video node's name
		if (Global.D_BUG) {
			System.out.println(
					"DEBUG in GraphMerger.mergedNodes: Changing \"" + 
					Global.removeNamespace(nodes.getVideoNode().getNodeName()) + 
					"\" to \"" + Global.removeNamespace(nodes.getTextNode().getNodeName()) + "\"");
		}
		changeName(nodes.getVideoNode().getNodeName(),nodes.getTextNode().getNodeName());
		outputNodes.add(mergedNode);
		videoNode = nodes.getVideoNode();
		textNode = nodes.getTextNode();
		videoNode.setMergedNode(mergedNode);
		textNode.setMergedNode(mergedNode);
		for (TripleMatch tripleMatch : nodes.getTripleMatches()) {
			if (tripleMatch.getSubject() == nodes) {
				object = tripleMatch.getObject();
				mergeNodes(object);		//Automatically merge them
			} else if (tripleMatch.getObject() == nodes) {
				subject = tripleMatch.getSubject();
				mergeNodes(subject);		//Automatically merge them
			} else {
				System.err.println("WARNING in GraphMerger.mergeNodes: Bug detected. Contact Ken Samuel.");
			}
		}
	}

	/**
	 * The <code>mergeTriples</code> method copies each triple in either of the input graphs to its proper 
	 * place in the merged graph.
	 * @see IAITriple
	 */
	@SuppressWarnings("unchecked")
	private void mergeTriples() {
		HashSet<IAITriple> triples;						//A set of triples to be added to the merged graph
		IAITriple mergedTriple;							//A triple on the merged graph
		IAINode mergedSubjectNode, mergedObjectNode;	//The subject and object in that triple
		Property predicate;								//The predicate in that triple
		IAINode objectNode;								//A node that is the object in a triple
		
		for (GraphType graphType : inputNodes.keySet()) {
			for (IAINode subjectNode : inputNodes.get(graphType).values()) {
				if ( ! subjectNode.wasMerged()) {	//Nodes that weren't merged must be copied
					outputNodes.add(subjectNode);
			}
				triples = (HashSet<IAITriple>)subjectNode.getSubjectTriples().clone();
				for (IAITriple triple : triples) {
					predicate = triple.getPredicate();
					objectNode = triple.getObject();
					subjectNode.removeSubjectTriple(triple);
					objectNode.removeObjectTriple(triple);
					mergedSubjectNode = subjectNode.getCurrentNode();
					mergedObjectNode = objectNode.getCurrentNode();
					mergedTriple = new IAITriple(mergedSubjectNode,predicate,mergedObjectNode);
					mergedSubjectNode.addSubjectTriple(mergedTriple);
					mergedObjectNode.addObjectTriple(mergedTriple);
				}
			}
		}
	}
	
	/**
	 * The <code>constructMergedGraph</code> method creates a new graph and fills it with all of the nodes
	 * in <code>outputNodes</code>.
	 *
	 * @return all of the merged nodes and their triples in the form of a <code>Model</code>.
	 * @see IAINode
	 */
	private Model constructMergedGraph() {
		HashSet<Statement> triplesToAdd;	//The triples that will be added to the merged graph
		Statement triple;					//One of those triples
		Resource subject;					//That triple's subject
		RDFNode object;						//That triple's object
		IAINode objectNode;					//The object as an IAINode
		Model returnValue;
		
		triplesToAdd = new HashSet<Statement>();				//Initialize
		returnValue = videoGraph;
		returnValue.add(textGraph);
		for (IAINode node : outputNodes) {
			if ( ! node.isLiteral()) {			//A literal has no type and cannot be the subject of a triple
				subject = returnValue.createResource(node.getNodeName());
				object = returnValue.createResource(node.getNodeType());
				triple = returnValue.createStatement(subject,RDF.type,object);
				triplesToAdd.add(triple);
				for (IAITriple subjectTriple : node.getSubjectTriples()) {
					objectNode = subjectTriple.getObject();
					if (objectNode.isLiteral()) {
						object = returnValue.createLiteral(objectNode.getNodeName(),null);
					} else {
						object = returnValue.createResource(objectNode.getNodeName());
					}
					triple = returnValue.createStatement(subject,subjectTriple.getPredicate(),object);
					triplesToAdd.add(triple);
				}
			}
		}
		for (Statement tripleToAdd : triplesToAdd) {
			returnValue.add(tripleToAdd);
		}
		return returnValue;
	}
	
	/**
	 * The <code>changeName</code> method changes the name of a node in the video graph and the text graph.
	 *
	 * @param oldName is the name the node currently has.
	 * @param newName is the name the node will have.
	 */
	private void changeName(String oldName, String newName) {
		changeName(videoGraph,oldName,newName);
		changeName(textGraph,oldName,newName);
	}

	/**
	 * The <code>changeName</code> method changes the name of a node in the given graph.
	 *
	 * @param graph is the graph in which to change the name of the node.
	 * @param oldName is the name the node currently has.
	 * @param newName is the name the node will have.
	 */
	private static void changeName(Model graph, String oldName, String newName) {
		Resource oldResource;					//The node to remove
		Resource newResource;					//The node to add
		StmtIterator triples;					//To loop through the triples
		Statement triple;						//One of the triples
		Property predicate;						//The predicate of that triple
		RDFNode object;							//The object of that triple
		Statement newTriple;					//The triple to replace it with
		ArrayList<Statement> triplesToRemove;	//The old triples
		
		triplesToRemove = new ArrayList<Statement>((int)graph.size());				//Initialize
		oldResource = graph.createResource(oldName);
		newResource = graph.createResource(newName);
		triples = graph.listStatements(oldResource,null,(RDFNode)null);	//It's only in the subject in graph
		while (triples.hasNext()) {
			triple = triples.next();
			if (Global.D_BUG) {
				System.out.print("DEBUG in GraphMerger.changeName: triple = ");
				Global.printTriple(triple);
				System.out.println();
			}
			predicate = triple.getPredicate();
			object = triple.getObject();
			newTriple = graph.createStatement(newResource,predicate,object);
			graph.add(newTriple);
			triplesToRemove.add(triple);
		}
		graph.remove(triplesToRemove);
		triples = graph.listStatements(null,null,oldResource);					//In the object
		if (triples.hasNext()) {				//It shouldn't be in the object in this graph
			System.err.println("WARNING in GraphMerger.changeName: Bug detected. Contact Ken Samuel.");
		}
	}
	
	/**
	 * The <code>getIntersection</code> method returns a set that has the elements that are in both of the
	 * given sets.
	 *
	 * @param set1 is one of the sets to compare.
	 * @param set2 is the other set.
	 * @return a set with all and only the elements that are in both of those sets. 
	 */
	private static HashSet<NodeTypeMatch> getIntersection(HashSet<NodeTypeMatch> set1, HashSet<NodeTypeMatch> set2) {
		HashSet<NodeTypeMatch> returnValue;
	
		returnValue = new HashSet<NodeTypeMatch>(set1);
		returnValue.retainAll(set2);
		return returnValue;
	}

	/**
	 * The <code>getIAINode</code> method returns the node with the given name from <code>inputNodes</code>. 
	 * If it is not found, a new node is created and added to <code>inputNodes</code>.
	 *
	 * @param graphType is the name of the graph that the desired node is in.
	 * @param nodeName is the name of the desired node.
	 * @return the <code>IAINode</code> with the given name.
	 */
	private IAINode getIAINode(GraphType graphType, String nodeName) {
		IAINode returnValue;

		if (inputNodes.get(graphType).containsKey(nodeName)) {
			returnValue = inputNodes.get(graphType).get(nodeName);
		} else {
			returnValue = new IAINode(nodeName,graphType);
			inputNodes.get(graphType).put(nodeName,returnValue);
		}
		return returnValue;
	}
	
	/**
	 * The <code>areCompatibleTypes</code> method determines whether two nodes have compatible types.
	 *
	 * @param node1 is one of the nodes to compare.
	 * @param node2 is the other node.
	 * @return <b><code>false</code></b> unless the given nodes' types are identical or one is a subtype of 
	 * the other or both nodes are literals with the same value.
	 * @see IAINode
	 */
	private static Boolean areCompatibleTypes(IAINode node1, IAINode node2) {
//		String type1, type2;					//The types of the input nodes
		
		if ((node1.isLiteral()) && (node2.isLiteral()) && (node1.getNodeName().equals(node2.getNodeName()))) {
			return true;		//Identical literals	//TODO: Do I really want to merge literals?
		}
		if ((node1.isLiteral()) || (node2.isLiteral())) {
			return false;
		}
//		type1 = node1.getNodeType();
//		type2 = node2.getNodeType();
//		return type1.equals(type2);
		return (
				(node1.getNodeType().equals(node2.getNodeType())) ||
				(node1.hasSubtype(node2)) ||
				(node2.hasSubtype(node1)));
	}

	/**
	 * The <code>loadGraph</code> method reads in an RDF graph from a file and returns it.
	 *
	 * @param filename is the name of the input file. It should be in RDF format.
	 * @return the graph found in that file.
	 * @throws FileNotFoundException if the input file cannot be found.
	 * @throws IOException if there's a problem closing the file input stream.
	 */
	private static Model loadGraph(String filename) throws FileNotFoundException, IOException {
		FileInputStream stream;			//A stream from the file
		Model returnValue;
		
		System.out.print("Loading \"" + filename + "\"...");
		stream = new FileInputStream(filename);
		returnValue = ModelFactory.createDefaultModel();
		returnValue.read(stream,null);
		stream.close();
		System.out.println(" Done.");

		return returnValue;
	}

	/**
	 * The <code>loadOntology</code> method reads in an OWL ontology from a file and saves it in the field
	 * <code>ontology</code>.
	 *
	 * @param filename is the name of the input file. It should be in OWL format.
	 * @return the ontology found in that file.
	 * @throws FileNotFoundException if the input file cannot be found.
	 * @throws IOException if there's a problem closing the file input stream.
	 */
	public OntModel loadOntology(String filename) throws FileNotFoundException, IOException {
		FileInputStream stream;			//A stream from the file
		OntModel returnValue;

		System.out.print("Loading \"" + filename + "\"...");
		stream = new FileInputStream(filename);
		returnValue = ModelFactory.createOntologyModel();
		returnValue.read(stream,null);
		stream.close();
		System.out.println(" Done.");

		return returnValue;
	}
	
	/**
	 * The <code>saveGraph</code> method saves an RDF graph to a file.
	 *
	 * @param graph is the graph to save.
	 * @param filename is the name of the output file.
	 * @throws FileNotFoundException if the folder for the output file cannot be found.
	 * @throws IOException if there's a problem with the file input stream.
	 */
	private static void saveGraph(Model graph, String filename) throws FileNotFoundException, IOException {
		FileOutputStream stream;			//A stream to the file

		System.out.print("Saving \"" + filename + "\"...");
		stream = new FileOutputStream(filename);
		graph.write(stream);
		stream.close();
		System.out.println(" Done.");
	}
	
	/**
	 * The <code>main</code> method is where the program begins.
	 *
	 * @param args can (optionally) have a value for the minimum number of characteristics of two nodes that 
	 * must match in order to merge the nodes.
	 * @throws Exception if there are any problems.
	 */
	public static void main(String[] args) throws Exception {
		RunTests.go(args);
	}
}