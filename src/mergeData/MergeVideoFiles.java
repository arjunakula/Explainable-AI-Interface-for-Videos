package mergeData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The <code>MergeVideoFiles</code> class analyzes "rdf", "ol", and "pt" files that were generated from a 
 * video that had been broken up into multiple files, removing redundancies in stationary objects, and writing
 * the modified results to output "rdf", "ol", and "pt" files.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 28, 2014
 * @since 1.6
 */
public class MergeVideoFiles {
	
	/** 
	 * <code>FIRST_FILES</code> is a list of the indexes of the first file to process in 
	 * <code>INPUT_OBJECT_FILES</code>, <code>OUTPUT_OBJECT_FILES</code>, <code>INPUT_EVENT_FILES</code>, 
	 * and <code>OUTPUT_EVENT_FILES</code>. For each element <i>n</i> in this list, the program will run 
	 * <code>mergeFilesForAVideo()</code> beginning with the <i>n</i>th file in each of the other lists. 
	 * The order of the elements in this list matches the order of the corresponding elements in the
	 * <code>LAST_FILES</code> list.   
	 */
	private static final ArrayList<Integer> FIRST_FILES = new ArrayList<Integer>(Arrays.asList(
			//XXX Uncomment one of the following three lines. XXX\\
			//0,1,10,18,26,27,30,31,39,47,48,53		//Office
			54,59,64,69,74,75,76,77,80				//Parking lot
			//81,86,91,96,101,102,103,104			//Garden
			));

	/** 
	 * <code>LAST_FILES</code> is a list of the indexes of the last file to process in 
	 * <code>INPUT_OBJECT_FILES</code>, <code>OUTPUT_OBJECT_FILES</code>, <code>INPUT_EVENT_FILES</code>, 
	 * and <code>OUTPUT_EVENT_FILES</code>. For each element <i>n</i> in this list, the program will run 
	 * <code>mergeFilesForAVideo()</code> ending with the <i>n</i>th file in each of the other lists. The
	 * order of the elements in this list matches the order of the corresponding elements in the
	 * <code>FIRST_FILES</code> list.  
	 */
	private static final ArrayList<Integer> LAST_FILES = new ArrayList<Integer>(Arrays.asList(
			//XXX Uncomment one of the following three lines. XXX\\
			//0,9,17,25,26,29,30,38,46,47,52,53		//Office
			58,63,68,73,74,75,76,79,80				//Parking lot
			//85,90,95,100,101,102,103,104			//Garden
			));

	/** 
	 * <code>EVENT_FILE_BEGINNINGS</code>  is a list of strings that precede identification codes in the 
	 * <code>INPUT_EVENT_FILES</code>. 
	 */
	private static final ArrayList<String> EVENT_FILE_BEGINNINGS = new ArrayList<String>(Arrays.asList(
			"\\sid=\"",
			"\\sagent=\"",
			"\\spatient=\"",
			"\\sobject=\""));
	
	/** 
	 * <code>EVENT_FILE_ENDINGS</code>  is a list of strings that follow identification codes in the 
	 * <code>INPUT_EVENT_FILES</code>.  The <code>EVENT_FILE_BEGINNINGS</code> and
	 * <code>EVENT_FILE_ENDINGS</code> lists are ordered so that each beginning string is matched with the 
	 * corresponding ending string. 
	 */
	private static final ArrayList<String> EVENT_FILE_ENDINGS = new ArrayList<String>(Arrays.asList(
			"\"",
			"\"",
			"\"",
			"\""));
	
	/** 
	 * <code>RDF_FILE_BEGINNINGS</code>  is a list of strings that precede identification codes in the 
	 * <code>INPUT_RDF_FILE</code>. 
	 */
	private static final ArrayList<String> RDF_FILE_BEGINNINGS = new ArrayList<String>(Arrays.asList(
			"\\srdf:about=\"#",
			"\\srdf:resource=\"#"
			));
	
	/** 
	 * <code>RDF_FILE_ENDINGS</code>  is a list of strings that follow identification codes in the 
	 * <code>INPUT_RDF_FILE</code>.  The <code>RDF_FILE_BEGINNINGS</code> and
	 * <code>RDF_FILE_ENDINGS</code> lists are ordered so that each beginning string is matched with the 
	 * corresponding ending string. 
	 */
	private static final ArrayList<String> RDF_FILE_ENDINGS = new ArrayList<String>(Arrays.asList(
			"\"",
			"\""
			));
	
