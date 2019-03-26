package TextParser;
/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import TextParser.TPLib;
import TextParser.TextParserEngine;

public class TextParserApi {
	
	private String stanfordParserGrammarFile = "";
	private String textParserGrammarFile = "";
	private String textParserMappingFile = "";
	private String workingFolder = "";
	private String sessionResultFolder = "";
	private String sessionErrorLogFile = "sessionErrorLog.txt";
		
	private String ipQueryText = "";
	private String ipRdfFile = "";
	
	private String err_log_file = "error_log.txt";
	private String sparql_result = "sparql_result.txt";

	private String sparql_query = "query_sparql.txt";
	
	private String cypher_output = "query_cypher.txt";
	
	private String owl_file;
	
	static private int ctr_line = 0; 
	
    public TextParserApi(
    		 String a_stanfordParserGrammarFile,
    		 String a_textParserGrammarFile,
    		 String a_textParserMappingFile,
    		 String a_workingFolder    		 
    		)  throws Exception 
    {    	
    	Initialize(a_stanfordParserGrammarFile, a_textParserGrammarFile, a_textParserMappingFile, a_workingFolder);
    }
    
    public void Initialize(
       		 String a_stanfordParserGrammarFile,
    		 String a_textParserGrammarFile,
       		 String a_textParserMappingFile,
       		 String a_workingFolder    		 
       		)  throws Exception 
    { 
    	workingFolder =  a_workingFolder;
    	stanfordParserGrammarFile = a_stanfordParserGrammarFile;
    	textParserGrammarFile = a_textParserGrammarFile;
    	textParserMappingFile = a_textParserMappingFile;
    	
    	if ( workingFolder == null ) {
        	return;
    	}
    	
    	try
    	{
    		// make session result root folder
    		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    		Date date = new Date();
    		sessionResultFolder = workingFolder + "/session_" + dateFormat.format(date);
    		File dir = new File(sessionResultFolder);   
    		dir.mkdir(); 

    		sessionErrorLogFile = sessionResultFolder +  "/sessionErrorLog.txt";
    	}

    	catch (Exception e)
    	{	 
    		e.printStackTrace();		
    		TPLib.AppendStringToFile(sessionErrorLogFile, "Caught Exception: TextParserMultiLine:  " + e.toString());
    		TPLib.OutputExceptionTraceStack(sessionErrorLogFile, e) ;
    	}
    }	
    
    public void setOwlFile( String owlFile ) {
    	owl_file = owlFile;
    }
    
    public String GetMessageFile() { 
    	File f = new File(err_log_file);
    	if (f.exists()) { 
    		return err_log_file ;
    	} else
    	{
    		return sparql_query ;
    	}
    }
    
    public String GetSparqlQueryFile() { return sparql_query ; } 
    
    public String GetAnswerFile() { return sparql_result ; } 
       
