package mergeData.mergeGraphs;

import java.util.HashSet;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntTools;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * The <code>IAINode</code> class has information about a node in an RDF graph.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 4, 2015
 * @since 1.6
 */
public class IAINode {

	/** <code>nodeName</code> is the name of this node. */
	String nodeName;
	
	/**
	 * <code>nodeType</code> is the type of this node, such as <code>Human</code>, <code>SmallObject</code>, 
	 * or <code>IsTouching</code>. 
	 */
	String nodeType;

	/** <code>ontologyNodeType</code> is the type of this node in the form of an <code>OntClass</code>. */
	OntClass ontologyNodeType;

	/** 
	 * <code>graphType</code> specifies which graph this node is in: The video graph, the text graph, or the
	 * merged graph. 
	 */
	GraphType graphType;

	/** 
	 * If this node was combined with another node to make a merged node, <code>mergedNode</code> is the node 
	 * that was produced by that merging.
	 */
//	HashSet<Node> mergedNodes;
	IAINode mergedNode;

	/** 
	 * <code>subjectTriples</code> is the set of triples in which this node is the subject.
	 * @see IAITriple
	 */
	HashSet<IAITriple> subjectTriples;

	/** 
	 * <code>objectTriples</code> is the set of triples in which this node is the object.
	 * @see IAITriple
	 */
//	HashMap<Property,HashSet<Node>> objects;
	HashSet<IAITriple> objectTriples;

	/** <code>nodeTypeMatches</code> is a set with all of the node type match pairs that this node is in. */
	HashSet<NodeTypeMatch> nodeTypeMatches;


	/**
	 * The <code>IAINode</code> constructor initializes the fields.
	 * 
	 * @param nodeNameIn is going to be the name of this node.
	 * @param graphTypeIn is going to be the type of graph this node is in. 
	 */
	public IAINode(String nodeNameIn, GraphType graphTypeIn) {
		nodeName = nodeNameIn;
		nodeType = null;
		ontologyNodeType = null;
		graphType = graphTypeIn;
		mergedNode = null;
		subjectTriples = new HashSet<IAITriple>();
		objectTriples = new HashSet<IAITriple>();
		nodeTypeMatches = new HashSet<NodeTypeMatch>();
	}
	
	/**
	 * The <code>addMergedNode</code> method updates this node based on the fact that it has been merged with
	 * another node.
	 *
	 * @param mergedNode is the node that was produced by the merging of this node with another node.
	 */
/*	public void addMergedNode(Node mergedNode) {
		if (mergedNodes == null) {
			mergedNodes = new HashSet<Node>();
		}
		mergedNodes.add(mergedNode);
	}
*/	

	/**
	 * The <code>setNodeType</code> setter changes the value of the global variable,
	 * <code>nodeType</code>, a <b><code>String</code></b>.
	 *
	 * @param nodeTypeIn is the new value that should be assigned to <code>nodeType</code>.
	 */
	public void setNodeType(String nodeTypeIn) {
		nodeType = nodeTypeIn;
	}

	/**
	 * The <code>setOntologyNodeType</code> setter changes the value of the global variable,
	 * <code>ontologyNodeType</code>, an <b><code>OntClass</code></b>.
	 *
	 * @param ontologyNodeTypeIn is the new value that should be assigned to <code>ontologyNodeType</code>.
	 */
	public void setOntologyNodeType(OntClass ontologyNodeTypeIn) {
		ontologyNodeType = ontologyNodeTypeIn;
	}

	/**
	 * The <code>setNodeName</code> setter changes the value of the global variable,
	 * <code>nodeName</code>, a <b><code>String</code></b>.
	 *
	 * @param nodeNameIn is the new value that should be assigned to <code>nodeName</code>.
	 */
	public void setNodeName(String nodeNameIn) {
		nodeName = nodeNameIn;
	}

	/**
	 * The <code>setMergedNode</code> setter changes the value of the global variable,
	 * <code>mergedNode</code>, a <b><code>IAINode</code></b>.
	 *
	 * @param mergedNodeIn is the new value that should be assigned to <code>mergedNode</code>.
	 */
	public void setMergedNode(IAINode mergedNodeIn) {
		mergedNode = mergedNodeIn;
	}

	/**
	 * The <code>addSubjectTriple</code> method adds the given triple to the global variable,
	 * <code>subjectTriples</code>, a <b><code>HashSet<IAITriple></code></b>.
	 * 
	 * @param triple is the triple that should be added to <code>subjectTriples</code>.
	 */
	public void addSubjectTriple(IAITriple triple) {
		subjectTriples.add(triple);
	}

	/**
	 * The <code>addObjectTriple</code> method adds the given triple to the global variable,
	 * <code>objectTriples</code>, a <b><code>HashSet<IAITriple></code></b>.
	 * 
	 * @param triple is the triple that should be added to <code>objectTriples</code>.
	 */
	public void addObjectTriple(IAITriple triple) {
		objectTriples.add(triple);
	}