	/** 
	 * <code>stationaryObjects</code> is a hash of hashes with all of the stationary objects found in the 
	 * first file on the <code>INPUT_OBJECT_FILES</code> list. The object's type points to its contour, which
	 * points to its identification code. 
	 */
	private static HashMap<String,HashMap<String,String>> stationaryObjects;
	
	/** 
	 * <code>idTranslations</code> is a hash in which each object identifier code that needs to be changed 
	 * points to the object identifier code that it should be changed to. 
	 */
	private static HashMap<String,String> idTranslations;
	
	
	/**
	 * The <code>mergeFilesForVideos</code> method merges stationary objects found in the data for different 
	 * parts of each video that is specified by <code>FIRST_FILES</code> and <code>LAST_FILES</code>.
	 * @throws IOException if there's a problem with a file.
	 */
	private static void mergeFilesForVideos() throws IOException {
		Integer count;					//For looping through arrays

		Global.copyFile(Global.INTERMEDIATE_RDF_FILE,Global.INTERMEDIATE_RDF_FILE+".bak");
		for (count = 0; count < FIRST_FILES.size(); count++) {
			mergeFilesForAVideo(FIRST_FILES.get(count),LAST_FILES.get(count));
			Global.copyFile(Global.OUTPUT_RDF_FILE,Global.INTERMEDIATE_RDF_FILE);
		}
	}
	
	/**
	 * The <code>mergeFilesForAVideo</code> method merges stationary objects found in the data for different 
	 * parts of a single video. 
	 * 
	 * @param firstFile is the index of the first file in each of the file lists 
	 * (<code>INPUT_OBJECT_FILES</code>, <code>OUTPUT_OBJECT_FILES</code>, <code>INPUT_EVENT_FILES</code>, 
	 * and <code>OUTPUT_EVENT_FILES</code>) that corresponds to the video to process.
	 * @param lastFile is the index of the last file in each of the file lists 
	 * (<code>INPUT_OBJECT_FILES</code>, <code>OUTPUT_OBJECT_FILES</code>, <code>INPUT_EVENT_FILES</code>, 
	 * and <code>OUTPUT_EVENT_FILES</code>) that corresponds to the same video as <code>firstFile</code>.
	 * @throws IOException if there's a problem with a file.
	 */
	private static void mergeFilesForAVideo(Integer firstFile, Integer lastFile) throws IOException {
		processFirstObjectFile(firstFile);
		processOtherObjectFiles(firstFile+1,lastFile);
		processEventFiles(firstFile,lastFile);
		processRdfFile();
	}

	/**
	 * The <code>processFirstObjectFile</code> method extracts information about the stationary objects in 
	 * the specified file on the <code>INPUT_OBJECT_FILES</code> list. It also copies this file, unchanged, to 
	 * the specified file on the <code>OUTPUT_OBJECT_FILES</code> list.
	 * 
	 * @param fileIndex is the index of the file in each of the file lists 
	 * (<code>INPUT_OBJECT_FILES</code> and <code>OUTPUT_OBJECT_FILES</code>) that this method should process.
	 * @throws IOException if there's a problem with a file.
	 */
	private static void processFirstObjectFile(Integer fileIndex) throws IOException {
		String inFile, outFile;			//The first input and output object files
		BufferedReader in;				//The handle to access the input file
		PrintStream out;				//The handle to access the output file
		String line;					//A line of text from the input file
		String positionLine;			//A line of text from the input file with information about an object
		String id;						//The identification code of an object
		String type;					//The type of that object, such as "Human"
		String position;				//The first position contour (location) of that object
		
		stationaryObjects = new HashMap<String,HashMap<String,String>>();			//Initialize
		inFile = Global.INTERMEDIATE_FILESTEMS.get(fileIndex) + Global.OBJECT_FILE_SUFFIX;
		outFile = Global.OUTPUT_FILESTEMS.get(fileIndex) + Global.OBJECT_FILE_SUFFIX;
		in = Global.openInputFile(inFile,true);
		out = Global.openOutputFile(outFile);
		while ((line = in.readLine()) != null) {			//Load the next line from the file
			if (line.matches("\\s*<object id=\"[^\"\\s]+\"\\s+label=\"[^\"\\s]+\".*>\\s*")) {
				//Beginning of an object: <object id="view-BR2-video-0_Ground_2_2" label="Ground" >
				out.println(line);
				id = Global.getSubstring(line,"\\sid=\"","\"");
				type = Global.getSubstring(line,"\\slabel=\"","\"");
				positionLine = in.readLine();
				out.println(positionLine);
				line = in.readLine();
				if (line.matches("\\s*</object>\\s*")) {		//A stationary object?
					//End of stationary object: </object>
					position = Global.getSubstring(positionLine,"\\scontour=\"","\"");
					if ( ! stationaryObjects.containsKey(type)) {		//Add it to stationaryObjects
						stationaryObjects.put(type,new HashMap<String,String>());
					}
					if ( ! stationaryObjects.get(type).containsKey(position)) {
						stationaryObjects.get(type).put(position,id);
					} else {
						System.err.println(
								"WARNING in MergeVideoFiles.processFirstObjectFile: Found two objects " +
								"with type = \"" + type + "\" and position = \"" + position + 
								"\" in \"" + inFile + "\".");
					}
				}
			}
			out.println(line);
		}
		in.close();
		out.close();
	}

