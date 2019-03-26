package TextParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeParser {

	public static SimpleDateFormat MSEE_TIME_FORMAT_MILLISECOND_PRECISION = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	public static SimpleDateFormat MSEE_TIME_FORMAT_SECOND_PRECISION = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private static TimeParser timeParser;
	
	private TimeParser( ) {
		
	}

	public static TimeParser getTimeParser( ) {
		if ( timeParser == null ) {
			timeParser = new TimeParser( );
		}
		return timeParser;
	}
	
	/**
	 * Parses the provided time string via MSEE_TIME_FORMAT_MILLISECOND_PRECISION (and, if unsuccessful, MSEE_TIME_FORMAT_SECOND_PRECISION),
	 * first appending a 'Z' at the end (if it is missing), and truncating (not rounding) any microseconds.
	 * @param source
	 * @return
	 */
	public Date parse( String source ) throws ParseException {
		if ( source == null ) {
			MSEE_TIME_FORMAT_MILLISECOND_PRECISION.parse("null"); // This should generate an exception.
		}
		
		if ( source.endsWith("z") ) {
			source = source.substring(0, source.length() - 1) + "Z";
		}
		
		if ( !source.endsWith("Z") ) {
			source = source + "Z";
		}
		
		// Check for microsecond precision
		int a = source.lastIndexOf(".");
		int b = source.lastIndexOf("Z");
		
		if ( a >= 0 && b - a > 4) {
			source = source.substring(0, a+4) + "Z";
		}
		
		// System.out.println( source );
		
		try {
			return MSEE_TIME_FORMAT_MILLISECOND_PRECISION.parse(source);
		} catch (ParseException e) {
			// System.out.println( e );
		}

		return MSEE_TIME_FORMAT_SECOND_PRECISION.parse(source);
	}
	
	// Test main
	public static void main( String[] args ) {
		String[] times = new String[] {
				"2013-10-12T14:15:00Z",
				"2013-10-12T14:15:00z",
				"2013-10-12T14:15:00",
				"2013-10-12T14:15:00.099Z",
				"2013-10-12T14:15:00.99Z",
				"2013-10-12T14:15:00.9999Z",
				"2013-10-12T14:15:00.999999Z",
				"2013-10-12T14:15:00.999999999Z"
				};
		
		for ( int i = 0; i < times.length; i++ ) {
			try {
				System.out.println( TimeParser.getTimeParser().parse(times[i]) );
				System.out.println( TimeParser.getTimeParser().parse(times[i]).getTime() );				
			}
			catch ( ParseException e ) {
				e.printStackTrace();
			}
		}
	}
}
