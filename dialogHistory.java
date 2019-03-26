package edu.ucla.xai.parser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class dialogHistory {

	int storylineID;
	
	int currentTurnID;
	String currentFocusEntityName;
	int currentFocusEntityNeo4jID;
	List<xAOG> xpgList = new ArrayList<xAOG>(); 

	public int getStorylineID() {
		return storylineID;
	}
	public void setStorylineID(int storylineID) {
		this.storylineID = storylineID;
	}
	
	public int getCurrentTurnID() {
		return currentTurnID;
	}
	public void setCurrentTurnID(int currentTurnID) {
		this.currentTurnID = currentTurnID;
	}
	public String getCurrentFocusEntityName() {
		return currentFocusEntityName;
	}
	public void setCurrentFocusEntityName(String currentFocusEntityName) {
		this.currentFocusEntityName = currentFocusEntityName;
	}
	public int getCurrentFocusEntityNeo4jID() {
		return currentFocusEntityNeo4jID;
	}
	public void setCurrentFocusEntityNeo4jID(int currentFocusEntityNeo4jID) {
		this.currentFocusEntityNeo4jID = currentFocusEntityNeo4jID;
	}
	public List<xAOG> getXpgList() {
		return xpgList;
	}
	public void setXpgList(List<xAOG> xpgList) {
		this.xpgList = xpgList;
	}

	public void loadHistory(String history_file_path) throws Exception, IOException {

		this.xpgList = new ArrayList<xAOG>(); 
		File historyFile = new File(history_file_path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		//Document xml = dBuilder.parse(new InputSource(new StringReader(query_string)));
		Document xml = dBuilder.parse(historyFile);
		xml.getDocumentElement().normalize();

		this.storylineID = Integer.parseInt(xml.getElementsByTagName("story_id").item(0).getTextContent().toString());
		this.currentTurnID = Integer.parseInt(xml.getElementsByTagName("current_turn").item(0).getTextContent().toString());
		
		NodeList nList = xml.getElementsByTagName("turn");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			
			Node nNode = nList.item(temp);
			
			xAOG xpg = new xAOG();
			
			if ( xml.getElementsByTagName("id").getLength() > 0 ) {
				xpg.setTurnId(Integer.parseInt(((Element)xml.getElementsByTagName("id").item(0)).getTextContent()));
			}	
			if ( xml.getElementsByTagName("explanation_type").getLength() > 0 ) {
				xpg.setExplanationType(((Element)xml.getElementsByTagName("explanation_type").item(0)).getTextContent());
			}
			if ( xml.getElementsByTagName("focus_entity_name").getLength() > 0 ) {
				xpg.setFocusEntityName(((Element)xml.getElementsByTagName("focus_entity_name").item(0)).getTextContent());
			}
			if ( xml.getElementsByTagName("focus_entity_neo4j_id").getLength() > 0 ) {
				xpg.setFocusEntityNeo4jID(((Element)xml.getElementsByTagName("focus_entity_neo4j_id").item(0)).getTextContent());
			}
			this.xpgList.add(xpg);
		}
	}
}
