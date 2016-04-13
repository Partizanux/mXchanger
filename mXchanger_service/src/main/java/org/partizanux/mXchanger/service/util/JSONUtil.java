package org.partizanux.mXchanger.service.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;
import org.partizanux.mXchanger.service.exception.ParseDataException;

import javafx.beans.property.StringProperty;

public class JSONUtil {
	
	//return dealerId from request
	public static long parseDealerMoneyRequest(String moneyRequest) throws ParseDataException {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		try {
			obj = (JSONObject) parser.parse(moneyRequest);
		} catch (ParseException e) {
			throw new ParseDataException("JSON moneyRequest parse exception\n", e);
		}
		
		return (long) obj.get("dealer");
	}
	
	//return Object[long dealerID, String money1, String money2]
	public static Object[] parseCurrencyRequest(String currencyRequest) throws ParseDataException {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		try {
			obj = (JSONObject) parser.parse(currencyRequest);
		} catch (ParseException e) {
			throw new ParseDataException("JSON currencyRequest parse exception\n", e);
		}
		
		Object[] reqParameters = new Object[3];
		reqParameters[0] = (Long)obj.get("dealer");
		reqParameters[1] = (String)obj.get("money1");
		reqParameters[2] = (String)obj.get("money2");
		
		return reqParameters;
	}
		
	public static String[] getMoneyPair(String offer) throws ParseDataException {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		try {
			obj = (JSONObject) parser.parse(offer);
		} catch (ParseException e) {
			throw new ParseDataException("JSON offer parse exception\n", e);
		}
		
		String[] money = new String[2];
		money[0] = (String)obj.get("money1");
		money[1] = (String)obj.get("money2");
		
		return money;
	}
	
	@SuppressWarnings("unchecked")
	public static Order parseOfferToOrder(String offer, Currency currency, Clock clock) throws ParseDataException {
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List<String> creatArrayContainer() {
				return new LinkedList<String>();
			}

			public Map<String, Object> createObjectContainer() {
				return new LinkedHashMap<String, Object>();
			}

		};

		Map<String, Object> json = null;

		try {
			json = (Map<String, Object>) parser.parse(offer, containerFactory);
		} catch (ParseException e) {
			throw new ParseDataException("JSON offer parse to Order exception\n", e);
		}

		Order order = new Order();
		
		Iterator<Entry<String, Object>> it = json.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Object> entry;
			
			entry = (Map.Entry<String, Object>) it.next();
			order.setDealerID((Long)entry.getValue());
			
