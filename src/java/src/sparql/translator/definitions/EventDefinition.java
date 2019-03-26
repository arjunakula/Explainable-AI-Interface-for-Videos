package sparql.translator.definitions;

import java.util.ArrayList;

import org.w3c.dom.Node;

import sparql.translator.utilities.Global;

/**
 * The <code>EventDefinition</code> class holds important information about an event definition in an XML 
 * query.
 *
 * @author Ken Samuel
 * @version 1.0, Dec 2, 2013
 * @since 1.6
 */
public class EventDefinition extends Definition {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;
	
	/** <code>set1</code> is the first set that is referenced in this event (if any). */
	private SetId set1;
	
	/**  <code>set2</code> is the second set that is referenced in this event (if there are two). */
	private SetId set2;
	
	/** <code>setDefinition1</code> is the definition of <code>set1</code>. */
	private SetDefinition setDefinition1;
	
	/** <code>setDefinition2</code> is the definition of <code>set2</code>. */
	private SetDefinition setDefinition2;
	
	/** 
	 * <code>setIds</code> is the names of the sets referenced in this event, in the order of their 
	 * appearance. If the special word "UNION" or "INTERSECTION" is found in this list, it is followed by
	 * the two sets to be unioned or intersected. 
	 */
	
	/** <code>number1</code> is the first number found in this event definition (if any). */
	private Double number1;
	
	/** <code>number2</code> is the second number found in this event definition (if there are two). */
	private Double number2;
	
	/** <code>minimum</code> is the minimum cardinality of that set. */
	private String minimum;
	
	/** <code>maximum</code> is the maximum cardinality of that set. */
	private String maximum;
	
	/** 
	 * <code>isGuaranteed</code> specifies whether this event is guaranteed to be true or false. If the 
	 * event's truth value may vary, then the value of this variable is <b><code>null</code></b>. 
	 */
	private Boolean isGuaranteed;
	
	/** <code>logicalOperator</code> is the name of a logical operator (and/or/not) found in this event's 
	 * definition, if any. 
	 */
	private String logicalOperator;
	
	/** <code>event1</code> is the first operand of the logical operator, if one exists. */
	private EventDefinition event1;
	
	/** <code>event2</code> is the second operand of the logical operator, if one exists. The "not" logical 
	 * operator does not have a second operand. 
	 */
	private EventDefinition event2;
	
	
	/**
	 * The <code>EventDefinition</code> constructor initializes the global variables.
	 *
	 * @param eventNode is the XML definition of the event.
	 */
	public EventDefinition(Node eventNode) {
		super(eventNode);
		set1 = null;
		set2 = null;
		setDefinition1 = null;
		setDefinition2 = null;
		number1 = null;
		number2 = null;
		minimum = null;
		maximum = null;
		isGuaranteed = null;
		logicalOperator = null;
		event1 = null;
		event2 = null;
	}

