package mergeData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>Global</code> class consists of things that are used throughout the program (the global constants
 * and variables).
 *
 * @author Ken Samuel
 * @version 1.0, Mar 13, 2014
 * @since 1.6
 */
public class Global {

	/** 
	 * <code>ERROR_RANGE</code> is the maximum number of consecutive frames in which an object can be missing 
	 * from the data and still be assumed to be in the video. 
	 */
	public static final Integer ERROR_RANGE = 100; 
	//public static final Integer ERROR_RANGE = 250; 
		
	/** 
	 * <code>MAIN_DIR</code> is a folder in which all of the input files are located and all of the 
	 * output files will be written. 
	 */
	public static final String MAIN_DIR = "C:\\0rg\\work\\projects\\MSEE\\data\\Phase2DistributableTestingData\\";
	//public static final String MAIN_DIR = "file:\\\\IAI73\\0rg2\\work\\projects\\MSEE\\data\\Phase2DistributableTestingData\\";

	/** 
	 * <code>OFFICE_DIR</code> is the folder in which all of the input files and output files for  
	 * the "SIGOffice" cameras are located.
	 */
	public static final String OFFICE_DIR = MAIN_DIR + "SIGOffice-2013-09-04-Testing\\"; 

	/** 
	 * <code>PARKING_LOT_DIR</code> is the folder in which all of the input files and output files for  
	 * the "SIGOffice" cameras are located.
	 */
	public static final String PARKING_LOT_DIR = MAIN_DIR + "SIGParkingLot-2013-09-28-Testing\\"; 

	/** 
	 * <code>GARDEN_DIR</code> is the folder in which all of the input files and output files for  
	 * the "PrattGarden" cameras are located.
	 */
	public static final String GARDEN_DIR = MAIN_DIR + "PrattGarden-2013-10-12-Testing\\"; 
	
	/** 
	 * <code>INPUT_RDF_FILE</code> is the name of the input file that is in "rdf" format. This is the 
	 * file with the original (unmerged) data.
	 */
	public static String INPUT_RDF_FILE = 
			//XXX Uncomment one of the following three lines. XXX\\
			//Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_multiview\\data.rdf";						//Office
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_multiview\\data.rdf"; 			//Parking lot                                                                    
			//Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_multiview\\data.rdf";						//Garden
	
	/**
	 * <code>INTERMEDIATE_RDF_FILE</code> is the name of the input file that is in "rdf" format. This is the file 
	 * with the intermediate (between <code>MergeEvents</code> and <code>MergeVideoFiles</code>) data.
	 */
	public static String INTERMEDIATE_RDF_FILE = 
			//XXX Uncomment one of the following three lines. XXX\\
			//Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_multiview\\data.rdf";				//Office
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_multiview\\data.rdf"; 	//Parking lot                                                                    
			//Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_multiview\\data.rdf";				//Garden
	
	/** 
	 * <code>OUTPUT_RDF_FILE</code> is the name of the output file where a modified version of the input 
	 * "rdf" file will be written. This is the file with the final (merged) data.
	 */
	public static String OUTPUT_RDF_FILE = 
			//XXX Uncomment one of the following three lines. XXX\\
			//Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_multiview\\data.rdf";				//Office
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_multiview\\data.rdf"; 	//Parking lot                                                  
			//Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_multiview\\data.rdf";				//Garden

