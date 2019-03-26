package mergeData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The <code>MergeEvents</code>  class is designed to correct redundancies in the ".rdf" and ".pt" files.
 * It was adapted from MergeEvents, and has similar functionality, but allows for multiple RDF and XML files (as opposed to
 * a single RDF file and multple XML files).
 *
 * @author Ken Samuel
 * @version 1.0, Mar 13, 2014
 * @since 1.6
 */
public class MergeEventsPhase3 {	
	/** 
	 * <code>events</code> is a sequences of hashes, in which each event's type points to the  
	 * identification code of its agent, which points to the identification code of its patient (if 
	 * any), which points to the identification code of its destination (if any), which points 
	 * to the event. 
	 */
	private static HashMap<String,HashMap<String,HashMap<String,HashMap<String,MSEEEvent>>>> events;
	
	/**
	 * The <code>MergeEvents</code> method removes the redundancies in the given files.
	 *
	 * @param rdfIn is the name of the input rdf file.
	 * @param rdfOut is the name of the output rdf file.
	 * @param inputFilestems is a list of the names of the input files that have location and time information 
	 * about the objects and events.
	 * @param outputFilestems is a list of the names of the output files that have location and time 
	 * information about the objects and events.
	 * @throws IOException if there's a problem with a file.
	 */
	public static void mergeEvents(
			String inputPath, String outputPath)
			throws IOException {
		BufferedReader rdfInput;			//The handle to access the input rdf file
		PrintStream rdfOutput;				//The handle to access the output rdf file
		String rdfIn;
		String rdfOut;
		String ptIn;						//One of the pt input files
		String ptOut;						//The corresponding pt output file
		BufferedReader eventsInput;			//The handle to access the input file for the events
		PrintStream eventsOutput;			//The handle to access the output file for the events
		Integer count;						//For counting loops
		
		File[] files = new File( inputPath ).listFiles();
		
		for ( int i = 0; i < files.length; i++ ) {
			if ( !files[i].getName().endsWith(Global.PARSE_GRAPH_FILE_SUFFIX) ) {
				continue;
			}
			
			String stem = files[i].getName().substring( 0, files[i].getName().length() - Global.PARSE_GRAPH_FILE_SUFFIX.length() );
			
			rdfIn = files[i].getAbsolutePath();
			rdfOut = outputPath + File.separator + files[i].getName();
			
			rdfInput = Global.openInputFile(rdfIn,true);
			rdfOutput = Global.openOutputFile(rdfOut);
			processRdfFile(rdfInput,rdfOutput);
			rdfInput.close();
			events = 																			//Initialize
					new HashMap<String, HashMap<String,HashMap<String,HashMap<String,MSEEEvent>>>>();
			ptIn = inputPath + File.separator + stem + Global.EVENT_FILE_SUFFIX;
			ptOut = outputPath + File.separator + stem + Global.EVENT_FILE_SUFFIX;
			eventsInput = Global.openInputFile(ptIn,true);
			eventsOutput = Global.openOutputFile(ptOut);
			processEventsFile(eventsInput,eventsOutput,rdfOutput);
			eventsInput.close();
			eventsOutput.close();
			
			rdfOutput.println("</rdf:RDF>");
			rdfOutput.close();
			
			Global.copyFile(
					inputPath + File.separator + stem + Global.OBJECT_FILE_SUFFIX,
					outputPath + File.separator + stem + Global.OBJECT_FILE_SUFFIX);
		}
	}
	
