package sparql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import TextParser.TimeParser;
import sparql.LocationData.LocationType;
import sparql.MseeFunction.ArgType;
import sparql.Parsedata.View;

public class TimeData {
	
	public enum TimeDataType { 
		VIEW_CENTRIC_TIME,
		SCENE_CENTRIC_TIME};
		
	// TimeDataType mType = TimeDataType.VIEW_CENTRIC_TIME;
	public boolean mbValidViewFrame = false; 
	public int mViewFrame_Start = -1; 
	public int mViewFrame_End = -1;
	public String mViewId  = "";
	
	private boolean mbValidSceneTime = false;
	public Date mSceneTime_Start = new Date();
	public Date mSceneTime_End = new Date();
	
	// IMPORTANT, UPDATE public void Set(TimeData timeData) WHEN ADDING NEW MEMBER

	public TimeData() {
		// TODO Auto-generated constructor stub
	}
	
	public TimeData(TimeDataType type, String string) throws MseeException {
		// TODO Auto-generated constructor stub
		if (type == TimeDataType.VIEW_CENTRIC_TIME)
		{
			SetViewFramePeriod(string);
		}
		if (type == TimeDataType.SCENE_CENTRIC_TIME)
		{
			SetSceneTimePeriod(string);
		}
	}


	public TimeData(String timeDataStr) throws MseeException {
		
		int i =  timeDataStr.indexOf(',');
		if (i == -1)
		{
			System.err.println("DEBUG ERROR Invalid timeDataStr, expects a commas" + timeDataStr);
			return; 
		}
		
		String str_type = timeDataStr.substring(1,i-1);
		String str_value = timeDataStr.substring(i+2,timeDataStr.length()-1);
		
		ArgType argType = MseeFunction.GetArgType(str_type );
		

		switch (argType)
		{
			case VIEW_CENTRIC_TIME_PERIOD:
				SetViewFramePeriod(str_value);
				break;
			case SCENE_CENTRIC_TIME_PERIOD:
				SetSceneTimePeriod(str_value);
				 break;
					 
			default:
				System.err.println("ERROR TimeData Set  invalid argtype " + argType.toString());
				throw new MseeException("ERROR TimeData Set invalid argtype " + argType.toString());				 
		}		
		
	}
	
	

	public TimeData(TimeData timeData) {
		// TODO Auto-generated constructor stub
		this.Set(timeData);
	}
	

	public TimeData(TimeDataType type, String str_starttime, String str_endtime) {
		// TODO Auto-generated constructor stub
		if (type == TimeDataType.VIEW_CENTRIC_TIME)
		{
			SetViewFramePeriod(str_starttime, str_endtime);
		}
		if (type == TimeDataType.SCENE_CENTRIC_TIME)
		{
			SetSceneTimePeriod(str_starttime, str_endtime);
		}
	}



	public TimeData clone() {
		return new TimeData(this);
	}

	
	public void Set(TimeData timeData) {
		// TODO Auto-generated constructor stub
		this.mbValidViewFrame = timeData.mbValidViewFrame;
		this.mViewFrame_Start = timeData.mViewFrame_Start;
		this.mViewFrame_End = timeData.mViewFrame_End;
		this.mViewId = timeData.mViewId;
		
		this.mbValidSceneTime = timeData.mbValidSceneTime;
		this.mSceneTime_Start = (Date) timeData.mSceneTime_Start.clone();
		this.mSceneTime_End = (Date) timeData.mSceneTime_End.clone();		
	}

	public void SetViewFramePeriod(int start, int end)
	{
		// mType = TimeDataType.VIEW_CENTRIC_TIME;
		mViewFrame_Start = start;
		mViewFrame_End = end; 
		
		mbValidViewFrame = true;
	}
	
	private void SetViewFramePeriod(String str_starttime, String str_endtime) {
		SetViewFramePeriod(Integer.parseInt(str_starttime), Integer.parseInt(str_endtime));
		
	}
	

	public void SetViewFramePeriod(int start, int end, String str_view_id)
	{
		mViewId = str_view_id;
		SetViewFramePeriod(start,end);
		this.SetSceneTimeFromViewFrame();
	}
	
	private void SetSceneTimeFromViewFrame() {
		
		View view = MseeDataset.GetView(mViewId);
		if (view == null)
		{
			System.out.println("ERROR SetSceneTimeFromViewFrame view not found: " + mViewId);
			return; 
		}
		
		// TODO test this code
		Date sceneTime_Start = view.GetSceneTimeStart();
		Date sceneTime_End = (Date) sceneTime_Start.clone();
		
		sceneTime_Start.setTime(sceneTime_Start.getTime() + Math.round(1000.0* mViewFrame_Start / view.GetFrameRate()));
		sceneTime_End.setTime(sceneTime_End.getTime() + Math.round(1000.0 * mViewFrame_End / view.GetFrameRate()));
		
		SetSceneTimePeriod(sceneTime_Start, sceneTime_End);		
	}
	

	public void ComputeViewTimeFromSceneTime(
			Date scene_start_time,
			Double frame_rate) {
		
		
		double second_1 = (mSceneTime_Start.getTime()-scene_start_time.getTime())/1000;		
		Double frame_1 =frame_rate * second_1;
		
		double second_2 = (mSceneTime_End.getTime()-scene_start_time.getTime())/1000;
		Double frame_2 =frame_rate * second_2;		

		SetViewFramePeriod(frame_1.intValue(), frame_2.intValue());			
	}
	

