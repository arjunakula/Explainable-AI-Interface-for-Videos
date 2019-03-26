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
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


public class ParserResult {
	public String inputId;
	public String inputText;
	public String veml;
	public String textFiltered;
	public String stanford;
	public String stanford_tag;
	public String dependencyFiltered;
	public String rdf_output;
	public String sparql_output;
	public String sparql_result;
	public String cypher_output;
	public String error_log;
	
	ParserResult(
		 String a_inputId,
		 String a_inputText,
		 String a_veml,
		 String a_textFiltered,
		 String a_stanford,
		 String a_stanford_tag,
		 String a_dependencyFiltered,
		 String a_rdf_output,
		 String a_sparql_output,
		 String a_sparql_result,
		 String a_cypher_output,
		 String a_error_log
		 )
		 {
			inputId = a_inputId;
			inputText = a_inputText;
			veml =  a_veml;
			textFiltered =  a_textFiltered;
			stanford =  a_stanford;
			stanford_tag = a_stanford_tag; 
			dependencyFiltered = a_dependencyFiltered;
			rdf_output = a_rdf_output;
			sparql_output = a_sparql_output;
			sparql_result = a_sparql_result;
			cypher_output = a_cypher_output;
			error_log =  a_error_log;
		 }	

	// create an xml node from the node in text parse tree
	public void InsertXmlNode(Document doc, Element cur_e) 
	{
		Element e_result = doc.createElement("result");
		InsertStringXmlNode(doc, e_result, "id", inputId);
		InsertFileContentXmlNode(doc, e_result, "input", inputText);
		InsertFileContentXmlNode(doc, e_result, "veml", veml);
		InsertFileContentXmlNode(doc, e_result, "textFiltered", textFiltered);
		InsertFileContentXmlNode(doc, e_result, "stanford", stanford);
		InsertFileContentXmlNode(doc, e_result, "stanford_tag", stanford_tag);		
		InsertFileContentXmlNode(doc, e_result, "dependencyFiltered", dependencyFiltered);
		InsertFileContentXmlNode(doc, e_result, "rdf_output", rdf_output);
		InsertFileContentXmlNode(doc, e_result, "sparql_output", sparql_output);
		InsertFileContentXmlNode(doc, e_result, "sparql_result", sparql_result);	
		InsertFileContentXmlNode(doc, e_result, "cypher_output", cypher_output);	
		InsertFileContentXmlNode(doc, e_result, "error_log", error_log);
		cur_e.appendChild(e_result);	
	}
	
	public void InsertStringXmlNode(Document doc, Element cur_e, String name, String str) 
	{
		Element e_new = doc.createElement(name);
		Text text = doc.createTextNode(str);
		e_new.appendChild(text);		
		cur_e.appendChild(e_new);		
		
	}// end InsertXMLNode
	
	public void InsertFileContentXmlNode(Document doc, Element cur_e, String name, String file) 
	{
		
		String file_content = "";
		
		// multiline
		try
		{
			FileReader fr = new FileReader(file);
			BufferedReader br1 = new BufferedReader(fr);		
			String ipText = null;			
			while((ipText=br1.readLine())!=null)   	//read the grammer file
			{
				file_content += ipText + "\n";
			}
			if (false)
			{	// debug
				System.out.println("file content: " + file_content);
			}
		}
		catch (FileNotFoundException e)
		{	 //e.printStackTrace();			
			file_content = "na";
		}	
		catch (Exception e)
		{	 e.printStackTrace();
			 file_content = "unknown error" + e.toString();
		}	
				
		
		Element e_new = doc.createElement(name);
		Text text = doc.createTextNode(file_content);
		e_new.appendChild(text);		
		cur_e.appendChild(e_new);		
		
	}// end InsertXMLNode
	
}
