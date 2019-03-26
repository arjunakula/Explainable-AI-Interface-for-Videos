/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

package TextParser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DependencyTree {
	
	public DependencyNode root;

	// create an xml node from the node in text parse tree
	DependencyTree(node n) {
		
		root = new DependencyNode();		
		root.SetNode(n);
		
	}
	

	public void WriteXml(String opfnxml) throws Exception // insert rule in the chart
	{

		// We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// //////////////////////
		// Creating the XML tree

		// create the root element and add it to the document
		Element xmlroot = doc.createElement("root");
		doc.appendChild(xmlroot);

		root.InsertXmlNode(doc, xmlroot);

		TPLib.SaveXml(doc, opfnxml);

	}

/* moved
	public void SaveXml(Document doc, String opfnxml) throws Exception 
	{
		// ///////////////
		// Output the XML

		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(opfnxml));
		writer.write(sw.toString());
		writer.close();
	}	
	*/ 
}
