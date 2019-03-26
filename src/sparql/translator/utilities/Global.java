package sparql.translator.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sparql.translator.definitions.Definition;
import sparql.translator.definitions.ObjectDefinition;
import sparql.translator.definitions.SetDefinition;
import sparql.translator.query.SPARQLQuery;
import sparql.translator.restrictions.QueryRestriction;
import sparql.translator.restrictions.Restriction;


/**
 * The <code>Global</code> class consists of things that are used throughout the program (the global constants
 * and variables).
 *
 * @author Ken Samuel
 * @version 1.0, Nov 15, 2013
 * @since 1.6
 */
public class Global {

	/** 
	 * <code>INCLUDE_CONFIDENCE_BLOCKS</code> specifies whether there should be tests for confidence values
	 * in the final SPARQL query. 
	 */
	public static final Boolean INCLUDE_CONFIDENCE_BLOCKS = false;
	
	/** <code>inputType</code> is the type of the value that is currently being processed or was the last 
	 * value processed. 
	 */
	public static XMLType inputType;

	/** 
	 * <code>hasStandardQueryStatement</code> specifies whether the &lt;QueryStatement&gt; of the current 
	 * query has the form of a comparison between a number and the cardinality of a set. 
	 */
	public static Boolean hasStandardQueryStatement;
	
	/** 
	 * <code>mathematicalComparisonOperator</code> is the operator in the &lt;QueryStatement&gt; 
	 * if <code>hasStandardQueryStatement</code> is <b><code>true</code></b>. The cardinality of a set is on 
	 * the left side of this operator, and the number is on its right side. If 
	 * <code>hasStandardQueryStatement</code> is <b><code>false</code></b>, then 
	 * <code>mathematicalComparisonOperator</code> is <b><code>null</code></b>. 
	 */
	public static String mathematicalComparisonOperator;
	
	/** 
	 * <code>number</code> is the number  in the &lt;QueryStatement&gt; 
	 * if <code>hasStandardQueryStatement</code> is <b><code>true</code></b>. If 
	 * <code>hasStandardQueryStatement</code> is <b><code>false</code></b>, then 
	 * <code>number</code> is <b><code>null</code></b>.
	 */
	public static Double number;
	
	/** 
	 * <code>useSubsetOfViews</code> specifies whether the query has &lt;eq&gt;, &lt;gt&gt;, or &lt;gte&gt; 
	 * in the QueryStatement.
	 */
	public static Boolean useSubsetOfViews = false;
	
	/** 
	 * <code>LOGICAL_OPERATORS</code> is the set of all logical operators that can be used in queries. Each of
	 * them converts one or more boolean values into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 3.1 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.3, Table 1.
	 */
	public static final HashSet<String> LOGICAL_OPERATORS = new HashSet<String>(Arrays.asList(
			"and",
			"not",
			"or"));
	
	/** 
	 * <code>NUMERICAL_COMPARISON_OPERATORS</code> is a hash in which each numerical comparison operator that  
	 * can be used in queries points to the corresponding operator in SPARQL format. Each of them converts two 
	 * numbers into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 3.2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.3, Table 2.
	 */
	public static final HashMap<String,String> NUMERICAL_COMPARISON_OPERATORS = HashMapBuilder.build(
			"eq","=",
			//"eq",">=",		// munwai; 2015-04-21; getting equal right is challenging, here I am testing if we can relax equal to be "greater than or equal" 
			"gt",">",
			"gte",">=",
			"lt","<",
			"lte","<=",
			"ne","!=");

	/** 
	 * <code>NEGATED_NUMERICAL_COMPARISON_OPERATORS</code> is a hash in which each numerical comparison 
	 * operator that can be used in queries points to the negated version of that operator in SPARQL format. 
	 * Each of them converts two numbers into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 3.2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.3, Table 2.
	 */
	public static final HashMap<String,String> NEGATED_NUMERICAL_COMPARISON_OPERATORS = HashMapBuilder.build(
			"eq","!=",
			"gt","<=",
			"gte","<",
			"lt",">=",
			"lte",">");

	/** 
	 * <code>SET_OPERATORS</code> is the set of all set operators that can be used in queries. Some of them 
	 * (<i>intersection</i> and <i>union</i>) convert one or more sets into a set. However <i>cardinality</i> 
	 * converts a set into a number.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 3.3 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.3, Table 3.
	 */
	public static final HashSet<String> SET_OPERATORS = new HashSet<String>(Arrays.asList(
			"cardinality",
			"intersection",
			"union"));
	
	/** 
	 * <code>TEMPORAL_OPERATORS</code> is a hash in which each temporal operator that can be used in queries
	 * points to the form it should take in the <code>IsTemporalRelation()</code> filter function. Each 
	 * of them converts two time ranges into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 3.4, Tables 1 and 2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.3, Table 4.
	 */
	public static final HashMap<String,String> TEMPORAL_OPERATORS = HashMapBuilder.build(
			"temporal-contains","CONTAINS",		//The first seven are basic temporal relationships (Table 1)
			"temporal-equals","EQUALS",
			"temporal-finished-by","FINISHED_BY",
			"temporal-meets","MEETS",
			"temporal-overlaps","OVERLAPS",
			"temporal-precedes","PRECEDES",
			"temporal-starts","STARTS",
			"temporal-after","AFTER",			//The last four are complex temporal relationships (Table 2)
			"temporal-before","BEFORE",
			"temporal-same-time-as","SAME_TIME_AS",
			"temporal-strictly-before","STRICTLY_BEFORE");
	
	/** 
	 * <code>NEGATED_TEMPORAL_OPERATORS_ARRAY</code> is a 2-dimensional array in which each temporal operator 
	 * that can be used in queries is followed by two lists of forms it may take in the 
	 * <code>IsTemporalRelation()</code> filter function when it is found inside a &lt;not&gt; block. For the 
	 * forms in the second of those two lists, the order of the operands must be swapped. Each of them 
	 * converts two time ranges into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 3.4, Tables 1 and 2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.3, Table 4.
	 */
	public static final String[][] NEGATED_TEMPORAL_OPERATORS_ARRAY = {
		{"temporal-contains"},				//The first seven are basic temporal relationships (Table 1)
			{},
			{"AFTER","BEFORE"},
		{"temporal-equals"},
			{"AFTER","FINISHED_BY"},
			{"AFTER","FINISHED_BY"},
		{"temporal-finished-by"},
			{"AFTER","EQUALS"},
			{"AFTER","FINISHED_BY"},
		{"temporal-meets"},
			{"AFTER","PRECEDES","SAME_TIME_AS"},
			{},
		{"temporal-overlaps"},
			{"EQUALS","PRECEDES","STARTS"},
			{"BEFORE","STARTS"},
		{"temporal-precedes"},
			{"AFTER","MEETS","SAME_TIME_AS"},
			{},
		{"temporal-starts"},
			{"BEFORE","EQUALS"},
			{"BEFORE","STARTS"},
		{"temporal-after"},					//The last four are complex temporal relationships (Table 2)
			{"EQUALS","FINISHED_BY"},
			{"AFTER","FINISHED_BY"},
		{"temporal-before"},
			{"EQUALS","STARTS"},
			{"BEFORE","STARTS"},
		{"temporal-same-time-as"},
			{"STRICTLY_BEFORE"},
			{"STRICTLY_BEFORE"},
		{"temporal-strictly-before"},
			{"SAME_TIME_AS"},
			{"STRICTLY_BEFORE"}};

	/** @see sparql.translator.utilities#NEGATED_TEMPORAL_OPERATORS_ARRAY */
	public static final HashMap<String,String[][]> NEGATED_TEMPORAL_OPERATORS =	
			HashMapBuilder2.build(NEGATED_TEMPORAL_OPERATORS_ARRAY);
	
	/** 
	 * <code>QUANTIFIERS</code> is the set of all quantifiers that can be used in queries. Each 
	 * of them converts a set of variables and a boolean value into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 4 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.4.
	 */
	public static final HashSet<String> QUANTIFIERS = new HashSet<String>(Arrays.asList(
			"exists",
			"forall"));	

