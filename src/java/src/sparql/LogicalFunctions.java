package sparql;

public class LogicalFunctions {
	
	public enum LogicalOperatorType {
		AND,
		OR,
		NOT,
		UNKNOWN,
	};
	
	public static LogicalOperatorType GetLogicalOperatorType(String string) {
		// TODO Auto-generated method stub
		if (string.equals("or")) {  return LogicalOperatorType.OR; }
		else if (string.equals("and")) {  return LogicalOperatorType.AND; }
		else if (string.equals("not")) {  return LogicalOperatorType.NOT; }
		
		System.out.println("DEBUG ERROR GetLogicalOperatorType; unrecognized string " + string);
		
		return LogicalOperatorType.UNKNOWN;	
	}
	
	public static int GetNumArgumentForLogicalOperator(LogicalOperatorType logicalOperatorType2) {
		switch (logicalOperatorType2)
		{
		case OR: return 2;
		case AND: return 2;
		case NOT: return 1;
		case UNKNOWN: 
			System.err.println("ERROR GetNumArgumentForLogicalOperator invalid type " + logicalOperatorType2 );
			return 0;
		default:
			System.err.println("ERROR GetNumArgumentForLogicalOperator invalid type " + logicalOperatorType2 );
			return 0;		
		}
	}
	
}
