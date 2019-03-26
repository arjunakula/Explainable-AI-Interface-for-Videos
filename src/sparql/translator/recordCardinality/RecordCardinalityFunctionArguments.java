package sparql.translator.recordCardinality;

import java.util.ArrayList;

/**
 * The <code>RecordCardinalityFunctionArguments</code> class represents a list of arguments for the 
 * "RecordCardinality" filter function that specifies another filter function along with its arguments, such 
 * as <code>"FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityFunctionArguments extends RecordCardinalityArguments {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>functionId</code> is the name of this function, such as "IsMoving". */
	private String functionId;
	
	/** 
	 * <code>functionArguments</code> is a list of the arguments of the function. Depending on the function, 
	 * there will be one, two, or three arguments.
	 */
	private ArrayList<RecordCardinalityArguments> functionArguments;

	
	/**
	 * The <code>RecordCardinalityFunctionArguments</code> constructor initializes the global variables.
	 * 
	 * @param value is the value of the second argument, which is the name of the function.
	 */
	public RecordCardinalityFunctionArguments(String value) {
		super("FUNCTION");
		functionId = value;
		functionArguments = new ArrayList<RecordCardinalityArguments>(3);
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
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		for (RecordCardinalityArguments argument : functionArguments) {
			argument.replaceVariable(oldVariable,newVariable,owner);
		}
	}
	
	/**
	 * The <code>addArgument</code> method adds a new argument (an object or color) to this event argument.
	 *
	 * @param argument is an object or color that is an argument of this event.
	 */
	public void addArgument(RecordCardinalityArguments argument) {
		functionArguments.add(argument);
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
		if (timePeriod != null) {
			returnValue.append(timePeriod + ",");
		}
		if (location != null) {
			returnValue.append(location + ",");
		}
		returnValue.append("\"" + argumentType + "\",");
		returnValue.append("\"" + functionId + "\"");
		for (RecordCardinalityArguments functionArgument : functionArguments) {
			returnValue.append("," + functionArgument);
		}
		return returnValue.toString();
	}
}