package sparql.translator;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import sparql.translator.utilities.UnableToRespondException;
import edu.stanford.nlp.util.StringUtils;


/**
 * The <code>RunTests</code> .
 *
 * @author Ken Samuel
 * @version 1.0, Feb 26, 2015
 * @since 1.6
 */
public class RunTests {

	
	/** <code>translator</code> is the object that provides access to the rest of the program. */
	private static XMLToSPARQLTranslator translator;

	
	/**
	 * The <code>go</code> method runs the selected tests.
	 *
	 * @throws Exception if there are any problems.
	 */
	public static void go() throws Exception {
		//translator = new XMLToSPARQLTranslator("C:\\0rg\\work\\projects\\MSEE\\data\\XML_to_SPARQL_data.obj");
		translator = new XMLToSPARQLTranslator();

		//someTests();
		//trainingQueriesTests2014();
		//comprehensiveTests2014();
		//trainingQueriesTests2015();
		//phase2Tests();
		newTests2015();
		//phase3Tests();
		//phase3lastTrainingQueries();
		System.out.println("All done.");
	}

	/**
	 * The <code>trainingQueriesTests2014</code> method runs the translator on the training queries that we 
	 * received in 2014.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void trainingQueriesTests2014() throws Exception {
		Integer socNum,storylineNum,queryNum;	//For looping through the queries
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		String[] socNames = {
				"soc-sig-office-2013-09-04-training",
				"soc-sig-parking-lot-2013-09-28-training",
				"soc-pratt-garden-2013-10-12-training",
		};
		String[][] storylineNames = {
				{
					"storyline-01-setting-up",
					"storyline-02-recovered-1",
					"storyline-03-recovered-2",
					"storyline-04-meeting-breakroom-1",
					"storyline-05-meeting-confroom-1",
					"storyline-06-bagswitch-1",
					"storyline-07-package-handoff-1",
					"storyline-08-enterexit",
					"storyline-09-aesthetics",
					"storyline-10-setting-up-SFQ",
					"storyline-11-recovered-1-SFQ",
					"storyline-12-recovered-2-SFQ",
					"storyline-13-meeting-breakroom-1-SFQ",
				},
				{
					"storyline-car-parts-SFQ",
					"storyline-groundcrew",
					"storyline-main-entrance",
					"storyline-main-entrance-SFQ",
				},
				{
					"storyline-A-Typical-Day-in-the-Garden",
					"storyline-A-Typical-Day-in-the-Garden-SFQ",
					"storyline-Disc-Golf",
					"storyline-Football-Players",
					"storyline-Interloper-1",
					"storyline-still-image",
				},
		};
		String[][][] queryNames = {
				{
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-17",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-17",
						"query-18",
						"query-19",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-06",
						"query-08",
						"query-09",
						"query-10",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-10",
						"query-11",
						"query-12",
					},
				},
				{
					{
						"query-1",
						"query-2",
						"query-3",
						"query-4",
						"query-5",
						"query-6",
						"query-7",
						"query-8",
						"query-9",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-04a",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-08a",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-16a",
						"query-17",
						"query-18",
						"query-19",
						"query-19a",
						"query-20",
						"query-21",
						"query-22",
						"query-23",
						"query-24",
						"query-25",
						"query-26",
						"query-27a",
						"query-27b",
						"query-27",
						"query-28",
						"query-29",
						"query-29a",
						"query-29b",
						"query-29c",
						"query-30",
						"query-31",
						"query-32",
						"query-33",			
						"query-34",
						"query-35",		
					},					
					{
						"query-00",
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-04a",
						"query-04b",
						"query-04c",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-17",
						"query-18",
						"query-19",
						"query-20",
						"query-21",
						"query-22",
						"query-23",
						"query-24",
						"query-25",
						"query-26",
						"query-27",
						"query-28",
						"query-29",
						"query-30",
						"query-31",
						"query-32",
						"query-33",			
						"query-34",
					},
					{
						"query-00",
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-17",
						"query-18",
						"query-19",
						"query-20",
						"query-21",
						"query-22",
						"query-23",
						"query-24",
						"query-25",
					}
				},
				{
					{
						"query-1",
						"query-2",
						"query-3",
						"query-4",
						"query-5",
						"query-6",
						"query-7a",
						"query-7b",
						"query-7c",
						"query-8",
						"query-9",						
						"query-10",					
						"query-10a",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16a",
						"query-16b",
						"query-17",
						"query-18",
						"query-19",						
						"query-20",
						"query-21",
						"query-22",
					},
					{
						"query-1",
						"query-2",
						"query-3",
						"query-4",
						"query-5",
						"query-6",
						"query-7a",
						"query-7b",
						"query-8",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
					},
					{
						"query-1",
						"query-2",
						"query-3",
						"query-4",
						"query-5",
						"query-6",
					},
					{
						"query-1",
						"query-2",
						"query-3",
						"query-4",
						"query-5",
						"query-6",
						"query-7",
						"query-8",
						"query-9",						
					},
					{
						"query-1a",
						"query-1b",
						"query-1c",
						"query-2",
						"query-3",
						"query-4a",
						"query-4b",
						"query-5",
						"query-6a",
						"query-6b",
						"query-7a",
						"query-7b",
						"query-7c",
						"query-8a",
						"query-8b",
						"query-8c",
						"query-9a",						
						"query-9b",						
						"query-10",					
						"query-11",
						"query-12",
					},
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-17",
						"query-18",
					},					
				},
		};
		
		socNum = null;					//Initialize
		storylineNum = null;			//Initialize
		queryNum = null;				//Initialize
		for (socNum = 0; socNum < socNames.length; socNum++) {
			for (storylineNum = 0; storylineNum < storylineNames[socNum].length; storylineNum++) {
				translator.translateQuery(null);			//Initialize
				for (queryNum = 0; queryNum < queryNames[socNum][storylineNum].length; queryNum++) {
					xMLFilename = 
							"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\" +
							"msee_package_1_2_20140205\\XMLData\\socs\\" + 
							socNames[socNum] + "\\storylines\\" + storylineNames[socNum][storylineNum] +
							"\\queries\\" + queryNames[socNum][storylineNum][queryNum] + "\\query.xml";
					title = 
							"data\\SIG_DATA2\\msee_package_1_2_20140205\\XMLData\\socs\\" + 
							socNames[socNum] + "\\storylines\\" + 
							storylineNames[socNum][storylineNum] + "\\queries\\" +
							queryNames[socNum][storylineNum][queryNum]; 
					if (queryNames[socNum][storylineNum][queryNum].equals(
							"query-")) {
						@SuppressWarnings("unused")
						boolean x = true;				//DBUG
					}
					printTranslation(xMLFilename,title,true,false);
				}
			}
		}
	}

	/**
	 * The <code>comprehensiveTests2014</code> method runs the translator on the training queries that we 
	 * received in 2014 and a set of queries that Ken Samuel created to test all kinds of queries that are 
	 * possible according to the "MSEE Formal Language Specification (FLS), version 1.7" and the "MSEE EES-SUT
	 * Interface Control Document (ICD), version 1.2".
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void comprehensiveTests2014() throws Exception {
		Integer socNum,storylineNum,queryNum;	//For looping through the queries
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		String[] socNames = {
				"soc-tests-for-XMLToSPARQLTranslator",
		};
		String[][] storylineNames = {
				{
					"storyline-tests-for-XMLToSPARQLTranslator",
					"storyline-examples-for-report",
					"storyline-event-tests",
				},
		};
		String[][][] queryNames = {
				{
					{
						"query-01",
						"query-02",
						"query-03",
						"query-04",
						"query-05",
						"query-06",
						"query-07",
						"query-08",
						"query-09",
						"query-10",
						"query-11",
						"query-12",
						"query-13",
						"query-14",
						"query-15",
						"query-16",
						"query-17",
						"query-18",
						"query-19",
						"query-20",
						"query-21",
						"query-22",
						"query-23",
						"query-24",
						"query-25",
						"query-26",
						"query-27",
						"query-28",
						"query-29",
						"query-30",
						"query-31",
						"query-32",
						"query-33",
						"query-34",
						"query-35",
						"query-36",
						"query-37",
						"query-38",
						"query-39",
						"query-40",
						"query-41",
						"query-42",
						"query-43",
						"query-44",
					},
					{
						"query-sets-2",
						"query-events",
					},
					{
						"query-",				//The set and event definitions
						"query-false-x",
						"query-x-false",
						"query-true-x-(precedesIIstrictly-before)",
						"query-X-true-(precedesIIafter)",
						"query-true-true-(equalsIsame-time-as)",
						"query-true-animal-(beforeIIsame-time-as)",
						"query-animal-true-(equalsIIsame-time-as)",
						"query-animal-eq",
						"query-eq-animal",
						"query-animal-gte",
						"query-gte-animal",
						"query-animal-lte",
						"query-lte-animal",
						"query-animal-gt",
						"query-gt-animal",
						"query-animal-lt",
						"query-lt-animal",
						"query-and",
						"query-or",
						"query-not",
						"query-and-or-not",
						"query-false-true-and-or-not-x-x",
						"query-chair-and",
						"query-and-chair",
						"query-and-and",
						"query-chair-or",
						"query-or-chair",
						"query-or-or",
						"query-and-or",
						"query-or-and",
						"query-andand-oror",
						"query-andor-orand",
						"query-n(ltIlte)-animal",			
						"query-animal-n(ltIlte)",
						"query-n(gtIgte)-animal",
						"query-animal-n(gtIgte)",
						"query-animal-neq-(precedesIIafter)",
						"query-animal-neq-overlaps",
						"query-animal-neq-(meetsIfinished-by)",
						"query-animal-neq-starts",
						"query-animal-neq-equals",
						"query-animal-neq-same-time-as",
						"query-neq-animal-(precedesIIafter)",
						"query-neq-animal-(overlapsIfinished-by)",
						"query-neq-animal-(meetsIcontains)",
						"query-neq-animal-(startsIequals)",
						"query-neq-animal-same-time-as",
						"query-neq-neq-(precedesIstrictly-before)",
						"query-neq-neq-meets",
						"query-neq-neq-overlaps",
						"query-neq-neq-finished-by",
						"query-neq-neq-contains",
						"query-neq-neq-starts",
						"query-neq-neq-equals",
						"query-neq-neq-same-time-as",
						"query-neq-neq-before",
						"query-neq-neq-after",
						"query-andornot-animal",
						"query-animal-andornot",
						"query-false-true-and-or-not-andornot",
						"query-animal-union",
						"query-animal-intersection",
						"query-intersection-animal",
						"query-union-intersection-intersection-union",
						"query-union-union-intersection-intersection",
						"query-union-union-union-intersection-intersection-intersection",
						"query-everything",
						"query-exists",
					}
				}
		};
		
		socNum = null;					//Initialize
		storylineNum = null;			//Initialize
		queryNum = null;				//Initialize
		for (socNum = 0; socNum < socNames.length; socNum++) {
			for (storylineNum = 0; storylineNum < storylineNames[socNum].length; storylineNum++) {
			//DBUG			for (storylineNum = 2; storylineNum <= 2; storylineNum++) {
				translator.translateQuery(null);			//Initialize
				for (queryNum = 0; queryNum < queryNames[socNum][storylineNum].length; queryNum++) {
					xMLFilename = 
							"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\" +
							"msee_package_1_2_20140205\\XMLData\\socs\\" + 
							socNames[socNum] + "\\storylines\\" + storylineNames[socNum][storylineNum] +
							"\\queries\\" + queryNames[socNum][storylineNum][queryNum] + "\\query.xml";
					title = 
							"data\\SIG_DATA2\\msee_package_1_2_20140205\\XMLData\\socs\\" + socNames[socNum] + 
							"\\storylines\\" + storylineNames[socNum][storylineNum] + 
							"\\queries\\" + queryNames[socNum][storylineNum][queryNum]; 
					if (queryNames[socNum][storylineNum][queryNum].equals(
							"query-")) {
						@SuppressWarnings("unused")
						boolean x = true;				//DBUG
					}
					printTranslation(xMLFilename,title,true,false);
				}
			}
		}
	}

	/**
	 * The <code>trainingQueriesTests2015</code> method runs the translator on the training queries that we 
	 * received in 2015.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void trainingQueriesTests2015() throws Exception {
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		Integer queryNum;						//For looping through the queries
	
		translator.translateQuery(null);			//Initialize
		for (queryNum = 1; queryNum <= 22; queryNum++) {
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\" +
				"msee_phase3_training_20150211\\SIGParkingLot-2014-10-18-Training\\SensorData\\SDT\\" +
				queryNum + ".xml";
			title = "data\\SIG_DATA2\\msee_phase3_training_20150211\\SIGParkingLot-2014-10-18-Training\\SensorData\\SDT\\query-" + queryNum;
			printTranslation(xMLFilename,title,true,false);
		}
	}

	/**
	 * The <code>trainingQueriesTests2014</code> method runs the translator on the queries used for the test
	 * in Phase II.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void phase2Tests() throws Exception {
		Integer socNum,storylineNum,queryNum;	//For looping through the queries
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		String[] socNames = {
				"SIGOffice-2013-09-04-Testing",
				"SIGParkingLot-2013-09-28-Testing",
				"PrattGarden-2013-10-12-Testing",
		};
		String[][] storylineNames = {
				{
					"storyline-001",
					"storyline-01",
					"storyline-02",
					"storyline-03.colors",
					"storyline-04.objects",
					"storyline-05.objects",
					"storyline-06.objects",
					//"storyline-07.objects",
					//"storyline-08.objects",
					//"storyline-09.objects",
					//"storyline-10.objects",
					//"storyline-10.objects_&_relationships",
					//"storyline-11.behavior_&_temporal",
					//"storyline-12.color_relation_action_object",
					//"storyline-13.color_relation_action_object",
					//"storyline-14.behavior_color_relation_action_object",
					//"storyline-15.behavior_color_relation",
					//"storyline-16.counting",
					//"storyline-17",
					//"storyline-18",
					//"storyline-19",
					//"storyline-20",
					//"storyline-21",
					//"storyline-42",
					//"storyline-43",
					//"storyline-44",
					//"storyline-45",
					//"storyline-46",
					//"storyline-47",
					//"storyline-60_online",
					//"storyline-61_time",
				},
				{
					"storyline-00",
					//"storyline-01",
					//"storyline-02",
					//"storyline-03",
					//"storyline-04",
					//"storyline-05",
					//"storyline-06",
					//"storyline-07",
					//"storyline-08",
				},
				{
					"storyline-01",
					"storyline-02",
					"storyline-03",
					"storyline-04",
					"storyline-05",
					"storyline-06",
					"storyline-07",
				},
		};
		String[][][] queryNames = {
				{
					{
						"query-01_chair",
						"query-01_pizza_chair_orig",
						"query-01_pizza_chair_simplied",
						"query-02_99_sitdown_false",
						"query-02_pizza_chair_orig",
						"query-03_99_feet_on_table",
						"query-03_pizza_chair_simplied",
						"query-04_00_hat",
						"query-04_01_hat_only",
						"query-04_02_hat_wearonly",
						"query-04_03_hat_wearNoLoc_hatNoLoc",
						"query-04_04_hat_wearNoLoc",
						"query-04_05_hat_noTL",
						"query-04_99_hat",
					},
					{
						"query-01.person_t",
						"query-02.person_f",
						"query-03.torso_t",
						"query-04.torso_f",
						"query-05.stand_t",
						"query-06.sit_f",
						"query-07.crawl_f",
						"query-08.eat_f",
					},
					{
						"query-01.sit_t",
						"query-02.sit_on_t",
						"query-03.point_f",
						"query-04.read_f",
						"query-05.run_f",
						"query-06.stand_f",
						"query-07.talk_f",
						"query-08.write_f",
					},
					{
						"query-01.azure",
						"query-02.black",
						"query-03.brown",
						"query-04.gray",
						"query-05.green",
						"query-06.orange",
						"query-07.pink",
						"query-08.purple",
						"query-09.red_t",
						"query-10.white",
						"query-11.yellow",
					},
					{
						"query-01.animal_f",
						"query-02.automobile_f",
						"query-03.ball_f",
						"query-04.bottom-wear_t",
						"query-05.building_f",
						"query-06.chair_t",
						"query-07.clothing_t",
						"query-08.disc",
						"query-09.female_f",
						"query-10.food_f",
						"query-11.footwear_t",
						"query-12.grass_f",
					},
					{
						"query-01.ground_t",
						"query-02.hat_f",
						"query-03.luggage_f",
						"query-04.male_t",
						"query-05.natural_f",
						"query-06.object_t",
						"query-07.other_t",
						"query-08.package_f",
						"query-09.paved_f",
						"query-10.plant_f",
					},
					{
						"query-01.road_f",
						"query-02.room_t",
						"query-03.sidewalk_f",
						"query-04.sky_f",
						"query-05.small-object_t",
						"query-06.table_t",
						"query-07.tool_f",
						"query-08.top-wear_t",
						"query-09.trashcan_t",
						"query-10.two-wheeled-vehicle",
					},
					//There are 24 more storylines
				},
				{
					{
						"query-02.vehicle",
						"query-02.vehicle_NoL",
						"query-02.vehicle_NoT",
						"query-02.vehicle_NoTL",
					},
					//There are 8 more storylines
				},
				{
					{
						"query-01.reading_t",
						"query-02.running_t",
						"query-03.two-wheeled-vehicle_turning-left_t",
						"query-04.two-wheeled-vehicle_turning-right_t",
						"query-05.two-wheeled-vehicle_u-turn_t",
						"query-06.writing_t",
						"query-07.person_following_t",
						"query-08.person_facing_black_t",
						"query-09.pillar_t",
					},
					{
						"query-01.person_animal_together_t",
						"query-02.person_carrying_object_t",
						"query-03.person_donning_hat_t",
						"query-04.person_mounting_two-wheeled-vehicle_t",
						"query-05.person_picking-up_disc_t",
						"query-06.person_putting-down_object_t",
						"query-07.person_swinging_arm_t",
						"query-08.person_throwing_disc_t",
					},
					{
						"query-01.person_wearing_hat_t",
						"query-02.animal_t",
						"query-03.disc_t",
						"query-04.grass_t",
						"query-05.hat_t",
						"query-06.two-wheeled-vehicle_t",
						"query-07.glasses_t",
						"query-08.hood_t",
						"query-09.wheel_t",
					},
					{
						"query-01.brown_animal_running_temporal_meets_brown_animal_walking_t",
						"query-02.person_sitting_temporal_overlaps_two_wheeled_vehicle_moving_t",
						"query-03.person_purple_shirt_t",
						"query-04.person_orange_shirt_t",
						"query-05.unpaved_t",
						"query-06.tool_t",
						"query-07.sidewalk_t",
						"query-08.person_opposite_motion_person_t",
						"query-09.person_facing_opposite_person_t",
						"query-10.person_outside_building_t",
						"query-11.person_inside_building_t",
					},
					{
						"query-01.person_wearing_hat_f",
						"query-02.running_f",
						"query-03.two-wheeled-vehicle_turning-left_f",
						"query-04.two-wheeled-vehicle_turning-right_f",
						"query-05.two-wheeled-vehicle_u-turn_f",
						"query-07.person_following_f",
						"query-08.person_facing_black_f",
					},
					{
						"query-01.person_animal_together_f",
						"query-02.person_carrying_object_f",
						"query-03.person_donning_hat_f",
						"query-04.person_mounting_two-wheeled-vehicle_f",
						"query-05.person_picking-up_disc_f",
						"query-06.person_putting-down_object_f",
						"query-07.person_swinging_arm_f",
						"query-08.person_throwing_disc_f",
					},
					{
						"query-01.brown_animal_running_temporal_meets_brown_animal_walking_f",
						"query-02.person_sitting_temporal_overlaps_two_wheeled_vehicle_moving_f",
						"query-03.person_purple_shirt_f",
						"query-04.person_orange_shirt_f",
						"query-05.crawling_f",
						"query-06.person_putting-down_object_f",
						"query-07.luggage_f",
						"query-08.person_opposite_motion_person_f",
						"query-09.person_facing_opposite_person_f",
						"query-10.person_outside_building_f",
						"query-11.person_inside_building_f",
					},
				},
		};
		
		socNum = null;					//Initialize
		storylineNum = null;			//Initialize
		queryNum = null;				//Initialize
		for (socNum = 0; socNum < socNames.length; socNum++) {
			for (storylineNum = 0; storylineNum < storylineNames[socNum].length; storylineNum++) {
				translator.translateQuery(null);			//Initialize
				for (queryNum = 0; queryNum < queryNames[socNum][storylineNum].length; queryNum++) {
					xMLFilename = 
							"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\" +
							"Phase2DistributableTestingData\\" + 
							socNames[socNum] + "\\storylines\\" + storylineNames[socNum][storylineNum] +
							"\\queries\\" + queryNames[socNum][storylineNum][queryNum] + "\\query.xml";
					title = 
							"data\\SIG_DATA2\\Phase2DistributableTestingData\\" + 
							socNames[socNum] + "\\storylines\\" + 
							storylineNames[socNum][storylineNum] + "\\queries\\" +
							queryNames[socNum][storylineNum][queryNum]; 
					if (queryNames[socNum][storylineNum][queryNum].equals(
							"query-")) {
						@SuppressWarnings("unused")
						boolean x = true;				//DBUG
					}
					printTranslation(xMLFilename,title,true,false);
				}
			}
		}
	}

	/**
	 * The <code>newTests2015</code> method runs the translator on queries that Ken Samuel created to test 
	 * the new features in the "MSEE Formal Language Specification (FLS), version 2.1" and the "MSEE EES-SUT 
	 * Interface Control Document (ICD), version 2.2". These features are object specifications, nonpolar 
	 * queries, scene descriptive texts, and single frame queries.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void newTests2015() throws Exception {
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		Integer queryNum;						//For looping through the queries
		String queryNumString;					//queryNum as a String object
		String n = "\n";
		String[][] resultTest = {
				{"false\n"},																			//000
				{"#person-0\n","true\n"},																//001
				{"true\n"},																				//002
				{n},																					//003
				{"#person-3\n","#person-4\n",n,n},														//004
				//DBUG{"#person-3\n",n,n,n},															//004
				//DBUG{"#person-3\n#person-0\n#person-4\n","#person-3\n#person-0\n#person-4\n",n,n},	//004
				{"true\n"},																				//005
				{"false\n"},																			//006
				{"#person-1\n#person-0\n"},//{n},														//007
				{"true\n"},																				//008
				{"#person-2\n","Human\nNamedIndividual\n"},												//009
				{"#person-1\n#person-0\n#person-2\n","MaleHuman\nNamedIndividual\n",					//010
					"#Human\n#NamedIndividual\n"},//{},									
				{"false\n"},//{"true\n"},																//011
				{"false\n"},																			//012
				{"false\n"},																			//013
				{"#person-3\n#person-2\n#person-4\n","FemaleHuman\nNamedIndividual\n",					//014
					"Human\nNamedIndividual\n"},
				{																						//015
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person0-sitting-1\">\n" + 
					"		<StartTime>2013-09-04T14:56:19.030Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:57:07.244Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{																						//016
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-people-sitting-1\">\n" + 
					"		<StartTime>2013-09-04T14:56:19.030Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:44.388Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-people-sitting-2\">\n" + 
					"		<StartTime>2013-09-04T14:56:53.063Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:57:07.244Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{"true\n"},																				//017
				{"true\n"},																				//018
				{"#person-3\n#person-4\n"},																//019
				{"#person-1\n#person-2\n","true\n"},													//020
				{"false\n"},																			//021
				{																						//022
					"<WhereAnswer>\n" + 
					"	<ViewCentricPolygon id=\"loc-sut-1\">\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>301</x>\n" + 
					"			<y>114</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>704</x>\n" + 
					"			<y>114</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>704</x>\n" + 
					"			<y>597</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>301</x>\n" + 
					"			<y>597</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<ViewId>view-BR3-video</ViewId>\n" + 
					"	</ViewCentricPolygon>\n" + 
					"</WhereAnswer>\n",
				"true\n"},
				{																						//023
					"<WhereAnswer>\n" + 
					"	<ViewCentricPolygon id=\"loc-sut-2\">\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>605</x>\n" + 
					"			<y>301</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>821</x>\n" + 
					"			<y>301</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>821</x>\n" + 
					"			<y>584</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>605</x>\n" + 
					"			<y>584</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<ViewId>view-BR3-video</ViewId>\n" + 
					"	</ViewCentricPolygon>\n" + 
					"</WhereAnswer>\n",
				"true\n"},
				{																						//024
					"<WhereAnswer>\n" + 
					"	<ViewCentricPolygon id=\"loc-sut-3\">\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>597</x>\n" + 
					"			<y>293</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>813</x>\n" + 
					"			<y>293</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>813</x>\n" + 
					"			<y>576</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<CartesianPixelPoint>\n" + 
					"			<x>597</x>\n" + 
					"			<y>576</y>\n" + 
					"		</CartesianPixelPoint>\n" + 
					"		<ViewId>view-BR3-video</ViewId>\n" + 
					"	</ViewCentricPolygon>\n" +
					"</WhereAnswer>\n",
				"true\n"},
				{																						//025
					"<WhereAnswer>\n" + 
					"	<CartesianMetricPolygon id=\"loc-sut-4\">\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>621</x>\n" + 
					"			<y>301</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>837</x>\n" + 
					"			<y>301</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>837</x>\n" + 
					"			<y>584</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>621</x>\n" + 
					"			<y>584</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"	</CartesianMetricPolygon>\n" + 
					"</WhereAnswer>\n", 
				"true\n"},
				{																						//026
					"<WhereAnswer>\n" + 
					"	<CartesianMetricPolygon id=\"loc-sut-5\">\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>621</x>\n" + 
					"			<y>301</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>837</x>\n" + 
					"			<y>301</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>837</x>\n" + 
					"			<y>584</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"		<CartesianMetricPoint>\n" + 
					"			<x>621</x>\n" + 
					"			<y>584</y>\n" + 
					"		</CartesianMetricPoint>\n" + 
					"	</CartesianMetricPolygon>\n" + 
					"</WhereAnswer>\n", 
				"true\n"},
				{"true\n"},
				{"true\n"},
				{"false\n"},
				{""},
				{
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-sitting-or-standing-1\">\n" + 
					"		<StartTime>2013-09-04T14:56:20.365Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:26.320Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-sitting-or-standing-2\">\n" + 
					"		<StartTime>2013-09-04T14:56:28.873Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:47.224Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-sitting-or-standing-3\">\n" + 
					"		<StartTime>2013-09-04T14:56:49.643Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:51.745Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-sitting-or-standing-4\">\n" + 
					"		<StartTime>2013-09-04T14:56:56.567Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:57:07.244Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{
					"<WhenAnswer>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person0-not-sitting-1\">\n" + 
					"		<StartTime>2013-09-04T14:56:17.212Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:19.013Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person0-not-sitting-1a\">\n" + 
					"		<StartTime>2013-09-04T14:56:17.212Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:19.030Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person0-not-sitting-2a\">\n" + 
					"		<StartTime>2013-09-04T14:56:44.388Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:53.063Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person0-sitting-1a\">\n" + 
					"		<StartTime>2013-09-04T14:56:19.030Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:57:07.244Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"},
				{
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-or-two-sitting-1\">\n" + 
					"		<StartTime>2013-09-04T14:56:17.212Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:19.030Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-or-two-sitting-2\">\n" + 
					"		<StartTime>2013-09-04T14:56:44.388Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:53.063Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person3-or-two-sitting-3\">\n" + 
					"		<StartTime>2013-09-04T14:56:56.567Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:57:07.244Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"}, 
				{
					"<WhenAnswer>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-1\">\n" + 
					"		<StartTime>2013-09-04T14:56:19.697Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:20.031Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-2\">\n" + 
					"		<StartTime>2013-09-04T14:56:37.548Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:39.533Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-3\">\n" + 
					"		<StartTime>2013-09-04T14:56:41.719Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:41.719Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-4\">\n" + 
					"		<StartTime>2013-09-04T14:56:51.245Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:52.947Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-5\">\n" + 
					"		<StartTime>2013-09-04T14:56:53.164Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:53.848Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-6\">\n" + 
					"		<StartTime>2013-09-04T14:56:55.065Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:56:55.533Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"	<SceneCentricTimePeriod id=\"time-sut-person4-7\">\n" + 
					"		<StartTime>2013-09-04T14:56:58.569Z</StartTime>\n" + 
					"		<EndTime>2013-09-04T14:57:07.244Z</EndTime>\n" + 
					"	</SceneCentricTimePeriod>\n" + 
					"</WhenAnswer>\n",
				"true\n"}, 
				{"true\n"},
				{"#person-1\n","#person-0\n"},
		};
		String[][] resultWarningMessages = {
				{"#person-0\n",""},
				{"#person-1,\n#person-2,\n","NamedIndividual\n","Human\nNamedIndividual\n"},
				{"","<BadAnswer></BadAnswer>\n",null,"true\n"},
				{""},
				{"","<BadAnswer></BadAnswer>\n","true\n"},
		};
		
		translator.translateQuery(null);			//Initialize
		for (queryNum = 0; queryNum < resultTest.length; queryNum++) {
			queryNumString = queryNum.toString();
			while (queryNumString.length() < 3) {
				queryNumString = "0" + queryNumString;
			}
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\test_queries\\socs\\soc-test\\" +
				"storylines\\storyline-test\\queries\\query-" + queryNumString + "\\query.xml";
			title = 
					"data\\test_queries\\socs\\soc-test\\storylines\\storyline-test\\queries\\query-" +
					queryNumString;
			printTranslation(xMLFilename,title,true,false,resultTest[queryNum]);
		}
	
		translator.translateQuery(null);			//Initialize
		for (queryNum = 0; queryNum < resultWarningMessages.length; queryNum++) {
			queryNumString = queryNum.toString();
			while (queryNumString.length() < 3) {
				queryNumString = "0" + queryNumString;
			}
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\test_queries\\socs\\soc-test\\" +
				"storylines\\storyline-warning-messages\\queries\\query-" + queryNumString + "\\query.xml";
			title = 
					"data\\test_queries\\socs\\soc-test\\storylines\\storyline-warning-messages\\queries\\query-" +
					queryNumString;
			printTranslation(xMLFilename,title,true,false,resultWarningMessages[queryNum]);
		}
	}

	/**
	 * The <code>phase3QueryTests</code> method runs the translator on the queries for the Phase III test.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void phase3Tests() throws Exception {
		String xMLFilename;					//The name of a file in XML format
		String sPARQLFilename;				//The name of a file to which to save the result in SPARQL format
		String[] inputFolders = {			//The folders with the input files in XML format
				//"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase3DistributableTestingData\\PrattGarden-2014-09-20-Testing\\PrattGarden-2014-09-20-Testing\\SensorData\\SDT\\",
				//"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase3DistributableTestingData\\Schiciano-2014-02-22-Testing\\Schiciano-2014-02-22-Testing\\SensorData\\SDT\\",
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-01-04-Testing\\SIGParkingLot-2014-01-04-Testing\\SensorData\\SDT\\",
				//"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-10-18-Testing\\SIGParkingLot-2014-10-18-Testing\\SensorData\\SDT\\",
		};
		Integer[] numFiles = {				//The number of files in each of those folders
				//34,
				//30,
				34,
				//30,
		};
		Integer index1, index2;				//For looping through arrays
	
		for (index1 = 0; index1 < inputFolders.length; index1++) {
			translator.translateQuery(null);			//Initialize
			for (index2 = 1; index2 <= numFiles[index1]; index2++) {
				xMLFilename = inputFolders[index1] + index2 + ".xml";
				sPARQLFilename = inputFolders[index1] + "SPARQL\\" + index2 + ".rq";
				saveTranslation(xMLFilename,sPARQLFilename);
			}
		}
	}

	/**
	 * The <code>phase3lastTrainingQueries</code> method runs the translator on queries that were created in 
	 * the final week of the Phase III test deadline.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void phase3lastTrainingQueries() throws Exception {
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		Integer queryNum;						//For looping through the queries
		String[][] parkingLotJanuary = {
				{},				//For the nonexistent query-0
				{"#person-0\n","true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"#vehicle-0\n#vehicle-1\n#vehicle-2\n"},
				{"true\n"},
				{"true\n"},
				{"\n"},
				{"true\n"},
				{"true\n"},
				{"#person-2\n","true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
		};
		String[][] parkingLotOctober = {
				{},				//For the nonexistent query-0
				{"true\n"},
				{"true\n"},
				{"#vehicle-0\n#vehicle-1\n#vehicle-2\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"\n"},
				{"true\n"},
				{"\n"},
				{"true\n"},
				{"true\n"},
				{"#trunk-1\n","true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"#person-0\n","true\n"},
				{"\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
				{"true\n"},
		};
		
		translator.translateQuery(null);			//Initialize
		for (queryNum = 1; queryNum < parkingLotJanuary.length; queryNum++) {
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-01-04-Testing\\test-IAI\\storylines\\storyline-01\\queries\\query-" + queryNum + "\\query.xml";
			title = 
					"data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-01-04-Testing\\test-IAI\\storylines\\storyline-01\\queries\\query-" + queryNum + "\\query.xml";
			printTranslation(xMLFilename,title,true,false,parkingLotJanuary[queryNum]);
		}
		translator.translateQuery(null);			//Initialize
		for (queryNum = 1; queryNum < parkingLotOctober.length; queryNum++) {
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\" +
				"Phase3DistributableTestingData\\SIGParkingLot-2014-10-18-Testing\\test_IAI\\storyline-02\\" +
				"queries\\query-" + queryNum + "\\query.xml";
			title = 
					"data\\SIG_DATA2\\Phase3DistributableTestingData\\SIGParkingLot-2014-10-18-Testing\\" +
					"test_IAI\\storyline-02\\queries\\query-" + queryNum;
			printTranslation(xMLFilename,title,true,false,parkingLotOctober[queryNum]);
		}
	}

	/**
	 * The <code>saveTranslation</code> method saves the translation of a query to a file.
	 *
	 * @param xMLFilename is the name of the file with the XML version of the query (the input).
	 * @param sPARQLFilename is the name of the file with the SPARQL version of the query (the output).
	 * @throws Exception if there are any problems.
	 */
	public static void saveTranslation(String xMLFilename, String sPARQLFilename) throws Exception {
		String xMLQuery;						//A query in its original XML form
		String sPARQLQuery;						//The same query after it has been translated to SPARQL
		PrintStream stream;						//To save the result to the output file
		
		System.out.print("Loading \"" + xMLFilename + "\"... ");
		xMLQuery = XMLToSPARQLTranslator.loadXMLQuery(xMLFilename);
		System.out.println("Done.");
		
		try {
			translator.translateQuery(xMLQuery);
		} catch (UnableToRespondException exception) {
			System.err.println("WARNING in RunTests.saveTranslation: " + exception.getMessage());
			return;
		}
		sPARQLQuery = translator.toString();
		
		System.out.print("Saving \"" + sPARQLFilename + "\"... ");
		stream = new PrintStream(new File(sPARQLFilename));
		stream.print(sPARQLQuery);
		stream.close();
		System.out.println("Done.");
	}

