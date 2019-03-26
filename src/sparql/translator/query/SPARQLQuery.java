package sparql.translator.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import sparql.translator.definitions.SetDefinition;
import sparql.translator.recordCardinality.RecordCardinalityArguments;
import sparql.translator.restrictions.OptionalRestriction;
import sparql.translator.restrictions.QueryRestriction;
import sparql.translator.restrictions.Restriction;
import sparql.translator.restrictions.StringRestriction;
import sparql.translator.restrictions.UnionRestriction;
import sparql.translator.utilities.Global;

/**
 * The <code>SPARQLQuery</code> class holds the information in a SPARQL query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 1, 2013
 * @since 1.6
 */
public class SPARQLQuery implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;
	
	/** <code>prefixes</code> holds all of the PREFIX definitions in the query. */
	private SPARQLPrefixes prefixes;
	
	/** <code>select</code> holds all of the information in the SELECT clause of the query. */
	private SPARQLSelect select;

	/** <code>where</code> holds all of the restrictions in the WHERE clause of the query. */
	private SPARQLWhere where;

	/**
	 * The <code>SPARQLQuery</code> constructor initializes all of the fields.
	 */
	public SPARQLQuery() {
		this(new LinkedHashSet<Restriction>());
	}

	/**
	 * The <code>SPARQLQuery</code> constructor initializes all of the fields.
	 * 
	 * @param restrictions are the restrictions to put in this object.
	 */
	public SPARQLQuery(LinkedHashSet<Restriction> restrictions) {
		prefixes = new SPARQLPrefixes();
		select = new SPARQLSelect();
		where = new SPARQLWhere(restrictions);
	}

	/**
	 * The <code>isEmpty</code> method reports whether or not there is anything, other than the prefixes, 
	 * in this query.
	 *
	 * @return <b><code>true</code></b> if there is nothing but prefixes in this query and 
	 * <b><code>false</code></b> otherwise.
	 */
	public Boolean isEmpty() {
		if (select.hasFormula()) {
			return false;			
		}
		if (where.hasRestrictions()) {
			return false;			
		}
		return true;
	}

	/**
	 * The <code>addPrefixes</code> method adds the "PREFIX" lines to this SPARQL query.
	 */
	public void addPrefixes() {
		for (String abbreviation : Global.SPARQL_PREFIXES.keySet()) {
			addPrefix(abbreviation,Global.SPARQL_PREFIXES.get(abbreviation));
		}
	}

	/**
	 * The <code>addPrefix</code> method adds a new prefix line to this query.
	 *
	 * @param abbreviation is the abbreviated form, which can be used in the query.
	 * @param expansion is the expanded form that specifies what the prefix means.
	 */
	private void addPrefix(String abbreviation, String expansion) {
		prefixes.add(abbreviation,expansion);
	}
	
	/**
	 * The <code>addSelectVariable</code> method adds a new variable whose values should be output when
	 * the query is processed.
	 *
	 * @param variable is the name of the variable to include in the output.
	 */
	public void addSelectVariable(String variable) {
		select.addVariable(variable);
	}
	
	/**
	 * The <code>addSelectFormula</code> method adds a new formula whose values should be output when the
	 * query is processed.
	 *
	 * @param formula is the formula to compute and include in the output.
	 * @param variable is the name to use in the header of the column with the formula's values.
	 */
	public void addSelectFormula(String formula, String variable) {
		select.addFormula(formula,variable);
	}
	
	/**
	 * The <code>distinct</code> method specifies that the DISTINCT restriction should be included in the
	 * SELECT line of this query.
	 */ 
	public void distinct() {
		select.distinct();
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this SPARQL
	 * query's WHERE clause with another variable. WARNING: The variables in the SELECT statement are not
	 * replaced.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		where.replaceVariable(oldVariable,newVariable,owner);
	}

	/**
	 * The <code>negateOperator</code> method negates the SELECT part of this query.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void negateOperator(String owner) {
		select.negateOperator(owner);
	}
	
	/**
	 * The <code>sortQuery</code> method moves all of the optional restrictions in this query to the end of 
	 * the WHERE block in this query.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void sortQuery(String owner) {
		where.sortQuery(owner);
	}
	
	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this SPARQL
	 * query to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>SPARQLQuery</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	public SPARQLQuery reorderRestrictions(String owner) {
		SPARQLWhere reorderedWhere;			//With the restrictions reordered
		SPARQLQuery returnValue;
		
		reorderedWhere = where.reorderRestrictions(owner);
		returnValue = new SPARQLQuery();
		returnValue.setPrefixes(prefixes);
		returnValue.setSelect(select);
		returnValue.setWhere(reorderedWhere);
		return returnValue;
	}

	/**
	 * The <code>extractOptionalRestrictions</code> method searches recursively through this query, 
	 * removing all of the optional restrictions and returning them in a list.
	 *
	 * @param isPositive is <b><code>false</code></b> if this restriction is found within an
	 * odd number of &lt;not&gt;s, and <b><code>true</code></b> otherwise.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a list of all the optional restrictions that have been removed from this restriction.
	 */
	public ArrayList<OptionalRestriction> extractOptionalRestrictions(Boolean isPositive, String owner) {	
		return where.extractOptionalRestrictions(isPositive,owner);
	}

	/**
	 * The <code>copy</code> method makes this query a duplicate of the given query, except for the prefixes. 
	 * WARNING: Only the pointers are copied, so any change in one of the queries causes the other one to 
	 * change in the same way.
	 *
	 * @param otherQuery is the query to make this query identical to.
	 */
	public void copy(SPARQLQuery otherQuery) {
		select = otherQuery.getSelect();
		where = otherQuery.getWhere();
	}

	/**
	 * The <code>addRestriction</code> method adds a new restriction to be enforced on the output of
	 * the query.
	 *
	 * @param restriction is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 */
	public void addRestriction(Restriction restriction) {
		where.addRestriction(restriction);
	}

	/**
	 * The <code>addRestriction</code> method adds a new restriction to be enforced on the output of
	 * the query.
	 *
	 * @param restriction is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 * @param isPredicateFilterFunctionRestriction is <b><code>true</code></b> if and only if the given
	 * restriction is a predicate filter function restriction.
	 */
	public void addRestriction(String restriction, Boolean isPredicateFilterFunctionRestriction) {
		StringRestriction newRestriction;
		
		newRestriction = new StringRestriction(restriction);
		newRestriction.setHasPFFRestriction(isPredicateFilterFunctionRestriction);
		where.addRestriction(newRestriction);
	}
	
	/**
	 * The <code>addRestriction</code> method adds a new restriction to be enforced on the output of
	 * the query.
	 *
	 * @param restriction is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 */
	public void addRestriction(UnionRestriction restriction) {
		where.addRestriction(restriction);
	}
	
	/**
	 * The <code>addRestrictions</code> method adds a list of new restrictions to be enforced on the output of
	 * the query.
	 *
	 * @param restrictions is a list of conditions that must be satisfied for a row to be output when the 
	 * query is processed.
	 */
	public void addRestrictions(LinkedHashSet<Restriction> restrictions) { 
		for (Restriction restriction : restrictions) {			
			addRestriction(restriction);
		}
	}

	/**
	 * The <code>addRestrictions</code> method copies the restrictions in the given query to this query.
	 *
	 * @param otherQuery is the query whose restrictions will be added to this query.
	 */
	public void addRestrictions(SPARQLQuery otherQuery) { 
		addRestrictions(otherQuery.getRestrictions());
	}

	/**
	 * The <code>addRecordCardinalityRestriction</code> method creates a "RecordCardinality" filter function 
	 * that includes all of the relevant information in the given set definition. The new filter function
	 * is added to this query as a restriction.
	 *
	 * @param set is the definition of the set whose cardinality should be recorded.
	 */
	public void addRecordCardinalityRestriction(SetDefinition set) {
		RecordCardinalityArguments arguments;
		StringBuffer restriction;
				
		arguments = set.getRecordCardinalityArguments();
		restriction = new StringBuffer();
		restriction.append("FILTER (fn:RecordCardinality(\"SET_NAME\",\"" + set.getId() + "\"");
		if (arguments != null) {//If there are no arguments, then the set is true at all times
			restriction.append("," + arguments.toString());
		}
		restriction.append(")) .");
		addRestriction(restriction.toString(),false);
	}
	
	/**
	 * The <code>addRecordCardinalityRestriction</code> method creates a "RecordCardinality" filter function 
	 * that includes all of the relevant information in the given set definition. The new filter function
	 * is added to this query as a restriction.
	 *
	 * @param id is the name of the set for recording the cardinality information.
	 * @param set is the definition of the set whose cardinality should be recorded.
	 */
	public void addRecordCardinalityRestriction(String id, SetDefinition set) {
		RecordCardinalityArguments arguments;
			
		arguments = set.getRecordCardinalityArguments();
		if (arguments != null) {
			addRestriction(
					"FILTER (fn:RecordCardinality(\"SET_NAME\",\"" + id + "\"," + 
					arguments.toString() + ")) .",
					false);
		}
	}
	
	/**
	 * The <code>addSubselect</code> method adds a new query that serves as a restriction to be enforced on
	 * the output of this query.
	 *
	 * @param subselect is a query with no prefixes.
	 */
	public void addSubselect(SPARQLQuery subselect) {
		where.addRestriction(new QueryRestriction(subselect,false));
	}
	
	/**
	 * The <code>deleteAllRestrictions</code> method clears all of the restrictions in this query.
	 */
	public void deleteAllRestrictions() {
		where.deleteAllRestrictions();
	}
	
	/**
	 * The <code>setPrefixes</code> setter changes the value of the global variable,
	 * <code>prefixes</code>, a <b><code>SPARQLPrefixes</code></b>.
	 *
	 * @param prefixesIn is the new value that should be assigned to <code>prefixes</code>.
	 */
	public void setPrefixes(SPARQLPrefixes prefixesIn) {
		prefixes = prefixesIn;
	}

	/**
	 * The <code>setSelect</code> setter changes the value of the global variable,
	 * <code>select</code>, a <b><code>SPARQLSelect</code></b>.
	 *
	 * @param selectIn is the new value that should be assigned to <code>select</code>.
	 */
	public void setSelect(SPARQLSelect selectIn) {
		select = selectIn;
	}

	/**
	 * The <code>setWhere</code> setter changes the value of the global variable,
	 * <code>where</code>, a <b><code>SPARQLWhere</code></b>.
	 *
	 * @param whereIn is the new value that should be assigned to <code>where</code>.
	 */
	public void setWhere(SPARQLWhere whereIn) {
		where = whereIn;
	}
	
	/**
	 * The <code>isDistinct</code> getter returns the value of the variable in the <code>SPARQLSelect</code>,
	 * <code>isDistinct</code>, a <b><code>Boolean</code></b>.
	 *
	 * @return the value of <code>isDistinct</code>.
	 */
	public Boolean isDistinct() {
		return select.isDistinct();
	}

	/**
	 * The <code>getPrefixes</code> getter returns the value of the global variable,
	 * <code>prefixes</code>, a <b><code>SPARQLPrefixes</code></b>.
	 *
	 * @return the value of <code>prefixes</code>.
	 */
	public SPARQLPrefixes getPrefixes() {
		return prefixes;
	}

	/**
	 * The <code>getSelect</code> getter returns the value of the global variable,
	 * <code>select</code>, a <b><code>SPARQLSelect</code></b>.
	 *
	 * @return the value of <code>select</code>.
	 */
	public SPARQLSelect getSelect() {
		return select;
	}

	/**
	 * The <code>getWhere</code> getter returns the value of the global variable,
	 * <code>where</code>, a <b><code>SPARQLWhere</code></b>.
	 *
	 * @return the value of <code>where</code>.
	 */
	public SPARQLWhere getWhere() {
		return where;
	}

	/**
	 * The <code>getRestrictions</code> method returns a list with all of the restrictions in this query's 
	 * WHERE clause.
	 *
	 * @return a list of restrictions, each of which is a string.
	 */
	public LinkedHashSet<Restriction> getRestrictions() {		
		return where.getRestrictions();
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
		return 
				prefixes.toString(tab) + 
				select.toString(tab) + 
				where.toString(tab,owner);
	}
	
	/**
	 * The <code>toString</code> method returns this query printed in a pretty format.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.toString("","");
	}
}