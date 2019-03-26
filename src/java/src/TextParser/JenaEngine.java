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
import java.io.InputStream;
import java.io.OutputStreamWriter;

import sparql.AnswerConfidence;
import sparql.CardinalityRecordList;

import arq.query;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;

/* reference
 * 	http://incubator.apache.org/jena/tutorials/rdf_api.html 
 *  http://incubator.apache.org/jena/documentation/query/app_api.html
 *  http://jena.apache.org/documentation/query/writing_functions.html
 *  http://www.slideshare.net/LeeFeigenbaum/sparql-cheat-sheet
 */


// com.hp.hpl.jena.rdf.model;
public class JenaEngine {

	private boolean bDebug = false;
	
	/**
	 * Creates an instance of the engine, and executes a query, based on the provided files (saving the result in output_file).
	 * @param a_rdf_file
	 * @param a_owl_file
	 * @param a_sparql_file
	 * @param output_file
	 * @throws Exception
	 */
	public JenaEngine(
			String a_rdf_file, 
			String a_owl_file,
			String a_sparql_file,
			String output_file ) throws Exception
	{
		String rdf_file = a_rdf_file;
		String sparql_file = a_sparql_file;
		String owl_file = a_owl_file; 
		
		
		// clear static variable		
		CardinalityRecordList.Reset();
		AnswerConfidence.Reset();
	
//		if (false)
//		{	// debug only 
//			rdf_file = "E:\\projects\\MSEE\\demo\\Earley\\result_20120319_093729\\line_0\\textdata.rdf";
//			sparql_file = "E:\\projects\\MSEE\\demo\\Earley\\result_20120319_093729\\line_1\\query_sparql.txt";
//		}
			
		System.out.println("rdf_file: "+ rdf_file);
		System.out.println("owl_file: "+ owl_file);		
		System.out.println("sparql_file: "+ sparql_file);
		
		
		if (false)
		{	// using the query main run class;
			
			String argv[] = new String[2];
			
			//argv[0] = "Sparql";
			argv[0] = "--data=" + rdf_file;			
			argv[1] = "--query=" + sparql_file;			
			
			new query(argv).mainRun() ;
		} else
		{	
			ExecuteQuery(rdf_file, owl_file, sparql_file, output_file);			
		}
	}
	
	/**
	 * Creates an instance of the engine, without executing a query.
	 */
	public JenaEngine( ) {
		
	}
	
	private String ReadQueryString(String sparql_inputFileName) throws Exception
	{
		String str = "";
		FileReader fr = new FileReader(sparql_inputFileName);
		BufferedReader br1 = new BufferedReader(fr);		
	
		String strtmp = null;
			 
		while((strtmp = br1.readLine())!=null)   	
		{
			str = str  + strtmp + '\n';				
		}
		
		fr.close();
		
		return str;
	}
	
	/* reference
	 * 	http://incubator.apache.org/jena/tutorials/rdf_api.html 
	 *  http://incubator.apache.org/jena/documentation/query/app_api.html
	 *  http://jena.apache.org/documentation/query/writing_functions.html
	 *  http://www.slideshare.net/LeeFeigenbaum/sparql-cheat-sheet
	 */
	/**
	 * Executes a saved query, saving the result in output_file.
	 * @param rdf_inputFileName
	 * @param owl_file
	 * @param sparql_inputFileName
	 * @param output_file
	 * @throws Exception
	 */
	public void ExecuteQuery(
			String rdf_inputFileName, 
			String owl_file, 				// input file for the msee ontology
			String sparql_inputFileName,
			String output_file
			)throws Exception
	{
		
		System.out.println("JenaEngine sparql_inputFileName: " + sparql_inputFileName);
		System.out.println("JenaEngine output_file: " + output_file);
		
		// read query		
		String queryString = ReadQueryString(sparql_inputFileName);
		
		String result = ExecuteQuery(rdf_inputFileName, owl_file, queryString);
		
		// \todo result could be null
		
    	FileOutputStream fos = new FileOutputStream(output_file); 
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");	
		out.write(result);
		out.close();
		

		// output same result to another file because the first output file (output_file) will be overwritten
		FileOutputStream fos2 = new FileOutputStream(output_file+".orig.txt"); 
		OutputStreamWriter out2 = new OutputStreamWriter(fos2, "UTF-8");
		out2.write("result \n");
		out2.write(result);
		out2.close();
		
	}
	
