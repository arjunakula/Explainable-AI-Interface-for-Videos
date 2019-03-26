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
import java.io.InputStreamReader;


public class RdfGenerator {

	final String bin = "bin\\TextVemlRdfConverterApp.exe";
	
	RdfGenerator(
			String veml_xml,
			String rdf_file,
			int dataid
		) throws Exception 
	{
		 try {
             Runtime rt = Runtime.getRuntime();
             //Process pr = rt.exec("cmd /c dir");
             String exec_str = new String(bin + " \"" + veml_xml + "\" \"" + rdf_file + "\" " + dataid);
             
             System.out.println("DEBUG exec_str : " + exec_str); 
             
             Process pr = rt.exec(exec_str);

             BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

             String line=null;

             while((line=input.readLine()) != null) {
                 System.out.println(line);
             }

             int exitVal = pr.waitFor();
             System.out.println("Exited with error code "+exitVal);

         } catch(Exception e) {
             System.out.println(e.toString());
             e.printStackTrace();
         }

		
	}
		
}
