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
import org.w3c.dom.Text;

import SimplexGraph.Veml2Cypher;

public class TextParserEngine {

	private TextFilter textfilter;
	private StanfordParserInterface stanfordParserInterface; 
	public Earley earley;
	private DependencyGrammar dependencyGrammar;
	private DependenciesFilter dependenciesFilter;

	private Document xmlEarleyParseTree;
	private DependencyTree dependencyTree;
	private JenaEngine jenaEngine; 
	public SparqlGenerator sparqlGenerator = null;
	
	
	private VemlNode vemlRoot;
	Document veml_xml_doc  = null; 
	

	public String rdf_file_fullpath = ""; 
	
	// static int found;
	public int bEarleyResult = 0; 
	
	private int ctr_dependency_attr_propagated = 0; // count the number of
													// dependency whose
													// attribute have been
													// propagated
	private boolean bDebug = true; 
	
	
	public TextParserEngine(
			String dir_root,
			String owl_file, 
			String stanford_parser_grammar_file,
			String dependency_grammar_ip_file, 
			String dependency_mapping_ip_file, 
			int dataid,
			String text_ip_file,
			String text_filter_op_file,
			String stanford_op_file, 
			String stanford_op_tag_file,
			String depend_filter_op_file, 
			String unrecogword_op_file,
			String map_file,				//To output explanations of how unrecognized words were handled
			String earley_grammar_op_file,
			String earley_chart_op_txt_file,
			String earley_chart_op_xml_file,
			String dependency_tree_op_xml_file, 
			String dependency_tree_details_op_xml_file,
			String veml_output_op_xml ,
			String rdf_output,
			String sparql_output,			
			String rdf_model_for_sparql,			// if the text is a query, it will run on this rdf model
			String sparql_result,
			String cypher_output,
			boolean bToGenerateRDF,
			boolean bRunSparqlOnly
		) throws Exception 
	{
		if (bRunSparqlOnly == false)
		{			
			if (bDebug) { System.out.println("running TextFilter"); } 
			
			textfilter = new TextFilter(text_ip_file, text_filter_op_file);			
		}	
		
		if (bRunSparqlOnly == false)
		{	if (bDebug) { System.out.println("running StanfordParserInterface"); }
			stanfordParserInterface = new StanfordParserInterface(stanford_parser_grammar_file, text_filter_op_file, stanford_op_file);
			if (bDebug) { System.out.println("finish StanfordParserInterface"); }
		}		
				
		if (bRunSparqlOnly == false) 
		{
			if (bDebug) { System.out.println("running DependencyGrammar"); }
			// this loads in the grammar, and convert it to a format suitable for earley algorithm.  
			dependencyGrammar = new DependencyGrammar(
					dependency_grammar_ip_file,
					earley_grammar_op_file);
		}
		
		String str_dependencies = null;

		if (bRunSparqlOnly == false) // run dependencies filter
		{
			dependenciesFilter = new DependenciesFilter(
					dependency_mapping_ip_file, stanford_op_file, stanford_op_tag_file, 
					depend_filter_op_file, unrecogword_op_file, map_file, dataid);
			String strMappedDependencies = dependenciesFilter
					.GetMappedDependencies();
			
			str_dependencies = strMappedDependencies ;				
		}

		if (bRunSparqlOnly == false) {
			
			System.out.println("\n\n\n =======================\n");
			System.out.println("EARLEY INPUT: \n\t" + str_dependencies);
			System.out.println("\n=======================\n");
			String sentencefile2[] = str_dependencies.split(" ");
			int length1 = sentencefile2.length;

			earley = new Earley(length1, earley_grammar_op_file, str_dependencies, earley_chart_op_txt_file, false);
			bEarleyResult = earley.found;

			if (bEarleyResult == 1) {
				xmlEarleyParseTree = OutputEarleyTreeToXmlFile( 
						str_dependencies,
						earley_chart_op_xml_file);

				GenerateDependencyTree( dependency_tree_op_xml_file );
			} else {
				System.out
						.println("parse error:check the grammer and the input sentence");
			}
		}

		if ((bEarleyResult == 1) || (bRunSparqlOnly == true))
		{
			if (bRunSparqlOnly == false)
			{
				// propagation attribute
				PropagateAttributes();
				dependencyTree.WriteXml( dependency_tree_details_op_xml_file );
	
				
				// generate veml
				GenerateVeml( veml_output_op_xml );		
				
				//System.out.println("arjun "+veml_output_op_xml);
				//System.exit(1);
				
				if (bToGenerateRDF)
				{	// for query, we do not really need the rdf
					String veml_file = dir_root + veml_output_op_xml;
					rdf_file_fullpath  = dir_root + rdf_output;//			
					RdfGenerator rdfGenerator = new RdfGenerator(veml_file, rdf_file_fullpath, dataid  );
				}			
				
				sparqlGenerator = new SparqlGenerator(veml_xml_doc, sparql_output);
				if ( (sparqlGenerator.isQuery)  && rdf_model_for_sparql != null )
				{
					jenaEngine = new JenaEngine(rdf_model_for_sparql , owl_file, sparql_output, sparql_result);
				}
				
				Veml2Cypher veml2cypher = new Veml2Cypher(veml_xml_doc, cypher_output);
				
			} else if ( rdf_model_for_sparql != null )
			{	jenaEngine = new JenaEngine(rdf_model_for_sparql , owl_file, sparql_output, sparql_result);
			}
			
			// if ((sparqlGenerator.isQuery) && (last_nonquery_rdf_file != null))
			// if ((sparqlGenerator.isQuery)  && (last_nonquery_rdf_file.length() > 0))
		
		 
		}
	}	
	

	
	public TextParserEngine(			
			String owl_file, 
			String rdf_model_for_sparql,			// if the text is a query, it will run on this rdf model
			String sparql_output,
			String sparql_result
		) throws Exception 
	{
		jenaEngine = new JenaEngine(rdf_model_for_sparql , owl_file, sparql_output, sparql_result);
	}	
	

	
	public void GenerateVeml( String veml_output_op_xml ) throws Exception 
	{
		// vemlRoot = new VemlNode(VEML_ROOT_NODE);
		vemlRoot = new VemlNode("scene");
		
		dependencyGrammar.GenerateVemlAtNode(vemlRoot, vemlRoot,
				dependencyTree.root);
		
		if (this.textfilter != null)
		{
			InsertTimeInformationToVemlObjectAndEvent(vemlRoot, textfilter.GetTimeBeginInSecond(), textfilter.GetTimeEndInSecond());
		}
			
		OutputVemlXml(vemlRoot, 
				veml_output_op_xml				
				);
	}
	
