package sparql.translator.restrictions;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import sparql.translator.query.SPARQLQuery;
import sparql.translator.utilities.Global;

/**
 * The <code>UnionRestriction</code> class holds a list of SPARQL restrictions that should be connected by
 * UNIONs. For example:
 * {
 *	   ?crossing1 rdf:type msee:Cross .
 *	   ?crossing1 msee:hasAgent ?x1 .
 *	   ?crossing1 msee:hasPatient ?x2 .
 * } UNION {
 *	   FILTER (fn:IsCrossing("OBJECT_ID",?x1,"OBJECT_ID",?x2)) .
 * }
 *
 * @author Ken Samuel
 * @version 1.0, Nov 18, 2013
 * @since 1.6
 */
public class UnionRestriction extends Restriction {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/** 
	 * <code>restrictionsGroups</code> is the list of groups of restrictions in this union restriction that  
	 * should not be marked as OPTIONAL, where a restrictions group is a list of restrictions that should be 
	 * joined to the other restrictions groups by UNION.
	 */
	private ArrayList<LinkedHashSet<Restriction>> restrictionsGroups;
	

	/**
	 * The <code>UnionRestriction</code> constructor initializes the global variables.
	 */
	public UnionRestriction() {
		this(10);
	}

	/**
	 * The <code>UnionRestriction</code> constructor initializes the global variables.
	 *
	 * @param size is the number of restriction groups that will be in this object.
	 */
	public UnionRestriction(int size) {
		super();
		restrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>(size);
	}
	
	/**
	 * The <code>replaceVariable</code> method replaces all occurrences of one variable in this restriction
	 * with another variable.
	 *
	 * @param oldVariable is the variable to be replaced.
	 * @param newVariable is the variable to replace <code>oldVariable</code>.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 */
	@Override
	public void replaceVariable(String oldVariable, String newVariable, String owner) {
		for (LinkedHashSet<Restriction> restrictionGroup : restrictionsGroups) {
			for (Restriction restriction : restrictionGroup) {
				restriction.replaceVariable(oldVariable,newVariable,owner);
			}
		}
	}

	/**
	 * The <code>reorderRestrictions</code> method changes the order of the restrictions in this object
	 * to make sure the predicate filter function restrictions are tested last. It does not change this
	 * object; it creates a new object to return.
	 *
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a <code>UnionRestriction</code> object with the same restrictions as this object in a different 
	 * order.
	 */
	@Override
	public UnionRestriction reorderRestrictions(String owner) {
		LinkedHashSet<Restriction> reorderedRestrictionsGroup;			  //With its restrictions reordered
		ArrayList<LinkedHashSet<Restriction>> predicateFilterFunctionRestrictionsGroups;//They have a PFF
		ArrayList<LinkedHashSet<Restriction>> otherRestrictionsGroups;	//Have no predicate filter functions
		SPARQLQuery subQuery;					//The other restrictions are pushed into a new SPARQL query
		QueryRestriction newRestriction;		//Replaces all of the other restrictions
		LinkedHashSet<Restriction> newRestrictionsGroup;//This group has only one restrictions: newRestriction
		Boolean hasPFFRestriction;	//Specifies whether the current restriction group has a PFF restriction
		UnionRestriction returnValue;

		predicateFilterFunctionRestrictionsGroups = 
				new ArrayList<LinkedHashSet<Restriction>>(restrictionsGroups.size());
		otherRestrictionsGroups = new ArrayList<LinkedHashSet<Restriction>>(restrictionsGroups.size());
		for (LinkedHashSet<Restriction> restrictionGroup : restrictionsGroups) {
			reorderedRestrictionsGroup = Global.reorderRestrictions(restrictionGroup,owner);
			hasPFFRestriction = false;						//Until proven otherwise
			for (Restriction restriction : reorderedRestrictionsGroup) {
				if (restriction.hasPredicateFilterFunctionRestriction == true) {
					hasPFFRestriction = true;
				}
			}
			if (hasPFFRestriction == true) {
				predicateFilterFunctionRestrictionsGroups.add(reorderedRestrictionsGroup);
			} else {
				otherRestrictionsGroups.add(reorderedRestrictionsGroup);
			}
		}
		returnValue = new UnionRestriction(
				predicateFilterFunctionRestrictionsGroups.size() + otherRestrictionsGroups.size());
		returnValue.setPolarity(polarity);
		returnValue.setHasPFFRestriction(hasPredicateFilterFunctionRestriction);
		if (predicateFilterFunctionRestrictionsGroups.size() >= 1) {
			for (LinkedHashSet<Restriction> otherRestrictionsGroup : otherRestrictionsGroups) {
				if (otherRestrictionsGroup.size() >= 1) {
					subQuery = new SPARQLQuery();
					subQuery.distinct();
					subQuery.addRestrictions(otherRestrictionsGroup);
					newRestriction = new QueryRestriction(subQuery,true);
					newRestrictionsGroup = new LinkedHashSet<Restriction>(1);
					newRestrictionsGroup.add(newRestriction);
					returnValue.addRestrictionsGroup(newRestrictionsGroup);
				}
			}
			returnValue.addAllRestrictionsGroups(predicateFilterFunctionRestrictionsGroups);
		} else {
			returnValue.addAllRestrictionsGroups(otherRestrictionsGroups);
		}
		return returnValue;
	}
	
