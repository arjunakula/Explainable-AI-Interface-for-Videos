/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 
package TextParser;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SparqlGenerator {
	
	// final static String SPARQL_RDF_TYPE = "rdf:type";
	final static String SPARQL_RDF_TYPE = "rdf:type/rdfs:subClassOf*";	
	
	final static String SPARQL_NS_MSEE = "msee";
	final static String SPARQL_MSEE_EVENT = "Event";
	final static String SPARQL_MSEE_HASTYPE = "hasType";
	final static String SPARQL_MSEE_HASAGENT = "hasAgent";
	final static String SPARQL_MSEE_HASLOCATION = "hasLocation";
	final static String SPARQL_MSEE_HASLOCATIONFROM = "hasLocationFrom";
	final static String SPARQL_MSEE_HASLOCATIONTO = "hasLocationTo";
	
	
	final static String SPARQL_MSEE_HASNAME = "hasName";
	final static String SPARQL_MSEE_HASFLUENT = "hasFluent";
	final static String SPARQL_MSEE_FLUENT_DOORSTATUS = "DoorStatus";
	final static String SPARQL_MSEE_HASVALUE = "hasValue";
	final static String SPARQL_MSEE_HASPATIENT = "hasPatient";
	final static String SPARQL_MSEE_HASEFFECT = "hasEffect";
	final static String SPARQL_MSEE_HASCAUSE = "hasCause";
	final static String SPARQL_MSEE_HASTOVALUE = "hasToValue";
	final static String SPARQL_MSEE_HASCOLOR = "hasColor";
		
	final static String SPARQL_MSEE_HASTIMEBEGIN = "hasTimeBegin";
	final static String SPARQL_MSEE_HASTIMEEND = "hasTimeEnd";
	final static String SPARQL_MSEE_HASTIMEAT = "hasTimeAt";
		
	final static String SPARQL_MSEE_HASTIME = "hasTime";
	final static String SPARQL_MSEE_PURPOSE = "purpose";
	final static String SPARQL_MSEE_CAUSE = "cause";
	

	public static String m_object_long_id_toreplacewith ="";
	
	public static String m_query_scenetime_start ="";
	public static String m_query_scenetime_end ="";
	

		
	Vector<String> selectVt = new Vector<String>();
	Vector<String> variableVt = new Vector<String>();
	Vector<SparqlTriple> tripleVt = new Vector<SparqlTriple>();
	Vector<SparqlUnion> unionVt = new Vector<SparqlUnion>();

	
	public boolean isQuery = false;
	
	// there are two versions on how events are represented
	// if bUseOntologyEventDef is true (this is the newer version)
	//		event type is defined in ontology, e.g. msee:drop
	//		e.g. (event_id  rdf:type msee:Drop)
	// if bUseOntologyEventDef is false (older version)
	//		event type is defined as a value of property msee:type,
	//		e.g. (event_id rdf:type msee:Event)
	//			(event_id msee:type "drop")
	
	public static enum SPARQL_EVENT_DEF_TYPE { 
		SPARQL_EVENT_DEF_BASED_ON_ONTOLOGY,			// use this for msee Phase 2,3 data	 
		SPARQL_EVENT_DEF_USING_PROPERTY};			// use this for msee Phase 1 data
		
				
	public static SPARQL_EVENT_DEF_TYPE bUseOntologyEventDef = SPARQL_EVENT_DEF_TYPE.SPARQL_EVENT_DEF_BASED_ON_ONTOLOGY ;
	
	
	SparqlGenerator(
			Document veml_xml_doc, 
			String sparql_op_file
		) throws Exception 
	{
		 try 
		 {
			// get data first			
			
			NodeList nl_scene = veml_xml_doc.getElementsByTagName(VemlNode.VEML_SCENE);
			if (nl_scene.getLength() ==0)
			{
				throw new Exception("Scene node not found.");
			}
			
			Element e_scene = (Element) nl_scene.item(0);
			
			// search for <select> 
			NodeList nl_select = e_scene.getElementsByTagName(VemlNode.VEML_SELECT);
			if (nl_select.getLength() ==0)
			{
				// no select found, this is not a query. no need to output sparql
				isQuery  = false; 
				return; 
			}
			isQuery   = true;
			
			for (int inode= 0; inode < nl_select.getLength(); inode++) 
			{
				Element e_select = (Element) nl_select.item(inode);	
				String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_select);
				selectVt.addElement(new String(str_id));				
			}
			
			// search for <variable>
			NodeList nl_var = e_scene.getElementsByTagName(VemlNode.VEML_VARIABLE);
			for (int inode= 0; inode < nl_var.getLength(); inode++) 
			{
				Element e_var = (Element) nl_var.item(inode);
				String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_var);
				variableVt.addElement(new String(str_id));				
		  	}
			
			// search for <object>
			NodeList nl_obj = e_scene.getElementsByTagName(VemlNode.VEML_OBJECT);
			for (int inode= 0; inode < nl_obj.getLength(); inode++) 
			{
				Element e_obj = (Element) nl_obj.item(inode);
				AddSparqlObject(e_obj);				
		  	}

			// search for <event>
			NodeList nl_event = e_scene.getElementsByTagName(VemlNode.VEML_EVENT);
			for (int inode= 0; inode < nl_event.getLength(); inode++) 
			{
				Element e_event = (Element) nl_event.item(inode);
				AddSparqlEvent(e_event);				
		  	}
			
			// search for <time>
			NodeList nl_time = e_scene.getElementsByTagName(VemlNode.VEML_TIME);
			for (int inode= 0; inode < nl_time.getLength(); inode++) 
			{
				Element e_time = (Element) nl_time.item(inode);
				AddSparqlTime(e_time);				
		  	}
			
			/*
			 * <FluentChange>
<id>fluentchange_id</id>
<hasToValue>1</hasToValue>
</FluentChange>
<CausalRelation>
<id>causalrelation_id</id>
<hasEffect>fluentchange_id</hasEffect>
<hasCause>event_id</hasCause>
</CausalRelation>

			 */
			// search for FluentChange
			NodeList nl_fluentchange = e_scene.getElementsByTagName(VemlNode.VEML_FLUENTCHANGE);
			for (int inode= 0; inode < nl_fluentchange.getLength(); inode++) 
			{
				Element e_fluentchange = (Element) nl_fluentchange.item(inode);
				AddSparqlFluentChange(e_fluentchange);				
		  	}
			
			// search for CausalRelation
			NodeList nl_causalrelation = e_scene.getElementsByTagName(VemlNode.VEML_CAUSALRELATION);
			for (int inode= 0; inode < nl_causalrelation.getLength(); inode++) 
			{
				Element e_causalrelation = (Element) nl_causalrelation.item(inode);
				AddSparqlCausalRelation(e_causalrelation);				
		  	}
			
			// now, output the sparql
			OutputSparql( veml_xml_doc, sparql_op_file);
			

         } catch(Exception e) {
             System.out.println(e.toString());
             e.printStackTrace();
         }		

	}
	
	private void AddSparqlObject( Element e_obj ) throws Exception 
	{
		String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_obj);
		String str_type = DependencyGrammar.getTagValue(VemlNode.VEML_TYPE, e_obj);
		
		if (str_type != null)
		{
			
			Boolean isVarType = IsVariable(str_type);
			
			SparqlTriple t = new SparqlTriple(
					str_id, 					
					SPARQL_RDF_TYPE, 
					isVarType ? str_type : SPARQL_NS_MSEE + ":" + str_type, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.RDF_PROPERTY,
					isVarType ? SparqlTriple.SparqlType.VARIABLE : SparqlTriple.SparqlType.MSEE_CLASS				
					);
			tripleVt.addElement(t);
		}
		
		InsertLocationIfExist(  e_obj, str_id );
		InsertNameIfExist(  e_obj, str_id );	
		InsertColorIfExist(  e_obj, str_id );	
		
		InsertFluentIfExist( e_obj, str_id ); 		
	}
	
