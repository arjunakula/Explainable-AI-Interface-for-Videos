package sparql.translator.definitions;

import org.w3c.dom.Node;

import sparql.translator.utilities.Global;

/**
 * The <code>ObjectDefinition</code> class holds important information about an object definition in an XML
 * query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class ObjectDefinition extends Definition {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>objectId</code>  is identifier of this object in the database if there is exactly one. */
	private String objectId;
	
	/** <code>xMLtype</code> is the type of this object in the form found in XML queries, such as "person". */
	private String xMLtype;
		
	/**
	 * The <code>ObjectDefinition</code> constructor initializes the global variables. This constructor is
	 * used for objects that are defined in XML queries.
	 *
	 * @param objectNode is the XML definition of the object.
	 */
	public ObjectDefinition(Node objectNode) {
		super(objectNode);
		objectId = null;
	}
	
	/**
	 * The <code>ObjectDefinition</code> constructor initializes the global variables. This constructor is
	 * used for objects that are answers to &lt;what&gt; queries.
	 *
	 * @param xMLId is the identifier of this object that may appear in future XML queries.
	 * @param sPARQLId is the identifier of this object in the database.
	 */
	public ObjectDefinition(String xMLId, String sPARQLId) {
		super(xMLId);
		objectId = sPARQLId;
	}
	
	 /**
	 * The <code>setObjectId</code> setter changes the value of the global variable,
	 * <code>objectId</code>, a <b><code>String</code></b>.
	 *
	 * @param newObjectId is the new value that should be assigned to <code>objectId</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void setObjectId(String newObjectId, String owner) {
		String warning;						//A message to let the user know there might be a problem

		if (objectId == null) {
			objectId = newObjectId;
			Global.changedDefinition(this);
		} else {
			warning = 
					"WARNING in XMLToSPARQLTranslator.reportResults in " + owner + ": " +
					"Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
			Global.unableToRespondMessage.add(warning);
		}
	}

	/**
	 * The <code>setXMLtype</code> setter changes the value of the global variable,
	 * <code>xMLtype</code>, a <b><code>String</code></b>.
	 *
	 * @param xMLtypeIn is the new value that should be assigned to <code>xMLtype</code>.
	 */
	public void setXMLtype(String xMLtypeIn) {
		xMLtype = xMLtypeIn;
		Global.changedDefinition(this);
	}

	/**
	 * The <code>getName</code> method returns the id of this object in the database.
	 *
	 * @return the database id of this object if it is known. Otherwise, return <b><code>null</code></b>.
	 */
	public String getName() {
		return objectId;
	}

	/**
	 * The <code>getObjectId</code> getter returns the only (or first) value in the global variable,
	 * <code>objectIds</code>, a <b><code>ArrayList<String></code></b>.
	 *
	 * @return this object's identifier in the database.
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * The <code>getXMLtype</code> getter returns the value of the global variable,
	 * <code>xMLtype</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>xMLtype</code>.
	 */
	public String getXMLtype() {
		return xMLtype;
	}
	

	/**
	 * The <code>toString</code> method returns the id of this definition's object.
	 *
	 * @return the value of <code>id</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}
}