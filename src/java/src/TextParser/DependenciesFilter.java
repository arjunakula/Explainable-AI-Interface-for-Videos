package TextParser;

/*
 * Copyright (c) 2012, Intelligent Automation Inc. 
 * All Rights Reserved.                                                       
 * Date:   3/30/2012
 * Author: Mun Wai Lee                                                           
 * E-Mail: mlee@i-a-i.com       
 *
 */ 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.MorphologicalProcessor;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import com.softcorporation.suggester.BasicSuggester;
import com.softcorporation.suggester.Suggestion;
import com.softcorporation.suggester.dictionary.BasicDictionary;
import com.softcorporation.suggester.util.BasicSuggesterConfiguration;
import com.softcorporation.suggester.util.SuggesterException;


/**
 * The <code>DependenciesFilter</code> .
 *
 * @author Ken Samuel
 * @version 1.0, Dec 30, 2014
 * @since 1.6
 */
/**
 * The <code>DependenciesFilter</code> .
 *
 * @author Ken Samuel
 * @version 1.0, Dec 30, 2014
 * @since 1.6
 */
public class DependenciesFilter {
	
	/**
	 *  <code>USE_SPELLING_CORRECTION</code> specifies whether the system, when it fails to find a word in the 
	 * lexicon, should try different spellings of the word.
	 */
	private static final Boolean USE_SPELLING_CORRECTION = true;
	
	/**
	 *  <code>USE_LEMMATIZER</code> specifies whether the system, when it fails to find a word in the lexicon, 
	 * should try looking for that word's lemmas (base forms). 
	 */
	private static final Boolean USE_LEMMATIZER = true;

	/**
	 *  <code>USE_SYNONYMS</code> specifies whether the system, when it fails to find a word in the lexicon, 
	 * should try looking for that word's lemmas' synonyms. (If <code>USE_LEMMATIZER</code> is 
	 * <b><code>false</code></b>, then synonyms are not checked, regardless of the value of 
	 * <b><code>USE_SYNONYMS</code></b>.
	 */
	private static final Boolean USE_SYNONYMS = true;
	
	/**
	 *  <code>USE_HYPERNYMS</code> specifies whether the system, when it fails to find a word in the lexicon, 
	 * should try looking for that word's lemmas' hypernyms. (If <code>USE_LEMMATIZER</code> is 
	 * <b><code>false</code></b>, then hypernyms are not checked, regardless of the value of 
	 * <b><code>USE_SYNONYMS</code></b>.
	 */
	private static final Boolean USE_HYPERNYMS = true;
	
	/** <code>TOKENIZER_FILENAME</code> is the filename of the model that the tokenizer will use. */
	private static final File TOKENIZER_FILENAME = new File(getRootPath() + "models/en-token.bin");

	/** 
	 * <code>DICTIONARY_FILENAME</code> is the filename of the dictionary that Suggester will use for spelling 
	 * correction. 
	 */
	private static String DICTIONARY_FILENAME = "file://" + getRootPath() + "dict/english.jar";			

	/** 
	 * <code>SPELLING_CONFIGURATION_FILENAME</code> is the name of the configuration file that Suggester will 
	 * use for spelling correction. 
	 */
	private static String SPELLING_CONFIGURATION_FILENAME = 
			"file://" + getRootPath() + "config/basicSuggester.config";		

	/** 
	 * <code>VERB_TAGS</code> holds all of the part-of-speech tags that correspond to verbs. 
	 */
	private final String[] VERB_TAGS = {
		"MD",
		"VB",
		"VBD",
		"VBG",
		"VBN",
		"VBP",
		"VBZ"
	};
			
	/** 
	 * <code>NOUN_TAGS</code> holds all of the part-of-speech tags that correspond to nouns. 
	 */
	private final String[] NOUN_TAGS = {
		"NN",
		"NNS",
		"NNP",
		"NNPS"
	};			
			
	/** 
	 * <code>ADJECTIVE_TAGS</code> holds all of the part-of-speech tags that correspond to adjectives. 
	 */
	private final String[] ADJECTIVE_TAGS = {
		"JJ",
		"JJR",
		"JJS"
	};			
			
	/** 
	 * <code>ADVERB_TAGS</code> holds all of the part-of-speech tags that correspond to adverbs. 
	 */
	private final String[] ADVERB_TAGS = {
		"RB",
		"RBR",
		"RBS"
	};			
			
	/** <code>verbTags</code> is the part-of-speech tags that correspond to types of verbs. */
	private HashSet<String> verbTags;

	/** <code>nounTags</code> is the part-of-speech tags that correspond to types of nouns. */
	private HashSet<String> nounTags;

	/** <code>verbTags</code> is the part-of-speech tags that correspond to types of adjectives. */
	private HashSet<String> adjectiveTags;

	/** <code>nounTags</code> is the part-of-speech tags that correspond to types of adverbs. */
	private HashSet<String> adverbTags;

	/** <code>wordNetDictionary</code> is a variable that is used to access WordNet. */
	Dictionary wordNetDictionary;

	/** <code>tokenizer</code> is the object to use to separate the sentence into tokens. */
	private TokenizerME tokenizer;

	/** <code>tokenizerModel</code> is the model that the tokenizer will use. */
	private TokenizerModel tokenizerModel;

	/** <code>dictionary</code> is the dictionary used by Suggester for spelling correction. */
	private BasicDictionary dictionary;

	/**
	 * <code>spellingConfiguration</code> is the configuration information used by Suggester for spelling 
	 * correction. 
	 */
	private BasicSuggesterConfiguration spellingConfiguration;

	/** <code>spellCorrect</code> is the object to use for spelling correction. */
	private BasicSuggester spellCorrect;

	public Dependency dependencies[]  = null ;
	public Vector<DependencyMap> dependenciesMaps= new Vector<DependencyMap>();
		
	Vector<String> tagwordVt = new Vector<String>();
	Vector<String> tagPosVt = new Vector<String>();
	
	final private boolean bDebug = false;

