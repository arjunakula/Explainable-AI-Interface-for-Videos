package sparql.translator.utilities;

/**
 * The <code>XMLType</code> catagorizes the input XML document into one of these types.
 *
 * @author Ken Samuel
 * @version 1.0, Jan 29, 2015
 * @since 1.6
 */
public enum XMLType {

	/** 
	 * A <code>Query</code> <code>XMLType</code> represents a polar query. The system should respond to this
	 * query with "true" or "false".
	 */
	Query,

	/** 
	 * A <code>What</code> <code>XMLType</code> represents a <code>&lt;what&gt</code> non-polar query. The 
	 * system should respond to this query with a list of objects.
	 */
	What,

	/** 
	 * A <code>When</code> <code>XMLType</code> represents a <code>&lt;when&gt</code> non-polar query. The 
	 * system should respond to this query with a list of time periods.
	 */
	When,

	/** 
	 * A <code>Where</code> <code>XMLType</code> represents a <code>&lt;where&gt</code> non-polar query. The 
	 * system should respond to this query with a bounding box.
	 */
	Where,

	/** 
	 * An <code>Object</code> <code>XMLType</code> represents an <code>&lt;object-spec&gt</code>, which
	 * assigns an identifier to a specific object.
	 */
	Object,

	/** 
	 * An <code>ObjectType</code> <code>XMLType</code> represents a query designed to determine the type of 
	 * an object that is going to be put in a &lt;WhatAnswer&gt;.
	 */
	ObjectType,

	/** 
	 * A <code>SceneDescriptiveText</code> <code>XMLType</code> represents a 
	 * <code>&lt;SceneDescriptiveText-spec&gt</code>, which provides factual information.
	 */
	SceneDescriptiveText,
}