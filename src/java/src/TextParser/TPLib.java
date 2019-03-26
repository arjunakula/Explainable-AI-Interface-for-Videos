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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;


public class TPLib {

	

    static public String GetFileContents(String filename) {
        //...checks on aFile are elided
    	File aFile = new File(filename);

        StringBuilder contents = new StringBuilder();
        
        try {
          //use buffering, reading one line at a time
          //FileReader always assumes default encoding is OK!
          BufferedReader input =  new BufferedReader(new FileReader(aFile));
          try {
            String line = null; //not declared within while loop
            /*
            * readLine is a bit quirky :
            * it returns the content of a line MINUS the newline.
            * it returns null only for the END of the stream.
            * it returns an empty String if two newlines appear in a row.
            */
            while (( line = input.readLine()) != null){
              contents.append(line);
              contents.append(System.getProperty("line.separator"));
            }
          }
          finally {
            input.close();
          }
        }
        catch (IOException ex){
          // ex.printStackTrace();
        
          System.out.println("File not found:" + filename);
          return "na";
        }
        
        return contents.toString();
      }
    
    static public void OutputStringToFile(String op_file, String str) throws Exception
	{ try {
			FileOutputStream fos = new FileOutputStream(op_file); 
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			out.write(str);
			out.close();	
		} catch (Exception e) {			
		}	
	}

	static public void AppendStringToFile(String op_file, String str) throws Exception
	{ try {
			FileWriter fstream = new FileWriter(op_file, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(str);
			out.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	static public void OutputExceptionTraceStack(String op_file, Exception arg_e) throws Exception
	{ try {
			FileWriter fstream = new FileWriter(op_file, true);
			BufferedWriter out = new BufferedWriter(fstream);			
			arg_e.printStackTrace(new PrintWriter(out, true));
			out.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}  
	
	
	 static public void copyfile(String srFile, String dtFile) throws IOException
	{
		 try{
			 File f1 = new File(srFile);
			  File f2 = new File(dtFile);
			  InputStream in = new FileInputStream(f1);
			  
			//For Overwrite the file.
			  OutputStream out = new FileOutputStream(f2);
			    byte[] buf = new byte[1024];
		   int len;		 
			 while ((len = in.read(buf)) > 0){		  
			  out.write(buf, 0, len);		 
			 }		 
			 in.close();
			  out.close();		 
			 System.out.println("File copied.");		  
		  }		 
		 catch(FileNotFoundException ex)
		 {		  
		   System.out.println(ex.getMessage() + " in the specified directory.");
		 }
	}	
	 
	 static	public void SaveXml(Document doc, String opfnxml) throws Exception 
		{
			// ///////////////
			// Output the XML

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);

			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter(opfnxml));
			writer.write(sw.toString());
			writer.close();
		}
    
}
