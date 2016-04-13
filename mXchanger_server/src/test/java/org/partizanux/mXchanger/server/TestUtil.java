package org.partizanux.mXchanger.server;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.partizanux.mXchanger.service.util.DateUtil;

public class TestUtil {
	
	public static Object getCSVAllDealerMoneyReq() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		
		String json = JSONValue.toJSONString(map);
		
		return "getAllDealerMoney" + "," + json;
	}
	
	public static Object getAllDealerMoneyReq() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static Object getCSVCurrencyReq(){
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("money1", "eur");
		map.put("money2", "usd");
		
		String json = JSONValue.toJSONString(map);
		
		return "getCurrency" + "," + json;
	}
	
	public static Object getCurrencyReq(){
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("money1", "eur");
		map.put("money2", "usd");
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static Object getCSVExchangeOffer() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("buyOrSell", "sell");
		map.put("money1", "eur");
		map.put("money2", "usd");
		map.put("amount", "100.00");
		String json = JSONValue.toJSONString(map);
		
		return "offer" + "," + json;
	}
	
	public static String getExchangeOffer() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("buyOrSell", "sell");
		map.put("money1", "eur");
		map.put("money2", "usd");
		map.put("amount", "100.00");
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static String getJsonAllDealerMoney() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("eur", "1000.00");
		map.put("usd", "1000.00");
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static String getJsonAllDealerMoneyAfterExchange() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("eur", "900.00");
		map.put("usd", "1109.19");
		
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
	
	public static String getJsonOrder(Clock clock) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("moneyToSell", "eur");
		map.put("moneyToBuy", "usd");
		map.put("amountToSell", "100.00");
		map.put("amountToBuy", "109.19");
		map.put("rate", "1.0919");
		
		LocalDateTime dateTime = LocalDateTime.now(clock);
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
