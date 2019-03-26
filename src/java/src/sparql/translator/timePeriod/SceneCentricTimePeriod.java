package sparql.translator.timePeriod;

/**
 * The <code>SceneCentricTimePeriod</code> class is the duration between two points in time with the
 * start time and end time expressed in UTC format (for example, "<code>2007-06-30T14:30:00.000Z</code>").
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class SceneCentricTimePeriod extends TimePeriod {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * The <code>SceneCentricTimePeriod</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this time period.
	 */
	public SceneCentricTimePeriod(String idIn) {
		super(idIn);
	}
	
	/**
	 * The <code>isInUTCFormat</code> method determines whether the given string is a time in UTC format
	 * (for example, "<code>2007-06-30T14:30:00.000Z</code>").
	 *
	 * @param string is the string to be tested.
	 * @return <b><code>true</code></b> if the given string is in the format "YYYY-MM-DDThh:mm:ss.sssZ" and 
	 * <b><code>false</code></b> otherwise.
	 */
	private static Boolean isInUTCFormat(String string) {
		return string.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\dZ");
	}
	
	/**
	 * The <code>unround</code> method fixes a time that is in the UTC format except the time may have been
	 * rounded (for example, "<code>2007-06-30T14:30:00Z</code>").
	 *
	 * @param roundedUTCString is a time that would be in UTF format if it had ".sss" after the seconds.
	 * @return the given string with zeroes added after the seconds, if necessary.
	 */
	private static String unround(String roundedUTCString) {
		String returnValue;
		
		if (roundedUTCString.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ")) {
			returnValue = roundedUTCString.replace("Z",".000Z");
		} else if (roundedUTCString.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\dZ")) {
			returnValue = roundedUTCString.replace("Z","00Z");
		} else if (roundedUTCString.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\dZ")) {
			returnValue = roundedUTCString.replace("Z","0Z");
		} else {
			returnValue = roundedUTCString;
		}
		return returnValue;
	}

	/**
	 * The <code>setStart</code> setter changes the value of the global variable,
	 * <code>start</code>, a <b><code>String</code></b>.
	 *
	 * @param startIn is the new value that should be assigned to <code>start</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@Override
	public void setStart(String startIn, String owner) {
		String newStart;			//The start time

		newStart = unround(startIn);
		if (! isInUTCFormat(newStart)) {
			System.err.println(
					"WARNING in SceneCentricTimePeriod.setStart in " + owner + ": The start time, \"" + 
					startIn + "\", needs to be in UTC format (YYYY-MM-DDThh:mm:ss.sssZ). " +
					"For example: 2007-06-30T14:30:00.000Z");
		}
		super.setStart(newStart,owner);
	}

	/**
	 * The <code>setEnd</code> setter changes the value of the global variable,
	 * <code>end</code>, a <b><code>String</code></b>.
	 *
	 * @param endIn is the new value that should be assigned to <code>end</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@Override
	public void setEnd(String endIn, String owner) {
		String newEnd;			//The end time

		newEnd = unround(endIn);
		if (! isInUTCFormat(newEnd)) {
			System.err.println(
					"WARNING in SceneCentricTimePeriod.setEnd in " + owner + ": The end time, \"" + 
					endIn + "\", needs to be in UTC format (YYYY-MM-DDThh:mm:ss.sssZ). " +
					"For example: 2007-06-30T14:30:00.000Z");
		}
		super.setEnd(newEnd,owner);
	}
}