package sparql;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msee.common.CartesianMetricPoint;
import msee.online.OnlineRequestType;
import msee.online.TimeInterval;

import org.apache.thrift.TException;

import TextParser.CObjectData;
import TextParser.TimeParser;
import edu.ucla.msee.online.OnlineComputingClient;

/**
 * This class provides an interface between UCLA's online functions and the Query Engine.
 * @author agrushin
 *
 */
public class OnlineComputingInterface {

	private static final boolean bVerboseThis = true;
	
	private static OnlineComputingInterface oci;
	
	private String knowledgeSetId;
	
	private OnlineComputingClient client;
	
	private long numCalls;
	
	private String errorMessage;
	
	// In some cases, an online function gets called a redundant number of times for the same arguments.
	// This map can be used to avoid making redundant requests to the server.
	private Map<ArrayList<String>,ArrayList<TimeData>> storedResponses = new HashMap<ArrayList<String>,ArrayList<TimeData>>();
	
	private OnlineComputingInterface( ) {
		this.client = new OnlineComputingClient();
	}
	
	/**
	 * Provides access to a single instance of the interface.
	 * @return
	 */
	public static OnlineComputingInterface getInterface( ) {
		if ( oci == null ) {
			oci = new OnlineComputingInterface();
		}
		
		return oci;
	}
	
	public void setKnowledgeSetId( String knowledgeSetId ) {
		this.knowledgeSetId = knowledgeSetId;
	}
	
	private TimeInterval translateTime( TimeData time_data ) {
		if ( time_data == null ) {
			time_data = MseeDataset.parsedDataDescriptor.areaOfResponsibility.GetSceneTime();
		}
		
		System.out.println( "Input time: " + time_data.mSceneTime_Start + "\t" + time_data.mSceneTime_End );
		
		TimeInterval retval = new TimeInterval();
		retval.start_time = TimeParser.MSEE_TIME_FORMAT_MILLISECOND_PRECISION.format(time_data.mSceneTime_Start);
		retval.end_time = TimeParser.MSEE_TIME_FORMAT_MILLISECOND_PRECISION.format(time_data.mSceneTime_End);
		return retval;
	}
	
	private List<CartesianMetricPoint> translateLocation( LocationData loc_data ) {
		if ( loc_data == null ) {
			loc_data = MseeDataset.parsedDataDescriptor.areaOfResponsibility.GetLocationData();
		}
		
		List<CartesianMetricPoint> points = new ArrayList<CartesianMetricPoint>();
		
		// TODO: We should call loc_data.ConvertViewToSceneCentric() for view-centric locations, though it does not matter too much right now, since location is
		// only used for roads, and it should already be scene-centric then.
		
		switch ( loc_data.location_type ) {
			case GEODETIC_POINT:
				points.add( new CartesianMetricPoint( loc_data.geodetic_point.latitude, loc_data.geodetic_point.longitude ) );
				break;					
			case GEODETIC_POLYGON:
				for ( GeodeticPoint gp : loc_data.geodetic_polygon.points ) {
					points.add( new CartesianMetricPoint( gp.latitude, gp.longitude ) );
				}
				break;									
			case CARTESIAN_METRIC_POINT:
				points.add( new CartesianMetricPoint( loc_data.cartesian_metric_point.x, loc_data.cartesian_metric_point.y ) );
				break;
			case CARTESIAN_METRIC_POLYGON:
				for ( sparql.CartesianMetricPoint cmp : loc_data.cartesian_metric_polygon.points ) {
					points.add( new CartesianMetricPoint( cmp.x, cmp.y ) );
				}
				break;		
			case VIEW_CENTRIC_POINT:
				points.add( new CartesianMetricPoint( loc_data.view_centric_point.x(), loc_data.view_centric_point.y() ) );
				break;
			case VIEW_CENTRIC_POLYGON:
				for ( ViewCentricPoint vcp : loc_data.view_centric_polygon.points ) {
					points.add( new CartesianMetricPoint( vcp.x(), vcp.y() ) );
				}
				break;		
			default:
				// TODO: Is there a better way to handle this case?
				System.err.println( "Warning: unknown type in LocationData object: " + loc_data + "." );
				break;
		}
		
		return points;
	}
	
