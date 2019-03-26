package sparql.translator.utilities;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sparql.translator.definitions.Definition;
import sparql.translator.definitions.LocationDefinition;
import sparql.translator.definitions.TimePeriodDefinition;
import sparql.translator.query.SPARQLQuery;
import sparql.translator.recordCardinality.RecordCardinalityArguments;
import sparql.translator.recordCardinality.RecordCardinalityColorArguments;
import sparql.translator.recordCardinality.RecordCardinalityEventArguments;
import sparql.translator.recordCardinality.RecordCardinalityFunctionArguments;
import sparql.translator.recordCardinality.RecordCardinalityLogicalOperatorArguments;
import sparql.translator.recordCardinality.RecordCardinalityObjectArguments;
import sparql.translator.restrictions.OptionalRestriction;
import sparql.translator.restrictions.Restriction;
import sparql.translator.restrictions.StringRestriction;
import sparql.translator.restrictions.UnionRestriction;

/**
 * The <code>TranslateCondition</code> class has functions that translate things that can appear in a 
 * &lt;condition&gt; block.
 *
 * @author Ken Samuel
 * @version 1.0, Jan 2, 2014
 * @since 1.6
 */
public class TranslateCondition {

	/** 
	 * <code>location</code> is a location restriction that is extracted by one of the methods that 
	 * translates arguments (<code>translateArguments()</code>, <code>translateObjectArguments()</code>, 
	 * <code>translateColorArguments()</code>, <code>translateFilterFunctionArguments()</code>, 
	 * <code>translatePartOfRelationshipArguments()</code>, and 
	 * <code>translateSameObjectRelationshipArguments()</code>). */
	private static LocationDefinition location;
	
	/** 
	 * <code>timePeriod</code> is a time restriction that is extracted by one of the methods that 
	 * translates arguments (<code>translateArguments()</code>, <code>translateObjectArguments()</code>, 
	 * <code>translateColorArguments()</code>, <code>translateFilterFunctionArguments()</code>, 
	 * <code>translatePartOfRelationshipArguments()</code>, and 
	 * <code>translateSameObjectRelationshipArguments()</code>). */
	private static TimePeriodDefinition timePeriod;
	