	/**
	 * The <code>translateEvent</code> method translates this event from XML to SPARQL. As input, it uses the
	 * value of the global variable <code>definitionInXML</code>, and it saves the result in the global 
	 * variables of this object. 
	 * 
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void translateEvent(String owner) {
		Node eventNode;					//One of the event nodes
		String warning;					//A message to let the user know there might be a problem

		eventNode = Global.getChildNode(definitionInXML,owner);
		if (eventNode != null) {
			translateEventCondition(eventNode,owner);
		} else {
			if (id.equals("")) {
				warning = "an <event>";
			} else {
				warning = "the event, \"" + id + "\"";
			}
			warning = 
					"WARNING in EventDefinition.translateEvent in " + owner + 
					": Unable to find anything in the definition of " + warning + ".";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}
	
	/**
	 * The <code>translateEventCondition</code> method translates the condition of this event from XML to 
	 * SPARQL.
	 *
	 * @param conditionTag is an XML tag with contents that can be true or false.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@SuppressWarnings("cast")
	private void translateEventCondition(Node conditionTag, String owner) {
		String tagName;					//The name of an XML tag
		String operator;				//A binary numerical comparison operator in SPARQL format (>, etc.)
		Integer numOperands;			//The number of operands that have been found
		ArrayList<Node> operands;		//A list of nodes that includes the operands of an operator
		SetDefinition set;				//A set that is created to represent an <exists>
		String setId;					//The name of that set
		Integer numExists;				//The number of <exists> nodes that have been encountered so far
		String warning;					//A message to let the user know there might be a problem

		numExists = 0;									//Initialize
		tagName = conditionTag.getNodeName();
		tagName = tagName.toLowerCase();
		if (Global.LOGICAL_OPERATORS.contains(tagName)) {		//Is it a logical operator?
			logicalOperator = tagName;				//"and", "or", or "not"
			Global.changedDefinition(this);
			operands = Global.getChildNodes(conditionTag,owner);
			translateLogicalExpression(operands,conditionTag,owner);
			return;
		}
		if (Global.NUMERICAL_COMPARISON_OPERATORS.containsKey(tagName)) {	//A numeric comparison operator
			operator = Global.NUMERICAL_COMPARISON_OPERATORS.get(tagName);
			if (operator.equals("!=")) {
				warning = 
						"WARNING in EventDefinition.translateEventCondition in a <" + tagName + ">  in " + 
						owner + ": <ne> is not part of the formal language specification.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			operands = Global.getChildNodes(conditionTag,owner);
			numOperands = 0;			//Initialize
			for (Node operand : operands) {
				translateNumericExpression(operand,"a <"+tagName+"> in "+owner);
				numOperands++;
				if (numOperands == 1) {
					if (number1 != null) {						//A number was listed first
						operator = Global.reverseOperator(operator);
					} 
				} else if (number2 != null) {			//Case 1: There are two numbers (and no sets)
					switch (operator) {						//It's guaranteed to be true or false
					case "=":
						isGuaranteed = ((double)number2 == (double)number1); //Numbers, not pointers
						break;
					case "!=":
						isGuaranteed = ((double)number2 != (double)number1); //Numbers, not pointers
						break;
					case "<":
						isGuaranteed = ((double)number2 < (double)number1);  //Numbers, not pointers
						break;
					case ">":
						isGuaranteed = ((double)number2 > (double)number1);  //Numbers, not pointers
						break;
					case "<=":
						isGuaranteed = ((double)number2 <= (double)number1); //Numbers, not pointers
						break;
					case ">=":
						isGuaranteed = ((double)number2 >= (double)number1); //Numbers, not pointers
						break;
					default:
						warning = 
								"WARNING in EventDefinition.translateEventCondition in " + owner + 
								": Bug detected. Contact Ken Samuel.";
						System.err.println(warning);
				    	Global.unableToRespondMessage.add(warning);
					}
					Global.changedDefinition(this);
					return;
				}
			}
			if (numOperands < 2) {
				warning = 
						"WARNING in EventDefinition.translateEventCondition in a <" + tagName + "> in " + 
						owner + ": <" + tagName + "> is a binary operator, so it needs 2 operands.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			if (number1 != null) {			//Case 2: There is one set and one number
				if (operator.equals("=")) {
					setMinimum(number1);
					setMaximum(number1);
				} else if (operator.equals("<")) {
					setMinimum(0.);
					number1--;
					Global.changedDefinition(this);
					setMaximum(number1);
				} else if (operator.equals(">")) {
					number1++;
					setMinimum(number1);
					maximum = null;						//There's no upper bound
					Global.changedDefinition(this);
				} else if (operator.equals("<=")) {
					setMinimum(0.);
					setMaximum(number1);
				} else if (operator.equals(">=")) {
					setMinimum(number1);
					maximum = null;						//There's no upper bound
					Global.changedDefinition(this);
				}
				return;
			}
			if (set2 != null) {			//Case 3: There are two sets (and no numbers)
				switch (operator) {
				case "=":
					setMinimum(set2,owner);
					setMaximum(set2,owner);
					break;
				case "<":	
					setMinimum(0.);
					setMaximum(set2,-1,owner);
					break;
				case ">":	
					setMinimum(set2,+1,owner);
					maximum = null;						//There's no upper bound
					Global.changedDefinition(this);
					break;
				case "<=":	
					setMinimum(0.);
					setMaximum(set2,owner);
					break;
				case ">=":	
					setMinimum(set2,owner);
					maximum = null;						//There's no upper bound
					Global.changedDefinition(this);
					break;
				case "!=":
					setMinimum(0.);
					maximum = null;			
					Global.changedDefinition(this);
					break;
				default:
					warning = 
							"WARNING in EventDefinition.translateEventCondition in a <" + tagName + ">  in " + 
							owner + ": Bug detected. Contact Ken Samuel.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
					setMinimum(0.);
					maximum = null;			
					Global.changedDefinition(this);
				}
			} else {
				warning = 
						"WARNING in EventDefinition.translateEventCondition in a <" + tagName + "> in " + 
						owner + ": <" + tagName + "> has an illegal argument.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			return;
		}
		if (Global.QUANTIFIERS.contains(tagName)) {
			if (tagName.equalsIgnoreCase("exists")) {
				numExists++;
				set = new SetDefinition(conditionTag);
				set.translateObjectOrSet(false,owner);
				setId = "set-exists-"+numExists;
				set.setId(setId);
				set.setPersists(getPersists());
				Global.storeDefinition(set,owner);
				setMinimum(1.);
				set1 = new SetId(setId);
				setDefinition1 = set;
				unableToRespondMessage.addAll(setDefinition1.getUnableToRespondMessage());
				Global.changedDefinition(this);
				return;
			} 
			if (tagName.equalsIgnoreCase("forall")) {
				warning = 
						"WARNING in EventDefinition.translateEventCondition in a <" + tagName + ">  in " + 
						owner + ": <forall> has been removed from the formal language specification.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return;
			}
			warning = 
					"WARNING in EventDefinition.translateEventCondition in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return;
		}
		if (
				(Global.OBJECT_TYPES.containsKey(tagName)) || 
				(Global.COLORS.containsKey(tagName)) || 
				(Global.PREDICATES.containsKey(tagName)) || 
				(Global.PREDICATES_FILTER_FUNCTIONS.containsKey(tagName))) {
			set = new SetDefinition(conditionTag);				//Treat its condition as a set with no bvars
			set.translateCondition(conditionTag,owner);
			setId = "set-" + getId();
			set.setId(setId);
			set.setPersists(getPersists());
			Global.storeDefinition(set,owner);			//It's guaranteed to be a new name
			setMinimum(1.);
			set1 = new SetId(setId);
			setDefinition1 = set;
			unableToRespondMessage.addAll(setDefinition1.getUnableToRespondMessage());
			Global.changedDefinition(this);
			return;
		}
		warning = 
				"WARNING in EventDefinition.translateEventCondition in " + owner + 
				": Found an unexpected tag, <" + tagName + "> in an <event>.";
		System.err.println(warning);
    	Global.unableToRespondMessage.add(warning);
	}

	/**
	 * The <code>translateLogicalExpression</code> method translates a logical operator, "not", "or", or 
	 * "and", and its operands.
	 *
	 * @param operands is a list with all of the operands of <code>logicalOperator</code>.
	 * @param conditionTag is an XML tag with contents that can be true or false.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private void translateLogicalExpression(ArrayList<Node> operands, Node conditionTag, String owner)
	{
		String warning;							//A message to let the user know there might be a problem

		if (operands.size() >= 1) {
			event1 = new EventDefinition(conditionTag);	
			event1.setId(getId()+"-1");
			event1.translateEventCondition(operands.get(0),"a <"+logicalOperator+"> in "+owner);
			unableToRespondMessage.addAll(event1.getUnableToRespondMessage());
			if (operands.size() >= 2) {
				if (logicalOperator.equals("not")) {
					warning = 
							"WARNING in EventDefinition.translateLogicalExpression in a <not> in " + 
							owner + ": <not> is a unary operator, so it cannot have 2 operands.";
					System.err.println(warning);
			    	Global.unableToRespondMessage.add(warning);
				} else if (operands.size() == 2) {
					event2 = new EventDefinition(conditionTag);	
					event2.setId(getId()+"-2");
					event2.translateEventCondition(operands.get(1),"a <"+logicalOperator+"> in "+owner);
					unableToRespondMessage.addAll(event2.getUnableToRespondMessage());
				} else {		//At least three operands
					event2 = new EventDefinition(conditionTag);
					event2.setId(getId()+"-2");
					event2.setLogicalOperator(logicalOperator);
					operands.remove(0);
					event2.translateLogicalExpression(operands,conditionTag,owner);
				}
			}
			Global.changedDefinition(this);
		} else {
			warning = 
					"WARNING in EventDefinition.translateLogicalExpression in a <" + logicalOperator + 
					"> in " + owner + ": A <" + logicalOperator + "> operator needs an operand."; 
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}
	
	/**
	 * The <code>translateNumericExpression</code> method translates a portion of the XML query that 
	 * represents a number into SPARQL.
	 *
	 * @param numericExpression is a portion of the XML query that returns a number.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private void translateNumericExpression(Node numericExpression, String owner) {
		String numericExpressionName;			//The name of the head node
		SetId set;								//A set inside the numeric expression
		SetDefinition setDefinition;			//The definition of that set
		String warning;							//A message to let the user know there might be a problem

		numericExpressionName = numericExpression.getNodeName();
		numericExpressionName = numericExpressionName.toLowerCase();
		if (numericExpressionName.equalsIgnoreCase("cn")) {	//It's simply a constant number
			if (number1 == null) {
				number1 = Double.valueOf(numericExpression.getTextContent().trim());
				Global.changedDefinition(this);
			} else {
				number2 = Double.valueOf(numericExpression.getTextContent().trim());
				Global.changedDefinition(this);
			}
		} else if (numericExpressionName.equalsIgnoreCase("cardinality")) {			//A set's cardinality
			set = translateSet(
					Global.getChildNode(numericExpression,owner),"a <cardinality> in "+owner);
			if (set == null) {			//Something is wrong with the query; a WARNING message was printed
				return;
			}
			setDefinition = (SetDefinition)Global.getDefinition(set.getName(),"a <cardinality> in "+owner);
			if (set1 == null) {
					set1 = set;
					setDefinition1 = setDefinition;
				} else {
					set2 = set;
					setDefinition2 = setDefinition;
				}
			if (setDefinition != null) {
				unableToRespondMessage.addAll(setDefinition.getUnableToRespondMessage());
			}
			Global.changedDefinition(this);
		} else {
			warning = 
					"WARNING in EventDefinition.translateNumericExpression in " + owner + 
					": Found a <" + numericExpressionName + "> tag where a numeric expression is expected.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}
	
	/**
	 * The <code>translateSet</code> method translates a portion of the event definition that represents a 
	 * set.
	 *
	 * @param setNode is the XML tag of the set to be translated.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return the set in its translated form.
	 */
	private SetId translateSet(Node setNode, String owner) { 
		String setNodeName;						//The name of that node
		String setId;							//The identifier of a set
		ArrayList<Node> setOperands;			//For intersection or union, the operands of that set operator
		SetId setOperand1,setOperand2;			//For intersection or union, the operands of that set operator
		SetDefinition setDefinition;			//The definition of a new set
		Integer index;							//For looping through an array
		String warning;							//A message to let the user know there might be a problem
		SetId returnValue;

		returnValue = null;											//Initialize
		setOperand1 = null;											//Initialize
		setOperand2 = null;											//Initialize
		if (setNode == null) {
			warning = 
					"WARNING in EventDefinition.translateSet in " + owner + 
					": Nothing was found where a set was expected.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
			return null;
		}
		setNodeName = setNode.getNodeName();
		if (setNodeName.equals("ci")) {
			setId = setNode.getTextContent().trim();
			if (! Global.isSetId(setId)) {
				System.err.println(
						"WARNING in EventDefinition.translateSet in " +
						owner + ": The set name, \"" + setId + "\", should begin with \"set-\".");
			}
			returnValue = new SetId(setId);
		} else if (setNodeName.equals("intersection") || setNodeName.equals("union")) {						
			setOperands = Global.getChildNodes(setNode,owner);
			if (setOperands.size() > 0) {
				setOperand1 = translateSet(setOperands.get(0),"a <"+setNodeName+"> in "+owner);				
				for (index = 1; index < setOperands.size(); index++) {
					setOperand2 = translateSet(setOperands.get(index),"a <"+setNodeName+"> in "+owner);
					setDefinition = new SetDefinition();
					setDefinition.combineSets(
							setNodeName,
							setOperand1.getName(),
							setOperand2.getName(),
							"a <"+setNodeName+"> in "+owner);
					if (index == setOperands.size() - 1) {
						setDefinition.setPersists(getPersists());
					} else {
						setDefinition.setPersists(false);
					}
					Global.storeDefinition(setDefinition,"a <"+setNodeName+"> in "+owner);
					setOperand1 = new SetId(
							setDefinition.getId(),
							setNodeName,
							setOperand1,
							setOperand2);
				}
			} else {
				warning = 
						"WARNING in EventDefinition.translateSet in a <" + setNodeName + "> in " + owner + 
						": Found a <" + setNodeName + "> with nothing in it.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
				return setOperand1;
			}
			setDefinition = (SetDefinition)Global.getDefinition(
					setOperand1.getName(),
					"a <"+setNodeName+"> in "+owner);
			unableToRespondMessage.addAll(setDefinition.getUnableToRespondMessage());
			if (setDefinition1 == null) {
				setDefinition1 = setDefinition;
				Global.changedDefinition(this);
			} else if (setDefinition2 == null) {
				setDefinition2 = setDefinition;
				Global.changedDefinition(this);
			} else {
				warning = 
						"WARNING in EventDefinition.translateSet in a <"  + setNodeName + "> in " + owner + 
						": Bug detected. Contact Ken Samuel.";
				System.err.println(warning);
		    	Global.unableToRespondMessage.add(warning);
			}
			unableToRespondMessage = setDefinition.getUnableToRespondMessage();
			returnValue = new SetId(
					setDefinition.getId(),
					setNodeName,
					setOperand1,
					setOperand2);
		} else {
			warning = 
					"WARNING in EventDefinition.translateSet in " +
					owner + ": Found an unexpected tag, <" + 
					setNodeName + ">, where a set was expected.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		return returnValue;
	}
	
	/**
	 * The <code>setToString</code> method converts a set name into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param set is the set whose cardinality will be the minimum or maximum value for this event.
	 * @return the string that should be inserted into an IsTemporalRelationWithQuantities filter function.
	 */
	private static String setToString(SetId set) {
		return "\"COUNT_SET\",\"SET_NAME\",\"" + set.getName() + "\"";
	}
	
	/**
	 * The <code>setSetId</code> setter changes the value of the global variable,
	 * <code>set1</code>, a <b><code>String</code></b>.
	 *
	 * @param setIdIn is the new value that should be assigned to <code>setId1</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	public void setSetId(String setIdIn, String owner) {
		String warning;							//A message to let the user know there might be a problem

		if (set1 != null) {
			set1.setName(setIdIn);
			setDefinition1 = (SetDefinition)Global.getDefinition(setIdIn,owner);
			unableToRespondMessage = setDefinition1.getUnableToRespondMessage();
			Global.changedDefinition(this);
		} else {
			warning = 
					"WARNING in EventDefinition.setSetId in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
	}

	/**
	 * The <code>setMinimum</code> method converts a number into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param num is the number that will be the minimum value for this event.
	 */
	private void setMinimum(Double num) {
		minimum = "\"MIN_QUANTITY\",\""  + num + "\"";
		Global.changedDefinition(this);
	}

	/**
	 * The <code>setMinimum</code> method converts a set name into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param set is the set whose cardinality will be the minimum value for this event.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private void setMinimum(SetId set, String owner) {
		setMinimum(set,0,owner);
	}

	/**
	 * The <code>setMinimum</code> method converts a set name into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param set is the set whose cardinality will specify a boundary this event.
	 * @param modifier is either 0 or +1. If it is 0, then the minimum value is the given set's cardinality. 
	 * If it is +1, then the minimum value is the given set's cardinality plus one.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private void setMinimum(SetId set, Integer modifier, String owner) {
		String warning;							//A message to let the user know there might be a problem

		minimum = setToString(set);
		if (modifier == 0) {
			minimum = "\"MIN_QUANTITY\"," + minimum;
		} else if (modifier == +1) {
			minimum = "\"GT_QUANTITY\"," + minimum;
		} else {						//Illegal value of modifier
			warning = 
					"WARNING in EventDefinition.setMinimum in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		Global.changedDefinition(this);
	}
	
	/**
	 * The <code>setMaximum</code> method converts a number into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param num is the number that will be the maximum value for this event.
	 */
	private void setMaximum(Double num) {
		maximum = "\"MAX_QUANTITY\",\"" + num + "\"";
		Global.changedDefinition(this);
	}
	
	/**
	 * The <code>setMaximum</code> method converts a set name into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param set is the set whose cardinality will be the maximum value for this event.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private void setMaximum(SetId set, String owner) {
		setMaximum(set,0,owner);
	}
	
	/**
	 * The <code>setMaximum</code> method converts a set name into the string that should be inserted into 
	 * an IsTemporalRelationWithQuantities filter function.
	 *
	 * @param set is the set whose cardinality will be the maximum value for this event.
	 * @param modifier is either 0 or -1. If it is 0, then the maximum value is the given set's cardinality. 
	 * If it is -1, then the maximum value is the given set's cardinality minus one.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	private void setMaximum(SetId set, Integer modifier, String owner) {
		String warning;							//A message to let the user know there might be a problem

		maximum = setToString(set);
		if (modifier == 0) {
			maximum = "\"MAX_QUANTITY\"," + maximum;
		} else if (modifier == -1) {
			maximum = "\"LT_QUANTITY\"," + maximum;
		} else {						//Illegal value of modifier
			warning = 
					"WARNING in EventDefinition.setMaximum in " + owner + 
					": Bug detected. Contact Ken Samuel.";
			System.err.println(warning);
	    	Global.unableToRespondMessage.add(warning);
		}
		Global.changedDefinition(this);
	}
	
	/**
	 * The <code>setIsGuaranteed</code> setter changes the value of the global variable,
	 * <code>isGuaranteed</code>, a <b><code>Boolean</code></b>.
	 *
	 * @param isGuaranteedIn is the new value that should be assigned to <code>isGuaranteed</code>.
	 */
	public void setIsGuaranteed(Boolean isGuaranteedIn) {
		isGuaranteed = isGuaranteedIn;
		Global.changedDefinition(this);
	}

	/**
	 * The <code>setLogicalOperator</code> setter changes the value of the global variable,
	 * <code>logicalOperator</code>, a <b><code>String</code></b>.
	 *
	 * @param logicalOperatorIn is the new value that should be assigned to <code>logicalOperator</code>.
	 */
	public void setLogicalOperator(String logicalOperatorIn) {
		logicalOperator = logicalOperatorIn;
		Global.changedDefinition(this);
	}

	/**
	 * The <code>isGuaranteedEqual</code> determines whether the value of the <code>isGuaranteed</code> 
	 * variable is the same as the given value.
	 *
	 * @param value is the value to compare with <code>isGuaranteed</code>.
	 * @return <b><code>true</code></b> if and only if <code>isGuaranteed</code> equals <code>value</code>.
	 */
	public Boolean isGuaranteedEqual(Boolean value) {
		if ((isGuaranteed == null) || (isGuaranteed != value)) {
			return false;
		}
		return true;
	}

	/**
	 * The <code>getSetId</code> getter returns the value of the global variable,
	 * <code>setId1</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>setId1</code>.
	 */
	public String getSetId() {
		if (set1 != null) {
			return set1.getName();
		}
		return null;
	}

	/**
	 * The <code>getSetId1</code> getter returns the value of the global variable,
	 * <code>setId1</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>setId1</code>.
	 */
	public String getSetId1() {
		if (set1 != null) {
			return set1.getName();
		}
		return null;
	}

	/**
	 * The <code>getSetId2</code> getter returns the value of the global variable,
	 * <code>setId2</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>setId2</code>.
	 */
	public String getSetId2() {
		if (set2 != null) {
			return set2.getName();
		}
		return null;
	}

	/**
	 * The <code>getSetDefinition1</code> getter returns the value of the global variable,
	 * <code>setDefinition1</code>, a <b><code>SetDefinition</code></b>.
	 *
	 * @return the value of <code>setDefinition1</code>.
	 */
	public SetDefinition getSetDefinition1() {
		return setDefinition1;
	}

	/**
	 * The <code>getSetDefinition2</code> getter returns the value of the global variable,
	 * <code>setDefinition2</code>, a <b><code>SetDefinition</code></b>.
	 *
	 * @return the value of <code>setDefinition2</code>.
	 */
	public SetDefinition getSetDefinition2() {
		return setDefinition2;
	}

	/**
	 * The <code>getMinimum</code> getter returns the value of the global variable,
	 * <code>minimum</code>, an <b><code>Integer</code></b>.
	 *
	 * @return the value of <code>minimum</code>.
	 */
	public String getMinimum() {
		return minimum;
	}

	/**
	 * The <code>getMaximum</code> getter returns the value of the global variable,
	 * <code>maximum</code>, an <b><code>Integer</code></b>.
	 *
	 * @return the value of <code>maximum</code>.
	 */
	public String getMaximum() {
		return maximum;
	}

	/**
	 * The <code>getIsGuaranteed</code> getter returns the value of the global variable,
	 * <code>isGuaranteed</code>, a <b><code>Boolean</code></b>.
	 *
	 * @return the value of <code>isGuaranteed</code>.
	 */
	public Boolean getIsGuaranteed() {
		return isGuaranteed;
	}

	/**
	 * The <code>getLogicalOperator</code> getter returns the value of the global variable,
	 * <code>logicalOperator</code>, a <b><code>String</code></b>.
	 *
	 * @return the value of <code>logicalOperator</code>.
	 */
	public String getLogicalOperator() {
		return logicalOperator;
	}

	/**
	 * The <code>getEvent1</code> getter returns the value of the global variable,
	 * <code>event1</code>, a <b><code>EventDefinition</code></b>.
	 *
	 * @return the value of <code>event1</code>.
	 */
	public EventDefinition getEvent1() {
		return event1;
	}

	/**
	 * The <code>getEvent2</code> getter returns the value of the global variable,
	 * <code>event2</code>, a <b><code>EventDefinition</code></b>.
	 *
	 * @return the value of <code>event2</code>.
	 */
	public EventDefinition getEvent2() {
		return event2;
	}

	/**
	 * The <code>toString</code> method returns the SPARQL version of this definition.
	 *
	 * @return the value of <code>definitionInSPARQL</code>.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getId();
	}
}