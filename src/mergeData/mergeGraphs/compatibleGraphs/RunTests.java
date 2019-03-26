package mergeData.mergeGraphs.compatibleGraphs;

/**
 * The <code>RunTests</code> tests the GraphMerger system.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 4, 2015
 * @since 1.6
 */
public class RunTests {
	
	/** 
	 * <code>originalGraphFilename</code> is the name of the input file with the graph that was built from 
	 * analyzing text. It should be in RDF format.
	 */
	private static final String originalGraphFilename = 
			//"data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\small_test\\a_fair_test.original.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\text\\small_test\\a_fair_test.original.rdf";
			"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\src\\java\\TextParser\\data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\demo\\indoor.rdf";
	
	/** 
	 * <code>modifiedGraphFilename</code> is the name of the output file with the graph that is compatible
	 * with graphs produced by analyzing videos. It will be in RDF format.
	 */
	private static final String modifiedGraphFilename = 
			//"data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\small_test\\a_fair_test.modified.rdf";
			//"C:\\0rg\\work\\projects\\Image_Audio_Annotation\\data\\graphMerger.data\\text\\small_test\\a_fair_test.modified.rdf";
			"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\src\\java\\TextParser\\data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\demo\\indoor.modified.rdf";
	

	/**
	 * The <code>go</code> method runs the selected tests.
	 *
	 * @param args are ignored. 
	 * @throws Exception if there are any problems.
	 */
	public static void go(@SuppressWarnings("unused") String[] args) throws Exception {
		CompatibleGraphs me;		//The object that provides access to the rest of the program
		
		me = new CompatibleGraphs();
		me.modifyGraph(originalGraphFilename,modifiedGraphFilename);
		System.out.println("All done.");
	}
}