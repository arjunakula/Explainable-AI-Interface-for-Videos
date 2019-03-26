package mergeData.mergeGraphs.compatibleGraphs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import sparql.translator.utilities.HashMapBuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


/**
 * The <code>Global</code> class consists of things that are used throughout the program (the global 
 * constants, variables, and methods).
 *
 * @author Ken Samuel
 * @version 1.0, Apr 9, 2015
 * @since 1.6
 */
public class Global {

	/** <code>D_BUG</code> specifies whether or not to print information to help with debugging. */
	public static final Boolean D_BUG = true;
	
	/** 
	 * <code>MSEE_OBJECT_TRANSLATIONS</code> is a hash in which the name of each MSEE object type that can be 
	 * found in the input graph points to its proper spelling in the modified graph.
	 */
	public static final HashMap<String,String> MSEE_OBJECT_TRANSLATIONS = HashMapBuilder.build(
			"animal","Animal",
			"Backpack","Luggage",
			"Bike","TwoWheeledVehicle",
			"clothing","Clothing",
			"cupboards","cupboards",
			"Eyeglasses","Glasses",
			"grass","Grass",
			"ground","Ground",
			"head","Head",
			"it","Object",
			"lower_body","LowerBody",
			"objects","Object",
			"pillar","Pillar",
			"plant","Plant",
			"refrigerator","Fridge",		//This really belongs in the lexicon
			"room","Room",
			"Sedan","Automobile",
			"Shirt","top-wear",
			"sidewalk","Sidewalk",
			"small_object","SmallObject",
			"Trash_can","Trashcan"
			);
	
	/** 
	 * <code>MSEE_EVENT_TRANSLATIONS</code> is a hash in which the name of each MSEE event type that can be  
	 * found in the input graph points to its proper spelling in the modified graph.
	 */
	public static final HashMap<String,String> MSEE_EVENT_TRANSLATIONS = HashMapBuilder.build(
			"appear","appear",
			"approach","Approach",
			"ask","ask",
			"call","call",
			"carry","Carry",
			"close","Close",
			"discuss","Discuss",
			"dismount","Dismount",
			"drive","driving",
			"eat","Eat",
			//"enter","IsEntering",
			"enter","Enter",
			//"exit","IsExiting",
			"exit","Exit",
			"face","IsFacing",
			"follow","Follow",
			"get","get",
			"have","have",
			"hold","hold",
			"inspect","inspect",
			"leave","Exit",
			"look","look",
			"move","Move",
			"open","Open",
			"pick","Pick",
			"pickup","PickUp",
			"point","Point",
			"put","Put",
			"putdown","PutDown",
			"raise","raise",
			"reach","reach",
			"read","Read",
			"return","return",
			"ride","ride",
			"run","Run",
			"sit","Sit",
			"stand","Stand",
			"start","Start",
			"stop","Stop",
			"take","Take",
			"talk","Talk",
			"throw","Throw",
			"touch","IsTouching",
			"turn","Turn",
			"use","Use",
			"walk","Walk",
			"wear","Wear",
			"write","write"
			);
	
	/** <code>graph</code> is the graph that was originally produced by <code>TextParserApp</code>. This 
	 * program will modify the graph so that it is compatible with video graphs produced by UCLA's automatic 
	 * video system. 
	 */
	public static Model graph;
	
	/** 
	 * <code>mseeObjectNames</code> is a hash in which the name of each MSEE object (such as "Ellen") 
	 * points to its identifier (such as "Ellen7001"). 
	 */
	public static HashMap<String,String> mseeObjectNames;
	
	/** 
	 * <code>mseeEventTypes</code> is a hash in which the identifier of each MSEE event (such as 
	 * "touched7002") points to its type (such as "touch"). 
	 */
	public static HashMap<String,String> mseeEventTypes;
	
	/** 
	 * <code>modifiedIdentifiers</code> is a hash in which the name of each MSEE object identifier that should
	 * be changed points to the value it should be changed to. 
	 */
	public static HashMap<String,String> modifiedIdentifiers;
	
	/**
	 * The <code>initialize</code> method initializes all of the fields in this class.
	 */
	public static void initialize() {
		graph = null;
		mseeObjectNames = new HashMap<String, String>();
		mseeEventTypes = new HashMap<String, String>();
		modifiedIdentifiers = new HashMap<String, String>();
	}
	
