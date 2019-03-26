package sparql;

import java.util.ArrayList;
import java.util.Date;

import sparql.MseeFunction.ArgType;

import com.hp.hpl.jena.sparql.expr.ExprList;

public class SetQuantity implements TemporalRelationChildNode {

	// the set of time intervals for each the set quantity condition is valid
	ArrayList< TimeData > timeDataList = new ArrayList< TimeData >();

	CardinalityRecord cardinalityRecord = null;

	String setName ="";
	
	ArrayList< QuantityCondition > quantityConditionList = new ArrayList< QuantityCondition >();
	
	
	/*  this is obsolete, to be replaced by quantityConditionList
	double minQuantityCondition = -1;
	double maxQuantityCondition = -1;
	double gtQuantityCondition = -1;
	double ltQuantityCondition = -1;
	double notQuantityCondition = -1;
	
	*/ 
	
		
	
	public void FindSet(String set_name) {
		this.setName = set_name;
		
		cardinalityRecord = CardinalityRecordList.FindCardinalityRecord(setName);
		
		if (cardinalityRecord==null)
		{
			System.err.println("WARN SetQuantity FindSet SET not found " + set_name);
			
		} else
		{	System.out.println("DEBUG SetQuantity FindSet SET found " + set_name + " cardinalityData " + cardinalityRecord.cardinalityDataList.size());
		}
		 		
	}
		/*  this is obsolete, to be replaced by quantityConditionList
	public void SetMinQuantity(String string) {
		System.out.println("DEBUG minQuantityCondition " + string + " " + minQuantityCondition);
		minQuantityCondition = Double.parseDouble(string);		
		System.out.println("DEBUG minQuantityCondition " + string + " " + minQuantityCondition);
	}
	
	public void SetMaxQuantity(String string) {
		System.out.println("DEBUG maxQuantityCondition " + string + " " + maxQuantityCondition);
		maxQuantityCondition = Double.parseDouble(string);		
		System.out.println("DEBUG maxQuantityCondition " + string + " " +  maxQuantityCondition);
	}
	

	public void SetGtQuantity(String string) {
		System.out.println("DEBUG gtQuantityCondition " + string + " " + gtQuantityCondition);
		gtQuantityCondition = Double.parseDouble(string);		
		System.out.println("DEBUG gtQuantityCondition " + string + " " +  gtQuantityCondition);
	}	
	

	public void SetLtQuantity(String string) {
		System.out.println("DEBUG ltQuantityCondition " + string + " " + ltQuantityCondition);
		ltQuantityCondition = Double.parseDouble(string);		
		System.out.println("DEBUG ltQuantityCondition " + string + " " +  ltQuantityCondition);
	}	
	
	public void SetNotQuantity(String string) {
		System.out.println("DEBUG notQuantityCondition " + string + " " + notQuantityCondition);
		notQuantityCondition = Double.parseDouble(string);		
		System.out.println("DEBUG notQuantityCondition " + string + " " +  notQuantityCondition);
	}	
	*/
	

	
	// TODO Test this with sample query 
	public int GetNumCardinalityRecord()
	{
		if (this.cardinalityRecord == null)
			return 0; 
		return cardinalityRecord.cardinalityDataList.size();			
	}

	public TimeData GetTimeRange() {		
		if (this.timeDataList.isEmpty())
			return null;
		
		TimeData data = this.timeDataList.get(0).clone();
		for (TimeData t : this.timeDataList)
		{
			data.ExtendSceneTimeRange(t);		
		}		
		return data;
	}

	/*
	 * get list of date samples
		 sort date, remove duplicate
	 	 set count to zero
		 go through the cardinality data
		 add count to time interval
		 apply conditions to time interval
		 get merge/trim time interval 
		 
		 result is put into class member: timeDataList
		
	 */
	public void ApplyQuantitiesConditions() {
		// TODO Auto-generated method stub
		
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG SetQuantity: ApplyQuantitiesConditions running" );
		}
		
		timeDataList.clear();	

		
		if (this.cardinalityRecord == null)
		{
			System.err.println("Warn SetQuantity " + this.setName + "Cardinality Record not found.");
			return; 
		}
		