	/** 
	 * <code>doWriteNewline</code> specifies whether a '\n' should be added to the map file, which has 
	 * explanations of how unrecognized words were handled. 
	 */
	private Boolean doWriteNewline;
	
	
	//function for early parser
	DependenciesFilter(
			String dependenciesMappingFile, 
			String ipfile, 
			String posTagFile, 
			String opfile, 
			String unrecogword_op_file,
			String map_file,				//To output explanations of how unrecognized words were handled
			int dataid) throws Exception
	{
		FileInputStream wordNetIn;					//The file input stream to load the WordNet dictionary

		doWriteNewline = false;						//Initialize

		//Set up WordNet
		String rootPath = getRootPath();
		rootPath= rootPath.replace("%20"," ");
    	wordNetIn = new FileInputStream(rootPath+ "config/file_properties.xml");
    	JWNL.initialize(wordNetIn);
    	wordNetIn.close();
    	wordNetDictionary = Dictionary.getInstance();

		//Transform the array, VERB_TAGS into the set verbTags
		this.verbTags = new HashSet<String>(this.VERB_TAGS.length);	
		for (String verbTag : this.VERB_TAGS) {
			this.verbTags.add(verbTag);
		}

		//Transform the array, NOUN_TAGS into the set nounTags
		this.nounTags = new HashSet<String>(this.NOUN_TAGS.length);	
		for (String nounTag : this.NOUN_TAGS) {
			this.nounTags.add(nounTag);
		}
		
		//Transform the array, ADJECTIVE_TAGS into the set verbTags
		this.adjectiveTags = new HashSet<String>(this.ADJECTIVE_TAGS.length);	
		for (String adjectiveTag : this.ADJECTIVE_TAGS) {
			this.adjectiveTags.add(adjectiveTag);
		}

		//Transform the array, ADVERB_TAGS into the set adverbTags
		this.adverbTags = new HashSet<String>(this.ADVERB_TAGS.length);	
		for (String adverbTag : this.ADVERB_TAGS) {
			this.adverbTags.add(adverbTag);
		}

		//Set up the tokenizer
		tokenizerModel = new TokenizerModel(TOKENIZER_FILENAME);
		tokenizer = new TokenizerME(tokenizerModel);

		//Set up the spelling correction system
		dictionary = new BasicDictionary(DICTIONARY_FILENAME);
		spellingConfiguration = new BasicSuggesterConfiguration(SPELLING_CONFIGURATION_FILENAME);
		spellCorrect = new BasicSuggester(spellingConfiguration);
		spellCorrect.attach(dictionary);

		ReadDependenciesMapping(dependenciesMappingFile);
		ReadPosTagData(posTagFile);			// read in stanford output that has the POS, E.G. [three/CD, men/NNS, are/VBP, sitting/VBG, ./.]
		ReadDependenciesData(ipfile, dataid);	 // read in stanford output of the dependencies
		DoDependenciesMapping(unrecogword_op_file,map_file);		

		// output
		FileOutputStream fos = new FileOutputStream(opfile); 
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		out.write(GetMappedDependencies());
		out.close();					
	}
	
	void DoDependenciesMapping(
			String unrecogword_op_file, 
			String map_file)				//To output explanations of how unrecognized words were handled
					throws Exception
	{
		String dependency;								//One of the dependencies to find in dependency maps
		String dependencyPOS;							//The part of speech of that dependency
		FileOutputStream mapStream;						//For the map_file output file
		OutputStreamWriter mapWriter;					//For the map_file output file

		mapStream = new FileOutputStream(map_file,true); 
		mapWriter = new OutputStreamWriter(mapStream, "UTF-8");
		for (int idepend = 0; idepend < dependencies.length ; idepend++)
		{
			for (int iarg = 0; iarg < Dependency.DEPENDENCY_FIELD_LENGTH; iarg++)
			{
				dependency = dependencies[idepend].arg[iarg];
				dependencyPOS = dependencies[idepend].pos[iarg];
				
				if (dependencyPOS.equals("NNP")) {		//We assume all proper nouns are person name parts
					DoDependencyMapping("Adam",dependencyPOS,idepend,iarg,unrecogword_op_file,mapWriter);
				} else {
					DoDependencyMapping(dependency,dependencyPOS,idepend,iarg,unrecogword_op_file,mapWriter);
				}
			}
		}
		if (doWriteNewline) {
			mapWriter.write("\n");
		}
		mapWriter.close();
		// println for debug
		for (int idepend = 0; idepend < dependencies.length ; idepend++)
		{
			if (bDebug)
			{
				System.out.println( dependencies[idepend].relation + "(" + 
					dependencies[idepend].mappedArg[0] + "," +
					dependencies[idepend].mappedArg[1] + ")");
			}
			
			for (int iattr = 0; iattr < dependencies[idepend].attributes.size(); iattr++)
			{
				DependencyAttribute attr = dependencies[idepend].attributes.elementAt(iattr);
			
				if (bDebug)
				{
					System.out.println( "\t" + attr.name + "=" + attr.value);
				}
			}			
		}
	}

