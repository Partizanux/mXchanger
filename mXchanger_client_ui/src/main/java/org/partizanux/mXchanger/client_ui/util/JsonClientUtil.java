package org.partizanux.mXchanger.client_ui.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.partizanux.mXchanger.client_ui.model.CurrencyRate;
import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.model.UOrder;

public class JsonClientUtil {
	
	private static Map<String, Object> parseJson2Map(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List<String> creatArrayContainer() {
				return new LinkedList<String>();
			}

			public Map<String, Object> createObjectContainer() {
				return new LinkedHashMap<String, Object>();
			}

		};

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) parser.parse(jsonString, containerFactory);
		 
		return jsonMap;
		
	}
	
	public static String getDealerMoneyRequest(long dealerId) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", dealerId);
		
		String json = JSONValue.toJSONString(map);
		
		return "getAllDealerMoney" + "," + json;
	}
	
	public static String getCurrencyRequest(long dealerId, String money1, String money2) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", dealerId);
		map.put("money1", money1);
		map.put("money2", money2);
		
		String json = JSONValue.toJSONString(map);
		
		return "getCurrency" + "," + json;
	}
	
	public static String getOfferExRequest(long dealerId, String moneyToSell, String moneyToBuy, BigDecimal sellAmount) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", dealerId);
		map.put("buyOrSell", "sell");
		map.put("money1", moneyToSell);
		map.put("money2", moneyToBuy);
		map.put("amount", sellAmount.toString());
		String json = JSONValue.toJSONString(map);
		
		return "offer" + "," + json;
	}
	
	public static String getConfirmRequest(boolean b) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("confirm", b);
		
		String json = JSONValue.toJSONString(map);

		return json;
	}

	public static ArrayList<DealerMoney> parseAllDealerMoney(String jsonMoney) throws ParseException {
		
		ArrayList<DealerMoney> moneyList = new ArrayList<>();
		
		Map<String, Object> json = parseJson2Map(jsonMoney);
		
		Iterator<Entry<String, Object>> it = json.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Object> entry;
			
			entry = it.next();
			String money = (String)entry.getKey();
			String amount = (String)entry.getValue();
			
			moneyList.add(new DealerMoney(money, new BigDecimal(amount)));
			
		}
		
		return moneyList;
		
	}
	
	@SuppressWarnings("unchecked")
	public static CurrencyRate parseCurrencyRate(String jsonCurrency) throws ParseException {
		
		String money1 = "";
		String money2 = "";
		List<String> curr = new LinkedList<>();
		
		Map<String, Object> json = parseJson2Map(jsonCurrency);
		
		Iterator<Entry<String, Object>> it = json.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Object> entry;
			
			it.next();// entry with dealerID - ignore
			entry = it.next();
			money1 = (String)entry.getKey();// ignore values with amount
			
			entry = it.next();
			money2 = (String)entry.getKey();
			
			entry = it.next();
			curr = (List<String>)entry.getValue();
			
		}
		
		BigDecimal bid = new BigDecimal(curr.get(0));
		BigDecimal ask = new BigDecimal(curr.get(1));
		LocalDateTime time = LocalDateTime.now();
		
		return new CurrencyRate(money1, money2, bid, ask, time);
	}

	public static UOrder parseOrder(String jsonOrder) throws ParseException {
		
		long dealer = 0;
		String moneyToSell = "";
		BigDecimal amountToSell = new BigDecimal("0.0");
		String moneyToBuy = "";
		BigDecimal amountToBuy = new BigDecimal("0.0");
		BigDecimal rate = new BigDecimal("0.0");
		LocalDateTime timestamp = LocalDateTime.now();

		Map<String, Object> json = parseJson2Map(jsonOrder);
		
		Iterator<Entry<String, Object>> it = json.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Object> entry;
			
			entry = it.next();
			dealer = (Long)entry.getValue();
			
			entry = it.next();
			moneyToSell = (String)entry.getValue();
			
			entry = it.next();
			moneyToBuy = (String)entry.getValue();
			
			entry = it.next();
			amountToSell = new BigDecimal((String)entry.getValue());
			
			entry = it.next();
			amountToBuy = new BigDecimal((String)entry.getValue());
			
			entry = it.next();
			rate = new BigDecimal((String)entry.getValue());
			
			entry = it.next();
			timestamp = DateUtil.parse((String)entry.getValue());
		}
		
		UOrder uorder = new UOrder(dealer, moneyToSell, amountToSell, 
				moneyToBuy, amountToBuy, rate, timestamp);
		
		return uorder;
	}
	
}
