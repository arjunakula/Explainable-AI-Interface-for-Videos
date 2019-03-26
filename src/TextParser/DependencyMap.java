/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 
package TextParser;

import java.util.HashMap;

public class DependencyMap {

	/**
	 *  <code>pOSTags</code> is a hash in which each part of speech tag that may be found in the dependencies 
	 * mapping file points to all of the corresponding tags in the Penn Treebank set of part-of-speech tags.
	 */
	private static final HashMap<String,String> POS_TAGS = HashMapBuilder.build(
			"VERB", "VB VBD VBG VBN VBP VBZ MD",			//Verb
			"NOUN", "NN NNS NNP NNPS PRP PRP$ WP WP$",		//Noun
			"ADJ", "JJ JJR JJS",							//Adjective
			"ADV", "RB RBR RBS WHB",						//Adverb
			"PREP", "IN",									//Preposition
			"CONJ", "CC",									//Conjunction
			"DET", "DT PDT WDT",							//Determiner
			"PART", "RP");
	
	public String mapToName;
	public String mapAttributeRule[] = null;
	public String mapFromName[] = null;
	public String mapFromPos[] = null;
	
	DependencyMap(String argtext) throws Exception
	{		
		String s1[] = argtext.split(" ");
		
		if (s1.length <= 1)
		{
			throw new Exception("Error parsing dependency map" + argtext);
		}
		
		mapToName = s1[0];
		
		int ctrRule = 0; 
		int ctrFrom = 0;
		
		for (int i = 1; i < s1.length; i++)
		{
			if (s1[i].length() > 0 )
			{
				if (s1[i].contains("="))
				{
					ctrRule++;				
				}
				else
				{	ctrFrom++;
				}
			}			
		}
		
		if (ctrRule > 0)
		{
			mapAttributeRule = new String[ctrRule];
		}
		if (ctrFrom > 0)
		{
			mapFromName = new String[ctrFrom*8];
			mapFromPos  = new String[ctrFrom*8];
		}
		int iRule = 0; 
		int iFrom = 0;
		
		for (int i = 1; i < s1.length; i++)
		{
			if (s1[i].length() > 0 )
			{
				if (s1[i].contains("="))
				{
					mapAttributeRule[iRule] = s1[i];
					iRule++;				
				}
				else
				{	
					if (s1[i].contains("/"))
					{
						String str[] = s1[i].split("/");
						if (POS_TAGS.containsKey(str[1]) ) {
							for (String tag : POS_TAGS.get(str[1]).split(" ")) {
								mapFromName[iFrom] = str[0];
								mapFromPos[iFrom] = tag;
								iFrom++;	
							}
						} else {
							mapFromName[iFrom] = str[0];
							mapFromPos[iFrom] = str[1];
							iFrom++;	
						}
					} else
					{
						mapFromName[iFrom] = s1[i];
						mapFromPos[iFrom] = "";
						iFrom++;	
					}
					
				}
			}			
		}						
	}
	
	public boolean IsMatchWithPos(String str, String pos)
	{
		if (mapFromName!=null)
		{
			for (int i =0; i < mapFromName.length; i++)
			{
				if ((mapFromPos[i] != null) && (mapFromPos[i].length() > 0) && (mapFromPos[i].equals(pos)))
				{
					if (mapFromName[i].equalsIgnoreCase(str))
					{	// found
						return true;
					}	else
					{
						// maps all numerics to 9
						String str_tmp = str;
						str_tmp = str_tmp.replace('0', '9');
						str_tmp = str_tmp.replace('1', '9');
						str_tmp = str_tmp.replace('2', '9');
						str_tmp = str_tmp.replace('3', '9');
						str_tmp = str_tmp.replace('4', '9');
						str_tmp = str_tmp.replace('5', '9');
						str_tmp = str_tmp.replace('6', '9');
						str_tmp = str_tmp.replace('7', '9');
						str_tmp = str_tmp.replace('8', '9');
						
						if (mapFromName[i].equalsIgnoreCase(str_tmp))
						{	// found
							return true;
						}						
					}
				} 
			}
		}		
		return false;
	}
	
	public boolean IsMatchWithoutPos(String str, String pos)
	{
		if (mapFromName!=null)
		{
			for (int i =0; i < mapFromName.length; i++)
			{
				if (mapFromPos[i] != null) {
					if ((mapFromPos[i].length() == 0) || (mapFromPos[i].equals(pos)))
				{
					if (mapFromName[i].equalsIgnoreCase(str))
					{	// found
						return true;
					}	else
					{
						// maps all numerics to 9
						String str_tmp = str;
						str_tmp = str_tmp.replace('0', '9');
						str_tmp = str_tmp.replace('1', '9');
						str_tmp = str_tmp.replace('2', '9');
						str_tmp = str_tmp.replace('3', '9');
						str_tmp = str_tmp.replace('4', '9');
						str_tmp = str_tmp.replace('5', '9');
						str_tmp = str_tmp.replace('6', '9');
						str_tmp = str_tmp.replace('7', '9');
						str_tmp = str_tmp.replace('8', '9');
						
						if (mapFromName[i].equalsIgnoreCase(str_tmp))
						{	// found
							return true;
						}						
					}		
				} 
				}
			}
		}		
		return false;
	}
	
	void Print() 
	{	
		System.out.println("mapToName "  +mapToName);
		
		if (mapAttributeRule!=null)
		{
			for (int i =0; i < mapAttributeRule.length; i++)
			{
				System.out.println("rule: " + i + ":" + mapAttributeRule[i]);		
			}
		}
		
		if (mapFromName!=null)
		{
			for (int i =0; i < mapFromName.length; i++)
			{
				System.out.println("from: " + i + ":" + mapFromName[i] + " tag:" + mapFromPos[i]);		
			}
		}
		
		
	}
	
}
