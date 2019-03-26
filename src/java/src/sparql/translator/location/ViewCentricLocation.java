package sparql.translator.location;

import sparql.translator.utilities.Global;

/**
 * The <code>ViewCentricLocation</code> abstract class is a point or a 2-dimensional region, as represented in  
 * the view of an observer (such as a camera). Its subclasses are <code>ViewCentricPoint</code> and 
 * <code>ViewCentricPolygon</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public abstract class ViewCentricLocation extends Location {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>viewId</code> is the name of the view (such as a camera's view) with the coordinate system that 
	 * corresponds to this class's pixel coordinates. 
	 */
	private String viewId;

	
	/**
	 * The <code>ViewCentricLocation</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 * @param viewIdIn is the name of the view (such as a camera's view) with the coordinate system that 
	 * corresponds to the pixel coordinates of this location.
	 */
	public ViewCentricLocation(String idIn, String viewIdIn) {
		super(idIn);
		viewId = viewIdIn;
	}

	/**
	 * The <code>ViewCentricLocation</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this location.
	 * corresponds to the pixel coordinates of this location.
	 */
	public ViewCentricLocation(String idIn) {
		this(idIn,null);
	}

	/**
	 * The <code>setViewId</code> setter changes the value of the global variable,
	 * <code>viewId</code>, a <b><code>String</code></b>.
	 *
	 * @param viewIdIn is the new value that should be assigned to <code>viewId</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void setViewId(String viewIdIn, String owner) {
		if (! Global.isViewId(viewIdIn)) {
			System.err.println(
					"WARNING in ViewCentricLocation.setViewId in " + owner + ": The view name, \"" + 
					viewIdIn + "\", should begin with \"view-\".");
		}
		viewId = viewIdIn;
	}

	/**
	 * The <code>getViewId</code> getter returns the value of the global variable,
	 * <code>viewId</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>viewId</code>.
	 */
	public String getViewId() {
		return viewId;
	}
}