    public String GetNewResultFolder() throws Exception
    {
    	ctr_line++;
		String result_folder = sessionResultFolder + "/line_" + ctr_line;
		
		try {

			File dir_line = new File(result_folder);   
			dir_line.mkdir(); 			
			
		} catch (Exception e)
		{	 e.printStackTrace();
			 
			TPLib.AppendStringToFile(sessionErrorLogFile, "Caught Exception: GetNewResultFolder:  "+ e.toString());
			TPLib.OutputExceptionTraceStack(sessionErrorLogFile, e) ;
			return null; 
		}
		return result_folder;
		
    }
    public boolean RunQuery(
    		String a_query, // The query, expressed in natural language
    		String a_rdffile, // The RDF file which should be queried
    		boolean bToGenerateRDF, // Whether or not the RDF translation of the query should be generated
    		boolean bReusePreviousSparql,
    		String resultFolder) throws Exception // The location where results should be stored; if null, a new folder will be created
    {
    	if (a_query.length() < 2 )
    	{
    		return false;		    		
    	}
		
		ipQueryText = a_query;
		ipRdfFile = a_rdffile;
	
		System.out.println("ipText" + ipQueryText);
		
		String result_folder = ( resultFolder == null ) ? GetNewResultFolder() : resultFolder; // This is where the results of the query will be written
		
		// output files
		err_log_file = result_folder + "/error_log.txt";
		
		String text_filter_op_file = result_folder + "/text_input_filtered.txt";				
		String stanford_op_file = result_folder + "/stanford_op.txt";
		String stanford_op_tag_file = result_folder + "/stanford_op.txt.tag.txt";		
		String depend_filter_op_file = result_folder + "/depend_filter.txt";
		String test_earley_grammar_file = result_folder + "/earley_gm.txt";
		String earley_chart_op_txt_file = result_folder + "/chart.txt";
		String earley_chart_op_xml_file = result_folder + "/chart.xml";
		String dependency_tree_op_xml_file = result_folder + "/DependencyTree.xml";
		String dependency_tree_details_op_xml_file = result_folder + "/DependencyTree_details.xml";
		String veml_output_op_xml = result_folder + "/veml_output.xml";
		String rdf_output = result_folder + "/textdata.rdf";
		
		String unrecogword_op_file = result_folder + "/unrecognized_word.txt" ;
		String map_file = result_folder + "/map_file.txt" ;
		
		
		if (bReusePreviousSparql == false)
		{
			sparql_query = result_folder + "/query_sparql.txt";
		}
		
		sparql_result = result_folder + "/sparql_result.txt";
		
		cypher_output = result_folder + "/query_cypher.txt";
		
		ctr_line++;
		
		int dataid= 1000 * (ctr_line+1);
		
		try {
			TPLib.AppendStringToFile(sessionErrorLogFile, "TestParserApi running ... ");

			// moved 
			//File dir_line = new File(result_folder);   
			//dir_line.mkdir(); 			
			
			// output single line to file
			String text_ip_file = result_folder + "/text_input.txt";
			TPLib.OutputStringToFile(text_ip_file, ipQueryText);
					
			
			String dir_root = "";		// this is dummy
		
			TextParserEngine textParserEngine = new TextParserEngine(
				dir_root, 			
				owl_file,
				//dependency_grammar_ip_file, 
				//dependency_mapping_ip_file,
				stanfordParserGrammarFile,
				textParserGrammarFile, // This is the grammar file.
				textParserMappingFile, // This is the lookup table file.
				dataid, 
				text_ip_file, // This is the file with the original query, expressed in natural language.
				text_filter_op_file, // Output of text filtering (this is where the lookup table is used); this seems to be the "filtered text" column in the table of results.
				stanford_op_file, // Output of the Stanford POS parser, with ids; this is the "stanford" column.
				stanford_op_tag_file,  // Output of the Stanford POS parser; this is the "pos_tag" column.
				depend_filter_op_file, // This is the dependencyFiltered column.
				unrecogword_op_file,	// This is an output file to list unrecognized word for debug purpose.
				map_file,               // This is an output file that explains how unrecognized words were handled.
				test_earley_grammar_file, // This seems to be the grammar, but is it copied from somewhere?
				earley_chart_op_txt_file, // This is some other grammar file.
				earley_chart_op_xml_file, // This seems to be the grammatical representation of the input.
				dependency_tree_op_xml_file, // This seems to be another representation.
				dependency_tree_details_op_xml_file, // This seems to be another representation.
				veml_output_op_xml, // This is the VEML representation.
				rdf_output, // This is the generated RDF, but it will not get created if bToGenerateRDF is false; it is the "RDF" column.
				sparql_query, // This is the SPARQL translation of the natural language query.
				ipRdfFile, // This is the RDF file to be queried; if it is null, then the Jena engine will not be called.
				sparql_result, // This is the result of the query.
				cypher_output,
				bToGenerateRDF, // Whether or not the RDF should be generated.
				bReusePreviousSparql);
			
			
			/* KIV
			if ((textParserEngine.sparqlGenerator != null) &&
				(textParserEngine.sparqlGenerator.isQuery == false))
			{
				last_nonquery_rdf_file = new String(textParserEngine.rdf_file_fullpath);
			}		
			textParserEngineVt.addElement(textParserEngine);
			*/  
			

			if (textParserEngine.bEarleyResult != 1)
			{
				// earley parsing not successful, output the chart.xml to log file
				TPLib.AppendStringToFile(err_log_file, "Earley Parsing NOT SUCCESSFUL\n\n");
				TPLib.AppendStringToFile(err_log_file, TPLib.GetFileContents(earley_chart_op_txt_file));
			}	
		} catch (Exception e)
		{	 e.printStackTrace();
			 
			TPLib.AppendStringToFile(sessionErrorLogFile, "Caught Exception: TextParserMultiLine:  "+ e.toString());
			TPLib.OutputExceptionTraceStack(sessionErrorLogFile, e) ;
			return false; 
		}							
		/* KIV
		ParserResult parserResult =  new ParserResult(
				String.valueOf(ctr_line),						
				text_ip_file, 
				veml_output_op_xml,
				text_filter_op_file, 
				stanford_op_file,
				stanford_op_tag_file ,
				depend_filter_op_file, 
				rdf_output, 
				sparql_output,
				sparql_result,
				err_log_file);
		
		parserResultVt.addElement(parserResult); 			
		*/ 
		    	
    	return true;   	    	
    }
    
