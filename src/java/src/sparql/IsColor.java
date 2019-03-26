package sparql;

import java.util.ArrayList;
import java.util.List;

import sparql.ColorFunctions.Color;

import TextParser.CObjectData;

import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.util.Utils;

// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html

public class IsColor extends FunctionBase
{
	
	public IsColor() { super() ; }
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
        if ( args.size() %2  != 0 )
            throw new QueryBuildException("Function '"+Utils.className(this)+"' takes even number of arguments") ;
        

        if (( args.size() < 4 ) || ( args.size() > 8 )) 
        	throw new QueryBuildException("Function '"+Utils.className(this)+"' takes four to eight arguments") ;
        
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	if (MseeFunction.bVerbose)
    	{
    		System.out.println("DEBUG IsColor running");
    	}
    	
    	
		// result
		Boolean b = true;	
		
		
		// we do not do color anymore in filter function
		if (false)
		{
				
	        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
	        ArrayList< String > argStr_list = new ArrayList< String >();
	               
	        for (int i = 0; i < args.size()/2 ; i++)
	        {
	        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
	        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
	        			args.get(2*i+1)));
	       }
	        
	        int index_time = -1;
	        int index_location = -1; 
	        int index_object = -1;
	        int index_color  = -1; 
	        
	        for (int i = 0; i < args.size()/2 ; i++)
	        {	if (MseeFunction.IsObjectType(argType_list.get(i)))
	        	{	index_object = i;        		
	        	} else if (MseeFunction.IsTimeType(argType_list.get(i)))
	        	{	index_time = i;
	        	} else if (MseeFunction.IsLocationType(argType_list.get(i)))
	        	{	index_location = i;
	        	}  else if (MseeFunction.IsColorType(argType_list.get(i)))
	        	{	index_color = i;
	        	} else
	        	{  throw new ARQInternalErrorException(Utils.className(this)+": IsColor: unable to parse arguments") ;
	        	}
	        }
	            
			if ((index_object ==-1) || (index_color ==-1) )
			{
			    throw new ARQInternalErrorException(Utils.className(this)+": IsColor: unable to parse arguments") ;
		    }
			
			// get time and location conditions		
			TimeData time_data  = null;
			LocationData loc_data = null; 
			try {
				// get time
				if (index_time != -1)
				{	time_data = MseeDataset.GetTimeData(
								argType_list.get(index_time),
								argStr_list.get(index_time));		
				}
				
				// get location 			
				if (index_location != -1)
				{	loc_data = MseeDataset.GetLocationData(
						argType_list.get(index_location),
								argStr_list.get(index_location));				
				}
			} catch (MseeException e) {				
				e.printStackTrace();
			}
		
			// get objects
			String object_id = argStr_list.get(index_object); 
			CObjectData object_agent = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(object_id ));
	
			// get color
			Color color = ColorFunctions.GetColor(argStr_list.get(index_color));
					
			
	
			if (object_agent != null) 
			{
				b = ColorFunctions.IsColor(time_data, loc_data, object_agent, color);
			} else
			{
				System.err.println(
						"WARNING in Iscolor, object_agent is null, object id " + object_id);
		
			}
			
			
			if (MseeFunction.bVerbose)
	    	{
				System.out.println("DEBUG IsColor ending");
	    	}
		}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}
