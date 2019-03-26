package sparql.translator.utilities;

import java.io.Serializable;
import java.util.HashMap;

import sparql.translator.definitions.Definition;

/**
 * The <code>XMLToSPARQLFileObject</code> class holds information that should be saved to disk.
 *
 * @author Ken Samuel
 * @version 1.0, Jun 12, 2014
 * @since 1.6
 */
public class XMLToSPARQLFileObject implements Serializable {

	/** <code>serialVersionUID</code> is required, because this is a serializable object. */
	private static final long serialVersionUID = -2636987313670301105L;

	/**
	 *  <code>persistentNames</code> is a hash in which each persistent name in the XML query points to  
	 *  the object with its definition.
	 */
	private HashMap<String,Definition> persistentNames;

	/** 
	 * <code>nameCounts</code> is a hash in which the prefix of each variable and object identifier name (the 
	 * part of the name without the number at the end) points to the number of names with that prefix. 
	 */
	private HashMap<String,Integer> nameCounts;

	/**
	 * The <code>XMLToSPARQLFileObject</code> constructor reads in the values of the global variables.
	 *
	 * @param persistentNamesIn is the value to be stored in <code>persistentNames</code>.
	 * @param nameCountsIn is the value to be stored in <code>nameCounts</code>.
	 */
	public XMLToSPARQLFileObject(HashMap<String, Definition> persistentNamesIn,
			HashMap<String, Integer> nameCountsIn) {
		persistentNames = persistentNamesIn;
		nameCounts = nameCountsIn;
	}

	/**
	 * The <code>getPersistentNames</code> getter returns the value of the global variable,
	 * <code>persistentNames</code>, a <b><code>HashMap<String,Definition></code></b>.
	 *
	 * @return the value of <code>persistentNames</code>.
	 */
	public HashMap<String, Definition> getPersistentNames() {
		return persistentNames;
	}

	/**
	 * The <code>getNameCounts</code> getter returns the value of the global variable,
	 * <code>nameCounts</code>, a <b><code>HashMap<String,Integer></code></b>.
	 *
	 * @return the value of <code>nameCounts</code>.
	 */
	public HashMap<String, Integer> getNameCounts() {
		return nameCounts;
	}
}