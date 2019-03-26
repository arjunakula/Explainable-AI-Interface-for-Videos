package sparql;


public class AnswerConfidence {
	
	public static double productOfConfidence = 1.0;
	public static int ctrConfidence = 0;
	public static double averageConfidence = 1.0;
	
			
	
		// reset the AnswerConfidence; this should be called before the start of each query;
		public static void Reset() {
			productOfConfidence = 1.0;
			ctrConfidence = 0;
			averageConfidence = 1.0;			
		}



		public static void UpdateConfidence(Double f) {
			// TODO Auto-generated method stub
			productOfConfidence = productOfConfidence * f;
			ctrConfidence++;
			if (ctrConfidence ==1)
			{averageConfidence = productOfConfidence;
			} else
			{
				double power = 1.0/ctrConfidence;
				averageConfidence = Math.pow(productOfConfidence, power);
				
				
			}
			System.out.println("DEBUG AnswerConfidence averageConfidence "+ averageConfidence);
			
			
		}
}