	/** 
	 * <code>OBJECT_TYPES</code> is a hash in which each object type that can be used in queries points to its 
	 * spelling in the rdf database. Each object type converts one content identifier, one location 
	 * (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Sections 5.1.1 and 5.1.2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> OBJECT_TYPES = HashMapBuilder.build(
			"animal","Animal",			//The first 34 are base object types (Section 5.1.1)
			"automobile","Automobile",
			//"ball","Ball",
			"ball","SmallObject",
			"smallobject","SmallObject",
			"bike","Bike",
			"bottom-wear","BottomWear",
			"building","Building",
			"chair","Chair",
			"clothing","Clothing",
			//"disc","Disc",
			"disc","SmallObject",
			"female","FemaleHuman",
			//"food","Food",
			"food","SmallObject", // munwai: map food to msee:Eat, which is an action 					
			"footwear","Footwear",
			"grass","Grass",
			"ground","Ground",
			"hat","Hat",
			//"luggage","Luggage",
			"luggage","SmallObject",
			"male","MaleHuman",
			"natural","Natural",
			"object","Object",
			"other","Other",
			//"package","Package",
			"package","SmallObject",
			"paved","Paved",
			"person","Human",
			"plant","Plant",
			"road","Road",
			"room","Room",
			"sidewalk","Sidewalk",
			"sky","Sky",
			"small-object","SmallObject",
			"table","Table",
			//"tool","Tool",
			"tool","SmallObject",
			"top-wear","TopWear",
			//"top-wear","Torso",
			//"trashcan","Trashcan",
			"trashcan","SmallObject",
			"two-wheeled-vehicle","TwoWheeledVehicle",
			"unpaved","Unpaved",
			"vehicle","Vehicle",
			"arm","UpperArm",			//The last 31 are part-of object types (Section 5.1.2)
			"art","Art",
			"bumper","Bumper",
			"ceiling","Ceiling",
			"door","Door",
			"ear","Ear",
			"eye","Eye",
			"fender","Fender",
			"floor","Floor",
			"foot","Foot",
			"glasses","Glasses",
			"hand","Hand",
			"head","Head",
			"hood","Hood",
			"leg","Leg",
			"light","Light",
			"lower-arm","LowerArm",
			"lower-body","LowerBody",
			"lower-leg","LowerLeg",
			"mouth","Mouth",
			"nose","Nose",
			"pillar","Pillar",
			"roof","Roof",
			"torso","Torso",
			"trunk","Trunk",
			"upper-arm","UpperArm",
			"upper-leg","UpperLeg",
			"wall","Wall",
			"wall-switch","WallSwitch",
			"wheel","Wheel",
			"window","Window",
			"car","Car",
			"cardoor","CarDoor",
			"carlight","CarLight"
			);

	/** 
	 * <code>OBJECT_TYPES_REVERSED</code> is the same as <code>OBJECT_TYPES</code>, except the database
	 * object types are the keys and the XML object types are the values. 
	 */
	public static final HashMap<String,String> OBJECT_TYPES_REVERSED = reverseHash(OBJECT_TYPES);

	/** 
	 * <code>SUPER_OBJECTS</code> is a hash in which each object that is a sub-object (type) of some other 
	 * object points to that other object. 
	 */
	public static final HashMap<String,String> SUPER_OBJECTS = HashMapBuilder.build(
			"automobile","vehicle",
			"ball","small-object",
			"bottom-wear","clothing",
			"disc","small-object",
			"female","person",
			"food","small-object",
			"footwear","clothing",
			"grass","unpaved",
			"hat","clothing",
			"luggage","small-object",
			"male","person",
			"natural","unpaved",
			"package","small-object",
			"paved","ground",
			"road","paved",
			"sidewalk","paved",
			"tool","small-object",
			"top-wear","clothing",
			"trashcan","small-object",
			"two-wheeeled-vehicle","vehicle",
			"unpaved","ground");
	
	/** 
	 * <code>SUB_OBJECTS</code> is a hash in which each object that has any sub-objects (types of that object)
	 * points to a list with its sub-objects. 
	 */
	public static final HashMap<String,ArrayList<String>> SUB_OBJECTS = getSubObjects();
	
	/** 
	 * <code>BEHAVIOR_ATTRIBUTES</code> is a hash in which each behavior attribute that can be used in queries
	 * points to its spelling in the rdf database. Each behavior attribute converts one content identifier, 
	 * one location (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.2.1 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> BEHAVIOR_ATTRIBUTES = HashMapBuilder.build(
			//"closed","CloseEvent",
			"crawling","Crawl",
			//"food","Eat",		// map food to Eat (action)
			"eating","Eat",
			"moving","Move",
			//"open","OpenEvent",
			"pointing","Point",
			"reading","Read",
			"running","Run",
			"sitting","Sit",
			"standing","Stand",
			"starting","Start",
			"stationary","Stationary",
			"stopping","Stop",
			"talking","Talk",
			"turning","Turn",
			"turning-left","TurnLeft",
			"turning-right","TurnRight",
			"u-turn","MakeUTurn",
			"walking","Walk",
			"writing","Write",
			"hasbackpack","HasBackpack",
			"hashat","HasHat",
			"hasglasses","HasGlasses",
			"turning","Turn",
			"turing","Turn",
			"turningleft","TurnLeft",
			"turingleft","TurnLeft",
			"turningRight","TurnRight",
			"turingRight","TurnRight",
			"donning","Don",
			"doffing","Doff"
			//"throwing","Throw"
			);
	
	/** 
	 * <code>BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS</code> is a hash in which each behavior attribute that 
	 * can be used in queries points to the corresponding Java function that can be called in a "FILTER" 
	 * clause of a SPARQL query. Each behavior attribute converts one content identifier, 
	 * one location (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.2.1 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS = HashMapBuilder.build(
			"closed","IsClosed",
			"open","IsOpen"
			);
	
	/** 
	 * <code>COLORS</code> is a hash in which each color that can be used in queries points to its spelling
	 * when used in an "IsColor" filter function. Each of them converts one content identifier, one location 
	 * (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.2.2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> COLORS = HashMapBuilder.build(
			"azure","azure",
			"black","black",
			"blue","blue",
			"brown","brown",
			"gray","gray",
			"green","green",
			"orange","orange",
			"pink","pink",
			"purple","purple",
			"red","red",
			"white","white",
			"yellow","yellow");

	/** 
	 * <code>BINARY_RELATIONSHIPS</code> is a hash in which each binary relationship that can be used in 
	 * queries points to its spelling in the rdf database. Each binary relationship converts two content 
	 * identifiers, one location (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.3 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> BINARY_RELATIONSHIPS = HashMapBuilder.build(			
			"following","IsFollowing",
			"opposite-motion","IsOppositeMotion",
			"oppositemotion","IsOppositeMotion",
			"same-motion","IsSameMotion",
			"samemotion","IsSameMotion",
			"together","IsTogether",
			"hit","Hit",
			"hitting","Hit",			
			"catch","Catch",			
			"catching","Catch",
			"ride","Ride",
			"riding","Ride",
			"CLOS","Clos",
			"facing","IsFacing",
			"facing-opposite","IsFacingOpposite",
			"facingopposite","IsFacingOpposite",
			"passing","IsPassing",
			"throw","Throw",
			"throwing","Throw",
			"Carrying","Carry",
			"Carry","Carry",
			"Delivering","Deliver",
			"Deliver","Deliver",
			"Touching","Touch",
			"Touch","Touch",
			"Dropping","Drop",
			"Drop","Drop",
			"PutDown","PutDown",
			"PuttingDown","PutDown",
			"Pickup","PickUp",
			"PickingUp","PickUp"
			);
	
	/** 
	 * <code>BINARY_RELATIONSHIPS_FILTER_FUNCTIONS</code> is a hash in which each binary relationship that 
	 * can be used in queries points to the corresponding Java function that can be called in a "FILTER" 
	 * clause of a SPARQL query. Each binary relationship converts two content identifiers, one location 
	 * (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.3 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> BINARY_RELATIONSHIPS_FILTER_FUNCTIONS = HashMapBuilder.build(
			"below","IsBelow",
			"clear-line-of-sight","IsClearLineOfSight",
			//"facing","IsFacing",
			//"facing-opposite","IsFacingOpposite",
			//"following","IsFollowing",
			"inside","IsInside",
			"on","IsOn",
			//"opposite-motion","IsOppositeMotion",
			"outside","IsOutside",
			//"passing","IsPassing",
			//"same-motion","IsSameMotion",
			//"together","IsTogether",
			"touching","IsTouching");	
	
	/** 
	 * <code>TERNARY_RELATIONSHIPS</code> is a hash in which each ternary relationship that can be used in 
	 * queries points to its spelling in the rdf database. Each ternary relationship converts three content 
	 * identifiers, one location (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.3 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> TERNARY_RELATIONSHIPS = HashMapBuilder.build(
			"putting-in","PuttingIn");
	
	/** 
	 * <code>TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS</code> is a hash in which each ternary relationship that 
	 * can be used in queries points to the corresponding Java function that can be called in a "FILTER" 
	 * clause of a SPARQL query. Each ternary relationship converts three content identifiers, one location 
	 * (optional), and one time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.3 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS = HashMapBuilder.build(
			"closer","IsCloser",
			"farther","IsFarther",
			"occluding","IsOccluding");

	/** 
	 * <code>SPECIAL_RELATIONSHIPS</code> is the set of relationships that are handled differently from all
	 * of the other relationships (and each other). Each of them is (necessarily) a binary relationship that
	 * converts two content identifiers into a boolean value.
	 */
	public static final HashSet<String> SPECIAL_RELATIONSHIPS = new HashSet<String>(Arrays.asList(
			"part-of", 
			"same-object"));	
	
	/** 
	 * <code>ACTIONS</code> is a hash in which each action that can be used in queries points to its spelling 
	 * in the rdf database. Each action converts two content identifiers, one location (optional), and one 
	 * time (optional) into a boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.4 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> ACTIONS = HashMapBuilder.build(
			//"carrying","Carry",
			//"catching","Catch",
			"crossing","Cross",
			"dismounting","Dismount",
			"doffing","Doff",
			"donning","Don",
			"driving","Drive",			
			//"dropping","Drop",
			"loading","Load",
			"mounting","Mount",
			"picking-up","PickUp",
			"putting-down","PutDown",
			"putting-up","PutUp",
			"swinging","Swing",
			"taking-down","TakeDown",
			//"throwing","Throw", 
			"unloading","Unload",
			"wearing","Wear"
			);
	
	/** 
	 * <code>ACTIONS_FILTER_FUNCTIONS</code> is a hash in which each action that can be used in queries points 
	 * to the corresponding Java function that can be called in a "FILTER" clause of a SPARQL query. Each 
	 * action converts two content identifiers, one location (optional), and one time (optional) into a 
	 * boolean value.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 5.4 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Section 5.5.3.2.
	 */
	public static final HashMap<String,String> ACTIONS_FILTER_FUNCTIONS = HashMapBuilder.build(
			//"carrying","Carry",
			//"carrying","IsTouching",
			"carrying","IsTogether",		// mw: carrying convert to isTogether
			"crossing","IsCrossing",
			"driving","IsDriving",
			"entering","IsEntering",
			"exiting","IsExiting",
			"mounting","IsMounting",
			"dismounting","IsDismounting");