/*
 * <FluentChange>
<id>fluentchange_id</id>
<hasToValue>1</hasToValue>
</FluentChange>
 */
	private void AddSparqlFluentChange( Element e_obj ) throws Exception 
	{
		String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_obj);
		
		String str_to_value = DependencyGrammar.getTagValue(VemlNode.VEML_HASTOVALUE, e_obj);
		if (str_to_value != null)
		{
			SparqlTriple t = new SparqlTriple(
					str_id, 					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASTOVALUE,  
					str_to_value, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.RDF_PROPERTY,
					SparqlTriple.SparqlType.CONST_STRING					
					);
			tripleVt.addElement(t);
		} 		
	}
	
	

	/*
	 * <CausalRelation>
<id>causalrelation_id</id>
<hasEffect>fluentchange_id</hasEffect>
<hasCause>event_id</hasCause>
</CausalRelation>
	 */
		private void AddSparqlCausalRelation( Element e_obj ) throws Exception 
		{
			String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_obj);
			
			String str_has_effect = DependencyGrammar.getTagValue(VemlNode.VEML_HASEFFECT, e_obj);
			if (str_has_effect != null)
			{
				SparqlTriple t = new SparqlTriple(
						str_id, 					
						SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASEFFECT,  
						str_has_effect, 
						SparqlTriple.SparqlType.VARIABLE,
						SparqlTriple.SparqlType.RDF_PROPERTY,
						SparqlTriple.SparqlType.VARIABLE				
						);
				tripleVt.addElement(t);
			} 		
			
			String str_has_cause = DependencyGrammar.getTagValue(VemlNode.VEML_HASCAUSE, e_obj);
			if (str_has_cause != null)
			{
				SparqlTriple t = new SparqlTriple(
						str_id, 					
						SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASCAUSE,  
						str_has_cause, 
						SparqlTriple.SparqlType.VARIABLE,
						SparqlTriple.SparqlType.RDF_PROPERTY,
						SparqlTriple.SparqlType.VARIABLE				
						);
				tripleVt.addElement(t);
			} 	
		}
		
		
	
	private void InsertFluentNode( String fluent_type, String str_id, String str_value) throws Exception
	{
		SparqlTriple t1 = new SparqlTriple(
				str_id, 					
				SPARQL_RDF_TYPE, 
				SPARQL_NS_MSEE + ":" + fluent_type, 
				SparqlTriple.SparqlType.VARIABLE,
				SparqlTriple.SparqlType.RDF_PROPERTY,
				SparqlTriple.SparqlType.MSEE_CLASS					
				);
		
		Boolean isVarValue = IsVariable(str_value);
		
		SparqlTriple t2 = new SparqlTriple(
				str_id, 					
				SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASVALUE,  
				str_value,
				SparqlTriple.SparqlType.VARIABLE,
				SparqlTriple.SparqlType.MSEE_PROPERTY,
				isVarValue ? SparqlTriple.SparqlType.VARIABLE : SparqlTriple.SparqlType.CONST_STRING
				);		
		
		tripleVt.addElement(t1);
		tripleVt.addElement(t2);
		
		// KIV: check duplicate triple
		
	}
	
	private void InsertTimeIfExist( Element e_obj, String cur_id ) throws Exception 
	{
		String str_time = DependencyGrammar.getTagValue(VemlNode.VEML_TIME, e_obj);
		if (str_time != null)
		{
			SparqlTriple t = new SparqlTriple(
					cur_id, 					
					SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASTIME,
					str_time, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,
					SparqlTriple.SparqlType.VARIABLE										
					);
			tripleVt.addElement(t);
		}
	}
	
	private void InsertLocationIfExist( Element e_obj, String cur_id ) throws Exception 
	{
		String str_loc = DependencyGrammar.getTagValue(VemlNode.VEML_LOCATION, e_obj);
		if (str_loc != null)
		{
			SparqlTriple t = new SparqlTriple(
					cur_id, 					
					SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASLOCATION,
					str_loc, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,
					SparqlTriple.SparqlType.VARIABLE										
					);
			tripleVt.addElement(t);
		}
		
		String str_loc_from = DependencyGrammar.getTagValue(VemlNode.VEML_LOCATION_FROM, e_obj);
		if (str_loc_from != null)
		{
			SparqlTriple t = new SparqlTriple(
					cur_id, 					
					SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASLOCATIONFROM,
					str_loc_from, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,
					SparqlTriple.SparqlType.VARIABLE										
					);
			tripleVt.addElement(t);
		}
		
		String str_loc_to = DependencyGrammar.getTagValue(VemlNode.VEML_LOCATION_TO, e_obj);
		if (str_loc_to != null)
		{
			SparqlTriple t = new SparqlTriple(
					cur_id, 					
					SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASLOCATIONTO,
					str_loc_to, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,
					SparqlTriple.SparqlType.VARIABLE										
					);
			tripleVt.addElement(t);
		}
	}
	
	private void InsertNameIfExist( Element e_obj, String cur_id ) throws Exception 
	{
		String str_name = DependencyGrammar.getTagValue(VemlNode.VEML_NAME, e_obj);
		if (str_name != null)
		{
			
			Boolean isVarValue = IsVariable(str_name);
			
			SparqlTriple t = new SparqlTriple(
					cur_id, 					
					SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASNAME,
					str_name, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,
					// SparqlTriple.SparqlType.CONST_STRING
					isVarValue ? SparqlTriple.SparqlType.VARIABLE : SparqlTriple.SparqlType.CONST_STRING
					);
			
			if (isVarValue)
			{
				t.bOptional = true;
			}
			tripleVt.addElement(t);
		}
	}
	
	private void InsertColorIfExist( Element e_obj, String cur_id ) throws Exception 
	{
		String str_name = DependencyGrammar.getTagValue(VemlNode.VEML_COLOR, e_obj);
		if (str_name != null)
		{
			
			Boolean isVarValue = IsVariable(str_name);
			
			SparqlTriple t = new SparqlTriple(
					cur_id, 					
					SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASCOLOR,
					str_name, 
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,
					// SparqlTriple.SparqlType.CONST_STRING
					isVarValue ? SparqlTriple.SparqlType.VARIABLE : SparqlTriple.SparqlType.CONST_STRING
					);
			
			if (isVarValue)
			{
				t.bOptional = true;
			}
			tripleVt.addElement(t);
		}
	}
	
	

	private Boolean IsVariable(String str)
	{
		
		for (int i = 0; i < variableVt.size(); i++)
		{
			if (variableVt.elementAt(i).equals(str))
			{
				return true;
			}
		}
		return false; 
	}
	
	
	private void InsertFluentIfExist( Element e_obj, String cur_id ) throws Exception 
	{
		InsertFluentDoorStatusIfExist(e_obj, cur_id);		
	}
	
	private void InsertFluentDoorStatusIfExist( Element e_obj, String cur_id ) throws Exception 
	{
		NodeList nl_fluent = e_obj.getElementsByTagName(VemlNode.VEML_FLUENT_DOORSTATUS);
				
		for (int inode= 0; inode < nl_fluent.getLength(); inode++) 
		{
			Element e_fluent = (Element) nl_fluent.item(inode);
			String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_fluent);
			String str_value = DependencyGrammar.getTagValue(VemlNode.VEML_VALUE, e_fluent);
			

			
			if ((str_id != null) && (str_value != null))
			{
				SparqlTriple t = new SparqlTriple(
						cur_id, 					
						SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_HASFLUENT,
						str_id, 
						SparqlTriple.SparqlType.VARIABLE,
						SparqlTriple.SparqlType.MSEE_PROPERTY,
						SparqlTriple.SparqlType.VARIABLE 
						);
				tripleVt.addElement(t);
				InsertFluentNode(SPARQL_MSEE_FLUENT_DOORSTATUS, str_id, str_value);
			}
	  	}		
	}
	
	private void AddSparqlTime( Element e_time ) throws Exception 
	{		
		String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_time );
		String str_begin = DependencyGrammar.getTagValue(VemlNode.VEML_BEGIN, e_time);
		String str_end = DependencyGrammar.getTagValue(VemlNode.VEML_END, e_time);
		String str_at = DependencyGrammar.getTagValue(VemlNode.VEML_AT, e_time);

		SparqlTriple t_begin = null;
		SparqlTriple t_end = null;
		SparqlTriple t_at = null;
		
		if (str_begin != null)
		{
			// KIV, ensure str_agent is a variable
			t_begin = new SparqlTriple(
					str_id,					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASTIMEBEGIN, 
					str_begin,
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
		}
		if (str_end != null)
		{
			// KIV, ensure str_agent is a variable
			t_end = new SparqlTriple(
					str_id,					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASTIMEEND, 
					str_end,
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
		}
		if (str_at != null)
		{
			// KIV, ensure str_agent is a variable
			t_at = new SparqlTriple(
					str_id,					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASTIMEAT, 
					str_at,
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
		}
		
		if ((t_at != null) && ((t_begin != null)||(t_end != null)))
		{
			// use union tag
			SparqlUnion union = new SparqlUnion();
			SparqlGroup g1 = new SparqlGroup();
			SparqlGroup g2 = new SparqlGroup();
			g1.AddTriple(t_at);
			
			if (t_begin != null)
			{
				g2.AddTriple(t_begin);
			}
			if (t_end != null)
			{
				g2.AddTriple(t_end);
			}
			union.AddGroup(g1);
			union.AddGroup(g2);
			unionVt.addElement(union);
			
		} else
		{
			if (t_at != null)
			{
				tripleVt.addElement(t_at);
			}
			if (t_begin != null)
			{
				tripleVt.addElement(t_begin);
			}
			if (t_end != null)
			{
				tripleVt.addElement(t_end);
			}
		}
	}
	
	
	
	private void AddSparqlEvent( Element e_event ) throws Exception 
	{
		String str_id = DependencyGrammar.getTagValue(VemlNode.VEML_ID, e_event );
		String str_type = DependencyGrammar.getTagValue(VemlNode.VEML_TYPE, e_event);
		String str_agent = DependencyGrammar.getTagValue(VemlNode.VEML_AGENT, e_event);
		String str_patient = DependencyGrammar.getTagValue(VemlNode.VEML_PATIENT, e_event);
		String str_purpose = DependencyGrammar.getTagValue(VemlNode.VEML_PURPOSE, e_event);
		
		if (str_type != null)
		{
			if (bUseOntologyEventDef == SPARQL_EVENT_DEF_TYPE.SPARQL_EVENT_DEF_BASED_ON_ONTOLOGY)
			{
				Boolean isVarType = IsVariable(str_type);
				
				if (isVarType)
				{
					SparqlTriple t1 = new SparqlTriple(
							str_id, 
							SPARQL_RDF_TYPE,					
							str_type,
							SparqlTriple.SparqlType.VARIABLE,
							SparqlTriple.SparqlType.RDF_PROPERTY,
							SparqlTriple.SparqlType.VARIABLE	
							);
					tripleVt.addElement(t1);
					
				} else
				{
					// ensure upper case for first letter
					String str_type_tmp = str_type.substring(0, 1).toUpperCase() + str_type.substring(1);
					
					SparqlTriple t1 = new SparqlTriple(
							str_id, 
							SPARQL_RDF_TYPE,					
							SPARQL_NS_MSEE + ":" +  str_type_tmp,
							SparqlTriple.SparqlType.VARIABLE,
							SparqlTriple.SparqlType.RDF_PROPERTY,
							SparqlTriple.SparqlType.MSEE_CLASS		
							);
					tripleVt.addElement(t1);
				}
			} else
			{
				SparqlTriple t1 = new SparqlTriple(
						str_id, 
						SPARQL_RDF_TYPE,					
						SPARQL_NS_MSEE + ":" +  SPARQL_MSEE_EVENT,
						SparqlTriple.SparqlType.VARIABLE,
						SparqlTriple.SparqlType.RDF_PROPERTY,
						SparqlTriple.SparqlType.MSEE_CLASS		
						);
				
				Boolean isVarType = IsVariable(str_type);
				
				SparqlTriple t2 = new SparqlTriple(
						str_id, 					
						SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASTYPE,					
						str_type,
						SparqlTriple.SparqlType.VARIABLE,
						SparqlTriple.SparqlType.MSEE_PROPERTY,		
						isVarType ? SparqlTriple.SparqlType.VARIABLE : SparqlTriple.SparqlType.CONST_STRING
						);
				
				tripleVt.addElement(t1);
				tripleVt.addElement(t2);
			}
		}
		
		if (str_agent != null)
		{
			// KIV, ensure str_agent is a variable
			SparqlTriple t = new SparqlTriple(
					str_id,					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASAGENT, 
					str_agent,
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
			tripleVt.addElement(t);
		}
		
		

		if (str_patient != null)
		{
			// KIV, ensure str_agent is a variable
			SparqlTriple t = new SparqlTriple(
					str_id,					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_HASPATIENT, 
					str_patient,
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
			tripleVt.addElement(t);
		}
		
		
		if (str_purpose != null)
		{
			SparqlTriple t_purpose = new SparqlTriple(
					str_id,					
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_PURPOSE, 
					str_purpose,
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
			
			// tripleVt.addElement(t);
			SparqlTriple t_cause = new SparqlTriple(
					str_purpose,								// note, the first and third items are swapped
					SPARQL_NS_MSEE + ":" + SPARQL_MSEE_CAUSE, 					
					str_id,		
					SparqlTriple.SparqlType.VARIABLE,
					SparqlTriple.SparqlType.MSEE_PROPERTY,					
					SparqlTriple.SparqlType.VARIABLE
			);
			
				
			// use union tag
			SparqlUnion union = new SparqlUnion();
			SparqlGroup g1 = new SparqlGroup();
			SparqlGroup g2 = new SparqlGroup();
			g1.AddTriple(t_purpose);
			g2.AddTriple(t_cause);
			union.AddGroup(g1);
			union.AddGroup(g2);
			unionVt.addElement(union);			
		}
		
		InsertLocationIfExist( e_event, str_id );
		InsertTimeIfExist( e_event, str_id );
	}
			
	private void OutputSparql( 
			Document veml_xml_doc,
			String sparql_op_file
		) throws Exception 
	{
		try 
		 {
			 
	      // output
			FileOutputStream fos = new FileOutputStream(sparql_op_file); 
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			
			out.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n");
			out.write("PREFIX data: <http://msee/data.rdf#>\n");
			
			out.write("PREFIX msee: <http://msee/ontology/msee.owl#>\n");
			out.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
			
			out.write("PREFIX fn: <java:sparql.>\n");
				
			
			
			// output select
			out.write("\nSELECT ");
			for (int i = 0; i < this.selectVt.size(); i++)
			{
				if (true ==selectVt.elementAt(i).contains("?object"))
				{
						// if it is an object id, do not output in select
				} else
				{
					out.write( "?" + selectVt.elementAt(i) + " ");
				}
			}
			
    		// output where
			out.write("\nWHERE {\n");
			for (int i = 0; i < this.tripleVt.size(); i++)
			{
				String str = tripleVt.elementAt(i).GetTripleString();
				
				// replace short_object_id with long_object_id, if needed
				if (m_object_long_id_toreplacewith.length() >0  )
				{
					int index_s = str.indexOf("?object");
					
					 if (index_s!=-1)
				       {
				        	int index_e = str.indexOf(" ",index_s);
				        	if (index_e == -1)
				        	{	index_e = str.indexOf(".",index_s);
				        	}
				        	if (index_e == -1)
				        	{	index_e = str.indexOf("\t",index_s);
				        	}
				        	if (index_e != -1)
				        	{
				        		str = str.substring(0,index_s) + " data:" + m_object_long_id_toreplacewith +  str.substring(index_e);				        			        			
				        	}
				        }
				}
				
				out.write( "\t" + str + "\n");
			}	
			
			// output union
			
			/*
			 * 	
	{ 
	?when14001 msee:hasTimeAt ?time_at.
	}
	UNION 
	{
	?when14001 msee:hasTimeBegin ?time_begin.
	?when14001 msee:hasTimeEnd ?time_end.
		}
				 */
			
			
			for (int iunion = 0; iunion < this.unionVt.size(); iunion++)
			{
				
				SparqlUnion union = unionVt.elementAt(iunion);
				
				for (int igroup = 0; igroup < union.groupVt.size(); igroup++)
				{
					SparqlGroup group = union.groupVt.elementAt(igroup);
					out.write( "\t{\n");
					for (int itriple = 0; itriple < group.tripleVt.size(); itriple++)
					{
						String str = group.tripleVt.elementAt(itriple).GetTripleString();
						out.write( "\t" + str + "\n");
					}						
					out.write( "\t}\n");
					if (igroup < union.groupVt.size()-1)
					{
						out.write( "\tUNION\n");
					}
				}
			}		
			
			
			// output filter 
			if ((this.m_query_scenetime_start.length() >0) && (this.m_query_scenetime_end.length() >0))
			{
				// FILTER (fn:IsAtTime("VIEW_CENTRIC_TIME_PERIOD","1400;1600;view-HC3","OBJECT_ID",?person_2)) 
				for (int i = 0; i < this.selectVt.size(); i++)
				{
					if ((true ==selectVt.elementAt(i).contains("?object")) ||
						(true ==selectVt.elementAt(i).contains("_name")) ||
						(true ==selectVt.elementAt(i).contains("time")) 
						)
							
					{
							// if it is an object id, do not output in select
					} else
					{
					
						out.write( "\tFILTER (fn:IsAtTime(\"SCENE_CENTRIC_TIME_PERIOD\",\""+m_query_scenetime_start+";"+m_query_scenetime_end+"\",\"ENTITY_ID\",?"+  selectVt.elementAt(i) + ")) .\n");
						//out.write( "\tFILTER (fn:IsAtTime(\"SCENE_CENTRIC_TIME_PERIOD\",\""+m_query_scenetime_start+";"+m_query_scenetime_end+"\",\"EVENT_ID\",?"+  selectVt.elementAt(i) + ")) .\n");
						
					}
				}
				
				
			}
			
			out.write("\n}");
			
			out.close();  

	    } catch(Exception e) {
	        System.out.println(e.toString());
	        e.printStackTrace();
	    }	
	}
}
