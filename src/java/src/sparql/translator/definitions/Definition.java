package sparql.translator.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sparql.translator.query.SPARQLQuery;
import sparql.translator.query.SPARQLSelect;
import sparql.translator.query.SPARQLWhere;
import sparql.translator.recordCardinality.RecordCardinalityArguments;
import sparql.translator.restrictions.Restriction;
import sparql.translator.utilities.Global;
import sparql.translator.utilities.TranslateCondition;
import sparql.translator.utilities.TranslateConditionReturnValue;

/**
 * The <code>Definition</code> abstract class holds the things that can be defined in an XML query:
 * times, locations, sets, and events. Its subclasses are <code>EventDefinition</code>, 
 * <code>LocationDefinition</code>, <code>SetDefinition</code>, and <code>TimePeriodDefinition</code>.
 *
 * @author Ken Samuel
 * @version 1.0, Oct 25, 2013
 * @since 1.6
 */
abstract public class Definition implements Serializable {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	protected static final long serialVersionUID = 7526472295622776147L;

	/** <code>id</code> is the name assigned to this definition in the XML query. */
	protected String id;
	
	/** <code>definitionInXML</code> is this definition in the XML query. */
	protected Element definitionInXML;
	
	/** <code>definitionInSPARQL</code> is this definition in the SPARQL query. */
	protected SPARQLQuery definitionInSPARQL;

	/** 
	 * <code>persists</code> specifies whether this definition should be remembered for the duration of the 
	 * storyline. 
	 */
	protected Boolean persists;
	
	/** <code>unableToRespondMessage</code> is a message to output to express the fact that the machine vision
	 * system cannot produce an answer to a query that uses this definition in the &lt;QueryStatement&gt;. 
	 * It is a list of predicate-argument pairs in this definition that cannot be detected by the machine 
	 * vision system.
	 */
	protected ArrayList<String> unableToRespondMessage;
	
	/** 
	 * <code>recordCardinalityArguments</code> is the list of arguments for the "RecordCardinality" filter
	 * function, not counting the first two arguments ("SET_NAME" and the name of the set). This variable is 
	 * needed only if the query requires a count of the number of elements in this set. The following is an 
	 * example of a list of "RecordCardinality" arguments:
	 * <p><code>"SET_NAME","set_moving","LOGICAL_OPERATOR","or",
	 * "VIEW_CENTRIC_TIME_PERIOD","1,9;view_cam1","CARTESIAN_METRIC_POINT","0.0,8.2","EVENT_ID","event_move1",
	 * "SCENE_CENTRIC_TIME_PERIOD","2013-09-04T15:25:51Z;2013-09-04T15:25:55Z","OBJECT_ID","obj_dog1",
	 * "CARTESIAN_PIXEL_POLYGON","0,8;7,8;7,4","FUNCTION","IsMoving","OBJECT_ID","obj_dog1"</code>
	 */
	protected RecordCardinalityArguments recordCardinalityArguments;
	
	/** 
	 * <code>timePeriodDefinitions</code> is a hash in which the identifier of each time period restriction 
	 * found in this set points to its definition object. 
	 */
	protected HashMap<String,TimePeriodDefinition> timePeriodDefinitions;
	
	/** 
	 * <code>locationDefinitions</code> is a hash in which the identifier of each location restriction found 
	 * in this set points to its definition object. 
	 */
	protected HashMap<String,LocationDefinition> locationDefinitions;
	
	/** 
	 * <code>boundVariables</code> is a list of the bound arguments in order. The SPARQL versions of their 
	 * names are used here. 
	 */
	protected ArrayList<String> boundVariables;

	
	/**
	 * The <code>Definition</code> constructor initializes the global variables.
	 * 
	 * @param definition is this object's definition in the XML query.
	 */
	public Definition(Node definition) {
		if (definition != null) {
			definitionInXML = (Element)definition;
			id = definitionInXML.getAttribute("id");
			if (definitionInXML.getAttribute("persists").equalsIgnoreCase("true")) {
				persists = true;
			} else {
				persists = false;
			}
		} else {
			definitionInXML = null;
			id = null;
			persists = null;
		}
		definitionInSPARQL = new SPARQLQuery();
		definitionInSPARQL.distinct();
		recordCardinalityArguments = null;
		timePeriodDefinitions = new HashMap<String, TimePeriodDefinition>();
		locationDefinitions = new HashMap<String, LocationDefinition>();
		boundVariables = new ArrayList<String>();
		unableToRespondMessage = new ArrayList<String>();
		Global.changedDefinition(this);
	}

