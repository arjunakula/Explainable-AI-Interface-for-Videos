/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

package TextParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class TextParserMultiLine {
	
	final String dir_root = "";

	public Vector<TextParserEngine> textParserEngineVt = new Vector<TextParserEngine>();
	Vector<ParserResult> parserResultVt = new Vector<ParserResult>();
	
	public String last_nonquery_rdf_file = "";
	 
	/**
	 * The <code>TextParserMultiLine</code> constructor .
	 *
	 * @param ip_multiline_file
	 * @throws Exception
	 */
	public TextParserMultiLine(String ip_multiline_file, String resultroot_folder) throws Exception 
	{
		// input grammar files 
		String dependency_grammar_ip_file = "..\\model\\dependency_grammar.xml";
		String dependency_mapping_ip_file = "..\\model\\dependencies_mapping.txt";
		
		// result summary xml
		String result_xml_file = resultroot_folder + "\\result_summary.xml" ;
		String xslt_file = resultroot_folder + "\\result_summary.xsl" ;
		String xslt_ref = "..\\model\\result_summary.xsl" ;
		TPLib.copyfile(xslt_ref, xslt_file );
			
		
		// multiline input file
		FileReader fr = new FileReader(ip_multiline_file);
		BufferedReader br1 = new BufferedReader(fr);		
		String ipText = null;		
		int ctr_line = 0; 
		while((ipText=br1.readLine())!=null)   	//read the grammar file
		{
			if (ipText.equalsIgnoreCase("stop"))
			{
				break;
			}
			
			if (ipText.startsWith("\t"))
			{	// if tabbed, ignore sentence
				continue;
			}
			
			if (ipText.length() > 2 )
			{
				System.out.println("ipText: " + ipText);
				
				String result_folder = resultroot_folder + "\\line_" + ctr_line;
				File dir_line = new File(result_folder);   
				dir_line.mkdir(); 			
				
				// output single line to file
				String text_ip_file = result_folder + "\\text_input.txt";
				TPLib.OutputStringToFile(text_ip_file, ipText);
						
				// output files
				String err_log_file = result_folder + "\\error_log.txt";
				String text_filter_op_file = result_folder + "\\text_input_filtered.txt";				
				String stanford_op_file = result_folder + "\\stanford_op.txt";
				String stanford_op_tag_file = result_folder + "\\stanford_op.txt.tag.txt";		
				String depend_filter_op_file = result_folder + "\\depend_filter.txt";
				String test_earley_grammar_file = result_folder + "\\earley_gm.txt";
				String earley_chart_op_txt_file = result_folder + "\\chart.txt";
				String earley_chart_op_xml_file = result_folder + "\\chart.xml";
				
				String dependency_tree_op_xml_file = result_folder + "\\DependencyTree.xml";
				String dependency_tree_details_op_xml_file = result_folder + "\\DependencyTree_details.xml";
				
				String veml_output_op_xml = result_folder + "\\veml_output.xml";
				
				String rdf_output = result_folder + "\\textdata.rdf";
				
				String sparql_output = result_folder + "\\query_sparql.txt";
				String sparql_result = result_folder + "\\sparql_result.txt";
				

				String cypher_output = result_folder + "\\query_cypher.txt";
				
				// output to two files to save text that are parsed successfully or unsucessfully 
				String op_success_file = resultroot_folder + "\\parse_success.txt" ;
				String op_fail_file = resultroot_folder + "\\parse_fail.txt" ;

				//unrecognized words are output to 1 of 2 files depending on whether a replacement was found
				String unrecogword_op_file = resultroot_folder + "\\unrecognized_word.txt" ;
				String map_file = resultroot_folder + "\\map_file.txt" ;

				
				// set dataid, so that each line has a different id
				int dataid= 1000 * (ctr_line+1);
				
				try {
					
				
					TextParserEngine textParserEngine = new TextParserEngine(
						dir_root, 
						null,
						".." + File.separator + "grammar" + File.separator + "englishPCFG.ser.gz",
						dependency_grammar_ip_file, 
						dependency_mapping_ip_file, 
						dataid,
						text_ip_file,
						text_filter_op_file,
						stanford_op_file, 
						stanford_op_tag_file, 
						depend_filter_op_file, 
						unrecogword_op_file,
						map_file,
						test_earley_grammar_file,
						earley_chart_op_txt_file,
						earley_chart_op_xml_file,
						dependency_tree_op_xml_file, 
						dependency_tree_details_op_xml_file,
						veml_output_op_xml,
						rdf_output,
						sparql_output,
						last_nonquery_rdf_file,
						sparql_result,
						cypher_output,
						true, false);
					
					if ((textParserEngine.sparqlGenerator != null) &&
						(textParserEngine.sparqlGenerator.isQuery == false))
					{
						last_nonquery_rdf_file = new String(textParserEngine.rdf_file_fullpath);
					}
				
					textParserEngineVt.addElement(textParserEngine); 
					
					
					if (textParserEngine.bEarleyResult != 1)
					{
						// earley parsing not successful, output the chart.xml to log file
						TPLib.AppendStringToFile(err_log_file, "EARLY PARSING NOT SUCCESSFUL\n\n");
						TPLib.AppendStringToFile(err_log_file, TPLib.GetFileContents(earley_chart_op_txt_file));
						 
						TPLib.AppendStringToFile(op_fail_file, TPLib.GetFileContents(text_ip_file));									
					} else
					{
						TPLib.AppendStringToFile(op_success_file, TPLib.GetFileContents(text_ip_file));
					}
									
				
				} catch (Exception e)
				{	 e.printStackTrace();
					 //System.err.println("Caught Exception: " + e.getMessage());
					 //System.err.println("Caught Exception: " + e.toString());
					 
					TPLib.AppendStringToFile(err_log_file, "Caught Exception: TextParserMultiLine:  "+ e.toString());
					TPLib.OutputExceptionTraceStack(err_log_file, e) ;
				}							
				
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
						cypher_output,
						err_log_file);
				
				parserResultVt.addElement(parserResult); 			
				
				
				ctr_line++;
			}	else
			{
				if (ipText.equalsIgnoreCase("stop"))
				{
					break;
				}
			}
		}	 
		
		
		OutputResultXml(result_xml_file);		
	}
	

	private void OutputResultXml(String result_xml_file) throws Exception
	{
		// We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// //////////////////////
		// Creating the XML tree

		// create the root element and add it to the document
		
	
		// Element xmlstyle = doc.createElement("root");
		//doc.setXmlStandalone(true); 
					
		
		Element xmlroot = doc.createElement("root");
		doc.appendChild(xmlroot);
		
		// style 
		ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "href=\"result_summary.xsl\" " + "type=\"text/xsl\" " );
		doc.insertBefore(pi, xmlroot);

		for (int i = 0; i < parserResultVt.size(); i++)
		{
			parserResultVt.elementAt(i).InsertXmlNode(doc, xmlroot);
		}
		TPLib.SaveXml(doc, result_xml_file);
			
	}
	
}
