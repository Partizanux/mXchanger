package org.partizanux.mXchanger.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class JDBCTemplateDao implements MXchangerDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String SQL_GET_DEALERMONEY_BY_ID = "SELECT * FROM DealerMoney WHERE dealerID=? AND moneyID=?";
	private static final String SQL_GET_ALL_DEALERMONEY = "SELECT * FROM DealerMoney WHERE dealerID=?";
	private static final String SQL_GET_CURRENCY_BY_PAIR = "SELECT * FROM currency WHERE money1=? AND money2=?";
	private static final String SQL_UPDATE_DEALERMONEY = "UPDATE DealerMoney SET amount=? where dealerID=? AND moneyID=?";
	private static final String SQL_PLUS_DEALERMONEY = "UPDATE DealerMoney SET amount=amount+? where dealerID=? AND moneyID=?";
	private static final String SQL_MINUS_DEALERMONEY = "UPDATE DealerMoney SET amount=amount-? where dealerID=? AND moneyID=?";
	private static final String SQL_SAVE_ORDER = "INSERT INTO Orders "
			+ "(dealerID, moneyToSell, moneyToBuy, buyAmount, sellAmount, rate, timestamp)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	@Override
	public DealerMoney getDealerMoney(long dealerID, String money) {
		return jdbcTemplate.queryForObject(SQL_GET_DEALERMONEY_BY_ID, 
				(rs, rowNum) -> {
					DealerMoney dm = new DealerMoney();
					dm.setDealerID(rs.getLong(1));
					dm.setMoneyID(rs.getString(2));
					dm.setAmount(rs.getBigDecimal(3));
					return dm;
				}, dealerID, money);
	}
	
	//returns a list of all DealerMoney for one dealer
	@Override
	public List<DealerMoney> getAllDealerMoney(long dealerID) {
		return jdbcTemplate.query(SQL_GET_ALL_DEALERMONEY, 
				(rs, rowNum) -> {
					DealerMoney dm = new DealerMoney();
					dm.setDealerID(rs.getLong(1));
					dm.setMoneyID(rs.getString(2));
					dm.setAmount(rs.getBigDecimal(3));
					return dm;
				}, dealerID);
	}

	@Override
	public Currency getCurrencyPairRate(String money1, String money2) {
		//Compare moneyID lexicographically
		//because table money contains money pairs in lex. order
		//i.e. money1 > money2, if not swap
		if (money1.compareTo(money2) > 0) {
			String temp = money1;
			money1 = money2;
			money2 = temp;
			return jdbcTemplate.queryForObject(SQL_GET_CURRENCY_BY_PAIR, 
					(rs, rowNum) -> {
						Currency c = new Currency();
						c.setMoney1(rs.getString(2));
						c.setMoney2(rs.getString(1));
						c.setTimestamp(rs.getTimestamp(3));
						c.setBid(new BigDecimal("1.00").divide(rs.getBigDecimal(5), 4, BigDecimal.ROUND_HALF_UP));
						c.setAsk(new BigDecimal("1.00").divide(rs.getBigDecimal(4), 4, BigDecimal.ROUND_HALF_UP));
						return c;
					}, money1, money2);
		}
		return jdbcTemplate.queryForObject(SQL_GET_CURRENCY_BY_PAIR, 
				(rs, rowNum) -> {
					Currency c = new Currency();
					c.setMoney1(rs.getString(1));
					c.setMoney2(rs.getString(2));
					c.setTimestamp(rs.getTimestamp(3));
					c.setBid(rs.getBigDecimal(4));
					c.setAsk(rs.getBigDecimal(5));
					return c;
				}, money1, money2);
	}

	@Override
	public void updateDealerMoneyAmount(long dealerID, String moneyID, BigDecimal amount) {
		jdbcTemplate.update(SQL_UPDATE_DEALERMONEY, amount, dealerID, moneyID);
	}
	
	@Override
	public void plusDealerMoneyAmount(long dealerID, String moneyID, BigDecimal amount) {
		jdbcTemplate.update(SQL_PLUS_DEALERMONEY, amount, dealerID, moneyID);
	}
	
	@Override
	public void minusDealerMoneyAmount(long dealerID, String moneyID, BigDecimal amount) {
		jdbcTemplate.update(SQL_MINUS_DEALERMONEY, amount, dealerID, moneyID);
	}

	@Override
	public void saveOrder(Order order) {
		long dealerID = order.getDealerID();
		String moneyToSell = order.getMoneyToSell();
		String moneyToBuy = order.getMoneyToBuy();
		BigDecimal buyAmount = order.getBuyAmount();
		BigDecimal sellAmount = order.getSellAmount();
		BigDecimal rate = order.getRate();
		Timestamp timestamp = order.getTimestamp();
		
		jdbcTemplate.update(SQL_SAVE_ORDER, dealerID, moneyToSell, moneyToBuy, buyAmount, sellAmount, rate, timestamp);
	}

}
