package org.partizanux.mXchanger.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
	private Long orderID;
	private Long dealerID;
	private String moneyToSell;
	private String moneyToBuy;
	private BigDecimal sellAmount;
	private BigDecimal buyAmount;
	private BigDecimal rate;
	private Timestamp timestamp;
	
	public Long getOrderID() {
		return orderID;
	}
	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}
	public Long getDealerID() {
		return dealerID;
	}
	public void setDealerID(Long dealerID) {
		this.dealerID = dealerID;
	}
	public String getMoneyToSell() {
		return moneyToSell;
	}
	public void setMoneyToSell(String moneyToSell) {
		this.moneyToSell = moneyToSell;
	}
	public String getMoneyToBuy() {
		return moneyToBuy;
	}
	public void setMoneyToBuy(String moneyToBuy) {
		this.moneyToBuy = moneyToBuy;
	}
	public BigDecimal getBuyAmount() {
		return buyAmount;
	}
	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}
	public BigDecimal getSellAmount() {
		return sellAmount;
	}
	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "Order: \n" + "dealerID: " + dealerID + 
				"\nmoneyToSell: " + moneyToSell +
				"\nmoneyToBuy: " + moneyToBuy + 
				"\nsellAmount: " + sellAmount + 
				"\nbuyAmount: " + buyAmount + 
				"\nrate: " + rate + 
				"\ntimestamp: " + timestamp;
	}
}
