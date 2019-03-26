package sparql.translator.recordCardinality;

import java.util.ArrayList;

/**
 * The <code>RecordCardinalityLogicalOperatorArgument</code> represents an argument for the 
 * "RecordCardinality" filter function that specifies a "not", "or", or "and" expression. "not has one 
 * operand of its own, while "or" and "and" each have two operands. For example: 
 * <code>"LOGICAL_OPERATOR","or","EVENT_ID","event_move1","OBJECT_ID","obj_dog1",
 * "FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityLogicalOperatorArgument extends RecordCardinalityArgument {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>operatorType</code> is the type of logical operator in this object. There are three 
	 * possible values: "not", "or", and "and".
	 */
	private String operatorType;
	
	/** <code>operands</code> is one (for "not") or two (for "or" and "and") operands. */
	private ArrayList<RecordCardinalityArgument> operands;

	
	/**
	 * The <code>RecordCardinalityLogicalOperatorArgument</code> constructor initializes the global variables.
	 * 
	 * @param type is the type of logical operator in this argument.
	 */
	public RecordCardinalityLogicalOperatorArgument(String type) {
		super("LOGICAL_OPERATOR");
		operatorType = type;
		operands = new ArrayList<RecordCardinalityArgument>(2);
	}
	
	/**
	 * The <code>RecordCardinalityLogicalOperatorArgument</code> constructor initializes the global variables.
	 * 
	 * @param type is the type of logical operator in this argument.
	 * @param operand is the only operand of this logical operator.
	 */
	public RecordCardinalityLogicalOperatorArgument(String type, RecordCardinalityArgument operand) {
		this(type);
		addOperand(operand);
	}
	
	/**
	 * The <code>RecordCardinalityLogicalOperatorArgument</code> constructor initializes the global variables.
	 * 
	 * @param type is the type of logical operator in this argument.
	 * @param operand1 is the first operand of this logical operator.
	 * @param operand2 is the second operand of this logical operator.
	 */
	public RecordCardinalityLogicalOperatorArgument(
			String type, RecordCardinalityArgument operand1, RecordCardinalityArgument operand2) {
		this(type);
		addOperand(operand1);
		addOperand(operand2);
	}
	
	/**
	 * The <code>addOperand</code> method adds a new operand (an object, event, or function) to this object.
	 *
	 * @param operand is an operand of this logical operator expression.
	 */
	public void addOperand(RecordCardinalityArgument operand) {
		operands.add(operand);
	}

	/**
	 * The <code>toString</code> method returns the value of this argument in the form of a string. Note that
	 * the argument might have arguments of its own, such as 
	 * <code>"LOGICAL_OPERATOR","or","EVENT_ID","event_move1","OBJECT_ID","obj_dog1",
	 * "FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>./code>.
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
		for (RecordCardinalityArgument operand : operands) {
			returnValue.append("," + operand);
		}
		return returnValue.toString();
	}
}