	/** 
	 * <code>INPUT_FILESTEMS</code> is a list of the names of the files with time and location information 
	 * about the MSEE objects and events. For objects, <code>OBJECT_FILE_SUFFIX</code> should be added to the
	 * end of the filestem, and for events, <code>EVENT_FILE_SUFFIX</code> is the suffix. These are the 
	 * filestems for the original (unmerged) data.
	 */
	public static final ArrayList<String> INPUT_FILESTEMS = new ArrayList<String>(Arrays.asList(
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR1_multiview_support\\view-BR1-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_0_multiview_support\\view-BR2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_1_multiview_support\\view-BR2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_2_multiview_support\\view-BR2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_3_multiview_support\\view-BR2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_4_multiview_support\\view-BR2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_5_multiview_support\\view-BR2-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_6_multiview_support\\view-BR2-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_7_multiview_support\\view-BR2-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR2_8_multiview_support\\view-BR2-video-8",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_0_multiview_support\\view-BR3-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_1_multiview_support\\view-BR3-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_2_multiview_support\\view-BR3-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_3_multiview_support\\view-BR3-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_4_multiview_support\\view-BR3-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_5_multiview_support\\view-BR3-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_6_multiview_support\\view-BR3-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_BR3_7_multiview_support\\view-BR3-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_0_multiview_support\\view-CR1-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_1_multiview_support\\view-CR1-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_2_multiview_support\\view-CR1-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_3_multiview_support\\view-CR1-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_4_multiview_support\\view-CR1-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_5_multiview_support\\view-CR1-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_6_multiview_support\\view-CR1-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR1_7_multiview_support\\view-CR1-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR2_multiview_support\\view-CR2-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR3_0_multiview_support\\view-CR3-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR3_1_multiview_support\\view-CR3-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR3_2_multiview_support\\view-CR3-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_CR4_multiview_support\\view-CR4-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_0_multiview_support\\view-HW1-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_1_multiview_support\\view-HW1-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_2_multiview_support\\view-HW1-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_3_multiview_support\\view-HW1-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_4_multiview_support\\view-HW1-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_5_multiview_support\\view-HW1-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_6_multiview_support\\view-HW1-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW1_7_multiview_support\\view-HW1-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_0_multiview_support\\view-HW2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_1_multiview_support\\view-HW2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_2_multiview_support\\view-HW2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_3_multiview_support\\view-HW2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_4_multiview_support\\view-HW2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_5_multiview_support\\view-HW2-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_6_multiview_support\\view-HW2-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_HW2_7_multiview_support\\view-HW2-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R1_multiview_support\\view-R1-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R2_0_multiview_support\\view-R2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R2_1_multiview_support\\view-R2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R2_2_multiview_support\\view-R2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R2_3_multiview_support\\view-R2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R2_4_multiview_support\\view-R2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\unmerged\\result_20140331_021605_office_R3_multiview_support\\view-R3-video",
			
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL1_0_multiview_support\\view-GL1-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL1_1_multiview_support\\view-GL1-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL1_2_multiview_support\\view-GL1-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL1_3_multiview_support\\view-GL1-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL1_4_multiview_support\\view-GL1-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL2_0_multiview_support\\view-GL2-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL2_1_multiview_support\\view-GL2-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL2_2_multiview_support\\view-GL2-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL2_3_multiview_support\\view-GL2-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL2_4_multiview_support\\view-GL2-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL3_0_multiview_support\\view-GL3-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL3_1_multiview_support\\view-GL3-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL3_2_multiview_support\\view-GL3-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL3_3_multiview_support\\view-GL3-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL3_4_multiview_support\\view-GL3-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL4_0_multiview_support\\view-GL4-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL4_1_multiview_support\\view-GL4-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL4_2_multiview_support\\view-GL4-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL4_3_multiview_support\\view-GL4-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_GL4_4_multiview_support\\view-GL4-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT1_multiview_support\\view-RT1-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT2_multiview_support\\view-RT2-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT3_multiview_support\\view-RT3-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT4_0_multiview_support\\view-RT4-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT4_1_multiview_support\\view-RT4-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT4_2_multiview_support\\view-RT4-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\unmerged\\result_20140403_134428_parkinglot_RT5_multiview_support\\view-RT5-video",
	
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC1_0_multiview_support\\view-HC1-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC1_1_multiview_support\\view-HC1-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC1_2_multiview_support\\view-HC1-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC1_3_multiview_support\\view-HC1-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC1_4_multiview_support\\view-HC1-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC2_0_multiview_support\\view-HC2-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC2_1_multiview_support\\view-HC2-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC2_2_multiview_support\\view-HC2-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC2_3_multiview_support\\view-HC2-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC2_4_multiview_support\\view-HC2-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC3_0_multiview_support\\view-HC3-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC3_1_multiview_support\\view-HC3-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC3_2_multiview_support\\view-HC3-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC3_3_multiview_support\\view-HC3-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC3_4_multiview_support\\view-HC3-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC4_0_multiview_support\\view-HC4-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC4_1_multiview_support\\view-HC4-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC4_2_multiview_support\\view-HC4-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC4_3_multiview_support\\view-HC4-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_HC4_4_multiview_support\\view-HC4-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_IP1_multiview_support\\view-IP1-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_IP2_multiview_support\\view-IP2-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_IP3_multiview_support\\view-IP3-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\unmerged\\result_20140404_001501_garden_IP4_multiview_support\\view-IP4-video"
			));

