package sparql.translator.recordCardinality;

import java.io.Serializable;

/**
 * The <code>RecordCardinalityArgument</code> class represents an argument for the "RecordCardinality" filter
 * function, which is needed only if the query requires a count of the number of elements in this set. The 
 * following are examples of "RecordCardinality" arguments:
 * <ul>
 * <li><code>"SET_NAME","set_moving"</code>
 * <li><code>"LOGICAL_OPERATOR","or"</code>
 * <li><code>"SCENE_CENTRIC_TIME_PERIOD","2013-09-04T15:25:51Z;2013-09-04T15:25:55Z"</code>
 * <li><code>"CARTESIAN_PIXEL_POLYGON","0,8;7,8;7,4"</code>
 * <li><code>"EVENT_ID","event_move1"</code>
 * <li><code>"FUNCTION","IsMoving"</code>
 * <li><code>"OBJECT_ID","obj_dog1"</code>
 * </ul>
 *
 * @author Ken Samuel
 * @version 1.0, Dec 27, 2013
 * @since 1.6
 */
public class RecordCardinalityArgument implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;

	/**
	 *  <code>argumentType</code> apecifies the type of this argument. It can be <code>SET_NAME</code>, 
	 * <code>LOGICAL_OPERATOR</code>, <code>EVENT_ID</code>, <code>FUNCTION</code>, <code>OBJECT_ID</code>, 
	 * or a type of time period or location.
	 */
	protected String argumentType;
	
	/** <code>timePeriod</code> is a restriction on this event's time period, if any. */
	protected String timePeriod;

	/** <code>location</code> is a restriction on this event's location, if any. */
	protected String location;

	
	/**
	 * The <code>RecordCardinalityArgument</code> constructor initializes the global variables.
	 * 
	 * @param type is the type of this argument.
	 */
	public RecordCardinalityArgument(String type) {
		argumentType = type;
		timePeriod = null;
		location = null;
	}
	
	/**
	 * The <code>RecordCardinalityArgument</code> constructor initializes the global variables.
	 */
	public RecordCardinalityArgument() {
		this("");
	}
	
	/**
	 * The <code>setTimePeriod</code> setter changes the value of the global variable,
	 * <code>timePeriod</code>, a <b><code>RecordCardinalityTimePeriodArgument</code></b>.
	 *
	 * @param timePeriodIn is the new value that should be assigned to <code>timePeriod</code>.
	 */
	public void setTimePeriod(String timePeriodIn) {
		timePeriod = timePeriodIn;
	}

	/**
	 * The <code>setLocation</code> setter changes the value of the global variable,
	 * <code>location</code>, a <b><code>RecordCardinalityLocationArgument</code></b>.
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