	/** 
	 * <code>PREDICATES</code> is a hash that has everything in 
	 * <code>BEHAVIOR_ATTRIBUTES</code>, <code>BINARY_RELATIONSHIPS</code>, 
	 * <code>TERNARY_RELATIONSHIPS</code>, and <code>ACTIONS</code>.
	 */
	public static final HashMap<String,String> PREDICATES = combineHashes(
			BEHAVIOR_ATTRIBUTES,BINARY_RELATIONSHIPS,TERNARY_RELATIONSHIPS,ACTIONS);
	
	/** 
	 * <code>PREDICATES_FILTER_FUNCTIONS</code> is a hash that has everything in 
	 * <code>BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS</code>, <code>BINARY_RELATIONSHIPS_FILTER_FUNCTIONS</code>, 
	 * <code>TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS</code>, and <code>ACTIONS_FILTER_FUNCTIONS</code>.
	 */
	public static final HashMap<String,String> PREDICATES_FILTER_FUNCTIONS = combineHashes(
			BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS,BINARY_RELATIONSHIPS_FILTER_FUNCTIONS,
			TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS,ACTIONS_FILTER_FUNCTIONS);
	
	/** <code>IGNORE_PREDICATES</code> is a list of the predicates that should not be translated. */
	public static final ArrayList<String> IGNORE_PREDICATES = new ArrayList<String>(Arrays.asList(
			"putting-in"));
	
	/** 
	 * <code>TIME_TYPES</code> is a hash in which each type of time that can be used in queries points 
	 * to its spelling in the Java functions that can be called in a "FILTER" clause of a SPARQL query.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 6.2 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Sections 5.1.3 and 5.5.1.
	 */
	public static final HashMap<String,String> TIME_TYPES = HashMapBuilder.build(
			"SceneCentricTimePeriod","SCENE_CENTRIC_TIME_PERIOD",
			"ViewCentricTimePeriod","VIEW_CENTRIC_TIME_PERIOD");

	/** 
	 * <code>LOCATION_TYPES</code> is a hash in which each type of location that can be used in queries points 
	 * to its spelling in the Java functions that can be called in a "FILTER" clause of a SPARQL query.
	 * <p>See "MSEE Formal Language Specification", Version 1.4, Section 6.3 and 
	 * "MSEE EES-SUT Interface Control Document (ICD)", Version 1.1, Sections 5.1.4 and 5.5.2.
	 */
	public static final HashMap<String,String> LOCATION_TYPES = HashMapBuilder.build(
			"ViewCentricPoint","VIEW_CENTRIC_POINT",
			"ViewCentricPolygon","VIEW_CENTRIC_POLYGON",
			"CartesianMetricPoint","CARTESIAN_METRIC_POINT",
			"CartesianMetricPolygon","CARTESIAN_METRIC_POLYGON",
			"CartesianPixelPoint","CARTESIAN_PIXEL_POINT",
			"CartesianPixelPolygon","CARTESIAN_PIXEL_POLYGON",
			"GeodeticPoint","GEODETIC_POINT",
			"GeodeticPolygon","GEODETIC_POLYGON",
			"Volume","VOLUME");

	/** 
	 * <code>NAME_PREFIXES</code> is a set with all of the prefixes of names that can be 
	 * defined in a query. 
	 */
	public static final HashSet<String> NAME_PREFIXES = new HashSet<String>(Arrays.asList(
				"obj",	//This does not appear in any of the "SIGOffice-2013-09-04-Training" example queries
				"obs",
				"time",
				"loc",
				"set",
				"event",
				"query",
				"soc",
				"view",
				"data"		//This is the prefix of the identifiers in the database
		));	
	
	/** 
	 * <code>SPARQL_PREFIXES</code> is a hash in which each abbreviation that may be used in the SPARQL form 
	 * of the query points to its expanded form. 
	 */
	public static final HashMap<String,String> SPARQL_PREFIXES = HashMapBuilder.build(
			"rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#",
			"rdfs","http://www.w3.org/2000/01/rdf-schema#",
			"msee","http://msee/ontology/msee.owl#",
			"data","http://msee/data.rdf#",
			"fn", "java:sparql.");
	
	/** 
	 * <code>ARGUMENT_TYPES</code> is an ordered list with the names of the annotation properties in the rdf 
	 * for the first, second, and third arguments of the behaviors, relationships, and actions. 
	 */
	public static final ArrayList<String> ARGUMENT_TYPES = new ArrayList<String>(Arrays.asList(
			"hasAgent",
			"hasPatient",
			"hasDestination"));	
	
	/** 
	 * <code>UNABLE_TO_RESPOND_FILENAME</code> is the name of the file with the tab-separated values matrix 
	 * that specifies what the video processing cannot currently do. The first row of the matrix should have 
	 * all of the predicates, and the first column should have all of the object types. A value of 
	 * <code>UNABLE_TO_RESPOND_VALUE</code> in the matrix indicates that whenever the predicate is paired with
	 * the object type, an &lt;UnableToRespond&gt; answer should be sent to the EES. 
	 */
	//public static final String UNABLE_TO_RESPOND_FILENAME = "data/UnableToRespond.indoor.vJ.tsv";
	//public static final String UNABLE_TO_RESPOND_FILENAME = "data/UnableToRespond.outdoor_and_auditorium.vJ.tsv";
	public static final String UNABLE_TO_RESPOND_FILENAME = "";		// 20160401 DO NOT LOAD
	
	/** 
	 * <code>UNABLE_TO_RESPOND_VALUE</code> is the value in the UnableToRespond matrix that indicates which 
	 * predicates with which object types should trigger an &lt;UnableToRespond&gt; answer.
	 */
	public static final String UNABLE_TO_RESPOND_VALUE = "UnableToRespond";

	/** 
	 * <code>unableToRespondMatrix</code> is a hashmap in which the XML name of each predicate points to 
	 * the set of object types that should trigger an &lt;UnableToRespond&gt; answer when paired with the 
	 * predicate. 
	 */
	public static HashMap<String,HashSet<String>> unableToRespondMatrix;
	
	/** <code>unableToRespondMessage</code> is a message to output to express the fact that the machine vision
	 * system cannot produce an answer to the query. It is a list of predicate-argument pairs in the query 
	 * that cannot be detected by the machine vision system.
	 */
	public static ArrayList<String> unableToRespondMessage;
	
	/** 
	 * <code>definitions</code> is a hash in which each named set, location, event, object, etc. in the XML
	 * query points to the object with its definition.
	 */
	public static HashMap<String,Definition> definitions;
	
	/**
	 *  <code>persistentNames</code> is a hash in which each persistent name in the XML query points to  
	 *  the object with its definition.
	 */
	public static HashMap<String,Definition> persistentNames;
	
	/**
	 *  <code>timePeriodDefinitionsUsedInLastQuery</code> is a hash in which the name of each time period that 
	 *  was used in the last query that was translated points to the object with its definition.
	 */
	public static HashMap<String,Definition> timePeriodDefinitionsUsedInLastQuery;
	
	/**
	 *  <code>locationDefinitionsUsedInLastQuery</code> is a hash in which the name of each location that 
	 *  was used in the last query that was translated points to the object with its definition.
	 */
	public static HashMap<String,Definition> locationDefinitionsUsedInLastQuery;
	
	/** 
	 * <code>dataFilename</code> is the name of the file where the data that should be 
	 * remembered for future queries is stored.
	 */
	private static String dataFilename;
	
	/** 
	 * <code>nameTranslations</code> specifies how to translate each variable and identifier name from XML
	 * to SPARQL. It is a hash in which each XML name points to the corresponding SPARQL name. 
	 */
	public static HashMap<String,String> nameTranslations;
	
	/** 
	 * <code>databaseIdToXMLId</code> saves the translations from object identifiers that are found in the 
	 * database to the identifier that is used to refer to it in the XML queries. It is a hash in which each 
	 * database name points to the corresponding XML name. 
	 */
	public static HashMap<String,String> databaseIdToXMLId;
	
	/** 
	 * <code>nameCounts</code> is a hash in which the prefix of each name (the part of the 
	 * name without the number at the end) points to the number of names with that prefix. 
	 */
	private static HashMap<String,Integer> nameCounts;
	

	/** <code>unknownObjects</code> is a list of the object identifiers found in XML queries for which the
	 * system has been unable to find a match in the database. 
	 */
	public static HashSet<String> unknownObjects;
	
	/** <code>mseeTypes</code> is a hash in which each variable and constant name points to its MSEE object 
	 * type (such as "person"). 
	 */
	public static HashMap<String,String> mseeTypes;
	
