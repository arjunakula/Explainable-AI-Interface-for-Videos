package sparql;

import java.sql.Date;
import java.util.ArrayList;




/**
 * @author mlee
 *
 * Cardinality Data is basically a set of time intervals for when a condition is valid
 * 
 *  in general, if there is more than 1 time intervals, the interval should not be overlapped, otherwise, the overlapping should be merged.
 */
public class CardinalityData {

	
	boolean valid_always = false;		//	always valid	, e.g. a stationary object with no time interval 
	boolean invalid_always = false; 	//  always invalid 	, e.g. when there is an object/event that is not found for a given ID.
	
	// if both above are false, then use the following time variable
	// list of time intervals. If there is more than one time interval, they should not intersect, otherwise, they should be merged
	ArrayList< TimeData > timeDataList = new ArrayList< TimeData >();
	
	public CardinalityData(TimeData timeData) {
		// TODO Auto-generated constructor stub
		// timeDatthis.time = new TimeData(timeData);		
		timeDataList.add(new TimeData(timeData));
	}


	public CardinalityData() {
		// TODO Auto-generated constructor stub
	}


	private void Reset() {
		// TODO Auto-generated method stub
		this.Set(new CardinalityData());
	}
	
	public CardinalityData(boolean b) {
		// TODO Auto-generated constructor stub
		if (b == true)
		{	
			SetAlwaysValid();
		} else
		{
			SetAlwaysInvalid();
		}			
	}


	private void Set(CardinalityData c_data) {
		// TODO Auto-generated method stub
		this.valid_always = c_data.valid_always;
		this.invalid_always = c_data.invalid_always;
		
		timeDataList.clear();		

		for (TimeData data : c_data.timeDataList)
		{	this.timeDataList.add(new TimeData(data));
		}
	}

	public void SetAlwaysInvalid() {
		invalid_always = true;
		valid_always = false;		
		timeDataList.clear();
	}

	public void SetAlwaysValid() {
		invalid_always = false;
		valid_always = true;		
		timeDataList.clear();
		
	}

	public void AddTimeData(TimeData new_data) {
		// TODO Auto-generated method stub
		this.timeDataList.add(new TimeData(new_data));
	}


	private boolean IsAlwaysInvalid() {		
		return (invalid_always == true);
	}	


	private boolean IsAlwaysValid() {
		return (valid_always == true);
	}


	public void Intersect(CardinalityData c_data) {
		
		// do intersection over two set of intervals
		
		// if any set is always invalid, then, the intersection is always invalid
		if (invalid_always)
			return; 
		
		if (c_data.IsAlwaysInvalid())
		{
			this.SetAlwaysInvalid(); 
			return;
		}
		
		if (c_data.IsAlwaysValid())
		{
			// arg is always valid; do nothing
			return; 				
		} 
			
		
		if (this.IsAlwaysValid())
		{
			this.Set(c_data);		// replace this object by the argument object			 			
		} else
		{
			// do pairwise intersection							
			CardinalityData tmp_data = new CardinalityData(); // temporary variable to hold intersected time intervals
			
 			for ( TimeData data1 : this.timeDataList )
				for ( TimeData data2 : c_data.timeDataList )
				{	
					// check if the two time data have intersection; if so, add to the new data
					TimeData new_data = TimeFunctions.GetSceneTimeIntersect(data1, data2);
					if (new_data != null)
					{
						tmp_data.AddTimeData(new_data);
					}
				}
 			
 			// after adding pair wise intersection, the order might be unsorted
 			// old tmp_data.SortTimeData();
 			TimeFunctions.SortTimeData(tmp_data.timeDataList); 			
 			
			this.Set(tmp_data);  // replace this object with the temp data 		}		
		}
	}


	public void Union(CardinalityData data) {
		// TODO Auto-generated method stub
		if (this.IsAlwaysValid())
		{	// nothing to do
			return;
		}
		
		if (this.IsAlwaysInvalid())
		{
			this.Set(data);
			return; 
		}
		
		if (data.IsAlwaysValid())
		{
			this.SetAlwaysValid();
			return; 
		}
				
		if (data.IsAlwaysInvalid())
		{	// nothing to do
			return;
		}
		
		// at this point; we have to look at the timedata
		if (this.timeDataList.isEmpty())
		{
			this.Set(data);
			return;
		}
		
		if (data.timeDataList.isEmpty())
		{	// nothing to do
			return;
		}
		
		// merge the list first
		for (TimeData t_data : data.timeDataList)
		{
			this.AddTimeData(t_data);
		}
		
		// sort by start time, and cleanup
		SortAndMergeOverlappingTimeData();
	}



	public void Negation(CardinalityData data) {
		// TODO Auto-generated method stub
		
		TimeData sceneTimeRange = MseeDataset.GetDatasetSceneTimeRange();
		
		if (data.IsAlwaysInvalid())
		{
			this.Reset();
			this.AddTimeData(sceneTimeRange);
			return;			
		}
		if (data.IsAlwaysValid())
		{
			this.SetAlwaysInvalid();
			return;			
		}	
		
		if (data.timeDataList.isEmpty())
		{	this.Reset();
			this.AddTimeData(sceneTimeRange);
			return;	
		}

		this.Reset();
		
		// use date_m to keep track of when the next valid time interval should starts
		Date date_m = (Date) sceneTimeRange.mSceneTime_Start.clone();
		
		for (TimeData d : data.timeDataList)
		{
			if (d.hasValidSceneTime() == false)
				continue;
			if (d.mSceneTime_Start.compareTo(d.mSceneTime_End) >=0)
			{
				// either the time is an instance or has invalid time interval, ignore
				continue;
			}		
			
			if (date_m.compareTo(d.mSceneTime_Start) >= 0 )
			{
				// date_m meets with (or is later) than the start of the time interval
				// no need to create a new time interval, start search from the end
				date_m = (Date) TimeFunctions.MaxDate(date_m, d.mSceneTime_End);
				continue;
			} else
			{
				// here, d starts after date_m, so we can create a new valid time_interval
				TimeData new_time = new TimeData();
				new_time.SetSceneTimePeriod(date_m, d.mSceneTime_Start);
				this.AddTimeData(new_time);
				
				// start search again from mSceneTime_End
				date_m = (Date) d.mSceneTime_End.clone();
			}
		}
		
		// check if we need to add one last time interview
		if (date_m.compareTo(sceneTimeRange.mSceneTime_End) < 0 )
		{
			TimeData new_time = new TimeData();
			new_time.SetSceneTimePeriod(date_m, sceneTimeRange.mSceneTime_End);
			this.AddTimeData(new_time);			
		}
	}


	public void Print() {
		if (this.valid_always)
		{	System.out.println("\t invalid_always");
		}
		if (this.invalid_always)
		{	System.out.println("\t invalid_always");
		}
		for (TimeData t :this.timeDataList)
		{
			System.out.println("\t time " + t.mSceneTime_Start.toString() + " time " + t.mSceneTime_End.toString());
		}	
	}


	public void SortAndMergeOverlappingTimeData() {
		// TODO Auto-generated method stub
		TimeFunctions.SortAndMergeOverlappingTimeData(this.timeDataList);
		
	}
}