	/** 
	 * <code>arguments</code> is a list of arguments that are extracted by one of the methods that 
	 * translates arguments (<code>translateArguments()</code>, <code>translateObjectArguments()</code>, 
	 * <code>translateColorArguments()</code>, <code>translateFilterFunctionArguments()</code>, 
	 * <code>translatePartOfRelationshipArguments()</code>, and 
	 * <code>translateSameObjectRelationshipArguments()</code>). */
	private static ArrayList<String> arguments;				//The arguments of the given predicate

	
	/**
	 * The <code>translateCondition</code> method translates a condition from XML to SPARQL. 
	 * NOTE</b>: The <code>sPARQLQuery</code> and <code>pFFRS</code> parameters may be changed by this method.
	 *
	 * @param conditionTag is an XML tag with contents that can be true or false.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in the &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return three things: 1) a list of arguments to be used in a "RecordCardinality" filter function if it 
	 * is necessary to count the number of elements in an object or set that has this condition,
	 * 2) a list of predicate (behavior, 
	 * relationship, and action) filter function restrictions found in the current block of the query, and
	 * 3) a list of other restrictions found in the current block of the query.
	 */
	public static TranslateConditionReturnValue translateCondition(
			Node conditionTag, Definition definition, String owner)
	{
		String tagName;						 //The name of an XML tag
		ArrayList<Node> conditionNodes;		 //A list of its conditions in XML
		String conditionType;				 //The type of that condition
		LinkedHashSet<Restriction> newRestrictions;//Restrictions that were recently generated
		LinkedHashSet<Restriction> andRestrictions;//Restrictions found in an <and>
		UnionRestriction unionRestriction;	 //List of restriction groups that might have UNIONs between them
		LinkedHashSet<Restriction> group;	 //One group of restrictions in a UnionRestriction
		UnionRestriction not;				 //A <not> block translated into SPARQL form
		UnionRestriction or;				 //An <or> block translated into SPARQL form
		LinkedHashSet<Restriction> orGroup;	 //The restrictions in one disjunct of that <or> block
		ArrayList<Node> existsConditionNodes;//A list of the conditions in an <exists> block
		RecordCardinalityArguments tempRCA;	 //To temporarily hold a RecordCardinalityArguments list
		String warning;						 //A message to let the user know there might be a problem
		
		/** The following variables are flags. */
		Boolean isDoneExistsBVar;			 //Specifies whether we have finished the <bvar> in <exists>
		Boolean isDoneExistsCondition;		 //Specifies whether we have finished the <condition> in <exists>
		Boolean isDoneNotCondition;		 	 //Specifies whether we have finished the <condition> in <not>
		Boolean sawBooleanCondition;		 //Specifies whether a legitimate boolean condition was found
		
		/** The following variables are lists of groups of restrictions. */
		ArrayList<LinkedHashSet<Restriction>> restrictionsGroups;//Some restrictions groups
		ArrayList<LinkedHashSet<Restriction>> onlyPFFRestrictionsGroups;//Each group has PFF restrictions 
		ArrayList<LinkedHashSet<Restriction>> noPFFRestrictionsGroups;	//Groups with no PFF restrictions 
		ArrayList<LinkedHashSet<Restriction>> allRestrictionsGroups;	//Groups from previous two variables 
		
		/** 
		 * The following variables holds lists of restrictions designed to prevent more than one 
		 * branch of an "or" from succeeding. This is the "not A" in (A or (not A and B)) = (A or B). 
		 */
		ArrayList<LinkedHashSet<Restriction>> negatedRestrictionsGroups;//Some negated restrictions groups
		
		/** The value returned by this method has three parts. */
		TranslateConditionReturnValue returnedValue;	//A value returned by a recursive call to this method
		TranslateConditionReturnValue returnValue;		//The value that will be returned by this method
		RecordCardinalityArguments recordCardinalityArguments;			//One of the return values
		SPARQLQuery sPARQLQuery;										//One of the return values
		
		returnValue = new TranslateConditionReturnValue();									//Initialize
		recordCardinalityArguments = null;													//Initialize
		tempRCA = null;																		//Initialize
		tagName = conditionTag.getNodeName();
		tagName = tagName.toLowerCase();
		if (Global.IGNORE_PREDICATES.contains(tagName)) {
			System.err.println(
					"WARNING in TranslateCondition.translateCondition in " + owner + 
					": I am ignoring a <" + tagName + "> predicate.");
			return new TranslateConditionReturnValue();
		}
		conditionNodes = Global.getChildNodes(conditionTag,owner);
		if (Global.LOGICAL_OPERATORS.contains(tagName)) {		//Is it a logical operator?
			if (tagName.equalsIgnoreCase("and")) {
				andRestrictions = new LinkedHashSet<Restriction>();			//Initialize
				for (Node conditionNode : conditionNodes) {
					returnedValue = translateCondition(conditionNode,definition,"an <and> in "+owner);
					andRestrictions.addAll(returnedValue.getSPARQLQuery().getRestrictions());
					tempRCA = returnedValue.getRecordCardinalityArguments();
					if (recordCardinalityArguments == null) {
						recordCardinalityArguments = tempRCA;
					} else if (tempRCA != null) {
						recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
								"and",recordCardinalityArguments,tempRCA);
					}
				}
				sPARQLQuery = new SPARQLQuery();
				sPARQLQuery.addRestrictions(andRestrictions);
				returnValue.setSPARQLQuery(sPARQLQuery);
				if (recordCardinalityArguments == null) {
					System.err.println(
							"WARNING in TranslateCondition.translateCondition in an <and> in " + owner + 
							": Found an <and> with nothing in it.");
				}
				returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
				return returnValue;
			}
			if (tagName.equalsIgnoreCase("not")) {
				not = new UnionRestriction();					//Initialize
				isDoneNotCondition = false;
				for (Node conditionNode : conditionNodes) {
					if (isDoneNotCondition == false) {
						returnedValue = translateCondition(conditionNode,definition,"a <not> in "+owner);
						recordCardinalityArguments = returnedValue.getRecordCardinalityArguments();	
						if (recordCardinalityArguments != null) {
							recordCardinalityArguments = 
									new RecordCardinalityLogicalOperatorArguments(
											"not",recordCardinalityArguments);
						}
						newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
						not.addRestrictionsGroup(newRestrictions);
						not.negateRestriction();
						not.setHasPFFRestriction(Global.hasPFFRestriction(newRestrictions));
						sPARQLQuery = new SPARQLQuery();
						sPARQLQuery.addRestriction(not);
						returnValue.setSPARQLQuery(sPARQLQuery);
						returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
						isDoneNotCondition = true;
					} else {
						warning = 
								"WARNING in TranslateCondition.translateCondition in a <not> in " + owner + 
								": Found more than 1 thing in a <not>.";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
				}
				if (isDoneNotCondition == false) {
					System.err.println(
							"WARNING in TranslateCondition.translateCondition in a <not> in " + owner + 
							": Found a <not> with nothing in it.");
				}
				return returnValue;
			}
			if (tagName.equalsIgnoreCase("or")) {
				or = new UnionRestriction();													//Initialize
				onlyPFFRestrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>();		//Initialize
				noPFFRestrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>();			//Initialize
				for (Node conditionNode : conditionNodes) {
					returnedValue = translateCondition(conditionNode,definition,"an <or> in "+owner);
					newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
					if (Global.hasPFFRestriction(newRestrictions) ) {
						onlyPFFRestrictionsGroups.add(newRestrictions);
					} else {
						noPFFRestrictionsGroups.add(newRestrictions);
					}
					tempRCA = returnedValue.getRecordCardinalityArguments();
					if (recordCardinalityArguments == null) {
						recordCardinalityArguments = tempRCA;
					} else if (tempRCA != null) {
						recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
								"or",recordCardinalityArguments,tempRCA);
					}
				}
				allRestrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>();			//Initialize
				allRestrictionsGroups.addAll(noPFFRestrictionsGroups);
				allRestrictionsGroups.addAll(onlyPFFRestrictionsGroups);
				negatedRestrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>();		//Initialize
				for (LinkedHashSet<Restriction> restrictionGroup : allRestrictionsGroups) {
					orGroup = new LinkedHashSet<Restriction>();									//Initialize
					if (negatedRestrictionsGroups.size() >= 1) {
						orGroup.add(getNegatedDisjunction(negatedRestrictionsGroups)); 
					}
					orGroup.addAll(restrictionGroup);
					or.addRestrictionsGroup(orGroup);
					if (Global.hasPFFRestriction(orGroup)) {
						or.setHasPFFRestriction(true);
					}
					negatedRestrictionsGroups.add(restrictionGroup);
				}
				sPARQLQuery = new SPARQLQuery();
				sPARQLQuery.addRestriction(or);
				returnValue.setSPARQLQuery(sPARQLQuery);
				if (recordCardinalityArguments == null) {
					System.err.println(
							"WARNING in TranslateCondition.translateCondition in an <or> in " + owner + 
							": Found an <or> with nothing in it.");
				}
				returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
				return returnValue;
			}
			warning = 
					"WARNING in TranslateCondition.translateCondition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return returnValue;
		}
		if (Global.NUMERICAL_COMPARISON_OPERATORS.containsKey(tagName)) {
			warning = 
					"WARNING in TranslateCondition.translateCondition in " + owner +
					": I don't know how to translate a numerical comparison operator in a <condition>.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			//FIXME The arguments must be <cn> or <cardinality> blocks.
			return returnValue;
		}
		if (Global.QUANTIFIERS.contains(tagName)) {
			if (tagName.equalsIgnoreCase("exists")) {
				isDoneExistsBVar = false;				//Initialize
				isDoneExistsCondition = false;			//Initialize
				for (Node headConditionNode : conditionNodes) {
					conditionType = headConditionNode.getNodeName();
					conditionType = conditionType.toLowerCase();
					if (conditionType.equalsIgnoreCase("bvar")) {
						if (isDoneExistsBVar == false) {
							Global.translateBoundVariables(
									headConditionNode,"the <bvar> in an <exists> in "+owner);
							//There's no need to do anything with the bound variables
							isDoneExistsBVar = true;
						} else {
							System.err.println(
									"WARNING in TranslateCondition.translateCondition in an <exists> " +
									"in " + owner + ": Found more than 1 <bvar> in an <exists>.");
						}
					} else if (conditionType.equalsIgnoreCase("condition")) {
						if (isDoneExistsCondition == false) {
							existsConditionNodes = Global.getChildNodes(headConditionNode,owner);
							for (Node existsConditionNode : existsConditionNodes) {
								if (isDoneExistsCondition == true) {
									warning = 
											"WARNING in TranslateCondition.translateCondition " +
											"in the <condition> in an <exists> in " + owner +  
											": Found more than 1 thing at the top level of a " +
											"<condition>.";
									System.err.println(warning);
							    	Global.unableToRespondMessage.add(warning);
								}
								returnedValue = translateCondition( 
										existsConditionNode,
										definition,
										"the <condition> in an <exists> in "+owner);
								returnValue = returnedValue;
								isDoneExistsCondition = true;
							}
							if (isDoneExistsCondition == false) {
								System.err.println(
										"WARNING in TranslateCondition.translateCondition " +
										"in the <condition> in an <exists> in " + owner + ": Found an " + 
										"empty <condition> in an <exists> block.");
								isDoneExistsCondition = true;
							}
						} else {
							warning = 
									"WARNING in TranslateCondition.translateCondition in an <exists> " +
									"in " + owner + 
									": Found more than 1 <condition> in an <exists> block.";
							System.err.println(warning);
					    	Global.unableToRespondMessage.add(warning);
						}
					} else {
						warning = 
								"WARNING in TranslateCondition.translateCondition in an <exists> in " + 
								owner + ": Found an unexpected tag, <" + conditionType + 
								">, in an <exists> block.";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
				}
				return returnValue;
			}
			if (tagName.equalsIgnoreCase("forall")) {
				warning = 
						"WARNING in TranslateCondition.translateCondition in " + owner + 
						": <forall> has been removed from the formal language specification.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return returnValue;
			}
			warning = 
					"WARNING in TranslateCondition.translateCondition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return returnValue;
		}
		sawBooleanCondition = false;											//Initialize
		restrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>();		//Initialize
		if (Global.OBJECT_TYPES.containsKey(tagName)) {
			returnedValue = translateObjectArguments(conditionNodes,tagName,definition,owner);
			Global.typeOfSpecifiedObject = tagName;
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			recordCardinalityArguments = tempRCA;
			sawBooleanCondition = true;
		}
		if (Global.BEHAVIOR_ATTRIBUTES.containsKey(tagName)) {
			returnedValue = translateArguments(conditionNodes,tagName,1,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments =new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		}
		if (Global.COLORS.containsKey(tagName)) {
			returnedValue = translateColorArguments(conditionNodes,tagName,definition,owner);
			if (returnedValue == null) {
				tempRCA = null;
			} else {
				newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
				tempRCA = returnedValue.getRecordCardinalityArguments();
				restrictionsGroups.add(newRestrictions);
			}
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		} 
		if (Global.SPECIAL_RELATIONSHIPS.contains(tagName)) {
			newRestrictions = null;							//Just to make the compiler happy
			if (tagName.equalsIgnoreCase("part-of") ) {			//A special case
				newRestrictions = translatePartOfRelationshipArguments(
						conditionNodes,definition,owner);
			} else if (tagName.equalsIgnoreCase("same-object") ) {			//A special case
				newRestrictions = translateSameObjectRelationshipArguments(
						conditionNodes,definition,owner);
			} else {
				warning = 
						"WARNING in TranslateCondition.translateCondition in " + owner + 
						": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			restrictionsGroups.add(newRestrictions);
			sawBooleanCondition = true;
		}
		if (Global.BINARY_RELATIONSHIPS.containsKey(tagName)) {			
			returnedValue = translateArguments(conditionNodes,tagName,2,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		} 
		if (Global.TERNARY_RELATIONSHIPS.containsKey(tagName)) {
			returnedValue = translateArguments(conditionNodes,tagName,3,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		} 
		if (Global.ACTIONS.containsKey(tagName)) {		
			returnedValue = translateArguments(conditionNodes,tagName,2,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		}
		if (Global.BEHAVIOR_ATTRIBUTES_FILTER_FUNCTIONS.containsKey(tagName)) {
			returnedValue = translateFilterFunctionArguments(
					conditionNodes,tagName,1,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		} 
		if (Global.BINARY_RELATIONSHIPS_FILTER_FUNCTIONS.containsKey(tagName)) {
			returnedValue = translateFilterFunctionArguments(
					conditionNodes,tagName,2,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		} 
		if (Global.TERNARY_RELATIONSHIPS_FILTER_FUNCTIONS.containsKey(tagName)) {
			returnedValue = translateFilterFunctionArguments(
					conditionNodes,tagName,3,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);			
			}
			sawBooleanCondition = true;
		} 
		if (Global.ACTIONS_FILTER_FUNCTIONS.containsKey(tagName)) {
			returnedValue = translateFilterFunctionArguments(
					conditionNodes,tagName,2,definition,owner);
			newRestrictions = returnedValue.getSPARQLQuery().getRestrictions();
			tempRCA = returnedValue.getRecordCardinalityArguments();
			restrictionsGroups.add(newRestrictions);
			if (recordCardinalityArguments == null) {
				recordCardinalityArguments = tempRCA;
			} else {
				recordCardinalityArguments = new RecordCardinalityLogicalOperatorArguments(
						"or",recordCardinalityArguments,tempRCA);
			}
			sawBooleanCondition = true;
		}
		unionRestriction = new UnionRestriction();								//Initialize
		negatedRestrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>(restrictionsGroups.size());
		for (LinkedHashSet<Restriction> restrictionsGroup : restrictionsGroups) {
			group = new LinkedHashSet<Restriction>(1 + restrictionsGroup.size());
			if (negatedRestrictionsGroups.size() >= 1) {
				group.add(getNegatedDisjunction(negatedRestrictionsGroups)); 
			}
			group.addAll(restrictionsGroup);
			unionRestriction.addRestrictionsGroup(group);
			if (Global.hasPFFRestriction(group)) {
				unionRestriction.setHasPFFRestriction(true);
			}
			negatedRestrictionsGroups.add(restrictionsGroup);
		}
		if (sawBooleanCondition == false) {
			warning = 
					"WARNING in TranslateCondition.translateCondition in " + owner + 
					": Found an unexpected tag, <" + tagName + 
					">, where there should be a boolean condition.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		sPARQLQuery = new SPARQLQuery();
		sPARQLQuery.addRestriction(unionRestriction);
		returnValue.setSPARQLQuery(sPARQLQuery);
		returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
		return returnValue;
	}

	/**
	 * The <code>translateArguments</code> method translates a list of arguments that are inside a
	 * tag with the name of a behavior, a relationship, or an action into the proper SPARQL form.
	 *
	 * @param argumentsNodes are the arguments of the behavior, relationship, or action in XML.
	 * @param predicate is the name of the behavior, relationship, or action.
	 * @param ary specifies the number of arguments that this behavior, relationship, or action expects: 
	 * 1 (unary), 2 (binary) or 3 (ternary).
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in the &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return three values: 1) a <code>RecordCardinalityArguments</code> object with information that should 
	 * be included in a call to the filter function, "RecordCardinality", 2) the value 
	 * <b><code>null</code></b>, and 3) a list of the restrictions generated by this method.
	 */
	private static TranslateConditionReturnValue translateArguments(
			ArrayList<Node> argumentsNodes, String predicate, Integer ary, Definition definition, 
			String owner) 
	{
		Restriction aNewRestriction;				//One of the restrictions generated by this method
		String predicateVariable;					//A new variable for the behavior, relationship, or action
		TranslateConditionReturnValue returnValue;
		RecordCardinalityEventArguments recordCardinalityArguments;		//One of the return values
		SPARQLQuery sPARQLQuery;										//One of the return values
		LinkedHashSet<Restriction> newRestrictions;						//The restrictions in sPARQLQuery
		
		returnValue = new TranslateConditionReturnValue();
		if (collectArguments(argumentsNodes,predicate,ary,definition,owner) == false) { return returnValue; }
		recordCardinalityArguments = null;										//Initialize
		newRestrictions = new LinkedHashSet<Restriction>(6);					//Initialize
		predicateVariable = Global.createNewVariable(predicate);
		aNewRestriction = new StringRestriction(
				predicateVariable + " rdf:type/rdfs:subClassOf* msee:" + 
				Global.PREDICATES.get(predicate) + " .");
		newRestrictions.add(aNewRestriction);
		recordCardinalityArguments = new RecordCardinalityEventArguments(predicateVariable);
		aNewRestriction = new StringRestriction(
				predicateVariable + " msee:" + Global.ARGUMENT_TYPES.get(0) + " " + 
				arguments.get(0) + " .");
		newRestrictions.add(aNewRestriction);
		recordCardinalityArguments.addArgument(new RecordCardinalityObjectArguments(arguments.get(0)));
		if (ary > 1) {
			aNewRestriction = new StringRestriction(
					predicateVariable + " msee:" + Global.ARGUMENT_TYPES.get(1) + " " + 
					arguments.get(1) + " .");	
			newRestrictions.add(aNewRestriction);
			recordCardinalityArguments.addArgument(new RecordCardinalityObjectArguments(arguments.get(1)));
		}
		if (ary > 2) {
			aNewRestriction = new StringRestriction(
					predicateVariable + " msee:" + Global.ARGUMENT_TYPES.get(2) + " " + 
					arguments.get(2) + " .");			
			newRestrictions.add(aNewRestriction);
			recordCardinalityArguments.addArgument(new RecordCardinalityObjectArguments(arguments.get(2)));
		}
		aNewRestriction = getTimeLocationRestriction(predicateVariable,false);
		if (aNewRestriction != null) {
			newRestrictions.add(aNewRestriction);
			if (timePeriod != null) {
				recordCardinalityArguments.setTimePeriod(timePeriod.toString());
			}
			if (location != null) {
				recordCardinalityArguments.setLocation(location.toString());
			}
		}
		if (Global.INCLUDE_CONFIDENCE_BLOCKS) {
			newRestrictions.add(getConfidenceRestriction(predicateVariable,newRestrictions));
		}
		
		sPARQLQuery = new SPARQLQuery();
		sPARQLQuery.addRestrictions(newRestrictions);
		returnValue.setSPARQLQuery(sPARQLQuery);
		returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
		return returnValue;
	}

	/**
	 * The <code>translateObjectArguments</code> method translates a list of arguments that are inside a
	 * tag with the name of an object into the proper SPARQL form.
	 *
	 * @param argumentsNodes are the arguments of the object in XML.
	 * @param object is the name of the object.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return three values: 1) a <code>RecordCardinalityArguments</code> object with information that should 
	 * be included in a call to the filter function, "RecordCardinality", 2) the value 
	 * <b><code>null</code></b>, and 3) a list of the restrictions generated by this method.
	 */
	private static TranslateConditionReturnValue translateObjectArguments(
			ArrayList<Node> argumentsNodes, String object, Definition definition, String owner) 
	{
		Restriction aNewRestriction;				//One of the restrictions generated by this method
		TranslateConditionReturnValue returnValue;
		RecordCardinalityObjectArguments recordCardinalityArguments;	//One of the return values
		SPARQLQuery sPARQLQuery;										//One of the return values
		LinkedHashSet<Restriction> newRestrictions;						//The restrictions in sPARQLQuery
		
		if (collectArguments(argumentsNodes,object,1,definition,owner) == false) { 
			return new TranslateConditionReturnValue();
		}
		recordCardinalityArguments = null;										//Initialize
		newRestrictions = new LinkedHashSet<Restriction>(3);					//Initialize
		aNewRestriction = new StringRestriction(
				arguments.get(0) + " rdf:type/rdfs:subClassOf* msee:" + 
				Global.OBJECT_TYPES.get(object) + " .");
		newRestrictions.add(aNewRestriction);
		recordCardinalityArguments = new RecordCardinalityObjectArguments(arguments.get(0));
		aNewRestriction = getTimeLocationRestriction(arguments.get(0),true);
		if (aNewRestriction != null) {
			newRestrictions.add(aNewRestriction);
			if (timePeriod != null) {
				recordCardinalityArguments.setTimePeriod(timePeriod.toString());
			}
			if (location != null) {
				recordCardinalityArguments.setLocation(location.toString());
			}
		}
		if (Global.INCLUDE_CONFIDENCE_BLOCKS) {
			newRestrictions.add(getConfidenceRestriction(arguments.get(0),newRestrictions));
		}
		returnValue = new TranslateConditionReturnValue();
		sPARQLQuery = new SPARQLQuery();
		sPARQLQuery.addRestrictions(newRestrictions);
		returnValue.setSPARQLQuery(sPARQLQuery);
		returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
		return returnValue;
	}
	
	/**
	 * The <code>translateColorArguments</code> method translates a list of arguments that 
	 * are inside a tag with the name of a color into the proper SPARQL form.
	 *
	 * @param argumentsNodes are the arguments of the color in XML.
	 * @param color is the name of the color.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return three values: 1) a <code>RecordCardinalityArguments</code> object with information that should 
	 * be included in a call to the filter function, "RecordCardinality", 2) the value 
	 * <b><code>null</code></b>, and 3) a list of the restrictions generated by this method.
	 */
	private static TranslateConditionReturnValue translateColorArguments(
			ArrayList<Node> argumentsNodes, String color, Definition definition, String owner)
	{
		StringRestriction timeLocationRestriction;	//A restriction on the time and/or location
		TranslateConditionReturnValue returnValue;
		RecordCardinalityFunctionArguments recordCardinalityArguments;	//One of the return values
		SPARQLQuery sPARQLQuery;										//One of the return values
		LinkedHashSet<Restriction> newRestrictions;						//The restrictions in sPARQLQuery

		returnValue = new TranslateConditionReturnValue();
		if (collectArguments(argumentsNodes,color,1,definition,owner) == false) { return returnValue; }
		recordCardinalityArguments = null;										//Initialize
		
		//The SPARQL query lines
		newRestrictions = new LinkedHashSet<Restriction>(2);		//?torso1_2 msee:hasColor "red".
		newRestrictions.add(new StringRestriction(
				arguments.get(0) + " msee:hasColor \"" + Global.COLORS.get(color) + "\" ."));
		timeLocationRestriction = getTimeLocationRestriction(arguments.get(0),true);
		if (timeLocationRestriction != null) {
			newRestrictions.add(timeLocationRestriction);
		}

		//The filter function
		recordCardinalityArguments = new RecordCardinalityFunctionArguments("IsColor");
		if (timePeriod != null) {			//A time restriction
			recordCardinalityArguments.setTimePeriod(timePeriod.toString());
		}
		if (location != null) {			//A time restriction
			recordCardinalityArguments.setLocation(location.toString());
		}
		recordCardinalityArguments.addArgument(new RecordCardinalityObjectArguments(arguments.get(0)));
		recordCardinalityArguments.addArgument(
				new RecordCardinalityColorArguments("\"" + Global.COLORS.get(color) + "\""));

		sPARQLQuery = new SPARQLQuery();
		sPARQLQuery.addRestrictions(newRestrictions);
		returnValue.setSPARQLQuery(sPARQLQuery);
		returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
		return returnValue;
	}


	/**
	 * The <code>translateFilterFunctionArguments</code> method translates a list of arguments that are inside 
	 * a tag with the name of a behavior, a relationship, or an action into a filter function with the proper 
	 * SPARQL form.
	 *
	 * @param argumentsNodes are the arguments of the behavior, relationship, or action in XML.
	 * @param predicate is the name of the behavior, relationship, or action.
	 * @param ary specifies the number of arguments that this behavior, relationship, or action expects: 
	 * 1 (unary), 2 (binary) or 3 (ternary).
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return three values: 1) a <code>RecordCardinalityArguments</code> object with information that should 
	 * be included in a call to the filter function, "RecordCardinality", 2) a list of the restrictions 
	 * generated by this method, and 3) the value <b><code>null</code></b>.
	 */
	private static TranslateConditionReturnValue translateFilterFunctionArguments(
			ArrayList<Node> argumentsNodes, String predicate, Integer ary, Definition definition, 
			String owner)
	{
		String filterFunction;					//The name of the filter function
		StringBuffer translation;				//The SPARQL translation of the given information 
		String comma;							//Specifies whether a comma should be included
		Integer count;							//For counting iterations of a loop
		String argument;						//One of the arguments of the filter function
		TranslateConditionReturnValue returnValue;
		RecordCardinalityFunctionArguments recordCardinalityArguments;//One of the return values
		SPARQLQuery sPARQLQuery;									//One of the return values
		LinkedHashSet<Restriction> newRestrictions;					//The restrictions in sPARQLQuery
		Restriction newRestriction;									//The only restriction in newRestrictions
	
		returnValue = new TranslateConditionReturnValue();
		if (collectArguments(argumentsNodes,predicate,ary,definition,owner) == false) { return returnValue; }
		recordCardinalityArguments = null;										//Initialize
		newRestrictions = new LinkedHashSet<Restriction>(1);		//Initialize
		filterFunction = Global.PREDICATES_FILTER_FUNCTIONS.get(predicate);
		translation = new StringBuffer("FILTER (fn:" + filterFunction + "(");
		recordCardinalityArguments = new RecordCardinalityFunctionArguments(filterFunction);
		comma = "";
		if (timePeriod != null) {			//A time restriction
			translation.append(timePeriod.toString());
			recordCardinalityArguments.setTimePeriod(timePeriod.toString());
			comma = ",";
		}
		if (location != null) {			//A time restriction
			translation.append(comma + location.toString());
			recordCardinalityArguments.setLocation(location.toString());
			comma = ",";
		}
		for (count = 0; count < arguments.size(); count++) {
			argument = Global.fixDatabaseIdentifier(arguments.get(count));
			translation.append(comma + "\"OBJECT_ID\"," + argument);
			recordCardinalityArguments.addArgument(new RecordCardinalityObjectArguments(argument));
			comma = ",";
		}
		translation.append(")) .");
		newRestrictions = new LinkedHashSet<Restriction>(1);		//Initialize	
		newRestriction = new StringRestriction(translation.toString());
		newRestriction.setHasPFFRestriction(true);
		newRestrictions.add(newRestriction);

		sPARQLQuery = new SPARQLQuery();
		sPARQLQuery.addRestrictions(newRestrictions);
		returnValue.setSPARQLQuery(sPARQLQuery);
		returnValue.setRecordCardinalityArguments(recordCardinalityArguments);
		return returnValue;
	}

	/**
	 * The <code>translatePartOfRelationshipArguments</code> method translates a list of arguments that 
	 * are inside a &lt;part-of&gt; tag.
	 *
	 * @param argumentsNodes are the arguments of the &lt;part-of&gt; tag in XML.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the restrictions generated by this method.
	 */
	private static LinkedHashSet<Restriction> translatePartOfRelationshipArguments(
			ArrayList<Node> argumentsNodes, Definition definition, String owner)
			{
		StringRestriction timeLocationRestriction;		//A restriction on the time and/or location
		LinkedHashSet<Restriction> returnValue;

		returnValue = new LinkedHashSet<Restriction>(2);	//?x_1 msee:isPartOf ?y_1 .
		if (collectArguments(argumentsNodes,"part-of",2,definition,owner) == false) { return returnValue; }
		returnValue.add(new StringRestriction(
				arguments.get(0) + " msee:isPartOf " + arguments.get(1) + " ."));
		timeLocationRestriction = getTimeLocationRestriction(arguments.get(1),true);
		if (timeLocationRestriction != null) {
			returnValue.add(timeLocationRestriction);
		}
		return returnValue;
	}

	/**
	 * The <code>translateSameObjectRelationshipArguments</code> method translates a list of arguments that 
	 * are inside a &lt;same-object&gt; tag.
	 *
	 * @param argumentsNodes are the arguments of the relationship in XML.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return all of the restrictions generated by this method.
	 */
	private static LinkedHashSet<Restriction> translateSameObjectRelationshipArguments(
			ArrayList<Node> argumentsNodes, Definition definition, String owner)
			{
		StringRestriction timeLocationRestriction;		//A restriction on the time and/or location
		LinkedHashSet<Restriction> returnValue;

		returnValue = new LinkedHashSet<Restriction>(2);	//FILTER (?x_1 = ?y_1) .
		if (collectArguments(argumentsNodes,"same-object",2,definition,owner) == false) { 
			return returnValue; 
		}
		returnValue.add(new StringRestriction(
				"FILTER (" + arguments.get(0) + " = " + arguments.get(1) + ") ."));
		timeLocationRestriction = getTimeLocationRestriction(arguments.get(1),true);
		if (timeLocationRestriction != null) {
			returnValue.add(timeLocationRestriction);
		}
		return returnValue;
	}

	/**
	 * The <code>translateArgument</code> method returns the SPARQL translation for an argument that
	 * is in a &lt;ci&gt;.
	 *
	 * @param xMLargument is an XML structure that represents the argument.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the SPARQL translation of the argument.
	 */
	public static String translateArgument(Node xMLargument, String owner) {
		String argumentText;			//The XML argument's text 
		String argumentSPARQL;			//The argument in SPARQL form		//FIXME: This should be type SPARQL, not String
		
		argumentSPARQL = null;			//Initialize
		argumentText = ((Element)xMLargument).getTextContent().trim();
		if (Global.isVariable(argumentText)) {		//Is it a variable?
			argumentSPARQL = Global.translateVariable(argumentText);
		} else {							//It must be a constant
			argumentSPARQL = Global.translateName(argumentText,"a <ci> in "+owner);
		}
		return argumentSPARQL;
	}

	/**
	 * The <code>collectArguments</code> method extracts any arguments found in an XML structure and stores
	 * them in global variables. This method should be called at the beginning of every method that 
	 * translates arguments (<code>translateArguments()</code>, <code>translateObjectArguments()</code>, 
	 * <code>translateColorArguments()</code>, <code>translateFilterFunctionArguments()</code>, 
	 * <code>translatePartOfRelationshipArguments()</code>, and 
	 * <code>translateSameObjectRelationshipArguments()</code>).
	 *
	 * @param argumentsNodes is the XML structure with the arguments.
	 * @param predicate is the predicate whose arguments are being collected.
	 * @param ary specifies the number of arguments that this predicate expects: 
	 * 1 (unary), 2 (binary) or 3 (ternary).
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in the &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return <b><code>true</code></b> if the required number of arguments were found, or 
	 * <b><code>false</code></b> if the attempt to collect the arguments failed.
	 */
	private static Boolean collectArguments(
			ArrayList<Node> argumentsNodes, String predicate, Integer ary, 
			Definition definition, String owner) {
		String argumentType;						//The argument is a time, location, name, or variable
		String translatedArgument;					//An argument after it has been translated
		String argumentMSEEType;					//The MSEE type of an argument
		ArrayList<String> argumentsMSEETypes;		//The MSEE types of all of the arguments
		String unableToRespondMessage;				//A message to return if the system cannot respond
		String s;									//Specifies if an 's' should be added to the end of a noun
		String warning;								//A message to let the user know there might be a problem

		location = null;									//Initialize
		timePeriod = null;									//Initialize
		arguments = new ArrayList<String>(ary);				//Initialize
		if (ary == 1) { s = ""; }			//Singular noun
		else { s = "s"; }					//Plural noun
		for (Node argumentNode : argumentsNodes ) {
			argumentType = argumentNode.getNodeName();
			if (argumentType.equalsIgnoreCase("time")) {				//A time restriction
				timePeriod = getTimePeriod(
						argumentNode,predicate,timePeriod,definition,"a <"+predicate+"> in "+owner);
			} else if (argumentType.equalsIgnoreCase("location")) {	//A location restriction
				location = getLocation(
						argumentNode,predicate,location,definition,"a <"+predicate+"> in "+owner);
			} else if (argumentType.equalsIgnoreCase("ci")) {			//A named constant or a variable
				translatedArgument = translateArgument(argumentNode,owner);
				if (translatedArgument == null) {		//An unknown object was encountered
					return false;
				}
				if (		//Is predicate an MSEE object type of a variable?
						(Global.OBJECT_TYPES.containsKey(predicate)) &&
						(Global.isVariable(translatedArgument))) {
					Global.mseeTypes.put(translatedArgument,predicate);
				}
				arguments.add(translatedArgument);
			} else {
				warning = 
						"WARNING in TranslateCondition.collectArguments in a <" + 
								predicate + "> in " + owner + ": Found an unexpected tag, <" + 
								argumentType + ">.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
		}
		if (arguments.size() > ary) {
			warning = 
					"WARNING in TranslateCondition.collectArguments in a <" +  
					predicate + "> in " + owner + ": Found more than " + ary + " argument" + s + 
					" for a <" + predicate + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(
	    			warning);
		} else if (arguments.size() < ary) {
			warning = 
					"WARNING in TranslateCondition.collectArguments in a <" + 
					predicate + "> in " + owner + ": Unable to find " + ary + 
					" argument" + s + " for a <" + predicate + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return false;
		}
		argumentsMSEETypes = new ArrayList<String>(arguments.size());
		for (String argument : arguments) {
			argumentMSEEType = Global.mseeTypes.get(argument);
			if (argumentMSEEType == null) {
				if ( ! argument.matches("data:obs-.*")) {	//No warning for observers
					warning = 
							"WARNING in TranslateCondition.collectArguments in a <" + 
									predicate + "> in " + owner + ": Found identifier that was " +
									"never defined, \"" + Global.removePrefix(argument) + "\".";
					System.err.println(warning);
					//munwai commented out for "food" mapping to Eat
					//Global.unableToRespondMessage.add(warning);
				}
			} else {
				argumentsMSEETypes.add(argumentMSEEType);
			}
		}
		unableToRespondMessage = Global.checkUnableToRespond(predicate,argumentsMSEETypes);
		if (unableToRespondMessage != null) {
			if (definition == null) {							//If we're in the query statement
				Global.unableToRespondMessage.add(unableToRespondMessage);
			} else {
				definition.addUnableToRespondMessage(unableToRespondMessage);
			}
		}
		return true;
	}

	/**
	 * The <code>getNegatedRestrictions</code> method constructs and returns the negation of a disjunction of
	 * the groups of restrictions in its input parameter. For example, if the input is [A,B,C], the return
	 * value is NOT(A OR B OR C).
	 *
	 * @param restrictionsGroups is a list of groups of restrictions.
	 * @return 
	 * the negation of the disjunction of the groups in <code>restrictionsGroups</code>.
	 */
	private static UnionRestriction getNegatedDisjunction(
			ArrayList<LinkedHashSet<Restriction>> restrictionsGroups) {
		UnionRestriction returnValue;

		returnValue = new UnionRestriction();
		returnValue.negateRestriction();
		for (LinkedHashSet<Restriction> restrictionsGroup : restrictionsGroups) {
			returnValue.addRestrictionsGroup(restrictionsGroup);
			if (Global.hasPFFRestriction(restrictionsGroup)) {
				returnValue.setHasPFFRestriction(true);
			}
		}
		return returnValue;
	}

	/**
	 * The <code>getConfidenceRestriction</code> method creates a restriction that handles a confidence value.
	 * The given restrictions are included in the new restriction.
	 *
	 * @param variable is the variable that the confidence value is for.
	 * @param restrictions is a list of restrictions that should be included in the return value.
	 * @return a new optional restriction that tests the variable for a confidence value and handles it 
	 * appropriately.
	 */
	private static Restriction getConfidenceRestriction(
			String variable, LinkedHashSet<Restriction> restrictions) {
		String confidenceVariable;					//A variable to hold the confidence value
		OptionalRestriction returnValue;

		returnValue = new OptionalRestriction();
		for (Restriction restriction : restrictions) {
			returnValue.addRestriction(restriction);
		}
		confidenceVariable = Global.createNewVariable("confidence");
		returnValue.addRestriction(new StringRestriction(
				variable + " msee:hasConfidence " + confidenceVariable + " ."));
		returnValue.addRestriction(new StringRestriction(
				"FILTER (fn:UpdateConfidence(" + confidenceVariable + ")) ."));
		return returnValue;
	}

	/**
	 * The <code>getTimeLocationRestriction</code> method returns a restriction that insures that the time
	 * and location are correct.
	 *
	 * @param theArguments specifies the thing that will be restricted to be in <code>timePeriod</code>  and 
	 * at <code>location</code>.
	 * @param isObject specifies the type of that thing. It should be <b><code>true</code></b> for objects and
	 * <b><code>false</code></b> otherwise.
	 * @return a restriction that requires <code>arguments</code> to be within <code>timePeriod</code> and 
	 * located at <code>location</code>. It is a filter function restriction in <code>String</code> form, such 
	 * as "FILTER (fn:IsAtTimeLocation(...)) .". 
	 */
	private static StringRestriction getTimeLocationRestriction(String theArguments, Boolean isObject) {
		StringBuffer returnValue;
		
		if ((timePeriod == null) && (location == null)) {
			return null;													//No restrictions
		}

		returnValue = new StringBuffer("FILTER (fn:IsAt");
		if (location != null) {
			if (timePeriod != null) {
				returnValue.append(											//Location and time restrictions
						"TimeLocation(" + timePeriod.toString() + "," + location.toString() + ",\""); 
			} else {														//Just a location restriction
				returnValue.append("Location(" + location.toString() + ",\"");
			}
		} else {
			if (timePeriod != null) {										//Just a time restriction
				returnValue.append("Time(" + timePeriod.toString() + ",\"");
			}
		}
		if (isObject) {
			returnValue.append("OBJECT_ID");
		} else {
			returnValue.append("EVENT_ID");
		}
		returnValue.append("\"," + Global.fixDatabaseIdentifier(theArguments) + ")) .");		//TODO: Make sure identifiers are spelled correctly: person-1 or data:person-1 or #person-1

		return new StringRestriction(returnValue.toString());
	}
	
	/**
	 * The <code>getTimePeriod</code> method returns the <code>TimePeriod</code> object that has the given
	 * name.
	 *
	 * @param timeNode is the &lt;time&gt; node.
	 * @param outerTag is the XML tag that surrounds the &lt;time&gt; tag.
	 * @param previousTimeId is the value of a time identifier that has previously been found for 
	 * <code>outerTag</code>. It should be <b><code>null</code></b>.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return an object with information about the time in <code>timeNode</code>.
	 */
	private static TimePeriodDefinition getTimePeriod(
			Node timeNode, String outerTag, TimePeriodDefinition previousTimeId, 
			Definition definition, String owner) {
		String timeId;							//The identifier of the desired time
		String warning;							//A message to let the user know there might be a problem
		TimePeriodDefinition returnValue;

		if (previousTimeId != null) {
			warning = 
					"WARNING in TranslateCondition.getTimePeriod in " + owner + 
					": Found more than 1 <time> restriction for a <" + outerTag + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		timeId = timeNode.getTextContent().trim();
		if (! Global.isTimeId(timeId)) {
			System.err.println(
					"WARNING in TranslateCondition.getTimePeriod in a <time> in " + owner + ": Found \"" + 
					timeId + "\" in a <time> tag, but a time identifier should begin with 'time-'.");
		}
		if (Global.hasDefinition(timeId)) {
			returnValue = (TimePeriodDefinition)Global.getDefinition(timeId,"a <time> in "+owner);
			if (definition == null) {
				Global.timePeriodDefinitionsUsedInLastQuery.put(timeId,returnValue);
			} else {
				definition.addTimePeriodDefinition(timeId,returnValue);
			}
			return returnValue;
		}
		warning = 
				"WARNING in TranslateCondition.getTimePeriod in a <time> in " + owner + 
				": Found an undefined identifier, \"" + timeId + "\".";
		System.err.println(warning);
    	Global.unableToRespondMessage.add(warning);
		return null;
	}

	/**
	 * The <code>getLocation</code> method returns the <code>Location</code> object that has the given
	 * name.
	 *
	 * @param locationNode is the &lt;location&gt; node.
	 * @param outerTag is the XML tag that surrounds the &lt;location&gt; tag.
	 * @param previousLocationId is the value of a location identifier that has previously been found for 
	 * <code>outerTag</code>. It should be <b><code>null</code></b>.
	 * @param definition is the definition of the object or set in which this condition is found. A value of 
	 * <b><code>null</code></b> means that this condition is in an &lt;exists&gt; &lt;condition&gt; in the 
	 * &lt;QueryStatement&gt;.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return an object with information about the location named <code>locationId</code>.
	 */
	private static LocationDefinition getLocation(
			Node locationNode, String outerTag, LocationDefinition previousLocationId, 
			Definition definition, String owner) {
		String locationId;				//The identifier of the desired location
		String warning;							//A message to let the user know there might be a problem
		LocationDefinition returnValue;

		if (previousLocationId != null) {
			warning = 
					"WARNING in TranslateCondition.getLocation in " + owner +  
					": Found more than 1 <location> restriction for a <" + outerTag + ">.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		locationId = locationNode.getTextContent().trim();
		if (! Global.isLocationId(locationId)) {
			System.err.println(
					"WARNING in TranslateCondition.getLocation in a <location> in " + 
					owner + ": Found \"" + locationId + 
					"\" in a <location> tag, but a location identifier should begin with 'loc-'.");
		}
		if (Global.hasDefinition(locationId)) {
			returnValue = (LocationDefinition)Global.getDefinition(locationId,owner);
			if (definition == null) {
				Global.locationDefinitionsUsedInLastQuery.put(locationId,returnValue);
			} else {
				definition.addLocationDefinition(locationId,returnValue);
			}
			return returnValue;
		}
		warning = 
				"WARNING in TranslateCondition.getLocation in a <location> in " + owner + 
				": Found an undefined identifier, \"" + locationId + "\".";	
		System.err.println(warning);
    	Global.unableToRespondMessage.add(warning);
		return null;
	}					
}