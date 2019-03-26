
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

public class DependencyGrammarRule {

	public String production;
	public String production_lhs = null;
	public String production_rhs = null;
	public String ip_attribute;
	public String ip_veml;
	public String attribute_rule[] = null;
	public String veml_script[] = null;


	final private boolean bDebug = false;  
	
	DependencyGrammarRule(String arg_production, String arg_attr, String arg_veml) throws Exception {
		production = arg_production;
		ip_attribute = arg_attr;
		ip_veml = arg_veml;
		
		// old, don't need for now
		// production = production.replace("--","->");
		
		if ( ip_attribute != null )
		{
			ip_attribute = ip_attribute.replace(" ", "");
			ip_attribute = ip_attribute.replace("\n", "");
			ip_attribute = ip_attribute.replace("\t", "");	
			
			attribute_rule = ip_attribute.split(";");			
		} else
		{
			ip_attribute =  new String("");	
			attribute_rule = new String[0];			
		}
		
		if ( ip_veml != null )
		{
			ip_veml = ip_veml.replace(" ", "");
			ip_veml = ip_veml.replace("\n", "");
			ip_veml = ip_veml.replace("\t", "");
			
			veml_script = ip_veml.split(";");
		}
		else
		{	ip_veml =  new String("");	
			veml_script = new String[0];			
		}
		
		String sa[] = production.split(" -- ");
		production_lhs = sa[0];
		production_rhs = sa[1];		
		if (bDebug)
		{
			System.out.println("DEBUG production_lhs: " + production_lhs);
			System.out.println("DEBUG production_rhs: " + production_rhs);
		}
	}
	
	public boolean IsProductionEquals(String a_production)
	{
		return (a_production.compareTo(this.production) ==0);
	}	
		
	
	public DependencyAttribute GenerateLhs(String rule, DependencyAttribute attr_rhs) throws Exception
	{
		
		String sa[] = rule.split("=");
		if (sa.length!=2)
		{
			throw new Exception("ERROR, unable to parse rule " + rule);
		}
		
		int i = sa[1].indexOf('.');
		
		String rhs = sa[1].substring(i+1);
		
	
		
		String sa_lhs[] = sa[0].split("\\.");
		String sa_rhs[] = rhs.split("\\.");
		String sa_attrname[] = attr_rhs.name.split("\\.");
		
		if (sa_lhs.length != 2) 
		{
			throw new Exception("ERROR, unable to parse rule sa_lhs" + sa_lhs);
		}	
		if ((sa_rhs.length != 2) && (sa_rhs.length != 3))  
		{
			throw new Exception("ERROR, unable to parse rule sa_rhs" + sa_rhs);
		}	
		
		if (sa_attrname.length != 2) 
		{
			throw new Exception("ERROR, unable to parse rule sa_attrname" + sa_attrname);
		}	
		
		String attr_value = attr_rhs.value;
		String attr_name = ""; 
		
		if (sa_lhs[0].equals("*"))
		{
			attr_name = sa_attrname[0];			
		} else
		{
			attr_name = sa_lhs[0];			
		}
		attr_name += ".";
		if (sa_lhs[1].equals("*"))
		{
			attr_name += sa_attrname[1];			
		} else
		{
			attr_name += sa_lhs[1];			
		}
		
		return new DependencyAttribute(attr_name, attr_value);		
	}
	
	public boolean IsMatchAttrNameRhs(String fct_rhs, DependencyAttribute attr_rhs) throws Exception
	{
		/*

		if (fct_rhs.substring(0,3).equals("ARG"))
		{
			int i = fct_rhs.indexOf('.');
			
			String s = fct_rhs.substring(i+1);
			
		*/ 	
		
		String s = fct_rhs;
			
		String sa1[] = s.split("\\.");
		String sa2[] = attr_rhs.name.split("\\.");
		
		// debug
		// System.out.println("sa1.length" + sa1.length);			
		
		if (sa1.length != 2) 
		{
			throw new Exception("ERROR, unable to parse rule " + s);
		}	
		if (sa2.length != 2) 
		{
			throw new Exception("ERROR, unable to parse pattern " + attr_rhs);
		}	
		
		for (int m = 0; m < 2; m++)
		{
			if ((sa1[m].equals(sa2[m]) == false) && (sa1[m].equals("*") == false))
			{
				return false;
				
			}				
		}	
		
		
		return true;	
		
		
		/* old,  20120329 
		 * remove after test
		if (fct_rhs.substring(0,3).equals("ARG"))
		{
			int i = fct_rhs.indexOf('.');
			
			String s = fct_rhs.substring(i+1);
			
			
			
			String sa1[] = s.split("\\.");
			String sa2[] = attr_rhs.name.split("\\.");
			
			// debug
			// System.out.println("sa1.length" + sa1.length);			
			
			if (sa1.length != 2) 
			{
				throw new Exception("ERROR, unable to parse rule " + s);
			}	
			if (sa2.length != 2) 
			{
				throw new Exception("ERROR, unable to parse pattern " + attr_rhs);
			}	
			
			for (int m = 0; m < 2; m++)
			{
				if ((sa1[m].equals(sa2[m]) == false) && (sa1[m].equals("*") == false))
				{
					return false;
					
				}				
			}	
			
			
		} else
		{
			throw new Exception("ERROR, unable to parse fct_rhs " + fct_rhs);
		}
		return true;
		*/ 		
	}
	

