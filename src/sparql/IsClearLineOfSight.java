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

public class IsClearLineOfSight extends FunctionBase
{
	
	boolean bVerboseThis = false; 
	
	static int numCallsIsClearLineOfSight = 0; 
	
	public IsClearLineOfSight() { super() ; }
	
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
		numCallsIsClearLineOfSight++;
		
    	if (MseeFunction.bVerbose || bVerboseThis )
    	{
    		System.out.println("DEBUG IsClearLineOfSight running");
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
        	{  throw new ARQInternalErrorException(Utils.className(this)+": IsPassing: unable to parse arguments") ;
        	}
        }
            
		if ((index_agent ==-1) || (index_patient ==-1) )
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
		
		// get objects
		
		String id_agent = MseeDataset.RemoveNamespace(argStr_list.get(index_agent));
		String id_patient = MseeDataset.RemoveNamespace(argStr_list.get(index_patient));
		

		
		if (id_agent.startsWith("\""))
		{
			id_agent = id_agent.substring(1,id_agent.length()-1);
		}
		
		if (id_patient.startsWith("\""))
		{
			id_patient = id_patient.substring(1,id_patient.length()-1);
		}
		
		String key_obs = "data:";

		if (id_patient.startsWith(key_obs)==true)
		{
			// swap, so that observer is the agent
			String tmp = id_agent;
			id_agent = id_patient;
			id_patient = tmp;
				
		} 
		
		
		System.out.println("DEBUG IsClearLineOfSight -  id_agent " + id_agent +  " id_patient " + id_patient + " numCalls " + numCallsIsClearLineOfSight);
		
		
		// result
		Boolean b = false;
		
		if  (id_agent.startsWith(key_obs)==true)
		{
			// agent is an observer
			System.out.println("DEBUG IsClearLineOfSight observer id " + id_agent);
			id_agent = id_agent.substring(key_obs.length());		// remove the "data:" tag

			if (MseeFunction.bVerbose || bVerboseThis )
		    {
				System.out.println("DEBUG IsClearLineOfSight observer id " + id_agent);
				System.out.println("DEBUG IsClearLineOfSight patient id " + id_patient);
		    }			
				
			b = SpatialFunctions.IsClearLineOfSight_WithObserver(time_data, loc_data, id_agent, id_patient);
				
			
		} else
		{	
			
			ArrayList<CObjectData> object_list_agent = MseeDataset.FindObjectDataList_or_parents(id_agent);
			ArrayList<CObjectData> object_list_patient = MseeDataset.FindObjectDataList_or_parents(id_patient);

			if (time_data != null)
			{
				if (time_data.hasValidSceneTime() )
				{
					if (MseeFunction.objectSpecQuery)
					{	time_data.AddMargin_msec(MseeDataset.time_margin_object_spec);
					}
					else if (time_data.GetSceneTimeInterval_Msec() < MseeDataset.min_unmodified_time_interval)
					{	time_data.AddMargin_msec(MseeDataset.time_margin);
					}
				} 
			}
					
			if (	(object_list_agent.size() >0) && 
					(object_list_patient.size() >0) && 								
					(id_agent.compareTo(id_patient)!=0))
			{		
				boolean bTimeOkay1 = (MseeDataset.CheckObjectList_AtTimeLocation_AllowNull(object_list_agent, time_data, loc_data));
				boolean bTimeOkay2 = (MseeDataset.CheckObjectList_AtTimeLocation_AllowNull(object_list_patient, time_data, loc_data));
									
				boolean bTimeLocOkay = bTimeOkay1 && bTimeOkay2;									
				if (bTimeLocOkay)
				{
					// for online function; we only need to call once with the first item on the list, 
					b = SpatialFunctions.IsClearLineOfSight(time_data, loc_data, object_list_agent.get(0), object_list_patient.get(0));
				}
			}		

			
			/*
			CObjectData object_agent = MseeDataset.FindObjectData(id_agent);
			CObjectData object_patient = MseeDataset.FindObjectData(id_patient);
	
			if (object_agent == null)
			{
				System.err.println("WARN IsClearLineOfSight unable to find agent " + id_agent);
			} else if (object_patient == null)
			{
				System.err.println("WARN IsClearLineOfSight unable to find patient " +  id_patient);
			} else	
			{
				if (MseeFunction.bVerbose || bVerboseThis )
		    	{
					System.out.println("DEBUG IsClearLineOfSight agent id" + object_agent.id);
					System.out.println("DEBUG IsClearLineOfSight patient id" + object_patient.id);
		    	}			
				boolean bTimeLocOkay = (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_agent, time_data, loc_data) &&
										MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_patient, time_data, loc_data));
					
				if (bTimeLocOkay)
				{
					b = SpatialFunctions.IsClearLineOfSight(time_data, loc_data, object_agent, object_patient);
				}
			}	
			*/ 
			
		}
		
		
		if (MseeFunction.bVerbose || bVerboseThis )
    	{
			System.out.println("DEBUG IsClearLineOfSight ending " + b);
    	}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}

