package sparql;

import java.util.ArrayList;

import sparql.MseeFunction.ArgType;

public class QuantityCondition {

	public enum QuantityConditionType {
		MIN,
		MAX,
		GT,
		LT,
		NOT,
		UNKNOWN,
	};
	
	final static String STR_COUNT_SET = "COUNT_SET";
	
	QuantityConditionType quantityConditionType = QuantityConditionType.UNKNOWN;
	
	private boolean bCountSet = false;

	private double quantityArgument = -1 ;

	private SetQuantity setQuantity = null; 		// this is used under "COUNT_SET" condition, where the quantity comparison is to be applied to the size of another set. 
	
	public int ParseArgList(ArrayList<ArgType> argType_list,
			ArrayList<String> argStr_list, int l1) {
		// TODO Auto-generated method stub
		
		if (l1 >= argType_list.size())
			return l1;
		
		int l2 = l1; 
		/*
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
		*/ 
		
		switch (argType_list.get(l2))
		{
			case ARG_MIN_QUANTITY:
				quantityConditionType = QuantityConditionType.MIN;
				break; 
			case AGR_MAX_QUANTITY:
				quantityConditionType = QuantityConditionType.MAX;
				break; 
			case AGR_GT_QUANTITY:
				quantityConditionType = QuantityConditionType.GT;
				break; 
			case AGR_LT_QUANTITY:
				quantityConditionType = QuantityConditionType.LT;
				break; 
			case AGR_NOT_QUANTITY:
				quantityConditionType = QuantityConditionType.NOT;
				break; 
			default:
				System.err.println("ERROR QuantityCondition ParseArgList invalid first arg: " + argType_list.get(l2));
				return l2;								
		}
		
		if (argStr_list.get(l2).compareTo(STR_COUNT_SET)==0)
		{
			// this is a COUNT_SET condition, where the argument of the quantity comparison is a set			
			this.bCountSet = true;
			
			// go to next argument to get the set name
			l2++;
			
			if (argType_list.get(l2) != ArgType.ARG_SET_NAME)
			{
				System.err.println("ERROR QuantityCondition ParseArgList expect SET_NAME: but get instead " + argType_list.get(l2) );
				return l2;
			} else
			{
				this.setQuantity = new SetQuantity();
				
    			l2 = this.setQuantity.ParseArgList(argType_list, argStr_list,  l2);
			}
						
		} else
		{
			this.bCountSet = false; 
			this.quantityArgument = Double.parseDouble(argStr_list.get(l2));	
			l2++;
		}
				
		return l2;
	}

	
	public boolean ApplyCondition(double i) {
		
		double arg = this.quantityArgument; 
		if (bCountSet == true)
		{	// use set as the comparison 
			
			if (this.setQuantity == null) 
			{	// set has not been found, hence, count is zero  
				arg = 0; 
			} else
			{
				//TODO to set with sample data. 
				// assign count as the number of records in the set 
				arg = this.setQuantity.GetNumCardinalityRecord();
			}			
		}
		
		switch (quantityConditionType )
		{
			case MIN:
				return i >= arg; 
			case MAX:
				return i <= arg;
			case GT:
				return i > arg;
			case LT:
				return i < arg;
			case NOT:
				return i != arg;				
			default:
				System.err.println("ERROR QuantityCondition ApplyCondition invalid quantityConditionType: " + quantityConditionType);
				return false;												
		}		
	}
}