	/**
	 * The <code>DoDependencyMapping</code> method maps a single dependency.
	 *
	 * @param dependency is the dependency to be mapped.
	 * @param dependencyPOS is the part-of-speech tag of <code>dependency</code>.
	 * @param idepend specifies the list of dependencies that includes <code>dependency</code>.
	 * @param iarg specifies the dependency in the <code>idepend</code> list that is <code>dependency</code>.
	 * @param unrecogword_op_file is the name of the file in which to write the unrecognized words.
	 * @param mapWriter is the stream in which to write the output.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 * @throws IOException if there's a problem writing to mapWriter.
	 * @throws SuggesterException if there's a problem with the Suggested spelling correction system.
	 * @throws Exception if the parser is unsuccessful.
	 */
	private void DoDependencyMapping(
			String dependency, 
			String dependencyPOS,
			int idepend,
			int iarg,
			String unrecogword_op_file, 
			OutputStreamWriter mapWriter) 
					throws JWNLException, IOException, SuggesterException, Exception {
		DependencyMap mapMatched;						//The dependency map in which a dependency was found
		ArrayList<String> spellings;					//Ways to spell the dependency if it is misspelled
		Integer index;									//A counter to loop through an array

		mapMatched = extensiveSearch(dependency,dependencyPOS,mapWriter);//Look for map for dependency

		if (USE_SPELLING_CORRECTION) {
			if (mapMatched == null) {				//Try spelling correction
				spellings = correctSpelling(dependency);
				index = 0;
				while ((mapMatched == null) && (index < spellings.size())) {
					mapMatched = extensiveSearch(spellings.get(index),dependencyPOS,mapWriter);
					if (mapMatched != null) {
						mapWriter.write(
								"\"" + spellings.get(index) + "\" is the correct spelling of \"" + 
										dependency + "\".\n");
						doWriteNewline = true;
					}
					index++;
				}
			}
		}

		if (mapMatched != null)
		{
			DependencyMap map = mapMatched;
			dependencies[idepend].SetMappedArg(iarg, map.mapToName);
			if (map.mapAttributeRule != null)
			{
				for (int irule=0; irule < map.mapAttributeRule.length ; irule++ )
				{
					String tmp[] = map.mapAttributeRule[irule].split("=");
					if (tmp.length != 2)
					{
						throw new Exception("unable to parse rule " + map.mapAttributeRule[irule]);
					}

					String attrname = new String(tmp[0]);
					String attrvalue;

					if (tmp[1].equals("ARG"))
					{
						attrvalue = new String(dependencies[idepend].arg[iarg]);
					} else if (tmp[1].equals("ARGID"))
					{	String attrvalue_tmp = new String(dependencies[idepend].argid[iarg]);
					attrvalue = attrvalue_tmp.replace('-','_');
					} else
					{
						attrvalue = tmp[1];

						// invalid
						// throw new Exception("unable to parse rule rhs: " + map.mapAttributeRule[irule]);
					}

					dependencies[idepend].AddAttribute(attrname, attrvalue);
					dependencies[idepend].AddFieldAttribute(iarg, attrname, attrvalue);
				}											
			}					
		} else
		{	
			if (unrecogword_op_file!=null)
			{
				String str_op = dependencies[idepend].arg[iarg] + "-" + dependencies[idepend].pos[iarg] +  "\n";
				TPLib.AppendStringToFile(unrecogword_op_file, str_op);
			}
		}
	}

	
	String GetMappedDependencies()
	{
		String str = "";
		for (int idepend = 0; idepend < dependencies.length ; idepend++)
		{			
			str += dependencies[idepend].GetMappedDependency();
			
			if (idepend < dependencies.length-1)
			{
				str += " ";
			}			
		}	
		return str;
	}
	
	void ReadDependenciesMapping(String dependenciesMappingFile) throws Exception
	{
		FileReader fr = new FileReader(dependenciesMappingFile);
		BufferedReader br1 = new BufferedReader(fr);
		
		String strline = null;
		while((strline=br1.readLine())!=null)   	//read the grammer file
		{
			int len = strline.length();
			
			if (bDebug)
			{
				System.out.println(len + " "  +strline);
			}
			 
			if (len > 0 )
			{	
				DependencyMap map = new DependencyMap(strline); 
				dependenciesMaps.addElement(map);
			}			
		}
		
		// output for debug
		for (int i =0; i < dependenciesMaps.size(); i++)
		{
			DependencyMap map = (DependencyMap) dependenciesMaps.get(i);
			if (bDebug)
			{	// debug
				map.Print();
			}
		}		
	}
	
	// read the dependencies data
	// which is the output file of stanford parser	
	void ReadDependenciesData(String ipfile, int dataid) throws Exception
	{
		FileReader fr1 = new FileReader(ipfile);
		BufferedReader br1 = new BufferedReader(fr1);
		
		
		String strline = null;
		/* no longer needed, // only need when text file has the tree in the beginning
		if (false) // debug
		{
			while((strline=br1.readLine())!=null)   	
			{
				int len = strline.length();
				System.out.println(len + " "  +strline);
				if (len == 0 )
				{					
					break;
				}			
			}		
		}
		*/
		
		
		Vector<String> tmp_depend=new Vector<String>();
		while((strline=br1.readLine())!=null)   
		{
			if (strline.length() > 0)
			{				
				tmp_depend.addElement(strline);
			}			
		}		
		
		
		int num_depend = tmp_depend.size();
		System.out.println("number of dependencies " +  num_depend  );		
		this.dependencies = new Dependency[num_depend];		
		
		for (int i = 0; i < num_depend ; i++)
		{
			
			dependencies[i] = new Dependency( tmp_depend.elementAt(i), tagwordVt, tagPosVt, dataid);
		}
		
		// println
		for (int i = 0; i < num_depend ; i++)
		{
			System.out.println(i + ": " + dependencies[i].text + " " + dependencies[i].relation + " " 
					+ dependencies[i].arg[0] + " " + dependencies[i].arg[1] + " "
					+ dependencies[i].argid[0] + " " + dependencies[i].argid[1]
					);
		}
		
		br1.close();
	}
	
	// read pos tag
	void ReadPosTagData(String posTagFile) throws Exception
	{
		// read pos tag
		FileReader fr2 = new FileReader(posTagFile);
		BufferedReader br2 = new BufferedReader(fr2);
		String strpos = br2.readLine();
		strpos = strpos.replace("[", "");
		strpos = strpos.replace("]", "");

		strpos = strpos.replace(",/,,", "_comma_/_comma_,");	// substitude for comma, before using comma as a splitter

		String strtag[] = strpos.split(",");

		for (int i = 0; i < strtag.length; i++)
		{
			String str1 = strtag[i].replace("_comma_/_comma_", ",/," );  // rollback 
			
			String str2 = str1.replace(" ", "");
			String str3[] = str2.split("/");
			if (str3.length !=2)
			{
				throw new Exception("ERROR unable to parse tag "+ str2);
			}
			
			tagwordVt.addElement(str3[0]);
			tagPosVt.addElement(str3[1]);
			
			if (true)
			{
				System.out.println("DEBUG TAG:" + i + "[word:"+str3[0]+" tag:"+str3[1]+"]");
			}
		}		
	}		

	/**
	 * The <code>SearchDependenciesMap</code> method tries to find a given dependency in each of the
	 * dependencies maps. First it tries to find an entry in the maps that matches both the text and its
	 * part-of-speech tag. If that is unsuccessful, it tries to match the text without the part-of-speech
	 * tag. If that too is unsuccessful, it returns <b><code>null</code></b>.
	 *
	 * @param dependencySpaces is the text to search for in the dependencies maps. It might have spaces in it.
	 * @param dependencyPOS is the part-of-speech of <code>dependency</code>.
	 * @return <b><code>null</code></b> if the search is unsuccessful. Otherwise, return the dependency map
	 * in which a match was found.
	 */
	private DependencyMap SearchDependenciesMap(String dependencySpaces, String dependencyPOS) {
		boolean bHasMatchedWithPos = false;
		boolean bHasMatchedWithoutPos = false;
		DependencyMap returnValue = null;
		String dependency;						//dependencySpaces with the spaces changed to underscores
		
		dependency = dependencySpaces.replace(' ','_');
		for (int imap=0; imap < dependenciesMaps.size(); imap++ )
		{
			DependencyMap map = dependenciesMaps.elementAt(imap);
			boolean bmatch = map.IsMatchWithPos( dependency, dependencyPOS );
			if (bmatch)
			{
				bHasMatchedWithPos = true; 
				returnValue = map;		
				// kiv , handle multiple matches
				break;
			} else  if((bHasMatchedWithPos == false ) && (bHasMatchedWithoutPos == false))
			{	bmatch = map.IsMatchWithoutPos( dependency, dependencyPOS );
				if (bmatch)
				{
					bHasMatchedWithoutPos = true;
					returnValue = map;	
				}
			}
		}
		return returnValue;
	}

