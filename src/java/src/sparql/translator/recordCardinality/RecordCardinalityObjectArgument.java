package sparql.translator.recordCardinality;

import sparql.translator.utilities.Global;

/**
 * The <code>RecordCardinalityObjectArgument</code>class represents an argument for the "RecordCardinality" 
 * filter function that specifies an object, such as <code>"obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityObjectArgument extends RecordCardinalityArgument {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>objectId</code> is the value of this object, such as "obj_dog1". */
	private String objectId;

	
	/**
	 * The <code>RecordCardinalityObjectArgument</code> constructor initializes the global variables.
	 * 
	 * @param value is the value of this argument.
	 */
	public RecordCardinalityObjectArgument(String value) {
		super("OBJECT_ID");
		if (value != null) {
			objectId = Global.fixDatabaseIdentifier(value);
		} else {
			objectId = "\"error\"";
		}
	}
	
	/**
	 * The <code>toString</code> method returns the value of this argument in the form of a string. Note that
	 * the argument might have arguments of its own, such as 
	 * <code>"CARTESIAN_METRIC_POINT","0.0,8.2","OBJECT_ID","obj_dog1"</code>.
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
		returnValue.append(objectId);
		return returnValue.toString();
	}
}