	/** 
	 * <code>typeOfSpecifiedObject</code> is the MSEE type of the object that is currently being searched
	 * for in the database.
	 */
	public static String typeOfSpecifiedObject;

	/**
	 * <code>changedFileObject</code> is a flag that signals when a change has been made to 
	 * the {@link XMLToSPARQLFileObject}, so it needs to be saved to disk. 
	 */
	private static Boolean changedFileObject;
	
	/**
	 * The <code>initializeVariables</code> method initializes the fields in this class.
	 * 
	 * @param filename is the name of the file where the data that should be 
	 * remembered for future queries is stored. If it is <b><code>null</code></b>, then the data
	 * will not be saved on the disk.
	 * @throws FileNotFoundException if there is a problem populating the <code>unableToRespondMatrix</code>.
	 * @throws IOException if there's a problem populating the <code>unableToRespondMatrix</code>.
	 */
	public static void initializeVariables(String filename) throws FileNotFoundException, IOException {
		dataFilename = filename;
		populateUnableToRespondMatrix();
		unableToRespondMessage = new ArrayList<String>();
		definitions = new HashMap<String, Definition>();
		persistentNames = new HashMap<String,Definition>();
		databaseIdToXMLId = new HashMap<String,String>();
		nameCounts = new HashMap<String,Integer>();
		nameTranslations = new HashMap<String,String>();
		unknownObjects = new HashSet<String>();
		mseeTypes = new HashMap<String, String>();
		typeOfSpecifiedObject = null;
		changedFileObject = false;
//		newStoryline();
	}

	/**
	 * The <code>newStoryline</code> method should be called when beginning to process a new storyline. It
	 * clears the persistent names file.
	 *
	 * @throws IOException if there's a problem saving the persistent data to disk.
	 */
	public static void newStoryline() throws IOException {
		persistentNames =  new HashMap<String,Definition>();		//Clear the persistent names
		definitions = new HashMap<String,Definition>();
		databaseIdToXMLId = new HashMap<String,String>();
		nameCounts = new HashMap<String,Integer>();
		changedFileObject = true;
		if (Global.dataFilename != null) {
			saveData();														//Clear the data file
		}
	}

	/**
	 * The <code>reverseHash</code> method swaps the keys and values in the given hash set, returning the 
	 * result.
	 *
	 * @param hash is the hash set that will be reversed.
	 * @return a hash set that is the same as <code>hash</code>, except that its keys are this hash set's 
	 * values and its values are this hash set's keys.
	 */
	private static HashMap<String,String> reverseHash(HashMap<String,String> hash) {
		String hashValue;						//One of the values in the given hash
		HashMap<String,String> returnValue;
		
		returnValue = new HashMap<String,String>(hash.size());
		for (String hashKey : hash.keySet()) {
			hashValue = hash.get(hashKey);
			returnValue.put(hashValue,hashKey);
		}
		return returnValue;
	}	
	
	/**
	 * The <code>combineHashes</code> method combines all of the given hashes into a single hash.
	 *
	 * @param hash1 is one of the hashes.
	 * @param hash2 is one of the hashes.
	 * @param hash3 is one of the hashes.
	 * @param hash4 is one of the hashes.
	 * @return a hash that has everything in all of the given hashes.
	 */
	private static HashMap<String,String> combineHashes(
			HashMap<String,String> hash1, HashMap<String,String> hash2, 
			HashMap<String,String> hash3, HashMap<String,String> hash4 
			) {
		HashMap<String,String> returnValue;
		
		returnValue = new HashMap<String,String>(
				hash1.size() + hash2.size() + hash3.size() + hash4.size() 
				);
		returnValue.putAll(hash1);
		returnValue.putAll(hash2);
		returnValue.putAll(hash3);
		returnValue.putAll(hash4);
		return returnValue;
	}
	
	/**
	 * The <code>getSubObjects</code> method creates the SUB_OBJECTS hash by reversing the SUPER_OBJECTS hash.
	 *
	 * @return a hash in which each object points to a list of its subobjects.
	 */
	private static HashMap<String,ArrayList<String>> getSubObjects() {
		String superObject;				//An object that is a super-object of another object
		HashMap<String,ArrayList<String>> returnValue;
		
		returnValue = new HashMap<String,ArrayList<String>>(SUPER_OBJECTS.size());		//Initialize
		for (String subObject : SUPER_OBJECTS.keySet()) {
			superObject = SUPER_OBJECTS.get(subObject);
			if (! returnValue.containsKey(superObject)) {
				returnValue.put(superObject,new ArrayList<String>());
			}
			returnValue.get(superObject).add(subObject);
		}
		return returnValue;
	}
	
	/**
	 * The <code>translateName</code> method translates the name of a defined object, observer, etc. into
	 * its SPARQL form.
	 *
	 * @param name is the name of something defined in the XML query.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the corresponding name in SPARQL form.
	 */
	public static String translateName(String name, String owner) {
		ObjectDefinition object;	//An object that has name as an id
		String warning;				//A message to let the user know there might be a problem
	String returnValue;
		
		if (definitions.containsKey(name)) {
			object = (ObjectDefinition)definitions.get(name);
			returnValue = object.getName();
		} else {
			returnValue = name;
		}
		if (returnValue == null) {			//FIXME: It's okay for it to be null. That just means I didn't bother sending it to Jena.
			warning = 
					"WARNING in Global.translateName in " + owner + 
					": Encountered an unknown object, " + name + ".";
			System.err.println(warning);
			if ( ! Global.unknownObjects.contains(name)) {
				unableToRespondMessage.add(warning);
			}
			return null;
		}
		return "data:" + returnValue;
	}
	
	/**
	 * The <code>translateVariable</code> method translates a variable name from its XML form to its SPARQL 
	 * form.
	 *
	 * @param nameInXML is the XML version of the variable's name.
	 * @return the SPARQL version of the name of the variable.
	 */
	public static String translateVariable(String nameInXML) {
		String nameInSPARQL;
		
		if (nameTranslations.containsKey(nameInXML)) {	//Does the variable exist yet?
			return nameTranslations.get(nameInXML);		//Yes
		}
		nameInSPARQL = Global.createNewVariable(nameInXML);
		nameTranslations.put(nameInXML,nameInSPARQL);
		return nameInSPARQL;
	}
	
	/**
	 * The <code>translateName</code> method translates an object identifier from its XML form to its 
	 * SPARQL form.
	 *
	 * @param nameInXML is the XML version of the object's identifier.
	 * @return the SPARQL version of the identifier of the object.
	 */
	public static String translateName(String nameInXML) {
		String nameInSPARQL;
		
		if (nameTranslations.containsKey(nameInXML)) {	//Does the variable exist yet?
			return nameTranslations.get(nameInXML);		//Yes
		}
		nameInSPARQL = Global.createNewVariable(nameInXML);
		nameTranslations.put(nameInXML,nameInSPARQL);
		return nameInSPARQL;
	}
	
	/**
	 * The <code>translateBoundVariables</code> method translates the bound variables of this set.
	 *
	 * @param boundVariablesTagNode is the &lt;bvar&gt; tag and everything under it.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the SPARQL bound variables in a list
	 */
	public static ArrayList<String> translateBoundVariables(Node boundVariablesTagNode, String owner) {
		Element boundVariablesElement;		//The given node as type Element
		NodeList boundVariablesNodes;		//All of the bound variables in this set
		Node boundVariableNode;				//One of those bound variables
		Element boundVariable;				//That bound variable as type Element
		String boundVariableXML;			//The name of that bound variable in the XML query
		String boundVariableSPARQL;			//The name of that bound variable in the SPARQL query
		Integer count;						//For counting iterations of a loop
		String warning;						//A message to let the user know there might be a problem
		ArrayList<String> returnValue;

		boundVariablesElement = (Element)boundVariablesTagNode;	
		boundVariablesNodes = Global.getElementsByTagNameCaseInsensitive(boundVariablesElement,"ci");
		if (boundVariablesNodes.getLength() == 0) {
			warning = 
					"WARNING in Global.translateBoundVariables in " + owner + ": No bound variables found.";
			System.err.println(warning);
	    	unableToRespondMessage.add(warning);
			return new ArrayList<String>(0);
		}
		returnValue = new ArrayList<String>(boundVariablesNodes.getLength());
		for (count = 0; count < boundVariablesNodes.getLength(); count++) {
			boundVariableNode = boundVariablesNodes.item(count);
			boundVariable = (Element)boundVariableNode;
			boundVariableXML = boundVariable.getTextContent().trim();
			boundVariableSPARQL = translateVariable(boundVariableXML);
			returnValue.add(boundVariableSPARQL);
		}
		return returnValue;
	}