	/**
	 * The <code>extensiveSearch</code> method tries to find the given dependency in a dependency map using
	 * a variety of natural language processing techniques. First it tries to find the dependency without
	 * modification. If that fails, it tries looking for lemmas of the dependency. If that doesn't work 
	 * either, it will try synonyms, and then hypernyms.
	 * 
	 * @param depUnderscores is the text to search for in the dependencies maps. It may have
	 * underscore symbols (_) separating words.
	 * @param depPOS is the part-of-speech of <code>dependency</code>.
	 * @param mapWriter is used to output explanations of how unrecognized words were handled.
	 * @return <b><code>null</code></b> if the search is unsuccessful. Otherwise, return the dependency map
	 * in which a match was found.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 * @throws IOException if there's a problem writing to mapWriter.
	 */
	private DependencyMap extensiveSearch(String depUnderscores, String depPOS, OutputStreamWriter mapWriter) 
			throws JWNLException, IOException {
		String dep;									//depUnderscores without underscores
		HashSet<ArrayList<String>> depLemmaLists;	//All of the lemmas (stemmed forms) of dependency
													//Each list represents a term, like [phone,call]
		String depLemmaTerm;						//The lemmas in a lemma list concatenated together
													//Each list represents a term, like [phone,call]
		HashSet<String> depSynonyms;				//The synonyms of a one-word lemma
		ArrayList<HashSet<String>> depSynonymsList;	//The synonyms of each lemma in a lemma list
		HashSet<String> depSynonymsTerms;			//All of the terms represented by depSynonymsList
													//Each list represents a term, like [phone,call]
		HashSet<String> depHypernyms;				//The hypernyms of a one-word lemma
		ArrayList<HashSet<String>> depHypernymsList;//The hypernyms of each lemma in a lemma list
		HashSet<String> depHypernymsTerms;			//All of the terms represented by depHypernymsList
		HashSet<String> checkedDependencies;		//The dependencies that have already been looked for
		HashMap<String,String> hypernymsToLemmas;	//Maps each hypernym to the lemma that it's a hypernym of
		String lemmaOfHypernym;						//A lemma that has a hypernym that was found
		Integer distance;							//How far away a hypernym may be from a dependency
		Boolean stopLooping;						//Specifies whether a while loop should continue
		DependencyMap returnValue;

		checkedDependencies = new HashSet<String>();			//Initialize
		depLemmaLists = new HashSet<ArrayList<String>>();		//Initialize
		depSynonyms = new HashSet<String>();				//Initialize
		depHypernyms = new HashSet<String>();			//Initialize
		stopLooping = false;									//Initialize

		dep = depUnderscores.replace('_',' ');		//Change underscores to spaces
		checkedDependencies.add(dep);
		returnValue = SearchDependenciesMap(dep,depPOS);	//First try no modifications
		if (USE_LEMMATIZER == false) {
			return returnValue;
		}
		if (returnValue == null) {										//Next, try lemmas
			depLemmaLists = getLemmasOfTerm(dep,depPOS);
			for (ArrayList<String> depLemmaList : depLemmaLists) {
				depLemmaTerm = makeTerm(depLemmaList);
				if (
						(returnValue == null) && 
						( ! checkedDependencies.contains(depLemmaTerm))) {//Don't check the same term twice
					returnValue = SearchDependenciesMap(depLemmaTerm,depPOS);
					if (returnValue != null) {
						if ( ! depLemmaTerm.equals(dep)) {
							mapWriter.write("\"" + depLemmaTerm + "\" is a lemma of \"" + dep + "\".\n");
							doWriteNewline = true;
						}
					}
					checkedDependencies.add(depLemmaTerm);
				}
			}
		}
		if (USE_SYNONYMS) {
			if (returnValue == null) {										//Next, try synonyms
				for (ArrayList<String> depLemmaList : depLemmaLists) {
					depSynonymsList = new ArrayList<HashSet<String>>(depLemmaList.size());		//Initialize
					for (String depLemma : depLemmaList) {
						depSynonyms = getSynonyms(depLemma,depPOS); 
						depSynonymsList.add(depSynonyms);
					}
					depSynonymsTerms = makeTerms(depSynonymsList);
					depLemmaTerm = makeTerm(depLemmaList);
					depSynonymsTerms.addAll(getSynonyms(depLemmaTerm,depPOS));	//Try the whole term
					for (String depSynonymsTerm : depSynonymsTerms) {
						if (
								(returnValue == null) && 
								( ! checkedDependencies.contains(depSynonymsTerm))) {//Don't repeat
							returnValue = SearchDependenciesMap(depSynonymsTerm,depPOS);
							if (returnValue != null) {
								mapWriter.write(
										"\"" + depSynonymsTerm + "\" is a synonym of \"" + depLemmaTerm + 
										"\".\n");
								if ( ! depLemmaTerm.equals(dep)) {
									mapWriter.write(
											"\"" + depLemmaTerm + "\" is a lemma of \"" + dep + "\".\n");
								}
								doWriteNewline = true;
							}
							checkedDependencies.add(depSynonymsTerm);
						}
					}
				}
			}
		}
		if (USE_HYPERNYMS == false) {
			return returnValue;
		}
		if (returnValue == null) {										//Next, try hypernyms
			for (ArrayList<String> depLemmaList : depLemmaLists) {
				distance = 0;
				while ((returnValue == null) && (stopLooping == false)) {
					stopLooping = true;
					distance++;
					hypernymsToLemmas = new HashMap<String,String>();							//Initialize
					depHypernymsList = new ArrayList<HashSet<String>>(depLemmaList.size());		//Initialize
					for (String depLemma : depLemmaList) {
						depHypernyms = getHypernyms(depLemma,depPOS,distance);
						depHypernymsList.add(depHypernyms);
						for (String depHypernym : depHypernyms) {
							hypernymsToLemmas.put(depHypernym,depLemma);
						}
					}
					depHypernymsTerms = makeTerms(depHypernymsList);
					depLemmaTerm = makeTerm(depLemmaList);
					depHypernyms = getHypernyms(depLemmaTerm,depPOS,distance);			//Try the whole term
					depHypernymsTerms.addAll(depHypernyms);
					for (String depHypernym : depHypernyms) {
						hypernymsToLemmas.put(depHypernym,depLemmaTerm);
					}
					for (String depHypernymsTerm : depHypernymsTerms) {
						if (
								(returnValue == null) && 
								( ! checkedDependencies.contains(depHypernymsTerm))) {	//Don't repeat
							returnValue = SearchDependenciesMap(depHypernymsTerm,depPOS);
							if (returnValue != null) {
								lemmaOfHypernym = getLemmaOfHypernym(depHypernymsTerm,hypernymsToLemmas);
								mapWriter.write(
										"\"" + depHypernymsTerm + "\" is a hypernym of \"" + 
										lemmaOfHypernym + "\".\n");
								if ( ! lemmaOfHypernym.equals(dep)) {
									mapWriter.write(
											"\"" + lemmaOfHypernym + "\" is a lemma of \"" + dep + "\".\n");
								}
								doWriteNewline = true;
							} else {
								stopLooping = false;
							}
							checkedDependencies.add(depHypernymsTerm);
						}
					}
				}
			}
		
//			for (ArrayList<String> depLemmaList : depLemmaLists) {
//				distance = 0;
//				while ((returnValue == null) && (stopLooping == false)) {
//					stopLooping = true;
//					distance++;
//					depHypernymsList = new ArrayList<HashSet<String>>(depLemmaList.size());		//Initialize
//					for (String depLemma : depLemmaList) {
//						if (returnValue == null) {
//							depHypernyms = getHypernyms(depLemma,depPOS,distance); 
//							depHypernymsList.add(depHypernyms);
////						}
////							depHypernymsTerms = makeTerms(depHypernymsList);
//							depHypernymsTerms = makeTerms(depHypernyms);
//							for (String depHypernymsTerm : depHypernymsTerms) {
//								if (
//										(returnValue == null) && 
//										( ! checkedDependencies.contains(depHypernymsTerm))) {//Don't repeat
//									returnValue = SearchDependenciesMap(depHypernymsTerm,depPOS);
//									if (returnValue != null) {
//										if (doWriteNewline) {
//											mapWriter.write("\n");
//										}
//										mapWriter.write(
//												"\"" + depHypernymsTerm + "\" is a hypernym of \"" + 
//												depLemma + "\".\n");
//										if ( ! depLemma.equals(dep)) {
//											mapWriter.write(
//													"\"" + depLemma + "\" is a lemma of \"" + dep + "\".\n");
//										}
//										doWriteNewline = true;
//									} else {
//										stopLooping = false;
//									}
//									checkedDependencies.add(depHypernymsTerm);
//								}
//							}
//						}
//					}
//					if (returnValue == null) {
//						depLemmaTerm = makeTerm(depLemmaList);
////						depHypernymsTerms.addAll(getHypernyms(depLemmaTerm,depPOS,distance));//Try the whole term
//						depHypernyms = getHypernyms(depLemmaTerm,depPOS,distance);
//						depHypernymsTerms = makeTerms(depHypernyms);
//						for (String depHypernymsTerm : depHypernymsTerms) {
//							if (
//									(returnValue == null) && 
//									( ! checkedDependencies.contains(depHypernymsTerm))) {//Don't repeat
//								returnValue = SearchDependenciesMap(depHypernymsTerm,depPOS);
//								if (returnValue != null) {
//									mapWriter.write(
//											"\"" + depHypernymsTerm + "\" is a hypernym of \"" + 
//											depLemmaTerm + "\".\n");
//									if ( ! depLemmaTerm.equals(dep)) {
//										mapWriter.write(
//												"\"" + depLemmaTerm + "\" is a lemma of \"" + dep + "\".\n");
//									}
//								} else {
//									stopLooping = false;
//								}
//								checkedDependencies.add(depHypernymsTerm);
//							}
//						}
//					}
//				}
//			}
		}
		return returnValue;
	}

