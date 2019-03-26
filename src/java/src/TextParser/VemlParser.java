package TextParser;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import TextParser.VemlNode;

public class VemlParser
{
	VemlNode veml_root; 
	
	boolean bverbose = false;

	public VemlParser(String veml_file) 
	{
		
		veml_root = new VemlNode("");
		
		 try {
			 
				File fXmlFile = new File(veml_file);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				//veml_root.SetName(doc.getNodeName());
				
				if (bverbose )
				{
					System.err.println("DEBUG doc.getNodeName(): " + doc.getNodeName() );
				}
				
				NodeList nodelist = doc.getChildNodes();
				
				if (nodelist.getLength() != 1)
				{
					System.err.println("ERROR expect only one nodelist.getLength(): " + nodelist.getLength() );
				}
								
				for (int inode= 0; inode < 1; inode++) 
				{
					Node nNode = nodelist.item(inode);
					veml_root= GetVemlNode(nNode);
					//veml_root.AddChild(new_node);
				}				
		  } catch (Exception e) {
			e.printStackTrace();
		  }						
	 
	}

	private VemlNode GetVemlNode(Node nNode) 
	{
		VemlNode new_node = new VemlNode(nNode.getNodeName());
		if (bverbose )
		{
			System.err.println("DEBUG  nNode.getNodeName(): " + nNode.getNodeName());
		}
		
		NodeList nodelist = nNode.getChildNodes();
		for (int inode= 0; inode < nodelist.getLength(); inode++) 
		{
			Node child_nNode = nodelist.item(inode);
	
			
			if (child_nNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				VemlNode new_child_node= GetVemlNode(child_nNode);
				new_node.AddChild(new_child_node);
			}
			else if ( child_nNode.getNodeType() == Node.TEXT_NODE )
			{
				if (child_nNode.getNodeValue().length() >0)
				{
					new_node.SetValue(child_nNode.getNodeValue());
					if (bverbose )
					{
						System.err.println("DEBUG TEXT_NODE child_nNode.getNodeValue(): " + child_nNode.getNodeValue());
					}
				}
			} else
			{	System.err.println("ERROR GetVemlNode don't know how to parse this node type " + child_nNode.getNodeType() );
				return null;
			}
		}
		return new_node;	
	}

	public VemlNode GetVemlNode() {
		// TODO Auto-generated method stub
		return veml_root;
	}
}
	