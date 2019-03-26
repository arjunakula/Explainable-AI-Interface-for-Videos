package sparql.translator.location;

/**
 * The <code>SceneCentricIndoorLocation</code> abstract class is a cartesian metric point, polygon, or 
 * 3-dimensional shape. Its subclasses are<code>CartesianMetricPoint</code>, 
 * <code>CartesianMetricPolygon</code>, and <code>CartesianMetric3DShape</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public abstract class SceneCentricIndoorLocation extends SceneCentricLocation {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * The <code>SceneCentricIndoorLocation</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 */
	public SceneCentricIndoorLocation(String idIn) {
		super(idIn);
	}
}