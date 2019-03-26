package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.tartarus.snowball.SnowballStemmer;


/**
 * The <code>NaturalLanguageTools</code> class has a collection of methods that are useful for processing 
 * text.
 *
 * @author Ken Samuel
 * @since 1.6
 * 
 * <p><br />
 * <h5><u>Version 1</u></h5><small><i>June 18, 2013</i></small><ul>
 * <li>Created this class's stub</li>
 * </ul>
 * <p><br />
 * <h5><u>Version 2</u></h5><small><i>June 19, 2013</i></small><ul>
 * <li>Wrote the first version of the <code>getStemmedVerbs</code> method</li>
 * </ul>
 * <h5><u>Version 3</u></h5><small><i>June 20, 2013</i></small><ul>
 * <li>Wrote the final version of the <code>getStemmedVerbs</code> method</li>
 * <li>Wrote the final version of the <code>getCompoundNouns</code> method</li>
 * </ul>
 * <h5><u>Version 4</u></h5><small><i>June 26, 2013</i></small><ul>
 * <li>Wrote all the rest of the methods</li>
 * </ul>
 * <h5><u>Version 5</u></h5><small><i>June 27, 2013</i></small><ul>
 * <li>Enabled <code>getSynonyms</code> to find terms in the MSEE dictionary that are not in WordNet</li>
 * </ul>
 * </ul>
 * <h5><u>Version 6</u></h5><small><i>December 23, 2014</i></small><ul>
 * <li>Added <code>getLemmas</code> to find lemmas in WordNet</li>
 * <li>Added <code>getHypernyms</code> to find hypernyms in WordNet</li>
 * <li>Required synonyms and hypernyms of a term to come from baseforms with the same number of words as that 
 * term.</li>
 * </ul>
 */
public class NaturalLanguageTools {
	
	/** 
	 * <code>VERB_TAGS</code> holds all of the part-of-speech tags produced by OpenNLP's part-of-speech tagger
	 *  that correspond to verbs. 
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
	 * <code>NOUN_TAGS</code> holds all of the part-of-speech tags produced by OpenNLP's part-of-speech tagger
	 *  that correspond to verbs. 
	 */
	private final String[] NOUN_TAGS = {
		"NN",
		"NNS",
		"NNP",
		"NNPS"
	};			
			
	/** 
	 * <code>ADJECTIVE_TAGS</code> holds all of the part-of-speech tags produced by OpenNLP's part-of-speech 
	 *  tagger that correspond to adjectives. 
	 */
	private final String[] ADJECTIVE_TAGS = {
		"JJ",
		"JJR",
		"JJS"
	};			
			
	/** 
	 * <code>ADVERB_TAGS</code> holds all of the part-of-speech tags produced by OpenNLP's part-of-speech 
	 *  tagger that correspond to adverbs. 
	 */
	private final String[] ADVERB_TAGS = {
		"RB",
		"RBR",
		"RBS"
	};			
			
	/** <code>TOKENIZER_FILENAME</code> is the filename of the model that the tokenizer will use. */
	private final File TOKENIZER_FILENAME = new File("models\\en-token.bin");

	/** 
	 * <code>PART_OF_SPEECH_TAGGER_FILENAME</code> is the filename of the model that the part-of-speech tagger 
	 * will use. 
	 */
	private final File PART_OF_SPEECH_TAGGER_FILENAME = new File("models\\en-pos-maxent.bin");
	
	/** <code>STEMMER_FILENAME</code> is the filename of the Snowball stemmer. */
	private final String STEMMER_FILENAME = "org.tartarus.snowball.ext.englishStemmer";
	
	/** <code>CHUNKER_FILENAME</code> is the filename of the model that the chunker will use. */
	private final String CHUNKER_FILENAME = "models\\en-chunker.bin";
	
	/** <code>JWNL_PROPERTIES_FILENAME</code> is the name of the properties file that JWNL needs. */
	private final String JWNL_PROPERTIES_FILENAME = "config\\file_properties.xml";

	/** <code>WORDNET_PARTS_OF_SPEECH</code> is a list of all of the parts of speech found in WordNet. */
	@SuppressWarnings("unused")
	private final POS[] WORDNET_PARTS_OF_SPEECH = {
			POS.NOUN,
			POS.VERB,
			POS.ADJECTIVE,
			POS.ADVERB
	};
	
	/** <code>wordNetDictionary</code> is a variable that is used to access WordNet. */
	Dictionary wordNetDictionary;

	/** <code>tokenizer</code> is the object to use to separate the sentence into tokens. */
	private TokenizerME tokenizer;
	
	/** <code>tokenizerModel</code> is the model that the tokenizer will use. */
	private TokenizerModel tokenizerModel;	