	public void PropagateAttributeAtNode(DependencyNode cur_node) throws Exception
	{
		if (attribute_rule == null)
		{	return;		
		}
		
		if (bDebug)
		{
			System.out.println("DEBUG PropagateAttributeAtNode production: " + production);
		}
		
		
		for (int irule = 0; irule < attribute_rule.length; irule++)
		{
			// debug
			// System.out.println("attr_function: " + attribute_rule[irule]);
			
			String rule = attribute_rule[irule];
			if (rule == null)
			{	continue;			
			}
			String sa[] = rule.split("=");
			if (sa.length!=2)
			{
				System.out.println("rule.length(): " + rule.length());
				
				throw new Exception("ERROR, unable to parse rule " + rule);
			}
			
			// debug
			// System.out.println("sa[1].substring(0,3)" + sa[1].substring(0,3));
			
			if (sa[1].substring(0,3).equals("ARG"))
			{
				int iarg = Integer.parseInt(sa[1].substring(3,4)) - 1;  		// -1 for zero-starting index
				if ((iarg>=0) && (iarg< cur_node.children.size()))
				{
					DependencyNode arg_node = cur_node.children.elementAt(iarg);
					
					// get the attribute part of rhs
					int i_tmp = sa[1].indexOf('.');
					String s_rhs_short = sa[1].substring(i_tmp+1);
					
					Vector<DependencyAttribute> attrVt = null;  
					
					// see if rhs refer to a specific field, if so, use the fieldAttributes data
					if ((s_rhs_short.length() > 5) && (s_rhs_short.substring(0,5).equals("FIELD")))
					{
						int ifield = Integer.parseInt(s_rhs_short.substring(5,6)) - 1;					
						attrVt = arg_node.fieldAttributes.elementAt(ifield);
						
						if (bDebug)
						{
							System.out.println("DEBUG ifield "+ ifield +  " BEFORE s_rhs_short "  + s_rhs_short);
						}
						
						
						int i_tmp2 = s_rhs_short.indexOf('.');
						String s_tmp =  s_rhs_short.substring(i_tmp2+1);
						s_rhs_short = new String(s_tmp);
						
						if (bDebug)
						{
							System.out.println("DEBUG ifield "+ ifield +  " AFTER s_rhs_short "  + s_rhs_short);
						}
						
						
					} else
					{					
						attrVt = arg_node.attributes;
					}
						
					
					if (bDebug)
					{
						System.out.println("DEBUG s_rhs_short "  + s_rhs_short);
						System.out.println("DEBUG attrVt.size() "  + attrVt.size());
					}
					
					// old. for (int iattr = 0; iattr < arg_node.attributes.size(); iattr++)
					for (int iattr = 0; iattr < attrVt.size(); iattr++)
					{
						//old, DependencyAttribute attr_rhs =  arg_node.attributes.elementAt(iattr);
						DependencyAttribute attr_rhs =  attrVt.elementAt(iattr);
						
						//old, remove after test
						// if ( IsMatchAttrNameRhs( sa[1], attr_rhs))
						
						if (bDebug)
						{
							System.out.println("DEBUG  attr_rhs.name "  + attr_rhs.name + " attr_rhs.value "  + attr_rhs.value);
							System.out.println("DEBUG  \t trying to match attribute " + attr_rhs.name + " value:" + attr_rhs.value);
						}
						
						if ( IsMatchAttrNameRhs( s_rhs_short, attr_rhs))
						{
							
							DependencyAttribute attr_lhs = GenerateLhs(rule, attr_rhs);
							cur_node.AddAttribute(attr_lhs);
							if (bDebug)
							{
								System.out.println("DEBUG  IsMatchAttrNameRhs YES ");
								System.out.println("DEBUG  cur_node.AddAttribute " + attr_lhs.name + " value " + attr_lhs.value );
							}	
							
							
						} else
						{			
							if (bDebug)
							{
								System.out.println("DEBUG  IsMatchAttrNameRhs NO ");
							}
						}										
					}
					
					
				}				
			} else
			{
				// rhs is not a argument, then it is constant.
				String rhs = sa[1];
				DependencyAttribute new_attr = new DependencyAttribute(sa[0], rhs.replace("\"", ""));
				cur_node.AddAttribute(new_attr);				
			}						
		}		
	}
	

	
}
