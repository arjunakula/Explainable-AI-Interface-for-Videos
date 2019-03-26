package sparql.Parsedata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 *  Contains a HashMap that map an object to another object of which it is part of. e.g. arm is part-of person
 *
 */
public class PartOfMapping {
	
	public HashMap<String, String> partOfMap =  new HashMap<String, String>();
	private boolean bVerboseThis = false;
	


	/**
	 *  Clear any existing data. This is called when there is a change in SOC
	 */
	public void Reset() {
		partOfMap.clear();		
	}
	

	public void LoadMap(String input_file) {

		BufferedReader inputStream;
		String line =null;

		try {
			inputStream = new BufferedReader(new FileReader(input_file));
		} catch (FileNotFoundException e1) {
			System.err.println("ERROR PartOfMapping LoadMap; error reading file " + input_file);
			return;
		}
		
		try {
			line = inputStream.readLine();
		} catch (IOException e) {
			System.err.println("ERROR PartOfMapping LoadMap; error inputStream.readLine " + input_file);
		}
		
		
		while (line != null) {
			
			String [] str_arr=  line.split(",");
			if (str_arr.length != 2)
			{
				System.err.println("ERROR PartOfMapping LoadMap; error str_arr.length != 2, line: " + line);
			} else
			{
				if (str_arr[0].startsWith("#"))				
				{
					str_arr[0] = str_arr[0].substring(1);
				}
				if (str_arr[1].startsWith("#"))
				{
					str_arr[1] = str_arr[1].substring(1);
				}
				
				AddMap(str_arr[1], str_arr[0]);
			}

			try {
				line = inputStream.readLine();
			} catch (IOException e) {
				break;
			}	
			
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("DEBUG PartOfMapping size of map " + partOfMap.size());
		
		/*		
		//testing
		String test_str = partOfMap.get("1bb0ed89-9555-4739-88fd-b4c6d9d92edc_UpperLeg_1");
		System.err.println("DEBUG PartOfMapping test: " + test_str);
		
		test_str = partOfMap.get("1bb0ed89-9555-4739-88fd-b4c6d9d92edc_UpperLeg_dummy");
		if (test_str==null)
		{
			System.err.println("DEBUG PartOfMapping test null okay: " );
		}
		*/ 
		
	}

	private void AddMap(String key, String value) {
		this.partOfMap.put(key,  value);
		
		if (this.bVerboseThis )
		{
			System.out.println("DEBUG PartOfMapping KEY " + key +" VALUE " + value);
		}
	}
	

}
