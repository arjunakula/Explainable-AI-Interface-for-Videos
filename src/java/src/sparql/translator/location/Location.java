package sparql.translator.location;

import java.io.Serializable;

import sparql.translator.utilities.ArgumentType;

/**
 * The <code>Location</code> abstract class is a point, a polygon, or a 3-dimensional shape. Its subclasses 
 * are <code>ViewCentricLocation</code> and <code>SceneCentricLocation</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 7, 2013
 * @since 1.6
 */
public abstract class Location  implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;

	
	/** <code>id</code> is the name assigned to this location. */
	protected String id;
	
	/** 
	 * <code>locationType</code> specifies how this location's points are represented. There are three 
	 * possible values:
	 * <li><b>SCENE_CENTRIC_INDOOR_LOCATION</b> : Cartesian metric coordinates (x, y, and z)  
	 * <li><b>SCENE_CENTRIC_OUTDOOR_LOCATION</b> : Geodetic coordinates (latitude, longitude, and altitude)  
	 * <li><b>VIEW_CENTRIC_LOCATION</b> : Pixel coordinates (x and y) of a specified observer (such as a 
	 * camera)  
	 */
	private ArgumentType locationType; 

	/** <code>locationShape</code> specifies whether this location is a point, a polygon, or a 3-dimensional 
	 * shape. 
	 */
	private ArgumentType locationShape;

	
	/**
	 * The <code>Location</code> constructor initializes the global variables.
	 *
	 * @param idIn is the name that is assigned to this location.
	 */
	public Location(String idIn) {
		id = idIn;
		locationType = null;
		locationShape = null;
	}

	/**
	 * The <code>setLocationType</code> setter changes the value of the global variable,
	 * <code>locationType</code>, a <b><code>ArgumentType</code></b>.
	 *
	 * @param locationTypeIn is the new value that should be assigned to <code>locationType</code>.
	 */
	public void setLocationType(ArgumentType locationTypeIn) {
		locationType = locationTypeIn;
	}

	/**
	 * The <code>setLocationShape</code> setter changes the value of the global variable,
	 * <code>locationShape</code>, a <b><code>ArgumentType</code></b>.
	 *
	 * @param locationShapeIn is the new value that should be assigned to <code>locationShape</code>.
	 */
	public void setLocationShape(ArgumentType locationShapeIn) {
		locationShape = locationShapeIn;
	}

	/**
	 * The <code>getLocationType</code> getter returns the value of the global variable,
	 * <code>locationType</code>, a <b><code>ArgumentType</code></b>.
	 *
	 * @return the value of <code>locationType</code>.
	 */
	public ArgumentType getLocationType() {
		return locationType;
	}

	/**
	 * The <code>getLocationShape</code> getter returns the value of the global variable,
	 * <code>locationShape</code>, a <b><code>ArgumentType</code></b>.
	 *
	 * @return the value of <code>locationShape</code>.
	 */
	public ArgumentType getLocationShape() {
		return locationShape;
	}
}