	/**
	 * The <code>getLemmasOfTerm</code> method returns the lemmas of the given term (word or phrase) and part-
	 * of-speech tag found in the WordNet dictionary.
	 * 
	 * @param term is the word or phrase to look up in the WordNet dictionary.
	 * @param partOfSpeech is a part-of-speech tag, using the Stanford part-of-speech tags standard, such as 
	 * "NN". A value of <code><b>null</b></code> indicates that the part-of-speech tag is unknown.
	 * @return a set of all possible combinations of the lemmas found in the WordNet dictionary
	 * that match each word in <code>term</code> with <code>partOfSpeech</code> as the part-of-speech tag. A 
	 * combination of lemmas is represented by a list of lemmas in which the nth lemma in the list is a lemma
	 * of the nth word in <code>term</code>. 
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	private HashSet<ArrayList<String>> getLemmasOfTerm(String term, String partOfSpeech) 
			throws JWNLException {
		String words;								//The given term with the words separated by spaces
		HashSet<ArrayList<String>> returnValue;
		
		words = term.replace('_', ' ');

		returnValue = new HashSet<ArrayList<String>>();							//Initialize
		for (POS pOS : stringToPOSTags(partOfSpeech)) {
			returnValue.addAll(safeGetLemmasOfTerm(words,pOS));
		}
		return returnValue;

	}
	
	/**
	 * The <code>safeGetLemmasOfTerm</code> method is the same as <code>getLemmasOfTerm()</code> with the 
	 * restrictions that the part-of-speech must be one of the WordNet part of speech tags and the words in a 
	 * term must be separated by spaces.
	 * 
	 * @param term is the word or phrase to look up in the WordNet dictionary. If it is a phrase, the words
	 * must be separated by spaces.
	 * @param partOfSpeech is the part-of-speech tag of <code>term</code>. It cannot be <b>null</b>. 
	 * @return a set of all possible combinations of the lemmas found in the WordNet dictionary
	 * that match each word in <code>term</code> with <code>partOfSpeech</code> as the part-of-speech tag. A 
	 * combination of lemmas is represented by a list of lemmas in which the nth lemma in the list is a lemma
	 * of the nth word in <code>term</code>. 
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	private HashSet<ArrayList<String>> safeGetLemmasOfTerm(String term, POS partOfSpeech) 
			throws JWNLException {
		ArrayList<String> wordsList;				//The given term with the words in a list
		
		wordsList = new ArrayList<String>(Arrays.asList(term.split(" ")));
		return safeGetLemmasOfWordsList(wordsList,partOfSpeech);
	}
	
	/**
	 * The <code>safeGetLemmasOfWordsList</code> method is the same as <code>getLemmasOfTerm()</code> except
	 * that the term is represented as a list of words instead of a concatenation of words separated by 
	 * spaces.
	 * 
	 * @param wordsList is the list of words to look up in the WordNet dictionary.
	 * @param partOfSpeech is the part-of-speech tag of <code>term</code>. It cannot be <b>null</b>. 
	 * @return a set of all possible combinations of the lemmas found in the WordNet dictionary
	 * that match each word in <code>wordsList</code> with <code>partOfSpeech</code> as the part-of-speech 
	 * tag. A combination of lemmas is represented by a list of lemmas in which the nth lemma in the list is 
	 * a lemma of the nth word in <code>wordsList</code>. 
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	private HashSet<ArrayList<String>> safeGetLemmasOfWordsList(
			ArrayList<String> wordsList, POS partOfSpeech) throws JWNLException {
		HashSet<String> lemmas;						//The lemmas of a single word
		ArrayList<String> lemmasList;				//A combination of lemmas of the words in the given term
		ArrayList<String> firstWords;				//All of the words in the given term except the last one
		HashSet<ArrayList<String>> firstLemmasLists;//The lemmas lists formed from the words in firstWords
		String lastWord;							//The last word in the given term
		//		HashSet<String> returnValue;
		HashSet<ArrayList<String>> returnValue;

		if (wordsList.size() == 1) {				//Base case
			lemmas = safeGetLemmasOfWord(wordsList.get(0),partOfSpeech);
			returnValue = new HashSet<ArrayList<String>>(lemmas.size());					//Initialize
			for (String lemma : lemmas) {
				lemmasList = new ArrayList<String>(1);
				lemmasList.add(lemma);
				returnValue.add(lemmasList);
			}
			return returnValue;
		}
		firstWords = new ArrayList<String>(wordsList.size() - 1);
		for (String word : wordsList) {
			if (firstWords.size() < wordsList.size() - 1) {
				firstWords.add(word);
			}
		}
		lastWord = wordsList.get(wordsList.size() - 1);
		firstLemmasLists = safeGetLemmasOfWordsList(firstWords,partOfSpeech);		//Recursion
		lemmas = safeGetLemmasOfWord(lastWord,partOfSpeech);
		returnValue = new HashSet<ArrayList<String>>(firstLemmasLists.size() * lemmas.size());	//Initialize
		for (ArrayList<String> firstLemmasList : firstLemmasLists) {
			for (String lemma : lemmas) {
				lemmasList = new ArrayList<String>(firstLemmasList.size() + 1);
				for (String aLemma : firstLemmasList ) {
					lemmasList.add(aLemma);
				}
				lemmasList.add(lemma);
				returnValue.add(lemmasList);
			}			
		}
		return returnValue;
	}

	/**
	 * The <code>safeGetLemmasOfWord</code> method is the same as <code>getLemmasOfWord()</code> with the 
	 * restriction that the part-of-speech must be one of the WordNet part of speech tags.
	 * 
	 * @param word is the word to look up in the WordNet dictionary.
	 * @param aPOStag is a part-of-speech tag of <code>word</code>. It cannot be <b>null</b>. 
	 * @return a set with all of the lemmas found in the WordNet dictionary that match
	 * <code>word</code> and <code>aPOStag</code>.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	@SuppressWarnings({ "unchecked" })
	private HashSet<String> safeGetLemmasOfWord(String word, POS aPOStag) throws JWNLException {
		List<String> baseForms;				//The lemmas in the form of a list (possibly with duplicates)
		HashSet<String> returnValue;		//The lemmas in the form of an HashSet
		
		assert(aPOStag != null);
		MorphologicalProcessor morphologicalProcessor = wordNetDictionary.getMorphologicalProcessor();
		baseForms = morphologicalProcessor.lookupAllBaseForms(aPOStag,word);
		returnValue = new HashSet<String>(baseForms);
		return returnValue;
	}

	/**
	 * The <code>getSynonyms</code> method searches in WordNet for synonyms of the given term
	 * that have the given part-of-speech tag (using the Stanford part-of-speech tags standard). It returns
	 * a set of all of the synonyms found. (It is unusual to find two or more different synonyms in WordNet, 
	 * but it is theoretically possible.)
	 *
	 * @param term is the word or phrase to search for in WordNet. It should consist of lemmas.
	 * @param partOfSpeech is the part-of-speech tag of that term, or <b>null</b> if it's unknown.
	 * @return a list of all of the words in WordNet that are synonyms of the given word and have 
	 * the same part-of-speech tag.
	 * @throws JWNLException if there is a problem searching for the synonyms in WordNet
	 */
	private HashSet<String> getSynonyms(String term, String partOfSpeech) throws JWNLException {
		HashSet<Synset> synsets;					//All of the WordNet synonym sets for a word or phrase
		HashSet<String> returnValue;
		
		returnValue = new HashSet<String>();		//Initialize
		synsets = getSynsets(term,partOfSpeech);
		for (Synset synset : synsets) {
			//If that synset is a key in dictionary, add the value(s) to returnValue
			for (Word word : synset.getWords()) {
				returnValue.add(word.getLemma().replace('_',' '));
			}
		}
		return returnValue;
	}

