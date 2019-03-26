package sparql;

import java.util.ArrayList;
import java.util.Date;

import TextParser.CEventData;
import sparql.TimeData;
import sparql.ColorFunctions.Color;
import sparql.TimeFunctions.TemporalRelation;

/**
 * @author mlee
 *
 *
 *
 */

/* reference
 * 
 * to get difference between time (msec)
 * 
 * 	long diff = t1.mSceneTime_End.getTime() - t2.mSceneTime_Start.getTime();
 * 
 */
public class TimeFunctions {

	
	public static enum TemporalRelation { 
		PRECEDES, 
		MEETS,
		OVERLAPS,
		FINISHED_BY,
		CONTAINS,
		STARTS, 
		EQUALS,
		BEFORE,		
		STRICTLY_BEFORE,
		AFTER, 
		SAME_TIME_AS,
		UNKNOWN }

	/**
	 * if a time difference between two events is within this value, it is considered as meeting.
	 */
	private static final long TIME_MEETS_TOLERANCE_MSEC = 500;
	private static final int TIME_MEETS_TOLERANCE_FRAME = 15;
		

	public static TemporalRelation GetTemporalRelation(String string) {
		if (string.equalsIgnoreCase("PRECEDES")) {  return TemporalRelation.PRECEDES; }
		if (string.equalsIgnoreCase("MEETS")) {  return TemporalRelation.MEETS; }
		if (string.equalsIgnoreCase("OVERLAPS")) {  return TemporalRelation.OVERLAPS; }
		if (string.equalsIgnoreCase("FINISHED_BY")) {  return TemporalRelation.FINISHED_BY; }
		if (string.equalsIgnoreCase("CONTAINS")) {  return TemporalRelation.CONTAINS; }
		if (string.equalsIgnoreCase("STARTS")) {  return TemporalRelation.STARTS; }
		if (string.equalsIgnoreCase("EQUALS")) {  return TemporalRelation.EQUALS; }
		if (string.equalsIgnoreCase("BEFORE")) {  return TemporalRelation.BEFORE; }
		if (string.equalsIgnoreCase("STRICTLY_BEFORE")) {  return TemporalRelation.STRICTLY_BEFORE; }
		if (string.equalsIgnoreCase("AFTER")) {  return TemporalRelation.AFTER; }
		if (string.equalsIgnoreCase("SAME_TIME_AS")) {  return TemporalRelation.SAME_TIME_AS; }
		
		System.err.println("ERROR GetTemporalRelation unrecognized type " + string);
		
		return TemporalRelation.UNKNOWN;	
	}	
	
		
	public static Boolean IsAtTime(TimeData time1, TimeData time2) 
	{
		Boolean b = false;
		
		if ((time1.hasValidSceneTime()) && (time2.hasValidSceneTime()))
		{	
			/* Date.compareTo()
			 * 
			 * Returns:
					the value 0 if the argument Date is equal to this Date; 
					a value less than 0 if this Date is before the Date argument; 
					and a value greater than 0 if this Date is after the Date argument. 
			 */
			if ((	(time1.mSceneTime_Start.compareTo(time2.mSceneTime_End) > 0) || 
					(time2.mSceneTime_Start.compareTo(time1.mSceneTime_End) > 0) ))
			{
				b = false;
			} else
			{
				/*
				System.err.println("WARNING in TimeFunctions, IsAtTime,  true for scene time" );
				System.err.println("\t time1 \t" );
				time1.PrintTimeData();
				System.err.println("\t time2 \t" );
				time2.PrintTimeData();
				*/
				b = true; 
			} 
		}	else
		{
				
			if (time1.mbValidViewFrame && time2.mbValidViewFrame )
			{
				if (	(time1.mViewFrame_Start > time2.mViewFrame_End) ||
						(time2.mViewFrame_Start > time1.mViewFrame_End) 
							)
					{
						// false
					} else
					{
						System.err.println("WARNING in TimeFunctions, IsAtTime, true comparing using view" );
						b = true;
					}
				}
			}
		
		
		/*
		if (((time1.mbValidViewFrame && time2.mbValidViewFrame ) ||
			(time1.hasValidSceneTime() && time2.hasValidSceneTime() )) == false)
		{
			System.err.println("WARNING in TimeFunctions, IsAtTime,  Different TimeData type" );
			b = false;
			//	throw new MseeException("Different TimeData type");			
		} else
		{			
			if (time1.hasValidSceneTime() && time2.hasValidSceneTime() )
			{	
				/* Date.compareTo()
				 * 
				 * Returns:
						the value 0 if the argument Date is equal to this Date; 
						a value less than 0 if this Date is before the Date argument; 
						and a value greater than 0 if this Date is after the Date argument. 
				 *
				if ((	(time1.mSceneTime_Start.compareTo(time2.mSceneTime_End) > 0) || 
						(time2.mSceneTime_Start.compareTo(time1.mSceneTime_End) > 0) )
						== false)
				{
					b = true;
				}
			} else
			{
				
				if (time1.mbValidViewFrame && time2.mbValidViewFrame )
				{
					if (( 	(time1.mViewFrame_Start > time2.mViewFrame_End) ||
							(time2.mViewFrame_Start > time1.mViewFrame_End) 
							) == false)
					{
						
						System.err.println("WARNING in TimeFunctions, IsAtTime,  comparing using view" );
					
						b = true;
					}
				}
			}
		}*/
		
		if (MseeFunction.bVerbose)
		{
			System.out.println("DEBUG TimeFunctions IsAtTime ending " + b.toString());
		}
		
		
		return b;
	}

