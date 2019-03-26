package sparql.translator.timePeriod;

import java.io.Serializable;

import sparql.translator.utilities.ArgumentType;

/**
 * The <code>TimePeriod</code> abstract class represents the duration between two points in time.
 * Its subclasses are <code>ViewCentricTimePeriod</code> and <code>SceneCentricTimePeriod</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 7, 2013
 * @since 1.6
 */
public abstract class TimePeriod implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;
	
	/** <code>id</code> is the name assigned to this time period. */
	protected String id;

	/** 
	 * <code>start</code> is the time at the beginning of this class's duration. For scene-centric times, it 
	 * is represented in UTC format (for example, "<code>2007-06-30T14:30:00.000Z</code>"). For view-centric 
	 * times, it is a frame number. 
	 */
	protected String start;
	
	/** 
	 * <code>end</code> is the time at the end of this class's duration. For scene-centric times, it 
	 * is represented in UTC format (for example, "<code>2007-06-30T14:30:00.000Z</code>"). For view-centric 
	 * times, it is a frame number. 
	 */
	protected String end;
	
	/** 
	 * <code>timeType</code> specifies how this time period is represented. There are two possible values:
	 * <li><b>SCENE_CENTRIC_TIME</b> : Time of day values in UTF format, such as "2013-09-04T14:53:28.000Z"  
	 * <li><b>VIEW_CENTRIC_TIME</b> : Frame numbers of a specified observer (such as a camera)  
	 */
	private ArgumentType timeType;

	
	/**
	 * The <code>TimePeriod</code> constructor initializes the global variable.
	 *
	 * @param idIn is the name that is assigned to this time period.
	 */
	public TimePeriod(String idIn) {
		id = idIn;
		timeType = null;
		start = null;
		end = null;
	} 
	

	/**
	 * The <code>setStart</code> setter changes the value of the global variable,
	 * <code>start</code>, a <b><code>Integer</code></b>.
	 *
	 * @param startIn is the new value that should be assigned to <code>start</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void setStart(String startIn, @SuppressWarnings("unused") String owner) {
		start = startIn;
	}

	/**
	 * The <code>setEnd</code> setter changes the value of the global variable,
	 * <code>end</code>, a <b><code>Integer</code></b>.
	 *
	 * @param endIn is the new value that should be assigned to <code>end</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void setEnd(String endIn, @SuppressWarnings("unused") String owner) {
		end = endIn;
	}

	/**
	 * The <code>setTimeType</code> setter changes the value of the global variable,
	 * <code>timeType</code>, a <b><code>ArgumentType</code></b>.
	 *
	 * @param timeTypeIn is the new value that should be assigned to <code>timeType</code>.
	 */
	public void setTimeType(ArgumentType timeTypeIn) {
		timeType = timeTypeIn;
	}

	/**
	 * The <code>getStart</code> getter returns the value of the global variable,
	 * <code>start</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>start</code>.
	 */
	public String getStart() {
		return start;
	}

	/**
	 * The <code>getEnd</code> getter returns the value of the global variable,
	 * <code>end</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>end</code>.
	 */
	public String getEnd() {
		return end;
	}	

	/**
	 * The <code>getTimeType</code> getter returns the value of the global variable,
	 * <code>timeType</code>, a <b><code>ArgumentType</code></b>.
	 *
	 * @return the value of <code>timeType</code>.
	 */
	public ArgumentType getTimeType() {
		return timeType;
	}
}