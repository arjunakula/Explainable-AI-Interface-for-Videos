package edu.ucla.xai.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.*;

public class Utility {

	Map<String, Map<String, Double> > vals = new HashMap<String, Map<String, Double> >();

	public void loadUtilities(String filepath) throws Exception{
		BufferedReader inputStream;
		String line = "";
		String meta_line = "";
		StringBuffer returnValue;

		returnValue = new StringBuffer();
		inputStream = new BufferedReader(new FileReader(filepath));
		try {
			meta_line = inputStream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] meta_data = meta_line.trim().split("\\s+");

		try {
			line = inputStream.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (line != null && line.trim().length()!=0) {
			String[] data = line.trim().split("\\s+");
			Map<String, Double> util = new HashMap<String,  Double>();
			for(int i = 1; i < data.length; i++){
				util.put(meta_data[i-1], Double.parseDouble(data[i]));
			}
			
			Map<String, Double> sortedUtil = sortByValue(util);
			
			vals.put(data[0], sortedUtil);

			line = inputStream.readLine();

		}

		inputStream.close();
	}
	
	 private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

	        // 1. Convert Map to List of Map
	        List<Map.Entry<String, Double>> list =
	                new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

	        // 2. Sort list with Collections.sort(), provide a custom Comparator
	        //    Try switch the o1 o2 position for a different order
	        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
	            public int compare(Map.Entry<String, Double> o1,
	                               Map.Entry<String, Double> o2) {
	                return (o1.getValue()).compareTo(o2.getValue());
	            }
	        });

	        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	        for (Map.Entry<String, Double> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        /*
	        //classic iterator example
	        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
	            Map.Entry<String, Integer> entry = it.next();
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }*/


	        return sortedMap;
	    }

}