	private ArrayList<TimeData> translateOutput( List<String> times ) {
		ArrayList<TimeData> retval = new ArrayList<TimeData>();
		
		if ( times == null ) {
			System.out.println( "Null times." );
		}
		else {
			for ( String time : times ) {
				System.out.println( time );
			}			
		}
		
		if ( times == null ) {
			System.err.println("DEBUG OnlineComputingInterface translateOutput times == null  " );
			
			return retval;
		}
		
		System.err.println("DEBUG OnlineComputingInterface translateOutput times.size() " + times.size());
		
		
		
		try {
			Date start = null;
			
			for ( String time : times ) {
				if ( start == null ) {
					start = TimeParser.getTimeParser().parse(time);
				}
				else {
					Date end = TimeParser.getTimeParser().parse(time);
					
					TimeData timeData = new TimeData();
					timeData.SetSceneTimePeriod(start, end);
					retval.add(timeData);
					
					start = null;
				}
			}
			
			if ( start != null ) {
				// In this case, we set the last time interval to [L, L], where L is the last element in times.
				System.err.println( "Warning: an online function returned an odd number of times." );
				TimeData timeData = new TimeData();
				timeData.SetSceneTimePeriod(start, start);
				retval.add(timeData);
			}
		}
		catch ( ParseException e ) {
			System.err.println( "An error occurred while parsing a time returned by an online function." );
			System.err.println(e);
		}
		
		System.err.println("DEBUG OnlineComputingInterface translateOutput retval.size() " + retval.size());
		
		return retval;
	}
	
	private boolean initialize( ) {
		if ( knowledgeSetId == null ) {
			return false;
		}
		
		System.err.println("DEBUG OnlineComputatingInterface initialize knowledgeSetId " + knowledgeSetId);
		try {
			client.Initialize(knowledgeSetId);
		}
		catch ( TException e ) {
			System.err.println( "An error occurred during the initialization of the online computing client." );
			System.err.println(e);
		}
		
		return true;
	}
	
	/**
	 * Send a unary online computation request.
	 * @param requestType
	 * @param time_data
	 * @param loc_data
	 * @param object1
	 * @return
	 */
	public ArrayList<TimeData> OnlineRequestUnary( OnlineRequestType requestType, TimeData time_data, LocationData loc_data, CObjectData object1 ) {
		// If knowledgeSetId is null, we will not contact the online computation server.
		
		numCalls++;
		System.out.println("DEBUG OnlineRequestUnary running, " + requestType + ", object " + object1.id + ", calls so far: " + getNumCalls() );
				
		ArrayList<String> hash = createHash(requestType, time_data, loc_data, object1, null, null);
		
		if ( storedResponses.get(hash) != null ) {
			return storedResponses.get(hash);
		}
		
		boolean initialized = initialize();
		
		if ( !initialized ) {
			storedResponses.put( hash, new ArrayList<TimeData>() );
			return storedResponses.get(hash);
		}
		
		List<String> retval = null;
		
		try {
			retval = client.OnlineRequestUnary(requestType, object1.id, translateLocation(loc_data), translateTime(time_data) );
			storedResponses.put( hash, translateOutput( retval ) );
			return storedResponses.get(hash);
		}
		catch ( Exception e ) {
			String message = "An error occurred for the request of type " + requestType + "; location = " + loc_data + "; time = " + time_data +
					"; object1 = " + object1 + ".";
			System.err.println( message );
			System.err.println(e);
			errorMessage = e.getMessage();
			if ( errorMessage == null ) {
				errorMessage = message;
			}
		}
		
		storedResponses.put( hash, new ArrayList<TimeData>() );
		return storedResponses.get(hash);
	}

