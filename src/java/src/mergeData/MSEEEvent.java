package mergeData;

import java.io.PrintStream;


/**
 * The <code>MSEEEvent</code> is an MSEE event (behavior, relationship, or action).
 *
 * @author Ken Samuel
 * @version 1.0, Mar 13, 2014
 * @since 1.6
 */
public class MSEEEvent extends MSEEThing {
	
	/** <code>agent</code> is the identification code of the agent of this event. */
	private String agent;
	
	/** <code>patient</code> is the identification code of the patient of this event, if any. */
	private String patient;
	
	/** <code>destination</code> is the identification code of the 3rd argument of this event, if any. */
	private String destination;

	/**
	 *  <code>eventsFileLine</code> holds  the location and time information about this event. It is a string 
	 *  in the form found in the events files. 
	 */
	private String eventsFileLine;
	
	/**  <code>startFrame</code> is the number of the frame in which this event begins. */
	private Integer startFrame;
	
	/** <code>endFrame</code> is the number of the frame in which this event ends. */ 
	private Integer endFrame;

	
	/**
	 * The <code>MSEEEvent</code> constructor initializes the global variables of this event.
	 *
	 * @param idIn is the identification code of this event.
	 * @param typeIn is the type of this event, such as "Move".
	 * @param confidenceIn is the confidence that it was correctly detected.
	 * @param agentIn is the identification number of its agent.
	 * @param patientIn is the identification number of its patient, if any.
	 * @param destinationIn is the identification number of its third argument, if any.
	 */
	public MSEEEvent(
			String idIn, String typeIn, String confidenceIn, String agentIn, String patientIn,  
			String destinationIn) {
		super(idIn,typeIn,confidenceIn);
		agent = agentIn;
		patient = patientIn;
		destination = destinationIn;
		eventsFileLine = null;
		startFrame = null;
		endFrame = null;
	}

	/**
	 * The <code>startsBefore</code> method specifies whether the first frame of this event comes
	 * before the given frame number.
	 *
	 * @param frameNumber is a number to compare with <code>startFrame</code>.
	 * @return <b><code>true</code></b> if and only if <code>startFrame</code> is less than 
	 * <code>frameNumber</code>. 
	 */
	public Boolean startsBefore(Integer frameNumber) {
		if (frameNumber == null) {
			System.err.println(
					"WARNING in MSEEEvent.startsBefore: A bug has been detected. Contact Ken Samuel.");
			return false;
		}
		if (startFrame == null) {			//This event doesn't have a start frame yet
			return false;
		}
		return (startFrame < frameNumber);
	}


	/**
	 * The <code>endsAfter</code> method specifies whether the last frame of this event comes
	 * after the given frame number.
	 *
	 * @param frameNumber is a number to compare with <code>endFrame</code>.
	 * @return <b><code>true</code></b> if and only if <code>endFrame</code> is greater than 
	 * <code>frameNumber</code>. 
	 */
	public Boolean endsAfter(Integer frameNumber) {
		if (frameNumber == null) {
			System.err.println(
					"WARNING in MSEEEvent.endsAfter: A bug has been detected. Contact Ken Samuel.");
			return false;
		}
		if (endFrame == null) {		//This event doesn't have an end frame yet
			return false;
		}
		return (endFrame > frameNumber);
	}
	
	/**
	 * The <code>writeInformationToRdfFile</code> method saves all of the information about this event to the 
	 * rdf output file in the proper format for that file. (The first line is handled by the superclass of 
	 * this event, <code>MSEEThing.writeThingToRdfFile()</code>.)
	 *
	 * @param out is the <code>PrintStream</code> that will be used to write data to the rdf output file.
	 */
	@Override
	protected void writeInformationToRdfFile(PrintStream out) {
		if (getAgent() != null) {
			out.println("\t<msee:hasAgent rdf:resource=\"#" + getAgent() + "\" />");
			//Example: <msee:hasAgent rdf:resource="#ed6134bb-ca8c-4325-93b1-789f053d2547" />
		} else {
			System.err.println(		//Every event should have an agent
					"WARNING in MSEEEvent.writeInformationToRdfFile: " +
					"A bug has been detected. Contact Ken Samuel.");
		}
		if (getPatient() != null) {
			String patient = "Unknown";
			if ( !getPatient().equals("") ) {
				patient = getPatient();
			}
			out.println("\t<msee:hasPatient rdf:resource=\"#" + patient + "\" />");				
			//Example: <msee:hasPatient rdf:resource="#ed6134bb-ca8c-4325-93b1-789f053d2547" />
		}
		if ((getDestination() != null) && ( ! getDestination().equals(""))) {
			out.println("\t<msee:hasDestination rdf:resource=\"#" + getDestination() + "\" />");
			//Example: <msee:hasDestination rdf:resource="#ed6134bb-ca8c-4325-93b1-789f053d2547" />
		}
		out.println("</msee:" + getType() + ">");
	}

