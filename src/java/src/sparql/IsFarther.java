package sparql;

import java.util.ArrayList;
import java.util.List;

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

public class IsFarther extends FunctionBase
{

	public IsFarther() { super() ; }
	
	@Override
    public void checkBuild(String uri, ExprList args)
    { 
        if ( args.size() %2  != 0 )
            throw new QueryBuildException("Function '"+Utils.className(this)+"' takes even number of arguments") ;
        

        if (( args.size() < 6 ) || ( args.size() > 10 )) 
        	throw new QueryBuildException("Function '"+Utils.className(this)+"' takes 6 to 10 arguments") ;
        
    }   
    
    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {
    	if (MseeFunction.bVerbose)
    	{
    		System.out.println("DEBUG IsFarther running");
    	}
		
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
        int index_agent = -1;
        int index_object1 = -1;
        int index_object2 = -1;
        
        for (int i = 0; i < args.size()/2 ; i++)
        {	if (MseeFunction.IsObjectType(argType_list.get(i)))
        	{	
        		if (index_agent==-1)
        		{	index_agent = i;
        		} else
        		{	if (index_object1==-1)
        			{	index_object1 = i;
        			} else
        			{
        				index_object2 =i;
        			}
        		}
        	} else if (MseeFunction.IsTimeType(argType_list.get(i)))
        	{	index_time = i;
        	} else if (MseeFunction.IsLocationType(argType_list.get(i)))
        	{	index_location = i;
        	} else
        	{  throw new ARQInternalErrorException(Utils.className(this)+": IsPassing: unable to parse arguments") ;
        	}
        }
            
		if ((index_agent ==-1) || (index_object1 ==-1) || (index_object2 ==-1)  )
		{
		    throw new ARQInternalErrorException(Utils.className(this)+": IsPassing: unable to parse arguments") ;
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
		CObjectData object_agent = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_agent)));
		CObjectData object_object1 = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_object1)));
		CObjectData object_object2 = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_object2)));

		// result
		Boolean b = false;		


		if (	(object_agent != null) && 
				(object_object1 != null) && 
				(object_object2 != null) && 
				(object_agent.id.compareTo(object_object1.id)!=0) &&
				(object_agent.id.compareTo(object_object2.id)!=0) && 
				(object_object1.id.compareTo(object_object2.id)!=0) 				
				)
		{
			boolean bTimeLocOkay  = true;
			/*
			 * boolean bTimeLocOkay = (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_agent, time_data, loc_data) &&
			 
									MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_object1, time_data, loc_data) &&
									MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_object2, time_data, loc_data));
			*/
			
			if (bTimeLocOkay)
			{
				b = SpatialFunctions.IsFarther(time_data, loc_data, object_agent, object_object1, object_object2);
			}
		}		
		
		
		
		if (MseeFunction.bVerbose)
    	{
			System.out.println("DEBUG IsFarther ending");
    	}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}
