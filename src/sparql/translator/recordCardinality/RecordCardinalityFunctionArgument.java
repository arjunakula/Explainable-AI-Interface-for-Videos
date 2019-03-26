package sparql.translator.recordCardinality;

import java.util.ArrayList;

/**
 * The <code>RecordCardinalityFunctionArgument</code> class represents an argument for the "RecordCardinality" 
 * filter function that specifies another filter function along with its arguments, such as 
 * <code>"FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityFunctionArgument extends RecordCardinalityArgument {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>functionId</code> is the name of this function, such as "IsMoving". */
	private String functionId;
	
	/** 
	 * <code>functionArguments</code> is a list of the arguments of the function. Depending on the function, 
	 * there will be one, two, or three arguments.
	 */
	private ArrayList<RecordCardinalityArgument> functionArguments;

	
	/**
	 * The <code>RecordCardinalityFunctionArgument</code> constructor initializes the global variables.
	 * 
	 * @param value is the value of this argument.
	 */
	public RecordCardinalityFunctionArgument(String value) {
		super("FUNCTION");
		functionId = value;
		functionArguments = new ArrayList<RecordCardinalityArgument>(3);
	}
	
	/**
	 * The <code>addArgument</code> method adds a new argument (an object or color) to this event argument.
	 *
	 * @param argument is an object that is an argument of this event.
	 */
	public void addArgument(RecordCardinalityArgument argument) {
		functionArguments.add(argument);
	}
	
	/**
	 * The <code>toString</code> method returns the value of this argument in the form of a string. Note that
	 * the argument might have arguments of its own, such as 
	 * <code>"FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>.
	 *
	 * @return a string representation of this object's value.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();
		if (timePeriod != null) {
			returnValue.append(timePeriod + ",");
		}
		if (location != null) {
			returnValue.append(location + ",");
		}
		returnValue.append("\"" + argumentType + "\",");
		returnValue.append("\"" + functionId + "\"");
		for (RecordCardinalityArgument functionArgument : functionArguments) {
			returnValue.append("," + functionArgument);
		}
		return returnValue.toString();
	}
}