	/**
	 * The <code>printTranslation</code> method outputs the translation of a query to standard output in a
	 * pretty format.
	 *
	 * @param xMLFilename is the name of the file with the XML version of the query.
	 * @param title is the name of the query.
	 * @param printBooleanQuery specifies whether to output the true/false SPARQL query.
	 * @param printNonBooleanQueries specifies whether to output the SPARQL queries that output the values of 
	 * the variables.
	 * @throws Exception if there are any problems.
	 */
	public static void printTranslation(
			String xMLFilename, String title, Boolean printBooleanQuery, Boolean printNonBooleanQueries) 
			throws Exception {
		String xMLQuery;						//A query in its original XML form
		ArrayList<String> xMLQueries;			//The non-boolean XML queries
		String line;							//A horizontal line to separate queries
		
		line = StringUtils.repeat("=",75);
		xMLQuery = XMLToSPARQLTranslator.loadXMLQuery(xMLFilename);
		System.out.println(title);
		System.out.println(StringUtils.repeat("-", title.length()));
		try {
			translator.translateQuery(xMLQuery);
		} catch (UnableToRespondException exception) {
			System.err.println("WARNING in RunTests.printTranslation: " + exception.getMessage());
			return;
		}
		if (printBooleanQuery) {
			System.out.print(translator.toString());
			System.out.println();
			System.out.println(line);
			System.out.println();
		}
		if (printNonBooleanQueries) {
			xMLQueries = translator.setDefinitionsToStrings();
			for (String anXMLQuery : xMLQueries) {
				System.out.println(anXMLQuery);
				System.out.println();
				System.out.println(line);
				System.out.println();
			}
		}		
	}