	/**
	 * Send a binary online computation request.
	 * @param requestType
	 * @param time_data
	 * @param loc_data
	 * @param object1
	 * @param object2
	 * @return
	 */
	public ArrayList<TimeData> OnlineRequestBinary( OnlineRequestType requestType, TimeData time_data, LocationData loc_data, CObjectData object1, CObjectData object2 ) {
		// If knowledgeSetId is null, we will not contact the online computation server.
		
		numCalls++;
		
		if (this.bVerboseThis)
		{
		
			if ((object1 != null)&&(object2 != null) ) {
				System.out.println("DEBUG OnlineRequestBinary running, " + requestType + ", object " + object1.id + " " + object2.id + ", calls so far: " + getNumCalls() );
			}
			if (time_data != null)
			{			System.out.print("\tDEBUG OnlineRequestBinary time  \t\t");
						time_data.PrintTimeData();
			}
			if (loc_data != null)
			{
				System.out.print("\tDEBUG OnlineRequestBinary loc_data  \t\t");
				loc_data.Printf();
			}
		}
		
		ArrayList<String> hash = createHash(requestType, time_data, loc_data, object1, object2, null);
		
		if ( storedResponses.get(hash) != null ) {
			return storedResponses.get(hash);
		}
		
		boolean initialized = initialize();
		
		if ( !initialized ) {
			storedResponses.put( hash, new ArrayList<TimeData>() );
			return storedResponses.get(hash);
		}
		
		List<String> retval = null;
		
		try {
			retval = client.OnlineRequestBinary(requestType, object1.id, object2.id, translateLocation(loc_data), translateTime(time_data) );
			storedResponses.put( hash, translateOutput( retval ) );
			return storedResponses.get(hash);
		}
		catch ( Exception e ) {
			String message = "An error occurred for the request of type " + requestType + "; location = " + loc_data + "; time = " + time_data +
					"; object1 = " + object1 + "; object2 = " + object2 + ".";
			System.err.println( message );
			System.err.println(e);
			errorMessage = e.getMessage();

			if ( errorMessage == null ) {
				errorMessage = message;
			}
			e.printStackTrace();
			System.err.println("Error Message: " + errorMessage );

		}
		storedResponses.put( hash, new ArrayList<TimeData>() );
		return storedResponses.get(hash);
	}
	
	/**
	 * Send a trinary online computation request.
	 * @param requestType
	 * @param time_data
	 * @param loc_data
	 * @param object1
	 * @param object2
	 * @param object3
	 * @return
	 */
	public ArrayList<TimeData> OnlineRequestTrinary( OnlineRequestType requestType, TimeData time_data, LocationData loc_data, CObjectData object1, CObjectData object2, CObjectData object3 ) {
		// If knowledgeSetId is null, we will not contact the online computation server.
		
		numCalls++;
		System.out.println("DEBUG OnlineRequestTrinary running, " + requestType + ", object " + object1.id + " " + object2.id + " " + object2.id + ", calls so far: " + getNumCalls() );
		
		ArrayList<String> hash = createHash(requestType, time_data, loc_data, object1, object2, object3);
		
		if ( storedResponses.get(hash) != null ) {
			return storedResponses.get(hash);
		}
		
		boolean initialized = initialize();
		
		if ( !initialized ) {
			storedResponses.put( hash, new ArrayList<TimeData>() );
			return storedResponses.get(hash);
		}
		
		List<String> retval = null;
		
		try {
			retval = client.OnlineRequestTrinary(requestType, object1.id, object2.id, object3.id, translateLocation(loc_data), translateTime(time_data) );
			storedResponses.put( hash, translateOutput( retval ) );
			return storedResponses.get(hash);
		}
		catch ( Exception e ) {
			String message = "An error occurred for the request of type " + requestType + "; location = " + loc_data + "; time = " + time_data +
					"; object1 = " + object1 + "; object2 = " + object2 + "; object3 = " + object3 + ".";
			System.err.println( message );
			System.err.println(e);
			errorMessage = e.getMessage();
			if ( errorMessage == null ) {
				errorMessage = message;
			}
		}
		
		storedResponses.put( hash, new ArrayList<TimeData>() );
		return storedResponses.get(hash);
	}
	
