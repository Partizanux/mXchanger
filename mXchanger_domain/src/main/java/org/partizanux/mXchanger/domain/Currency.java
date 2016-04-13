package org.partizanux.mXchanger.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

//MUST: money1 < money2 lexicographically
//because money pairs in DB are also in lexicographically order
public class Currency {
	private String money1;
	private String money2;
	private Timestamp timestamp;
	private BigDecimal bid;
	private BigDecimal ask;
	
	public String getMoney1() {
		return money1;
	}
	public void setMoney1(String money1) {
		this.money1 = money1;
	}
	public String getMoney2() {
		return money2;
	}
	public void setMoney2(String money2) {
		this.money2 = money2;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public BigDecimal getBid() {
		return bid;
	}
	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}
	public BigDecimal getAsk() {
		return ask;
	}
	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}
	
	@Override
	public String toString() {
		return "Currency: [money1: " + money1 + "money2: " + money2 + 
				", timestamp: " + timestamp + ", bid: " + bid + ", ask: " + ask + "]";
	}
	
}