	/**
	 * @param temporal_relation
	 * @param t1
	 * @param t2
	 * @return
	 * @throws MseeException
	 */
	public static Boolean IsTemporalRelation(
			TemporalRelation temporal_relation, 
			TimeData t1,
			TimeData t2) throws MseeException {
		// TODO Auto-generated method stub
		
		if (((t1.mbValidViewFrame && t2.mbValidViewFrame ) ||
				(t1.hasValidSceneTime() && t2.hasValidSceneTime() )) == false)
			{
				throw new MseeException("Different TimeData type");			
			}
		
		switch (temporal_relation)
		{
			case PRECEDES: 		return IsPrecedesTime(t1, t2);  
			case MEETS: 		return IsMeetsTime(t1, t2); 
			case OVERLAPS: 		return IsOverlapsTime(t1, t2); 
			case FINISHED_BY: 	return IsFinishedByTime(t1, t2); 
			case CONTAINS: 		return IsContainsTime(t1, t2); 
			case STARTS : 		return IsStartsTime(t1, t2);  
			case EQUALS: 		return IsEqualsTime(t1, t2);  
			case BEFORE	: 		return IsBeforeTime(t1, t2); 
			case STRICTLY_BEFORE: return IsStrictlyBeforeTime(t1, t2);  
			case AFTER : 		return IsAfterTime(t1, t2);  
			case SAME_TIME_AS: 	return IsSameTimeAs(t1, t2);  
		}
		
		return false;
	}




	
	/**
	 * @param t1
	 * @param t2
	 * @return true if there exists some time that is in both t1 and t2
	 */
	public static Boolean IsSameTimeAs(TimeData t1, TimeData t2) {
		return ((IsPrecedesTime(t1,t2) == false) && 
				(IsPrecedesTime(t2,t1) == false) );
	}

