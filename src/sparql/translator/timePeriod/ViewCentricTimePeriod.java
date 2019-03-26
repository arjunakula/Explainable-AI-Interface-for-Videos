package sparql.translator.timePeriod;

import sparql.translator.utilities.Global;

/**
 * The <code>ViewCentricTimePeriod</code> class is the duration between two points in time, as
 * represented in the view of an observer (such as a camera).
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class ViewCentricTimePeriod extends TimePeriod {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;
	
	/** 
	 * <code>viewId</code> is the name of the view (such as a camera's view) with the frame numbers that 
	 * correspond to this class's start frame and end frame. 
	 */
	private String viewId;
	
	
	/**
	 * The <code>ViewCentricTimePeriod</code> constructor initializes this class's fields.
	 *
	 * @param idIn is the identifier to be assigned to this time period.
	 */
	public ViewCentricTimePeriod(String idIn) {
		super(idIn);
		viewId = null;
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
					"WARNING in ViewCentricTimePeriod.setViewId in " + owner + ": The view name, \"" + 
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