	/**
	 * <code>INTERMEDIATE_FILESTEMS</code> is a list of the names of the files with time and location  
	 * information about the MSEE objects and events. For objects, <code>OBJECT_FILE_SUFFIX</code> should be 
	 * added to the end of the filestem, and for events, <code>EVENT_FILE_SUFFIX</code> is the suffix. These 
	 * are the filestems for the intermediate (between <code>MergeEvents</code> and 
	 * <code>MergeVideoFiles</code>) data.
	 */
	public static final ArrayList<String> INTERMEDIATE_FILESTEMS = new ArrayList<String>(Arrays.asList(
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR1_multiview_support\\view-BR1-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_0_multiview_support\\view-BR2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_1_multiview_support\\view-BR2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_2_multiview_support\\view-BR2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_3_multiview_support\\view-BR2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_4_multiview_support\\view-BR2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_5_multiview_support\\view-BR2-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_6_multiview_support\\view-BR2-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_7_multiview_support\\view-BR2-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR2_8_multiview_support\\view-BR2-video-8",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_0_multiview_support\\view-BR3-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_1_multiview_support\\view-BR3-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_2_multiview_support\\view-BR3-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_3_multiview_support\\view-BR3-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_4_multiview_support\\view-BR3-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_5_multiview_support\\view-BR3-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_6_multiview_support\\view-BR3-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_BR3_7_multiview_support\\view-BR3-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_0_multiview_support\\view-CR1-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_1_multiview_support\\view-CR1-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_2_multiview_support\\view-CR1-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_3_multiview_support\\view-CR1-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_4_multiview_support\\view-CR1-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_5_multiview_support\\view-CR1-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_6_multiview_support\\view-CR1-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR1_7_multiview_support\\view-CR1-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR2_multiview_support\\view-CR2-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR3_0_multiview_support\\view-CR3-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR3_1_multiview_support\\view-CR3-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR3_2_multiview_support\\view-CR3-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_CR4_multiview_support\\view-CR4-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_0_multiview_support\\view-HW1-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_1_multiview_support\\view-HW1-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_2_multiview_support\\view-HW1-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_3_multiview_support\\view-HW1-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_4_multiview_support\\view-HW1-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_5_multiview_support\\view-HW1-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_6_multiview_support\\view-HW1-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW1_7_multiview_support\\view-HW1-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_0_multiview_support\\view-HW2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_1_multiview_support\\view-HW2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_2_multiview_support\\view-HW2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_3_multiview_support\\view-HW2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_4_multiview_support\\view-HW2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_5_multiview_support\\view-HW2-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_6_multiview_support\\view-HW2-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_HW2_7_multiview_support\\view-HW2-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R1_multiview_support\\view-R1-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R2_0_multiview_support\\view-R2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R2_1_multiview_support\\view-R2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R2_2_multiview_support\\view-R2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R2_3_multiview_support\\view-R2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R2_4_multiview_support\\view-R2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\partly_merged\\result_20140331_021605_office_R3_multiview_support\\view-R3-video",
			
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL1_0_multiview_support\\view-GL1-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL1_1_multiview_support\\view-GL1-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL1_2_multiview_support\\view-GL1-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL1_3_multiview_support\\view-GL1-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL1_4_multiview_support\\view-GL1-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL2_0_multiview_support\\view-GL2-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL2_1_multiview_support\\view-GL2-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL2_2_multiview_support\\view-GL2-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL2_3_multiview_support\\view-GL2-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL2_4_multiview_support\\view-GL2-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL3_0_multiview_support\\view-GL3-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL3_1_multiview_support\\view-GL3-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL3_2_multiview_support\\view-GL3-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL3_3_multiview_support\\view-GL3-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL3_4_multiview_support\\view-GL3-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL4_0_multiview_support\\view-GL4-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL4_1_multiview_support\\view-GL4-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL4_2_multiview_support\\view-GL4-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL4_3_multiview_support\\view-GL4-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_GL4_4_multiview_support\\view-GL4-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT1_multiview_support\\view-RT1-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT2_multiview_support\\view-RT2-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT3_multiview_support\\view-RT3-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT4_0_multiview_support\\view-RT4-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT4_1_multiview_support\\view-RT4-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT4_2_multiview_support\\view-RT4-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\partly_merged\\result_20140403_134428_parkinglot_RT5_multiview_support\\view-RT5-video",
	
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC1_0_multiview_support\\view-HC1-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC1_1_multiview_support\\view-HC1-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC1_2_multiview_support\\view-HC1-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC1_3_multiview_support\\view-HC1-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC1_4_multiview_support\\view-HC1-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC2_0_multiview_support\\view-HC2-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC2_1_multiview_support\\view-HC2-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC2_2_multiview_support\\view-HC2-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC2_3_multiview_support\\view-HC2-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC2_4_multiview_support\\view-HC2-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC3_0_multiview_support\\view-HC3-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC3_1_multiview_support\\view-HC3-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC3_2_multiview_support\\view-HC3-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC3_3_multiview_support\\view-HC3-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC3_4_multiview_support\\view-HC3-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC4_0_multiview_support\\view-HC4-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC4_1_multiview_support\\view-HC4-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC4_2_multiview_support\\view-HC4-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC4_3_multiview_support\\view-HC4-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_HC4_4_multiview_support\\view-HC4-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_IP1_multiview_support\\view-IP1-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_IP2_multiview_support\\view-IP2-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_IP3_multiview_support\\view-IP3-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\partly_merged\\result_20140404_001501_garden_IP4_multiview_support\\view-IP4-video"
			));