	/**
	 * The <code>removeNamespace</code> method returns the part of the given string that follows the pound 
	 * sign (#).
	 *
	 * @param name is the full name, including the namespace.
	 * @return <code>name</code> with the namespace removed.
	 */
	public static String removeNamespace(String name) {
		String returnValue;
		
		returnValue = name.replaceFirst(".*#","");
		return returnValue;
	}

	/**
	 * The <code>copyNamespace</code> method attaches the same namespace of one name to another name.
	 *
	 * @param absoluteName is a name with a namespace.
	 * @param nonabsoluteName is a name that doesn't have a namespace.
	 * @return <code>nonabsoluteName</code> with the namespace of <code>absoluteName</code> attached to it.
	 */
	public static String copyNamespace(String absoluteName, String nonabsoluteName) {
		String namespace;				//The namespace of the absolute name
		
		namespace = absoluteName.replaceFirst("#.*","");
		return namespace + "#" + nonabsoluteName;
	}

	
	/**
	 * The <code>removeNumber</code> method removes the number at the end of an identifier.
	 *
	 * @param identifier is the identifier of a node in the original graph (such as "plant7004").
	 * @return the part of that identifier that precedes the number (such as "plant"). 
	 */
	public static String removeNumber(String identifier) {
		String returnValue;
		
		returnValue = removeNamespace(identifier);
		if (returnValue.matches("\\w+\\d+")) {		//A sequence of letters followed by a sequence of numbers
			returnValue = returnValue.replaceAll("\\d","");
		} else {
			System.err.println(
					"WARNING in Global.removeNumber: Found an identifier with an unexpected " +
					"format, \"" + identifier + "\".");
			returnValue = identifier;
		}
		return returnValue;
	}

	/**
	 * The <code>loadGraph</code> method reads in an RDF graph from a file and returns it.
	 *
	 * @param filename is the name of the input file. It should be in RDF format.
	 * @return the graph found in that file.
	 * @throws FileNotFoundException if the input file cannot be found.
	 * @throws IOException if there's a problem closing the file input stream.
	 */
	public static Model loadGraph(String filename) throws FileNotFoundException, IOException {
		FileInputStream stream;			//A stream from the file
		Model returnValue;
		
		System.out.print("Loading \"" + filename + "\"...");
		stream = new FileInputStream(filename);
		returnValue = ModelFactory.createDefaultModel();
		returnValue.read(stream,null);
		stream.close();
		System.out.println(" Done.");
	
		return returnValue;
	}

	/**
	 * The <code>saveGraph</code> method saves an RDF graph to a file.
	 *
	 * @param aGraph is the graph to save.
	 * @param filename is the name of the output file.
	 * @throws FileNotFoundException if the folder for the output file cannot be found.
	 * @throws IOException if there's a problem with the file input stream.
	 */
	public static void saveGraph(Model aGraph, String filename) throws FileNotFoundException, IOException {
		FileOutputStream stream;			//A stream to the file
	
		if (aGraph == null) {
			throw new IOException("There is nothing to save.");
		}
		System.out.print("Saving \"" + filename + "\"...");
		stream = new FileOutputStream(filename);
		aGraph.write(stream);
		stream.close();
		System.out.println(" Done.");
	}

	/**
	 * The <code>printGraph</code> method displays a graph of type <code>Model</code> in a pretty format.
	 *
	 * @param aGraph is the graph to print.
	 */
	public static void printGraph(Model aGraph) {
		StmtIterator triples;					//To loop through the triples
		Statement triple;						//One of the triples

		triples = aGraph.listStatements();
		while (triples.hasNext()) {
			triple = triples.next();
//			System.out.print("\t");
			printTriple(triple);
			System.out.println();
		}
	}

	/**
	 * The <code>printTriple</code> method displays a triple of type <code>Statement</code> in a pretty 
	 * format.
	 *
	 * @param triple is the triple to print.
	 */
	public static void printTriple(Statement triple) {
		Resource subject;						//The subject of that triple
		Property predicate;						//The predicate of that triple
		String subjectName;						//The name of the subject
		RDFNode object;							//The object of that triple

		subject = triple.getSubject();
		predicate = triple.getPredicate();
		object = triple.getObject();
		subjectName = subject.getURI();
		System.out.print(
				"<" + Global.removeNamespace(subjectName) + 
				"," + Global.removeNamespace(predicate.toString()) + 
				"," + Global.removeNamespace(object.toString()) + ">");
	}
}