package sparql.translator.restrictions;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import sparql.translator.utilities.Global;

/**
 * The <code>OptionalRestriction</code> class holds a list of SPARQL restrictions that should be surrounded by
 * the word OPTIONAL.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 4, 2014
 * @since 1.6
 */
public class OptionalRestriction extends Restriction {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>restrictions</code> is the list of restrictions that should collectively be marked as OPTIONAL.
	 */
	private LinkedHashSet<Restriction> restrictions;
	
	/** 
	 * <code>doOutput</code> specifies whether this restriction should be included when the query is output. 
	 */
	private Boolean doOutput;
	
	
	/**
	 * The <code>OptionalRestriction</code> constructor initializes the global variables.
	 *
	 */
	public OptionalRestriction() {
		super();
		restrictions = new LinkedHashSet<Restriction>();
		doOutput = false;	//They should only be output after being extracted and added to the query's end
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
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		for (Restriction restriction : restrictions) {
			restriction.replaceVariable(oldVariable,newVariable,owner);
		}
	}

	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this object
	 * to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>OptionalRestriction</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	@Override
	public OptionalRestriction reorderRestrictions(String owner) {
		OptionalRestriction returnValue;
		
		returnValue = new OptionalRestriction();
		returnValue.setPolarity(polarity);
		returnValue.setHasPFFRestriction(hasPredicateFilterFunctionRestriction);
		returnValue.setRestrictions(Global.reorderRestrictions(restrictions,owner));
		returnValue.setDoOutput(doOutput);
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
		String warning;							//A message to let the user know there might be a problem

		warning = 
				"WARNING in OptionalRestriction.extractOptionalRestrictions: " +
				"Bug detected. Contact Ken Samuel.";
		System.err.println(warning);			//This method should never be called for an OptionalRestriction
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
	public OptionalRestriction clone() {
		OptionalRestriction returnValue;
		
		returnValue = (OptionalRestriction)super.clone();
		for (Restriction restriction : restrictions) {
			returnValue.addRestriction(restriction.clone());
		}
		returnValue.setDoOutput(doOutput);
		return returnValue;
	}

	/**
	 * The <code>addRestriction</code> method adds the given restriction to this object's list of
	 * restrictions.
	 *
	 * @param restriction is a new restriction to be added to this optional restriction.
	 */
	public void addRestriction(Restriction restriction) {
		restrictions.add(restriction);
	}
	
	/**
	 * The <code>setRestrictions</code> setter changes the value of the global variable,
	 * <code>restrictions</code>, a <b><code>LinkedHashSet<Restriction></code></b>.
	 *
	 * @param restrictionsIn is the new value that should be assigned to <code>restrictions</code>.
	 */
	public void setRestrictions(LinkedHashSet<Restriction> restrictionsIn) {
		restrictions = restrictionsIn;
	}

	/**
	 * The <code>setDoOutput</code> setter changes the value of the global variable,
	 * <code>doOutput</code>, a <b><code>Boolean</code></b>.
	 *
	 * @param doOutputIn is the new value that should be assigned to <code>doOutput</code>.
	 */
	public void setDoOutput(Boolean doOutputIn) {
		doOutput = doOutputIn;
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
	public String getString(String tab, String owner) {
		String line;									//One of the line in the return value
		StringBuffer returnValue;

		if (doOutput == false) { return ""; }
		if (restrictions.size() == 0) { return ""; }
		returnValue = new StringBuffer();
		returnValue.append("\n" + tab + "OPTIONAL {");
		for (Restriction restriction : restrictions) {
			line = restriction.toString("    " + tab,owner);
			if (line != null) {
				returnValue.append(line);
			}
		}
		returnValue.append("\n" + tab + "}");
		return returnValue.toString();
	}
}