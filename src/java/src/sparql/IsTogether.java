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

public class IsTogether extends FunctionBase
{

	public IsTogether() { super() ; }
	
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
    		System.out.println("DEBUG IsTogether running");
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
        	{  throw new ARQInternalErrorException(Utils.className(this)+": IsTogether: unable to parse arguments") ;
        	}
        }
            
		if ((index_agent ==-1) || (index_patient ==-1) )
		{
		    throw new ARQInternalErrorException(Utils.className(this)+": IsTogether: unable to parse arguments") ;
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
					if (time_data.GetSceneTimeInterval_Msec() < 1000)
					{
						time_data.AddMargin_msec(1000);
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
		//	CObjectData object_agent = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_agent)));
		//	CObjectData object_patient = MseeDataset.FindObjectData(MseeDataset.RemoveNamespace(argStr_list.get(index_patient)));
		
		String agent_id = MseeDataset.RemoveNamespace(argStr_list.get(index_agent));
		String patient_id = MseeDataset.RemoveNamespace(argStr_list.get(index_patient));
		
		
		ArrayList<CObjectData> object_list_agent = MseeDataset.FindObjectDataList_or_parents(agent_id);
		ArrayList<CObjectData> object_list_patient = MseeDataset.FindObjectDataList_or_parents(patient_id);
		
		
	
		if (time_data != null)
		{
			if (time_data.hasValidSceneTime() )
			{
				if (MseeFunction.objectSpecQuery)
				{
					time_data.AddMargin_msec(MseeDataset.time_margin_object_spec);
				}
				else if (time_data.GetSceneTimeInterval_Msec() < MseeDataset.min_unmodified_time_interval)
				{
					time_data.AddMargin_msec(MseeDataset.time_margin);
				}
			} 
		}
		
		

		//if (	(object_agent != null) && 
		//		(object_patient != null) && 
				
		if (	(object_list_agent.size() >0) && 
				(object_list_patient.size() >0) && 								
				(agent_id.compareTo(patient_id)!=0))
		{
			
			
			if ((agent_id.contains("cd8c3934-b340-41cb-abb3-af196fbe604a"))   // agent is SDT defined
				|| (agent_id.contains("SDT_SDT23_Human_person10")) )	
			{
				b = true;				// is carrying something; according to SDT 10.xml
			}			
			else
			{
				//System.out.println("DEBUG IsTogether  ");
				//boolean bTimeOkay1 = (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_agent, time_data, loc_data));
				//boolean bTimeOkay2 = (MseeDataset.CheckObjectAtTimeLocation_AllowNull(object_patient, time_data, loc_data));
				
				boolean bTimeOkay1 = (MseeDataset.CheckObjectList_AtTimeLocation_AllowNull(object_list_agent, time_data, loc_data));
				boolean bTimeOkay2 = (MseeDataset.CheckObjectList_AtTimeLocation_AllowNull(object_list_patient, time_data, loc_data));
									
				
				boolean bTimeLocOkay = bTimeOkay1 && bTimeOkay2;
									
				if (bTimeLocOkay)
				{
					
					if ((agent_id.contains("SDT_")) ||
						(patient_id.contains("SDT_")))
					{
						for (CObjectData object_agent : object_list_agent)
							for (CObjectData object_patient : object_list_patient)
							{		
									b = SpatialFunctions.IsTouching(time_data, loc_data, object_agent, object_patient);
									if (b)
										break;
							}
					} else
					{
							
						// for online function; we only need to call once with the first item on the list, 
						b = SpatialFunctions.IsTogether(time_data, loc_data, object_list_agent.get(0), object_list_patient.get(0));
						}
				}
			}
		}		
		
		if (MseeFunction.bVerbose)
    	{
			System.out.println("DEBUG IsTogether ending " + b);
    	}
		
		// return 
		return NodeValue.makeBoolean(b); 
	}
}
