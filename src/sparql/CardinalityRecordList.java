package sparql;

import java.util.ArrayList;


public class CardinalityRecordList {
	
	public static  ArrayList< CardinalityRecord > cardinalityRecordList = new ArrayList< CardinalityRecord >(); 
	
	public static int debug_ctr = 0; 

	public static   void InsertRecordCardinality(RecordCardinalityNode rcNode) {
		// static  ArrayList< CardinalityRecord > cardinalityRecordList = new ArrayList< CardinalityRecord >();
		
		int index = -1; 
		index = GetIndexCardinalityRecord(rcNode.setName) ;
		if (index == -1)
		{
			cardinalityRecordList.add(new CardinalityRecord(rcNode));		
		} else
		{
			cardinalityRecordList.get(index).AddNode(rcNode);
		}
	}

	public static   int GetIndexCardinalityRecord(String setName) {
			
			for (CardinalityRecord record : cardinalityRecordList)
			{
				if (record.record_name.compareTo(setName)==0)
				{
					return cardinalityRecordList.indexOf(record);
				}			
			}		 
			
			return -1;
		}
	
		public static CardinalityRecord FindCardinalityRecord(String setName) {
			
			for (CardinalityRecord record : cardinalityRecordList)
			{
				if (record.record_name.compareTo(setName)==0)
				{
					return record;
				}			
			}		
			return null;
		}

		// reset the cardinality record; this should be called before the start of each query;
		public static void Reset() {
			cardinalityRecordList.clear();
			debug_ctr = 0;
			
		}
}