	/** <code>partOfSpeechTagger</code> is the object to use to tag the sentence with part-of-speech tags. */
	private POSTaggerME partOfSpeechTagger;
	
	/** <code>partOfSpeechModel</code> is the model that the part-of-speech tagger will use. */
	
	/** <code>partOfSpeechModel</code> is . */
	private POSModel partOfSpeechModel;
	
	/** <code>chunker</code> is the object to use to find the compound nouns in the sentence. */
	private ChunkerME chunker;
	
	/** <code>stemClass</code> is the word stemmer in the form of a generic Class. */
	@SuppressWarnings("rawtypes")
	private Class stemClass;
	
	/** <code>stemmer</code> is the object to use to stem words. */
	private SnowballStemmer stemmer;
	
	/** <code>verbTags</code> is the part-of-speech tags that correspond to types of verbs. */
	private HashSet<String> verbTags;
	
	/** <code>nounTags</code> is the part-of-speech tags that correspond to types of nouns. */
	private HashSet<String> nounTags;
	
	/** <code>verbTags</code> is the part-of-speech tags that correspond to types of adjectives. */
	private HashSet<String> adjectiveTags;
	
	/** <code>nounTags</code> is the part-of-speech tags that correspond to types of adverbs. */
	private HashSet<String> adverbTags;

	
	/**
	 * The <code>NaturalLanguageTools</code> constructor prepares the natural language processing tools for
	 * use.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	public NaturalLanguageTools() throws Exception {
		ChunkerModel chunkerModel;					//The model that the chunker will use
		FileInputStream chunkerModelIn;				//The file input stream used to load the chunker model
		FileInputStream wordNetIn;					//The file input stream to load the WordNet dictionary

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
		this.tokenizerModel = new TokenizerModel(this.TOKENIZER_FILENAME);
		this.tokenizer = new TokenizerME(this.tokenizerModel);

		//Set up the part-of-speech tagger
		this.partOfSpeechModel = new POSModel(this.PART_OF_SPEECH_TAGGER_FILENAME);
		this.partOfSpeechTagger = new POSTaggerME(this.partOfSpeechModel);

		//Set up the stemmer
		this.stemClass = Class.forName(this.STEMMER_FILENAME);
		this.stemmer = (SnowballStemmer) this.stemClass.newInstance();

		//Set up the chunker
		chunkerModelIn = new FileInputStream(this.CHUNKER_FILENAME);
		chunkerModel = new ChunkerModel(chunkerModelIn);
		this.chunker = new ChunkerME(chunkerModel);
		chunkerModelIn.close();

		//Set up WordNet
		wordNetIn = new FileInputStream(this.JWNL_PROPERTIES_FILENAME);
		JWNL.initialize(wordNetIn);
		wordNetIn.close();
		this.wordNetDictionary = Dictionary.getInstance();
	}

	/**
	 * The <code>getStemmedWord</code> method returns the stemmed form of the given word.
	 *
	 * @param word is the word to be stemmed.
	 * @return the stemmed form of <code>word</code>.
	 */
	public String getStemmedWord(String word) {
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}
	
	/**
	 * The <code>getCompoundNouns</code> method returns all of the compound nouns found in the given sentence,
	 * where a compound noun is the sequence of nouns in any noun phrase that has more than one noun.
	 *
	 * @param sentence is the sentence to search in.
	 * @return a list of compound nouns found in that sentence.
	 */
	public List<String> getCompoundNouns(String sentence) {
		String[] tokenizedSentence;			//The given sentence after it has been tokenized
		String[] partOfSpeechTaggedSentence;//The given sentence with part-of-speech tags
		String[] chunkedSentence;			//The given sentence after it has been separated into chunks
		List<String> returnValue;			//The value that this method will return
		ArrayList<String> currentNoun;		//The (possibly compound) noun that is being analyzed
		
		//Run a tokenizer on the sentence
		tokenizedSentence = this.tokenizer.tokenize(sentence);  
		
		//Run a part-of-speech tagger on the sentence
		partOfSpeechTaggedSentence = this.partOfSpeechTagger.tag(tokenizedSentence);

		//Run a chunked on the sentence
		chunkedSentence = this.chunker.chunk(tokenizedSentence, partOfSpeechTaggedSentence);

		returnValue = new ArrayList<String>(tokenizedSentence.length / 2);
		currentNoun = new ArrayList<String>(tokenizedSentence.length);
		for (int index = 0; index <= tokenizedSentence.length; index++) {
			if (
					(index == tokenizedSentence.length) ||						//No more words
					(! this.nounTags.contains(partOfSpeechTaggedSentence[index])) ||	//Not a noun
					(chunkedSentence[index].startsWith("B-"))) 					//Beginning a new chunk
			{
				if (currentNoun.size() > 1) {						//A compound noun
					returnValue.add(currentNoun.toString());		//Save it
				}
				currentNoun = new ArrayList<String>(tokenizedSentence.length - index); //Restart
			}
			if (
					(index < tokenizedSentence.length) &&
					(this.nounTags.contains(partOfSpeechTaggedSentence[index])))	//Is it a noun 
			{
				currentNoun.add(tokenizedSentence[index]);	//Extend the current noun with the new noun
			}
		}
		return returnValue;
	}
	
