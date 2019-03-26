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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DependencyNode {
	
	public String name;
	public String production;
	public Vector<DependencyAttribute> attributes = new Vector<DependencyAttribute>();
	
	public Vector< Vector<DependencyAttribute> > fieldAttributes = new Vector<Vector<DependencyAttribute> >();
	
	
	public Vector<DependencyNode> children = new Vector<DependencyNode>();

	DependencyNode() 
	{	
		// create two field attribute
		for (int ifield = 0; ifield < Dependency.DEPENDENCY_FIELD_LENGTH; ifield++)
		{
			fieldAttributes.addElement(new  Vector<DependencyAttribute>() );
		}			
	}		
	
	// create  node from the node in text parse tree
	public void SetNode(node n) 
	{
		name = n.name;
		
		// children node
		if (n.pointers.size() > 0) 
		{
			for (int x = n.pointers.size() - 1; x >= 0; x--) 
			{
				
				DependencyNode new_child = new DependencyNode();
				new_child.SetNode((node) n.pointers.elementAt(x));
				children.addElement(new_child);				
			}// endfor
		}// endif
	}
	

	// create an xml node from the node in text parse tree
	public void InsertXmlNode(Document doc, Element cur_e) {
		// top=new DefaultMutableTreeNode(n.name);

		// Element child = doc.createElement("child");
		cur_e.setAttribute("name", this.name);
		
		if (this.production != null) 
		{	if (this.production.length()>0)
			{
				Element e_production = doc.createElement("production");
				e_production.setAttribute("name", production);
				cur_e.appendChild(e_production);
			}
		}
		
		for (int i = 0; i < attributes.size() ; i++) 
		{
			DependencyAttribute attr = attributes.elementAt(i);
			Element e_attr = doc.createElement("attribute");
			e_attr.setAttribute("name", attr.name);
			e_attr.setAttribute("value", attr.value);
			cur_e.appendChild(e_attr);
		}
		
		// add field attributes. This is only used for the terminal nodes
		for (int ifield = 0; ifield < fieldAttributes.size() ; ifield++) 
		{
			Element e_field = doc.createElement("field_attribute");
			for (int i = 0; i < fieldAttributes.elementAt(ifield).size() ; i++) 
			{
				DependencyAttribute attr = fieldAttributes.elementAt(ifield).elementAt(i);
				
				Element e_attr = doc.createElement("attribute");
				e_attr.setAttribute("name", attr.name);
				e_attr.setAttribute("value", attr.value);
				e_field.appendChild(e_attr);
			}
			cur_e.appendChild(e_field);
		}

		// children node
		if (children.size() > 0) {
			// for(int x=0;x<n.pointers.size();x++)
			for (int x = 0; x < children.size() ; x++) 
			{
				Element child = doc.createElement("node");
				cur_e.appendChild(child);
				children.elementAt(x).InsertXmlNode(doc, child);
			}// endfor
		}// endif

	}// end InsertXMLNode
	
	public void SetProduction(String str_production) 
	{
		this.production = str_production;
	}
	
	
	
	public void AddFieldAttribute(int ifield, DependencyAttribute arg_attr ) throws Exception
	{
		AddAttributeToVector(fieldAttributes.elementAt(ifield), arg_attr);
	}
	
	
	public void AddAttribute(DependencyAttribute arg_attr ) throws Exception
	{
		AddAttributeToVector(attributes, arg_attr);
	}
	
	static public void AddAttributeToVector(Vector<DependencyAttribute> attrVt, DependencyAttribute arg_attr ) throws Exception
	{						 
		 
		boolean isDuplicate = false; 
		
		// check duplication. 
		for (int i = 0; i < attrVt.size(); i++)
		{
			if (attrVt.elementAt(i).name.equals(arg_attr.name))
			{
				
				if (attrVt.elementAt(i).value.equals(arg_attr.value))
				{
					isDuplicate = true;				
				} else
				{
					// throw new Exception("ERROR inconsistent attribute " + arg_attr.name + " has two values: " + attrVt.elementAt(i).value + " , " + arg_attr.value );
					System.out.println("ERROR inconsistent attribute " + arg_attr.name + " has two values: " + attrVt.elementAt(i).value + " , " + arg_attr.value );
				}
			}
		}
		
		if (isDuplicate)
		{
			System.out.println("DEBUG Attribute already exists. name: " + arg_attr.name + " value: " + arg_attr.value);
		
		} else
		{
			DependencyAttribute new_attr = new DependencyAttribute(arg_attr);
			attrVt.add(new_attr);
		}
	}
	
	

	public String GetAttributeValue(String attr_name)
	{
		String value = null;
		
		// check duplication. 
		for (int i = 0; i < this.attributes.size(); i++)
		{
			if (this.attributes.elementAt(i).name.equals(attr_name))
			{
				value = this.attributes.elementAt(i).value;
				break;
			}
		}
		
		return value;
	}
	
}