		// get list of date samples
		ArrayList<Date> date_list_unsorted = new ArrayList<Date>();
		for (CardinalityData c_data: this.cardinalityRecord.cardinalityDataList)
		{
			for (TimeData t : c_data.timeDataList)
			{
				if ((t == null) ||
					(t.hasValidSceneTime() == false))
				{	System.err.println("ERROR SetQuantity; invalid scene time");
					continue;
				}
				date_list_unsorted.add((Date) t.mSceneTime_Start.clone());
				date_list_unsorted.add((Date) t.mSceneTime_End.clone());
			}
		}
		
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG SetQuantity: ApplyQuantitiesConditions sort date starting" );
		}
		
		// sort date, 
		ArrayList<Date> date_list_sorted = new ArrayList<Date>();		// put sorted dates here
		while ( date_list_unsorted.size() >0)
		{
			// get the earliest date from the remaining list
			int m = 0; 
			int n = m+1;
			while (n < date_list_unsorted.size())
			{
				Date m_date = date_list_unsorted.get(m);
				Date n_date = date_list_unsorted.get(n);
				
				if (m_date.compareTo(n_date) ==0)
				{	// duplicate date; 
					// do not remove; because time interval can be instant; do nothing instead
						// date_list_unsorted.remove(n);
					n++;
				} else if (m_date.compareTo(n_date) > 0)
				{
					m = n; // earlier date
					n++;								
				} else
				{
					n++;
				}
			}
			date_list_sorted.add((Date) date_list_unsorted.get(m).clone());		
			date_list_unsorted.remove(m);
		}
		
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG SetQuantity: ApplyQuantitiesConditions sort date ending" );
		}
		/*
		// debug check sorting
		for (Date d : date_list_sorted)
		{
			System.out.println("DEBUG sort date " + d.toString());
		}
		*/
		
		// the number of time interval is one less than the number of time stamps
		int ctr_interval= date_list_sorted.size()-1;		

		if (ctr_interval>0)
		{
			// set count to zero
			int[] countArray = new int[ctr_interval];
			boolean[] validArray  = new boolean[ctr_interval];
			for (int i = 0; i < ctr_interval; i++ )
			{
				countArray[i] =0; 
				validArray[i] = false;
			}
			
	
			// go through the cardinality data, add count to time interval
			for (int i = 0; i < ctr_interval; i++ )
			{
				for (CardinalityData c_data: this.cardinalityRecord.cardinalityDataList)
				{
					for (TimeData t : c_data.timeDataList)
					{
						if ((t == null) ||
							(t.hasValidSceneTime() == false))
						{	System.err.println("ERROR SetQuantity; invalid scene time");
							continue;
						}
						if ((t.mSceneTime_Start.compareTo(date_list_sorted.get(i)) <=0) &&
							(t.mSceneTime_End.compareTo(date_list_sorted.get(i+1)) >=0))
						{
							countArray[i]++;
						}					
					}
				}
			}
			
			// apply conditions to time interval
			for (int i = 0; i < ctr_interval; i++ )
			{
				// set true first
				validArray[i] = true;
				
				for ( QuantityCondition q :this.quantityConditionList)
				{
					// if any one condition fails, then set to false
					if ( q.ApplyCondition(countArray[i]) == false)
					{
						validArray[i] = false;
						break; 
					}
				}
	
			}
			
			// update timeDataList
			Date new_start = null;
			Date new_end = null;	
			
			for (int i = 0; i < ctr_interval; i++ )
			{	if (validArray[i])
				{
					if (new_start == null)
					{	// start a new interval
						new_start = date_list_sorted.get(i);
						new_end = date_list_sorted.get(i+1);
					} else
					{
						// extend the current interval
						new_end = date_list_sorted.get(i+1);
					}					
				}
			
				if ((i == ctr_interval -1) || (validArray[i]== false))
				{
					if (new_start != null)
					{	// push
						TimeData t = new TimeData();
						t.SetSceneTimePeriod(new_start, new_end);
						this.timeDataList.add(t);
						new_start= null;
						new_end= null;					
					}
				}
			}
		} // if
		
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG SetQuantity: ApplyQuantitiesConditions ending" );
		}
		
	}

	public ArrayList< TimeData > GetTimeDataList() {
		// TODO Auto-generated method stub
		return this.timeDataList;
	}

	public int ParseArgList(ArrayList<ArgType> argType_list,
			ArrayList<String> argStr_list, int l1) {
		// TODO Auto-generated method stub
		
		if (l1 >= argType_list.size())
			return l1;
		
		int l2 = l1; 
		
		// first arg must be the set name		
		if (argType_list.get(l2) == ArgType.ARG_SET_NAME)
		{
			FindSet(argStr_list.get(l2));
			l2++;
		} else
		{
			System.err.println("ERROR SetQuantity:ParseArgList first arg is not ARG_SET_NAME");
			return l2; 			
		}
		
		
		while (l2 < argType_list.size())
		{
			if (	(argType_list.get(l2) == ArgType.ARG_MIN_QUANTITY) ||
					(argType_list.get(l2) == ArgType.AGR_MAX_QUANTITY) ||
					(argType_list.get(l2) == ArgType.AGR_GT_QUANTITY) ||
					(argType_list.get(l2) == ArgType.AGR_LT_QUANTITY) ||
					(argType_list.get(l2) == ArgType.AGR_NOT_QUANTITY)
				)
    		{	
				QuantityCondition q = (new QuantityCondition());
				
				l2 = q.ParseArgList(argType_list, argStr_list,  l2);
				
				this.quantityConditionList.add(q);				
    		} else
    		{
    			// unhandled arguement type
    			return l2; 	
    		}
		}
			
		/* this is replaced by above
		while (l2 < argType_list.size())
		{
			switch (argType_list.get(l2))
			{
	    		case ARG_MIN_QUANTITY:
	    			SetMinQuantity(argStr_list.get(l2));
	    			l2++;
	    			break;
	    		case AGR_MAX_QUANTITY:
	    			SetMaxQuantity(argStr_list.get(l2));
	    			l2++;
	    			break;	    			
	    		case AGR_GT_QUANTITY:
	    			SetGtQuantity(argStr_list.get(l2));
	    			l2++;
	    			break;	    			
	    		case AGR_LT_QUANTITY:
	    			SetLtQuantity(argStr_list.get(l2));
	    			l2++;
	    			break;	    			
	    		case AGR_NOT_QUANTITY:
	    			SetNotQuantity(argStr_list.get(l2));
	    			l2++;
	    			break;
	    		default:
	    			// unhandled arguement type
	    			return l2; 	
			}
		}
		 */ 
		
		return l2;
	}

}
