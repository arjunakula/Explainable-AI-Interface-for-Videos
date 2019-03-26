package TextParser;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 


import java.util.Vector;

public class Dependency extends Exception {
	
	static final int DEPENDENCY_FIELD_LENGTH = 2;
	
	public String text ;		//sentence to be parsed
	public String relation;
	public String argid[] = new String[2] ;		// with the id, e.g. "entering-3"
	public String arg[] = new String[2];			// without the id , e.g. "entering"
	public String pos[] = new String[2];			// e.g. "JJ"
	public int id[] = new int[2];
	public String mappedArg[] = new String[2];	
		
	public Vector<DependencyAttribute> attributes = new Vector<DependencyAttribute>();
	
	public Vector< Vector<DependencyAttribute> > fieldAttributes = new Vector< Vector<DependencyAttribute> >();
	
	final private boolean bDebug = false;  
		
	Dependency(
			String argtext,
			Vector<String> tagwordVt ,
			Vector<String> tagPosVt ,
			int dataid
			) throws Exception
	{		
		
		System.out.println("argtext: " + argtext);
		
		// create two field attribute
		for (int ifield = 0; ifield < DEPENDENCY_FIELD_LENGTH; ifield++)
		{
			fieldAttributes.addElement(new  Vector<DependencyAttribute>() );
		}
			
		
		text = replace(argtext," ", "");
		
		String s1[] = text.replace('(',' ').split(" ");
		
		if (s1.length != 2)
		{
			throw new Exception("Error parsing dependency" + argtext);
		}
		
		relation = s1[0];		
		String s2 = s1[1];
		String s3 = replace(s2,")", "");
		String s4[] = s3.split(",");
		
		if (s4.length != 2)
		{
			throw new Exception("Error parsing dependency" + argtext);
		}
		
		//String tmp_argid[] = = new String[2] ;	
		
		//tmp_argid[0] = s4[0];
		//tmp_argid[1] = s4[1];
		
		String s5[] = s4[0].split("-");
		arg[0] = s5[0];		
		id[0] = Integer.parseInt(s5[1]) ;
		
		
		String s6[] = s4[1].split("-");
		arg[1] = s6[0];		
		id[1] = Integer.parseInt(s6[1]) ;
		
		
		argid[0] = new String(arg[0] + (id[0]+dataid));
		argid[1] = new String(arg[1] + (id[1]+dataid));
	
		
		System.out.println("argid0: " + argid[0] + " argid1: " + argid[1]);
		if (id[0] > 0 )
		{
			pos[0] = tagPosVt.elementAt(id[0] -1);
		} else
		{ 	pos[0] = "";		
		}
		if (id[1] > 0 )
		{
			pos[1] = tagPosVt.elementAt(id[1] -1);
		} else
		{ 	pos[1] = "";		
		}
		
		System.out.println("pos0: " + pos[0] + " pos1: " + pos[1]);
				
		mappedArg[0] = "";
		mappedArg[1] = "";
	}
	
	void AddAttribute(String attr, String value)
	{
		DependencyAttribute da = new DependencyAttribute(attr, value);
		attributes.addElement(da);		
	}	
	
	void AddFieldAttribute(int ifield, String attr, String value) throws Exception
	{
		if (bDebug)
		{
			System.out.println("DEBUG ifield " + ifield + " attr " +  attr + " value " + value);
		}
		
		if ((ifield >=0) && (ifield <DEPENDENCY_FIELD_LENGTH))
		{
			DependencyAttribute da = new DependencyAttribute(attr, value);
			fieldAttributes.elementAt(ifield).addElement(da);
		} else
		{
			throw new Exception("ERROR, AddFieldAttribute: invalid ifield:" + ifield);
		}
	}	
	
	
	void SetMappedArg(int iarg, String str)
	{
		if (mappedArg[iarg].equals(""))
		{	// set mapped value
			mappedArg[iarg] = str;
		} else if (mappedArg[iarg].equals(str))
		{	// okay, already mapped 			
		} else
		{
			System.out.println("WARNING dependency " + arg[iarg] + " maps to two values " + mappedArg[iarg] + " and " + str);
			System.out.println("\t IGNORE " + str);			
		}		
	}
	
	String GetMappedDependency()
	{
		String str = relation + "(" + mappedArg[0] + "," + mappedArg[1] + ")";		
		return str;
	}
	
	
	static String replace(String str, String pattern, String replace) {
	    int s = 0;
	    int e = 0;
	    StringBuffer result = new StringBuffer();

	    while ((e = str.indexOf(pattern, s)) >= 0) {
	        result.append(str.substring(s, e));
	        result.append(replace);
	        s = e+pattern.length();
	    }
	    result.append(str.substring(s));
	    return result.toString();
	}
	
	

	
}
