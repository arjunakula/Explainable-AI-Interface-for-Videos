package sparql.translator.location;

/**
 * The <code>SceneCentricLocation</code> abstract class is a point, a polygon, or a 3-dimensional shape. Its 
 * subclasses are <code>GeodeticPoint</code>, <code>GeodeticPolygon</code>, <code>Geodetic3DShape</code>, 
 * <code>CartesianMetricPoint</code>, <code>CartesianMetricPolygon</code>, and 
 * <code>CartesianMetric3DShape</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public abstract class SceneCentricLocation extends Location {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * The <code>SceneCentricLocation</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 */
	public SceneCentricLocation(String idIn) {
		super(idIn);
	}
}