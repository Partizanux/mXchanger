package org.partizanux.mXchanger.client_ui;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.partizanux.mXchanger.service.util.DateUtil;

public class TestClient {
	
	protected String getAllDealerMoneyResponse(String money1, String money2, BigDecimal amount1, BigDecimal amount2) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(money1, amount1.toString());
		map.put(money2, amount2.toString());
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	protected String getJsonCurrencyResponse(String money1, String money2, BigDecimal bid, BigDecimal ask) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put(money1, new BigDecimal("0.00").toString());
		map.put(money2, new BigDecimal("0.00").toString());
		
		List<Object> rate = new LinkedList<>();
		
		rate.add(bid.toString());
		rate.add(ask.toString());
		//rate.add(new Timestamp(new Date().getTime()));
		map.put("currency", rate);
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	protected static String getJsonOrder(long id, String moneyToSell, String moneyToBuy,
			BigDecimal amountToSell, BigDecimal amountToBuy, BigDecimal rate) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", 1L);
		map.put("moneyToSell", moneyToSell);
		map.put("moneyToBuy", moneyToBuy);
		map.put("amountToSell", amountToSell.toString());
		map.put("amountToBuy", amountToBuy.toString());
		map.put("rate", rate.toString());
		
		LocalDateTime dateTime = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(dateTime);
		
		map.put("timestamp", DateUtil.format(timestamp));
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
}
