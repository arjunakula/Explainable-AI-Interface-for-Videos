package mergeData;

import java.io.PrintStream;


/**
 * The <code>MSEEThing</code> is an MSEE object or event (behavior, relationship, or action). This class has 
 * two subclasses: <code>MSEEObject</code> and <code>MSEEEvent</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 13, 2014
 * @since 1.6
 */
public class MSEEThing {
	
	/** <code>id</code> is the identification code of this thing. */
	private String id;

	/** <code>type</code> is the type of this thing, such as "Human" or "Move". */
	private String type;
	
	/** <code>confidence</code> is the confidence that this thing was correctly detected. */
	private String confidence;

	/** 
	 * In the case where this thing was duplicated (with different <code>information</code>),
	 * <code>duplicate</code> is a pointer to the duplicate thing. 
	 */
	private MSEEThing duplicate;
	

	/**
	 * The <code>MSEEThing</code> constructor initializes the global variables of this object.
	 *
	 * @param idIn is the identification code of this <code>Thing</code>.
	 * @param typeIn is the type of this <code>Thing</code>, such as "Human".
	 * @param confidenceIn is the confidence that it was correctly detected.
	 */
	public MSEEThing(String idIn, String typeIn, String confidenceIn) {
		id = idIn;
		type = typeIn;
		confidence = confidenceIn;
		duplicate = null;
	}

	/**
	 * The <code>writeToRdfFile</code> method saves this thing in the rdf output file in the 
	 * proper format for that file.
	 *
	 * @param out is the <code>PrintStream</code> that will be used to write data to the rdf output file.
	 */
	public void writeThingToRdfFile(PrintStream out) {
		out.println("<msee:" + getType() + " rdf:about=\"#" + getId() + "\">");
		//Example: <msee:Human rdf:about="#138022bb-a29e-433f-b24c-4515836daf33">
		if ((getConfidence() != null) && ( ! Global.isOne(getConfidence()))) {
			out.println("\t<msee:hasConfidence>" + getConfidence() + "</msee:hasConfidence>");
			//Example: <msee:hasConfidence>0.8</msee:hasConfidence>
		}
		writeInformationToRdfFile(out);
	}
	
	/**
	 * The <code>writeInformationToRdfFile</code> method saves all of the information about this thing to the 
	 * rdf output file in the proper format for that file. (The first line is handled by the superclass of 
	 * this object.)
	 *
	 * @param out is the <code>PrintStream</code> that will be used to write data to the rdf output file.
	 */
	@SuppressWarnings("unused")
	protected void writeInformationToRdfFile(PrintStream out) {
		System.err.println(				//This method should be overridden by its subclasses
				"WARNING in MSEEThing.writeInformationToRdfFile: A bug has been detected. Contact Ken Samuel."
				);
	}

	/**
	 * The <code>setId</code> setter changes the value of the global variable,
	 * <code>id</code>, a <b><code>String</code></b>.
	 *
	 * @param idIn is the new value that should be assigned to <code>id</code>.
	 */
	public void setId(String idIn) {
		id = idIn;
	}

	/**
	 * The <code>setType</code> setter changes the value of the global variable,
	 * <code>type</code>, a <b><code>String</code></b>.
	 *
	 * @param typeIn is the new value that should be assigned to <code>type</code>.
	 */
	public void setType(String typeIn) {
		type = typeIn;
	}

	/**
	 * The <code>setConfidence</code> setter changes the value of the global variable,
	 * <code>confidence</code>, a <b><code>String</code></b>.
	 *
	 * @param confidenceIn is the new value that should be assigned to <code>confidence</code>.
	 */
	public void setConfidence(String confidenceIn) {
		confidence = confidenceIn;
	}

	/**
	 * The <code>setDuplicate</code> setter changes the value of the global variable,
	 * <code>duplicate</code>, a <b><code>Thing</code></b>.
	 *
	 * @param duplicateIn is the new value that should be assigned to <code>duplicate</code>.
	 */
	public void setDuplicate(MSEEThing duplicateIn) {
		duplicate = duplicateIn;
	}

	/**
	 * The <code>getId</code> getter returns the value of the global variable,
	 * <code>id</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>id</code>.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * The <code>getType</code> getter returns the value of the global variable,
	 * <code>type</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>type</code>.
	 */
	public String getType() {
		return type;
	}

	/**
	 * The <code>getConfidence</code> getter returns the value of the global variable,
	 * <code>confidence</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>confidence</code>.
	 */
	public String getConfidence() {
		return confidence;
	}

	/**
	 * The <code>getDuplicate</code> getter returns the value of the global variable,
	 * <code>duplicate</code>, a <b><code>Thing</code></b>.
	 *
	 * @return the value of <code>duplicate</code>.
	 */
	public MSEEThing getDuplicate() {
		return duplicate;
	}

	/**
	 * The <code>toString</code> method returns a string that represents this <code>Thing</code>.
	 *
	 * @return some information about this <code>Thing</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return type + " (id=" + id + ")";
	}
}