	/** 
	 * <code>OUTPUT_FILESTEMS</code> is a list of the names of the files with time and location information 
	 * about the MSEE objects and events. For objects, <code>OBJECT_FILE_SUFFIX</code> should be added to the
	 * end of the filestem, and for events, <code>EVENT_FILE_SUFFIX</code> is the suffix. These are the 
	 * filestems for the final (merged) data.
	 */
	public static final ArrayList<String> OUTPUT_FILESTEMS = new ArrayList<String>(Arrays.asList(
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR1_multiview_support\\view-BR1-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_0_multiview_support\\view-BR2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_1_multiview_support\\view-BR2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_2_multiview_support\\view-BR2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_3_multiview_support\\view-BR2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_4_multiview_support\\view-BR2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_5_multiview_support\\view-BR2-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_6_multiview_support\\view-BR2-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_7_multiview_support\\view-BR2-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR2_8_multiview_support\\view-BR2-video-8",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_0_multiview_support\\view-BR3-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_1_multiview_support\\view-BR3-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_2_multiview_support\\view-BR3-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_3_multiview_support\\view-BR3-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_4_multiview_support\\view-BR3-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_5_multiview_support\\view-BR3-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_6_multiview_support\\view-BR3-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_BR3_7_multiview_support\\view-BR3-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_0_multiview_support\\view-CR1-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_1_multiview_support\\view-CR1-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_2_multiview_support\\view-CR1-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_3_multiview_support\\view-CR1-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_4_multiview_support\\view-CR1-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_5_multiview_support\\view-CR1-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_6_multiview_support\\view-CR1-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR1_7_multiview_support\\view-CR1-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR2_multiview_support\\view-CR2-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR3_0_multiview_support\\view-CR3-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR3_1_multiview_support\\view-CR3-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR3_2_multiview_support\\view-CR3-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_CR4_multiview_support\\view-CR4-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_0_multiview_support\\view-HW1-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_1_multiview_support\\view-HW1-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_2_multiview_support\\view-HW1-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_3_multiview_support\\view-HW1-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_4_multiview_support\\view-HW1-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_5_multiview_support\\view-HW1-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_6_multiview_support\\view-HW1-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW1_7_multiview_support\\view-HW1-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_0_multiview_support\\view-HW2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_1_multiview_support\\view-HW2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_2_multiview_support\\view-HW2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_3_multiview_support\\view-HW2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_4_multiview_support\\view-HW2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_5_multiview_support\\view-HW2-video-5",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_6_multiview_support\\view-HW2-video-6",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_HW2_7_multiview_support\\view-HW2-video-7",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R1_multiview_support\\view-R1-video",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R2_0_multiview_support\\view-R2-video-0",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R2_1_multiview_support\\view-R2-video-1",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R2_2_multiview_support\\view-R2-video-2",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R2_3_multiview_support\\view-R2-video-3",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R2_4_multiview_support\\view-R2-video-4",
			Global.OFFICE_DIR + "result_20140331_021605_office\\fully_merged\\result_20140331_021605_office_R3_multiview_support\\view-R3-video",
			
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL1_0_multiview_support\\view-GL1-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL1_1_multiview_support\\view-GL1-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL1_2_multiview_support\\view-GL1-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL1_3_multiview_support\\view-GL1-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL1_4_multiview_support\\view-GL1-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL2_0_multiview_support\\view-GL2-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL2_1_multiview_support\\view-GL2-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL2_2_multiview_support\\view-GL2-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL2_3_multiview_support\\view-GL2-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL2_4_multiview_support\\view-GL2-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL3_0_multiview_support\\view-GL3-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL3_1_multiview_support\\view-GL3-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL3_2_multiview_support\\view-GL3-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL3_3_multiview_support\\view-GL3-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL3_4_multiview_support\\view-GL3-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL4_0_multiview_support\\view-GL4-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL4_1_multiview_support\\view-GL4-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL4_2_multiview_support\\view-GL4-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL4_3_multiview_support\\view-GL4-video-3",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_GL4_4_multiview_support\\view-GL4-video-4",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT1_multiview_support\\view-RT1-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT2_multiview_support\\view-RT2-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT3_multiview_support\\view-RT3-video",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT4_0_multiview_support\\view-RT4-video-0",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT4_1_multiview_support\\view-RT4-video-1",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT4_2_multiview_support\\view-RT4-video-2",
			Global.PARKING_LOT_DIR + "result_20140402_130453_parkinglot\\fully_merged\\result_20140403_134428_parkinglot_RT5_multiview_support\\view-RT5-video",
	
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC1_0_multiview_support\\view-HC1-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC1_1_multiview_support\\view-HC1-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC1_2_multiview_support\\view-HC1-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC1_3_multiview_support\\view-HC1-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC1_4_multiview_support\\view-HC1-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC2_0_multiview_support\\view-HC2-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC2_1_multiview_support\\view-HC2-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC2_2_multiview_support\\view-HC2-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC2_3_multiview_support\\view-HC2-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC2_4_multiview_support\\view-HC2-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC3_0_multiview_support\\view-HC3-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC3_1_multiview_support\\view-HC3-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC3_2_multiview_support\\view-HC3-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC3_3_multiview_support\\view-HC3-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC3_4_multiview_support\\view-HC3-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC4_0_multiview_support\\view-HC4-video-0",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC4_1_multiview_support\\view-HC4-video-1",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC4_2_multiview_support\\view-HC4-video-2",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC4_3_multiview_support\\view-HC4-video-3",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_HC4_4_multiview_support\\view-HC4-video-4",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_IP1_multiview_support\\view-IP1-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_IP2_multiview_support\\view-IP2-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_IP3_multiview_support\\view-IP3-video",
			Global.GARDEN_DIR + "result_20140404_001501_garden\\fully_merged\\result_20140404_001501_garden_IP4_multiview_support\\view-IP4-video"
			));

