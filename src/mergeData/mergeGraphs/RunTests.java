package mergeData.mergeGraphs;




/**
 * The <code>RunTests</code> tests the GraphMerger system.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 4, 2015
 * @since 1.6
 */
public class RunTests {
	
	/** 
	 * <code>videoGraphFilename</code> is the name of the input file with the graph that was built from 
	 * analyzing videos. It should be in RDF format.
	 */
	private static final String videoGraphFilename =
			//"data\\ImageAudioAnnotation_Project\\graphMerger.data\\video\\small_test\\graph.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\video\\small_test\\graph.rdf";
			"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\src\\java\\TextParser\\data\\ImageAudioAnnotation_Project\\graphMerger.data\\video\\demo\\ypo-indoor.rdf";

	/** 
	 * <code>textGraphFilename</code> is the name of the input file with the graph that was built from 
	 * analyzing text. It should be in RDF format.
	 */
	private static final String textGraphFilename = 
			//"data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\small_test\\graph.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\text\\small_test\\graph.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\text\\small_test\\graph.modified.rdf";
			"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\src\\java\\TextParser\\data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\demo\\indoor.modified.rdf";

	/** 
	 * <code>mergedGraphFilename</code> is the name of the output file with the graph that will be produced
	 * by merging <code>videoGraphFilename</code> and <code>textGraphFilename</code>. It will be in RDF 
	 * format.
	 */
	private static final String mergedGraphFilename = 
			//"data\\ImageAudioAnnotation_Project\\graphMerger.data\\merged\\small_test\\graph.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\merged\\small_test\\graph.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\merged\\small_test\\graph.modified.rdf";
			"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\src\\java\\TextParser\\data\\ImageAudioAnnotation_Project\\graphMerger.data\\merged\\demo\\indoor.merged.rdf";

	/** 
	 * <code>ontologyFilename</code> is the name of the input file with the ontology that is used by the other
	 * graphs. It should be in OWL format.
	 */
	private static final String ontologyFilename = 
			//"data\\ImageAudioAnnotation_Project\\graphMerger.data\\ontologies\\small_test\\msee_lite.owl";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\ontologies\\small_test\\msee_lite.owl";
			"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\src\\java\\TextParser\\data\\ImageAudioAnnotation_Project\\graphMerger.data\\ontologies\\demo\\demo.owl";

	/** 
	 * <code>defaultMinNumMatches</code> is the minimum number of characteristics of two nodes that must match 
	 * in order to merge the nodes. 
	 */
	private static final Integer defaultMinNumMatches = 1;
	
	
	/**
	 * The <code>go</code> method runs the selected tests.
	 *
	 * @param args can (optionally) have a value for the minimum number of characteristics of two nodes that 
	 * must match in order to merge the nodes.
	 * @throws Exception if there are any problems.
	 */
	public static void go(String[] args) throws Exception {
		GraphMerger graphMerger;		//The object that provides access to the rest of the program
		Integer minNumMatches;			//The minimum number of characteristics of two nodes that must match 
		
		graphMerger = new GraphMerger();
		minNumMatches = defaultMinNumMatches;
		if (args.length > 0) {
			try {
				minNumMatches = Integer.parseInt(args[0]);
			} catch(NumberFormatException exception) {
				throw new NumberFormatException("The argument must be an integer.");
			}
		}
		graphMerger.mergeGraphs(
				videoGraphFilename,textGraphFilename,mergedGraphFilename,ontologyFilename,minNumMatches);
	}

	/**
	 * The <code>main</code> method is where the program begins.
	 *
	 * @param args can (optionally) have a value for the minimum number of characteristics of two nodes that 
	 * must match in order to merge the nodes. .
	 * @throws Exception if there are any problems.
	 */
	public static void main(String[] args) throws Exception {
		GraphMerger graphMerger;		//The object that provides access to the rest of the program
		Integer minNumMatches;			//The minimum number of characteristics of two nodes that must match 
		
		graphMerger = new GraphMerger();
		minNumMatches = defaultMinNumMatches;
		if (args.length > 0) {
			try {
				minNumMatches = Integer.parseInt(args[0]);
			} catch(NumberFormatException exception) {
				throw new NumberFormatException("The argument must be an integer.");
			}
		}
		graphMerger.mergeGraphs(
				videoGraphFilename,textGraphFilename,mergedGraphFilename,ontologyFilename,minNumMatches);
	}
}