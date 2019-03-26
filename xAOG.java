package edu.ucla.xai.parser;

public class xAOG {
	int turnId;
	String explanationType;
	
	String focusEntityName;
	String focusEntityNeo4jID;
	
	public int getTurnId() {
		return turnId;
	}
	public void setTurnId(int turnId) {
		this.turnId = turnId;
	}
	public String getExplanationType() {
		return explanationType;
	}
	public void setExplanationType(String explanationType) {
		this.explanationType = explanationType;
	}
	public String getFocusEntityName() {
		return focusEntityName;
	}
	public void setFocusEntityName(String focusEntityName) {
		this.focusEntityName = focusEntityName;
	}
	public String getFocusEntityNeo4jID() {
		return focusEntityNeo4jID;
	}
	public void setFocusEntityNeo4jID(String focusEntityNeo4jID) {
		this.focusEntityNeo4jID = focusEntityNeo4jID;
	}
	
}
