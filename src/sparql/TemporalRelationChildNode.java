package sparql;

import java.util.ArrayList;

import sparql.MseeFunction.ArgType;

public interface TemporalRelationChildNode {

	int ParseArgList(ArrayList<ArgType> argType_list,
			ArrayList<String> argStr_list, int iarg);

	void ApplyQuantitiesConditions();

	ArrayList< TimeData >  GetTimeDataList();

	TimeData GetTimeRange();
	

}
