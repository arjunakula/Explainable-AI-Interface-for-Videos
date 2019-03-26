package sparql.translator.recordCardinality;

import sparql.translator.utilities.Global;


/**
 * The <code>RecordCardinalityObjectArguments</code>class represents a list of arguments for the 
 * "RecordCardinality" filter function that specifies an object, such as <code>"obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityObjectArguments extends RecordCardinalityArguments {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>objectId</code> is the value of this object, such as "obj_dog1". */
	private String objectId;

	
	/**
	 * The <code>RecordCardinalityObjectArguments</code> constructor initializes the global variables.
	 * 
	 * @param value is the second argument, which is the name of the object.
	 */
	public RecordCardinalityObjectArguments(String value) {
		super("OBJECT_ID");
		if (value != null) {
			objectId = Global.fixDatabaseIdentifier(value);		//TODO: Make sure identifiers are spelled correctly: person-1 or data:person-1 or #person-1
		} else {
			objectId = "\"error\"";
		}
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
	public void replaceVariable(String oldVariable, String newVariable, @SuppressWarnings("unused") String owner) {
		if (objectId.equals(oldVariable)) {
			objectId = newVariable;
		}
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
		returnValue.append(objectId);
		return returnValue.toString();
	}
}