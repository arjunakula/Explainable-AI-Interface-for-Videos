

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

import org.w3c.dom.Document;

import sparql.MseeFunction;
import sparql.SimpleBoundingBox;
import sparql.TimeFunctions;
import sparql.Parsedata.Homography;


public class CEventDataset {

	public Vector<CEventData> eventDataVt = new Vector<CEventData>();
	
	public void Printf()
	{	
		for (int i = 0; i <eventDataVt.size(); i++ )
		{
			CEventData eventdata = eventDataVt.elementAt(i);
			System.out.printf("event "+i +": " + eventdata.id + " begin " + eventdata.begin_frame + " end " + eventdata.end_frame + "\n");
		}		
	}
	
	public void  AddEventText(String str_id, String str_text)
	{
		CEventData data = FindEventData(str_id);
		if (data == null)
		{
			System.out.print("ERROR unable to find event " + str_id + " for text " + str_text +"\n");
		} else
		{
			data.AddText(str_text);
		}
	}
	
	public CEventData FindEventData(String str_id)
	{
		String str_tmp = str_id;
		
		// remove linefeed
		// System.out.printf("before str_tmp -" + str_tmp + "-\n");		
		if (str_tmp.charAt(str_tmp.length()-1 ) == '\n')
			str_tmp =str_tmp.substring(0, str_tmp.length()-1 );
		if (str_tmp.charAt(str_tmp.length()-1 ) == '\r')
			str_tmp =str_tmp.substring(0, str_tmp.length()-1 );		
		// System.out.printf("after str_tmp -" + str_tmp + "-\n");		
		
		
		for (int i = 0; i <eventDataVt.size(); i++ )
		{
			CEventData data = eventDataVt.elementAt(i);
			
			/*
			// if (data.id.equals(str_id))
			String str_tmp = str_id;
			if (str_tmp.length() > data.id.length())
			{
				// different length, the string id may have some line end char at the end.
				// so we make the length to be same before comparing
				str_tmp = str_tmp.substring(0,data.id.length());
			}			
			
			if (str_tmp.equals(data.id))
			{
				return data;
			}
			*/ 
			
			// if (str_tmp.equals(data.id))
			if (str_tmp.contentEquals(data.id))
			{
				return data;
			}
		
		
		}	
		return null;
	}

	

	public ArrayList<CEventData> FindEventDataList(String event_id) {
		
		String str_tmp = event_id;
		
		// remove linefeed
		// System.out.printf("before str_tmp -" + str_tmp + "-\n");		
		if (str_tmp.charAt(str_tmp.length()-1 ) == '\n')
			str_tmp =str_tmp.substring(0, str_tmp.length()-1 );
		if (str_tmp.charAt(str_tmp.length()-1 ) == '\r')
			str_tmp =str_tmp.substring(0, str_tmp.length()-1 );		
		// System.out.printf("after str_tmp -" + str_tmp + "-\n");		
		
		
		ArrayList<CEventData> list = new ArrayList<CEventData>(); 
		
		
		for (int i = 0; i <eventDataVt.size(); i++ )
		{
			CEventData data = eventDataVt.elementAt(i);
				
			// if (str_tmp.equals(data.id))
			if (str_tmp.contentEquals(data.id))
			{
				list.add(data);
			}
		
		
		}	
		return list;
	}	
	

