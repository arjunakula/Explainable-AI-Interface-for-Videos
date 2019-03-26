

/**
 * Test Jena and sparql code
 *
 * Ref: http://jena.sourceforge.net/ARQ/app_api.html
 */

// package jena.examples.rdf ;
package TextParser;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.graph.*;
// import com.hp.hpl.jena.graph.query.*;
// import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.sparql.function.FunctionBase2;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp; 
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
// import com.hp.hpl.jena.sparql.function;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;

import java.io.*;


class SparqlQuery_FromFile 
{
	private static final int DEBUG_DATA = 0; 

    public static void main(String[] args) 
	{
		// default input rdf file 
		String inputRdfFilename = "C:\\Inetpub\\wwwroot\\Ontology\\BackLot_320x240_5min.avi.semantics.rdf";

		// text file containing the input sparql query 
		String inputQueryTextFilename = "C:\\Projects\\SceneUnderstanding\\Source\\Development\\Java_code\\Query_Text.txt";

		// output result file 
		String outputResultFileName = "C:\\Projects\\SceneUnderstanding\\Source\\Development\\Java_code\\result_sparqlQuery.txt";
		
		// testing 
		
		if (DEBUG_DATA>0)
		{	// print argument 
			for (String s: args) {
				System.out.println("arg: " + s);
			}		

			if (args.length > 0)
			{	for (int iarg = 0; iarg < args.length; iarg++)
				{
					System.out.println("arg " + iarg + ": " + args[iarg]);
				}			
			}
		}

		if (args.length !=3 )
		{
			System.out.println("ERROR, need three arguments: input_rdf input_query output_result");
			throw new IllegalArgumentException( "ERROR, need three arguments: input_rdf input_query output_result");
		}

		inputRdfFilename = args[0];
		inputQueryTextFilename = args[1];
		outputResultFileName = args[2];


    /* test query with DatasetFactory

	see: http://jena.sourceforge.net/ARQ/app_api.html
	***/

		// String inputFileName = "C:\\Inetpub\\wwwroot\\Ontology\\TownCenter1.avs.semantics.owl";
		// String inputFileName =  "C:\\Inetpub\\wwwroot\\Ontology\\TownCenter1.avs.semantics.owl";
		String inputFileName =  inputRdfFilename; 
		String inputFileName2 = "C:\\Inetpub\\wwwroot\\Ontology\\vsMarkup.owl";
		String inputFileName3 = "C:\\Inetpub\\wwwroot\\Ontology\\vsObject.owl";
		String inputFileName4 = "C:\\Inetpub\\wwwroot\\Ontology\\geofeature_at_RestonTownCenter.rdf";

        // create an empty model
        Model model = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		Model model3 = ModelFactory.createDefaultModel();
		Model model4 = ModelFactory.createDefaultModel();

        // use the class loader to find the input file

        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) 
		{	throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
		InputStream in2 = FileManager.get().open(inputFileName2);
		if (in2 == null) 
		{	throw new IllegalArgumentException( "File: " + inputFileName2 + " not found");
		}
		InputStream in3 = FileManager.get().open(inputFileName3);
		if (in3 == null) 
		{	throw new IllegalArgumentException( "File: " + inputFileName3 + " not found");
		}
		InputStream in4 = FileManager.get().open(inputFileName4);
		if (in4 == null) 
		{	throw new IllegalArgumentException( "File: " + inputFileName4 + " not found");
		}

		// read the RDF/XML files
  		try
		{
			model.read( in, "" );
			model2.read( in2, "" );
			model3.read( in3, "" );
			model4.read( in4, "" );
			in.close();
			in2.close();
			in3.close();
			in4.close();
		} catch (Exception e)
		{ 	System.err.println ("Error reading file");
		}

		// set up datasource
		DataSource dataSource = DatasetFactory.create() ;
		dataSource.setDefaultModel(model) ;
		dataSource.addNamedModel("http://leem2043/ontology/vsMarkup.owl", model2) ;
		dataSource.addNamedModel("http://leem2043/ontology/vsObject.owl", model3) ;
		dataSource.addNamedModel("http://leem2043/ontology/geofeature_at_RestonTownCenter.rdf", model4) ;

		// read query 
		String queryString = "";
		try
		{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(inputQueryTextFilename);
			// Get the object of DataInputStream
			DataInputStream inQuery = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(inQuery));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				// Print the content on the console
				if (DEBUG_DATA>0)
				{
					System.out.println (strLine);
				}
				queryString += "\n";
				queryString += strLine;
			}
			//Close the input stream
			inQuery.close();
		}
		catch (Exception e)
		{	//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("query: " + queryString);

		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
 		QueryExecution qe = QueryExecutionFactory.create(query, dataSource);
		ResultSet results = qe.execSelect();

		// Output query results
		// ResultSetFormatter.out(System.out, results, query);

		// output to file
		FileOutputStream out1;
		PrintStream p;
		try
		{
			out1 = new FileOutputStream(outputResultFileName);
			p = new PrintStream( out1 );

			ResultSetFormatter.out(p, results, query);

			p.close();
		} 
		catch (Exception e)
		{
			System.err.println ("Error writing to file");
		}

		// Important - free up resources used running the query
		qe.close();

		System.out.println();
/**/
    }
}