			entry = (Map.Entry<String, Object>) it.next();
			if (((String)entry.getValue()).equals("buy")){
				entry = it.next();
				order.setMoneyToBuy((String)entry.getValue());
				entry = it.next();
				order.setMoneyToSell((String)entry.getValue());
				entry = it.next();
				order.setBuyAmount(new BigDecimal((String)entry.getValue()));
				
				//money pair in Currency is in lex. order
				if (order.getMoneyToBuy().equals(currency.getMoney1())) {
					order.setRate(currency.getAsk());
					order.setSellAmount(order.getBuyAmount().multiply(order.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
					}
				if (order.getMoneyToBuy().equals(currency.getMoney2())) {
					order.setRate(new BigDecimal("1.00").divide(currency.getBid(), 4, BigDecimal.ROUND_HALF_UP));
					order.setSellAmount(order.getBuyAmount().multiply(order.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			} 
			
			if (((String)entry.getValue()).equals("sell")){
				entry = it.next();
				order.setMoneyToSell((String)entry.getValue());
				entry = it.next();
				order.setMoneyToBuy((String)entry.getValue());
				entry = it.next();
				order.setSellAmount(new BigDecimal((String)entry.getValue()));
				
				//money pair in Currency is in lex. order
				if (order.getMoneyToSell().equals(currency.getMoney1())) {
					order.setRate(currency.getBid());
					order.setBuyAmount(order.getSellAmount().multiply(order.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				if (order.getMoneyToSell().equals(currency.getMoney2())) {
					order.setRate(new BigDecimal("1.00").divide(currency.getAsk(), 4, BigDecimal.ROUND_HALF_UP));
					order.setBuyAmount(order.getSellAmount().multiply(order.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			}
			
		}
		
		LocalDateTime dateTime = LocalDateTime.now(clock);
		Timestamp timestamp = Timestamp.valueOf(dateTime);
		
		order.setTimestamp(DateUtil.parse(DateUtil.format(timestamp)));
		//order.setTimestamp(new Timestamp(new Date().getTime()));
		//new Timestamp(System.nanoTime()) - works not correct, probably because my low battery
		
		return order;
	}
	
	public static String formatAllDeallerMoney(List<DealerMoney> list) {
		Map<String, Object> map = new LinkedHashMap<>();
		
		for (int i = 0; i < list.size(); i++) {
			DealerMoney dm = list.get(i);
			map.put(dm.getMoneyID(), dm.getAmount().toString());
		}

		String json = JSONValue.toJSONString(map);
		
		return json;
		
	}
	
	public static String formatCurrencyPair(DealerMoney dealerMoney1, DealerMoney dealerMoney2, Currency currency) {
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("dealer", dealerMoney1.getDealerID());//dealerMoney1.getDealerID() == dealerMoney2.getDealerID()
		
		//moneyID : amount key-values pair put in key lex. order
		//i.e. first "eur", than "usd"
		if(dealerMoney1.getMoneyID().compareTo(dealerMoney2.getMoneyID()) > 0) {
			//swap
			DealerMoney temp = dealerMoney1;
			dealerMoney1 = dealerMoney2;
			dealerMoney2 = temp;
		}
		
		map.put(dealerMoney1.getMoneyID(), dealerMoney1.getAmount().toString());//BigDecimal is a subclass of java.lang.Number
		map.put(dealerMoney2.getMoneyID(), dealerMoney2.getAmount().toString());
		
		List<Object> rate = new LinkedList<>();
		
		rate.add(currency.getBid().toString());
		rate.add(currency.getAsk().toString());
		//rate.add(currency.getTimestamp().toString());
		//we'll use the latest currency
		map.put("currency", rate);
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}

	@SuppressWarnings("unchecked")
	public static Order parseOrder(String jsonOrder) throws ParseDataException {		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List<String> creatArrayContainer() {
				return new LinkedList<String>();
			}

			public Map<String, Object> createObjectContainer() {
				return new LinkedHashMap<String, Object>();
			}

		};

		Map<String, Object> json = null;

		try {
			json = (Map<String, Object>) parser.parse(jsonOrder, containerFactory);
		} catch (ParseException e) {
			throw new ParseDataException("JSON order parse exception\n", e);
		}
		
		Order order = new Order();
		
		Iterator<Entry<String, Object>> it = json.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Object> entry;
			
			entry = it.next();
			order.setDealerID((Long)entry.getValue());
			
			entry = it.next();
			order.setMoneyToSell((String)entry.getValue());
			
			entry = it.next();
			order.setMoneyToBuy((String)entry.getValue());
			
			entry = it.next();
			order.setSellAmount(new BigDecimal((String)entry.getValue()));
			
			entry = it.next();
			order.setBuyAmount(new BigDecimal((String)entry.getValue()));
			
			entry = it.next();
			order.setRate(new BigDecimal((String)entry.getValue()));
			
			entry = it.next();
			order.setTimestamp(DateUtil.parse((String)entry.getValue()));
		}
		
		return order;
	}
	
	public static String formatOrder(Order order) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("dealer", order.getDealerID());
		map.put("moneyToSell", order.getMoneyToSell());
		map.put("moneyToBuy", order.getMoneyToBuy());
		map.put("amountToSell", order.getSellAmount().toString());
		map.put("amountToBuy", order.getBuyAmount().toString());
		map.put("rate", order.getRate().toString());
		map.put("timestamp", DateUtil.format(order.getTimestamp()));
		
		String json = JSONValue.toJSONString(map);
		
		return json;
	}
	
	public static boolean parseConfirm(String message) throws ParseDataException{
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(message);
		} catch (ParseException e) {
			throw new ParseDataException("JSON confirm parse exception\n", e);
		}
		return (boolean)obj.get("confirm");
	}
	
	public static String getExchangeCommittedMessage(boolean flag) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("committed", flag);
		
		String json = JSONValue.toJSONString(map);

		return json;
	}
	
}
