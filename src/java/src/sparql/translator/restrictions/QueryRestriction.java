package sparql.translator.restrictions;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import sparql.translator.query.SPARQLQuery;
import sparql.translator.utilities.Global;



/**
 * The <code>QueryRestriction</code> class holds a query that can be a subselect restriction in a SPARQL
 * query's WHERE clause.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 22, 2013
 * @since 1.6
 */
public class QueryRestriction extends Restriction {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>restriction</code> is the <code>SPARQLQuery</code> form of this restriction. 
	 */
	private SPARQLQuery restriction;

	
	/**
	 * The <code>QueryRestriction</code> constructor initializes the global variables.
	 *
	 * @param restrictionIn is a query to put in this object.
	 * @param isDistinct specifies whether the variables in the SELECT of the query in this object should be 
	 * marked as DISTINCT.
	 */
	public QueryRestriction(SPARQLQuery restrictionIn, Boolean isDistinct) {
		super();
		restriction = restrictionIn;
		if (isDistinct == true) {
			restriction.distinct();
		}
	}
	
	/**
	 * The <code>QueryRestriction</code> constructor initializes the global variables.
	 *
	 * @param restrictions is a list of restrictions to put in this object.
	 * @param isDistinct specifies whether the variables in the SELECT of the query in this object should be 
	 * marked as DISTINCT.
	 */
	public QueryRestriction(LinkedHashSet<Restriction> restrictions, Boolean isDistinct) {
		this(new SPARQLQuery(restrictions),isDistinct);
	}
	
	/**
	 * The <code>QueryRestriction</code> constructor initializes the global variables.
	 *
	 * @param aRestriction is a single restriction, other than a QueryRestriction, to put in this object.
	 * @param isDistinct specifies whether the variables in the SELECT of the query in this object should be 
	 * marked as DISTINCT.
	 */
	public QueryRestriction(Restriction aRestriction, Boolean isDistinct) {
		super();
		restriction = new SPARQLQuery(Global.getOneRestrictionList(aRestriction));
		if (isDistinct == true) {
			restriction.distinct();
		}
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
			String oldVariable, String newVariable, String owner) 
	{
		restriction.replaceVariable(oldVariable,newVariable,owner);
	}

	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this object
	 * to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>QueryRestriction</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	@Override
	public QueryRestriction reorderRestrictions(String owner) {
		QueryRestriction returnValue;
		
		returnValue = new QueryRestriction(restriction.reorderRestrictions(owner),restriction.isDistinct());
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
			Boolean isPositive, String owner) {	
		return restriction.extractOptionalRestrictions(isPositive,owner);
	}

	/**
	 * The <code>addRestriction</code> method adds a new restriction into this object's 
	 * <code>SPARQLQuery</code>.
	 *
	 * @param newRestriction is a condition to be added to <code>restriction</code>.
	 */
	public void addRestriction(Restriction newRestriction) {
		restriction.addRestriction(newRestriction);
	}

	/**
	 * The <code>addRestrictions</code> method adds new restrictions into this object's 
	 * <code>SPARQLQuery</code>.
	 *
	 * @param newRestrictions is a list of conditions to be added to <code>restriction</code>.
	 */
	public void addRestrictions(LinkedHashSet<Restriction> newRestrictions) {
		for (Restriction newRestriction : newRestrictions) {
			addRestriction(newRestriction);
		}
	}

	/**
	 * The <code>setRestriction</code> setter changes the value of the global variable,
	 * <code>restriction</code>, a <b><code>SPARQLQuery</code></b>.
	 *
	 * @param restrictionIn is the new value that should be assigned to <code>restriction</code>.
	 */
	public void setRestriction(SPARQLQuery restrictionIn) {
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
	public String getString(String tab, String owner) {
		StringBuffer returnValue;

		returnValue = new StringBuffer();
		returnValue.append("\n" + tab + "{");
		returnValue.append("\n" + restriction.toString("    " + tab,owner));
		returnValue.append("\n" + tab + "}");
		return returnValue.toString();
	}
}