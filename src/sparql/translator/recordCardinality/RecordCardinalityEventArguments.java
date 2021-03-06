package sparql.translator.recordCardinality;

import java.util.ArrayList;

/**
 * The <code>RecordCardinalityEventArguments</code> class represents a list of arguments for the 
 * "RecordCardinality" filter function that specifies a behavior, relationship, or action along with its 
 * arguments, such as <code>"EVENT_ID","event_move1","OBJECT_ID","obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityEventArguments extends RecordCardinalityArguments {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>eventId</code> is the name of this event, such as "event_move1". */
	private String eventId;
	
	/** 
	 * <code>eventArguments</code> is a list of the arguments of the event. Depending on the event, there will
	 * be one, two, or three arguments.
	 */
	private ArrayList<RecordCardinalityObjectArguments> eventArguments;

	
	/**
	 * The <code>RecordCardinalityEventArguments</code> constructor initializes the global variables.
	 * 
	 * @param value is the value of this list of arguments.
	 */
	public RecordCardinalityEventArguments(String value) {
		super("EVENT_ID");
		eventId = value;
		eventArguments = new ArrayList<RecordCardinalityObjectArguments>(3);
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
		for (RecordCardinalityArguments argument : eventArguments) {
			argument.replaceVariable(oldVariable,newVariable,owner);
		}
	}
	
	/**
	 * The <code>addArgument</code> method adds a new argument (an object) to this event argument.
	 *
	 * @param argument is an object that is an argument of this event.
	 */
	public void addArgument(RecordCardinalityObjectArguments argument) {
		eventArguments.add(argument);
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
		returnValue.append("\"" + argumentType + "\"," + eventId);
		for (RecordCardinalityObjectArguments eventArgument : eventArguments) {
			returnValue.append("," + eventArgument);
		}
		return returnValue.toString();
	}
}