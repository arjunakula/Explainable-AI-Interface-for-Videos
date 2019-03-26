/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

package TextParser;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


class TextParserApp extends JPanel {

	private JEditorPane Pane;
	private JTree tree;

	// private TextParserEngine textParserEngine = null;
	private TextParserMultiLine textParserMultiLine = null;
	
	public TextParserApp() {
		super(new GridLayout(1, 0));		
	}
	
	/**
	 * Parses the provided sentence, and returns the generated RDF as a string, or null, if there was a parsing error.
	 * This function will generate a number of files in resultroot_folder, containing the results of the various stages of the parsing process. 
	 * @param sentence
	 * @return
	 */
	public String parseSentence( String sentence, String resultroot_folder ) throws Exception {
		// First, place the sentence in a file.
		
		FileWriter inputFile = new FileWriter( resultroot_folder + File.separator + "sentence.txt" );
		
		inputFile.write(sentence);
		
		inputFile.close();
		
		// Then, parse the file.
		
		parseFile( resultroot_folder + File.separator + "sentence.txt", resultroot_folder );
		
		// Finally, return the RDF result.
		
		StringBuffer rdf = new StringBuffer();
		
		try {
			FileReader fr = new FileReader( resultroot_folder + File.separator + "line_0" + File.separator + "textdata.rdf" );
			BufferedReader br = new BufferedReader(fr);
						
			String line = null;
			while ( ( line = br.readLine()) != null ) {
				rdf.append(line + "\n");
			}
			
			br.close();
		}
		catch ( FileNotFoundException e ) {
			// There was a parsing error, and the RDF result was not generated.
			return null;
		}
		
		return rdf.toString();
	}
	
	/**
	 * Parses the each line in the provided ip_multiline_file, and generates files in resultroot_folder that contain results of the various stages of the parsing process. 
	 * @param sentence
	 * @return
	 */
	public void parseFile( String ip_multiline_file, String resultroot_folder ) throws Exception {		
		if (true)	// input is a file with multiline
		{
			textParserMultiLine = new TextParserMultiLine(ip_multiline_file, resultroot_folder);
			
			if (false)
			{
				if (textParserMultiLine.textParserEngineVt.size() > 0 ) 
				{
					textParserMultiLine.textParserEngineVt.elementAt(0).GetIpTextSentence();
					DisplayEarleyTree(
							textParserMultiLine.textParserEngineVt.elementAt(0).GetIpTextSentence(),
							textParserMultiLine.textParserEngineVt.elementAt(0)
							);
				}
			}
		}	
		
		if (false)
		{
			
			int  num_file = 0;
			String input_dir_root = "";
			Vector<String> input_names = new Vector<String>();
			
			if (false)
			{	num_file = 19;
				input_dir_root = "data\\data_Feb2013\\text\\";
	
				input_names.add("chengcheng yu");
				input_names.add("Dan Xie");
				input_names.add("Jianyu Wang");
				input_names.add("XIAOHAN NIE");
				input_names.add("Yixin Zhu");
			} else if (false)
			{			
				num_file = 15;
				input_dir_root = "data\\data_Feb2013\\text_courtyard\\";
				input_names.add("Hang Qi");
				input_names.add("Shuo");
				input_names.add("Tae Eun");
				input_names.add("Yixin Zhu");
			}	else if (false)
			{			
				num_file = 15;
				input_dir_root = "data\\may_2013\\indoor_clean\\";
				input_names.add("1.chengcheng yu");
				input_names.add("2.Dan Xie");
				input_names.add("3.Jianyu Wang");
				input_names.add("4.XIAOHAN NIE");
				input_names.add("5.Yixin Zhu");
			}	else 
			{			
				num_file = 15;
				input_dir_root = "data\\may_2013\\outdoor_text\\";
				input_names.add("chengcheng yu");
				input_names.add("Hang Qi");				
				input_names.add("Tae Eun");
				input_names.add("xiaohan nie");
				input_names.add("Yixin Zhu");
			}
			
			for (int i = 0; i < input_names.size(); i++)
			{
				String input_dir = input_dir_root + input_names.elementAt(i);
				String output_dir = resultroot_folder + "\\" + input_names.elementAt(i);
				
				File dir_output = new File(output_dir);   
				dir_output.mkdir(); 

				TextParserMultiFile textParserMultiFile = new TextParserMultiFile(input_dir,output_dir, num_file );	
			
			}
			
		}
	}
	
	/**
	 * Creates a folder called result_<current date/time>.
	 * @return
	 */
	public static String makeResultRootFolder( ) {
		// make result root folder
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		String resultroot_folder = "result_" + dateFormat.format(date);
		File dir = new File(resultroot_folder);
		
		while ( dir.exists() ) {
			// This can occur if the method is called repeatedly within a short span of time.  In this case, include milliseconds in the name.
			System.out.println( "The folder " + resultroot_folder + " already exists." );
			dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
			date = new Date();
			resultroot_folder = "result_" + dateFormat.format(date);
			dir = new File(resultroot_folder);
		}
		
		dir.mkdir();
		return resultroot_folder;
	}
	