	/**
	 * The <code>printTranslation</code> method outputs the translation of a query to standard output in a
	 * pretty format.
	 *
	 * @param xMLFilename is the name of the file with the XML version of the query.
	 * @param title is the name of the query.
	 * @param printBooleanQuery specifies whether to output the true/false SPARQL query.
	 * @param printNonBooleanQueries specifies whether to output the SPARQL queries that output the values of 
	 * the variables.
	 * @param results is a list of results that should be returned from the database when given the last
	 * query. This information should be sent to <code>reportResults()</code>.
	 * @throws Exception if there are any problems.
	 */
	public static void printTranslation(
			String xMLFilename, String title, Boolean printBooleanQuery, Boolean printNonBooleanQueries,
			String[] results) 
			throws Exception {
		String xMLQuery;						//A query in its original XML form
		ArrayList<String> xMLQueries;			//The non-boolean XML queries
		String line;							//A horizontal line to separate queries
		String returnedValue;					//The return value of reportResult()
		
		line = StringUtils.repeat("=",75);
		xMLQuery = XMLToSPARQLTranslator.loadXMLQuery(xMLFilename);
		System.out.println(title);
		System.out.println(StringUtils.repeat("-", title.length()));
		try {
			translator.translateQuery(xMLQuery);
		} catch (UnableToRespondException exception) {
			System.err.println("WARNING in RunTests.printTranslation: " + exception.getMessage());
			return;
		}
		if (printBooleanQuery) {
			System.out.print(translator.toString());
			System.out.println();
			System.out.println(line);
			System.out.println();
		}
		if (printNonBooleanQueries) {
			xMLQueries = translator.setDefinitionsToStrings();
			for (String anXMLQuery : xMLQueries) {
				System.out.println(anXMLQuery);
				System.out.println();
				System.out.println(line);
				System.out.println();
			}
		}
		for (String result : results) {
			try {
				returnedValue = translator.reportResults(result);
				if (returnedValue == null) {
					System.out.println("[null]");
				} else {
					System.out.println(returnedValue);
				}
				System.out.println();
				System.out.println(line);
				System.out.println();
			} catch (UnableToRespondException exception) {
				System.err.println("WARNING in RunTests.printTranslation: " + exception.getMessage());
			}
		}
	}
	
