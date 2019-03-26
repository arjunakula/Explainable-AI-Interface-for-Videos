package sparql.translator.location;

/**
 * The <code>SceneCentricOutdoorLocation</code> abstract class is a geodetic point, polygon, or 3-dimensional 
 * shape. Its subclasses are <code>GeodeticPoint</code>, <code>GeodeticPolygon</code>, and 
 * <code>Geodetic3DShape</code>. 
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public abstract class SceneCentricOutdoorLocation extends SceneCentricLocation {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * The <code>SceneCentricOutdoorLocation</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 */
	public SceneCentricOutdoorLocation(String idIn) {
		super(idIn);
	}
}