	public void DisplayEarleyTree(String sentencefile, TextParserEngine textParserEngine) throws Exception 
	{
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("yy");
		top = createNodes(top, textParserEngine.earley.ptree); // create the nodes in the
												// parsetree
		tree = new JTree(top, false); // create the GUI
		JScrollPane treeView = new JScrollPane(tree);
		Pane = new JEditorPane();
		Pane.setEditable(false);
		Pane.setText("PARSE TREE FOR = " + "(" + sentencefile + ")"
				+ "\n the Earley chart is in chart.txt file");
		// initHelp();
		JScrollPane View = new JScrollPane(Pane);
		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(View);
		Dimension minimumSize = new Dimension(100, 100);
		splitPane.setDividerLocation(600);
		splitPane.setPreferredSize(new Dimension(700, 1000));
		treeView.setPreferredSize(new Dimension(600, 1000));
		add(splitPane);
	}

	// create the nodes in the parsetree
	public DefaultMutableTreeNode createNodes(DefaultMutableTreeNode top, node n) {
		top = new DefaultMutableTreeNode(n.name);
		if (n.pointers.size() > 0) {
			for (int x = 0; x < n.pointers.size(); x++) {
				DefaultMutableTreeNode top1 = new DefaultMutableTreeNode("yy");
				top.add(createNodes(top1, (node) n.pointers.elementAt(x)));
			}// endfor
		}// endif
		else {
			return top;
		}// end elseif
		return top;
	}// end createnodes

	// main function
	public static void main(String args[]) throws Exception 
	{
		JFrame frame = new JFrame("Parse Tree");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		TextParserApp newContentPane = new TextParserApp();

		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);
		
		// String ip_multiline_file = "..\\ucla_parse_tree\\textdata_2.txt";
		// String ip_multiline_file = "data\\textdata_dev.txt";
		// String ip_multiline_file = "data\\textquery_dev.txt";
		// String ip_multiline_file = "data\\20120317_17-20-07.txt";
		// String ip_multiline_file = "data\\20120317_2video.txt";
		
		// String ip_multiline_file = "data\\hallway_text1.txt";
		// String ip_multiline_file = "data\\hallway_text1_modified.txt";
		// String ip_multiline_file = "data\\outdoor_text_modified.txt";
		// String ip_multiline_file = "data\\demo_sept2012_v01.txt";
		// String ip_multiline_file = "data\\data_Feb2013\\data_Feb2013.txt";
		
		// String ip_multiline_file = "C:\\Users\\agrushin\\Documents\\MSEE\\NL_to_XML\\parse_success_original.txt";
		// String ip_multiline_file = "data\\msee_dec2014_demo\\parser_testdata_v01.txt";			
		
		// String ip_multiline_file = "data\\may_2013\\data_may2013.txt";
		//String ip_multiline_file = "data\\msee_dec2014_demo\\parser_testdata_v01.txt";

		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\indoor-view1-text_mw.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\indoor-view2-text_mw.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\indoor-view3-text_mw.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\indoor-view4-text_mw.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\outdoor-view1-rt5-text_ks.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\outdoor-view2-gl3-2-text_ks.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\outdoor-view3-gl4-2-text_ks.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\outdoor-view4-rt3-text_ks.txt";

		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\test.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\Tu.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\office.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\graph_for_test_queries.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\graphMerger.data\\a_fair_test.txt";
		//String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\graphMerger.data\\input.txt";
		// String ip_multiline_file = "data\\ImageAudioAnnotation_Project\\graphMerger.data\\text\\demo\\indoor.txt";
		
		String ip_multiline_file = "data\\msee_feb2016_demo\\msee_feb2016_demo_textdata_v01.txt";
						
		newContentPane.parseFile(ip_multiline_file, makeResultRootFolder());
		
		// This is an example of how to use the parseSentence method.
		if (false) {
			String sentence = "William has two mice.";
			String result = newContentPane.parseSentence(sentence, makeResultRootFolder());
			System.out.println( "Result for " + sentence + ":\n" + result );
			
			sentence = "Genna is on a couch.";
			result = newContentPane.parseSentence(sentence, makeResultRootFolder());
			System.out.println( "Result for " + sentence + ":\n" + result );

			sentence = "John wants a taxi.";
			result = newContentPane.parseSentence(sentence, makeResultRootFolder());
			System.out.println( "Result for " + sentence + ":\n" + result );
		}

		// Display the window.
		// if (found == 1) {
			
		if (false) {
			frame.pack();
			frame.setVisible(true);
		}
	}
}