	/**
	 * The <code>twoTests</code> method runs the translator on the two old test queries because their
	 * answers have changed.
	 * 
	 * @throws Exception if there are any problems.
	 */
	public static void someTests() throws Exception {
		String xMLFilename;						//The name of a file in XML format
		String title;							//The full name of a query
		Integer queryNum;						//For looping through the queries
	
		translator.translateQuery(null);			//Initialize
		for (queryNum = 1; queryNum <= 13; queryNum++) {
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase2DistributableTestingData\\soc-sig-parking-lot-2013-09-28-testing\\storylines_darpa_2\\storyline-abduction\\queries\\query-" + queryNum + "\\query.xml";
			title = "data\\SIG_DATA2\\Phase2DistributableTestingData\\soc-sig-parking-lot-2013-09-28-testing\\storylines_darpa_2\\storyline-abduction\\queries\\query-" + queryNum;
			printTranslation(xMLFilename,title,true,false);
		}
		translator.translateQuery(null);			//Initialize
		for (queryNum = 1; queryNum <= 3; queryNum++) {
			xMLFilename = 
				"C:\\0rg\\work\\projects\\MSEE\\SVN\\trunk\\trunk\\data\\SIG_DATA2\\Phase2DistributableTestingData\\soc-sig-parking-lot-2013-09-28-testing\\storylines_darpa_2\\storyline-package-exchange\\queries\\query-" + queryNum + "\\query.xml";
			title = "data\\SIG_DATA2\\Phase2DistributableTestingData\\soc-sig-parking-lot-2013-09-28-testing\\storylines_darpa_2\\storyline-package-exchange\\queries\\query-" + queryNum;
			printTranslation(xMLFilename,title,true,false);
		}
	}
}