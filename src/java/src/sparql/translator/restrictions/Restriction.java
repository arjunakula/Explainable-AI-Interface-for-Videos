package sparql.translator.restrictions;

import java.io.Serializable;
import java.util.ArrayList;

import sparql.translator.utilities.Global;


/**
 * The <code>Restriction</code> class is a restriction that can go in a SPARQL query's WHERE clause. There 
 * are three kinds of restrictions: <code>StringRestriction</code>, <code>QueryRestriction</code>, and 
 * <code>UnionRestriction</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 22, 2013
 * @since 1.6
 */
public class Restriction implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;

	/** <code>polarity</code> specifies whether this restriction should be true or false. */
	protected Boolean polarity;
	
	/** <code>hasPredicateFilterFunctionRestriction</code> specifies whether this restriction has at least
	 * one predicate (behavior, relationship, or action) filter function restriction in it. 
	 */
	protected Boolean hasPredicateFilterFunctionRestriction;
	
	
	/**
	 * The <code>Restriction</code> constructor initializes the global variables.
	 */
	public Restriction() {
		polarity = true;			//Unless negateRestriction() is called
		hasPredicateFilterFunctionRestriction = false;				//Default value
	}
	
	/**
	 * The <code>negateRestriction</code> method changes this restriction's polarity.
	 */
	public void negateRestriction() {
		polarity = ! polarity;
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this restriction
	 * with another variable.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * by one of this class's subclasses.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void replaceVariable(
			@SuppressWarnings({ "unused" }) String oldVariable, 
			@SuppressWarnings({ "unused" }) String newVariable,
			String owner) {
		String warning;							//A message to let the user know there might be a problem

		warning = 
				"WARNING in Restriction.replaceVariable in " + owner + ": Bug detected. Contact Ken Samuel.";
		System.err.println(warning);		//This method should be handled by one of Restriction's subclasses.
    	Global.unableToRespondMessage.add(warning);
	}

	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this object
	 * to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>Restriction</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	public Restriction reorderRestrictions(@SuppressWarnings("unused") String owner) {
		Restriction returnValue;
		
		returnValue = new Restriction();
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
	public ArrayList<OptionalRestriction> extractOptionalRestrictions(
			@SuppressWarnings({ "unused" }) Boolean isPositive,
			String owner) {
		String warning;							//A message to let the user know there might be a problem

		warning = 
				"WARNING in Restriction.extractOptionalRestrictions in " + owner + 
				": Bug detected. Contact Ken Samuel.";
		System.err.println(warning);				//This method should be handled by one of Restriction's subclasses.
    	Global.unableToRespondMessage.add(warning);
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
	public Restriction clone() {
		Restriction returnValue;
		
		returnValue = new Restriction();
		returnValue.setPolarity(polarity);
		returnValue.setHasPFFRestriction(hasPredicateFilterFunctionRestriction);
		return returnValue;
	}
	
	/**
	 * The <code>copyValues</code> method copies the values of this object's global variables to the given
	 * <code>Restriction</code> object.
	 *
	 * @param otherRestriction is a restriction that will be a duplicate of this object. Note that this
	 * parameter will be changed by this method.
	 */
	protected void copyValues(Restriction otherRestriction) {
		otherRestriction.setPolarity(polarity);
	}
	
	/**
	 * The <code>setPolarity</code> setter changes the value of the global variable,
	 * <code>polarity</code>, a <b><code>Boolean</code></b>.
	 *
	 * @param polarityIn is the new value that should be assigned to <code>polarity</code>.
	 */
	public void setPolarity(Boolean polarityIn) {
		polarity = polarityIn;
	}

	/**
	 * The <code>setHasPFFRestriction</code> setter changes the value of the global variable,
	 * <code>hasPredicateFilterFunctionRestriction</code>, a <b><code>Boolean</code></b>.
	 *
	 * @param hasPredicateFilterFunctionRestrictionIn is the new value that should be assigned to 
	 * <code>hasPredicateFilterFunctionRestriction</code>.
	 */
	public void setHasPFFRestriction(Boolean hasPredicateFilterFunctionRestrictionIn) {
		hasPredicateFilterFunctionRestriction = hasPredicateFilterFunctionRestrictionIn;
	}

	/**
	 * The <code>isPositive</code> getter specifies whether or not this restriction is negated.
	 *
	 * @return <b><code>false</code></b> if this restriction is negated, and <b><code>true</code></b> 
	 * otherwise.
	 */
	public Boolean isPositive() {
		return polarity;
	}
	
	/**
	 * The <code>hasPFFRestriction</code> getter returns the value of the global variable,
	 * <code>hasPredicateFilterFunctionRestriction</code>, a <b><code>Boolean</code></b>.
	 *
	 * @return the value of <code>hasPredicateFilterFunctionRestriction</code>.
	 */
	public Boolean hasPFFRestriction() {
		return hasPredicateFilterFunctionRestriction;
	}

	/**
	 * The <code>getPolarity</code> getter returns the value of the global variable,
	 * <code>polarity</code>, a <b><code>Boolean</code></b>.
	 *
	 * @return the value of <code>polarity</code>.
	 */
	public Boolean getPolarity() {
		return polarity;
	}

	/**
	 * The <code>getString</code> method returns this object in the form of a string that can be included in
	 * a SPARQL query.
	 *
	 * @param tab specifies the number of spaces to include at the beginning of each line.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a string representation of this object.
	 */
	public String getString(@SuppressWarnings("unused") String tab, String owner) {
		String warning;							//A message to let the user know there might be a problem

		warning = 
				"WARNING in Restriction.getString in " + owner + ": Bug detected. Contact Ken Samuel.";
		System.err.println(warning);		//This method should be handled by one of Restriction's subclasses.
    	Global.unableToRespondMessage.add(warning);
		return null;
	}
	
	/**
	 * The <code>toString</code> method returns this object in the form of a string that can be included in
	 * a SPARQL query.
	 *
	 * @param tab specifies the number of spaces to include at the beginning of each line.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	public String toString(String tab, String owner) {
		StringBuffer returnValue;
		
		if (polarity == false) {			//A negated restriction
			returnValue = new StringBuffer();
			returnValue.append("\n" + tab + "FILTER (NOT EXISTS {");
			returnValue.append(this.getString(tab + "    ",owner));
			returnValue.append("\n" + tab + "})");
			return returnValue.toString();
		}
		return this.getString(tab, owner);
	}

	/**
	 * The <code>toString</code> method returns this WHERE clause printed in a pretty format, but it doesn't
	 * include any of the filters.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.toString("","the <Query>");
	}
}