	public boolean IsFluentName(String name)
	{
		if (
				(name.equals(VemlNode.VEML_FLUENT_THIRSTYSTATUS)) || 
				(name.equals(VemlNode.VEML_FLUENT_DOORSTATUS)) || 
				(name.equals(VemlNode.VEML_FLUENT_LIGHTSTATUS)) 
				)
		{
			return true;
		}
		return false;
	}
	
	public void InsertTimeInformationToVemlObjectAndEvent(VemlNode v_node, String strTimeBegin, String strTimeEnd) throws Exception 
	{
		if ((strTimeBegin == null) || (strTimeEnd == null))
		{	return;
		}		
		
		if ((v_node.name != null) && (v_node.name.length() > 0)) 
		{
			if ( 
					// option: do not insert for object
				  // (v_node.name.equals(VemlNode.VEML_OBJECT)) ||
				 (v_node.name.equals(VemlNode.VEML_EVENT)) ||
				 (IsFluentName(v_node.name))
				 )	
			{
				VemlNode n_time = new VemlNode(VemlNode.VEML_TIME);
				n_time.AddSimpleChildEndNodeIfNotExist(VemlNode.VEML_BEGIN, strTimeBegin);
				n_time.AddSimpleChildEndNodeIfNotExist(VemlNode.VEML_END, strTimeEnd);	
				v_node.AddChild(n_time);
			}					
		}

		for (int ichild = 0; ichild < v_node.children.size(); ichild++) 
		{
			InsertTimeInformationToVemlObjectAndEvent(v_node.children.elementAt(ichild), strTimeBegin, strTimeEnd);
		}
		
	}
	public void OutputVemlXml(VemlNode v_root, String opfnxml) throws Exception 
	{
		// We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		
		 // old	Document doc = docBuilder.newDocument();
		veml_xml_doc = docBuilder.newDocument();

		// //////////////////////
		// Creating the XML tree

		// create the root element and add it to the document
		Element root = veml_xml_doc.createElement(v_root.name);
		veml_xml_doc.appendChild(root);

		InsertVemlXmlNode(veml_xml_doc, root, v_root);

		/*
		 * example. DO NOT DELETE YET. //create a comment and put it in the root
		 * element Comment comment = doc.createComment("Just a thought");
		 * root.appendChild(comment);
		 * 
		 * //create child element, add an attribute, and add to root Element
		 * child = doc.createElement("child"); child.setAttribute("name",
		 * "value"); root.appendChild(child);
		 * 
		 * //add a text element to the child Text text =
		 * doc.createTextNode("Filler, ... I could have had a foo!");
		 * child.appendChild(text);
		 */

		TPLib.SaveXml(veml_xml_doc, opfnxml);
	}

