package sparql.translator.utilities;

import sparql.translator.query.SPARQLQuery;
import sparql.translator.recordCardinality.RecordCardinalityArguments;

/**
 * The <code>TranslateConditionReturnValue</code> class holds all of the information that needs to be
 * returned by the <code>translateCondition()</code> method in the <code>TranslateCondition</code> class.
 *
 * @author Ken Samuel
 * @version 1.0, Sep 4, 2014
 * @since 1.6
 */
public class TranslateConditionReturnValue {

	/** <code>sPARQLQuery</code> is the query that was constructed by the 
	 * <b><code>translateCondition()</code></b> method. 
	 */
	SPARQLQuery sPARQLQuery;
	
	/** <code>recordCardinalityArguments</code> is a list of arguments to be used in a "RecordCardinality" 
	 * filter function if it is necessary to count the number of elements in a set that has 
	 * <b><code>sPARQLQuery</code></b>'s WHERE clause.
	 */
	RecordCardinalityArguments recordCardinalityArguments;
	
	/**
	 * The <code>TranslateConditionReturnValue</code> constructor initializes this class's fields.
	 */
	public TranslateConditionReturnValue() {
		sPARQLQuery = new SPARQLQuery();
		recordCardinalityArguments = new RecordCardinalityArguments();
	}

	/**
	 * The <code>setSPARQLQuery</code> setter changes the value of the global variable,
	 * <code>sPARQLQuery</code>, a <b><code>SPARQLQuery</code></b>.
	 *
	 * @param sPARQLQueryIn is the new value that should be assigned to <code>sPARQLQuery</code>.
	 */
	public void setSPARQLQuery(SPARQLQuery sPARQLQueryIn) {
		sPARQLQuery = sPARQLQueryIn;
	}

	/**
	 * The <code>setRecordCardinalityArguments</code> setter changes the value of the global variable,
	 * <code>recordCardinalityArguments</code>, a <b><code>RecordCardinalityArguments</code></b>.
	 *
	 * @param recordCardinalityArgumentsIn is the new value that should be assigned to 
	 * <code>recordCardinalityArguments</code>.
	 */
	public void setRecordCardinalityArguments(RecordCardinalityArguments recordCardinalityArgumentsIn) {
		recordCardinalityArguments = recordCardinalityArgumentsIn;
	}

	/**
	 * The <code>getSPARQLQuery</code> getter returns the value of the global variable,
	 * <code>sPARQLQuery</code>, a <b><code>SPARQLQuery</code></b>.
	 *
	 * @return the value of <code>sPARQLQuery</code>.
	 */
	public SPARQLQuery getSPARQLQuery() {
		return sPARQLQuery;
	}

	/**
	 * The <code>getRecordCardinalityArguments</code> getter returns the value of the global variable,
	 * <code>recordCardinalityArguments</code>, a <b><code>RecordCardinalityArguments</code></b>.
	 *
	 * @return the value of <code>recordCardinalityArguments</code>.
	 */
	public RecordCardinalityArguments getRecordCardinalityArguments() {
		return recordCardinalityArguments;
	}
}