	/**
	 * The <code>getLemmas</code> method returns the lemmas of the given term (word or phrase) and part-of-
	 * speech tag found in the WordNet dictionary.
	 * 
	 * @param term is the word or phrase to look up in the WordNet dictionary.
	 * @param partOfSpeech is a part-of-speech tag, using the Stanford part-of-speech tags standard, such as 
	 * "NN". A value of <code><b>null</b></code> indicates that the part-of-speech tag is unknown.
	 * @return a set of all of the lemmas found in the WordNet dictionary that match
	 * the given term and part-of-speech tags.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	public HashSet<String> getLemmas(String term, String partOfSpeech) throws JWNLException {
		String words;								//The given term with the words separated by spaces
		HashSet<String> returnValue;
		
		returnValue = new HashSet<String>();							//Initialize
		words = term.replace('_', ' ');
		for (POS pOS : stringToPOSTags(partOfSpeech)) {
			returnValue.addAll(safeGetLemmas(words,pOS));
		}
		return returnValue;
	}

	/**
	 * The <code>getSynonyms</code> method searches in the MSEE dictionary for synonyms of the given word
	 * that have the given part-of-speech tag (using the Stanford part-of-speech tags standard). It returns
	 * a set of all of the synonyms found. (It is unusual to find two or more different synonyms in the 
	 * dictionary, but it is theoretically possible.)
	 *
	 * @param term is the word or phrase to search for in the MSEE dictionary.
	 * @param partOfSpeech is the part-of-speech tag of that word, or <b>null</b> if it's unknown.
	 * @return a list of all of the words in the MSEE dictionary that are synonyms of the given word and have 
	 * the same part-of-speech tag.
	 * @throws JWNLException if there is a problem searching for the synonyms in WordNet
	 */
	public HashSet<String> getSynonyms(String term, String partOfSpeech) throws JWNLException {
		HashSet<Synset> synsets;					//All of the WordNet synonym sets for a word or phrase
		HashSet<String> lemmas;						//The base form(s) of the given term
		HashSet<String> returnValue;
		
		returnValue = new HashSet<String>();		//Initialize
		lemmas = getLemmas(term,partOfSpeech);
		for (String lemma : lemmas) {
			synsets = getSynsets(lemma,partOfSpeech);
			for (Synset synset : synsets) {
				//If that synset is a key in dictionary, add the value(s) to returnValue
				for (Word word : synset.getWords()) {
					returnValue.add(word.getLemma() + "/" + word.getPOS().getKey());
				}
			}
		}
		return returnValue;
	}
	