	/**
	 * The <code>processRdfFile</code> method goes through the rdf file and writes everything except the 
	 * events to the output rdf file. (The events will be added in <code>processEventsFile()</code>.
	 *
	 * @param in is a <code>BufferedReader</code> that can be used to access the data in the input rdf file.
	 * @param out is a <code>PrintStream</code> that can be used to write data to the output rdf file.
	 * @throws IOException if there's a problem with the files.
	 */
	private static void processRdfFile(BufferedReader in, PrintStream out) throws IOException {
		String line;							//One line of text from the input rdf file
		ArrayList<String> lines;				//The lines of text that describe a particular thing
		Boolean finishedHeaders;				//Specifies if we are done reading the rdf file's header lines
		String type;							//Its type, such as Human
		String id;								//Its identification number
		Boolean loop;							//Specifies whether a while loop should continue looping 
		
		finishedHeaders = false;					//Initialize		
		while ((line = in.readLine()) != null) {			//Load the next line from the file
			if (line.matches("\\s*<msee:\\S+\\s+rdf:about=\"#[^\"\\s]+\">\\s*")) {
				//Beginning of thing: <msee:Human rdf:about="#138022bb-a29e-433f-b24c-4515836daf33">
				lines = new ArrayList<String>(5);	//Initialize
				finishedHeaders = true;
				type = Global.getSubstring(line,"\\s*<msee:","\\s");
				id = Global.getSubstring(line,"rdf:about=\"#","\"");
				lines.add(line);
				loop = true;					//Initialize
				//Collect the lines for the thing
				while (loop) {
					line = in.readLine();
					if (line == null) {
						System.err.println(
								"WARNING in MergeData.processRdfFile: Couldn't find a </msee:" + type + 
								"> tag for id=\"" + id + "\".");
						break;
					} else if (line.matches("\\s*<msee:hasAgent\\s.*")) {//If it has an agent, it's an event
						//Agent: <msee:hasAgent rdf:resource="#obj-person1" />
						lines = new ArrayList<String>(0);	//We won't save this until processEventsFile()
						while ( ! line.matches("\\s*</msee:" + type + ">\\s*")) {//Skip the rest of the event
							line = in.readLine();							
						}
						loop = false;
					} else if (line.matches("\\s*</msee:" + type + ">\\s*")) {
						//End of thing: </msee:Human>
						loop = false;
						lines.add(line);
					} else {
						lines.add(line);
					}
				}
				Global.printLines(out,lines);			//Save the result to the rdf output file
			} else if (finishedHeaders == false) {
				out.println(line);
			} else if ( ! line.matches("\\s*</rdf:RDF>\\s*")) {	//Ignore the last line of the file
				// ! Last line: </rdf:RDF>
				System.err.println(
						"WARNING in MergeData.processRdfFile: Found an unexpected line in the rdf file:");
				System.err.println(line);
			}
		}
	}
	
