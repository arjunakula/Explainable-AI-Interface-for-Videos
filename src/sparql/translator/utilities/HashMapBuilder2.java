package sparql.translator.utilities;

import java.util.HashMap;


/**
 * The <code>HashMapBuilder2</code> class is used to make it easy to initialize a <code>HashMap</code> with 
 * specific values. Each key is a string, and its value is two 1-dimensional string arrays (which is 
 * represented as a 2-dimensional string array in which the first dimension always has exactly two elements).
 * This is done with the following syntax:
 * <p><code><ul>
 * <li>String[][] array = {
 * <ul><li>{"key1"},
 *     <ul><li>{"value111","value112","value113"},
 *         <li>{"value121","value122"},</ul>
 *     <li>{"key2"},
 *     <ul><li>{"value211","value212"},
 *         <li>{},</ul>
 *     <li>{"key3"},
 *     <ul><li>{},
 *         <li>{"value321"}};</ul></ul>
 * <li>HashMap&lt;String,String[][]&gt; data = HashMapBuilder2.build(array);</ul></code>
 * 
 * @author Ken Samuel
 * @version 1.0, Dec 31, 2013
 * @since 1.6
 */
public class HashMapBuilder2 {

	/**
	 * The <code>build</code> method creates a new <code>HashMap</code> object that is initialized with the 
	 * given values. Each key in the <code>HashMap</code> points to an array with two arrays of strings in it.
	 *
	 * @param data is a 2-dimensional arrays of strings, where sequences of 3 elements in the first array 
	 * (the 1st, 2nd, and 3rd elements, or the 4th, 5th, and 6th elements, etc.) have the following 
	 * interpretation:
	 * <ul><li>The first element is a one-element 1-dimensional array that holds a key.
	 *     <li>The second element is a 1-dimensional array that is the first member of the value.
	 *     <li>The third element is a 1-dimensional array that is the second member of the value.
	 * @return a <code>HashMap</code> object in which each key points to an array with two elements, both of 
	 * which are 1-dimensional arrays of strings.
	 */
	public static HashMap<String,String[][]> build(String[][] data){
		String key;											//One of the keys
		String[][] value;									//The corresponding value
		String[] valueElement0, valueElement1;				//The two arrays in the value
		Integer step;										//For keeping track of the position in the array
		String warning;										//A message to let the user know there might be a problem
	    HashMap<String, String[][]> result;
	    
	    if ((data.length % 3) != 0) {
	    	warning = 
	    			"WARNING in HashMapBuilder2.build: Bug detected. Contact Ken Samuel.";
	    	System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
	    }
	    
	    result = new HashMap<String, String[][]>();			//Initialize
	    key = null;											//Initialize
	    valueElement0 = new String[0];						//Initialize
	    valueElement1 = new String[0];						//Initialize
	    step = -1;											//Initialize
	    for(String[] array : data){
	        step++;
	        switch(step % 3){
	        case 0: 
	            if(array[0] == null) {
	            	warning = "WARNING in HashMapBuilder2.build: Bug detected. Contact Ken Samuel.";
	    	    	System.err.println(warning);
	    	    	Global.unableToRespondMessage.add(warning);
	            }
	            key = array[0];
	            continue;
	        case 1:
	        	valueElement0 = array;
	            continue;
	        case 2:
	        	valueElement1 = array;
		    	value = new String[2][Math.max(valueElement0.length,valueElement1.length)];
		    	value[0] = valueElement0;
		    	value[1] = valueElement1;
		    	result.put(key,value);
	            break;
	        default:			//This should never happen
	        	warning = "WARNING in HashMapBuilder2.build: Bug detected. Contact Ken Samuel.";
    	    	System.err.println(warning);
    	    	Global.unableToRespondMessage.add(warning);
	        }
	    }
	    return result;
	}
}