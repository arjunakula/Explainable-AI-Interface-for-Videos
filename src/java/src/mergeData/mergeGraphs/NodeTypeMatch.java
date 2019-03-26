package mergeData.mergeGraphs;

import java.util.HashSet;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * The <code>NodeTypeMatch</code> class represents a pair of nodes, one from each input graph, that have 
 * compatible types. Two types are compatible if they are equivalent or one is a subtype of the other.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 5, 2015
 * @since 1.6
 * @see IAINode
 */
public class NodeTypeMatch {

	/** <code>videoNode</code> is the node in this pair that comes from the video graph. */
	IAINode videoNode;

	/** <code>textNode</code> is the node in this pair that comes from the text graph. */
	IAINode textNode;

	/** 
	 * <code>nodeType</code> is the combined type of the two nodes in this pair. If they have different types, 
	 * <code>nodeType</code> is the more specific type (the subtype).
	 */
	String nodeType;

	/** 
	 * <code>nodeTypeDistance</code> is the number of subtype/supertype links in the ontology between the 
	 * types of the nodes in this pair. If the nodes have the same type, then <code>nodeTypeDistance</code> = 
	 * 0. 
	 */
	Integer nodeTypeDistance;

	/** 
	 * The values of <code>tripleMatches</code> is the set of <code>TripleMatch</code> triples in which this
	 * pair is the subject or the object.
	 */
	HashSet<TripleMatch> tripleMatches;
	
	
	/**
	 * The <code>NodeTypeMatch</code> constructor initializes the fields.
	 *
	 * @param videoNodeIn is going to be the node in this pair that comes from the video graph.
	 * @param textNodeIn is going to be the node in this pair that comes from the text graph.
	 * @param ontology is the ontology that specifies which types are subtypes of which types.
	 */
	public NodeTypeMatch(IAINode videoNodeIn, IAINode textNodeIn, OntModel ontology) {
		videoNode = videoNodeIn;
		textNode = textNodeIn;
		combineNodeTypes(ontology);
		tripleMatches = null;
	}

	/**
	 * The <code>combineNodeTypes</code> method sets a node type for this node pair.
	 * 
	 * @param ontology is the ontology that specifies which types are subtypes of which types.
	 */
	private void combineNodeTypes(OntModel ontology) {
//		String videoNodeType, textNodeType;				//The two nodes' types
//		OntClass videoNodeType, textNodeType;			//The two nodes' types
		
//		videoNodeType = videoNode.getNodeType();
//		videoNodeType = videoNode.getOntologyNodeType();
//		textNodeType = textNode.getNodeType();
//		textNodeType = textNode.getOntologyNodeType();
		if (videoNode.hasSubtype(textNode)) {
//			nodeType = textNodeType;			
			nodeType = textNode.getNodeType();			
		} else {
//			nodeType = videoNodeType;
			nodeType = videoNode.getNodeType();			
		}
		if (videoNode.hasSameType(textNode)) {
			nodeTypeDistance = 0;
		} else {
			nodeTypeDistance = videoNode.computeNodeTypeDistance(textNode,ontology);
		}
	}
	
	/**
	 * The <code>setTripleMatches</code> setter changes the value of the global variable,
	 * <code>tripleMatches</code>, a <b><code>HashSet<TripleMatch></code></b>.
	 *
	 * @param tripleMatchesIn is the new value that should be assigned to <code>tripleMatches</code>.
	 */
	public void setTripleMatches(HashSet<TripleMatch> tripleMatchesIn) {
		tripleMatches = tripleMatchesIn;
	}

	/**
	 * The <code>getVideoNode</code> getter returns the value of the global variable,
	 * <code>videoNode</code>, a <b><code>IAINode</code></b>.
	 *
	 * @return the value of <code>videoNode</code>.
	 */
	public IAINode getVideoNode() {
		return videoNode;
	}

	/**
	 * The <code>getTextNode</code> getter returns the value of the global variable,
	 * <code>textNode</code>, a <b><code>IAINode</code></b>.
	 *
	 * @return the value of <code>textNode</code>.
	 */
	public IAINode getTextNode() {
		return textNode;
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
	 * The <code>getTripleMatches</code> getter returns the value of the global variable,
	 * <code>tripleMatches</code>, a <b><code>HashSet<TripleMatch></code></b>.
	 *
	 * @return the value of <code>tripleMatches</code>.
	 */
	public HashSet<TripleMatch> getTripleMatches() {
		return tripleMatches;
	}

	/**
	 * The <code>getNumTripleMatches</code> method returns the number of triple matches of this pair.
	 *
	 * @return the number of triples matches that this pair is in.
	 * @see TripleMatch
	 */
	public Integer getNumTripleMatches() {
		return tripleMatches.size();
	}

	/**
	 * The <code>toString</code> method returns this node's name.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return videoNode + "=" + textNode;
	}
}