	/**
	 * The <code>setAgent</code> setter changes the value of the global variable,
	 * <code>agent</code>, a <b><code>String</code></b>.
	 *
	 * @param agentIn is the new value that should be assigned to <code>agent</code>.
	 */
	public void setAgent(String agentIn) {
		agent = agentIn;
	}

	/**
	 * The <code>setPatient</code> setter changes the value of the global variable,
	 * <code>patient</code>, a <b><code>String</code></b>.
	 *
	 * @param patientIn is the new value that should be assigned to <code>patient</code>.
	 */
	public void setPatient(String patientIn) {
		patient = patientIn;
	}

	/**
	 * The <code>setDestination</code> setter changes the value of the global variable,
	 * <code>destination</code>, a <b><code>String</code></b>.
	 *
	 * @param destinationIn is the new value that should be assigned to <code>destination</code>.
	 */
	public void setDestination(String destinationIn) {
		destination = destinationIn;
	}

	/**
	 * The <code>setLine</code> setter changes the value of the global variable,
	 * <code>eventsFileLine</code>, a <b><code>String</code></b>.
	 *
	 * @param eventsFileLineIn is the new value that should be assigned to <code>locTime</code>.
	 */
	public void setLine(String eventsFileLineIn) {
		eventsFileLine = eventsFileLineIn;
	}

	/**
	 * The <code>setStartFrame</code> setter changes the value of the global variable,
	 * <code>startFrame</code>, a <b><code>Integer</code></b>.
	 *
	 * @param startFrameIn is the new value that should be assigned to <code>startFrame</code>.
	 */
	public void setStartFrame(Integer startFrameIn) {
		if (startFrameIn == 10341) {
			int x = 0;
		}
		startFrame = startFrameIn;
	}

	/**
	 * The <code>setEndFrame</code> setter changes the value of the global variable,
	 * <code>endFrame</code>, a <b><code>Integer</code></b>.
	 *
	 * @param endFrameIn is the new value that should be assigned to <code>lastFrame</code>.
	 */
	public void setEndFrame(Integer endFrameIn) {
		if (endFrameIn == 10341) {
			int x = 0;
		}
		endFrame = endFrameIn;
	}

	/**
	 * The <code>getAgent</code> getter returns the value of the global variable,
	 * <code>agent</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>agent</code>.
	 */
	public String getAgent() {
		return agent;
	}

	/**
	 * The <code>getPatient</code> getter returns the value of the global variable,
	 * <code>patient</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>patient</code>.
	 */
	public String getPatient() {
		return patient;
	}

	/**
	 * The <code>getDestination</code> getter returns the value of the global variable,
	 * <code>destination</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>destination</code>.
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * The <code>getLine</code> getter returns the value of the global variable,
	 * <code>eventsFileLine</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>locTime</code>.
	 */
	public String getLine() {
		return eventsFileLine;
	}

	/**
	 * The <code>getStartFrame</code> getter returns the value of the global variable,
	 * <code>startFrame</code>, a <b><code>Integer</code></b>.
	 *
	 * @return the value of <code>startFrame</code>.
	 */
	public Integer getStartFrame() {
		return startFrame;
	}

	/**
	 * The <code>getEndFrame</code> getter returns the value of the global variable,
	 * <code>endFrame</code>, a <b><code>Integer</code></b>.
	 *
	 * @return the value of <code>endFrame</code>.
	 */
	public Integer getEndFrame() {
		return endFrame;
	}
}