	/**
	 * The <code>addNodeTypeMatch</code> method adds the given node type match to the global variable,
	 * <code>nodeTypeMatches</code>, a <b><code>HashSet<IAITriple></code></b>.
	 * 
	 * @param nodeTypeMatch is the node type match that should be added to <code>nodeTypeMatches</code>.
	 * @see NodeTypeMatch
	 */
	public void addNodeTypeMatch(NodeTypeMatch nodeTypeMatch) {
		nodeTypeMatches.add(nodeTypeMatch);
	}

	/**
	 * The <code>removeSubjectTriple</code> method removes the given triple from the global variable,
	 * <code>subjectTriples</code>, a <b><code>HashSet<IAITriple></code></b>.
	 * 
	 * @param triple is the triple that should be added to <code>subjectTriples</code>.
	 */
	public void removeSubjectTriple(IAITriple triple) {
		subjectTriples.remove(triple);
	}

	/**
	 * The <code>addObjectTriple</code> method removes the given triple from the global variable,
	 * <code>objectTriples</code>, a <b><code>HashSet<IAITriple></code></b>.
	 * 
	 * @param triple is the triple that should be added to <code>objectTriples</code>.
	 */
	public void removeObjectTriple(IAITriple triple) {
		objectTriples.remove(triple);
	}

	/**
	 * The <code>wasMerged</code> method determines whether this node was merged with another node to make 
	 * a new node.
	 *
	 * @return <b><code>true</code></b> if this node was merged, and <b><code>false</code></b> otherwise.
	 */
	public Boolean wasMerged() {
		return (mergedNode != null);
	}
	
	/**
	 * The <code>isLiteral</code> method specifies whether or not this node is a literal like "green".
	 *
	 * @return <b><code>true</code></b> if and only if this node represents a literal value as opposed to a 
	 * regular node.
	 */
	public Boolean isLiteral() {
		return (nodeType == null);
	}


	/**
	 * The <code>computeNodeTypeDistance</code> method determines how many subtype or supertype links must be
	 * traversed in order to get from this node's type to another node's type. The two types must be in a
	 * subtype/supertype relation or equivalent.
	 *
	 * @param otherNode is the node to compare with this node.
	 * @param ontology is the ontology that specifies which types are subtypes of which types.
	 * @return the distance between <code>ontologyNodeType</code> and <code>otherNode</code>'s type.
	 */
	public Integer computeNodeTypeDistance(IAINode otherNode, OntModel ontology) {
		OntClass otherOntologyNodeType;
		
		otherOntologyNodeType = otherNode.getOntologyNodeType();
		if ((ontologyNodeType == null) && (otherOntologyNodeType == null)) {
			return 0;
		}
		if ((ontologyNodeType == null) || (otherOntologyNodeType == null)) {
			System.err.println(
					"WARNING in IAINode.computeNodeTypeDistance: Bug detected. Contact Ken Samuel");
			return 0;
		}
		if (ontologyNodeType == otherOntologyNodeType) {		//Are they the same?
			return 0;
		}
		if (hasSubtype(otherOntologyNodeType)) {
//			Model w = ontology.getBaseModel();
//			Resource x = otherOntologyNodeType;
//			RDFNode y = ontologyNodeType;
//			OntTools.PredicatesFilter z = new OntTools.PredicatesFilter(RDFS.subClassOf);
//			Path a = OntTools.findShortestPath(w,x,y,z);
//			return a.size();
			return OntTools.findShortestPath(
					ontology.getBaseModel(), 
					otherOntologyNodeType, 
					ontologyNodeType,
					new OntTools.PredicatesFilter(RDFS.subClassOf)
					).size();
		}
		if (hasSupertype(otherOntologyNodeType)) {
			return OntTools.findShortestPath(
					ontology.getBaseModel(), 
					ontologyNodeType,
					otherOntologyNodeType, 
					new OntTools.PredicatesFilter(RDFS.subClassOf)
					).size();
		}
		System.err.println("WARNING in IAINode.computeNodeTypeDistance: Bug detected. Contact Ken Samuel");
		return 0;
	}

	/**
	 * The <code>hasSameType</code> method determines whether the type of a given node is the same as the type
	 * of this node.
	 *
	 * @param otherNode is the node that should be compared with this node.
	 * @return <b><code>true</code></b> if  <code>otherNodeType</code> is the same as 
	 * <code>nodeType</code>, and <b><code>false</code></b> otherwise.
	 */
	public Boolean hasSameType(IAINode otherNode) {
		return hasSameType(otherNode.getOntologyNodeType());
//		Boolean returnValue = hasSameType(otherNode.getOntologyNodeType());
//		return returnValue;
	}
		
	/**
	 * The <code>hasSameType</code> method determines whether the type of a given node is the same as the type
	 * of this node.
	 *
	 * @param otherNodeType is the node that should be compared with this node.
	 * @return <b><code>true</code></b> if  <code>otherNodeType</code> is the same as 
	 * <code>nodeType</code>, and <b><code>false</code></b> otherwise.
	 */
	private Boolean hasSameType(OntClass otherNodeType) {
		if ((ontologyNodeType == null) || (otherNodeType == null)) {
//			System.err.println("WARNING in IAINode.hasSubtype: Bug detected. Contact Ken Samuel");
			return false;
		}
//		return ontologyNodeType.hasEquivalentClass(otherNodeType);
//		return ontologyNodeType == otherNodeType;
//		Boolean returnValue = ((ontologyNodeType == otherNodeType) || (ontologyNodeType.hasEquivalentClass((Resource)otherNodeType)));
//		return returnValue;
		return (ontologyNodeType.toString().equals(otherNodeType.toString()));
	}
			
