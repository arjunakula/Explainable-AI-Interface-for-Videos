
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

public class SparqlGroup {

	Vector<SparqlTriple> tripleVt = new Vector<SparqlTriple>();
	
	SparqlGroup() 
	{
	}	

	public void AddTriple(SparqlTriple t) throws Exception
	{
		tripleVt.addElement(t);		
	}	
}