	/**
	 * The <code>processEventsFile</code> method goes through the events input file, modifies it, and saves 
	 * the results to the events output file. It also finishes saving data to the rdf output file.
	 *
	 * @param in is a <code>BufferedReader</code> that can be used to access the data in the events input 
	 * file.
	 * @param out is a <code>PrintStream</code> that can be used to write data to the events output file.
	 * @param rdfOut is a <code>PrintStream</code> that can be used to write data to the rdf output file.
	 * @throws IOException if there's a problem with the files.
	 */
	private static void processEventsFile(BufferedReader in, PrintStream out, PrintStream rdfOut)
			throws IOException {
		String line;						//One line of text from the input events file
		MSEEEvent event;					//An event
		String id;							//Its identification code
		String type;						//Its type
		String agent;						//Its agent's identification code
		String patient;						//Its patient's identification code (if any)
		String destination;					//Its third argument's identification code (if any)
		String position;					//The view-centric location of the event
		String confidence;					//The confidence that the event was correctly detected
		Integer startFrame;					//The frame in which the event begins
		Integer endFrame;					//The frame in which the event ends
		MSEEEvent newEvent;					//A newly created event
		MSEEEvent duplicateEvent;			//An event that's identical to an event except for its line
		String eventLine;					//A string that will be saved in the events output file

		while ((line = in.readLine()) != null) {			//Load the next line from the file
			if (line.matches("\\s*<action\\s.*")) {
				//An event: <action id="8b2e64f8" name="Stationary" agent="8b2e64f8" patient="" object="" 
				//           position="0,0,0,1,1,0,1,1" confidence="1.0" begin_frame="0" end_frame="9"/>
				id = Global.getSubstring(line,"\\sid=\"","\"");
				type = Global.getSubstring(line,"\\sname=\"","\"");
				agent = Global.getSubstring(line,"\\sagent=\"","\"");
				patient = Global.getSubstring(line,"\\spatient=\"","\"");
				destination = Global.getSubstring(line,"\\sobject=\"","\"");
				position = Global.getSubstring(line,"\\sposition=\"","\"");
				confidence = Global.getSubstring(line,"\\sconfidence=\"","\"");
				startFrame = Integer.valueOf(Global.getSubstring(line,"\\sbegin_frame=\"","\""));
				endFrame = Integer.valueOf(Global.getSubstring(line,"\\send_frame=\"","\""));
					event = getEvent(type,agent,patient,destination);	//Search in the events hash
					if (event == null) {
						event = new MSEEEvent(id,type,confidence,agent,patient,destination);
						if (events.containsKey(type)) {
							if (events.get(type).containsKey(agent)) {
								if (events.get(type).get(agent).containsKey(patient)) {
									if (events.get(type).get(agent).get(patient).containsKey(destination)) {
										System.err.println(	//event is null, so this shouldn't be possible
												"DEBUG in MergeData.processEventsFile: " +
												"Bug detected. Contact Ken Samuel.");
									} else {
										events.get(type).get(agent).get(patient).put(destination,event);
									}
								} else {
									events.get(type).get(agent).put(patient,new HashMap<String,MSEEEvent>());
									events.get(type).get(agent).get(patient).put(destination,event);
								}
							} else {
								events.get(type).put(agent,new HashMap<String,HashMap<String,MSEEEvent>>());
								events.get(type).get(agent).put(patient,new HashMap<String,MSEEEvent>());
								events.get(type).get(agent).get(patient).put(destination,event);
							}
						} else {
							events.put(type,new HashMap<String,HashMap<String,HashMap<String,MSEEEvent>>>());
							events.get(type).put(agent,new HashMap<String,HashMap<String,MSEEEvent>>());
							events.get(type).get(agent).put(patient,new HashMap<String,MSEEEvent>());
							events.get(type).get(agent).get(patient).put(destination,event);
						}
						event.writeThingToRdfFile(rdfOut);
					}
				if 	(event.getEndFrame() != null) {
					while (
							(event.getDuplicate() != null) &&
							(
									(startFrame <= event.getStartFrame()) || 
									(endFrame > event.getEndFrame() + Global.ERROR_RANGE))) {
						event = (MSEEEvent)event.getDuplicate();			//Wrong event; try the next one
					}
					if ( startFrame < event.getStartFrame() ) { // An event is out of temporal order
						System.err.println( "Time out of order: " + line );
					}
					if (
							(startFrame <= event.getStartFrame()) ||
							(endFrame > event.getEndFrame() + Global.ERROR_RANGE)) {//Need new event
						newEvent = new MSEEEvent(id,type,confidence,agent,patient,destination);
						event.setDuplicate(newEvent);
						eventLine = event.getLine();
						eventLine = Global.replaceSubstring(eventLine,"\\sid=\"","\"",id);
						newEvent.setLine(eventLine);
						event = newEvent;								
						event.writeThingToRdfFile(rdfOut);
					}
				}
				setLine(event,line,id,agent,patient,destination,position,confidence);
				if ( ! event.startsBefore(startFrame)) {
					event.setStartFrame(startFrame);
				}
				if ( ! event.endsAfter(endFrame)) {
					event.setEndFrame(endFrame);
				}
			 } else if (line.matches("\\s*</events>\\s*")) {
				 //End of all events: </events>
				 for (HashMap<String,HashMap<String,HashMap<String,MSEEEvent>>> eventHash1 : events.values()){
					 for (HashMap<String,HashMap<String,MSEEEvent>> eventHash2 : eventHash1.values()) {
						 for (HashMap<String,MSEEEvent> eventHash3 : eventHash2.values()) {
							 for (MSEEEvent anEvent : eventHash3.values()) {
								 duplicateEvent = anEvent;
								 while (duplicateEvent != null) {
									 eventLine = duplicateEvent.getLine();
									 eventLine = Global.replaceSubstring(
											 eventLine,"\\sbegin_frame=\"","\"",
											 duplicateEvent.getStartFrame().toString());
									 eventLine = Global.replaceSubstring(
											 eventLine,"\\send_frame=\"","\"",
											 duplicateEvent.getEndFrame().toString());
									 out.println(eventLine);
									 duplicateEvent = (MSEEEvent)duplicateEvent.getDuplicate();
								 }
							 }
						 }
					 }
				 }
				 out.println(line);
			 } else {
				 out.println(line);
			 }
		}
	}

