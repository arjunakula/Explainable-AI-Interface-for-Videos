/**
 * 
 */
package edu.ucla.xai.parser;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import SimplexGraph.GraphDB;

/**
 * @author Arjun Akula, April-12-2018
 *
 */
public class queryParser {

	/**
	 * 
	 */
	public GraphDB m_graphDB = null;
	public String utility_file = "xutilities.txt";
	public String dialog_history_directory = "dialog_history\\";
	public Utility utils = new Utility();

	public queryParser() throws Exception {

		// load Utility file
		utils.loadUtilities(this.utility_file);
	}


	public boolean EvidenceExists(String next_explanation_type, dialogHistory history){
		boolean status = false;
		String node_name = history.currentFocusEntityName;
		String node_id = Integer.toString(history.currentFocusEntityNeo4jID);

		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "";

		if(next_explanation_type == "Attribute_Text"){
			neo4j_return_string = "n.XAttr";
		}

		else if(next_explanation_type == "Action_Text"){
			neo4j_return_string = "n.detectionscores";
		}

		else if(next_explanation_type == "Bubble"){
			neo4j_return_string = "n.Bubble";
		}

		else if(next_explanation_type == "X-Attribute"){
			neo4j_return_string = "n.XAttr";
		}

		else if(next_explanation_type == "Beta"){
			neo4j_return_string = "n.XAttr";
		}

		else if(next_explanation_type == "Gamma"){
			neo4j_return_string = "n.XAttr";
		}

		//else if(next_explanation_type == "Time"){

		//}

		String sparql_query = "MATCH(" + neo4j_match_string+ ") WHERE EXISTS( "+neo4j_return_string+")";

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		if(output.length() == 0){
			return false;
		}
		else{
			return true;
		}
	}

	public String parse(int storylineID, GraphDB m_graphDB) throws Exception{
		
		String explanation_xml = "";
		this.m_graphDB = m_graphDB;

		// load history using storylineID and identify the type of explanation you need to generate now using utilities
		dialogHistory dh = new dialogHistory(); 
		String history_file_path = this.dialog_history_directory+storylineID+".xml";
		dh.loadHistory(history_file_path);

		String next_explanation_type = "";
		String next_explanation_content = "";
		String next_explanation = "";

		while(next_explanation_type.length() == 0){
			next_explanation_type = getNextExplanationTypeFromHistory(dh);

			if(next_explanation_type.length() == 0) break;

			if(!EvidenceExists(next_explanation_type, dh)){
				next_explanation_type = "";
			}
		}

		if(next_explanation_type.length() == 0){
			// we ran out of evidences. can't explain it further.
		}

		else if(next_explanation_type == "Attribute_Text"){
			//next_explanation_content = getNextExplanationContentFromHistory(dh, next_explanation_type);
			next_explanation = getExplanationByAttributeText(dh, next_explanation_content);
		}

		else if(next_explanation_type == "Action_Text"){
			//next_explanation_content = getNextExplanationContentFromHistory(dh, next_explanation_type);
			next_explanation = getExplanationByActionText(dh, next_explanation_content);
		}

		else if(next_explanation_type == "Bubble"){
			//next_explanation_content = getNextExplanationContentFromHistory(dh, next_explanation_type);
			next_explanation = getExplanationByBubble(dh, next_explanation_content);
		}

		else if(next_explanation_type == "X-Attribute"){
			//next_explanation_content = getNextExplanationContentFromHistory(dh, next_explanation_type);
			next_explanation = getExplanationByXAttribute(dh, next_explanation_content);
		}

		else if(next_explanation_type == "Beta"){
			//next_explanation_content = getNextExplanationContentFromHistory(dh, next_explanation_type);
			next_explanation = getExplanationByBeta(dh, next_explanation_content);
		}

		else if(next_explanation_type == "Gamma"){
			//next_explanation_content = getNextExplanationContentFromHistory(dh, next_explanation_type);
			next_explanation = getExplanationByGamma(dh, next_explanation_content); 
		}

		//else if(next_explanation_type == "Time"){
		//	next_explanation_content = getNextExplanationContentFromHistory(history, next_explanation_type);
		//	next_explanation = getExplanationByTime(history, next_explanation_content);
		//}

		// update history file
		updateHistory(next_explanation_type, dh);

		// return the explanation to the user in the form of XML to the front end
		return explanation_xml;
	}

	public String getExplanationByAttributeText(dialogHistory history, String next_explanation_content){

		String node_name = "Standing";
		String node_id = "a1";
		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "n.XAttr";
		String sparql_query = "MATCH(" + neo4j_match_string+ ") RETURN "+neo4j_return_string;

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		return output;
	}

	public String getExplanationByActionText(dialogHistory history, String next_explanation_content){
		String node_name = "Standing";
		String node_id = "a1";
		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "n.detectionscores ";
		String sparql_query = "MATCH(" + neo4j_match_string+ ") RETURN "+neo4j_return_string;

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		return output;
	}

	public String getExplanationByBubble(dialogHistory history, String next_explanation_content){

		String node_name = "Standing";
		String node_id = "a1";
		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "n.Bubble";
		String sparql_query = "MATCH(" + neo4j_match_string+ ") RETURN "+neo4j_return_string;

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		return output;
	}

	public String getExplanationByXAttribute(dialogHistory history, String next_explanation_content){

		String node_name = "Standing";
		String node_id = "a1";
		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "n.XAttr";
		String sparql_query = "MATCH(" + neo4j_match_string+ ") RETURN "+neo4j_return_string;

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		return output;

	}

	public String getExplanationByBeta(dialogHistory history, String next_explanation_content){

		String node_name = "Standing";
		String node_id = "a1";
		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "n.XAttr";
		String sparql_query = "MATCH(" + neo4j_match_string+ ") RETURN "+neo4j_return_string;

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		return output;
	}

	public String getExplanationByGamma(dialogHistory history, String next_explanation_content){

		String node_name = "Standing";
		String node_id = "a1";
		String neo4j_match_string = "n:"+node_name+"{"+":"+"'"+node_id+"'}";
		String neo4j_return_string = "n.XAttr";
		String sparql_query = "MATCH(" + neo4j_match_string+ ") RETURN "+neo4j_return_string;

		List<String> list_return = new ArrayList<String>();
		list_return.add(neo4j_return_string);

		String output = m_graphDB.ExecuteGraphDbQuery(sparql_query, list_return);
		return output;

	}

	//public String getExplanationByTime(List<dialogHistory> history, String next_explanation_content){

	//String sparql_query = "";
	//String output = runSparql(sparql_query);

	//}

	public String getNextExplanationTypeFromHistory(dialogHistory history){

		String next_explanation_type="";
		String new_current_node="";
		String current_node = history.currentFocusEntityName;
				
		for (Map.Entry<String, Double> entry : utils.vals.get(current_node).entrySet()) {
            String key = entry.getKey();
            Double val = entry.getValue();
            
            List<xAOG> xpgList = history.getXpgList();
            boolean used_expln_status = false;
            for (int j = 0; j< xpgList.size(); j++){
            	xAOG xaog = new xAOG();
            	
            	if(xaog.explanationType.equalsIgnoreCase(key)){
            		used_expln_status = true;
            		break;
            	}
            }
            if(used_expln_status){
            	continue;
            }
            else{
            	next_explanation_type = key;
            	new_current_node = current_node;
            	break;
            }
        }
		//FIXME:handle gamma and beta node changes here. 
		return next_explanation_type;

	}

	public void updateHistory(String nextExplanationType, String nextCurrentNode, dialogHistory history){
		
		
		
	}

}