	/**
	 * The <code>hasSubtype</code> method determines whether the type of a given node is a subtype of the type
	 * of this node.
	 *
	 * @param otherNode is the node that should be compared with this node.
	 * @return <b><code>true</code></b> if  <code>otherNode</code>'s type is a subtype of 
	 * <code>nodeType</code>, and <b><code>false</code></b> otherwise.
	 */
	public Boolean hasSubtype(IAINode otherNode) {
		return hasSubtype(otherNode.getOntologyNodeType());
	}
		
	/**
	 * The <code>hasSubtype</code> method determines whether the type of a given node is a subtype of the type
	 * of this node.
	 *
	 * @param otherNodeType is the node that should be compared with this node.
	 * @return <b><code>true</code></b> if  <code>otherNodeType</code> is a subtype of 
	 * <code>nodeType</code>, and <b><code>false</code></b> otherwise.
	 */
	private Boolean hasSubtype(OntClass otherNodeType) {
		if ((ontologyNodeType == null) || (otherNodeType == null)) {
//			System.err.println("WARNING in IAINode.hasSubtype: Bug detected. Contact Ken Samuel");
			return false;
		}
		return ontologyNodeType.hasSubClass(otherNodeType);
	}
		
	/**
	 * The <code>hasSupertype</code> method determines whether the type of a given node is a supertype of the 
	 * type of this node.
	 *
	 * @param otherNode is the node that should be compared with this node.
	 * @return <b><code>true</code></b> if the type of <code>otherNode</code> is a supertype of 
	 * <code>nodeType</code>, and <b><code>false</code></b> otherwise.
	 */
//	private Boolean hasSupertype(IAINode otherNode) {
//		return ontologyNodeType.hasSuperClass(otherNode.getOntologyNodeType());
//	}
	
/**
 * The <code>hasSupertype</code> method determines whether the type of a given node is a subtype of the type
 * of this node.
 *
 * @param otherNodeType is the node that should be compared with this node.
 * @return <b><code>true</code></b> if  <code>otherNodeType</code> is a subtype of 
 * <code>nodeType</code>, and <b><code>false</code></b> otherwise.
 */
private Boolean hasSupertype(OntClass otherNodeType) {
	if ((ontologyNodeType == null) || (otherNodeType == null)) {
		System.err.println("WARNING in IAINode.hasSupertype: Bug detected. Contact Ken Samuel");
		return false;
	}
	return ontologyNodeType.hasSuperClass(otherNodeType);
}
	
		
	/**
	 * The <code>getCurrentNode</code> getter returns this node or the node that this node was changed to.
	 *
	 * @return the node produced by merging this node, if this node has been merged. Otherwise, return this 
	 * node.
	 */
	public IAINode getCurrentNode() {
		if (wasMerged()) {
			return mergedNode;
		}
		return this;
	}

	/**
	 * The <code>getNodeName</code> getter returns the value of the global variable,
	 * <code>nodeName</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>nodeName</code>.
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * The <code>getNodeType</code> getter returns the value of the global variable,
	 * <code>nodeType</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>nodeType</code>.
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * The <code>getOntologyNodeType</code> getter returns the value of the global variable,
	 * <code>ontologyNodeType</code>, a <b><code>OntClass</code></b>.
	 *
	 * @return the value of <code>ontologyNodeType</code>.
	 */
	private OntClass getOntologyNodeType() {
		return ontologyNodeType;
	}

	/**
	 * The <code>getSubjectTriples</code> getter returns the value of the global variable,
	 * <code>subjectTriples</code>, a <b><code>HashSet<IAITriple></code></b>.
	 *
	 * @return the value of <code>subjectTriples</code>.
	 */
	public HashSet<IAITriple> getSubjectTriples() {
		return subjectTriples;
	}

	/**
	 * The <code>getObjectTriples</code> getter returns the value of the global variable,
	 * <code>objectTriples</code>, a <b><code>HashSet<IAITriple></code></b>.
	 *
	 * @return the value of <code>objectTriples</code>.
	 */
	public HashSet<IAITriple> getObjectTriples() {
		return objectTriples;
	}

	/**
	 * The <code>getNodeTypeMatches</code> getter returns the value of the global variable,
	 * <code>nodeTypeMatches</code>, a <b><code>HashSet<NodeTypeMatch></code></b>.
	 *
	 * @return the value of <code>nodeTypeMatches</code>.
	 */
	public HashSet<NodeTypeMatch> getNodeTypeMatches() {
		return nodeTypeMatches;
	}

	/**
	 * The <code>toString</code> method returns this node's name.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (getNodeType() == null) {			//It's a literal, like "green"
			return "\"" + getNodeName() +"\"";
		}
		return Global.removeNamespace(getNodeName());
	}
}