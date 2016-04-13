package org.partizanux.mXchanger.client_ui.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.partizanux.mXchanger.client.Client;
import org.partizanux.mXchanger.client_ui.ClientMain;
import org.partizanux.mXchanger.client_ui.model.CurrencyRate;
import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.model.UOrder;
import org.partizanux.mXchanger.client_ui.util.JsonClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Service
public class ClientService {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
	
	//long dealerID = 1L;
	
	@Autowired
	private Client client;
	
	@Autowired
	private ClientMain app;
	
	public ObservableList<String> getAvailableMoney() {
		ObservableList<String> money = FXCollections.observableArrayList();
		money.add("eur");
		money.add("usd");
		
		return money;
	}
	
	public ObservableList<DealerMoney> getDealerMoney() {
		
		String req = JsonClientUtil.getDealerMoneyRequest(app.getDealerId());
		String res = client.sendMessage(req, false);
		
		List<DealerMoney> moneyList = null;
		
		try {
			moneyList = JsonClientUtil.parseAllDealerMoney(res);
		} catch (ParseException e) {
			logger.error("Can't parse response with DealerMoney \n", e);
		}
		
		ObservableList<DealerMoney> dealerMoneyList = FXCollections.observableArrayList();
		
		for(int i = 0; i < moneyList.size(); i++)
			dealerMoneyList.add(moneyList.get(i));
		
		return dealerMoneyList;
	}
	
	public CurrencyRate getCurrency(String money1, String money2) {
		
		if(money1.equals(money2)) {
			return new CurrencyRate(money1, money2, new BigDecimal("1.00"), new BigDecimal("1.00"), LocalDateTime.now());
		}
		
		String req = JsonClientUtil.getCurrencyRequest(app.getDealerId(), money1, money2);
		String res = client.sendMessage(req, false);
		
		CurrencyRate currRate = null;
		
		try {
			currRate = JsonClientUtil.parseCurrencyRate(res);
		} catch (ParseException e) {
			logger.error("Can't parse response with Currency \n", e);
		}
		
		return currRate;
	}
	
	public UOrder offerEx(long dealerId, String moneyToSell, String moneyToBuy, BigDecimal sellAmount) {
		
		String req = JsonClientUtil.getOfferExRequest(app.getDealerId(), moneyToSell, moneyToBuy, sellAmount);
		String res = client.sendMessage(req, true);
		// because sendMessage kept the session
		// client must send only confirm message to the server
		
		UOrder uorder = null;
		
		try {
			uorder = JsonClientUtil.parseOrder(res);
		} catch (ParseException e) {
			logger.error("Can't parse response with Order \n", e);
		}
		
		return uorder;
	}
	
	public void confirmEx(boolean flag) {
		
		String req = JsonClientUtil.getConfirmRequest(flag);
		// req is sent to the server via session kept by previous offferEx method
		client.sendMessageInSession(req);
		
		// we don't worry about server answer
		// sendMessageInSession returns after it gets some message
		// which means that server complete operation
		
	}
	
}
