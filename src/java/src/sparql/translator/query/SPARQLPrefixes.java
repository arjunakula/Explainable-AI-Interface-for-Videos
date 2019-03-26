package sparql.translator.query;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The <code>SPARQLPrefixes</code> holds the information in the PREFIX definitions of a SPARQL query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 1, 2013
 * @since 1.6
 */
public class SPARQLPrefixes implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>prefixes</code> is a hash in which each abbreviated form of a prefix points to its expansion. */
	private HashMap<String,String> prefixes;

	
	/**
	 * The <code>SPARQLPrefixes</code> constructor initializes this class's fields.
	 *
	 */
	public SPARQLPrefixes() {
		prefixes = new HashMap<String,String>();
	}

	/**
	 * The <code>clone</code> method makes a duplicate copy of this object and returns it. 
	 *
	 * @return a new object that is identical to this one, but any changes made to that 
	 * object will not affect this object and vice versa.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SPARQLPrefixes clone() {
		SPARQLPrefixes returnValue;
		
		returnValue = new SPARQLPrefixes();
		for (String key : prefixes.keySet()) {
			returnValue.add(key,prefixes.get(key));
		}
		return returnValue;
	}
	
	/**
	 * The <code>add</code> method adds a new prefix line to this object.
	 *
	 * @param abbreviation is the abbreviated form, which can be used in the query.
	 * @param expansion is the expanded form that specifies what the prefix means.
	 */
	public void add(String abbreviation, String expansion) {
		prefixes.put(abbreviation,expansion);
	}

	/**
	 * The <code>toString</code> method returns this object in the form of a string that can be included in
	 * a SPARQL query.
	 *
	 * @param tab specifies the number of spaces to include at the beginning of each line.
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	public String toString(String tab) {
		String newline;					//Specifies whether a newline should be printed
		StringBuffer returnValue;
		
		newline = "";
		returnValue = new StringBuffer();
		for (String abbreviation : prefixes.keySet()) {
			returnValue.append(
					newline + tab + "PREFIX " + abbreviation + ": <" + prefixes.get(abbreviation) + ">");
			newline = "\n";
		}
		returnValue.append(newline + newline);
		return returnValue.toString();
	}
	
	/**
	 * The <code>toString</code> method returns these prefixes.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.toString("");
	}
}