	/**
	 * Returns the number of online function calls that were made since the last call to resetNumCalls().
	 * @return
	 */
	public long getNumCalls( ) {
		return numCalls;
	}
	
	/**
	 * Resets the count for the number of calls.
	 */
	public void resetNumCalls( ) {
		numCalls = 0;
	}
	
	/**
	 * Returns the first error message (if any) that was returned by the online computation server since the last call to resetErrorMessage().
	 * If there is no error message, then null is returned.
	 * @return
	 */
	public String getErrorMessage( ) {
		return errorMessage;
	}
	
	/**
	 * Resets the error message to null.
	 * @return
	 */
	public void resetErrorMessage( ) {
		errorMessage = null;
	}
	
	/**
	 * Clears the stored responses map.
	 * @return
	 */
	public void clearStoredResponses( ) {
		storedResponses.clear();
	}
	
	private ArrayList<String> createHash( OnlineRequestType requestType, TimeData time_data, LocationData loc_data, CObjectData object1, CObjectData object2, CObjectData object3 ) {
		ArrayList<String> hash = new ArrayList<String>();
				
		hash.add(requestType.toString());
		
		if ( time_data != null ) {
			hash.add( time_data.getUniqueStringDescription() );
		}
		
		if ( loc_data != null ) {
			hash.add( loc_data.getUniqueStringDescription() );
		}
		
		if ( object1 != null ) {
			hash.add( object1.id );
		}
		
		if ( object2 != null ) {
			hash.add( object2.id );
		}
		
		if ( object3 != null ) {
			hash.add( object3.id );
		}
		
		return hash;		
	}
	
	// For testing
	public static void main( String[] args ) {
		TimeData timeData = new TimeData();
		timeData.mSceneTime_Start = new Date( );
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( timeData.mSceneTime_Start.getTime() + 10000 );
		timeData.mSceneTime_End = c.getTime();
		
		System.err.println( OnlineComputingInterface.getInterface().translateTime(timeData) );
		
		ArrayList<sparql.CartesianMetricPoint> points = new ArrayList<sparql.CartesianMetricPoint>( );
		points.add( new sparql.CartesianMetricPoint( 0, 10 ) );
		points.add( new sparql.CartesianMetricPoint( 10, 20 ) );
		points.add( new sparql.CartesianMetricPoint( -5, 20 ) );
		sparql.CartesianMetricPolygon polygon = new sparql.CartesianMetricPolygon();
		polygon.points = points;
		LocationData locData = new LocationData( );
		locData.location_type = LocationData.LocationType.CARTESIAN_METRIC_POLYGON;
		locData.cartesian_metric_polygon = polygon;
		
		System.err.println( OnlineComputingInterface.getInterface().translateLocation(locData) );
		
		ArrayList<String> output = new ArrayList<String>();
		
		for ( int i = 0; i <= 5; i++ ) {
			if ( i > 0 ) {
				c = Calendar.getInstance();
				c.setTimeInMillis( timeData.mSceneTime_Start.getTime() + i * 1000 );
				
				output.add( TimeParser.MSEE_TIME_FORMAT_SECOND_PRECISION.format(c.getTime()) );
			}
			
			ArrayList<TimeData> times = OnlineComputingInterface.getInterface().translateOutput(output);				
			
			for ( int j = 0; j < times.size(); j++ ) {
				System.err.println( times.get(j).mSceneTime_Start + "\t" + times.get(j).mSceneTime_End );
			}
			
			System.err.println( "" );
		}
		
		// OnlineComputingInterface.getInterface().setKnowledgeSetId("soc1");
		
		// System.err.println( OnlineComputingInterface.getInterface().OnlineRequestBinary( OnlineRequestType.ONLINE_BIN_TOGETHER, timeData, locData, new CObjectData("obj1"), new CObjectData("obj2") ) );
	}
}
