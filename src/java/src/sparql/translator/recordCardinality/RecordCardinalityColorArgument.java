package sparql.translator.recordCardinality;

/**
 * The <code>RecordCardinalityColorArgument</code>class represents an argument for the "RecordCardinality" 
 * filter function that specifies a color, such as <code>"red"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityColorArgument extends RecordCardinalityArgument {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>colorId</code> is the value of this color, such as "red". */
	private String colorId;
	
	
	/**
	 * The <code>RecordCardinalityColorArgument</code> constructor initializes the global variables.
	 * 
	 * @param value is the value of this argument.
	 */
	public RecordCardinalityColorArgument(String value) {
		super("COLOR");
		colorId = value;
	}
	
	/**
	 * The <code>toString</code> method returns the value of this argument in the form of a string.
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