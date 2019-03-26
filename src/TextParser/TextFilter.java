/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

package TextParser;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;


public class TextFilter {

	private String strTime = null; 
	private String strTimeBegin = null; 
	private String strTimeEnd = null; 
		
	
	TextFilter(
			String text_ip_file,
			String text_filter_op_file			
		) throws Exception 
	{
		FileReader fr = new FileReader(text_ip_file);
		BufferedReader br1 = new BufferedReader(fr);		
		
		FileOutputStream fos = new FileOutputStream(text_filter_op_file); 
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		
		System.out.println("text_filter_op_file " + text_filter_op_file);
		
		
		String str = null;
		
		while((str = br1.readLine())!=null)   	
		{
			str = ExtractTimeIfAvail(str);
			str = str.toLowerCase();
				
			// kiv. move to model folder
			str = str.replace("african american", "african_american");
			str = str.replace("african-american", "african_american");
			str = str.replace("break room", "break_room");
			str = str.replace("bread plate", "bread_plate");
			str = str.replace("cake fork", "cake_fork");			
			str = str.replace("conference room", "conference_room");
			str = str.replace("cm", "centimeter");
			str = str.replace("delivery van", "delivery_van");
			str = str.replace("dessert spoon", "dessert_spoon");
			str = str.replace("dinner fork", "dinner_fork");
			str = str.replace("dinner set", "dinner_set");
			str = str.replace("dinner knife", "dinner_knife");
			str = str.replace("elevator button", "elevator_button");
			str = str.replace("elevator door", " elevator_door");
			str = str.replace("elevator foor", " elevator_door");	
			str = str.replace("food truck", "food_truck");
			str = str.replace("foor", "door");
			str = str.replace("hand bag", "handbag");
			str = str.replace("he's", "he is");
			str = str.replace("native-american", "native_american");
			str = str.replace("native american", "native_american");
			str = str.replace("package-0", "package_0");
			str = str.replace("package-1", "package_1");
			str = str.replace("package-7", "package_7");
			str = str.replace("parent task", "parent");
			str = str.replace("parent-task", "parent");
			str = str.replace("pick up", "pick_up");
			str = str.replace("pickup", "pickup");
			str = str.replace("pickup truck", "pickup_truck");			
			str = str.replace("police officer", "policeman");
			str = str.replace("phone call", "phone_call");	
			str = str.replace("person-0", "person_0");		
			str = str.replace("person-1", "person_1");		
			str = str.replace("person-2", "person_2");		
			str = str.replace("person-3", "person_3");		
			str = str.replace("person-4", "person_4");		
			str = str.replace("person-5", "person_5");	
			str = str.replace("person-6", "person_6");	
			str = str.replace("person-7", "person_7");	
			str = str.replace("person-8", "person_8");	
			str = str.replace("person-9", "person_9");	
			str = str.replace("Science Court", "science_court");	
			str = str.replace("salad fork", "salad_fork");
			str = str.replace("salad plate", "salad_plate");
			str = str.replace("service plate", "service_plate");
			str = str.replace("soup bowl", "soup_bowl");
			str = str.replace("soup spoon", "soup_spoon");
			str = str.replace("small_object", "small object");
			str = str.replace("table corner", "table_corner");
			str = str.replace("table leg", "table_leg");
			str = str.replace("table side", "table_side");
			str = str.replace("table top", "table_top");
			str = str.replace("togheter", "together");
			str = str.replace("trash bin", "trash_bin");	
			str = str.replace("trash can", "trash_can");
			str = str.replace("utensil tray", "utensil_tray");			
			str = str.replace("vehicle-0", "vehicle_0");
			str = str.replace("vehicle-1", "vehicle_1");
			str = str.replace("vehicle-2", "vehicle_2");
			str = str.replace("vehicle-3", "vehicle_3");
			str = str.replace("vehicle-4", "vehicle_4");
			str = str.replace("vehicle-5", "vehicle_5");
			str = str.replace("vehicle-6", "vehicle_6");
			str = str.replace("vehicle-7", "vehicle_7");
			str = str.replace("vehicle-8", "vehicle_8");
			str = str.replace("vehicle-9", "vehicle_9");
			str = str.replace("vender machine", "vending_machine");
			str = str.replace("vending machine", "vending_machine");
			str = str.replace("vision lab", "vision_lab");
			str = str.replace("walkes", "walks");
			str = str.replace("water fountain", "water_fountain");
			str = str.replace("-year-old", " year_old");
			str = str.replace("year-old", "year_old");
			str = str.replace("year old", "year_old");
			str = str.replace("years-old", "year_old");
			str = str.replace("years old", "year_old");
			str = str.replace("Young Hall", "young_hall");			
			str = str.replace("walking in group", "walking_group");
			str = str.replace("water machine", "water_machine");

			str = str.replace("water glass", "water_glass");
			str = str.replace("wine glass", "wine_glass");
			
			// Attributes
			str = str.replace("short hair", "short_hair");
			str = str.replace("long hair", "long_hair");
			str = str.replace("long sleeves", "long_sleeves");
			str = str.replace("long pants", "pants");
			str = str.replace("t-shirt", "shirt");
			str = str.replace("tshirt", "shirt");
			
			// FIXME: Modified by Arjun Akula : for the purpose of demo on March 2, 2017
			str = str.toLowerCase().replace("highlight all", "who is");
			str = str.replace("is anyone wearing a", "who has");
			str = str.replace("highlight", "who is");
			str = str.replace("show me all", "who is");
			str = str.replace("show me", "who is");
			str = str.replace("show all", "who is");
			str = str.replace("show", "who is");
			str = str.replace("is there any", "who is");
			str = str.replace("is anyone", "who is");
			str = str.replace("who all", "who");
			str = str.replace("in the video", "");
			
			// str = str.replace("walk into", "enter into");
			
			
			str = str.replace("they\'re", "they are");
			str = str.replace("they\\\'re", "they are");
			

			str = str.replace("-", "_");
					
			
			out.write(str);
							
		}
		out.close();
		fos.close();
	}
	
