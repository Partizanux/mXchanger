package org.partizanux.mXchanger.client_ui.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.partizanux.mXchanger.client.Client;
import org.partizanux.mXchanger.client_ui.model.CurrencyRate;
import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.model.UOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//@Service
public class DummyClientService {
	@Autowired
	private Client client;
	
	public ObservableList<String> getAvailableMoney() {
		ObservableList<String> money = FXCollections.observableArrayList();
		money.add("eur");
		money.add("usd");
		
		return money;
	}
	
	public ObservableList<DealerMoney> getDealerMoney() {
		// stub for test
		ObservableList<DealerMoney> dealerMoneyList = FXCollections.observableArrayList();
		dealerMoneyList.add(new DealerMoney("uah", new BigDecimal("999.99")));
		dealerMoneyList.add(new DealerMoney("eur", new BigDecimal("500.01")));
		
		return dealerMoneyList;
	}
	
	public UOrder offerEx(long dealerId, String moneyToSell, String moneyToBuy, BigDecimal sellAmount) {
		// stub for test
		return new UOrder(dealerId, moneyToSell, sellAmount,
				moneyToBuy, new BigDecimal("107.233"), new BigDecimal("27.0"), LocalDateTime.now());
	}
	
	public void confirmEx(boolean flag) {
		// stub for test
	}
	
	public CurrencyRate getCurrency(String money1, String money2) {
		if(money1.equals(money2)) {
			return new CurrencyRate(money1, money2, new BigDecimal("1.00"), new BigDecimal("1.00"), LocalDateTime.now());
		}
		// stub for test
		return new CurrencyRate(money1, money2, new BigDecimal("1.09"), new BigDecimal("1.08"), LocalDateTime.now());
	}
}