	/**
	 * The <code>populateUnableToRespondMatrix</code> method loads the file with the matrix that specifies 
	 * when an &lt;UnableToRespond&gt; answer should be sent to the EES and stores its information in the 
	 * <code>unableToRespondMatrix</code>.
	 *
	 * @throws FileNotFoundException if the file with the matrix cannot be found.
	 * @throws IOException if there's a problem reading data from the file
	 */
	public static void populateUnableToRespondMatrix() throws FileNotFoundException, IOException {
		String[] predicates;				//The predicates, listed in the order that they appear in the file
		String objectType;					//One of the object types
		BufferedReader input;				//For reading the file with the matrix
		String line;						//One of the lines in the matrix
		String[] values;					//The values in that line
		Integer index;						//For looping through arrays
		
		if (false)
		{
			input = new BufferedReader(new FileReader(UNABLE_TO_RESPOND_FILENAME));
		
			line = input.readLine();			//The line with the predicate names
			predicates = line.split("\t");
			unableToRespondMatrix = new HashMap<String,HashSet<String>>(predicates.length);
			for (index = 1; index < predicates.length; index++) {
				unableToRespondMatrix.put(predicates[index],new HashSet<String>());
			}
			line = input.readLine();
			while (line != null) {
				values = line.split("\t");
				objectType = values[0];
				for (index = 1; index < predicates.length; index++) {
					if (values[index].equals(UNABLE_TO_RESPOND_VALUE)) {
						unableToRespondMatrix.get(predicates[index]).add(objectType);
					}
				}
				line = input.readLine();
			}
			input.close();
		} else
		{	unableToRespondMatrix = new HashMap<String,HashSet<String>>(0);
		
		}
	}

	/**
	 * The <code>checkUnableToRespond</code> method determines whether the given predicate with the given
	 * argument can be handled by the machine vision system.
	 *
	 * @param predicate is a behavior (in the keys of <code>BEHAVIOR_ATTRIBUTES</code> or 
	 * <code>BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS</code>), color (in the keys of <code>COLORS</code>), 
	 * relationship (in the keys of <code>BINARY_RELATIONSHIPS</code>, <code>TERNARY_RELATIONSHIPS</code>, 
	 * <code>BINARY_RELATIONSHIPS_FILTER_FUNCTIONS</code>, or 
	 * <code>TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS</code>), or action (in the keys of  <code>ACTIONS_</code> 
	 * or <code>ACTIONS_FILTER_FUNCTIONS</code>).
	 * @param argument is an object type (in the keys of <code>OBJECT_TYPES</code>).
	 * @return <b><code>true</code></b> if the machine vision system cannot handle the given predicate 
	 * with the given argument. Otherwise, return <b><code>false</code></b>.
	 */
	public static Boolean checkUnableToRespond(String predicate, String argument) {
		if (unableToRespondMatrix.get(predicate) ==null)
		{	// unrecognize predicate, just return false;
			return false;
		}
				
		if (unableToRespondMatrix.get(predicate).contains(argument)) {
			return true;
		}
		return false;
	}
	
	/**
	 * The <code>checkUnableToRespond</code> method determines whether the given predicate with the given
	 * arguments can be handled by the machine vision system. If not, it returns the reason why.
	 *
	 * @param predicate is a behavior (in the keys of <code>BEHAVIOR_ATTRIBUTES</code> or 
	 * <code>BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS</code>), color (in the keys of <code>COLORS</code>), 
	 * relationship (in the keys of <code>BINARY_RELATIONSHIPS</code>, <code>TERNARY_RELATIONSHIPS</code>, 
	 * <code>BINARY_RELATIONSHIPS_FILTER_FUNCTIONS</code>, or 
	 * <code>TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS</code>), or action (in the keys of <code>ACTIONS_</code> 
	 * or <code>ACTIONS_FILTER_FUNCTIONS</code>).
	 * @param arguments is a list of MSEE object type (in the keys of <code>OBJECT_TYPES</code>).
	 * @return a part of a message to send to the EES if the machine vision system cannot handle the given 
	 * predicate with either of the given arguments. Otherwise, <b><code>null</code></b> is returned. 
	 */
	public static String checkUnableToRespond(String predicate, ArrayList<String> arguments) {
		Boolean unableToRespond;			//Specifies whether the system can respond to this
		String comma;						//Specifies whether to add a comma to the return value
		StringBuffer returnValue;
		
		if ((predicate.contains("pointing")) ||
				(predicate.contains("talking"))
				)
		{
			unableToRespond = false;
		} else	
		{
			unableToRespond = false;
			returnValue = null;
			for (String argument : arguments) {
				if (checkUnableToRespond(predicate,argument)) {
					unableToRespond = true;
				}
			}
		}
		if (unableToRespond) {
			comma = "";
			returnValue = new StringBuffer();
			returnValue.append(predicate + "(");
			for (String argument : arguments) {
				returnValue.append(comma + argument);
				comma = ",";
			}
			returnValue.append(")");
			return returnValue.toString();
		}
		return null;
	}

	/**
	 * The <code>constructUnableToRespondMessage</code> method transforms unableToRespondMessage, which is a 
	 * list of reasons that the system is unable to respond to the query into a single comprehensive
	 * statement to send to the EES.
	 *
	 * @param interruptedReturnValue is the value that would have been returned if the 
	 * <code>UnableToRespondException</code> wasn't thrown.
	 * @return a complete @lt;UnableToRespond&gt; message.
	 */
	public static String constructUnableToRespondMessage(String interruptedReturnValue) {
		String s;					//Specifies whether or not to print an "s"
		StringBuffer returnValue;
		
		returnValue = new StringBuffer();					//Initialize
		if (unableToRespondMatrix.size() == 1) { s = ""; } else { s = "s"; }
		returnValue.append(
				"The following value was not returned because of the <UnableToRespond> exception:\n\n" +
				interruptedReturnValue + "\nThe reason" + s + " for the <UnableToRespond>:\n");
		for (String partOfUnableToRespondMessage : unableToRespondMessage) {
			returnValue.append("\t" + partOfUnableToRespondMessage + "\n");
		}
		unableToRespondMessage = new ArrayList<String>();			//Clear it
		return returnValue.toString();
	}
	
