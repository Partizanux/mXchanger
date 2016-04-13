package org.partizanux.mXchanger.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

import org.partizanux.mXchanger.dao.MXchangerDao;
import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.partizanux.mXchanger.service.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("mXchangerService")
public class MXchangerServiceJSON implements MXchangerService<String>{
	@Autowired
	private MXchangerDao dao;
	@Autowired
	private Clock clock;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW,  isolation=Isolation.SERIALIZABLE, readOnly=true)
	public String getAllDealerMoney(String moneyRequest) throws ParseDataException {
		long id = JSONUtil.parseDealerMoneyRequest(moneyRequest);
		
		List<DealerMoney> moneyList = dao.getAllDealerMoney(id);
		
		String json = JSONUtil.formatAllDeallerMoney(moneyList);
		
		return json;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
	public String getDealerMoneyPair(String currencyRequest) throws ParseDataException {
		Object[] par = JSONUtil.parseCurrencyRequest(currencyRequest);
		long dealerID = (long)par[0];
		String money1 = (String)par[1];
		String money2 = (String)par[2];
		
		DealerMoney dealerMoney1 = dao.getDealerMoney(dealerID, money1);
		DealerMoney dealerMoney2 = dao.getDealerMoney(dealerID, money2);
		Currency currency = dao.getCurrencyPairRate(money1, money2);
		
		String json = JSONUtil.formatCurrencyPair(dealerMoney1, dealerMoney2, currency);
		
		return json;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
	public String requestEx(String offer) throws ParseDataException {
		String[] pair = JSONUtil.getMoneyPair(offer);
		Currency currency = dao.getCurrencyPairRate(pair[0], pair[1]);
		
		Order order = JSONUtil.parseOfferToOrder(offer, currency, clock);
		String jsonOrder = JSONUtil.formatOrder(order);
		
		return jsonOrder;
	}
	
	@Override
	public boolean confirmEx(String confirm) throws ParseDataException {
		return JSONUtil.parseConfirm(confirm);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public String doExchange(String jsonOrder) throws ParseDataException {
		
		Order order = JSONUtil.parseOrder(jsonOrder);
		
		if(!checkDealerMoney(order) || !checkIfCurrencyNotChanged(order)) {
			return JSONUtil.getExchangeCommittedMessage(false);
		}
		
		dao.saveOrder(order);//should save actual time ?
		dao.minusDealerMoneyAmount(order.getDealerID(), order.getMoneyToSell(), order.getSellAmount());
		dao.plusDealerMoneyAmount(order.getDealerID(), order.getMoneyToBuy(), order.getBuyAmount());

		return JSONUtil.getExchangeCommittedMessage(true);
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	private boolean checkDealerMoney(Order order) {
		long dealerID = order.getDealerID();
		String moneyToSell = order.getMoneyToSell();
		BigDecimal amountToSell = order.getSellAmount();
		
		BigDecimal dealerAmount = dao.getDealerMoney(dealerID, moneyToSell).getAmount();
		
		if(dealerAmount.compareTo(amountToSell) >= 0)
			return true;
		else
			return false;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	private boolean checkIfCurrencyNotChanged(Order order) {
		//we'll check timestamps
		String money1 = order.getMoneyToBuy();
		String money2 = order.getMoneyToSell();
		//getCurrencyPairRate method is argument order independent
		//so dao.getCurrencyPairRate(money1, money2) = dao.getCurrencyPairRate(money2, money1)
		boolean b = order.getTimestamp().after(dao.getCurrencyPairRate(money1, money2).getTimestamp());
		return b;
	}
	
}
