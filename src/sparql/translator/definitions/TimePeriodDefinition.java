package sparql.translator.definitions;

import java.util.ArrayList;

import org.w3c.dom.Node;

import sparql.translator.timePeriod.SceneCentricTimePeriod;
import sparql.translator.timePeriod.TimePeriod;
import sparql.translator.timePeriod.ViewCentricTimePeriod;
import sparql.translator.utilities.ArgumentType;
import sparql.translator.utilities.Global;

/**
 * The <code>TimePeriodDefinition</code> holds important information about a time definition in an XML query.
 *
 * @author Ken Samuel
 * @version 1.0, Nov 4, 2013
 * @since 1.6
 */
public class TimePeriodDefinition extends Definition {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;
	
	/** 
	 * <code>timePeriod</code>  is an object that holds all of the information about the time period that is 
	 * defined in this class. 
	 */
	private TimePeriod timePeriod;
	
	/** <code>definitionStringInSPARQL</code> is this definition in the SPARQL query. */
	private StringBuffer definitionStringInSPARQL;
	
	
	/**
	 * The <code>TimePeriodDefinition</code> constructor initializes the global variables.
	 *
	 * @param timePeriodNode is the XML definition of the time period.
	 * variable name without the number at the end) points to the number of variables with that prefix.
	 */
	public TimePeriodDefinition(Node timePeriodNode) {
		super(timePeriodNode);
		timePeriod = null;
		definitionStringInSPARQL = new StringBuffer();
	}

	/**
	 * The <code>translateTimePeriod</code> method translates this time period from XML to SPARQL. As input, 
	 * it uses the value of the global variable <code>definitionInXML</code>, and it saves the result in the 
	 * global variable <code>definitionInSPARQL</code>.
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void translateTimePeriod(String owner) {
		String timeType;				//Specifies the format of the time period
		String timeTypeSPARQL;			//That format translated into its name in a SPARQL filter function
		ArrayList<Node> timeArguments;	//Information about this time period
		String timeArgumentName;		//The name of one of the argumentsof this time period in the XML query 
		String warning;							//A message to let the user know there might be a problem

		/////// Time Type ///////
		timeType = definitionInXML.getNodeName();
		if (Global.TIME_TYPES.keySet().contains(timeType)) {	//Is it a legitimate time type?
			timeTypeSPARQL = Global.TIME_TYPES.get(timeType);
			definitionStringInSPARQL.append("\"" + timeTypeSPARQL + "\",");
			if (timeType.equals("SceneCentricTimePeriod")) {
				timePeriod = new SceneCentricTimePeriod(id);
				timePeriod.setTimeType(ArgumentType.SCENE_CENTRIC_TIME);
			} else if (timeType.equals("ViewCentricTimePeriod")) {
				timePeriod = new ViewCentricTimePeriod(id);
				timePeriod.setTimeType(ArgumentType.VIEW_CENTRIC_TIME);
			} else {
				warning = 
						"WARNING in TimePeriodDefinition.translateTimePeriod in " + 
						owner + ": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return;
			}
			timeArguments = Global.getChildNodes(definitionInXML,owner);
			for (Node timeArgument : timeArguments) {
				timeArgumentName = timeArgument.getNodeName(); 
				if (id.equals("")) {
					warning = "a <" + timeType + ">";
				} else {
					warning = "the time \"" + id + "\"";
				}
				if (timeArgumentName.equals("StartTime")) {
					((SceneCentricTimePeriod)timePeriod).setStart(timeArgument.getTextContent().trim(),
							warning+" in "+owner);
				} else if (timeArgumentName.equals("EndTime")) {
					((SceneCentricTimePeriod)timePeriod).setEnd(timeArgument.getTextContent().trim(),
							warning+" in "+owner);
				} else if (timeArgumentName.equals("StartFrame")) {
					((ViewCentricTimePeriod)timePeriod).setStart(timeArgument.getTextContent().trim(),
							warning+" in "+owner);
				} else if (timeArgumentName.equals("EndFrame")) {
					((ViewCentricTimePeriod)timePeriod).setEnd(timeArgument.getTextContent().trim(),
							warning+" in "+owner);
				} else if (timeArgumentName.equals("ViewId")) {
					((ViewCentricTimePeriod)timePeriod).setViewId(
							timeArgument.getTextContent().trim(),
							warning+" in "+owner);
				} else {
					warning = 
							"WARNING in TimePeriodDefinition.translateTimePeriod in " + warning + " in " + 
							owner + ": An unexpected time argument, <" + timeArgumentName + 
							">, was found in " + warning + ".";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
			}
			definitionStringInSPARQL.append("\"" + timePeriod.getStart() + ";" + timePeriod.getEnd());
			if (timePeriod.getClass() == ViewCentricTimePeriod.class) {
				definitionStringInSPARQL.append(";" + ((ViewCentricTimePeriod)timePeriod).getViewId());
			}
			definitionStringInSPARQL.append("\"");
			Global.changedDefinition(this);
		} else {
			warning = 
					"WARNING in TimePeriodDefinition.translateTimePeriod in " + owner + 
					": An unexpected time type, <" + timeType + ">, was found in " + owner + ".";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}
	
	/**
	 * The <code>getTimePeriod</code> getter returns the value of the global variable,
	 * <code>timePeriod</code>, a <b><code>TimePeriod</code></b>.
	 *
	 * @return the value of <code>timePeriod</code>.
	 */
	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this definition.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return definitionStringInSPARQL.toString();
	}
}