	/**
	 * The <code>getHypernyms</code> method searches in WordNet for hypernyms of the given word
	 * at the given distance that have the given part-of-speech tag (using the Stanford part-of-speech tags 
	 * standard). It returns a set of all of the hypernyms found.
	 *
	 * @param term is the word or phrase to search for in WordNet. It should consist of lemmas.
	 * @param partOfSpeech is the part-of-speech tag of that term, or <b>null</b> if it's unknown.
	 * @param distance is the number of hops between the term and its hypernyms.
	 * @return a list of all of the words in WordNet that are hypernyms of the given term at the 
	 * given distance and have the same part-of-speech tag.
	 * @throws JWNLException if there is a problem searching for the hypernyms in WordNet
	 */
	private HashSet<String> getHypernyms(String term, String partOfSpeech, Integer distance) 
			throws JWNLException {
		HashSet<Synset> synsets;					//All of the WordNet synonym sets for a term
		HashSet<Synset> hypernymSynsets;			//All of the hypernyms found in WordNet
		HashSet<String> returnValue;
		
		returnValue = new HashSet<String>();		//Initialize
		synsets = getSynsets(term,partOfSpeech);
		for (Synset synset : synsets) {
			hypernymSynsets = getHypernymSynsets(synset,distance);
			//If that synset is a key in dictionary, add the value(s) to returnValue
			for (Synset hypernymSynset : hypernymSynsets) {
				for (Word word : hypernymSynset.getWords()) {
					returnValue.add(word.getLemma().replace('_',' '));
				}
			}
		}
		return returnValue;
	}

