/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

package TextParser;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class VemlNode {

	static public final String VEML_SCENE = "scene";
	static public final String VEML_SELECT = "select";
	static public final String VEML_ID = "id";
	static public final String VEML_VARIABLE = "variable";
	static public final String VEML_TYPE = "type";
	static public final String VEML_AGENT = "agent";
	static public final String VEML_OBJECT = "object";
	static public final String VEML_EVENT = "event";
	static public final String VEML_LOCATION = "location";
	static public final String VEML_LOCATION_FROM = "locationFrom";
	static public final String VEML_LOCATION_TO = "locationTo";
	static public final String VEML_DIRECTION = "direction" ;
	static public final String VEML_OF = "of" ;
	static public final String VEML_IN = "in" ;	
	
	static public final String VEML_NUM = "num" ;
	static public final String VEML_WITH = "with" ;
	static public final String VEML_MANNER = "manner" ;
	static public final String VEML_DIRECTION_TO = "directionTo" ;
	
	
		
	static public final String VEML_NAME = "name";
	
	static public final String VEML_VALUE = "value";
	static public final String VEML_PATIENT = "patient";
	static public final String VEML_PURPOSE = "purpose";
	
	static public final String VEML_TIME = "time";
	static public final String VEML_BEGIN = "begin";
	static public final String VEML_END = "end";
	static public final String VEML_AT = "at";
	
	public static final String VEML_TRUE = "true";	
	public static final String VEML_FALSE = "false";
	
	// old static final String VEML_DOORSTATUS = "doorstatus";
	static public final String VEML_FLUENT_THIRSTYSTATUS = "thirsty";
	static public final String VEML_FLUENT_DOORSTATUS = "doorstatus";
	static public final String VEML_FLUENT_LIGHTSTATUS = "lightstatus";
	
	static public final String VEML_HASTOVALUE = "hasToValue";
	
	static public final String VEML_FLUENTCHANGE  = "FluentChange";
	
	static public final String VEML_CAUSALRELATION  = "CausalRelation";
	
	static public final String VEML_HASEFFECT  = "hasEffect";
	static public final String VEML_HASCAUSE  = "hasCause";
	
	static public final String VEML_SUBEVENT  = "subevent";
		
	
	// task related
	static public final String VEML_TASK_GRAPH = "task-graph";
	static public final String VEML_TASK = "task";	
	
	static public final String VEML_CHILDREN_TASK = "children";
	static public final String VEML_CHILD_TASK = "child";
	static public final String VEML_PARENT_TASK = "parent";
	static public final String VEML_ENABLES_TASK = "enables";
	static public final String VEML_PLAN = "plan";
	static public final String VEML_ATOMIC_TASK = "atomic-task";
	 
	// object attributes
	static public final String VEML_COLOR = "color";		
		
	// fluents
	static public final String VEML_FLUENT = "fluent";
	static public final String VEML_FLUENT_VALUE = "value";
	static public final String VEML_HASFLUENT = "hasFluent";
	
	static public final String VEML_FLUENT_INSTANTIATED = "instantiated";
	
	public static final String VEML_COMMAND = "command";
	public static final String VEML_STATE = "state";	
	
	
	public String name = "";
	public String value = "";
	private String id = null;		// use GetId() to get id. 
	public Vector<VemlNode> children = new Vector<VemlNode>();
	
	public VemlNode(String a_name)
	{
		name = a_name;
	}	
	
	public VemlNode(String a_name, String a_value)
	{
		name = a_name;
		value = a_value;
	}	
	
	public void AddChild(VemlNode node)
	{
		children.addElement(node);
	}

	public VemlNode FindChildNodeWithName(String a_name)
	{
		for (int i=0; i < children.size(); i++)
		{
			VemlNode child = children.elementAt(i);
			if (child.name.equals(a_name))
			{		
				return child;			 
			}
		}		
		return null;
	}
	

	public String FindChildNodeValueWithName(String a_name)
	{
		VemlNode node = FindChildNodeWithName(a_name);
		if (node == null)
			return null;
		return node.GetValue();
		
	}
	

	public Vector<VemlNode> GetChildNodeListWithName(String a_name) {
		Vector<VemlNode> node_list = new Vector<VemlNode>();		
		for (int i=0; i < children.size(); i++)
		{
			VemlNode child = children.elementAt(i);
			if (child.name.equals(a_name))
			{		
				node_list.add(child);
			}
		}		
		return node_list;
	}
	
	public VemlNode FindChildNodeWithNameAndId(String a_name, String a_id)
	{
		for (int i=0; i < children.size(); i++)
		{
			VemlNode child = children.elementAt(i);
			if (child.name.equals(a_name))
			{		
				for (int j=0; j < child.children.size(); j++)
				{
					if (child.children.elementAt(j).name.equals("id"))
					{		
						if (child.children.elementAt(j).value.equals(a_id))
						{
							return child; 
						}
					}
				}				 
			}
		}		
		return null;
	}
	
	
	
	public VemlNode FindSimpleChildEndNode(String a_name, String a_value) 
	{	
		for (int i=0; i < children.size(); i++)
		{
			VemlNode child = children.elementAt(i);
			if (child.name.equals(a_name) && (child.value.equals(a_value)) )
			{		
				if (child.children.size() ==0)
				{
					return child;
				} else
				{
					System.err.println("WARN UNEXPECTED ERROR FindSimpleChildEndNode: node is found, but is not an end-node");
				}
			}
		}		
		return null;
	}
	

	public VemlNode FindSimpleChildEndNode(String a_name) 
	{	
		for (int i=0; i < children.size(); i++)
		{
			VemlNode child = children.elementAt(i);
			if (child.name.equals(a_name))
			{		
				if (child.children.size() ==0)
				{
					return child;
				} else
				{
					System.err.println("WARN UNEXPECTED ERROR FindSimpleChildEndNode: node is found, but is not an end-node");
				}
			}
		}		
		return null;
	}
	
		
	public VemlNode AddSimpleChildEndNodeIfNotExist(String a_name, String a_value)
	{	
		VemlNode node = FindSimpleChildEndNode(a_name, a_value);
		
		if (node == null)
		{
			node = new VemlNode(a_name, a_value);
		 	this.AddChild(node);
		}		
		return node;
	}
	
	public VemlNode AddOrReplaceSimpleChildEndNode(String a_name, String a_value)
	{	
		VemlNode node = FindSimpleChildEndNode(a_name);
		
		if (node == null)
		{
			node = new VemlNode(a_name, a_value);
		 	this.AddChild(node);
		}	else
		{
			node.SetValue(a_value);
		}
		return node;
	}
	
		
	public VemlNode FindOrAddChild(String a_name) 
	{	
		VemlNode node = FindChildNodeWithName(a_name);
		
		if (node == null)
		{
			node = new VemlNode(a_name);
		 	this.AddChild(node);
		}		
		return node;
	}
	

	public VemlNode Clone() {
		VemlNode new_node = new VemlNode(this.name, this.value);  
		
		for (int i=0; i < children.size(); i++)
		{
			VemlNode in_child = children.elementAt(i);
			VemlNode out_child = in_child.Clone();
			new_node.AddChild(out_child);			
		}	
		return new_node; 
	}

	static public void OutputVemlXml(VemlNode v_root, String opfnxml) throws Exception 
	{
		// We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		
		 // old	Document doc = docBuilder.newDocument();
		Document veml_xml_doc  = docBuilder.newDocument();

		// //////////////////////
		// Creating the XML tree

		// create the root element and add it to the document
		System.err.println("DEBUG " + v_root.name);
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

	static public void InsertVemlXmlNode(Document doc, Element cur_e, VemlNode v_node)
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

	public String GetId() {
		VemlNode id_node = FindChildNodeWithName(VemlNode.VEML_ID);
		if (id_node != null)
		{	return id_node.GetValue();
		} else if 	(this.id != null)
		{
			return this.id;
		} else
		{
			return "";
		}
			
	}

	// veml node for event and object should have a child node "type"
	public String GetType() {
		VemlNode node = FindChildNodeWithName(VemlNode.VEML_TYPE);
		if (node != null)
		{	return node.GetValue();
		} else 
		{	return null;
		}		
	}


	public void SetId(String a_id) {
		VemlNode id_node = FindChildNodeWithName(VemlNode.VEML_ID);
		if (id_node == null)
		{
			this.id = a_id;	
		} else
		{	id_node.SetValue(a_id);		
			this.id = a_id;	
		}
	}

	public String GetName() {
		return this.name;
	}

	
	public String GetValue() {
		return this.value;
	}

	public void ClearAllChild() {
		children.clear();		
	}

	public void SetName(String localName) {
		// TODO Auto-generated method stub
		if (localName != null)
		{	name = localName;
		}		
	}

	public void SetValue(String nodeValue) {
		// TODO Auto-generated method stub
		if (nodeValue != null)
		{	value = nodeValue;
		}	
		
	}

	public void FindAndReplaceXmlValueInAllChildren(String old_value, String new_value) {
		if (GetValue().equals(old_value))
		{
			System.out.println("\t replacing " + old_value + " with " + new_value);
			SetValue(new_value);
		}
		for (int i=0; i < children.size(); i++)
		{
			children.elementAt(i).FindAndReplaceXmlValueInAllChildren(old_value, new_value);
		}			
	}

	public void Println() {
		// TODO Auto-generated method stub
		System.out.println("VemlNode: name:" + this.name + " value:" + this.value + " #child:" + this.children.size());
		
	}



}

