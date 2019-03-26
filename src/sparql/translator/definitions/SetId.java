package sparql.translator.definitions;

import java.io.Serializable;

/**
 * The <code>SetId</code> class holds all of the information required to specify a particular set that may
 * be required as an argument in a call to the <code>IsTemporalRelationWithQuantities()</code> or 
 * <code>ComputeWhen</code> <code>FILTER</code> function.
 *
 * @author Ken Samuel
 * @version 1.0, Feb 24, 2014
 * @since 1.6
 */
public class SetId implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>setName</code> is the name of the set in the form used in calls to the 
	 * <code>RecordCardinality()</code> and <code>IsTemporalRelationWithQuantities()</code> filter functions. 
	 */
	private String setName;
	
	/** 
	 * <code>setOperator</code> specifies whether this set is the intersection or union of two sets. The 
	 * value of this variable may be "intersection", "union", or <b><code>null</code></b> for a simple set. 
	 */
	private String setOperator;
	
	/** 
	 * <code>setOperand1</code> is one of the sets that is joined by a set operator. If 
	 * <code>setOperator</code> is <b><code>null</code></b>, then this variable is not used. 
	 */
	private SetId setOperand1;

	/** 
	 * <code>setOperand2</code> is the other set that is joined by a set operator. If 
	 * <code>setOperator</code> is <b><code>null</code></b>, then this variable is not used. 
	 */
	private SetId setOperand2;

	/**
	 * The <code>SetId</code> constructor initializes the global variables of this class for a basic set 
	 * (not an intersection or union of sets).
	 *
	 * @param setNameIn is the name that will be used for this set. If the name is unknown, this parameter's 
	 * value should be <b><code>null</code></b>.
	 */
	public SetId(String setNameIn) {
		this(setNameIn,null,null,null);
	}

	/**
	 * The <code>SetId</code> constructor initializes the global variables of this class for a complex set 
	 * (either an intersection or union of sets).
	 *
	 * @param setNameIn is the name that will be used for this set. If the name is unknown, this parameter's 
	 * value should be <b><code>null</code></b>.
	 * @param setOperatorIn is the set operator, if any. Its value should be "intersection", "union", or 
	 * <b><code>null</code></b>.
	 */
	public SetId(String setNameIn, String setOperatorIn) {
		this(setNameIn,setOperatorIn,null,null);
	}

	/**
	 * The <code>SetId</code> constructor initializes the global variables of this class.
	 *
	 * @param setNameIn is the name that will be used for this set. If the name is unknown, this parameter's 
	 * value should be <b><code>null</code></b>.
	 * @param setOperatorIn is the set operator, if any. Its value should be "intersection", "union", or 
	 * <b><code>null</code></b>.
	 * @param setOperand1In is one of the sets joined by the set operator. If there is no set operator, then  
	 * this parameter should be <b><code>null</code></b>.
	 * @param setOperand2In is the other set that is joined by the set operator. If there is no set operator, 
	 * then this parameter should be <b><code>null</code></b>.
	 */
	public SetId(String setNameIn, String setOperatorIn, SetId setOperand1In, SetId setOperand2In) {
		setName = setNameIn;
		setOperator = setOperatorIn;
		setOperand1 = setOperand1In;
		setOperand2 = setOperand2In;
	}

	/**
	 * The <code>setName</code> setter changes the value of the global variable,
	 * <code>setName</code>, a <b><code>String</code></b>.
	 *
	 * @param setNameIn is the new value that should be assigned to <code>setName</code>.
	 */
	public void setName(String setNameIn) {
		setName = setNameIn;
	}

	/**
	 * The <code>getName</code> getter returns the value of the global variable,
	 * <code>setName</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>setName</code>.
	 */
	public String getName() {
		return setName;
	}

	/**
	 * The <code>getSetOperator</code> getter returns the value of the global variable,
	 * <code>setOperator</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>setOperator</code>.
	 */
	public String getSetOperator() {
		return setOperator;
	}

	/**
	 * The <code>getSetOperand1</code> getter returns the value of the global variable,
	 * <code>setOperand1</code>, a <b><code>SetId</code></b>.
	 *
	 * @return the value of <code>setOperand1</code>.
	 */
	public SetId getSetOperand1() {
		return setOperand1;
	}

	/**
	 * The <code>getSetOperand2</code> getter returns the value of the global variable,
	 * <code>setOperand2</code>, a <b><code>SetId</code></b>.
	 *
	 * @return the value of <code>setOperand2</code>.
	 */
	public SetId getSetOperand2() {
		return setOperand2;
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this definition.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}