	/**
	 * The <code>getHypernymSynsets</code> method searches in the MSEE dictionary for hypernyms of the given 
	 * synset that are a specified distance above that synset. It returns a set of all of the hypernym synsets
	 * found.
	 *
	 * @param synset is the synset to search for in the MSEE dictionary.
	 * @param distance is the number of hops between the term and its hypernym.
	 * @return a list of all of the synsets in the MSEE dictionary that are hypernyms of the given synset at 
	 * the given distance.
	 * @throws JWNLException if there is a problem searching for the hypernyms in WordNet
	 */
	@SuppressWarnings("unchecked")
	private static HashSet<Synset> getHypernymSynsets(Synset synset, Integer distance) 
			throws JWNLException {
		HashSet<Synset> returnValue;
		PointerTargetTree hypernymsTree;					//The hypernyms of the given synset as a tree
		List<PointerTargetNodeList> hypernymsPaths;			//A list of all of the paths down that tree
		PointerTargetNode hypernymNode;						//A node at the end of one of those paths
		Synset hypernymWords;								//The words in that node
		
		returnValue = new HashSet<Synset>();					//Initialize
		hypernymsTree = PointerUtils.getInstance().getHypernymTree(synset,distance);
		hypernymsPaths = hypernymsTree.toList();
		for (PointerTargetNodeList hypernymPath : hypernymsPaths) {
			hypernymNode = (PointerTargetNode)hypernymPath.get(hypernymPath.size() - 1);
			hypernymWords = hypernymNode.getSynset();
			returnValue.add(hypernymWords);
		}
		return returnValue;
	}

	/**
	 * The <code>getSynsets</code> method searches in the WordNet dictionary for synsets with 
	 * the given term (word or phrase) and part-of-speech tag.
	 * 
	 * @param term is the word or phrase to look up in the WordNet dictionary.
	 * @param partOfSpeech is a part-of-speech tag, using the Stanford part-of-speech tags standard, such as 
	 * "NN". A value of <code><b>null</b></code> indicates that the part-of-speech tag is unknown.
	 * @return a list of all of the synsets found in the WordNet dictionary that match
	 * the given term and part-of-speech tags.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	private HashSet<Synset> getSynsets(String term, String partOfSpeech) throws JWNLException {
		String words;								//The given term with the words separated by spaces
		HashSet<Synset> returnValue;
		
		returnValue = new HashSet<Synset>();							//Initialize
		words = term.replace('_', ' ');
		for (POS pOS : stringToPOSTags(partOfSpeech)) {
			returnValue.addAll(safeGetSynsets(words,pOS));
		}
		return returnValue;
	}

	/**
	 * The <code>safeGetSynsets</code> method is the same as <code>getSynsets()</code> with the restrictions 
	 * that the part-of-speech must be one of the WordNet part of speech tags and the words in a phrase must 
	 * be separated by spaces.
	 * 
	 * @param words is the word or phrase to look up in the WordNet dictionary. If it is a phrase, the words
	 * must be separated by spaces.
	 * @param partOfSpeech is the part-of-speech tag of <code>term</code>. It cannot be <b>null</b>. 
	 * @return a list of all of the synsets found in the WordNet dictionary that match
	 * the given term and part-of-speech tag.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	private HashSet<Synset> safeGetSynsets(String words, POS partOfSpeech) throws JWNLException
	{
		IndexWord indexWord;						//The WordNet representation of the given values
		HashSet<ArrayList<String>> lemmasLists;		//All combinations of the base forms of the given words
		HashSet<Synset> returnValue;		
		
		assert(partOfSpeech != null);
		returnValue = new HashSet<Synset>();
		lemmasLists = safeGetLemmasOfTerm(words,partOfSpeech);
		for (ArrayList<String> lemmasList : lemmasLists) {
			indexWord = wordNetDictionary.getIndexWord(partOfSpeech,makeTerm(lemmasList));
			if (indexWord != null) {
				returnValue.addAll(Arrays.asList(indexWord.getSenses())); 
			}
		}
		return returnValue;
	}

	/**
	 * The <code>getLemmaOfHypernym</code> method returns a lemma that a given hypernym is a hypernym of.
	 *
	 * @param hypernym is a term that is a hypernym of some lemma.
	 * @param hypernymsToLemmas is a map from each hypernym to the lemma that it's a hypernym of.
	 * @return the lemma that <code>hypernym</code> is a hypernym of.
	 */
	private static String getLemmaOfHypernym(String hypernym, HashMap<String,String> hypernymsToLemmas) {
		ArrayList<String> hypernymWords;					//A list of the words in the given hypernym
		String space;					//Specifies whether a space should be added
		StringBuffer returnValue;
		
		if (hypernymsToLemmas.containsKey(hypernym)) {
			return hypernymsToLemmas.get(hypernym);
		}
		returnValue = new StringBuffer();							//Initialize
		space = "";													//Initialize
		hypernymWords = new ArrayList<String>(Arrays.asList(hypernym.split(" ")));
		for (String hypernymWord : hypernymWords) {
			if (hypernymsToLemmas.containsKey(hypernymWord)) {
				returnValue.append(space + hypernymsToLemmas.get(hypernymWord));
				space = " ";
			} else {
				System.err.println(
						"Error in DependenciesFilter.getLemmaOfHypernym: \"" + hypernymWord + 
						"\" is not found in hypernymsToLemmas.");
			}
		}
		return returnValue.toString();
	}

