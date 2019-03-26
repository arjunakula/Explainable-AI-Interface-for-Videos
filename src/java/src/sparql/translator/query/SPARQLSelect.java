package sparql.translator.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import sparql.translator.utilities.Global;

/**
 * The <code>SPARQLSelect</code> holds the information in the SELECT clause of a SPARQL query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 1, 2013
 * @since 1.6
 */
public class SPARQLSelect implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>variables</code> is a list of the names of the variables that should be included in the output
	 * when the query is processed. 
	 */
	private ArrayList<String> variables;
	
	/**
	 *  <code>formulas</code> is a hash in which each variable name that is the result of a formula points to
	 * its formula. 
	 */
	private HashMap<String,String> formulas;
	
	/** 
	 * <code>distinct</code> specifies whether duplicate tuples of the values of the variables and formulas 
	 * are deleted. It is <b><code>false</code></b> unless specified otherwise (with the 
	 * <code>distinct()</code> method).
	 */
	private Boolean isDistinct;
	
	/**
	 * The <code>SPARQLSelect</code> constructor initializes this class's fields.
	 *
	 */
	public SPARQLSelect() {
		variables = new ArrayList<String>();
		formulas = new HashMap<String,String>();
		isDistinct = false;
	}
	
	/**
	 * The <code>hasFormula</code> method reports whether or not there is a formula in this select block.
	 *
	 * @return <b><code>true</code></b> if there is a formula in this select block and 
	 * <b><code>false</code></b> otherwise.
	 */
	public Boolean hasFormula() {
		return (formulas.size() > 0);
	}
	
	/**
	 * The <code>distinct</code> method specifies that the DISTINCT restriction should be included.
	 */ 
	public void distinct() {
		isDistinct = true;
	}

	/**
	 * The <code>addVariable</code> method adds a new variable to this object.
	 *
	 * @param variable is the name of the variable to include in the output of the query.
	 */ 
	public void addVariable(String variable) {
		variables.add(variable);
	}

	/**
	 * The <code>addFormula</code> method adds a new formula to this object.
	 *
	 * @param formula is the formula to compute and include in the output of the query.
	 * @param variable is the name to use in the header of the column with the formula's values.
	 */
	public void addFormula(String formula, String variable) {
		formulas.put(variable,formula);
	}

	/**
	 * The <code>negateOperator</code> method negates the formula in this SELECT block. So, for example, 
	 * "COUNT(*) &gt; 0" becomes "COUNT(*) &lt;= 0".
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void negateOperator(String owner) {
		String formula;				//One of the formulas in this SELECT block
		String[] formulaParts;		//The formula with its parts separated into elements of this array
		String formulaPart;			//One of those parts
		StringBuffer newFormula;	//The formula with its operators reversed
		String space;				//Specifies whether a space should be printed
		int index;					//For looping through the array
		
		space = "";					//Initialize
		for (String variable : formulas.keySet()) {
			formula = formulas.get(variable);
			formulaParts = formula.split(" ");
			newFormula = new StringBuffer();
			for (index = 0; index < formulaParts.length; index++) {
				formulaPart = formulaParts[index];
				newFormula.append(space);
				space = " ";
				if (Global.NUMERICAL_COMPARISON_OPERATORS.values().contains(formulaPart)) { //An operator?
					newFormula.append(negateOperator(formulaPart,owner));
				} else {
					newFormula.append(formulaPart);
				}
			}
			formulas.put(variable,newFormula.toString());
		}
		
		
	}

	/**
	 * The <code>negateOperator</code> method negates an operator. For example, it changes "&gt;" to "&lt;=".
	 *
	 * @param operator is a numeric operator in SPARQL form.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * was found.
	 * @return the opposite of the given operator.
	 */
	public static String negateOperator(String operator, String owner) {
		String warning;							//A message to let the user know there might be a problem

		switch (operator) {
			case "=": return "!=";
			case "<": return ">=";
			case ">": return "<=";
			case "<=": return ">";
			case ">=": return "<";
			case "!=": return "=";
			default:
				warning = 
						"WARNING in SPARQLSelect.negateOperator in " + owner + 
						": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
		}
		return operator;
	}
		
	/**
	 * The <code>isDistinct</code> getter returns the value of the global variable,
	 * <code>isDistinct</code>, a <b><code>Boolean</code></b>.
	 *
	 * @return the value of <code>isDistinct</code>.
	 */
	public Boolean isDistinct() {
		return isDistinct;
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
		StringBuffer returnValue;
		
		returnValue = new StringBuffer(tab + "SELECT");
		if (isDistinct) {
			returnValue.append(" DISTINCT");
		}
		if ((variables.size() == 0) && (formulas.size() == 0)) {
			returnValue.append(" *");
		} else {
			for (String variable : variables) {
				returnValue.append(" " + variable);
			}
			for (String variable : formulas.keySet()) {
				returnValue.append(" ((" + formulas.get(variable) + ") AS " + variable + ")");
			}
		}
		return returnValue.toString();
	}	
	
	/**
	 * The <code>toString</code> method returns this SELECT statement printed in a pretty format.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.toString("");
	}
}