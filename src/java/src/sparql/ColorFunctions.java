package sparql;

import TextParser.CObjectData;


public class ColorFunctions {

	
	public static enum Color { 
		COLOR_NA, 
		COLOR_AZURE,
		COLOR_BLACK,
		COLOR_BLUE,
		COLOR_BROWN,
		COLOR_GRAY,
		COLOR_GREEN,
		COLOR_ORANGE,
		COLOR_PINK,
		COLOR_PURPLE,
		COLOR_RED,
		COLOR_WHITE,
		COLOR_YELLOW,
		COLOR_OTHER 
		};
		
		
	public static Color GetColor(String string) {
		if (string.equalsIgnoreCase("azure")) {  return Color.COLOR_AZURE; }
		else if (string.equalsIgnoreCase("black")) {  return Color.COLOR_BLACK; }
		else if (string.equalsIgnoreCase("blue")) {  return Color.COLOR_BLUE; }
		else if (string.equalsIgnoreCase("brown")) {  return Color.COLOR_BROWN; }
		else if (string.equalsIgnoreCase("gray")) {  return Color.COLOR_GRAY; }
		else if (string.equalsIgnoreCase("green")) {  return Color.COLOR_GREEN; }
		else if (string.equalsIgnoreCase("orange")) {  return Color.COLOR_ORANGE; }
		else if (string.equalsIgnoreCase("pink")) {  return Color.COLOR_PINK; }
		else if (string.equalsIgnoreCase("purple")) {  return Color.COLOR_PURPLE; }
		else if (string.equalsIgnoreCase("red")) {  return Color.COLOR_RED; }
		else if (string.equalsIgnoreCase("white")) {  return Color.COLOR_WHITE; }
		else if (string.equalsIgnoreCase("yellow")) {  return Color.COLOR_YELLOW; }
		else if (string.equalsIgnoreCase("other")) {  return Color.COLOR_OTHER; }
		else if (string.equalsIgnoreCase("na")) {  return Color.COLOR_NA; }
		
		System.out.println("DEBUG ERROR GetColor unrecognized type " + string);
		
		return Color.COLOR_OTHER;	
		
	}

	static boolean warned = false;
	public static Boolean IsColor(TimeData time_data, LocationData loc_data,
			CObjectData object_agent, Color color) {
			
		if (!warned )
		{	System.err.println(
				"WARNING in ColorFunctions, NOT IMPLEMENTED; output TRUE for now");
			warned = true;
		}
		return true;
	}

}