	/**
	 * The <code>processOtherObjectFiles</code> method modifies all of the specified files in the 
	 * <code>INPUT_OBJECT_FILES</code> list. The identification code of each stationary 
	 * object in each file is changed to the identification code of the corresponding object in the file that
	 * was processed by <code>processFirstObjectFile()</code>. Each modified data set is saved to the  
	 * corresponding file on the <code>OUTPUT_OBJECT_FILES</code> list. 
	 * 
	 * @param firstFile is the index of the first file in each of the file lists 
	 * (<code>INPUT_OBJECT_FILES</code> and <code>OUTPUT_OBJECT_FILES</code>) that this method should process.
	 * @param lastFile is the index of the last file in each of the file lists 
	 * (<code>INPUT_OBJECT_FILES</code> and <code>OUTPUT_OBJECT_FILES</code>) that this method should process.
	 * @throws IOException if there's a problem with a file.
	 */
	private static void processOtherObjectFiles(Integer firstFile, Integer lastFile) throws IOException {
		String inFile, outFile;			//A pair of input and output object files
		BufferedReader in;				//The handle to access the input file
		PrintStream out;				//The handle to access the output file
		String line;					//A line of text from the input file
		String positionLine;			//A line of text from the input file with information about an object
		String nextLine;				//The line of text following the position line
		String id;						//The identification code of an object
		String type;					//The type of that object, such as "Human"
		String position;				//The first position contour (location) of that object
		String targetId;				//The identification code that id should be translated into
		Integer count;					//For looping through arrays
		
		idTranslations = new HashMap<String,String>();								//Initialize
		for (count = firstFile; count <= lastFile; count++) {
			inFile = Global.INTERMEDIATE_FILESTEMS.get(count) + Global.OBJECT_FILE_SUFFIX;
			outFile = Global.OUTPUT_FILESTEMS.get(count) + Global.OBJECT_FILE_SUFFIX;
			in = Global.openInputFile(inFile,true);
			out = Global.openOutputFile(outFile);
			while ((line = in.readLine()) != null) {			//Load the next line from the file
				if (line.matches("\\s*<object id=\"[^\"\\s]+\"\\s+label=\"[^\"\\s]+\".*>\\s*")) {
					//Beginning of an object: <object id="view-BR2-video-0_Ground_2_2" label="Ground" >
					id = Global.getSubstring(line,"\\sid=\"","\"");
					type = Global.getSubstring(line,"\\slabel=\"","\"");
					positionLine = in.readLine();
					nextLine = in.readLine();
					if (													//A stationary object?
							(nextLine.matches("\\s*</object>\\s*")) &&
							( ! positionLine.matches(".*\\sframe=.*")) ) {	//Not if it has a frame number
						//End of stationary object: </object>
						position = Global.getSubstring(positionLine,"\\scontour=\"","\"");
						if (													//Is there a match?
								(stationaryObjects.containsKey(type)) && 
								(stationaryObjects.get(type).containsKey(position))) {
							targetId = stationaryObjects.get(type).get(position);
							idTranslations.put(id,targetId);
							line = Global.replaceSubstring(line,"\\sid=\"","\"",targetId);		//Translate
						} else {
							System.err.println(
									"WARNING in MergeVideoFiles.processOtherObjectFiles: Found an object " +
									"with type = \"" + type + "\" and position = \"" + position + 
									"\" in \"" + inFile + "\", but unable to find a matching object in \"" + 
									Global.INTERMEDIATE_FILESTEMS.get(0) + Global.OBJECT_FILE_SUFFIX + "\".");
							if ( ! stationaryObjects.containsKey(type)) {		//Add it to stationaryObjects
								stationaryObjects.put(type,new HashMap<String,String>());
							}
							if ( ! stationaryObjects.get(type).containsKey(position)) {
								stationaryObjects.get(type).put(position,id);
							}
						}
					}
					out.println(line);
					out.println(positionLine);
					out.println(nextLine);
				} else {
					out.println(line);
				}
			}
			in.close();
			out.close();
		}
	}
	