	public void SetViewFramePeriod(String str_frame_period) throws MseeException
	{
		String [] str_arr=  str_frame_period.split(";");		
		if (str_arr.length == 1)
		{	int i = Integer.parseInt(str_frame_period); 
			SetViewFramePeriod(i,i);
		} else if (str_arr.length == 2 )
		{	SetViewFramePeriod(Integer.parseInt(str_arr[0]), Integer.parseInt(str_arr[1]));
		}  else if (str_arr.length == 3 )
		{	SetViewFramePeriod(Integer.parseInt(str_arr[0]), Integer.parseInt(str_arr[1]), str_arr[2]);
		} else
		{	
			System.out.println("ERROR SetSceneTimePeriod unable to parse time string " + str_frame_period);
			throw new MseeException("ERROR SetSceneTimePeriod unable to parse time string " + str_frame_period);
		
		}	
	}
	

	public void SetSceneTimePeriod(Date sceneTime_Start, Date sceneTime_End) {
		// TODO Auto-generated method stub
		
		if ((sceneTime_Start == null) || (sceneTime_Start == null))
		{
			System.err.println("ERROR SetSceneTimePeriod, input is null");
			return; 
		}
		
		mSceneTime_Start = (Date) sceneTime_Start.clone();
		mSceneTime_End = (Date) sceneTime_End.clone();

		mbValidSceneTime = true;
	}
	
	// reference:
	// http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
	//
	// 2013-09-04T14:53:28.000Z
	/*
	    <StartTime>2013-09-04T14:53:28.000Z</StartTime>
	    <EndTime>2013-09-04T14:53:34.000Z</EndTime>
	*/
	public void SetSceneTimePeriod(String start_time, String end_time) 
	{
		// mType = TimeDataType.SCENE_CENTRIC_TIME ;

		try {
			mSceneTime_Start = TimeParser.getTimeParser().parse(start_time);
		} catch (ParseException e) {
			e.printStackTrace();				
			System.err.println("ERROR SetSceneTimePeriod unable to parse time string " + start_time);
		}
		try {
			mSceneTime_End = TimeParser.getTimeParser().parse(end_time);
		} catch (ParseException e) {
			e.printStackTrace();				
			System.err.println("ERROR SetSceneTimePeriod unable to parse time string " + end_time);
		}
		
		mbValidSceneTime = true;
	}	
	
	public void SetSceneTimePeriod(String str_time)
	throws MseeException
	{
		String [] str_arr=  str_time.split(";");
		
		if (str_arr.length == 1)
		{
			SetSceneTimePeriod(str_time, str_time);
		} else if (str_arr.length == 2 )
		{
			SetSceneTimePeriod(str_arr[0], str_arr[1]);
		} else
		{
			System.out.println("ERROR SetSceneTimePeriod unable to parse time string " + str_time);
			
			throw new MseeException("ERROR SetSceneTimePeriod unable to parse time string " + str_time);
		}		
	}

	/**
	 * Returns a string that uniquely identifies this particular time interval.  For a different time interval (so long as it is valid), this method should return a different string.
	 * @return
	 */
	public String getUniqueStringDescription( ) {		
		// TODO Auto-generated method stub
		if (mbValidViewFrame)
		{
			return "VIEW_CENTRIC_TIME " + 
					mViewFrame_Start + "; " + 
					mViewFrame_End;
		}
			
		if (hasValidSceneTime())
		{
			return "SCENE_CENTRIC_TIME " +
					mSceneTime_Start.toString() + "; " + 
					mSceneTime_End.toString();
		} else
		{
			return "\t\t\t mbValidSceneTime is false ";		
		}
	}
	
	public void PrintTimeData() {
		System.out.println( getUniqueStringDescription() );
	}

	public boolean hasValidSceneTime() {
		// TODO Auto-generated method stub
		return ((this.mbValidSceneTime) &&
				(this.mSceneTime_Start != null) &&
				(this.mSceneTime_End != null));		
		
	}

	
	public void ExtendSceneTimeRange(TimeData t) {
		if (	(t == null) ||
				(this.hasValidSceneTime() == false) ||
				(t.hasValidSceneTime() == false))
		{
			System.err.println("ERROR TimeData ExtendSceneTimeRange");
			return; 
		}
	
		if (t.mSceneTime_Start.compareTo(this.mSceneTime_Start) < 0)
		{
			this.mSceneTime_Start = t.mSceneTime_Start;
		}
		
		if (t.mSceneTime_End.compareTo(this.mSceneTime_End) > 0)
		{
			this.mSceneTime_End = t.mSceneTime_End;
		}
	}

	
	public long GetSceneTimeInterval_Msec() {		
		if (hasValidSceneTime() == false)
		{
			System.err.println("ERROR GetSceneTimeInterval_Msec:  Invalid Scene Time");
			return 0;
		}
		
		return mSceneTime_End.getTime() - mSceneTime_Start.getTime();		
	}

	public void AddMargin_msec(int msec) {
		// TODO Auto-generated method stub
		if (mSceneTime_Start!= null)
		{
			this.mSceneTime_Start.setTime(this.mSceneTime_Start.getTime() - msec);
		}
		if (mSceneTime_End!= null)
		{	
			this.mSceneTime_End.setTime(this.mSceneTime_End.getTime() + msec);
		}
			
	}

}