	/**
	 * Executes an in-memory query, returning the result (as a string).
	 * @param rdf_inputFileName
	 * @param owl_file
	 * @param queryString
	 * @throws Exception
	 */
	public String ExecuteQuery(
			String rdf_inputFileName, 
			String owl_file, 				// input file for the msee ontology
			String queryString
			)throws Exception
	{
		if (bDebug)
		{
			System.out.println("ExecuteQuery  ModelFactory.createDefaultModel" );
		}
			
		 // create an empty model
		Model model = ModelFactory.createDefaultModel();
		Model model2 = null;
		if (owl_file!=null)
		{
			model2 = ModelFactory.createDefaultModel();
		}	
			
		 if (bDebug)
		{
			System.out.println("ExecuteQuery  ModelFactory.createDefaultModel" );
		}
			
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open( rdf_inputFileName );
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + rdf_inputFileName + " not found");
		}
		InputStream in2 = null;
		if (owl_file!= null)
		{
			in2 = FileManager.get().open(owl_file);
			if (in2 == null) 
			{	throw new IllegalArgumentException( "File: " + owl_file + " not found");
			}
		}
		

		// read the RDF/XML file
  		try
		{
			model.read(in, null);
			in.close();
			
			if (owl_file!= null)
			{
				model2.read( in2, "" );
				in2.close();
			}
			
		} catch (Exception e)
		{ 	System.err.println ("Error reading file");
		}

		// set up datasource
		//Dataset dataSource = DatasetFactory.create() ;

//		Dataset dataSource = DatasetFactory.create() ;
//		
//		dataSource.setDefaultModel(model) ;
//		if (owl_file!= null)
//		{
//			System.out.println("Jena Engine: adding msee.owl source" );
//			dataSource.addNamedModel("http://msee/ontology/msee.owl", model2) ;
//		}
//		
		
		// Model model;
		if (owl_file!= null)
		{	// model = model1.union(model2);
			model.add(model2);
		}
		
		// execute
				
		Query query = QueryFactory.create(queryString) ;
		
		// use this when there is only one rdf as data source
		 QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		
		// use this when there is multiple files (rdf + owl) as datasource
		//QueryExecution qexec = QueryExecutionFactory.create(query, dataSource);

		  
		 StringBuffer result = new StringBuffer();
		  
		  try 
		  { 	
		  
		    ResultSet results = qexec.execSelect() ;
		  
		    /*
		     * 
		     * ResultSetFormatter fmt = new ResultSetFormatter(results, query) ;
    fmt.printAll(System.out) ;

		     */
		    
		  //  
		    
		    if (false)
		    {
		    	// use standard result formatter
		    	ResultSetFormatter.out(results) ;
		    } else if (true) 
		    {
		    	
		        ResultSetRewindable resultSetRewindable = new ResultSetMem(results) ;
		        int numCols = resultSetRewindable.getResultVars().size() ;
		        
		        result.append("Number of answers: "+ resultSetRewindable.size() + "\n"); 
		        
		        int ctrRow= 0; 
		        for ( ; resultSetRewindable.hasNext() ; )
		        {
		        	System.out.println("Answer: #" + (ctrRow+1) + "\t\tnumCols " + numCols);
		        	
		        	/*
		        	if (false)
		        	{
		        		result.append("#" + (ctrRow+1)  + ":");
		        	}
		        	*/ 
		        	
		            QuerySolution rBind = resultSetRewindable.nextSolution() ;
		           
		            for ( int col = 0 ; col < numCols ; col++ )
		            {
		            	            	
		                String rVar = results.getResultVars().get(col) ;
		                String str_solution = "";
		                
		                System.out.println("\t col:" + col + "\t Value: " + rVar);
		                
		                RDFNode n = rBind.get(rVar) ;
		                if (n!=null)
		                {
			                if ( n.isLiteral() )
			                {
			                    str_solution = ((Literal)n).getLexicalForm() ;
			                }
			                
			                if ( n.isResource() )
			                {
			                   Resource r = (Resource)n ;
			                    if ( ! r.isAnon() )
			                    {
			                    	str_solution =  r.getURI() ;
			                    }
			                }
			                
			                System.out.println(rVar + ": " + str_solution );
			                
			                int index = str_solution.lastIndexOf("#");
			                if (index !=-1)
			                {
			                	str_solution = str_solution.substring(index);
			                }
			                	
			                // out.write(str_solution  + "\n");		
			                result.append(str_solution);
			                if (col < numCols-1)
			                {
			                	result.append(",");
			                }
			                
		                } 	                

		                //row[col] = this.getVarValueAsString(rBind, rVar );
		            }
		            result.append("\n");
		            ctrRow++;
		            //printRow(pw, row, colWidths, colStart, colSep, colEnd) ;
		        }		
				

		    }

	    
		    /* keep for reference
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      
		      RDFNode x = soln.get("varName") ;       // Get a result variable by name.
		      Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
		      Literal l = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
		    }
		    
		    */ 
		  } finally { qexec.close() ; }
		  
		  return result.toString();
	}
}
