package sparql.translator.definitions;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.w3c.dom.Node;

import sparql.translator.query.SPARQLQuery;
import sparql.translator.recordCardinality.RecordCardinalityLogicalOperatorArguments;
import sparql.translator.restrictions.QueryRestriction;
import sparql.translator.restrictions.Restriction;
import sparql.translator.restrictions.UnionRestriction;
import sparql.translator.utilities.Global;

/**
 * The <code>SetDefinition</code> class holds important information about a set definition in an XML query.
 *
 * @author Ken Samuel
 * @version 1.0, Oct 25, 2013
 * @since 1.6
 */
public class SetDefinition extends Definition {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;


	/**
	 * The <code>SetDefinition</code> constructor initializes the global variables.
	 *
	 * @param setNode is the XML definition of the set.
	 */
	public SetDefinition(Node setNode) {
		super(setNode);
	}
	
	/**
	 * The <code>SetDefinition</code> constructor initializes the global variables.
	 * 
	 */
	public SetDefinition() {
		this(null);
	}

	/**
	 * The <code>combineSets</code> method transforms this set definition into the intersection or union of
	 * two other sets.
	 *
	 * @param operator specifies how the sets should be combined. Its value is "intersection" or "union".
	 * @param setId1 is the name of one of the sets to be combined.
	 * @param setId2 is the name of the other set to be combined.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void combineSets(String operator, String setId1, String setId2, String owner) {
		SetDefinition operand1,operand2;					//The two operands of the operator
		SPARQLQuery operand1SPARQL,operand2SPARQL;			//Their SPARQL queries
		ArrayList<String> boundVariables1,boundVariables2;	//Their bound variables
		SPARQLQuery subquery;								//One of the two subqueries for "union"
		LinkedHashSet<Restriction> subqueryList;			//A one-element list with one of the subqueries
		UnionRestriction unionRestriction;					//The restriction for "union"
		Integer count;										//For looping through the bound variables
		String warning;										//A message to let the user know there might be a problem
		
		operand1 = (SetDefinition)Global.getDefinition(setId1,owner);
		operand2 = (SetDefinition)Global.getDefinition(setId2,owner);
		boundVariables1 = operand1.getBoundVariables();
		boundVariables2 = operand2.getBoundVariables();
		if (boundVariables1.size() == boundVariables2.size()) {
			for (count = 0; count < boundVariables1.size(); count++) {
				operand2.replaceVariable(boundVariables2.get(count),boundVariables1.get(count),owner);
			}
			operand1SPARQL = operand1.getSPARQL();
			operand2SPARQL = operand2.getSPARQL();
			definitionInSPARQL = new SPARQLQuery();
			id = "set-(" + Global.stripPrefix(setId1,owner);
			if (operator == null) {
				warning = 
						"WARNING in SetDefinition.combineSets in " + owner + 
						": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			} else if (operator.equals("intersection")) {
				id += "-and-" + Global.stripPrefix(setId2,owner) + ")";
				addRestrictions(operand1SPARQL);
				addRestrictions(operand2SPARQL);
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"and",
						operand1.getRecordCardinalityArguments(),
						operand2.getRecordCardinalityArguments());
			} else if (operator.equals("union")) {
				id += "-or-" + Global.stripPrefix(setId2,owner) + ")";
				unionRestriction = new UnionRestriction();
				subquery = new SPARQLQuery();					//Create the first subquery
				subquery.addRestrictions(operand1SPARQL);
				subqueryList = new LinkedHashSet<Restriction>(1);
				subqueryList.add(new QueryRestriction(subquery,true));
				unionRestriction.addRestrictionsGroup(subqueryList);
				subquery = new SPARQLQuery();					//Create the second subquery
				subquery.addRestrictions(operand2SPARQL);
				subqueryList = new LinkedHashSet<Restriction>(1);
				subqueryList.add(new QueryRestriction(subquery,true));
				unionRestriction.addRestrictionsGroup(subqueryList);
				addRestriction(unionRestriction);
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",
						operand1.getRecordCardinalityArguments(),
						operand2.getRecordCardinalityArguments());
			} else {
				warning = 
						"WARNING in SetDefinition.combineSets in " + owner + 
						": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			definitionInSPARQL.distinct();
			Global.changedDefinition(this);
			addBoundVariables(operand1.getBoundVariables());
		} else {
			warning = 
					"WARNING in SetDefinition.combineSets in " + owner + ": Sets cannot be joined with " +
					"<" + operator + "> unless they have the same number of bound variables.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			id = "warning";
		}
	}
}