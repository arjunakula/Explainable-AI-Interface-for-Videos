package sparql.translator.restrictions;

import java.util.ArrayList;

/**
 * The <code>StringRestriction</code> class holds a string that can be a restriction in a SPARQL
 * query's WHERE clause.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 22, 2013
 * @since 1.6
 */
public class StringRestriction extends Restriction {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>restriction</code> is the text of this class's restriction. */
	String restriction;
	
	
	/**
	 * The <code>StringRestriction</code> constructor initializes the global variables.
	 *
	 * @param restrictionIn is the initial value of the global variable, <code>restriction</code>.
	 */
	public StringRestriction(String restrictionIn) {
		super();
		restriction = restrictionIn;
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this restriction
	 * with another variable.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@Override
	public void replaceVariable(
			String oldVariable, String newVariable, @SuppressWarnings("unused") String owner) {
		restriction = restriction.replaceAll("\\" + oldVariable,newVariable);
	}

	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this object
	 * to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>StringRestriction</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	@Override
	public StringRestriction reorderRestrictions(@SuppressWarnings("unused") String owner) {
		StringRestriction returnValue;
		
		returnValue = new StringRestriction(restriction);
		returnValue.setPolarity(polarity);
		returnValue.setHasPFFRestriction(hasPredicateFilterFunctionRestriction);
		return returnValue;
	}
	
	/**
	 * The <code>extractOptionalRestrictions</code> method searches recursively through this restriction, 
	 * removing all of the optional restrictions and returning them in a list.
	 *
	 * @param isPositive is <b><code>false</code></b> if this restriction is found within an
	 * odd number of &lt;not&gt;s, and <b><code>true</code></b> otherwise.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a list of all the optional restrictions that have been removed from this restriction.
	 */
	@Override
	public ArrayList<OptionalRestriction> extractOptionalRestrictions(
			@SuppressWarnings({ "unused" }) Boolean isPositive, @SuppressWarnings("unused") String owner) {	
		return new ArrayList<OptionalRestriction>(0);
	}

	/**
	 * The <code>clone</code> method makes a duplicate copy of this object and returns it. 
	 *
	 * @return a new object that is identical to this one, but any changes made to that 
	 * object will not affect this object and vice versa.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public StringRestriction clone() {
		StringRestriction returnValue;
		
		returnValue = (StringRestriction)super.clone();
		returnValue.setRestriction(restriction);
		return returnValue;
	}

	/**
	 * The <code>setRestriction</code> setter changes the value of the global variable,
	 * <code>restriction</code>, a <b><code>String</code></b>.
	 *
	 * @param restrictionIn is the new value that should be assigned to <code>restriction</code>.
	 */
	public void setRestriction(String restrictionIn) {
		restriction = restrictionIn;
	}

	/**
	 * The <code>getString</code> method returns this object in the form of a string that can be included in
	 * a SPARQL query.
	 *
	 * @param tab specifies the number of spaces to include at the beginning of each line.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a string representation of this object.
	 */
	@Override
	public String getString(String tab, @SuppressWarnings("unused") String owner) {
		return "\n" + tab + restriction;
	}
}