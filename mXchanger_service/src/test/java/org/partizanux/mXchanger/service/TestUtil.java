package org.partizanux.mXchanger.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;
import org.partizanux.mXchanger.service.util.DateUtil;

//The factory for dummy objects for tests
//We'll make this objects and there json formats CONSISTENT for purpose of testing
public class TestUtil {
	
	public static String getAllDealerMoneyRequest() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static List<DealerMoney> getAllDealerMoneyList() {
		DealerMoney dm1 = new DealerMoney();
		dm1.setDealerID(1L);
		dm1.setMoneyID("eur");
		dm1.setAmount(new BigDecimal("1000.00"));
		
		DealerMoney dm2 = new DealerMoney();
		dm2.setDealerID(1L);
		dm2.setMoneyID("usd");
		dm2.setAmount(new BigDecimal("1000.00"));
		
		ArrayList<DealerMoney> list = new ArrayList<>();
		list.add(dm1);
		list.add(dm2);
		
		return list;
	}
	
	public static String getCurrencyRequest() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("money1", "eur");
		map.put("money2", "usd");
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static Object[] getCurrencyRequestParameters() {
		Object[] par = new Object[3];
		par[0] = new Long(1L);
		par[1] = "eur";
		par[2] = "usd";
		
		return par;
	}
	
	public static DealerMoney getDealerMoney(String money) {
		DealerMoney dealerMoney = new DealerMoney();
		dealerMoney.setDealerID(1L);
		dealerMoney.setMoneyID(money);
		dealerMoney.setAmount(new BigDecimal("1000.00"));
		
		return dealerMoney;
	}
	
	public static Currency getCurrency() {
		Currency currency = new Currency();
		currency.setMoney1("eur");
		currency.setMoney2("usd");
		currency.setBid(new BigDecimal("1.0919"));
		currency.setAsk(new BigDecimal("1.0921"));
		//ignore timestamp
		//currency.setTimestamp(new Timestamp(new Date().getTime()));
		
		return currency;
	}
	
	public static Order getOrder(Clock freeze) {
		Order order = new Order();
		//order.setOrderID(1L);
		order.setDealerID(1L);
		order.setMoneyToSell("eur");
		order.setMoneyToBuy("usd");
		order.setSellAmount(new BigDecimal("100.00"));
		order.setBuyAmount(new BigDecimal("109.19"));
		order.setRate(new BigDecimal("1.0919"));
		
		LocalDateTime dateTime = LocalDateTime.now(freeze);
		Timestamp timestamp = Timestamp.valueOf(dateTime);
		
		order.setTimestamp(DateUtil.parse(DateUtil.format(timestamp)));
		
		return order;
	}
	
	public static String getJsonAllDealerMoney() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("eur", "1000.00");
		map.put("usd", "1000.00");
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static String getJsonCurrency() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("eur", new BigDecimal("1000.00").toString());
		map.put("usd", new BigDecimal("1000.00").toString());
		
		List<Object> rate = new LinkedList<>();
		
		rate.add("1.0919");
		rate.add("1.0921");
		//rate.add(new Timestamp(new Date().getTime()));
		map.put("currency", rate);
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static String getJsonOffer() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("buyOrSell", "sell");
		map.put("money1", "eur");
		map.put("money2", "usd");
		map.put("amount", "100.00");
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static String getJsonOrder(Clock freeze) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("moneyToSell", "eur");
		map.put("moneyToBuy", "usd");
		map.put("amountToSell", "100.00");
		map.put("amountToBuy", "109.19");
		map.put("rate", "1.0919");
		
		LocalDateTime dateTime = LocalDateTime.now(freeze);
		Timestamp timestamp = Timestamp.valueOf(dateTime);
		
		map.put("timestamp", DateUtil.format(timestamp));
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}

	public static String getConfirmMessage() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("confirm", true);
		
		String json = JSONValue.toJSONString(map);

		return json;
	}
	
}
