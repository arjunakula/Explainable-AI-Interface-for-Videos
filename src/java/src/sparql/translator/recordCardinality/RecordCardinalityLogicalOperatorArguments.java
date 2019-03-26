package sparql.translator.recordCardinality;

import java.util.ArrayList;

/**
 * The <code>RecordCardinalityLogicalOperatorArguments</code> represents a list of arguments for the 
 * "RecordCardinality" filter function that specifies a "not", "or", or "and" expression. "not has one 
 * operand of its own, while "or" and "and" each have two operands. For example: 
 * <code>"LOGICAL_OPERATOR","or","EVENT_ID","event_move1","OBJECT_ID","obj_dog1",
 * "FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityLogicalOperatorArguments extends RecordCardinalityArguments {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>operatorType</code> is the type of logical operator in this object. There are three 
	 * possible values: "not", "or", and "and".
	 */
	private String operatorType;
	
	/** <code>operands</code> is one (for "not") or two (for "or" and "and") operands. */
	private ArrayList<RecordCardinalityArguments> operands;

	
	/**
	 * The <code>RecordCardinalityLogicalOperatorArguments</code> constructor initializes the global 
	 * variables.
	 * 
	 * @param type is the type of logical operator, which is the second argument in this argument list.
	 */
	public RecordCardinalityLogicalOperatorArguments(String type) {
		super("LOGICAL_OPERATOR");
		operatorType = type;
		operands = new ArrayList<RecordCardinalityArguments>(2);
	}
	
	/**
	 * The <code>RecordCardinalityLogicalOperatorArguments</code> constructor initializes the global 
	 * variables.
	 * 
	 * @param type is the type of logical operator, which is the second argument in this argument list.
	 * @param operand is the only operand of this logical operator.
	 */
	public RecordCardinalityLogicalOperatorArguments(String type, RecordCardinalityArguments operand) {
		this(type);
		addOperand(operand);
	}
	
	/**
	 * The <code>RecordCardinalityLogicalOperatorArguments</code> constructor initializes the global 
	 * variables.
	 * 
	 * @param type is the type of logical operator, which is the second argument in this argument list.
	 * @param operand1 is the first operand of the logical operator.
	 * @param operand2 is the second operand of the logical operator.
	 */
	public RecordCardinalityLogicalOperatorArguments(
			String type, RecordCardinalityArguments operand1, RecordCardinalityArguments operand2) {
		this(type);
		addOperand(operand1);
		addOperand(operand2);
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
		for (RecordCardinalityArguments operand : operands) {
			operand.replaceVariable(oldVariable,newVariable,owner);
		}
	}
	
	/**
	 * The <code>addOperand</code> method adds a new operand (an object, event, or function) to this object.
	 *
	 * @param operand is an operand of this logical operator expression.
	 */
	public void addOperand(RecordCardinalityArguments operand) {
		operands.add(operand);
	}

	/**
	 * The <code>toString</code> method returns the value of this argument list in the form of a string. 
	 *
	 * @return a string representation of this object's value.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();
		returnValue.append("\"" + argumentType + "\",");
		returnValue.append("\"" + operatorType + "\"");
		for (RecordCardinalityArguments operand : operands) {
			returnValue.append("," + operand);
		}
		return returnValue.toString();
	}
}