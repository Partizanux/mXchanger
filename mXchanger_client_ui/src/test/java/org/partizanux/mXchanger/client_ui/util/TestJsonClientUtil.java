package org.partizanux.mXchanger.client_ui.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.partizanux.mXchanger.client_ui.TestClient;
import org.partizanux.mXchanger.client_ui.model.CurrencyRate;
import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.model.UOrder;
import org.partizanux.mXchanger.client_ui.util.JsonClientUtil;

public class TestJsonClientUtil extends TestClient{
	
	private static final String REGEX = "([A-Za-z0-9-_]+),";
	
	private static final String allDealerMoney_Method_Name = "getAllDealerMoney";
	private static final String getCurrency_Method_Name = "getCurrency";
	private static final String offer_Method_Name = "offer";
	
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
	
	private static String getRequestMethod(String req) {
		String[] arr = req.toString().split(",");
		return arr[0];
	}
	
	private static String getRequestJsonData(String req) {
		return req.toString().replaceFirst(REGEX, "");
	}
	
	@Test
	public void testGetDealerMoneyRequest() throws ParseException {
		
		long exp = 1L;
		String req = JsonClientUtil.getDealerMoneyRequest(exp);
		
		String actMethodName = getRequestMethod(req);
		assertEquals(allDealerMoney_Method_Name, actMethodName);
		
		long act = (long) parseJson2Map(getRequestJsonData(req)).get("dealer");
		assertEquals(exp, act);
	}
	
	@Test
	public void testGetCurrencyRequest() throws ParseException {
		
		long expId = 1L;
		String expMoney1 = "eur";
		String expMoney2 = "usd";
		
		String req = JsonClientUtil.getCurrencyRequest(expId, expMoney1, expMoney2);
		
		String actMethodName = getRequestMethod(req);
		assertEquals(getCurrency_Method_Name, actMethodName);
		
		long actId = (long) parseJson2Map(getRequestJsonData(req)).get("dealer");
		assertEquals(expId, actId);
		String actMoney1 = (String) parseJson2Map(getRequestJsonData(req)).get("money1");
		assertEquals(expMoney1, actMoney1);
		String actMoney2 = (String) parseJson2Map(getRequestJsonData(req)).get("money2");
		assertEquals(expMoney2, actMoney2);
		
	}
	
	@Test
	public void testGetOfferExRequest() throws ParseException {
		
		long expId = 1L;
		String expOp = "sell";
		String expMoney1 = "eur";
		String expMoney2 = "usd";
		BigDecimal expAmount = new BigDecimal("100.01");
		
		String req = JsonClientUtil.getOfferExRequest(expId, expMoney1, expMoney2, expAmount);
		
		String actMethodName = getRequestMethod(req);
		assertEquals(offer_Method_Name, actMethodName);
		
		long actId = (long) parseJson2Map(getRequestJsonData(req)).get("dealer");
		assertEquals(expId, actId);
		String actOp = (String) parseJson2Map(getRequestJsonData(req)).get("buyOrSell");
		assertEquals(expOp, actOp);
		String actMoney1 = (String) parseJson2Map(getRequestJsonData(req)).get("money1");
		assertEquals(expMoney1, actMoney1);
		String actMoney2 = (String) parseJson2Map(getRequestJsonData(req)).get("money2");
		assertEquals(expMoney2, actMoney2);
		String amount = (String) parseJson2Map(getRequestJsonData(req)).get("amount");
		assertEquals(expAmount, new BigDecimal(amount));
		
	}
	
	@Test
	public void testGetConfirmRequest() throws ParseException {
		
		boolean exp = false;
		
		String confirm = JsonClientUtil.getConfirmRequest(exp);
		
		boolean act = (boolean) parseJson2Map(confirm).get("confirm");
		
		assertEquals(exp, act);
	}
	
	@Test
	public void testParseAllDealerMoney() throws ParseException {
		
		String expMoney1 = "eur";
		String expMoney2 = "usd";
		BigDecimal expAmount1 = new BigDecimal("100.12");
		BigDecimal expAmount2 = new BigDecimal("98.97");
		
		String res = getAllDealerMoneyResponse(expMoney1, expMoney2, expAmount1, expAmount2);
		
		List<DealerMoney> money = JsonClientUtil.parseAllDealerMoney(res);
		
		DealerMoney dm1 = money.get(0);
		assertEquals(expMoney1, dm1.getMoney().getValue());
		assertEquals(expAmount1, dm1.getAmount().getValue());
		
		DealerMoney dm2 = money.get(1);
		assertEquals(expMoney2, dm2.getMoney().getValue());
		assertEquals(expAmount2, dm2.getAmount().getValue());
		
	}
	
	@Test
	public void testParseCurrencyRate() throws ParseException {
		
		String expMoney1 = "eur";
		String expMoney2 = "usd";
		BigDecimal expBid = new BigDecimal("1.0919");
		BigDecimal expAsk = new BigDecimal("1.0921");
		
		String json = getJsonCurrencyResponse(expMoney1, expMoney2, expBid, expAsk);
		
		CurrencyRate cr = JsonClientUtil.parseCurrencyRate(json);
		
		assertEquals(expMoney1, cr.getMoney1().getValue());
		assertEquals(expMoney2, cr.getMoney2().getValue());
		assertEquals(expBid, cr.getBid().getValue());
		assertEquals(expAsk, cr.getAsk().getValue());
	}
	
	@Test
	public void testParseOrder() throws ParseException {
		
		Long expId = 1l;
		String expMoneyToSell = "eur";
		String expMoneyToBuy = "usd";
		BigDecimal expAmountToSell = new BigDecimal("10.14");
		BigDecimal expAmountToBuy = new BigDecimal("12.53");
		BigDecimal rate = new BigDecimal("1.0919");
		
		String json = getJsonOrder(expId, expMoneyToSell, expMoneyToBuy, expAmountToSell, expAmountToBuy, rate);
		
		UOrder uo = JsonClientUtil.parseOrder(json);
		
		assertEquals(expId, uo.getDealer().getValue());
		assertEquals(expMoneyToSell, uo.getMoneyToSell().getValue());
		assertEquals(expMoneyToBuy, uo.getMoneyToBuy().getValue());
		assertEquals(expAmountToSell, uo.getAmountToSell().getValue());
		assertEquals(expAmountToBuy, uo.getAmountToBuy().getValue());
		assertEquals(rate, uo.getRate().getValue());
		// ignore timestamp
		
	}
	
}
