package org.partizanux.mXchanger.client_ui.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CurrencyRate {
	private StringProperty money1;
	private StringProperty money2;
	private ObjectProperty<BigDecimal> bid;
	private ObjectProperty<BigDecimal> ask;
	private ObjectProperty<LocalDateTime> time;
	
	public CurrencyRate() {
	}
	
	public CurrencyRate(String money1, String money2, BigDecimal bid, BigDecimal ask, LocalDateTime time) {
		this.setMoney1(new SimpleStringProperty(money1));
		this.setMoney2(new SimpleStringProperty(money2));
		this.setBid(new SimpleObjectProperty<BigDecimal>(bid));
		this.setAsk(new SimpleObjectProperty<BigDecimal>(ask));
		this.setTime(new SimpleObjectProperty<LocalDateTime>(time));
	}

	public StringProperty getMoney1() {
		return money1;
	}

	public void setMoney1(StringProperty money) {
		this.money1 = money;
	}
	
	public StringProperty getMoney2() {
		return money2;
	}

	public void setMoney2(StringProperty money) {
		this.money2 = money;
	}

	public ObjectProperty<BigDecimal> getBid() {
		return bid;
	}

	public void setBid(ObjectProperty<BigDecimal> bid) {
		this.bid = bid;
	}

	public ObjectProperty<BigDecimal> getAsk() {
		return ask;
	}

	public void setAsk(ObjectProperty<BigDecimal> ask) {
		this.ask = ask;
	}

	public ObjectProperty<LocalDateTime> getTime() {
		return time;
	}

	public void setTime(ObjectProperty<LocalDateTime> time) {
		this.time = time;
	}
	
}
