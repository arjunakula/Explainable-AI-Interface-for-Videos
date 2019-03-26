package sparql.translator.recordCardinality;

import java.io.Serializable;

/**
 * The <code>RecordCardinalityArguments</code> class represents a list of arguments for the 
 * "RecordCardinality" filter function, which is needed only if the query requires a count of the number of 
 * elements in this set. The following are examples of "RecordCardinality" arguments:
 * <ul>
 * <li><code>"SET_NAME","set_moving"</code>
 * <li><code>"LOGICAL_OPERATOR","or","EVENT_ID","event_move1","OBJECT_ID","obj_dog1",
 * "FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>./code>.
 * <li><code>"SCENE_CENTRIC_TIME_PERIOD","2013-09-04T15:25:51Z;2013-09-04T15:25:55Z"</code>
 * <li><code>"CARTESIAN_PIXEL_POLYGON","0,8;7,8;7,4"</code>
 * <li><code>"EVENT_ID","event_move1","OBJECT_ID","obj_dog1"</code>
 * <li><code>"FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>
 * <li><code>"CARTESIAN_METRIC_POINT","0.0,8.2","OBJECT_ID","obj_dog1"</code>
 * </ul>
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityArguments implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;

	/**
	 *  <code>argumentType</code> apecifies the type of the first argument. It can be <code>SET_NAME</code>, 
	 * <code>LOGICAL_OPERATOR</code>, <code>EVENT_ID</code>, <code>FUNCTION</code>, <code>OBJECT_ID</code>, 
	 * or a type of time period or location.
	 */
	protected String argumentType;
	
	/** <code>timePeriod</code> is a restriction on this event's time period, if any. */
	protected String timePeriod;

	/** <code>location</code> is a restriction on this event's location, if any. */
	protected String location;

	
	/**
	 * The <code>RecordCardinalityArguments</code> constructor initializes the global variables.
	 * 
	 * @param type is the type of the first argument.
	 */
	public RecordCardinalityArguments(String type) {
		argumentType = type;
		timePeriod = null;
		location = null;
	}
	
	/**
	 * The <code>RecordCardinalityArguments</code> constructor initializes the global variables.
	 */
	public RecordCardinalityArguments() {
		this("");
	}

	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this list of  
	 * arguments with another variable. (This method is supposed to be 
	 * overridden by one of its subclasses' <code>replaceVariable</code> methods.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@SuppressWarnings("unused")
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		return;
	}
	
	/**
	 * The <code>setTimePeriod</code> setter changes the value of the global variable,
	 * <code>timePeriod</code>, a <b><code>RecordCardinalityTimePeriodArguments</code></b>.
	 *
	 * @param timePeriodIn is the new value that should be assigned to <code>timePeriod</code>.
	 */
	public void setTimePeriod(String timePeriodIn) {
		timePeriod = timePeriodIn;
	}

	/**
	 * The <code>setLocation</code> setter changes the value of the global variable,
	 * <code>location</code>, a <b><code>RecordCardinalityLocationArguments</code></b>.
	 *
	 * @param locationIn is the new value that should be assigned to <code>location</code>.
	 */
	public void setLocation(String locationIn) {
		location = locationIn;
	}
	
	/**
	 * The <code>toString</code> method returns <b><code>null</code></b>. (This method is supposed to be 
	 * overridden by one of its subclasses' <code>toString</code> methods.
	 *
	 * @return a string representation of this object's value.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return null;
	}
}