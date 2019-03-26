package TextParser;

import java.util.Vector;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

public class SparqlUnion {


	
	Vector<SparqlGroup> groupVt = new Vector<SparqlGroup>();
	
	
	SparqlUnion() 
	{

	}	
	

	public void AddGroup(SparqlGroup g) throws Exception
	{
		groupVt.addElement(g);
		
	}	
}