	/** 
	 * <code>RDF_FILE_SUFFIX</code> is the last characters in the name of every parse graph file.
	 */
	public static final String PARSE_GRAPH_FILE_SUFFIX = "_rdf.rdf";
	
	/** 
	 * <code>OBJECT_FILE_SUFFIX</code> is the last characters in the name of every file with time and location 
	 * information about the MSEE objects.
	 */
	public static final String OBJECT_FILE_SUFFIX = "_ol.xml";
	
	/** 
	 * <code>EVENT_FILE_SUFFIX</code> is the last characters in the name of every file with time and location  
	 * information about the MSEE events (behaviors, relationships, and actions).
	 */
	public static final String EVENT_FILE_SUFFIX = "_pt.xml";

	
	/**
	 * The <code>openOutputFile</code> method opens a file for writing and returns a 
	 * <code>PrintStream</code> that can be used to access the file.
	 *
	 * @param filename is the name of the file to save to.
	 * @return a  <code>PrintStream</code> to access the data in that file.
	 * @throws IOException if there's a problem opening the file.
	 */
	public static PrintStream openOutputFile(String filename)  throws IOException {
		PrintStream returnValue;
		
		createDirectories(filename);			//Create any directories that don't already exist
		System.out.print("Opening " + filename + "... ");
		returnValue = new PrintStream(new File(filename));
		System.out.println("Done.");
		return returnValue;
	}

