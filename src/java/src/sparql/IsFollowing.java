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

public class IsFollowing extends FunctionBase
{

	public IsFollowing() { super() ; }
	
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
    		System.out.println("DEBUG IsFollowing running");
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
        int index_patient = -1; 
        
        for (int i = 0; i < args.size()/2 ; i++)
        {	if (MseeFunction.IsObjectType(argType_list.get(i)))
        	{	
        		if (index_agent==-1)
        		{	index_agent = i;
        		} else
        		{
        			index_patient =i;
        		}
        	} else if (MseeFunction.IsTimeType(argType_list.get(i)))
        	{	index_time = i;
        	} else if (MseeFunction.IsLocationType(argType_list.get(i)))
        	{	index_location = i;
        	} else
        	{  throw new ARQInternalErrorException(Utils.className(this)+": IsFollowing: unable to parse arguments") ;
        	}
        }
            
		if ((index_agent ==-1) || (index_patient ==-1) )
		{
		    throw new ARQInternalErrorException(Utils.className(this)+": IsFollowing: unable to parse arguments") ;
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
			
			if (time_data != null)
			{
				if (time_data.hasValidSceneTime() )
				{
					if (time_data.GetSceneTimeInterval_Msec() < 2000)
					{
						time_data.AddMargin_msec(2000);
					}
				} 
			}
			
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


		// result
		Boolean b = false;		
		
		// get objects
		CObjectData object_agent = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_agent)));
		CObjectData object_patient = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_patient)));

		if (	(object_agent != null) && 
				(object_patient != null) && 
				(object_agent.id.compareTo(object_patient.id)!=0))
		{
			boolean bTimeLocOkay = true;
			//boolean bTimeLocOkay = (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_agent, time_data, loc_data) &&
			//						MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_patient, time_data, loc_data));
								
			if (bTimeLocOkay)
			{
				b = SpatialFunctions.IsFollowing(time_data, loc_data, object_agent, object_patient);
			}
		}		
		
		if (MseeFunction.bVerbose)
    	{
			System.out.println("DEBUG IsFollowing ending " + b);
    	}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}
