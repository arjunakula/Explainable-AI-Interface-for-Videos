package sparql.translator.recordCardinality;

/**
 * The <code>RecordCardinalityColorArguments</code>class represents a list of arguments for the 
 * "RecordCardinality" filter function that specifies a color, such as <code>"red"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityColorArguments extends RecordCardinalityArguments {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>colorId</code> is the value of this color, such as "red". */
	private String colorId;
	
	
	/**
	 * The <code>RecordCardinalityColorArguments</code> constructor initializes the global variables.
	 * 
	 * @param value is the color represented by this argument.
	 */
	public RecordCardinalityColorArguments(String value) {
		super("COLOR");
		colorId = value;
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this list of  
	 * arguments with another variable.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@Override
	@SuppressWarnings("unused")
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		return;			//There are no variables in this object
	}
	
	/**
	 * The <code>toString</code> method returns the value of this list of arguments in the form of a string.
	 *
	 * @return a string representation of this object's value.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();
		returnValue.append("\"" + argumentType + "\",");
		returnValue.append(colorId);
		return returnValue.toString();
	}
}