	/**
	 * The <code>processEventFiles</code> method modifies all of the specified files in the 
	 * <code>INPUT_EVENT_FILES</code> list. The identification code of each stationary object in each file is 
	 * changed to the identification code of the corresponding object in the file that was processed by 
	 * <code>processFirstObjectFile()</code>. Each set of modified data is saved to the corresponding file on 
	 * the <code>OUTPUT_EVENT_FILES</code> list.
	 * 
	 * @param firstFile is the index of the first file in each of the file lists 
	 * (<code>INPUT_EVENT_FILES</code> and <code>OUTPUT_EVENT_FILES</code>) that this method should process.
	 * @param lastFile is the index of the last file in each of the file lists 
	 * (<code>INPUT_EVENT_FILES</code> and <code>OUTPUT_EVENT_FILES</code>) that this method should process.
	 * @throws IOException if there's a problem with a file.
	 */
	private static void processEventFiles(Integer firstFile, Integer lastFile) throws IOException {
		Integer count;					//For looping through arrays

		for (count = firstFile; count <= lastFile; count++) {
			processFile(
					Global.INTERMEDIATE_FILESTEMS.get(count) + Global.EVENT_FILE_SUFFIX,
					Global.OUTPUT_FILESTEMS.get(count) + Global.EVENT_FILE_SUFFIX,
					EVENT_FILE_BEGINNINGS,EVENT_FILE_ENDINGS);
		}
	}
	
	/**
	 * The <code>processRdfFile</code> method modifies the  <code>INPUT_RDF_FILE</code> file. The 
	 * identification code of each stationary object in the file is changed to the identification code of the 
	 * corresponding object in the first file on the <code>INPUT_OBJECT_FILES</code> list. The modified data 
	 * is saved to the <code>OUTPUT_RDF_FILE</code>.
	 * 
	 * @throws IOException if there's a problem with a file.
	 */
	private static void processRdfFile() throws IOException {
		processFile(Global.INTERMEDIATE_RDF_FILE,Global.OUTPUT_RDF_FILE,RDF_FILE_BEGINNINGS,RDF_FILE_ENDINGS);
	}
	
	/**
	 * The <code>processFile</code> method modifies the given input file. It translates the identification
	 * codes of stationary objects as specified in the <code>idTranslations</code> hash. The modified data is 
	 * saved to the given output file.
	 * 
	 * @param inFile is the name of the file to read from.
	 * @param outFile is the name of the file to write to.
	 * @param beginnings is a list of strings that precede identification codes.
	 * @param endings is a list of strings that follow identification codes. The <code>beginnings</code> and
	 * <code>endings</code> lists are ordered so that each beginning string is matched with the corresponding
	 * ending string. 
	 * @throws IOException if there's a problem with a file.
	 */
	private static void processFile(
			String inFile, String outFile, ArrayList<String> beginnings, ArrayList<String> endings) 
			throws IOException {
		BufferedReader in;				//The handle to access the input file
		PrintStream out;				//The handle to access the output file
		String line;					//A line of text from the input file
		String beginning,ending;		//A beginning string and the corresponding ending string
		String id;						//The identification code of an object
		String targetId;				//The identification code that id should be translated into
		Integer count;					//For looping through arrays

		in = Global.openInputFile(inFile,true);
		out = Global.openOutputFile(outFile);
		while ((line = in.readLine()) != null) {			//Load the next line from the file
			for (count = 0; count < beginnings.size(); count++) {
				beginning = beginnings.get(count);
				ending = endings.get(count);
				if (line.matches(".*" + beginning + ".*" + ending + ".*")) {
					id = Global.getSubstring(line,beginning,ending);
					if (idTranslations.containsKey(id)) {
						targetId = idTranslations.get(id);
						line = Global.replaceSubstring(line,beginning,ending,targetId);		//Translate
					}
				}
			}
			out.println(line);		
		}
		in.close();
		out.close();
	}
	
	/**
	 * The <code>main</code> method is where the program begins.
	 *
	 * @param args are ignored.
	 * @throws Exception if there are any problems.
	 */
	public static void main(String[] args) throws Exception {
		mergeFilesForVideos();
		System.out.println("All done.");
	}
}