	/**
	 * The <code>openInputFile</code> method opens a file and returns a <code>BufferedReader</code> that can 
	 * be used to access the data in the file.
	 *
	 * @param filename is the name of the file to load.
	 * @param printInfo specifies whether information should be sent to standard output.
	 * @return a  <code>BufferedReader</code> to access the data in that file.
	 * @throws IOException if there's a problem opening the file.
	 */
	public static BufferedReader openInputFile(String filename, Boolean printInfo)  throws IOException {
		BufferedReader returnValue;
		
		if (printInfo) {
			System.out.print("Opening " + filename + "... ");
		}
		returnValue = new BufferedReader(new FileReader(filename));
		if (printInfo) {
			System.out.println("Done.");
		}
		return returnValue;
	}

	/**
	 * The <code>copyFile</code> method copies all of the text in one file into another file.
	 *
	 * @param inFile is the name of the file to be copied.
	 * @param outFile is the name to give the new file.
	 * 
	 * @throws IOException if there's a problem with a file.
	 */
	public static void copyFile(String inFile, String outFile) throws IOException {
		BufferedReader in;				//The handle to access the input file
		PrintStream out;				//The handle to access the output file
		String line;					//A line of text from the input file

		in = openInputFile(inFile,true);
		out = openOutputFile(outFile);
		while ((line = in.readLine()) != null) {			//Load the next line from the file
			out.println(line);
		}
		in.close();
		out.close();
	}
	
	/**
	 * The <code>createDirectories</code> method creates any directories that don't already exist that are in
	 * the path of the given file name.
	 *
	 * @param filename is the name of a file, which may include a directory path.
	 */
	public static void createDirectories(String filename) {
		Matcher match;				//A match between a regular expression and a string
		String dirname;				//The path of the filename
		File directory;				//The path as a File object

		match = Pattern.compile("(\\\\|/)[^\\\\/]*$").matcher(filename);	//Find the last (back)slash
		if (match.find()) {
			dirname = filename.substring(0,match.start()+1);
			directory = new File(dirname);
			if ( ! directory.exists()) {
				System.out.print("Creating \"" + dirname + "\"... ");
				directory.mkdirs();
				System.out.println("Done.");
			}
		}
	}
	
