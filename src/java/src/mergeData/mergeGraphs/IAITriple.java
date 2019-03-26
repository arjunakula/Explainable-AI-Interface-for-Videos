package mergeData.mergeGraphs;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * The <code>IAITriple</code> class represents an RDF triple. So it consists of a subject, a predicate, and an
 * object.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 5, 2015
 * @since 1.6
 * @see IAINode
 */
public class IAITriple {

	/** <code>subject</code> is the first thing in this RDF triple. */
	IAINode subject;

	/** <code>predicate</code> is the second thing in this RDF triple. */
	Property predicate;

	/** <code>object</code> is the third thing in this RDF triple. */
	IAINode object;


	/**
	 * The <code>IAITriple</code> constructor initializes the fields.
	 *
	 * @param subjectIn is going to be this triple's subject.
	 * @param predicateIn is going to be this triple's predicate.
	 * @param objectIn is going to be this triple's object.
	 */
	public IAITriple(IAINode subjectIn, Property predicateIn, IAINode objectIn) {
		subject = subjectIn;
		predicate = predicateIn;
		object = objectIn;
	}

	/**
	 * The <code>getSubject</code> getter returns the value of the global variable,
	 * <code>subject</code>, a <b><code>IAINode</code></b>.
	 *
	 * @return the value of <code>subject</code>.
	 */
	public IAINode getSubject() {
		return subject;
	}

	/**
	 * The <code>getPredicate</code> getter returns the value of the global variable,
	 * <code>predicate</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>predicate</code>.
	 */
	public Property getPredicate() {
		return predicate;
	}

	/**
	 * The <code>getObject</code> getter returns the value of the global variable,
	 * <code>object</code>, a <b><code>IAINode</code></b>.
	 *
	 * @return the value of <code>object</code>.
	 */
	public IAINode getObject() {
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