	public void LoadEventTextXml(String fn_xml)
	{
		
    	try
    	{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
			InputStream in = new FileInputStream(fn_xml);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			
			CEventData eventdata = null; 
			
			String str_id = null;
			String str_text = null;
		
			while (eventReader.hasNext()) 
			{
				XMLEvent event = eventReader.nextEvent();
				
				if (event.isStartElement()) 
				{
					StartElement startElement = event.asStartElement();
					// If we have a item element we create a new item

    				/*
    				 * -<node> 
    				 * <id>60a8e35e-d4d5-4724-b791-82b13e9f9a70</id> 
    				 * <textdescription>A HUMAN walks on a FLOOR at the center of the image between 00:21 to 00:24.</textdescription> </node>
    				 * 
    				 */
					if (startElement.getName().getLocalPart() == ("node")) 
					{
						// new node
						
						str_id = null;
						str_text = null;
					}
					
					if (startElement.getName().getLocalPart() == ("id")) 
					{
						event = eventReader.nextEvent();
						str_id = event.asCharacters().getData();
					}
					
					if (startElement.getName().getLocalPart() == ("textdescription")) 
					{
						event = eventReader.nextEvent();
						str_text = event.asCharacters().getData();
						
						if (str_id!=null)
						{
							AddEventText(str_id, str_text);
						}						
					}						
				}					
			}
			
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} 
		
		Printf();
		
	}	
	
	
	public void LoadXml(String fn_xml, double frameRateScale, String observer_id, String view_id, String data_uri)
	{
		System.out.println("loading file " + fn_xml);
    	try
    	{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
			InputStream in = new FileInputStream(fn_xml);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			
			
			while (eventReader.hasNext()) 
			{
				XMLEvent event = eventReader.nextEvent();
				
				if (event.isStartElement()) 
				{
					StartElement startElement = event.asStartElement();
					// If we have a item element we create a new item

    				// If we have a item element we create a new item
					if (startElement.getName().getLocalPart() == ("action")) 
					{
					
						
						/*
						 * <action end_time="24" begin_time="21" end_frame="562" begin_frame="499" 
						 * id="bd93c94e-90e2-4ada-bbd5-2ab8a55b2604" name="[WALK ON FLOOR POSE C]" 
						 * position="291,282" object="fef1b536-043f-5d1a-a17b-fe265efaae6b" agent="b2332815-623d-548f-a44e-26e0478fbcd4"/>
						 * 
						 */
						
						String id="unknownid";						
						int begin_frame = 0; 
						int end_frame = 0; 
						String str_position ="";
						String str_textdescription="";
						
						Iterator iterator = startElement.getAttributes();
					    while (iterator.hasNext()) 
					    {
					          Attribute attribute = (Attribute) iterator.next();
					          QName name = attribute.getName();
					          String value = attribute.getValue();
					          
					          if (name.getLocalPart() == ("id"))
					          {
					        	  id = value;
					          } else if (name.getLocalPart() == ("begin_frame"))
					          {
					        	  begin_frame = Integer.parseInt(value);
					          }else if (name.getLocalPart() == ("end_frame"))
					          {
					        	  end_frame = Integer.parseInt(value);
					          }
					          else if (name.getLocalPart() == ("position"))
					          {
					        	  str_position = value;
					          }
					          else if (name.getLocalPart() == ("textdescription"))
					          {
					        	  str_textdescription = value;
					          }
					     }
					    
					    if (false) // if (MseeFunction.bVerbose)
					    {
					    	System.out.println("DEBUG CEventDataset LoadXml id "+ id + " begin_frame " + begin_frame + " end_frame " + end_frame + " str_position " + str_position);
					    }
					    
					    
						if (frameRateScale != 1.0)
						{
							begin_frame = (int) Math.round((double) begin_frame * frameRateScale);
							end_frame = (int) Math.round((double) end_frame * frameRateScale);
						}
					      
					     // old
					    //  eventDataVt.add(new CEventData(id, begin_frame, end_frame, str_position));	
					    CEventData event_data_found = this.FindEventData(id);
					    if (event_data_found != null)
					    {

					    	if (false) // if (MseeFunction.bVerbose)
						    {
						    	System.out.println("\t DEBUG CEventDataset LoadXml id "+ id + " add new action ");
						    }
						    event_data_found.AddActionData(begin_frame, end_frame, str_position);
						    event_data_found.AddText(str_textdescription);
					    } else
					    {
					    	if (false) // if (MseeFunction.bVerbose)
						    {
					    		System.out.println("\t DEBUG CEventDataset LoadXml id "+ id + " add new CEventData ");
						    }
					    	
					    	CEventData event_data_new = new CEventData(observer_id, view_id, data_uri, id, null, begin_frame, end_frame);
					    	event_data_new.AddActionData(begin_frame, end_frame, str_position);
					    	event_data_new.AddText(str_textdescription);
					    	eventDataVt.add(event_data_new);			    	
					    	
					    }
					    
						
					}
				}
					
			}
			
    	} catch (FileNotFoundException e) {
			System.err.println("WARN file not found:" + fn_xml );
    		// e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} 
		
		// Printf();
		
	}

	public void ComputeSceneCentricTime(Date sceneTime_Start, double frameRate) {
		
		// debug		
		System.out.println("DEBUG CEventDataset ComputeSceneCentricTime sceneTime_Start :" + sceneTime_Start.toString());
		System.out.println("DEBUG CEventDataset ComputeSceneCentricTime frameRate :" + frameRate);		
		 
		for (CEventData data : eventDataVt)
		{	data.ComputeSceneCentricTime(sceneTime_Start, frameRate);
		}	
	}

	public void ComputeSceneCentricLocation(Homography hView2Map, SimpleBoundingBox map_bbox) {
		// TODO Auto-generated method stub
		for (CEventData data : this.eventDataVt)
		{	data.ComputeSceneCentricLocation(hView2Map, map_bbox);
		}
		
	}

	public Date GetMinSceneTime() {
		// TODO Auto-generated method stub
		if (this.eventDataVt.isEmpty()) 
			return null;
		
		Date date = null;
		for (CEventData data : this.eventDataVt)
		{	date = TimeFunctions.MinDate(date, data.mSceneTime_Start);
		}
		return date;
	}		
	

	public Date GetMaxSceneTime() {
		// TODO Auto-generated method stub
		if (this.eventDataVt.isEmpty()) 
			return null;
		
		Date date = null;
		for (CEventData data : this.eventDataVt)
		{	date = TimeFunctions.MaxDate(date, data.mSceneTime_End);
		}
		return date;
	}

	public void SubsamplePos(int i) {
		// TODO Auto-generated method stub
		for (CEventData data : this.eventDataVt)
		{	data.SubsamplePos(i);
		}
		
		
	}

}



