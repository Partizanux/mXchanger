package org.partizanux.mXchanger.client_ui.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UOrder {
	private LongProperty dealer;
	private StringProperty moneyToSell;
	private ObjectProperty<BigDecimal> amountToSell;
	private StringProperty moneyToBuy;
	private ObjectProperty<BigDecimal> amountToBuy;
	private ObjectProperty<BigDecimal> rate;
	private ObjectProperty<LocalDateTime> timestamp;
	
	public UOrder() {
	}
	
	public UOrder(long dealer, String moneyToSell, BigDecimal amountToSell, 
			String moneyToBuy, BigDecimal amountToBuy, BigDecimal rate, LocalDateTime timestamp) {
		this.dealer = new SimpleLongProperty(dealer);
		this.moneyToSell = new SimpleStringProperty(moneyToSell);
		this.amountToSell = new SimpleObjectProperty<BigDecimal>(amountToSell);
		this.moneyToBuy = new SimpleStringProperty(moneyToBuy);
		this.amountToBuy = new SimpleObjectProperty<BigDecimal>(amountToBuy);
		this.rate = new SimpleObjectProperty<BigDecimal>(rate);
		this.timestamp = new SimpleObjectProperty<LocalDateTime>(timestamp);
	}
	
	public LongProperty getDealer() {
		return dealer;
	}
	public void setDealer(LongProperty dealer) {
		this.dealer = dealer;
	}
	public StringProperty getMoneyToSell() {
		return moneyToSell;
	}
	public void setMoneyToSell(StringProperty moneyToSell) {
		this.moneyToSell = moneyToSell;
	}
	public ObjectProperty<BigDecimal> getAmountToSell() {
		return amountToSell;
	}
	public void setAmountToSell(ObjectProperty<BigDecimal> amountToSell) {
		this.amountToSell = amountToSell;
	}
	public StringProperty getMoneyToBuy() {
		return moneyToBuy;
	}
	public void setMoneyToBuy(StringProperty moneyToBuy) {
		this.moneyToBuy = moneyToBuy;
	}
	public ObjectProperty<BigDecimal> getAmountToBuy() {
		return amountToBuy;
	}
	public void setAmountToBuy(ObjectProperty<BigDecimal> amountToBuy) {
		this.amountToBuy = amountToBuy;
	}
	public ObjectProperty<BigDecimal> getRate() {
		return rate;
	}
	public void setRate(ObjectProperty<BigDecimal> rate) {
		this.rate = rate;
	}
	public ObjectProperty<LocalDateTime> getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(ObjectProperty<LocalDateTime> timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