	/**
	 * The <code>Definition</code> constructor initializes the global variables. This constructor is
	 * used for objects that are answers to &lt;what&gt; queries. Only three of the global variables will be
	 * used for this object: <code>id</code>, <code>persists</code>, and <code>unableToRespondMessage</code>.
	 *
	 * @param newId is the identifier of this object that may appear in future XML queries.
	 */
	public Definition(String newId) {
		id = newId;
		setPersists(true);			//The <what> objects are always persistent
		unableToRespondMessage = new ArrayList<String>();
		Global.changedDefinition(this);
	}

	/**
	 * The <code>translateObjectOrSet</code> method translates this definition from XML to SPARQL. As input, 
	 * it uses the value of the global variable <code>definitionInXML</code>, and it saves the result in the 
	 * global variable <code>definitionInSPARQL</code>. 
	 * 
	 * @param isObject specifies whether this is an object definition (<b><code>true</code></b>) or a set 
	 * definition (<b><code>false</code></b>).
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void translateObjectOrSet(Boolean isObject, String owner) {
		ArrayList<Node> nodes;				//All of the XML nodes for this object or set
		String nodeName;					//The name of the tag at the head of that node
		ArrayList<Node> conditionNodes;		//Everything inside the <condition> tag (there should only be 1)
		Boolean isDoneBvar;					//Specifies whether we have finished the bound variables
		Boolean isDoneCondition;			//Specifies whether we have finished the condition
		String definitionType;				//Specifies whether this is an object or a set definition
		String warning;						//A message to let the user know there might be a problem

		isDoneBvar = false;						//Initialize
		isDoneCondition = false;				//Initialize
		if (isObject) {
			definitionType = "Object";
		} else {
			definitionType = "Set";
		}
		nodes = Global.getChildNodes(definitionInXML,owner);
		for (Node node : nodes) {
			nodeName = node.getNodeName();
			nodeName = nodeName.toLowerCase();
			if (nodeName.equalsIgnoreCase("bvar")) {
				if (isDoneBvar == true) {
					warning = 
							"WARNING in Definition.translateObjectOrSet in " + getWarning(isObject) + 
							owner + ": Found more than 1 <bvar> tag in " + getWarning(isObject) + 
							"<" + definitionType + ">.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
				addBoundVariables(Global.translateBoundVariables(node,getWarning(isObject)+owner));
				isDoneBvar = true;
			} else if (nodeName.equalsIgnoreCase("condition")) {
				if (isDoneCondition == true) {
					warning = 
							"WARNING in Definition.translateObjectOrSet in " + getWarning(isObject) + owner + 
							": Found more than 1 <condition> tag in " + getWarning(isObject) + 
							"<" + definitionType + ">.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				}
				conditionNodes = Global.getChildNodes(node,owner);
				for (Node conditionNode : conditionNodes) {
					if (isDoneCondition == true) {
						warning = 
								"WARNING in Definition.translateObjectOrSet in the <condition> in " +
								getWarning(isObject) + owner + 
								": Found more than 1 thing at the top level of the <condition> " +
								"tag in " + getWarning(isObject) + "<" + definitionType + ">.";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
					translateCondition(conditionNode,"the <condition> in "+getWarning(isObject)+owner);
					isDoneCondition = true;
				}
				if (isDoneCondition == false) {
					System.err.println(
							"WARNING in Definition.translateObjectOrSet in " + 
							getWarning(isObject) + owner + ": Found an empty <condition>.");
					isDoneCondition = true;
				}
			} else {
				warning = 
						"WARNING in Definition.translateObjectOrSet in " + getWarning(isObject) + owner + 
						": Found an unexpected tag, <" + nodeName + 
						">, in " + getWarning(isObject) + "<" + definitionType + ">.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
		}
		if (isDoneBvar == false) {
			warning = 
					"WARNING in Definition.translateObjectOrSet in " + getWarning(isObject) + owner +
					": Unable to find a <bvar> in " + getWarning(isObject) + "<" + definitionType + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		if (isDoneCondition == false) {
			warning = 
					"WARNING in Definition.translateObjectOrSet in " + getWarning(isObject) + owner +
					": Unable to find a <condition> in " + getWarning(isObject) + 
					"<" + definitionType + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}
	
	/**
	 * The <code>translateCondition</code> method translates a condition from XML to SPARQL. 
	 *
	 * @param conditionNode is an XML tag with contents that can be true or false.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return three things: 1) a list of arguments to be used in a "RecordCardinality" filter function if it 
	 * is necessary to count the number of elements in an object or set that has this condition,
	 * 2) a list of predicate (behavior, relationship, and action) filter function restrictions found in the 
	 * current block of the query, and
	 * 3) a list of other restrictions found in the current block of the query.
	 */
	public TranslateConditionReturnValue translateCondition(Node conditionNode, String owner) 
	{
		TranslateConditionReturnValue returnValue;

		returnValue = TranslateCondition.translateCondition(conditionNode,this,owner);
		definitionInSPARQL.addRestrictions(returnValue.getSPARQLQuery());
		recordCardinalityArguments = returnValue.getRecordCardinalityArguments();
		Global.changedDefinition(this);
		return returnValue;
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this definition's 
	 * SPARQL translation with another variable.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		definitionInSPARQL.replaceVariable(oldVariable,newVariable,owner);
		recordCardinalityArguments.replaceVariable(oldVariable,newVariable,owner);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this SPARQL
	 * query to make sure the predicate filter function restrictions are tested last.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void reorderRestrictions(String owner) {
		definitionInSPARQL.reorderRestrictions(owner);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addRestriction</code> method adds a new restriction to be enforced on the output of
	 * this definition's query.
	 *
	 * @param restriction is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 */
	public void addRestriction(Restriction restriction) {
		definitionInSPARQL.addRestriction(restriction);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addRestriction</code> method adds a new restriction to be enforced on the output of
	 * this definition's query.
	 *
	 * @param restriction is a condition that must be satisfied for a row to be output when the query is
	 * processed.
	 * @param isPredicateFilterFunctionRestriction is <b><code>true</code></b> if and only if the given
	 * restriction is a predicate filter function restriction.
	 */
	public void addRestriction(String restriction, Boolean isPredicateFilterFunctionRestriction) {
		definitionInSPARQL.addRestriction(restriction,isPredicateFilterFunctionRestriction);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addRestrictions</code> method adds a list of new restrictions to be enforced on the output of
	 * this definition's query.
	 *
	 * @param restrictions is a list of conditions that must be satisfied for a row to be output when the 
	 * query is processed.
	 */
	public void addRestrictions(LinkedHashSet<Restriction> restrictions) { 
		for (Restriction restriction : restrictions) {
			addRestriction(restriction);
		}
	}

	/**
	 * The <code>addRestrictions</code> method adds the restrictions of another query to be enforced on the 
	 * output of this definition's query.
	 *
	 * @param query is a SPARQL query.
	 */
	public void addRestrictions(SPARQLQuery query) {
		definitionInSPARQL.addRestrictions(query);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addBoundVariables</code> method adds the given variables to this object or set's list of  
	 * bound variables.
	 *
	 * @param newBoundVariables are the variables to add to this object or set's list of bound variables.
	 */
	public void addBoundVariables(ArrayList<String> newBoundVariables) {
		for (String variable : newBoundVariables) {
			addBoundVariable(variable);
		}
	}
	
	/**
	 * The <code>addBoundVariable</code> method adds the given variable to this object or set's list of bound 
	 * variables.
	 *
	 * @param newBoundVariable is the variable to add to this object or set's list of bound variables.
	 */
	public void addBoundVariable(String newBoundVariable) {
		boundVariables.add(newBoundVariable);
		definitionInSPARQL.addSelectVariable(newBoundVariable);	//It needs to go in the SELECT statement too
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addTimePeriodDefinition</code> method records a time period definition in the hash of 
	 * definitions of time periods found in this set definition.
	 *
	 * @param timePeriodId is the name of the time period to record.
	 * @param definition is the definition of that time period.
	 */
	public void addTimePeriodDefinition(String timePeriodId, TimePeriodDefinition definition) {
		timePeriodDefinitions.put(timePeriodId,definition);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addLocationDefinition</code> method records a location definition in the hash of 
	 * definitions of locations found in this set definition.
	 *
	 * @param locationId is the name of the location to record.
	 * @param definition is the definition of that location.
	 */
	public void addLocationDefinition(String locationId, LocationDefinition definition) {
		locationDefinitions.put(locationId,definition);
		Global.changedDefinition(this);
	}

	/**
	 * The <code>addUnableToRespondMessage</code> setter changes the value of the global variable,
	 * <code>unableToRespondMessage</code>, a <b><code>String</code></b>.
	 *
	 * @param unableToRespondMessageIn is the new value that should be added to the 
	 * <code>unableToRespondMessage</code> list.
	 */
	public void addUnableToRespondMessage(String unableToRespondMessageIn) {
		unableToRespondMessage.add(unableToRespondMessageIn);
	}

	/**
	 * The <code>setPersists</code> setter changes the value of the global variable,
	 * <code>persists</code>, a <b><code>Boolean</code></b>.
	 *
	 * @param persistsIn is the new value that should be assigned to <code>persists</code>.
	 */
	public void setPersists(Boolean persistsIn) {
		persists = persistsIn;
		Global.changedDefinition(this);
	}

	/**
	 * The <code>setId</code> setter changes the value of the global variable,
	 * <code>id</code>, a <b><code>String</code></b>.
	 *
	 * @param idIn is the new value that should be assigned to <code>id</code>.
	 */
	public void setId(String idIn) {
		id = idIn;
		Global.changedDefinition(this);
	}

	/**
	 * The <code>getId</code> getter returns the value of the global variable,
	 * <code>id</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>name</code>.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The <code>getPersists</code> getter returns the value of the global variable,
	 * <code>persists</code>, a <b><code>Boolean</code></b>.
	 *
	 * @return the value of <code>persists</code>.
	 */
	public Boolean getPersists() {
		return persists;
	}

	/**
	 * The <code>getSelect</code> getter returns the SELECT line of the global variable,
	 * <code>definitionInSPARQL</code>, a <b><code>SPARQLSelect</code></b>.
	 *
	 * @return the value of the SELECT line in <code>definitionInSPARQL</code>.
	 */
	public SPARQLSelect getSelect() {
		return definitionInSPARQL.getSelect();
	}

	/**
	 * The <code>getWhere</code> getter returns the WHERE block of the global variable,
	 * <code>definitionInSPARQL</code>, a <b><code>SPARQLWhere</code></b>.
	 *
	 * @return the value of the WHERE block in <code>definitionInSPARQL</code>.
	 */
	public SPARQLWhere getWhere() {
		return definitionInSPARQL.getWhere();
	}
	
	/**
	 * The <code>getSPARQL</code> getter returns this object's SPARQL definition.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 */
	public SPARQLQuery getSPARQL() {
		return definitionInSPARQL;
	}

	/**
	 * The <code>getTimePeriodDefinitions</code> getter returns the value of the global variable,
	 * <code>timePeriodDefinitions</code>, a <b><code>HashMap<String,TimePeriodDefinition></code></b>.
	 *
	 * @return the value of <code>timePeriodDefinitions</code>.
	 */
	public HashMap<String, TimePeriodDefinition> getTimePeriodDefinitions() {
		return timePeriodDefinitions;
	}

	/**
	 * The <code>getLocationDefinitions</code> getter returns the value of the global variable,
	 * <code>locationDefinitions</code>, a <b><code>HashMap<String,LocationDefinition></code></b>.
	 *
	 * @return the value of <code>locationDefinitions</code>.
	 */
	public HashMap<String, LocationDefinition> getLocationDefinitions() {
		return locationDefinitions;
	}

	/**
	 * The <code>getBoundVariables</code> getter returns the value of the global variable,
	 * <code>boundVariables</code>, an <b><code>ArrayList<String></code></b>.
	 *
	 * @return the value of <code>boundVariables</code>.
	 */
	public ArrayList<String> getBoundVariables() {
		return boundVariables;
	}

	/**
	 * The <code>getRestrictions</code> method returns a list with all of the restrictions in this 
	 * definition's query's WHERE clause.
	 *
	 * @return a list of restrictions, each of which is a string.
	 */
	public LinkedHashSet<Restriction> getRestrictions() {		
		return definitionInSPARQL.getRestrictions();
	}
	
	/**
	 * The <code>getRecordCardinalityArguments</code> getter returns the value of the global variable,
	 * <code>recordCardinalityArguments</code>, a <code>RecordCardinalityArguments</code>.
	 *
	 * @return the value of <code>recordCardinalityArguments</code>.
	 */
	public RecordCardinalityArguments getRecordCardinalityArguments() {
		return recordCardinalityArguments;
	}

	/**
	 * The <code>getUnableToRespondMessage</code> getter returns the value of the global variable,
	 * <code>unableToRespondMessage</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>unableToRespondMessage</code>.
	 */
	public ArrayList<String> getUnableToRespondMessage() {
		return unableToRespondMessage;
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this definition.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return definitionInSPARQL.toString();
	}

	/**
	 * The <code>getWarning</code> method returns information to put in a warning message.
	 *
	 * @param isObject specifies whether this is an object definition (<b><code>true</code></b>) or a set 
	 * definition (<b><code>false</code></b>).
	 * @return the name of this object or set, if there is one. Otherwise, just return "an &lt;object&gt;" or 
	 * "a &lt;set&gt;".
	 */
	private String getWarning(Boolean isObject) {
		String definitionType;				//Specifies whether this is an object or a set definition

		if (isObject) {
			definitionType = "object";
		} else {
			definitionType = "set";
		}
		if (id.equals("")) {
			return "a <" + definitionType + "> in ";
		}
		return "the " + definitionType + ", \""+id+"\", in ";
	}	
}