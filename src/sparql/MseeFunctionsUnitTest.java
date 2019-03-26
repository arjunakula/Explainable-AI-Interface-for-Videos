package sparql;

import java.text.ParseException;

public class MseeFunctionsUnitTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			// test time parsing
			TimeData timeData1 = new TimeData();
			TimeData timeData2 = new TimeData();
			TimeData timeData3 = new TimeData();
			TimeData timeData4 = new TimeData();
			timeData1.SetViewFramePeriod("158,160");
			timeData2.SetViewFramePeriod("150,167");		
			timeData3.SetSceneTimePeriod("2013-09-04T14:53:28.000Z;2013-09-04T14:53:34.000Z");
			timeData4.SetSceneTimePeriod("2013-09-04T14:53:34.000Z;2013-09-04T14:53:40.000Z");
			timeData1.PrintTimeData();		
			timeData2.PrintTimeData();
			timeData3.PrintTimeData();
			timeData4.PrintTimeData();
		
			// test time operation 
			System.out.println(" IsAtTime 1 2 " + TimeFunctions.IsAtTime(timeData1, timeData2));
			System.out.println(" IsAtTime 3 4 " + TimeFunctions.IsAtTime(timeData3, timeData4));

			// test exception
			//			System.out.println(" IsAtTime 1 3 " + TimeFunctions.IsAtTime(timeData1, timeData3));
		
		}  catch (MseeException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
}