	/**
	 * The <code>setLine</code> method creates the line of text that will be saved to the events file for a
	 * given event and saves it on that event's object.
	 *
	 * @param event is the event that the line describes.
	 * @param line is the line in its original form in the events file.
	 * @param id is the identification code of the event.
	 * @param agent is the identification code of the event's agent.
	 * @param patient is the identification code of the event's patient (if any).
	 * @param destination is the identification code of the event's third argument (if any).
	 * @param position is the view-centric location of the event.
	 * @param confidence is the confidence that the event was correctly detected.
	 */
	private static void setLine(
			MSEEEvent event, String line, String id, String agent, String patient, 
			String destination, String position, String confidence) {
		String newLine;						//The line that will be stored in the event

		newLine = Global.replaceSubstring(line,"\\sid=\"","\"",id);
		newLine = Global.replaceSubstring(newLine,"\\sagent=\"","\"",agent);
		newLine = Global.replaceSubstring(newLine,"\\spatient=\"","\"",patient);
		newLine = Global.replaceSubstring(newLine,"\\sobject=\"","\"",destination);
		newLine = Global.replaceSubstring(newLine,"\\sposition=\"","\"",position);
		if (Global.isOne(confidence)) {
			newLine = Global.removeSubstring(newLine,"\\sconfidence=\"[^\\s\"]+\"");
		} else if ( ! confidence.equals("")) {
			newLine = Global.replaceSubstring(newLine,"\\sconfidence=\"","\"",confidence);
		}
		event.setLine(newLine);
	}
	
	/**
	 * The <code>getEvent</code> method returns the event in <code>events</code> that has the given type,
	 * agent, patient, and destination. If none is found, then <b><code>null</code></b> is returned.
	 *
	 * @param type is the type of the desired event, such as "Move".
	 * @param agent is the agent of the desired event.
	 * @param patient is the patient of the desired event (if any). If it is <b><code>null</code></b>, that
	 * means this event does not have a patient.
	 * @param destination is the third argument of the desired event (if any). If it is 
	 * <b><code>null</code></b>, that means this event does not have a third argument.
	 * @return the event in <code>events</code>  that matches the parameter values, or 
	 * <b><code>null</code></b> if none exists.
	 */
	private static MSEEEvent getEvent(String type, String agent, String patient, String destination) {
		if (
				events.containsKey(type) &&
				events.get(type).containsKey(agent) &&
				events.get(type).get(agent).containsKey(patient) &&
				events.get(type).get(agent).get(patient).containsKey(destination)) {
			return events.get(type).get(agent).get(patient).get(destination);
		}
		return null;
	}
	
	/**
	 * The <code>main</code> method is where the program begins.
	 *
	 * @param args are ignored.
	 * @throws Exception if there are any problems.
	 */
	public static void main(String[] args) throws Exception {
//		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\soc-sig-office-2013-09-04-testing\\original",
//				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\soc-sig-office-2013-09-04-testing\\merged" );
		
//		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-01-04-Testing\\original",
//				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-01-04-Testing\\merged" );
		
//		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-10-18-Testing\\original",
//				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-10-18-Testing\\merged" );
		
//		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\PrattGarden-2014-09-20-Testing\\original",
//				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\PrattGarden-2014-09-20-Testing\\merged" );
		
		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\1_soc-Schiciano-2014-02-22-Testing-Evacuation\\original",
				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\1_soc-Schiciano-2014-02-22-Testing-Evacuation\\merged" );
		
		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\2_soc-PrattGarden-2014-09-20-Testing-Fashion\\original",
				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\2_soc-PrattGarden-2014-09-20-Testing-Fashion\\merged" );

		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\3_soc-PrattGarden-2014-09-20-Testing-Sport\\original",
				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\3_soc-PrattGarden-2014-09-20-Testing-Sport\\merged" );

		mergeEvents( "C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\4_soc-sig-parking-lot-2013-09-28-testing-MC\\original",
				"C:\\MSEE_2\\msee\\data\\SIG_DATA2\\Phase3DistributableTestingData\\4_soc-sig-parking-lot-2013-09-28-testing-MC\\merged" );

		
		System.out.println("All done.");
	}
}