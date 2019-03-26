package TextParser;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

import java.io.*;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.File;


public class DependencyGrammar {

	public Vector<DependencyGrammarRule> rules = new Vector<DependencyGrammarRule>();
	
	final private boolean bDebug = false; 
	
	DependencyGrammar(String ip_grammar_file, String op_earley_grammar_file) throws Exception
	{
		LoadGrammar(ip_grammar_file);
		GenerateEarleyGrammarText(op_earley_grammar_file);		
	}
	
	private void LoadGrammar(String grammar_file) throws Exception
	{
		 try {
			 
				File fXmlFile = new File(grammar_file);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
		 
				if (true)
				{ 	// debug
					System.out.println("Root element :" + doc.getDocumentElement().getNodeName());							
					PrintNode(doc.getDocumentElement());				
				}
				
				NodeList nodelist = doc.getElementsByTagName("node");
				System.out.println(nodelist.getLength());
				
				for (int inode= 0; inode < nodelist.getLength(); inode++) 
				{
					Node nNode = nodelist.item(inode);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) 
					{
					  
				      Element eElement = (Element) nNode;		 
				      String str_production = getTagValue("production", eElement);
				      String str_attribute = getTagValue("attribute", eElement);
				      String str_veml = getTagValue("veml", eElement);
				      
				      DependencyGrammarRule rule = new DependencyGrammarRule(str_production, str_attribute, str_veml);
				      rules.addElement(rule);				      
					}
				}
				
				if (false)
				{
					for (int irule = 0; irule < rules.size(); irule++)
					{
						DependencyGrammarRule rule = rules.elementAt(irule);
						System.out.println("\n\nrule " + irule + ":\n");
						System.out.println("production:\n" + rule.production);
						System.out.println("attribute:\n" + rule.ip_attribute);
						System.out.println("veml:\n" + rule.ip_veml);					
					}
				}
				
			  } catch (Exception e) {
				e.printStackTrace();
			  }						
	}
	
	private void GenerateEarleyGrammarText(String op_earley_grammar_file) throws Exception
	{ try {
			FileOutputStream fos = new FileOutputStream(op_earley_grammar_file); 
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			
			String prev_lhs = null; 
			
			for (int irule = 0; irule < rules.size(); irule++)
			{
				
				DependencyGrammarRule rule = rules.elementAt(irule);				
				
				if ((irule > 0) && (prev_lhs!=null) && prev_lhs.equals(rule.production_lhs))
				{
					out.write(" | " + rule.production_rhs);					
				} else
				{
					String str_prod =  rule.production;
					
					// Earley module uses "->"
					str_prod = str_prod.replace("--","->");			
					
					if (irule > 0) 
					{
						out.write("\n");
					}
							
					out.write(str_prod);
				}	
				prev_lhs = rule.production_lhs;
			}		
			out.write("\n");
			out.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	private void PrintNode(Node node) 
	{
		 if (node.getNodeType() == Node.ELEMENT_NODE) {
			 
		      Element eElement = (Element) node;

		      if (bDebug)
		      {
		    	  System.out.println("element :" + eElement.getNodeName());
		      }
		      
		      Node n2 = eElement.getFirstChild();
		      while (n2 != null)
		      {
		    	  PrintNode(n2);
		    	  n2 = n2.getNextSibling();
		      }
		   }
		 else if (node.getNodeType() == Node.TEXT_NODE) {
			 // org.w3c.dom.
			 Text text = (Text) node;		
			 if (bDebug)
		     {
				 System.out.println("text :" + text.getTextContent());
		     }
			 	      
		   } else
		   {
				 System.out.println("ERROR: unrecognized node type: " + node.getNodeType());
		   }		 
	}
		 
	 
	  public static String getTagValue(String sTag, Element eElement) 
	  {
		NodeList nlList = eElement.getElementsByTagName(sTag);
	 
		if (nlList.getLength()>0 )
		{
		    Node nValue = (Node) nlList.item(0);
		
		    Node nChild = nValue.getFirstChild();
		    if (nChild != null)
		    {
		    	return nChild.getNodeValue();
		    }
		}
		return null;
	  }
	 

		// propagate attribute from children node to current node
		public void PropagateAttributeAtNode(DependencyNode cur_node) throws Exception
		{
			boolean bMatch = false;
			for (int irule = 0; irule < rules.size(); irule++)
			{
				if (rules.elementAt(irule).IsProductionEquals(cur_node.production))
				{
					if (bMatch)
					{
						throw new Exception("ERROR multiple production matched " + cur_node.production);
					}
		
					// production is matched, apply the attribute functions
					rules.elementAt(irule).PropagateAttributeAtNode(cur_node);
					bMatch = true;
				}
			}
			
			if (bMatch == false)
			{
				throw new Exception("ERROR production not found " + cur_node.production);
			}			
		}

		public void GenerateVemlAtNode(VemlNode vemlroot, VemlNode vemlnode,  DependencyNode node ) throws Exception 
		{
			boolean bMatch = false;
			
			if (node.production == null)
			{	// likely to be terminal node
				return; 
			}
			
			for (int irule = 0; irule < rules.size(); irule++)
			{
				if (rules.elementAt(irule).IsProductionEquals(node.production))
				{
					if (bMatch)
					{
						throw new Exception("ERROR multiple production matched " + node.production);
					}
		
					// production is matched, apply the attribute functions
					GenerateVemlAtNodeWithRule(vemlroot, vemlnode, node, rules.elementAt(irule));
					bMatch = true;
				}
			}
			
			if (bMatch == false)
			{
				throw new Exception("ERROR production not found " + node.production);
			}				
		}
		public VemlNode GetTargetVemlNode(String arg, VemlNode vemlroot, VemlNode veml_current_node, Vector<String> new_nodes_var, Vector<VemlNode> new_nodes)
		{
			VemlNode node_tgt = null;
			if (arg.equals("ROOT"))
			{
				node_tgt = vemlroot;
			} else if (arg.equals("CURRENTNODE"))
			{
				node_tgt = veml_current_node;
			} else
			{
				for (int ivar = 0; ivar < new_nodes_var.size(); ivar++)
				{
					if (bDebug)
					{
						System.out.println("DEBUG new_nodes_var.elementAt(ivar) "+ new_nodes_var.elementAt(ivar));
					}
					
					if (arg.equals(new_nodes_var.elementAt(ivar)))
					{
						node_tgt = new_nodes.elementAt(ivar);
					}
				}
			}
			return node_tgt;
		}
		
		
		public	void CreateNewVemlNodeIfNotExist(
				String nodeName,
				String script, 
				VemlNode vemlroot,
				VemlNode vemlnode,  
				Vector<String> new_nodes_var, 
				Vector<VemlNode> new_nodes, 
				DependencyNode node,
				DependencyGrammarRule rule
				) throws Exception 
		{

			// e.g. N1 = CreateObjectNodeId(ROOT, msee:object.id);
			// e.g. n1 = CreateEventNodeId(ROOT, msee:event.id);
			
			String sa[] = script.split("[=(,)]");
			if (sa.length != 4)
			{
				throw new Exception("ERROR CreateVemlNode: unable to parse script " + script);
			}
			
		
			
			VemlNode node_tgt = GetTargetVemlNode(sa[2], vemlroot, vemlnode, new_nodes_var, new_nodes);					
			if (node_tgt == null)
			{
				throw new Exception("ERROR CreateVemlNode: unable to parse script " + script);						
			}
	
			String str_id = null;
			if (sa[3].charAt(0)=='\"')
			{
				str_id = sa[3].replace("\"", "");				
			} else
			{
				str_id =  node.GetAttributeValue(sa[3]);		
			}
			VemlNode new_v_node = node_tgt.FindChildNodeWithNameAndId(nodeName, str_id);
						
			
			if (new_v_node == null)
			{
				new_v_node = new VemlNode(nodeName);
				
				if (str_id != null)
				{		
					VemlNode id_node = new VemlNode("id", str_id);
					new_v_node.AddChild(id_node);							
				} else
				{
					throw new Exception("ERROR unable to find id " + script + " production: " + rule.production);
				}
				
				node_tgt.AddChild(new_v_node);
			}
			
			new_nodes_var.addElement(sa[0]);
			new_nodes.addElement(new_v_node);				
		}
			
		
		

		public	void CreateUnionNode(
				String script, 
				VemlNode vemlroot,
				VemlNode vemlnode,  
				Vector<String> new_nodes_var, 
				Vector<VemlNode> new_nodes, 
				DependencyNode node,
				DependencyGrammarRule rule
				) throws Exception 
		{
			// e.g.  U1 = AddUnionNode(N3);			
			String sa[] = script.split("[=(,)]");
			if (sa.length != 3)
			{
				throw new Exception("ERROR CreateVemlNode: unable to parse script " + script);
			}					
			VemlNode node_tgt = GetTargetVemlNode(sa[2], vemlroot, vemlnode, new_nodes_var, new_nodes);					
			if (node_tgt == null)
			{
				throw new Exception("ERROR CreateVemlNode: unable to parse script " + script);						
			}	
		
			VemlNode new_v_node = new VemlNode("union");
			
			node_tgt.AddChild(new_v_node);
			
			new_nodes_var.addElement(sa[0]);
			new_nodes.addElement(new_v_node);				
		}
		
		

		public	void CreateGroupNode(
				String script, 
				VemlNode vemlroot,
				VemlNode vemlnode,  
				Vector<String> new_nodes_var, 
				Vector<VemlNode> new_nodes, 
				DependencyNode node,
				DependencyGrammarRule rule
				) throws Exception 
		{
			// e.g.  U1 = AddUnionNode(N3);			
			String sa[] = script.split("[=(,)]");
			if (sa.length != 3)
			{
				throw new Exception("ERROR CreateVemlNode: unable to parse script " + script);
			}					
			VemlNode node_tgt = GetTargetVemlNode(sa[2], vemlroot, vemlnode, new_nodes_var, new_nodes);					
			if (node_tgt == null)
			{
				throw new Exception("ERROR CreateVemlNode: unable to parse script " + script);						
			}	
		
			VemlNode new_v_node = new VemlNode("group");
			
			node_tgt.AddChild(new_v_node);
			
			new_nodes_var.addElement(sa[0]);
			new_nodes.addElement(new_v_node);				
		}
		
		
			
		public void GenerateVemlAtNodeWithRule(
				VemlNode vemlroot, 
				VemlNode vemlnode,  
				DependencyNode node, 
				DependencyGrammarRule rule) throws Exception 
		{
			if (bDebug)
			{
				System.out.println("DEBUG GenerateVemlAtNodeWithRule " + rule.production);
			}
			
			Vector<String> new_nodes_var= new Vector<String>();  // variable name, e.g. "N1", "N2", etc.
			Vector<VemlNode> new_nodes= new Vector<VemlNode>();
			
			if (rule.veml_script == null)
			{
				return;
			}
			for (int i = 0; i < rule.veml_script.length; i++)
			{
				String script = rule.veml_script[i];
				
				if (bDebug)
				{	System.out.println("\tDEBUG GenerateVemlAtNodeWithRule: i " + i);
					System.out.println("\tDEBUG GenerateVemlAtNodeWithRule: script " + script);
				}
				
				if (script.contains("AddAttributeString"))
				{
					// e.g. AddAttributeString(CURRENTNODE, ‘object’, msee:object.id);
					String sa[] = script.split("[(,)]");
					if (sa.length != 4)
					{
						throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);
					}
					
					VemlNode node_tgt = GetTargetVemlNode(sa[1], vemlroot, vemlnode, new_nodes_var, new_nodes);					
					if (node_tgt == null)
					{
						throw new Exception("ERROR unable to parse script " + script +  " arg: " + sa[1] + " production: " + rule.production);						
					}
					
					String attr_name = sa[2].replace("\"", "");					
					String value = node.GetAttributeValue(sa[3]);
					
					if (value != null)
					{					
						/*old
						 	VemlNode new_v_node = new VemlNode(attr_name, value);
						 	node_tgt.AddChild(new_v_node);
						 	*/
						node_tgt.AddSimpleChildEndNodeIfNotExist(attr_name, value);						
					} else
					{
						System.out.println("WARNING cannot find attribute " + sa[3]);
					}
					
				} else if (script.contains("CreateObjectNodeId"))
				{
					// e.g. N1 = CreateObjectNodeId(ROOT, msee:object.id);
					
					CreateNewVemlNodeIfNotExist(
							"object",
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 
					
					
				} else if (script.contains("CreateEventNodeId"))
				{
					// e.g. n1 = CreateEventNodeId(ROOT, msee:event.id);
					
					CreateNewVemlNodeIfNotExist(
							"event",
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 		
					
					
				} else if (script.contains("CreateTimeNodeId"))
				{
					// e.g. n1 = CreateEventNodeId(ROOT, msee:event.id);
					
					CreateNewVemlNodeIfNotExist(
							"time",
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 		
					
					
				}
				else if (script.contains("CreateSelectNodeId"))
				{
					// e.g. n1 = CreateEventNodeId(ROOT, msee:event.id);
					
					CreateNewVemlNodeIfNotExist(
							"select",
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 
				}else if (script.contains("CreateVariableNodeId"))
				{
					// e.g. n1 = CreateEventNodeId(ROOT, msee:event.id);
					
					CreateNewVemlNodeIfNotExist(
							"variable",
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 
				} else if (script.contains("AddUnionNode"))
				{
					// e.g.  U1 = AddUnionNode(N3);
					CreateUnionNode(
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 
				} else if (script.contains("AddGroupNode"))
				{
					// e.g.  G1 = AddGroupNode(U1);
					CreateGroupNode(
							script, 
							vemlroot,
							vemlnode,  
							new_nodes_var, 
							new_nodes, 
							node,
							rule ); 
				} 						
				else if (script.contains("CreateFluentNode"))
					{
						// e.g. n1 = CreateFluentNode(ROOT, msee:fluent.type, msee:fluent.id);
						String sa[] = script.split("[=(,)]");
						if (sa.length != 5)
						{
							throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);
						}
						
						String str_type =  node.GetAttributeValue(sa[3]);						
						if (str_type == null)
						{		
							throw new Exception("ERROR unable to find msee:fluent.type " + script + " production: " + rule.production);
						}	
						VemlNode new_v_node = new VemlNode(str_type);
						new_nodes_var.addElement(sa[0]);
						new_nodes.addElement(new_v_node);
						
						VemlNode node_tgt = GetTargetVemlNode(sa[2], vemlroot, vemlnode, new_nodes_var, new_nodes);					
						if (node_tgt == null)
						{
							throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);						
						}	
												
						String str_id =  node.GetAttributeValue(sa[4]);
						
						if (str_id != null)
						{		
							VemlNode id_node = new VemlNode("id", str_id);
							new_v_node.AddChild(id_node);
							node_tgt.AddChild(new_v_node);
						} else
						{
							throw new Exception("ERROR unable to find object id " + script + " production: " + rule.production);
						}						
					}
				else if (script.contains("CreateFluentChangeNode"))
				{
					// e.g. n1 = 	N2 = CreateFluentChangeNode(ROOT, msee:fluentchange.type, msee:fluentchange.id);
					String sa[] = script.split("[=(,)]");
					if (sa.length != 5)
					{
						throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);
					}
					
					String str_type =  node.GetAttributeValue(sa[3]);						
					if (str_type == null)
					{		
						throw new Exception("ERROR unable to find msee:fluent.type " + script + " production: " + rule.production);
					}	
					VemlNode new_v_node = new VemlNode(str_type);
					new_nodes_var.addElement(sa[0]);
					new_nodes.addElement(new_v_node);
					
					VemlNode node_tgt = GetTargetVemlNode(sa[2], vemlroot, vemlnode, new_nodes_var, new_nodes);					
					if (node_tgt == null)
					{
						throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);						
					}	
											
					String str_id =  node.GetAttributeValue(sa[4]);
					
					if (str_id != null)
					{		
						VemlNode id_node = new VemlNode("id", str_id);
						new_v_node.AddChild(id_node);
						node_tgt.AddChild(new_v_node);
					} else
					{
						throw new Exception("ERROR unable to find object id " + script + " production: " + rule.production);
					}						
				}
				else if (script.contains("CreateCausalRelationNode"))
				{
					// e.g. n1 = 	N3 = CreateCausalRelationNode(ROOT, msee:causalrelation.type, msee:causalrelation.id);
					String sa[] = script.split("[=(,)]");
					if (sa.length != 5)
					{
						throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);
					}
					
					String str_type =  node.GetAttributeValue(sa[3]);						
					if (str_type == null)
					{		
						throw new Exception("ERROR unable to find msee:fluent.type " + script + " production: " + rule.production);
					}	
					VemlNode new_v_node = new VemlNode(str_type);
					new_nodes_var.addElement(sa[0]);
					new_nodes.addElement(new_v_node);
					
					VemlNode node_tgt = GetTargetVemlNode(sa[2], vemlroot, vemlnode, new_nodes_var, new_nodes);					
					if (node_tgt == null)
					{
						throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);						
					}	
											
					String str_id =  node.GetAttributeValue(sa[4]);
					
					if (str_id != null)
					{		
						VemlNode id_node = new VemlNode("id", str_id);
						new_v_node.AddChild(id_node);
						node_tgt.AddChild(new_v_node);
					} else
					{
						throw new Exception("ERROR unable to find object id " + script + " production: " + rule.production);
					}						
				}
				else if (script.contains("ExpandNode"))
				{
					if (false)
					{	// this rule is obsolete, we will always expand all non-terminal nodes
						
						// e.g.  ExpandNode(ROOT,ARG1);
						String sa[] = script.split("[(,)]");
						if (sa.length != 3)
						{
							throw new Exception("ERROR unable to parse script " + script);
						}
						
						VemlNode node_tgt = GetTargetVemlNode(sa[1], vemlroot, vemlnode, new_nodes_var, new_nodes);					
						if (node_tgt == null)
						{
							throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);						
						}
						
						if (sa[2].substring(0,3).equals("ARG") == false)
						{
							throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);
						}
						
						int ichild = Integer.parseInt(sa[2].substring(3,4)) - 1;  		// -1 for zero-starting index
						
						if ((ichild>=0) && (ichild< node.children.size()))
						{
							GenerateVemlAtNode(vemlroot, node_tgt,  node.children.elementAt(ichild)  );						
						} else
						{
							throw new Exception("ERROR unable to parse script " + script + " production: " + rule.production);
						}
					}
					
				} else if (script.contains("NIL"))
				{
					// DO NOTHING
				} else
				{
					throw new Exception("ERROR unrecognized script " + script);
				}				
			} // for (int i = 0; i < rule.veml_script.length; i++)
			
			// now expand all node using root
			// 20120409, still testing. 
			for (int ichild = 0; ichild < node.children.size(); ichild++)
			{
				GenerateVemlAtNode(vemlroot, vemlroot,  node.children.elementAt(ichild)  );	
			}
		}
}
