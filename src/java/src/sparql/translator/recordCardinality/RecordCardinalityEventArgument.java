package sparql.translator.recordCardinality;

import java.util.ArrayList;

/**
 * The <code>RecordCardinalityEventArgument</code> class represents an argument for the "RecordCardinality" 
 * filter function that specifies a behavior, relationship, or action along with its arguments, such as 
 * <code>"EVENT_ID","event_move1","OBJECT_ID","obj_dog1"</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityEventArgument extends RecordCardinalityArgument {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** <code>eventId</code> is the name of this event, such as "event_move1". */
	private String eventId;
	
	/** 
	 * <code>eventArguments</code> is a list of the arguments of the event. Depending on the event, there will
	 * be one, two, or three arguments.
	 */
	private ArrayList<RecordCardinalityObjectArgument> eventArguments;

	
	/**
	 * The <code>RecordCardinalityEventArgument</code> constructor initializes the global variables.
	 * 
	 * @param value is the value of this argument.
	 */
	public RecordCardinalityEventArgument(String value) {
		super("EVENT_ID");
		eventId = value;
		eventArguments = new ArrayList<RecordCardinalityObjectArgument>(3);
	}
	
	/**
	 * The <code>addArgument</code> method adds a new argument (an object) to this event argument.
	 *
	 * @param argument is an object that is an argument of this event.
	 */
	public void addArgument(RecordCardinalityObjectArgument argument) {
		eventArguments.add(argument);
	}
	
	/**
	 * The <code>toString</code> method returns the value of this argument in the form of a string. Note that
	 * the argument might have arguments of its own, such as 
	 * <code>"EVENT_ID","event_move1","OBJECT_ID","obj_dog1"</code>.
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
		for (RecordCardinalityObjectArgument eventArgument : eventArguments) {
			returnValue.append("," + eventArgument);
		}
		return returnValue.toString();
	}
}