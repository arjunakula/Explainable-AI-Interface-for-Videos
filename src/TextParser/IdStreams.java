package TextParser;

import java.util.HashMap;
import java.util.Map;

import sparql.translator.utilities.Global;

/**
 * Provides three streams of ids: for objects, times and locations.
 * @author agrushin
 *
 */
public class IdStreams {
	
	private static IdStreams instance;
	
	private long objectIndex;
	private long timeIndex;
	private long locationIndex;
	
	private Map<String,String> idMap;
	
	private IdStreams( ) {
		reset();
	}
	
	public static IdStreams getIdStreams( ) {
		if ( instance == null ) {
			instance = new IdStreams();
		}
		
		return instance;
	}
	
	/**
	 * Resets all streams to their initial values.  This method should be called at the beginning of every new storyline.
	 */
	public void reset( ) {
		objectIndex = 0;
		timeIndex = 0;
		locationIndex = 0;
		
		idMap = new HashMap<String,String>();
	}
	
	/**
	 * Returns the next object id in the stream, or an existing object id, if rdfIndex has been observed before, since the last call to reset().
	 * @param rdfIndex The id of the object in the RDF representation.
	 * @return
	 */
	public String nextObjectId( String rdfIndex ) {
		if ( idMap.get(rdfIndex) != null ) {
			return idMap.get(rdfIndex);
		}
		
		String id = "obj-sut-" + (++objectIndex);
		
		idMap.put(rdfIndex, id);
		
		return id;
	}

	/**
	 * Returns the next time id in the stream.
	 * @return
	 */
	public String nextTimeId( ) {
		String timeId;
		
		do {
			timeId = "time-sut-" + (++timeIndex);
		}
		while ( Global.definitions.containsKey(timeId) );
		
		return timeId;
	}
	
	/**
	 * Returns the next location id in the stream.
	 * @return
	 */
	public String nextLocationId( ) {
		String locationId;
		
		do {
			locationId = "loc-sut-" + (++locationIndex);
		}
		while( Global.definitions.containsKey(locationId) );
		
		return locationId;
	}
}
