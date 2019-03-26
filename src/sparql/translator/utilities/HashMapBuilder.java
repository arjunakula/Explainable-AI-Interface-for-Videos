package sparql.translator.utilities;

import java.util.HashMap;


/**
 * The <code>HashMapBuilder</code> class is used to make it easy to initialize a <code>HashMap</code> with 
 * specific values. This is done with the following syntax:
 * <p><code>HashMap<String,String> data = HashMapBuilder.build("key1","value1","key2","value2");</code>
 *
 * @author Aerthel
 * @version 1.0, Jul 4, 2013
 * @since 1.6
 */
public class HashMapBuilder {

	/**
	 * The <code>build</code> method creates a new <code>HashMap</code> object that is initialized with the 
	 * given values.
	 *
	 * @param data is a list of strings, where, the nth string is the key for the (n+1)th string for all odd
	 * n. 
	 * @return a <code>HashMap</code> object with the given values.
	 */
	public static HashMap<String, String> build(String... data){
		String warning;							//A message to let the user know there might be a problem
	    HashMap<String, String> result = new HashMap<String, String>();

	    if(data.length % 2 != 0) {
	    	warning = "WARNING in HashMapBuilder.build: Bug detected. Contact Ken Samuel.";
	    	System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
	    }

	    String key = null;
	    Integer step = -1;

	    for(String value : data){
	        step++;
	        switch(step % 2){
	        case 0: 
	            if(value == null) {
	            	warning = "WARNING in HashMapBuilder2.build: Bug detected. Contact Ken Samuel.";
	    	    	System.err.println(warning);
	    	    	Global.unableToRespondMessage.add(warning);
	            }
	            key = value;
	            continue;
	        case 1:             
	            result.put(key, value);
	            break;
	        default:			//This should never happen
	        	warning = "WARNING in HashMapBuilder.build: Bug detected. Contact Ken Samuel.";
    	    	System.err.println(warning);
    	    	Global.unableToRespondMessage.add(warning);
	        }
	    }

	    return result;
	}
}