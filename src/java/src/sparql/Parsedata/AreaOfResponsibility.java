package sparql.Parsedata;

import sparql.LocationData;
import sparql.LocationData.LocationType;
import sparql.TimeData;
import sparql.TimeData.TimeDataType;

public class AreaOfResponsibility {
	
	TimeData timeData = null;
	
	LocationData locData = new LocationData(LocationType.CARTESIAN_METRIC_POLYGON);


	public void Reset() {
		timeData = null;
		locData = new LocationData(LocationType.CARTESIAN_METRIC_POLYGON);		
	} 
	
	public void SetSceneTime(String str_starttime, String str_endtime) {
		// TODO Auto-generated method stub
		timeData = new TimeData(TimeDataType.SCENE_CENTRIC_TIME, str_starttime, str_endtime);
		System.out.println("DEBUG AreaOfResponsibility:  SetSceneTime");
		timeData.PrintTimeData();
		
	}

	public void AddCartesianMetricPolygon(double x, double y) {
		locData.AddPointToCartesianMetricPolygon(x,y);
	}

	public TimeData GetSceneTime() {
		return this.timeData; 
	}

	public LocationData GetLocationData() {
		return this.locData;
	}


}