    static public boolean TestJni(String s1, String s2) 
    {
    	if (s1.length() >  s2.length())
    	{
    		return true;
    	}    	
    	return false;    	    	
    }  

    /**
     * This method accepts a natural language query, and returns a SPARQL translation, when possible.  The query is not executed.
     * stored in results_folder/
     * @param a_query
     * @param results_folder
     * @return
     */
    public String RunTextQuery(    	
 		 String a_query,
 		 String results_folder
    	) 
    {
    	boolean bToGenerateRDF = false; 
    	
    	try
    	{    		    
    		// Note: the owl file is hard-coded, but it should not matter, since we are not running queries, and thus it won't be used.
    		RunQuery(a_query, null, bToGenerateRDF, false, results_folder);
    		
        	return TPLib.GetFileContents(sparql_query);
    	}
    	catch (Exception e)
		{
    		System.err.println(e);
    		return null;
		}    	
    }    
    

    public String  Sparql_GetObject(String result_folder, String rdf_file, String ns, String subject, String property) throws Exception
    {
    	if ( result_folder == null ) {
        	result_folder = GetNewResultFolder();
    	}

    	sparql_query = result_folder + "/query_sparql.txt";
		sparql_result = result_folder + "/sparql_result.txt";
	

		/*
		String ipQueryText = "PREFIX msee:      <http://msee/ontology/msee.owl#>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT ?What\n" +
				"WHERE{ 	#" + subject + " " + property + " ?What.\n}\n";
		TPLib.OutputStringToFile(sparql_query, ipQueryText);	
		*/
	
		  // output
		FileOutputStream fos = new FileOutputStream(sparql_query); 
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		out.write( formulateObjectQuery(ns, subject, property) );
		out.close();  
		
		
		TextParserEngine textParserEngine = new TextParserEngine(this.owl_file, rdf_file, sparql_query, sparql_result);
		
		
		System.err.println("DEBUG sparql_query: " + sparql_query);
		
		String tmp_answerText = TPLib.GetFileContents(sparql_result);
		String str[] = tmp_answerText.split("\n");
		if (str.length > 1)		// ignore first line, because it is about "no. of answer..."
		{
			System.out.printf("event " + subject + " property " + property + " object " + str[1] + "\n" );
			
			return str[1];
			
		}   
    	return null;
    }
	
    public static String formulateObjectQuery( String ns, String subject, String property ) {
    	StringBuffer query = new StringBuffer();
    	
    	query.append("PREFIX msee:      <http://msee/ontology/msee.owl#>\n");
    	query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
    	query.append("PREFIX ns: <"+ns+"#>\n");
		
		// query.append("PREFIX pt_0_0_semantics_withText: <http://msee/ontology/pt_0_0_semantics_withText.semantics.rdf#>\n");
		
		// output select
		// query.append("\nSELECT ?What ?name ");
    	query.append("\nSELECT ?What ");
	
		// output where
    	query.append("\nWHERE{ ");
    	query.append("\n\tns:" + subject + " " + property + " ?What.");
		// query.append("\n\t?What msee:hasName ?name.");
		query.append("\n\n}");		

		return query.toString();
    }
	
}