	public void InsertVemlXmlNode(Document doc, Element cur_e, VemlNode v_node)
			throws Exception // insert rule in the chart
	{
		if ((v_node.value != null) && (v_node.value.length() > 0)) {
			Text text = doc.createTextNode(v_node.value);
			cur_e.appendChild(text);
		}

		for (int ichild = 0; ichild < v_node.children.size(); ichild++) {
			Element child = doc.createElement(v_node.children.elementAt(ichild).name);
			cur_e.appendChild(child);
			InsertVemlXmlNode(doc, child, v_node.children.elementAt(ichild));
		}
	}

	public void PropagateAttributes() throws Exception {
		ctr_dependency_attr_propagated = 0;
		PropagateAttributes(dependencyTree.root);
	}

	public void PropagateAttributes(DependencyNode cur_node) throws Exception {
		String str_production = cur_node.name + " --";

		for (int i = 0; i < cur_node.children.size(); i++) {
			DependencyNode n2 = cur_node.children.elementAt(i);
			str_production += (" " + n2.name);
			PropagateAttributes(n2);
		}

		// generate the production string for current node
		if (cur_node.children.size() > 0) {
			cur_node.SetProduction(str_production);
		}

		// get attributes for this node
		if (cur_node.children.size() == 0) {
			// this is an end node
			Dependency d = dependenciesFilter.dependencies[ctr_dependency_attr_propagated];
			
			try {
				for (int iattr = 0; iattr < d.attributes.size(); iattr++) {		
					cur_node.AddAttribute((DependencyAttribute) d.attributes.elementAt(iattr));
				}
				
				for (int ifield = 0; ifield < Dependency.DEPENDENCY_FIELD_LENGTH; ifield++)
				{
					for (int iattr = 0; iattr < d.fieldAttributes.elementAt(ifield).size(); iattr++) 
					{		
						cur_node.AddFieldAttribute(ifield, (DependencyAttribute)  d.fieldAttributes.elementAt(ifield).elementAt(iattr));
					}
				}			
				
			}  catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("WARN exception in PropagateAttributes at end-node. Not Fatal, ignore. ");
			}			
			
			ctr_dependency_attr_propagated++;
		} else {
			// apply attribute function to propagate attribute from children
			dependencyGrammar.PropagateAttributeAtNode(cur_node);
		}
	}

	public Document OutputEarleyTreeToXmlFile(String sentencefile,
			String opfnxml) throws Exception // insert rule in the chart
	{
		// We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// //////////////////////
		// Creating the XML tree

		// create the root element and add it to the document
		Element root = doc.createElement("root");
		doc.appendChild(root);

		InsertXMLNode(doc, root, earley.ptree);

		TPLib.SaveXml(doc, opfnxml);

		return doc;
	}

	public void GenerateDependencyTree(String dependency_tree_op_xml_file) throws Exception {
		dependencyTree = new DependencyTree(earley.ptree);
		dependencyTree.WriteXml( dependency_tree_op_xml_file );
	}

	/* moved
	public void SaveXml(Document doc, String opfnxml) throws Exception {
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
		String xmlString = sw.toString();

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(opfnxml));
		writer.write(sw.toString());
		writer.close();
	}
	*/ 

	// create an xml node from the node in text parse tree
	private void InsertXMLNode(Document doc, Element cur_e, node n) 
	{
		cur_e.setAttribute("name", n.name);

		// children node
		if (n.pointers.size() > 0) {
			// for(int x=0;x<n.pointers.size();x++)
			for (int x = n.pointers.size() - 1; x >= 0; x--) {
				Element child = doc.createElement("node");
				cur_e.appendChild(child);
				InsertXMLNode(doc, child, (node) n.pointers.elementAt(x));
			}// endfor
		}// endif

	}// end InsertXMLNode
	
	public String GetIpTextSentence()
	{
		return stanfordParserInterface.ipText;		
	}
	
	public VemlNode GetRootVemlNode()
	{	return vemlRoot;
	}
	
}