	/**
	 * The <code>getHypernyms</code> method searches in the MSEE dictionary for hypernyms of the given word
	 * at the given distance that have the given part-of-speech tag (using the Stanford part-of-speech tags 
	 * standard). It returns a set of all of the hypernyms found.
	 *
	 * @param term is the word or phrase to search for in the MSEE dictionary.
	 * @param partOfSpeech is the part-of-speech tag of that word, or <b>null</b> if it's unknown.
	 * @param distance is the number of hops between the term and its hypernym.
	 * @return a list of all of the words in the MSEE dictionary that are hypernyms of the given term at the 
	 * given distance and have the same part-of-speech tag.
	 * @throws JWNLException if there is a problem searching for the hypernyms in WordNet
	 */
	public HashSet<String> getHypernyms(String term, String partOfSpeech, Integer distance) 
			throws JWNLException {
		HashSet<Synset> synsets;					//All of the WordNet synonym sets for a word or phrase
		HashSet<Synset> hypernymSynsets;			//All of the hypernyms found in WordNet
		HashSet<String> returnValue;
		
		returnValue = new HashSet<String>();		//Initialize
		synsets = getSynsets(term,partOfSpeech);
		for (Synset synset : synsets) {
			hypernymSynsets = getHypernymSynsets(synset,distance);
			//If that synset is a key in dictionary, add the value(s) to returnValue
			for (Synset hypernymSynset : hypernymSynsets) {
				for (Word word : hypernymSynset.getWords()) {
					returnValue.add(word.getLemma() + "/" + word.getPOS().getKey());
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
	 * The <code>safeGetLemmas</code> method is the same as <code>getLemmas()</code> with the restrictions 
	 * that the part-of-speech must be one of the WordNet part of speech tags and the words in a phrase must 
	 * be separated by spaces.
	 * 
	 * @param words is the word or phrase to look up in the WordNet dictionary. If it is a phrase, the words
	 * must be separated by spaces.
	 * @param aPOStag is the part-of-speech tag of <code>term</code>. It cannot be <b>null</b>. 
	 * @return a list of all of the lemmas found in the WordNet dictionary that match
	 * the given term and part-of-speech tag.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	private HashSet<String> safeGetLemmas(String words, POS aPOStag) throws JWNLException {
		List<String> baseForms;				//The lemmas in the form of a list (possibly with duplicates)
		HashSet<String> returnValue;		//The lemmas in the form of an HashSet
		
		assert(aPOStag != null);
		MorphologicalProcessor morphologicalProcessor = wordNetDictionary.getMorphologicalProcessor();
		baseForms = (List<String>)morphologicalProcessor.lookupAllBaseForms(aPOStag,words);
		returnValue = new HashSet<String>(baseForms);
		returnValue = filterLength(returnValue,words);		//A hack to get rid of some incorrect lemmas
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
	 * @param pOS is the part-of-speech tag of <code>term</code>. It cannot be <b>null</b>. 
	 * @return a list of all of the synsets found in the WordNet dictionary that match
	 * the given term and part-of-speech tag.
	 * @throws JWNLException if there's a problem with the WordNet dictionary.
	 */
	private HashSet<Synset> safeGetSynsets(String words, POS pOS) throws JWNLException
	{
		IndexWord indexWord;						//The WordNet representation of the given values
		HashSet<String> lemmas;						//The base forms of the given words
		HashSet<Synset> returnValue;		
		
		assert(pOS != null);
		returnValue = new HashSet<Synset>();
		lemmas = safeGetLemmas(words,pOS);
		for (String lemma : lemmas) {
			indexWord = wordNetDictionary.getIndexWord(pOS,lemma);
			if (indexWord != null) {
				returnValue.addAll(Arrays.asList(indexWord.getSenses())); 
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
	 * The <code>main</code> method is used for testing the methods in this class.
	 *
	 * @param args are ignored.
	 * @throws Exception if something goes wrong.
	 */
	public static void main(String[] args) throws Exception {
		NaturalLanguageTools me = new NaturalLanguageTools();
		
		String sentence2 = "I saw a tall vending machine with flowerpots near the bus stop on 18th Street.";
		System.out.println("The compound nouns in '" + sentence2 + "' are: "+ me.getCompoundNouns(sentence2));
		
		String sentence3 = "Please give me my dog Mr. Washington.";
		System.out.println("The compound nouns in '" + sentence3 + "' are: "+ me.getCompoundNouns(sentence3));
		
		String word1 = "attacking";
		String partOfSpeech1 = null;
		System.out.println("The lemmas of '" + word1 + "' are: " + me.getLemmas(word1,partOfSpeech1));
		
//		String word3 = "auto";
		String word3 = "health care worker";
		String partOfSpeech3 = "NN";
		System.out.println("The lemmas of '" + word3 + "' are: " + me.getLemmas(word3,partOfSpeech3));
		
		String word4 = "automobiles";
		String partOfSpeech4 = "NN";
		System.out.println("The lemmas of '" + word4 + "' are: " + me.getLemmas(word4,partOfSpeech4));
		System.out.println("The stemmed form of '" + word4 + "' is: " + me.getStemmedWord(word4));
		
		String word5 = "went";
		String partOfSpeech5 = null;
		System.out.println("The synonyms of '" + word5 + "' are: " + me.getSynonyms(word5,partOfSpeech5));
		
		String word2 = "heavens";
		String partOfSpeech2 = null;
		System.out.println("The synonyms of '" + word2 + "' are: " + me.getSynonyms(word2,partOfSpeech2));
		System.out.println("The direct hypernyms of '" + word2 + "' are: " + 
				me.getHypernyms(word2,partOfSpeech2,1));
		
		String word6 = "pickup_truck";
		String partOfSpeech6 = null;
		System.out.println("The lemmas of '" + word6 + "' are: " + me.getLemmas(word6,partOfSpeech6));
		System.out.println("The synonyms of '" + word6 + "' are: " + me.getSynonyms(word6,partOfSpeech6));
		System.out.println("The depth 2 hypernyms of '" + word6 + "' are: " + 
				me.getHypernyms(word6,partOfSpeech6,2));
		
		String word7 = "pickup trucks";
		String partOfSpeech7 = "NN";
		System.out.println("The synonyms of '" + word7 + "' are: " + me.getSynonyms(word7,partOfSpeech7));
		System.out.println("The depth 100 hypernyms of '" + word7 + "' are: " + 
				me.getHypernyms(word7,partOfSpeech7,100));
	}
}