package org.partizanux.mXchanger.domain;

public class Money {
	private String moneyID;
	private String description;
	
	public String getMoneyID() {
		return moneyID;
	}
	public void setMoneyID(String moneyID) {
		this.moneyID = moneyID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Money: [moneyID: " + moneyID + ", description: " + description + "]";
	}
}
