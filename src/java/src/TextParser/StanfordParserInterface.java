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
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class StanfordParserInterface {
	
	public String ipText = null;;
	
	private boolean bDebug = false;
	
	StanfordParserInterface(			
			String stanford_parser_grammar_file,
			String text_ip_file,
			String stanford_op_file 
		) throws Exception 
	{
	
		if (bDebug) { System.out.println("running StanfordParserInterface ... "); }
		
	    String grammar = stanford_parser_grammar_file;
		    String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		    LexicalizedParser lp = 
		      new LexicalizedParser(grammar, options);
		    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

		    Iterable<List<? extends HasWord>> sentences;		    	    
		    
		    // read input text
		    ReadInputText(text_ip_file);
		    String sent2 = ipText;	    	
		    System.out.println(sent2);
		    	
		    Tokenizer<? extends HasWord> toke = 
		        tlp.getTokenizerFactory().getTokenizer(new StringReader(sent2));
		      List<? extends HasWord> sentence2 = toke.tokenize();
		      List<List<? extends HasWord>> tmp = 
		        new ArrayList<List<? extends HasWord>>();
		      // tmp.add(sentence);
		      tmp.add(sentence2);
		      sentences = tmp;

		    for (List<? extends HasWord> sentence : sentences) {
		      Tree parse = lp.apply(sentence);
		      parse.pennPrint();
		      System.out.println();
		      System.out.println(parse.taggedYield());
		      System.out.println();

		      GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		      Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed(true);
		      System.out.println(tdl);
		      System.out.println();
		      
		      // output tree to file	
		      try {
		    	String op_file_tree = stanford_op_file + ".tree.txt";		    	
				FileOutputStream fos = new FileOutputStream(op_file_tree); 
				OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");					
      		    parse.pennPrint(new PrintWriter(out, true));
      		    out.close();	    	  
		      } catch (Exception e) {
					e.printStackTrace();
			  }
		      
		      // output tag to file	
		      try {
		    	String op_file_tag = stanford_op_file + ".tag.txt";		    	
				FileOutputStream fos = new FileOutputStream(op_file_tag); 
				OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");		
				out.write(parse.taggedYield().toString());				
      		    out.close();	    	  
		      } catch (Exception e) {
					e.printStackTrace();
			  }
		      
		      // output dependency to file	
		      try {
					FileOutputStream fos = new FileOutputStream(stanford_op_file); 
					OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
					
					 Iterator<TypedDependency> itr = tdl.iterator();       
					 TypedDependency typeddepend = null;       
					 while (itr.hasNext()) 
					 {          
						 typeddepend = (TypedDependency) itr.next();          //rest of the code block removed       }
						 out.write(typeddepend.toString()  + "\n");						 
					 }					
					out.close();	
				} catch (Exception e) {
					e.printStackTrace();
				}
		    } 
				
	}	
	
	private void ReadInputText(String ipfile) throws Exception
	{
		FileReader fr = new FileReader(ipfile);
		BufferedReader br1 = new BufferedReader(fr);		
		
		ipText=br1.readLine();			
	}	
	
	
}
