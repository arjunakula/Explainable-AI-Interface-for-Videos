package sparql;

import java.util.ArrayList;

public class CardinalityRecord {

	
	String record_name;
	ArrayList< CardinalityData > cardinalityDataList = new ArrayList< CardinalityData >();
	
	
	public CardinalityRecord(RecordCardinalityNode rcNode) {
		
		// this is a new record; so log the name
		this.record_name = rcNode.setName;
		AddNode(rcNode);				
	}

	public void AddNode(RecordCardinalityNode rcNode) {
		
		cardinalityDataList.add(rcNode.cardinalityData);		
	}		
}
