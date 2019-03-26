package sparql;

import java.util.ArrayList;
import java.util.List;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.expr.nodevalue.XSDFuncOp ;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.function.FunctionBase4 ;
import com.hp.hpl.jena.sparql.util.Utils;

// reference
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/NodeValue.html
//		http://www.docjar.com/docs/api/com/hp/hpl/jena/sparql/expr/nodevalue/XSDFuncOp.html


public class IsAtTimeLocation extends FunctionBase
{
	// for debug
	static int ctr = 0; 
	
    @Override
    public void checkBuild(String uri, ExprList args)
    { 
        if ( args.size()  != 6 )
            throw new QueryBuildException("Function '"+Utils.className(this)+"' takes six arguments") ;
    }
    
	public IsAtTimeLocation() { super() ; }
	

    @Override
    public final NodeValue exec(List<NodeValue> args) 
    {

    	if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG IsAtTimeLocation running");
		}

        if ( args == null )
            // The contract on the function interface is that this should not happen.
            throw new ARQInternalErrorException(Utils.className(this)+": Null args list") ;
        
        if ( args.size() != 6 )
            throw new ExprEvalException(Utils.className(this)+": Wrong number of arguments: Wanted six, got "+args.size()) ;
        
        ArrayList<MseeFunction.ArgType > argType_list = new ArrayList<MseeFunction.ArgType>();
        ArrayList< String > argStr_list = new ArrayList< String >();
               
        
        for (int i = 0; i < args.size()/2 ; i++)
        {
        	argType_list.add( MseeFunction.GetArgType(args.get(2*i).getString()) );
        	argStr_list.add( MseeFunction.GetStringFromNodeValue(argType_list.get(i), 
        			args.get(2*i+1)));
       }
        /*
        if (MseeFunction.bVerbose)
        {
	        for (int i = 0; i < args.size()/2 ; i++)
	        {
	        	System.out.println("DEBUG IsAtTimeLocation i " + Integer.toString(i) 
	        			+ " argtype " + argType_list.get(i).toString());
	        }        
	        for (int i = 0; i < args.size()/2 ; i++)
	        {  	System.out.println("DEBUG IsAtTimeLocation i " + Integer.toString(i) 
	        			+ " string " + argStr_list.get(i) );
	        }
        }
        */ 
        
        // get index of argument by functions
        int index_entity = -1;
        int index_time = -1;
        int index_location = -1; 
        
        for (int i = 0; i < args.size()/2 ; i++)
        {	if (MseeFunction.IsEntityType(argType_list.get(i)))
        	{	index_entity = i;
        	} else if (MseeFunction.IsTimeType(argType_list.get(i)))
        	{	index_time = i;
        	} else if (MseeFunction.IsLocationType(argType_list.get(i)))
        	{	index_location = i;
        	}        
        }
                

		Boolean b = false;		
		
		if ((index_entity ==-1) || (index_time ==-1) || (index_location ==-1))
		{
			System.err.println("WARNING in IsAtTimeLocation, ARQInternalErrorException");			
		    // throw new ARQInternalErrorException(Utils.className(this)+": IsAtTimeLocation: unable to parse arguments") ;
	    }
		
		if (argStr_list.get(index_entity).contains("Unknown") == true)
		{
			System.out.println("DEBUG IsAtTime object is Unknown; return true;  " + argStr_list.get(index_entity) );
			b = true;
		}
		else
		{
			// check time condition 
			try {
				b = MseeDataset.IsEntityAtTime(
						argType_list.get(index_entity),
						argStr_list.get(index_entity),
						argType_list.get(index_time),
						argStr_list.get(index_time));
			} catch (MseeException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.err.println("WARNING in IsAtTimeLocation, IsEntityAtTime, ARQInternalErrorException");
			    // throw new ARQInternalErrorException(Utils.className(this)+": IsAtTimeLocation: GetIsEntityAtTime fails") ;
			}
			/*
			ctr++;
			//if (ctr % 20 ==0)
			{
	
				System.out.println("DEBUG in IsAtTimeLocation, ctr, " + ctr + " entity " + argStr_list.get(index_entity));
			}
			*/
			
			
			// check location condition (apply AND) 
			if (b)
			{
				
				try {
					b = MseeDataset.IsEntityAtLocation(
							argType_list.get(index_entity),
							argStr_list.get(index_entity),
							argType_list.get(index_location),
							argStr_list.get(index_location));
				} catch (MseeException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.err.println("WARNING in IsAtTimeLocation, IsEntityAtLocation, ARQInternalErrorException");
				    // throw new ARQInternalErrorException(Utils.className(this)+": IsAtTimeLocation: GetIsEntityAtLocation fails") ;
				}
				
			}
			
			if (b)
			{
				if (argType_list.get(index_entity) ==  ArgType.OBJECT_ID)
				{
					
		
					b = MseeDataset.IsObjectAtTimeLocation(
								argStr_list.get(index_entity),
								argType_list.get(index_time),
								argStr_list.get(index_time),
								argType_list.get(index_location),
								argStr_list.get(index_location));
							
				}
					
			}
		}
		
		/*
		// return 
		if ((MseeFunction.bVerbose) )
		{
			
			if (b)
			{
				ctr_true++;
				System.out.println("DEBUG IsAtTimeLocation ending " + b + " ctr_true " + ctr_true + " entity " + argStr_list.get(index_entity));
				
			} else
			{	System.out.println("DEBUG IsAtTimeLocation ending " + b );
			}
		}
*/
		
		return NodeValue.makeBoolean(b); 
    }    
}
