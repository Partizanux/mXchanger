package org.partizanux.mXchanger.dao;

import java.math.BigDecimal;
import java.util.List;

import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;

public interface MXchangerDao {
	DealerMoney getDealerMoney(long dealerID, String money);
	List<DealerMoney> getAllDealerMoney(long dealerID);
	Currency getCurrencyPairRate(String money1, String money2);
	void updateDealerMoneyAmount(long dealerID, String moneyID, BigDecimal amount);
	void plusDealerMoneyAmount(long dealerID, String moneyID, BigDecimal amount);
	void minusDealerMoneyAmount(long dealerID, String moneyID, BigDecimal amount);
	void saveOrder(Order order);
	
}