	/**
	 * The <code>extractOptionalRestrictions</code> method searches recursively through this restriction, 
	 * removing all of the optional restrictions and returning them in a list.
	 *
	 * @param isPositive is <b><code>false</code></b> if this restriction is found within an
	 * odd number of &lt;not&gt;s, and <b><code>true</code></b> otherwise.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a list of all the optional restrictions that have been removed from this restriction.
	 */
	@Override
	public ArrayList<OptionalRestriction> extractOptionalRestrictions(Boolean isPositive, String owner) {
		ArrayList<OptionalRestriction> myOptionalRestrictions;	 //Optional restrictions in restrictionsGroups
		ArrayList<OptionalRestriction> theirOptionalRestrictions;//Optional restrictions from other objects
		
		theirOptionalRestrictions = new ArrayList<OptionalRestriction>();			//Initialize
		for (LinkedHashSet<Restriction> restrictionGroup : restrictionsGroups) {
			myOptionalRestrictions = new ArrayList<OptionalRestriction>();				//Initialize
			for (Restriction restriction : restrictionGroup) {
				if (restriction.getClass() == OptionalRestriction.class) {		//Found one
					myOptionalRestrictions.add((OptionalRestriction)restriction);
				} else {
					theirOptionalRestrictions.addAll(
							restriction.extractOptionalRestrictions(isPositive == restriction.isPositive(),
							owner));
				}
			}
			for (OptionalRestriction myOptionalRestriction : myOptionalRestrictions) {
				if (isPositive == myOptionalRestriction.isPositive()) {		//Only save the positive ones
					theirOptionalRestrictions.add(myOptionalRestriction);
				}
			}
		}
		return theirOptionalRestrictions;
	}

	/**
	 * The <code>addRestrictionsGroup</code> method adds the given restriction group to this object's list of
	 * restriction groups.
	 *
	 * @param restrictionsGroup is a list of restrictions.
	 */
	public void addRestrictionsGroup(LinkedHashSet<Restriction> restrictionsGroup) {
		restrictionsGroups.add(restrictionsGroup);
	}

	/**
	 * The <code>addAllRestrictionsGroups</code> method adds all of the given restriction group to this 
	 * object's list of restriction groups.
	 *
	 * @param restrictionsGroupsIn is a list of groups (lists) of restrictions.
	 */
	public void addAllRestrictionsGroups(ArrayList<LinkedHashSet<Restriction>> restrictionsGroupsIn) {
		restrictionsGroups.addAll(restrictionsGroupsIn);
	}
	
	/**
	 * The <code>size</code> method returns the number of restriction groups in this object.
	 *
	 * @return the number of restriction groups in <code>restrictionsGroups</code>.
	 */
	public Integer size() {
		return restrictionsGroups.size();
	}
	
	/**
	 * The <code>getString</code> method returns this object in the form of a string that can be included in
	 * a SPARQL query.
	 *
	 * @param tab specifies the number of spaces to include at the beginning of each line.
	 * @param owner is a description of the last XML tag that was found in the XML version of the query.
	 * @return a string representation of this object.
	 */
	@Override
	public String getString(String tab, String owner) {
		String braces;									//Specifies the form that the next braces should have
		String line;									//One of the line in the return value
		StringBuffer returnValue;

		if (restrictionsGroups.size() == 0) { return ""; }
		returnValue = new StringBuffer();
		if (restrictionsGroups.size() == 1) {
			for (Restriction restriction : restrictionsGroups.get(0)) {
				line = restriction.toString(tab,owner);
				if (line != null ) {
					returnValue.append(line);
				}
			}
		} else {
			braces = "\n" + tab + "{";
			for (LinkedHashSet<Restriction> restrictionGroup : restrictionsGroups) {
				returnValue.append(braces);
				for (Restriction restriction : restrictionGroup) {
					line = restriction.toString("    " + tab,owner);
					if (line != null) {
						returnValue.append(line);
						braces = "\n" + tab + "} UNION {";
					}
				}
			}
			returnValue.append("\n" + tab + "}");
		}
		return returnValue.toString();
	}
}