	/**
	 * The <code>printLines</code> method prints an array of strings by printing each member of the array on
	 * a separate line.
	 *
	 * @param out is a <code>PrintStream</code> that can be used to write data to a file.
	 * @param lines is the information to be printed to the <code>out</code> stream.
	 */
	public static void printLines(PrintStream out, ArrayList<String> lines) {
		String confidence;				//A confidence value
		
		for (String line : lines) {
			if (line.matches("\\s*<msee:hasConfidence>[^\\s<]+\\s*</msee:hasConfidence>\\s*")) {
				//Confidence: <msee:hasConfidence>1.0</msee:hasConfidence>
				confidence = Global.getSubstring(line,"<msee:hasConfidence>\\s*","\\s*</msee:hasConfidence>");
				if ( ! isOne(confidence)) {		//Don't print out confidences of 1.0
					out.println(line);
				}
			} else {
				out.println(line);
			}
		}
	}

	/**
	 * The <code>replaceSubstring</code> method replaces a substring in a given string with a new substring
	 * and returns the result. The boundaries of the original substring (in the given string) are specified 
	 * by two regular expressions, one that matches the substring immediately preceding the desired substring, 
	 * and the other that matches the substring immediately following the desired substring. If one of the 
	 * regular expressions is not found, the given string is returned unchanged.
	 *
	 * @param string is the string that has the original substring in it.
	 * @param precedingRegEx is a regular expression that matches the part of <code>string</code> immediately
	 * preceding the original substring. If it's <b><code>null</code></b>, then the left boundary will be the
	 * beginning of <code>string</code>.
	 * @param followingRegEx is a regular expression that matches the part of <code>string</code> immediately
	 * following the original substring. If it's <b><code>null</code></b>, then the right boundary will be the
	 * end of <code>string</code>.
	 * @param newSubstring is the substring that will replace the original substring in <code>string</code>.
	 * @return the original string with the original substring replaced by newSubstring.
	 */
	public static String replaceSubstring(
			String string, String precedingRegEx, String followingRegEx, String newSubstring) {
		String originalSubstring;		//The substring that will be replaced
		String stringBegin;				//The characters in the given string preceding the original substring
		String stringEnd;				//The characters in the given string following the original substring

		originalSubstring = getSubstring(string,precedingRegEx,followingRegEx);
		if (originalSubstring.equals("")) {		//Didn't find one of the regex's
			return string;
		}
		stringEnd = getSubstring(string,precedingRegEx+originalSubstring,null);
		stringBegin = getSubstring(string,null,originalSubstring+stringEnd);
		return stringBegin + newSubstring + stringEnd;
	}

	/**
	 * The <code>removeSubstring</code> method removes a substring from a given string and returns the result. 
	 * The substring is specified by a regular expression. Only one substring is removed.
	 *
	 * @param string is the string that has the substring in it.
	 * @param regEx is a regular expression that matches the part of <code>string</code> to be removed.
	 * @return the original string without the substring.
	 */
	public static String removeSubstring(String string, String regEx) {
		String stringBegin;				//The characters in the given string preceding the substring
		String stringEnd;				//The characters in the given string following the substring

		stringBegin = getSubstring(string,null,regEx);
		stringEnd = getSubstring(string,regEx,null);
		return stringBegin + stringEnd;
	}

	/**
	 * The <code>isOne</code> method determines whether a string is the number 1.
	 *
	 * @param string is the string to test.
	 * @return <b><code>true</code></b> if and only if the string is "1", "1.0", "1.00", etc.
	 */
	public static Boolean isOne(String string) {
		return (Double.valueOf(string) == 1);
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
}