	/**
	 * The <code>makeTerm</code> method converts a list of words into a term with spaces between the words.
	 *
	 * @param words is a list of words.
	 * @return the concatenation of the words in <code>list</code>, separated by spaces.
	 */
	private static String makeTerm(ArrayList<String> words) {
		String space;					//Specifies whether a space should be added
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();
		space = "";						//Don't start with a space
		for (String word : words) {
			returnValue.append(space + word);
			space = " ";
		}
		
		return returnValue.toString();
	}

	/**
	 * The <code>makeTerms</code> method converts a list of sets of words into terms by concatenating each
	 * word in the first set to each word in the second set to each word in the third set, etc., with spaces
	 * between the words.
	 *
	 * @param wordsSets is a list of sets of words.
	 * @return all possible concatenations of the words in <code>wordsSets</code>, separated by spaces.
	 */
	private HashSet<String> makeTerms(ArrayList<HashSet<String>> wordsSets) {
		ArrayList<HashSet<String>> firstWordsSets;	//All of the sets in wordsSets except the last one
		HashSet<String> lastWordsSet;				//The last set in wordsSets
		HashSet<String> firstTerms;					//The terms formed from the words in firstWordsSets
		HashSet<String> returnValue;
		
		if (wordsSets.size() == 1) {			//Base case
			return wordsSets.get(0);
		}
		firstWordsSets = new ArrayList<HashSet<String>>(wordsSets.size() - 1);
		for (HashSet<String> wordsSet : wordsSets) {
			if (firstWordsSets.size() < wordsSets.size() - 1) {
				firstWordsSets.add(wordsSet);
			}
		}
		lastWordsSet = wordsSets.get(wordsSets.size() - 1);
		firstTerms = makeTerms(firstWordsSets);				//Recursion
		returnValue = new HashSet<String>(firstTerms.size() * lastWordsSet.size());
		for (String firstTerm : firstTerms) {
			for (String lastWord : lastWordsSet) {
				returnValue.add(firstTerm + " " + lastWord);
			}
		}
		return returnValue;
	}

	/**
	 * The <code>filterLength</code> method returns a set with all of the terms in a given set of terms
	 * except for those that have a different number of words than another given term.
	 *
	 * @param terms is the set of terms to test and possibly return.
	 * @param term is used to determine the desired number of words in a term.
	 * @return all elements in the <code>terms</code> set that have the same number of words as 
	 * <code>term</code>.
	 */
	private static HashSet<String> filterLength(HashSet<String> terms, String term) {
		Integer numWords;					//The desired number of words
		HashSet<String> returnValue;
		
		returnValue = new HashSet<String>(terms.size());
		numWords = countWords(term);
		for (String aTerm : terms) {
			if (numWords == countWords(aTerm)) {
				returnValue.add(aTerm);
			}
		}
		return returnValue;
	}

	/**
	 * The <code>countWords</code> method returns the number of words in a given term. (A term can have 1 or
	 * more words.
	 *
	 * @param term is a word or phrase.
	 * @return the number of words in <code>term</code>.
	 */
	private static Integer countWords(String term) {
		return term.split(" ").length;
	}

	/**
	 * The <code>stringToPOSTags</code> method converts the name of a part-of-speech tag from <code>String</code> 
	 * form into an equivalent list of objects of type <code>POS</code>.
	 *
	 * @param partOfSpeech is a part-of-speech tag, using the Stanford part-of-speech tags standard, such as "NN".
	 * A value of <code><b>null</b></code> indicates that the part-of-speech tag is unknown.
	 * @return a list of part-of-speech tags that have the same meaning as <code>partOfSpeech</code> in the form 
	 * of <code>POS</code> object(s). Any part-of-speech tags that are not found in WordNet are ignored.
	 */
	private ArrayList<POS> stringToPOSTags(String partOfSpeech) {
		ArrayList<POS> returnValue;
		
		returnValue = new ArrayList<POS>(4);										//Initialize		
		if ((partOfSpeech == null) || (this.verbTags.contains(partOfSpeech)))
			returnValue.add(POS.VERB);
		if ((partOfSpeech == null) || (this.nounTags.contains(partOfSpeech)))
			returnValue.add(POS.NOUN);
		if ((partOfSpeech == null) || (this.adjectiveTags.contains(partOfSpeech)))
			returnValue.add(POS.ADJECTIVE);
		if ((partOfSpeech == null) || (this.adverbTags.contains(partOfSpeech)))
			returnValue.add(POS.ADVERB);
		return returnValue;
	}

	/**
	 * The <code>tokenize</code> method converts text into an array of tokens, returning the result.
	 *
	 * @param text is any text that should be tokenized.
	 * @return the same text split up into separate <code>Token</code> objects, which are ordered in an 
	 * <code>ArrayList</code>.
	 */
	private String[] tokenize(String text) {
		return tokenizer.tokenize(text);
	}

	/**
	 * The <code>correctSpelling</code> method fixes spelling errors in a list of words, returning the result.
	 *
	 * @param text is a word or phrase.
	 * @return suggestions for the correct spelling of <code>text</code>.
	 * @throws SuggesterException if there's a problem with the Suggested spelling correction system.
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String> correctSpelling(String text) throws SuggesterException {
		String[] words;							//The given text after it has been separated into words
		ArrayList<Suggestion> corrections;		//The correct spellings of a word
		ArrayList<String> returnValue;
		
		returnValue = new ArrayList<String>(); 		//Initialize
		words = tokenize(text);
		for (String word : words) {
			corrections = spellCorrect.getSuggestions(word,1);
			for (Suggestion correction : corrections) {
				returnValue.add(correction.getWord());
			}
		}
		return returnValue;
	}

	/**
	 * The <code>getRootPath</code> method returns the path to the folder that is being treated as the root.
	 *
	 * @return a string that ends with a forward slash.
	 */
	public static String getRootPath() {
	    String path = DependenciesFilter.class.getProtectionDomain().getCodeSource().getLocation().getPath();	    
	    if (path.endsWith(".jar")) {
	    	path = new File(path).getParentFile().getParentFile().getAbsolutePath();		    	
	    } else if (path.endsWith("bin/")) {
	    	path = new File(path).getParentFile().getAbsolutePath();			    	
	    } else {
	    	path = new File(path).getAbsolutePath();		    			    	
	    }
	    
	    // replace proper space character
		path= path.replace("%20"," ");
		
	    return path+"/";
	}
}
