package utilities;

import java.io.FileInputStream;
import java.util.List;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * The <code>PersonType</code> class outputs (to standard output) all of the hyponyms of "person" in WordNet. 
 *
 * @author Ken Samuel
 * @since 1.6
 */
public class PersonType {

	/** <code>wordNetDictionary</code> is a variable that is used to access WordNet. */
	Dictionary wordNetDictionary;

	/**
	 * The <code>PersonType</code> constructor prepares WordNet for use.
	 * 
	 * @throws Exception if something goes wrong.
	 */
	public PersonType() throws Exception {
		FileInputStream wordNetIn;					//The file input stream to load the WordNet dictionary

		//Set up WordNet
    	wordNetIn = new FileInputStream("config/file_properties.xml");
    	JWNL.initialize(wordNetIn);
    	wordNetIn.close();
    	wordNetDictionary = Dictionary.getInstance();
	}

	/**
	 * The <code>printSynonyms</code> method extracts and prints all of the synonyms in a WordNet 
	 * <code>Synset</code> object.
	 *
	 * @param synset is a sense of a word in the form of a <code>Synset</code>. 
	 */
	public void printSynonyms(Synset synset) {
		Word[] synonymsArray;		//The synonyms of the given term in an array

		if (synset.getPointers(PointerType.INSTANCE_HYPERNYM).length == 0) {
			synonymsArray = synset.getWords();
			for (Word synonym : synonymsArray) {
				System.out.println(synonym.getLemma());
			}
		}
	}
	
	/**
	 * The <code>printHyponyms</code> method finds and prints all of the hyponyms of a WordNet 
	 * <code>Synset</code> object.
	 *
	 * @param synset is a sense of a word in the form of a <code>Synset</code>.
	 * @throws JWNLException if there's a problem with WordNet. 
	 */
	@SuppressWarnings("unchecked")
	public void printHyponyms(Synset synset) throws JWNLException {
		PointerTargetTree hyponymsTree;						//The hyponyms of the given synset as a tree
		List<PointerTargetNodeList> hyponymsPaths;			//A list of all of the paths down that tree
		PointerTargetNode hyponymNode;						//A node at the end of one of those paths
		Synset hyponymWords;								//The words in that node

		hyponymsTree = PointerUtils.getInstance().getHyponymTree(synset);
		hyponymsPaths = hyponymsTree.toList();
		for (PointerTargetNodeList hyponymPath : hyponymsPaths) {
			hyponymNode = (PointerTargetNode)hyponymPath.get(hyponymPath.size() - 1);
			hyponymWords = hyponymNode.getSynset();
			printSynonyms(hyponymWords);
		}
	}


	/**
	 * The <code>main</code> method is where the program begins.
	 * 
	 * @param args are ignored.
	 * @throws Exception if something goes wrong.
	 */
	public static void main(String[] args) throws Exception {
		PersonType me;
		Synset personObject;			//"person" in the form of a WordNet object

		me = new PersonType();
		personObject = Dictionary.getInstance().getIndexWord(POS.NOUN, "person").getSense(1);
		me.printSynonyms(personObject);
		me.printHyponyms(personObject);
	}
}