	/**
	 * The <code>createXMLObject</code> method transforms a string into an XML object. It is assumed that the
	 * string consists of valid XML text.
	 *
	 * @param string is XML text.
	 * @return the same XML as <code>string</code>, in the form of a <code>org.w3c.dom.Document</code> object.
	 * @throws IOException if any IO errors occur.
	 * @throws XMLException if the input is not in a legal XML format.
	 */
	public static Document createXMLObject(String string) 
			throws IOException, XMLException {
		DocumentBuilderFactory dbFactory;	//To get a DocumentBuilder object
		DocumentBuilder dBuilder;			//Used to parse the XML file
		Document returnValue;
		
		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(true);
			dBuilder = dbFactory.newDocumentBuilder();
			returnValue = dBuilder.parse(new InputSource(new StringReader(string)));//Change it to a DOM object
			returnValue.getDocumentElement().normalize();							//This step is recommended
		} catch (ParserConfigurationException exception) {
			throw new XMLException(exception.getMessage());
		} catch (SAXException exception) {
			throw new XMLException(exception.getMessage());
		}
		return returnValue;
	}

	
	/**
	 * The <code>createNewVariable</code> method creates a SPARQL variable that hasn't been used before in
	 * this query. The new variable is guaranteed to have no hyphens in it.
	 *
	 * @param baseName is the main part of the variable name, without the preceding "?" and the following 
	 * number.
	 * @return the name of a new SPARQL variable that has the given base name.
	 */
	public static String createNewVariable(String baseName) {
		return "?" + createNewName(removeHyphens(baseName));
	}

	/**
	 * The <code>createNewName</code> method creates a variable or object identifier that hasn't been used 
	 * before in this query. The returned value is guaranteed to have no hyphens in it.
	 *
	 * @param namePrefix is the main part of the variable name, without the preceding "?" and the following 
	 * number.
	 * @return the name of a new SPARQL variable that has the given base name.
	 */
	public static String createNewName(String namePrefix) {
		Integer nameSuffix;		//The number at the end of the new variable name
		
		if (nameCounts.containsKey(namePrefix)) {
			nameSuffix = nameCounts.get(namePrefix);
			nameSuffix++;
		} else {											//We're seeing this name for the first time
			nameSuffix = 1;
		}
		nameCounts.put(namePrefix,nameSuffix);				//Update nameCounts
		return namePrefix + "_" + nameSuffix.toString();
	}

	/**
	 * The <code>stripPrefix</code> method removes everything up to and including the first hyphen in the 
	 * given string and returns the result.
	 *
	 * @param string is the string to be stripped.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the given string with the first part removed.
	 */
	public static String stripPrefix(String string, String owner) {
		Integer hyphenPosition;			//The position of the first hyphen
		String warning;					//A message to let the user know there might be a problem
		
		hyphenPosition = string.indexOf('-');
		if (hyphenPosition == -1) {
			warning = 
					"WARNING in Global.stripPrefix in " + owner + ": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	unableToRespondMessage.add(warning);
			return string;
		}
		return string.substring(hyphenPosition+1);
	}

	/**
	 * The <code>removeNewlineCharacter</code> method removes the '\n' character from the end of the given
	 * string, if there is one, and returns the result. If the input does not end with '\n', it is returned
	 * unchanged.
	 *
	 * @param string is the string to remove the newline character from.
	 * @return <code>string</code> with the last character removed, if that character is '\n', or 
	 * <code>string</code> unchanged otherwise.
	 */
	public static String removeNewlineCharacter(String string) {
		String returnValue;
		
		if ((string == null) || (string.equals(""))) {
			returnValue = string;
		} else if (string.substring(string.length() - 1).equals("\n")) {
			returnValue = string.substring(0,string.length() - 1);
		} else {
			returnValue = string;
		}
		return returnValue;
	}

	/**
	 * The <code>cleanJenaOutput</code> method fixes some problems with Jena's results that are sent to
	 * <code>XMLToSPARQLTranslator.reportResult()</code>. Any pound sign (#) is removed from the beginning of 
	 * the given string and any comma is removed from the end of it.
	 *
	 * @param string is a string that is returned by Jena.
	 * @return <code>string</code> except the first character of <code>string</code> if it is '#'  and the 
	 * last character of <code>string</code> if it is ','.
	 */
	public static String cleanJenaOutput(String string) {
		String returnValue;
		
		returnValue = string;
		if (returnValue.startsWith("#")) {
			returnValue = returnValue.substring(1); 
		}
		if (returnValue.endsWith(",")) {
			returnValue = returnValue.substring(0,returnValue.length() - 1); 
		}
		return returnValue;
	}	

	/**
	 * The <code>removeHyphens</code> method makes sure that the output string does not have any hyphens.
	 *
	 * @param string is a string that might have any number of hyphens.
	 * @return a string with no hyphens that is probably unique.
	 */
	public static String removeHyphens(String string) {
		return string.
				replace("-_","-._").
				replace("_-","_.-").
				replace("__","___").
				replace("-","__");
	}

	/**
	 * The <code>removePrefix</code> method makes sure that the output string does not begin with "data:".
	 *
	 * @param string is a string that might begin with "data:".
	 * @return all of <code>string</code> after removing "data:" from the beginning, if possible.
	 */
	public static String removePrefix(String string) {
		if (string.startsWith("data:")) {
			return string.substring(5);
		}
		return string;
	}

	/**
	 * The <code>fixDatabaseIdentifier</code> method makes a minor modification that is required when putting
	 * a database identifier in a filter function.
	 *
	 * @param string is something that is about to go in a filter function.
	 * @return is the same as <code>string</code> unless string is a database identifier, in which case it's
	 * modified for the filter function.
	 */
	public static String fixDatabaseIdentifier(String string) {
		if (string.startsWith("data:")) {
			return "\"" + string.substring(5) + "\"";
		}
		return string;
}

	
	/**
	 * The <code>removeFirstChild</code> method removes the first child node in the given node. It returns
	 * that child.
	 *
	 * @param node is a node with at least one child.
	 * @return the child node that was removed.
	 */
	public static Node removeFirstChild(Node node) {
		NodeList childNodes;			//The children of the given node
		Node removeNode;				//The node to be removed
		Node returnValue;

		returnValue = null;
		childNodes = node.getChildNodes();
		while (returnValue == null) {
			if (childNodes.getLength() > 0) {
				removeNode = childNodes.item(0);
				if (removeNode.getNodeType() == Node.ELEMENT_NODE) {
					returnValue = node.removeChild(removeNode);
				} else {
					node.removeChild(removeNode);				//Remove the text node too
				}
			} else {
				return null;		//Nothing found in the <Objects> node
			}
		}
		return returnValue; //If no <object-spec> nodes are found, null is returned
	}

	/**
	 * The <code>reverseOperator</code> method changes "&lt;" to "&gt;" and vice versa.
	 *
	 * @param operator is a numeric operator in SPARQL form.
	 * @return the opposite of the given operator.
	 */
	public static String reverseOperator(String operator) {
		String returnValue;

		returnValue = operator;
		returnValue = returnValue.replace('<','*');
		returnValue = returnValue.replace('>','<');
		returnValue = returnValue.replace('*','>');
		return returnValue;
	}

	/**
	 * The <code>storeDefinition</code> method stores the given definition in the proper global variable(s).
	 *
	 * @param definition is the definition of a time period, location, object, set, or event.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public static void storeDefinition(Definition definition, String owner) {
		String id;						//The identifier of the new definition
		String warning;					//A message to let the user know there might be a problem
		
		id = definition.getId();
		if (id == null) {
			warning = 
					"WARNING in Global.storeDefinition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	unableToRespondMessage.add(warning);
			return;
		}
		if (
				(hasDefinition(id)) &&
				( ! id.startsWith("set-("))) {
			System.err.println(
					"WARNING in Global.storeDefinition in " + owner + 
					": Found more than 1 definition of \"" + id + "\".");
		}
		definitions.put(id,definition);
		if (definition.getPersists()) {
			persistentNames.put(id,definition);
			changedFileObject = true;
		}
	}

	/**
	 * The <code>removeDefinition</code> method removes the given definition from the proper global 
	 * variable(s).
	 *
	 * @param definition is the definition of a time period, location, object, set, or event.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public static void removeDefinition(Definition definition, String owner) {
		String id;						//The identifier of the new definition
		String warning;					//A message to let the user know there might be a problem
		
		id = definition.getId();
		if ((id == null) || ( ! hasDefinition(id))) {
			warning = 
					"WARNING in Global.removeDefinition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	unableToRespondMessage.add(warning);
			return;
		}
		definitions.remove(id);
		if (definition.getPersists()) {
			persistentNames.remove(id);
			changedFileObject = true;
		}
	}

	/**
	 * The <code>changedDefinition</code> method should called whenever a change is made to a definition. 
	 * Certain things need to be done.
	 *
	 * @param definition is the thing that was changed.
	 */
	public static void changedDefinition(Definition definition) {
		Boolean persists;						//Specifies whether this definition is a persistent definition
		
		if (definition != null) { 
			persists = definition.getPersists();
			if ((persists != null) && (persists == true)) {
				changedFileObject = true;				//So the change gets saved to disk
			}
		}
	}
	
	/**
	 * The <code>updateNameCount</code> should be called whenever a name prefix is used to create a new name.
	 *
	 * @param namePrefix is the name prefix that was used to create a new name.
	 * @see Global#nameCounts
	 */
	public static void updateNameCount(String namePrefix) {
		nameCounts.put(namePrefix,getNameCount(namePrefix) + 1);
		changedFileObject = true;				//So the change gets saved to disk
	}

	/**
	 * The <code>getNameCount</code> returns the number of times that a particular name prefix has been used,
	 *
	 * @param namePrefix is the prefix of interest.
	 * @return the number of times <code>namePrefix</code> has been used as the prefix of a name.
	 * @see Global#nameCounts
	 */
	public static Integer getNameCount(String namePrefix) {
		if (nameCounts.containsKey(namePrefix)) {
			return nameCounts.get(namePrefix);
		}
		return 0;
	}

	/**
	 * The <code>saveData</code> method saves the data that must be remembered for future queries to the disk.
	 *
	 * @throws FileNotFoundException if there is a problem with the file name.
	 * @throws IOException if there's a problem writing to the file.
	 */
	public static void saveData() 
			throws FileNotFoundException, IOException {
		ObjectOutputStream out;					//The output stream
		XMLToSPARQLFileObject fileObject;		//The object to save to the file
		
		if ((dataFilename != null) && (changedFileObject == true)) {
			System.out.println("Saving \"" + dataFilename + "\".");
			fileObject = new XMLToSPARQLFileObject(persistentNames,nameCounts);
		
			// backup first
			File file = new File(Global.dataFilename);
			if (file.exists())
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
				Date date = new Date();
				String backup_name = Global.dataFilename + "." + dateFormat.format(date)+ ".bk";
				File file2 = new File(backup_name);
				file.renameTo(file2);
			}	
			
			out = new ObjectOutputStream(new FileOutputStream(dataFilename));
			out.writeObject(fileObject);
			out.close();
			changedFileObject = false;
		}
	}

	/**
	 * The <code>loadData</code> method loads the persistent names and variable counts from disk.
	 *
	 * @throws FileNotFoundException if there is a problem with the file name.
	 * @throws IOException if there's a problem reading from the file.
	 * @throws ClassNotFoundException if there's something wrong with the file.
	 */
	@SuppressWarnings("unchecked")
	public static void loadData()
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in;					//The input stream
		XMLToSPARQLFileObject fileObject;		//The object that was saved to the file
		
		if (dataFilename != null) {
			System.out.println("Loading \"" + dataFilename + "\".");
			in = new ObjectInputStream(new FileInputStream(dataFilename));
			fileObject = (XMLToSPARQLFileObject)in.readObject();
			in.close();
			persistentNames = fileObject.getPersistentNames();
			nameCounts = fileObject.getNameCounts();
			definitions = (HashMap<String,Definition>)persistentNames.clone();
		}
	}
	
	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in the given list
	 * to make sure the predicate filter function restrictions are tested last.
	 *
	 * @param restrictions is a list of restrictions.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the given restrictions sorted in order so that the predicate filter functions are tested last.
	 */
	public static LinkedHashSet<Restriction> reorderRestrictions(
			LinkedHashSet<Restriction> restrictions, String owner) {
		LinkedHashSet<Restriction> predicateFilterFunctionRestrictions;	//They have predicate filter functions
		LinkedHashSet<Restriction> otherRestrictions;				//They have no predicate filter functions
		Restriction reorderedRestriction;		//A restriction with the restrictions within it reordered
		SPARQLQuery subQuery;					//The other restrictions are pushed into a new SPARQL query
		QueryRestriction newRestriction;		//Replaces all of the other restrictions
		LinkedHashSet<Restriction> returnValue;
	
		predicateFilterFunctionRestrictions = new LinkedHashSet<Restriction>(restrictions.size());
		otherRestrictions = new LinkedHashSet<Restriction>(restrictions.size());
		for (Restriction restriction : restrictions) {		//Sort the restrictions
			reorderedRestriction = restriction.reorderRestrictions(owner);
			if (restriction.hasPFFRestriction() == true) {
				predicateFilterFunctionRestrictions.add(reorderedRestriction);
			} else {
				otherRestrictions.add(reorderedRestriction);
			}
		}
		returnValue = new LinkedHashSet<Restriction>(1 + predicateFilterFunctionRestrictions.size());
		if (predicateFilterFunctionRestrictions.size() >= 1) {
			if (otherRestrictions.size() >= 1) {
				subQuery = new SPARQLQuery();
				subQuery.distinct();
				subQuery.addRestrictions(otherRestrictions);
				newRestriction = new QueryRestriction(subQuery,true);
				returnValue.add(newRestriction);
			}
			returnValue.addAll(predicateFilterFunctionRestrictions);
		} else {
			returnValue.addAll(otherRestrictions);
		}
		return returnValue;
	}
	
	/**
	 * The <code>hasDefinition</code> method reports whether or not a given identifier has been assigned a 
	 * definition in <code>definitions</code> yet. 
	 *
	 * @param id is the identifier to check.
	 * @return <b><code>true</code></b> if <code>id</code> is found in <code>definitions</code>. Otherwise, 
	 * return <b><code>false</code></b>.
	 */
	public static Boolean hasDefinition(String id) {
		return definitions.containsKey(id);
	}

	/**
	 * The <code>hasPFFRestriction</code> method determines whether a given set of restrictions includes any
	 * predicate filter function restrictions.
	 *
	 * @param restrictions is a set of restrictions to test.
	 * @return <b><code>true</code></b> if and only if there is at least one predicate filter function in
	 * <b><code>restrictions</code></b>.
	 */
	 public static Boolean hasPFFRestriction(LinkedHashSet<Restriction> restrictions) {
		Boolean returnValue;
		
		returnValue = false;					//Until proven otherwise
		for (Restriction restriction : restrictions) {
			if (restriction.hasPFFRestriction() == true) {
				returnValue = true;
			}
		}
		return returnValue;
	}
	
	/**
	 * The <code>isTimeId</code> method determines whether the given string is the name of a time.
	 *
	 * @param string is a string that might be the name of a time.
	 * @return <b><code>true</code></b> if the string begins with "time-" and <b><code>false</code></b> 
	 * otherwise.
	 */
	public static Boolean isTimeId(String string) {
		if ((string.length() >= 5) && (string.substring(0,5).equalsIgnoreCase("time-"))) {
			return true;
		}
		return false;
	}

	/**
	 * The <code>isLocationId</code> method determines whether the given string is the name of a location.
	 *
	 * @param string is a string that might be the name of a set.
	 * @return <b><code>true</code></b> if the string begins with "loc-" and <b><code>false</code></b> 
	 * otherwise.
	 */
	public static Boolean isLocationId(String string) {
		if ((string.length() >= 4) && (string.substring(0,4).equalsIgnoreCase("loc-"))) {
			return true;
		}
		return false;
	}

	/**
	 * The <code>isSetId</code> method determines whether the given string is the name of a set.
	 *
	 * @param string is a string that might be the name of a set.
	 * @return <b><code>true</code></b> if the string begins with "set-" and <b><code>false</code></b> 
	 * otherwise.
	 */
	public static Boolean isSetId(String string) {
		if ((string.length() >= 4) && (string.substring(0,4).equalsIgnoreCase("set-"))) {
			return true;
		}
		return false;
	}

	/**
	 * The <code>isEventId</code> method determines whether the given string is the name of an event.
	 *
	 * @param string is a string that might be the name of a event.
	 * @return <b><code>true</code></b> if the string begins with "event-" and <b><code>false</code></b> 
	 * otherwise.
	 */
	public static Boolean isEventId(String string) {
		if ((string.length() >= 6) && (string.substring(0,6).equalsIgnoreCase("event-"))) {
			return true;
		}
		return false;
	}

	/**
	 * The <code>isViewId</code> method determines whether the given string is the name of a view.
	 *
	 * @param string is a string that might be the name of a view.
	 * @return <b><code>true</code></b> if the string begins with "view-" and <b><code>false</code></b> 
	 * otherwise.
	 */
	public static Boolean isViewId(String string) {
		if ((string.length() >= 5) && (string.substring(0,5).equalsIgnoreCase("view-"))) {
			return true;
		}
		return false;
	}

	/**
	 * The <code>isVariable</code> method determines whether the input is the name of a variable or the
	 * name of a constant.
	 *
	 * @param string is the name of a variable or a constant.
	 * @return <b><code>true</code></b> if <code>string</code> is the name of a variable and 
	 * <b><code>false</code></b> otherwise.
	 */
	public static Boolean isVariable(String string) {
		String[] parts;			//The name after it has been split on hyphens
		
		if (string.contains("-")) {
			parts = string.split("-");
		} else if (string.contains(":")) {
			parts = string.split(":");
		} else {
			return true;
		}
		if (NAME_PREFIXES.contains(parts[0])) {
			return false;
		}
		return true;
	}

	/**
	 * The <code>isNumber</code> method determines whether the given string is a number, such as "0.0".
	 *
	 * @param string is any string.
	 * @return <b><code>true</code></b> if <code>string</code> has the form of an integer or floating point
	 * number.
	 */
	public static Boolean isNumber(String string) {
		try {
			Double.parseDouble(string);
		} catch (NumberFormatException exception) {
			return false;
		}
		return true;
	}

	/**
	 * The <code>getUnion</code> method merges two sets into the union of those sets.
	 *
	 * @param set1 is one of the sets to be merged.
	 * @param set2 is the other set.
	 * @return a set that has everything in the two given sets.
	 */
	public static SetDefinition getUnion(SetDefinition set1, SetDefinition set2) {
		SetDefinition returnValue;
		
		returnValue = set1;
		returnValue.addBoundVariables(set2.getBoundVariables());
		returnValue.addRestrictions(set2.getRestrictions());
		return returnValue;
	}

	/**
	 * The <code>getIntersection</code> method merges two sets into the intersection of those sets.
	 *
	 * @param set1 is one of the sets to be merged.
	 * @param set2 is the other set.
	 * @return a set that has everything in the two given sets.
	 */
	public static SetDefinition getIntersection(SetDefinition set1, SetDefinition set2) {
		SetDefinition returnValue;
		
		returnValue = new SetDefinition();
		returnValue.addBoundVariables(set1.getBoundVariables());
		returnValue.addBoundVariables(set2.getBoundVariables());
		//TODO (But nothing is calling this method...)
		return returnValue;
	}

	/**
	 * The <code>getElementsByTagNameCaseInsensitive</code> method returns a <code>NodeList</code> of all the 
	 * <code>Elements</code> in the given document (in the order that they appear) with the given tag name in 
	 * any of four different forms: 1) the given form, 2) all lowercase, 3) all caps, and 4) capitalized. It 
	 * is assumed that the document is internally consistent, meaning that all of the occurrences of the tag 
	 * name have the same form.
	 *
	 * @param document is the document in which to search for all occurrences of a tag name.
	 * @param tagname is the name of the tag to match on. The special value "*" matches all tags.
	 * @return a new <code>NodeList</code> object containing all the occurrences of <code>tagname</code>.
	 */
	public static NodeList getElementsByTagNameCaseInsensitive(Document document, String tagname) {
		return getElementsByTagNameCaseInsensitive(document.getDocumentElement(),tagname);
	}

	/**
	 * The <code>getElementsByTagNameCaseInsensitive</code> method returns a <code>NodeList</code> of all the 
	 * <code>Elements</code> in the given node (in the order that they appear) with the given tag name in 
	 * any of four different forms: 1) the given form, 2) all lowercase, 3) all caps, and 4) capitalized. It 
	 * is assumed that the document is internally consistent, meaning that all of the occurrences of the tag 
	 * name have the same form.
	 *
	 * @param node is the node in which to search for all occurrences of a tag name.
	 * @param tagname is the name of the tag to match on. The special value "*" matches all tags.
	 * @return a new <code>NodeList</code> object containing all the occurrences of <code>tagname</code>.
	 */
	public static NodeList getElementsByTagNameCaseInsensitive(Node node, String tagname) {
		return getElementsByTagNameCaseInsensitive((Element)node,tagname);
	}

	/**
	 * The <code>getElementsByTagNameCaseInsensitive</code> method returns a <code>NodeList</code> of all the 
	 * <code>Elements</code> in the given element (in the order that they appear) with the given tag name in 
	 * any of four different forms: 1) the given form, 2) all lowercase, 3) all caps, and 4) capitalized. It 
	 * is assumed that the document is internally consistent, meaning that all of the occurrences of the tag 
	 * name have the same form.
	 *
	 * @param element is the document in which to search for all occurrences of a tag name.
	 * @param tagname is the name of the tag to match on. The special value "*" matches all tags.
	 * @return a new <code>NodeList</code> object containing all the occurrences of <code>tagname</code>.
	 */
	public static NodeList getElementsByTagNameCaseInsensitive(Element element, String tagname) {
		String tagname1,tagname2,tagname3,tagname4;				//The four different forms of the tag name
		String lowerFirstCharacter, upperFirstCharacter;		//The tag name's first character in two forms
		NodeList elements1,elements2,elements3,elements4;		//The elements of each form
		
		tagname1 = tagname;							//The given form
		tagname2 = tagname.toLowerCase();
		tagname3 = tagname.toUpperCase();
		lowerFirstCharacter = tagname2.substring(0,1);
		upperFirstCharacter = lowerFirstCharacter.toUpperCase();
		tagname4 = tagname1.replaceFirst(lowerFirstCharacter,upperFirstCharacter);
		elements1 = element.getElementsByTagName(tagname1);
		elements2 = element.getElementsByTagName(tagname2);
		elements3 = element.getElementsByTagName(tagname3);
		elements4 = element.getElementsByTagName(tagname4);
		if (elements1.getLength() > 0) { return elements1; }
		if (elements2.getLength() > 0) { return elements2; }
		if (elements3.getLength() > 0) { return elements3; }
		return elements4;
	}

	/**
	 * The <code>getChildNode</code> method returns the useful child of the given XML node. It is assumed that 
	 * there is only one useful child.
	 *
	 * @param parent is the XML node that is the parent of the desired child node.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the child as an XML node.
	 */
	public static Node getChildNode(Node parent, String owner) {
		return getNode(parent.getChildNodes(),"a <"+parent.getNodeName()+"> in "+owner);
	}

	/**
	 * The <code>getNode</code> method returns a useful XML node from a given list of XML 
	 * nodes. It is assumed that there is only one such node.
	 *
	 * @param nodes is the list of XML nodes that includes the desired node.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the only node in <code>nodes</code> that represents an XML tag.
	 */
	public static Node getNode(NodeList nodes, String owner) {
		ArrayList<Node> nodesFound; 					//The nodes found in the given list
		
		nodesFound = getNumNodes(nodes,1,owner);
		if (nodesFound.size() == 0) {
			return null;
		}
		return nodesFound.get(0);
	}

	/**
	 * The <code>getNode</code> method extracts a node with the given name from the given XML block. If none
	 * is found, <b><code>null</code></b> is returned. If more than one is found, a warning is sent to 
	 * "stderr", and only the first one is returned.
	 *
	 * @param root is the top node in the XML block in which to search for the desired node.
	 * @param nodeName is the name of the desired node.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the desired node, or <b><code>null</code></b> if none is found in the document.
	 */
	public static Node getNode(Document root, String nodeName, String owner) {
		NodeList nodes;	//All of the parts of the query that have the given name (there should only be one)
		String warning;	//A message to let the user know there might be a problem
		
		nodes = Global.getElementsByTagNameCaseInsensitive(root,nodeName);
		if (nodes.getLength() != 1) {		//There should only be one
			if (nodes.getLength() == 0) {
				return null;
			}
			warning = 
					"WARNING in Global.getNode in " + owner + 
					": Found more than 1 <" + nodeName + "> tag in a query.";
			System.err.println(warning);
	    	unableToRespondMessage.add(warning);
		}
		return nodes.item(0);
	}
	
	/**
	 * The <code>getChildNodes</code> method returns a list with all of the useful XML nodes that are children
	 * of a given XML node.
	 * 
	 * @param parent is the XML node that is the parent of the desired child nodes.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the nodes in <code>nodes</code> that represent XML tags.
	 */
	public static ArrayList<Node> getChildNodes(Node parent, String owner) {
		String warning;							//A message to let the user know there might be a problem

		if (parent == null) {
			warning = 
					"WARNING in Global.getChildNodes in " + owner + ": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	unableToRespondMessage.add(warning);
			return new ArrayList<Node>(0);
		}
		return getNodes(parent.getChildNodes(),"a <"+parent.getNodeName()+"> in "+owner);
	}

	/**
	 * The <code>getNodes</code> method returns a list with all of the useful XML nodes in a 
	 * given list of XML nodes.
	 *
	 * @param nodes is the list of XML nodes that includes the desired nodes.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the nodes in <code>nodes</code> that represent XML tags.
	 */
	public static ArrayList<Node> getNodes(NodeList nodes, String owner) {
		return getNumNodes(nodes,null,owner);
	}
	
	/**
	 * The <code>getNumChildNodes</code> method returns a list with all of the useful XML nodes that are 
	 * children of a given XML node.
	 * 
	 * @param parent is the XML node that is the parent of the desired child nodes.
	 * @param numNodes is the number of nodes that should be in the return value. If it is 
	 * <b><code>null</code></b>, then the return value may have any length.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the nodes in <code>nodes</code> that represent XML tags.
	 */
	public static ArrayList<Node> getNumChildNodes(Node parent, Integer numNodes, String owner) {
		return getNumNodes(parent.getChildNodes(),numNodes,"a <"+parent.getNodeName()+"> in "+owner);
	}
	
	/**
	 * The <code>getNumNodes</code> method returns a list with all of the useful XML nodes in a 
	 * given list of XML nodes.
	 *
	 * @param nodes is the list of XML nodes that includes the desired nodes.
	 * @param numNodes is the number of nodes that should be in the return value. If it is 
	 * <b><code>null</code></b>, then the return value may have any length.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the nodes in <code>nodes</code> that represent XML tags.
	 */
	public static ArrayList<Node> getNumNodes(NodeList nodes, Integer numNodes, String owner) {
		Integer numNodesFound;					//The number of nodes found so far
		Integer count;							//For counting iterations of a loop
		String s;								//Specifies whether an "s" should be printed
		String warning;							//A message to let the user know there might be a problem
		ArrayList<Node> returnValue;
		
		if (numNodes != null) {
			returnValue = new ArrayList<Node>(numNodes);				//Initialize
		} else {
			returnValue = new ArrayList<Node>();						//Initialize
		}
		for (count = 0; count < nodes.getLength(); count++) {
			if (nodes.item(count).getNodeType() == Node.ELEMENT_NODE) {		//Skip the text nodes
				returnValue.add(nodes.item(count));
			}
		}
		numNodesFound = returnValue.size();
		if (numNodes != null) {
			if (numNodesFound != numNodes) {
				s = "";														//Initialize
				if (numNodesFound != 1) { s = "s"; }
				warning = 
						"WARNING in Global.getNumNodes in " + owner + 
						": Found " + numNodesFound + " node" + s + " in a block when ";
				if (numNodesFound > numNodes) {
					warning += "only ";
				}
				if (numNodes == 1) {
					warning += "one node was expected.";
				} else {
					warning += numNodes + " nodes were expected.";
				}
				System.err.println(warning);
		    	// mw unableToRespondMessage.add(warning);
			}
		}
		return returnValue;
	}
	
	/**
	 * The <code>getDefinition</code> method returns a definition that was previously stored in 
	 * <code>definitions</code>.
	 *
	 * @param id is the name of the desired definition.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the definition that is named <code>id</code>.
	 */
	public static Definition getDefinition(String id, String owner) {
		String warning;							//A message to let the user know there might be a problem

		if (hasDefinition(id)) {
			return definitions.get(id);
		}
		warning = 
				"WARNING in Global.getDefinition in " + owner + 
				": Found an undefined name, \"" + id + "\".";
		System.err.println(warning);		//I don't think this can happen because the callers test hasDefinition() first
    	unableToRespondMessage.add(warning);
		return null;
	}

	/**
	 * The <code>getSubString</code> method extracts a substring from a given string. The boundaries of the
	 * substring are specified by two regular expressions, one that matches the substring immediately
	 * preceding the desired substring, and the other that matches the substring immediately following
	 * the desired substring. If one of the regular expressions is not found, an empty string ("") is
	 * returned.
	 *
	 * @param string is the string that has the desired substring in it.
	 * @param precedingRegEx is a regular expression that matches the part of <code>line</code> immediately
	 * preceding the desired substring. If it's <b><code>null</code></b>, then the left boundary will be the
	 * beginning of <code>string</code>.
	 * @param followingRegEx is a regular expression that matches the part of <code>line</code> immediately
	 * following the desired substring. If it's <b><code>null</code></b>, then the right boundary will be the
	 * end of <code>string</code>.
	 * @return the part of <code>line</code> that is between <code>precedingRegEx</code> and 
	 * <code>followingRegEx</code>.
	 */
	public static String getSubstring(String string, String precedingRegEx, String followingRegEx) {
		Matcher match;						//A match between a regular expression and a string
		Integer begin,end;					//The indices of the boundaries of the desired substring in string
	
		if (precedingRegEx == null) {
			begin = 0;
		} else {
			match = Pattern.compile(precedingRegEx).matcher(string);
			if (match.find()) {
				begin = match.end();
			} else {
				return "";
			}
		}
		if (followingRegEx == null) {
			end = string.length();
		} else {
			match = Pattern.compile(followingRegEx).matcher(string);
			if (match.find(begin)) {
				end = match.start();
			} else {
				return "";
			}
		}
		return string.substring(begin,end);
	}
	
	/**
	 * The <code>getOneRestrictionList</code> method returns a list with exactly one restriction.
	 *
	 * @param restriction is the only restriction to put in the list.
	 * @return a <code>LinkedHashSet</code> with one element, <code>restriction</code>.
	 */
	public static LinkedHashSet<Restriction> getOneRestrictionList(Restriction restriction) {
		LinkedHashSet<Restriction> returnValue;

		returnValue = new LinkedHashSet<Restriction>(1);
		returnValue.add(restriction);
		return returnValue;
	}
}