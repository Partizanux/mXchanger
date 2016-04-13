package org.partizanux.mXchanger.domain;

public class Dealer {
	private long dealerID;
	private String firstName;
	private String lastName;
	private String info;
	
	public long getDealerID() {
		return dealerID;
	}
	public void setDealerID(long dealerID) {
		this.dealerID = dealerID;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		return "Dealer [dealerID: " + dealerID + ", firstName: " + firstName + 
				", lastName: " + lastName + ", info: " + info + "]";
	}
	
}
