package sparql.translator.query;

import java.util.ArrayList;

/**
 * The <code>SPARQLFilters</code> holds the information in the FILTER restrictions of a SPARQL query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 1, 2013
 * @since 1.6
 */
public class SPARQLFilters {

	/** <code>filters</code> is a list of the filter restrictions of the query. */
	private ArrayList<String> filters;
	
	
	/**
	 * The <code>SPARQLFilters</code> constructor initializes this class's fields.
	 *
	 */
	public SPARQLFilters() {
		filters = new ArrayList<String>();
	}
	
	/**
	 * The <code>add</code> method adds a new restriction to this object.
	 *
	 * @param filter is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 */
	public void add(String filter) {
		filters.add(filter);
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

		returnValue = new StringBuffer();
		for (String filter : filters) {
			returnValue.append(tab + "FILTER " + filter + "\n");
		}
		return returnValue.toString();
	}
	
	/**
	 * The <code>toString</code> method returns these filters printed in a pretty format.
	 *
	 * @return a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.toString("");
	}
}