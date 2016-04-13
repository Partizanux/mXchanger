package org.partizanux.mXchanger.domain;

import java.math.BigDecimal;

public class DealerMoney {
	private long dealerID;
	private String moneyID;
	private BigDecimal amount;

	public long getDealerID() {
		return dealerID;
	}
	
	public void setDealerID(long dealerID) {
		this.dealerID = dealerID;
	}

	public String getMoneyID() {
		return moneyID;
	}

	public void setMoneyID(String moneyID) {
		this.moneyID = moneyID;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "DealerMoney: [dealerID: " + dealerID + ", moneyID: " + moneyID + ", amount: " + amount + "]";
	}
}
