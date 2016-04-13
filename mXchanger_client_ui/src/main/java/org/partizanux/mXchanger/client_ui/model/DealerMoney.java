package org.partizanux.mXchanger.client_ui.model;

import java.math.BigDecimal;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DealerMoney {
	private StringProperty money;
	private ObjectProperty <BigDecimal> amount;
	
	public DealerMoney() {
	}
	
	public DealerMoney(String money, BigDecimal amount) {
		this.setMoney(new SimpleStringProperty(money));
		this.setAmount(new SimpleObjectProperty<BigDecimal>(amount));
	}

	public StringProperty getMoney() {
		return money;
	}

	public void setMoney(StringProperty money) {
		this.money = money;
	}

	public ObjectProperty <BigDecimal> getAmount() {
		return amount;
	}

	public void setAmount(ObjectProperty <BigDecimal> amount) {
		this.amount = amount;
	}
}
