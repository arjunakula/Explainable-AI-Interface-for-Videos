/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 
package TextParser;
public class SparqlTriple {


	
	public static enum SparqlType { 
		VARIABLE, 
		MSEE_PROPERTY, 
		MSEE_CLASS, 
		RDF_PROPERTY, 
		CONST_STRING, 
		UNKNOWN };
	
	public String[] text = new String[3];
	public SparqlType[] type = new SparqlType[3];
	
	public boolean bOptional = false;
	
	SparqlTriple() 
	{
		for (int i = 0; i < 3; i++)
		{
			text[i] = new String("");
			type[i] = SparqlType.UNKNOWN;			
		}	
	}	
	
	SparqlTriple( 	String s0,					
					String s1,										
					String s2,
					SparqlType t0, 
					SparqlType t1,
					SparqlType t2
			) 
	{
		text[0] = s0; 		type[0] = t0;
		text[1] = s1; 		type[1] = t1;
		text[2] = s2; 		type[2] = t2;	
	}	


	public static String GetEntityString(String text, SparqlType type) throws Exception
	{
		if (type == SparqlType.VARIABLE)
		{
			if (text.charAt(0) != '?')
			{	return "?" + text;			
			} else
			{
				return text;
			}
		} else if ((type == SparqlType.MSEE_PROPERTY) ||
				   (type == SparqlType.MSEE_CLASS) ||
				   (type == SparqlType.RDF_PROPERTY) 				   
					)
		{
			return text;
		} else if (type == SparqlType.CONST_STRING)
		{
			return "\"" + text + "\"";
		} else 
		{
			throw new Exception("ERROR GetEntityString: unknown type found " + text );
		}		
	}
	
	public String GetTripleString() throws Exception
	{
		String str = 	GetEntityString(text[0], type[0]) + " " + 
						GetEntityString(text[1], type[1]) + " " + 
						GetEntityString(text[2], type[2]);
		
		if (bOptional)
		{
			str = " OPTIONAL { " + str + " }" ;							
		} else
		{
			str = str + " .";
		}
		return str; 
	}	
}
