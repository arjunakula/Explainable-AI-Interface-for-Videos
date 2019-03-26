package sparql.translator.utilities;

/**
 * The <code>ArgumentType</code> is a type of argument in a FILTER line that specifies times and/or 
 * locations.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 7, 2013
 * @since 1.6
 */
public enum ArgumentType {

	/** 
	 * A <code>SCENE_CENTRIC_TIME</code> <code>ArgumentType</code> represents a time of day that is 
	 * defined with values in UTF format, such as "2013-09-04T14:53:28.000Z".
	 */
	SCENE_CENTRIC_TIME,

	/** 
	 * A <code>VIEW_CENTRIC_TIME</code> <code>ArgumentType</code> represents a time that is 
	 * defined with frame numbers of a specified observer (such as a camera).
	 */
	VIEW_CENTRIC_TIME,

	/** 
	 * A <code>SCENE_CENTRIC_INDOOR_LOCATION</code> <code>ArgumentType</code> represents a location that is 
	 * defined with cartesian metric coordinates (x, y, and z).
	 */
	SCENE_CENTRIC_INDOOR_LOCATION,

	/** 
	 * A <code>SCENE_CENTRIC_OUTDOOR_LOCATION</code> <code>ArgumentType</code> represents a location that is 
	 * defined with geodetic coordinates (latitude, longitude, and altitude). 
	 */
	SCENE_CENTRIC_OUTDOOR_LOCATION,

	/** 
	 * A <code>VIEW_CENTRIC_LOCATION</code> <code>ArgumentType</code> represents a location that is 
	 * defined by pixel coordinates (x, y) of a specified observer (such as a camera).
	 */
	VIEW_CENTRIC_LOCATION,
	
	/** <code>POINT</code> is a single zero-dimensional point in space. */
	POINT,
	
	/** <code>POLYGON</code> is a two-dimensional shape. */
	POLYGON,
	
	/** <code>VOLUME</code> is a three-dimensional shape. */
	VOLUME,
}