	private String ExtractTimeIfAvail(String str)
	{
		if (str.length()<5)
		{	return str;		
		}
		
		if (	(str.charAt(2) == ':') && 
				((str.charAt(0) >= '0') && (str.charAt(0) <= '9')) &&
				((str.charAt(1) >= '0') && (str.charAt(1) <= '9')) &&
				((str.charAt(3) >= '0') && (str.charAt(3) <= '9'))&&
				((str.charAt(4) >= '0') && (str.charAt(4) <= '9'))
		 )
		{
			strTime = str.substring(0,5);
			strTimeBegin = strTime;
			strTimeEnd = strTime;
			
			int i = 5;
			while ((str.length() >i) && ((str.charAt(i) == ' ')  || (str.charAt(i) == '-')))
			{
				i++;
			}
			
			String str_tmp = str.substring(i);
			
			if (	(str_tmp.charAt(2) == ':') && 
					((str_tmp.charAt(0) >= '0') && (str_tmp.charAt(0) <= '9')) &&
					((str_tmp.charAt(1) >= '0') && (str_tmp.charAt(1) <= '9')) &&
					((str_tmp.charAt(3) >= '0') && (str_tmp.charAt(3) <= '9'))&&
					((str_tmp.charAt(4) >= '0') && (str_tmp.charAt(4) <= '9'))
			 )
			{
		
				strTimeEnd = str_tmp.substring(0,5);
				
				int j = 5;
				while ((str_tmp.length() >j) && ((str_tmp.charAt(j) == ' ')  || (str_tmp.charAt(j) == '-')))
				{
					j++;
				}
				
				str_tmp = str_tmp.substring(j);
				
			}
			
			return str_tmp;
			
		} else if (	(str.charAt(1) == ':') && 
					((str.charAt(0) >= '0') && (str.charAt(0) <= '9')) &&					
					((str.charAt(2) >= '0') && (str.charAt(2) <= '9'))&&
					((str.charAt(3) >= '0') && (str.charAt(3) <= '9'))
			 )
			{
				strTime = str.substring(0,4);
				strTimeBegin = strTime;
				strTimeEnd = strTime;
				
				int i = 4;
				while ((str.length() >i) && ((str.charAt(i) == ' ')  || (str.charAt(i) == '-')))
				{
					i++;
				}
				
				String str_tmp = str.substring(i);
				
				if (	(str_tmp.charAt(1) == ':') && 
						((str_tmp.charAt(0) >= '0') && (str_tmp.charAt(0) <= '9')) &&						
						((str_tmp.charAt(2) >= '0') && (str_tmp.charAt(2) <= '9'))&&
						((str_tmp.charAt(3) >= '0') && (str_tmp.charAt(3) <= '9'))
				 )
				{
			
					strTimeEnd = str_tmp.substring(0,5);
					
					int j = 4;
					while ((str_tmp.length() >j) && ((str_tmp.charAt(j) == ' ')  || (str_tmp.charAt(j) == '-')))
					{
						j++;
					}
					
					str_tmp = str_tmp.substring(j);
					
				}
				
				return str_tmp;
				
		} else
		{
			return str; 
		}
		
	}
	
	public String GetTimeBegin()  
	{
		return strTimeBegin;
		
	}
	
	public String GetTimeEnd()
	{
		return strTimeEnd;		
	}
	
	public String GetTimeBeginInSecond()  
	{
		if (strTimeBegin == null)
			return null;
		
		if ((strTimeBegin.length()==5) && (strTimeBegin.charAt(2)==':'))
		{	
			String tmp1 = strTimeBegin.substring(0,2);
			String tmp2 = strTimeBegin.substring(3,5);
			
			int sec = Integer.parseInt(tmp1) * 60 + Integer.parseInt(tmp2);
			String newstr = new String("" + sec);
			return newstr;			
		}
		else
		{ return strTimeBegin;		
		}
		
	}
	
	public String GetTimeEndInSecond()
	{
	
		if (strTimeEnd == null)
			return null;
		
		if ((strTimeEnd.length()==5) && (strTimeEnd.charAt(2)==':'))
		{	
			String tmp1 = strTimeEnd.substring(0,2);
			String tmp2 = strTimeEnd.substring(3,5);
			
			int sec = Integer.parseInt(tmp1) * 60 + Integer.parseInt(tmp2);
			String newstr = new String("" + sec);
			return newstr;			
		}
		else
		{ return strTimeEnd;		
		}
		
	}
	
}