	/**
	 * @param t1
	 * @param t2
	 * @return return true if t1 ends after t2 ends
	 */
	public static Boolean IsAfterTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return (t1.mSceneTime_End.compareTo(t2.mSceneTime_End) > 0);
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return (t1.mViewFrame_End > t2.mViewFrame_End);
		}		
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t1 precedes t2 or t1 meets t2
	 */
	public static Boolean IsStrictlyBeforeTime(TimeData t1, TimeData t2) {
		return (IsPrecedesTime(t1, t2) || IsMeetsTime(t1, t2));		
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t1 begins before t2
	 */
	public static Boolean IsBeforeTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return (t1.mSceneTime_Start.compareTo(t2.mSceneTime_Start) < 0);
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return (t1.mViewFrame_Start < t2.mViewFrame_Start);
		}		
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t1 and t2 begin and end at the same time
	 */
	public static Boolean IsEqualsTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return ((Math.abs(t1.mSceneTime_Start.getTime() - t2.mSceneTime_Start.getTime())<= TIME_MEETS_TOLERANCE_MSEC) &&
					(Math.abs(t1.mSceneTime_End.getTime() - t2.mSceneTime_End.getTime())<= TIME_MEETS_TOLERANCE_MSEC) 
					); 
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return ((Math.abs(t1.mViewFrame_Start - t2.mViewFrame_Start) <= TIME_MEETS_TOLERANCE_FRAME) &&
					(Math.abs(t1.mViewFrame_End- t2.mViewFrame_End) <= TIME_MEETS_TOLERANCE_FRAME) 
					);
		}		
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t1 and t2 begin at the same time, but t1 ends before t2
	 */
	public static Boolean IsStartsTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return ((Math.abs(t1.mSceneTime_Start.getTime() - t2.mSceneTime_Start.getTime())<= TIME_MEETS_TOLERANCE_MSEC) &&
					(t1.mSceneTime_End.compareTo(t2.mSceneTime_End) < 0)); 
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return ((Math.abs(t1.mViewFrame_Start - t2.mViewFrame_Start) <= TIME_MEETS_TOLERANCE_FRAME) &&
					(t1.mViewFrame_End < t2.mViewFrame_End));
		}
		
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t2 begins and ends during t1
	 */
	public static Boolean IsContainsTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return ((t1.mSceneTime_Start.compareTo(t2.mSceneTime_Start) <= 0) &&
					(t1.mSceneTime_End.compareTo(t2.mSceneTime_End) >= 0)); 
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return ((t1.mViewFrame_Start <= t2.mViewFrame_Start) &&
					(t1.mViewFrame_End >= t2.mViewFrame_End));
		}
		
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t1 begins before t2, and both end at the same time
	 */
	public static Boolean IsFinishedByTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return ((t1.mSceneTime_Start.compareTo(t2.mSceneTime_Start) < 0) &&
					(Math.abs(t1.mSceneTime_End.getTime() - t2.mSceneTime_End.getTime())<= TIME_MEETS_TOLERANCE_MSEC));
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return ((t1.mViewFrame_Start < t2.mViewFrame_Start) &&
					(Math.abs(t1.mViewFrame_End - t2.mViewFrame_End) <= TIME_MEETS_TOLERANCE_FRAME));
		}
		
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if : t1 begins, then t2 begins, then t1 ends
	 */
	public static Boolean IsOverlapsTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return ((t1.mSceneTime_Start.compareTo(t2.mSceneTime_Start) < 0) &&
					(t2.mSceneTime_Start.compareTo(t1.mSceneTime_End) < 0) &&
					(t1.mSceneTime_End.compareTo(t2.mSceneTime_End) < 0) );
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {
			return ((t1.mViewFrame_Start < t2.mViewFrame_Start) &&
					(t2.mViewFrame_Start < t1.mViewFrame_End) &&
					(t1.mViewFrame_End < t2.mViewFrame_End) );
		}
		return false;
	}


	/**
	 * @param t1
	 * @param t2
	 * @return true if t1 ends before t2 begins
	 */
	public static Boolean IsPrecedesTime(TimeData t1, TimeData t2) {

		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			return (t1.mSceneTime_End.compareTo(t2.mSceneTime_Start) < 0);
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {
			return (t1.mViewFrame_End < t2.mViewFrame_Start);
		}
		return false;
	}
	

	/**
	 * 
	 * @param t1
	 * @param t2
	 * @return true if t2 begins when t1 ends
	 */
	public static Boolean IsMeetsTime(TimeData t1, TimeData t2) {
		if (t1.hasValidSceneTime() && t2.hasValidSceneTime()) {
			long diff = t1.mSceneTime_End.getTime() - t2.mSceneTime_Start.getTime();
			return (Math.abs(diff)<= TIME_MEETS_TOLERANCE_MSEC);
		}

		if (t1.mbValidViewFrame && t2.mbValidViewFrame) {			
			return (Math.abs(t1.mViewFrame_End - t2.mViewFrame_Start) <= TIME_MEETS_TOLERANCE_FRAME);
		}
		return false;
	}

