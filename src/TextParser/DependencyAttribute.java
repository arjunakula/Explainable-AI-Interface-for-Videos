package TextParser;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

public class DependencyAttribute {
	String name;
	String value;
	
	DependencyAttribute(String arg_name, String arg_value) 
	{
		name = arg_name;
		value = arg_value;	
	}	
	
	DependencyAttribute(DependencyAttribute arg_attr) 
	{
		name = arg_attr.name;
		value = arg_attr.value;	
	}	
	
	public boolean Equals(DependencyAttribute arg_attr) 
	{
		return (name.equals(arg_attr.name) && value.equals(arg_attr.value));	
	}	
}
