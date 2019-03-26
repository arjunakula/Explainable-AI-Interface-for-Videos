package sparql.translator.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import sparql.translator.restrictions.OptionalRestriction;
import sparql.translator.restrictions.Restriction;
import sparql.translator.utilities.Global;

/**
 * The <code>SPARQLWhere</code> holds the information in the WHERE clause of a SPARQL query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 1, 2013
 * @since 1.6
 */
public class SPARQLWhere implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;
	
	/** <code>restrictions</code> is a list of the restrictions that go in the WHERE clause of the query. */
	private LinkedHashSet<Restriction> restrictions;

	
	/**
	 * The <code>SPARQLWhere</code> constructor initializes this class's fields.	 
	 */
	public SPARQLWhere() {
		restrictions = new LinkedHashSet<Restriction>();
	}
	
	/**
	 * The <code>SPARQLWhere</code> constructor initializes this class's fields.
	 * 
	 * @param restrictionsIn is the list of restrictions in this object.
	 */
	public SPARQLWhere(LinkedHashSet<Restriction> restrictionsIn) {
		restrictions = restrictionsIn;
	}
	
	/**
	 * The <code>hasRestrictions</code> method reports whether or not there are any restrictions in this 
	 * where block.
	 *
	 * @return <b><code>true</code></b> if there is at least one restriction in this where block and 
	 * <b><code>false</code></b> otherwise.
	 */
	public Boolean hasRestrictions() {
		return (restrictions.size() > 0);
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this WHERE
	 * clause with another variable.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		for (Restriction restriction : restrictions) {
			restriction.replaceVariable(oldVariable,newVariable,owner);
		}
	}
	
	/**
	 * The <code>sortQuery</code> method moves all of the optional restrictions to the end of this
	 * WHERE block.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void sortQuery(String owner) {
		extractOptionalRestrictions(true,owner);
	}
	
	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this object
	 * to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>SPARQLWhere</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	public SPARQLWhere reorderRestrictions(String owner) {
		LinkedHashSet<Restriction> reorderedRestrictions;			//The restrictions, reordered
		SPARQLWhere returnValue;
		
		reorderedRestrictions = Global.reorderRestrictions(restrictions,owner);
		returnValue = new SPARQLWhere(reorderedRestrictions);
		return returnValue;
	}

	/**
	 * The <code>extractOptionalRestrictions</code> method searches recursively through this WHERE block's
	 * restrictions, removing all of the optional restrictions and returning them in a list.
	 *
	 * @param isPositive is <b><code>false</code></b> if this WHERE block's restrictions are found within an
	 * odd number of &lt;not&gt;s, and <b><code>true</code></b> otherwise.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return an empty list, because all of the optional restrictions are fully processed in this method,
	 * so they shouldn't be passed on.
	 */
	public ArrayList<OptionalRestriction> extractOptionalRestrictions(Boolean isPositive, String owner) {
		ArrayList<OptionalRestriction> myOptionalRestrictions;		//Optional restrictions in restrictions
		ArrayList<OptionalRestriction> theirOptionalRestrictions;	//Optional restrictions from other objects
		
		myOptionalRestrictions = new ArrayList<OptionalRestriction>();				//Initialize
		theirOptionalRestrictions = new ArrayList<OptionalRestriction>();			//Initialize
		for (Restriction restriction : restrictions) {
			if (restriction.getClass() == OptionalRestriction.class) {		//Found one
				myOptionalRestrictions.add((OptionalRestriction)restriction);
			} else {
				theirOptionalRestrictions.addAll(restriction.extractOptionalRestrictions(
						isPositive == restriction.isPositive(),owner));
			}
		}
		for (OptionalRestriction optionalRestriction : myOptionalRestrictions) {
			if (isPositive == optionalRestriction.isPositive()) {		//Don't keep the negative restrictions
				optionalRestriction = optionalRestriction.clone();
				optionalRestriction.setDoOutput(true);
				addRestriction(optionalRestriction);
			}
		}
		for (OptionalRestriction optionalRestriction : theirOptionalRestrictions) {
			optionalRestriction = optionalRestriction.clone();
			optionalRestriction.setDoOutput(true);
			addRestriction(optionalRestriction);
		}
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
	public SPARQLWhere clone() {
		SPARQLWhere returnValue;
		
		returnValue = new SPARQLWhere();
		for (Restriction restriction : restrictions) {
			returnValue.addRestriction(restriction.clone());
		}
		return returnValue;
	}

	/**
	 * The <code>addRestriction</code> method adds a new restriction to this object.
	 *
	 * @param restriction is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 */
	public void addRestriction(Restriction restriction) {
		restrictions.add(restriction);
	}

	/**
	 * The <code>deleteAllRestrictions</code> method clears all of the restrictions in this WHERE block.
	 */
	public void deleteAllRestrictions() {
		restrictions = new LinkedHashSet<Restriction>();
	}
	
	
	/**
	 * The <code>getRestrictions</code> method returns a list with all of the restrictions in this 
	 * WHERE clause.
	 *
	 * @return a list of restrictions, each of which is a string.
	 */
	public LinkedHashSet<Restriction> getRestrictions() {		
		return restrictions;
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

		returnValue = new StringBuffer("\n" + tab + "WHERE {");
		for (Restriction restriction : restrictions) {
			returnValue.append(restriction.toString("    " + tab,owner));
		}
		returnValue.append("\n" + tab + "}");
		return returnValue.toString();
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
		return this.toString("","");
	}
}