/*
 * ref
 * 	Date mSceneTime_Start = new Date();
	Date mSceneTime_End = new Date();
 */
	public static TimeData GetSceneTimeIntersect(TimeData data1, TimeData data2) {
		// TODO Auto-generated method stub
		
		if ((data1.hasValidSceneTime()== false) ||
		    (data2.hasValidSceneTime()  == false))
		{	// cannot compare scene time
			return null;
		}
		
		 Date start = new Date();
		 Date end = new Date();
		 
		 // get the later start time
		 if (data1.mSceneTime_Start.compareTo(data2.mSceneTime_Start)<0)
		 {
			 start = (Date) data2.mSceneTime_Start.clone();
		 } else
		 { 	start = (Date) data1.mSceneTime_Start.clone();
		 }

		 // get the earlier end time
		 if (data1.mSceneTime_End.compareTo(data2.mSceneTime_End)<0)
		 {	end = (Date) data1.mSceneTime_End.clone();
		 } else
		 { 	end = (Date) data2.mSceneTime_End.clone();
		 }
		
		 if (start.compareTo(end)>0)
		 {
			 // not intersect
			 return null;
		 } 
		 
		 TimeData intersect = new TimeData();
		 intersect.SetSceneTimePeriod(start,end);
		 return intersect;		
	}

	// given two time data, get the enclosing time interval, i.e. with the earliest start time and latest end time
	public static TimeData GetSceneTimeRange(TimeData t1, TimeData t2) {
		// TODO Auto-generated method stub

		if ((t1.hasValidSceneTime() == false) ||
		    (t2.hasValidSceneTime() == false))
		{	// cannot compare scene time
			System.err.println("ERROR GetSceneTimeRange   t1 or t2 has invalid scene time");			
			return null;
		}
		
		 Date start = new Date();
		 Date end = new Date();
		 
		 // get the earlier start time
		 if (t2.mSceneTime_Start.compareTo(t2.mSceneTime_Start)<0)
		 {
			 // t2 is earlier
			 start = (Date) t2.mSceneTime_Start.clone();
		 } else
		 { 	 // t1 is earlier
			 start = (Date) t1.mSceneTime_Start.clone();
		 }

		 // get the later end time
		 if (t1.mSceneTime_End.compareTo(t2.mSceneTime_End)<0)
		 {	// t2 is later
			 end = (Date) t2.mSceneTime_End.clone();
		 } else
		 { 	// t1 is later
			 end = (Date) t1.mSceneTime_End.clone();
		 }		
		 
		 TimeData range = new TimeData();
		 range.SetSceneTimePeriod(start,end);
		 return range;		 
	}


	public static Date MinDate(Date d1, Date d2) {
		
		if ((d1 == null) && (d2 == null)) return null;
		
		if (d1 == null)	return (Date)d2.clone();
		if (d2 == null) return (Date)d1.clone();
		if (d1.compareTo(d2)<0)
			return (Date)d1.clone();
		else 
			return (Date)d2.clone();		
	}

	public static Date MaxDate(Date d1, Date d2) {
		if ((d1 == null) && (d2 == null)) return null;
		
		if (d1 == null)	return (Date)d2.clone();
		if (d2 == null) return (Date)d1.clone();
		if (d1.compareTo(d2)<0)
			return (Date)d2.clone();
		else 
			return (Date)d1.clone();
		
	}
	

	
	public static void SortTimeData(ArrayList< TimeData > timeDataList) {
		// TODO Auto-generated method stub
		if (timeDataList.size() <=1)
		{
			// nothing to do
			return;
		}
		
		// sort
		//
		int ctr= timeDataList.size();
		for (int i = 0; i < ctr-1;i++)
		for (int j = i+1; j < ctr; j++)
		{
			if (timeDataList.get(i).mSceneTime_Start.compareTo(timeDataList.get(j).mSceneTime_Start) > 0)
			{	// swap
				TimeData tmp_data = new TimeData(timeDataList.get(i));
				timeDataList.get(i).Set(timeDataList.get(j));
				timeDataList.get(j).Set(tmp_data);				
			}
		}
		
		// debug
		for (int i = 0; i < timeDataList.size();i++)
		{
			System.out.println("DEBUG CardinalityData sorted " + i 
					+ " start_time " + timeDataList.get(i).mSceneTime_Start.toString() 
					+ " end_time " + timeDataList.get(i).mSceneTime_End.toString());
		}

	}
	
	// given a list of time data; sort them, and merge overlapping ones
	public static void SortAndMergeOverlappingTimeData(ArrayList< TimeData > timeDataList) {
		if (timeDataList.size() <=1)
		{
			// nothing to do
			return;
		}
		
		// SortTimeData();
		TimeFunctions.SortTimeData(timeDataList);		
				
		// now, merge any two items if they have overlap
		int m = 0; 
		while (m < timeDataList.size())
		{
			TimeData t1 = timeDataList.get(m);
			TimeData t2 = timeDataList.get(m+1);
			if (TimeFunctions.GetSceneTimeIntersect(t1,t2) !=null )
			{	
				TimeData t3 = TimeFunctions.GetSceneTimeRange(t1,t2);
				if (t3 != null)
				{
					timeDataList.get(m).Set(t3);
				}
				timeDataList.remove(m+1);				
			}
			m++;			
		}		
	}
}
