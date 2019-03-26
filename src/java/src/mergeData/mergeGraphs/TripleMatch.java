package mergeData.mergeGraphs;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * The <code>TripleMatch</code> class is similar to the <code>Triple</code> class, but its subject and object are 
 * both <code>NodeTypeMatch</code>es instead of <code>Node</code>s. In other words, it is a triple that is found in both of the input graphs.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 5, 2015
 * @since 1.6
 * @see IAITriple
 * @see NodeTypeMatch
 */
public class TripleMatch {
	
	/** <code>subject</code> is the two nodes that are the subject of this triple. */
	NodeTypeMatch subject;
	
	/** <code>predicate</code> is the predicate in this triple. */
	Property predicate;
	
	/** <code>object</code> is the two nodes that are the object of this triple. */
	NodeTypeMatch object;
	

	/**
	 * The <code>TripleMatch</code> constructor initializes the fields.
	 *
	 * @param subjectIn is going to be this triple's subject.
	 * @param predicateIn is going to be this triple's predicate.
	 * @param objectIn is going to be this triple's object.
	 */
	public TripleMatch(NodeTypeMatch subjectIn, Property predicateIn, NodeTypeMatch objectIn) {
		subject = subjectIn;
		predicate = predicateIn;
		object = objectIn;
	}

	/**
	 * The <code>getSubject</code> getter returns the value of the global variable,
	 * <code>subject</code>, a <b><code>NodeTypeMatch</code></b>.
	 *
	 * @return the value of <code>subject</code>.
	 */
	public NodeTypeMatch getSubject() {
		return subject;
	}

	/**
	 * The <code>getPredicate</code> getter returns the value of the global variable,
	 * <code>predicate</code>, a <b><code>Property</code></b>.
	 *
	 * @return the value of <code>predicate</code>.
	 */
	public Property getPredicate() {
		return predicate;
	}

	/**
	 * The <code>getObject</code> getter returns the value of the global variable,
	 * <code>object</code>, a <b><code>NodeTypeMatch</code></b>.
	 *
	 * @return the value of <code>object</code>.
	 */
	public NodeTypeMatch getObject() {
		return object;
	}

	/**
	 * The <code>toString</code> method returns this node's name.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<" + getSubject() + "," + getPredicate().getLocalName() + "," + getObject() + ">";
	}
}