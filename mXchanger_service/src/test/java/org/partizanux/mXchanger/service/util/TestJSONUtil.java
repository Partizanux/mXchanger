package org.partizanux.mXchanger.service.util;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;
import org.partizanux.mXchanger.service.TestUtil;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.partizanux.mXchanger.service.util.JSONUtil;

public class TestJSONUtil {
	private Clock testClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
	
	@Test
	public void testParseDealerMoneyRequest() throws ParseDataException {
		String req = TestUtil.getAllDealerMoneyRequest();
		long act = JSONUtil.parseDealerMoneyRequest(req);
		
		assertEquals(1L, act);
	}
	
	@Test
	public void testFormatDealerMoney() {
		List<DealerMoney> list = TestUtil.getAllDealerMoneyList();
		String act = JSONUtil.formatAllDeallerMoney(list);
		String exp = TestUtil.getJsonAllDealerMoney();
		
		assertEquals(exp, act);
	}
	
	@Test
	public void testParseCurrencyRequest() throws ParseDataException {
		String req = TestUtil.getCurrencyRequest();
		Object[] act = JSONUtil.parseCurrencyRequest(req);
		Object[] exp = TestUtil.getCurrencyRequestParameters();
		
		assertArrayEquals(exp, act);
	}
	
	@Test
	public void testGetMoneyPair() throws ParseDataException {
		String offer = TestUtil.getJsonOffer();
		
		assertEquals(JSONUtil.getMoneyPair(offer)[0], "eur");
		assertEquals(JSONUtil.getMoneyPair(offer)[1], "usd");
	}
	
	@Test
	public void testParseOfferToOrder() throws ParseDataException {
		String offer = TestUtil.getJsonOffer();
		Currency currency = TestUtil.getCurrency();
		Order order = JSONUtil.parseOfferToOrder(offer, currency, testClock);
		
		Order exp = TestUtil.getOrder(testClock);
		
		assertEquals(exp.getDealerID(), order.getDealerID());
		assertEquals(exp.getMoneyToBuy(), order.getMoneyToBuy());
		assertEquals(exp.getMoneyToSell(), order.getMoneyToSell());
		assertEquals(exp.getBuyAmount(), order.getBuyAmount());
		assertEquals(exp.getSellAmount(), order.getSellAmount());
		assertEquals(exp.getRate(), order.getRate());
	}
	
	@Test
	public void testFormatCurrencyPair() {
		String exp = TestUtil.getJsonCurrency();
		
		String pair = JSONUtil.formatCurrencyPair(TestUtil.getDealerMoney("eur"),
				TestUtil.getDealerMoney("usd"), TestUtil.getCurrency());
		
		assertEquals(exp, pair);
	}
	
	@Test
	public void testFormatOrder() {
		String exp = TestUtil.getJsonOrder(testClock);
		
		Order order = TestUtil.getOrder(testClock);
		String act = JSONUtil.formatOrder(order);
		
		assertEquals(exp, act);
	}
	
	@Test
	public void testParseConfirm() throws ParseDataException {
		assertTrue(JSONUtil.parseConfirm(TestUtil.getConfirmMessage()));
	}
	
	@Test
	public void testParseOrder() throws ParseDataException {
		Order exp = TestUtil.getOrder(testClock);
		Order act = JSONUtil.parseOrder(TestUtil.getJsonOrder(testClock));
		
		assertEquals(exp.getDealerID(), act.getDealerID());
		assertEquals(exp.getMoneyToBuy(), act.getMoneyToBuy());
		assertEquals(exp.getMoneyToSell(), act.getMoneyToSell());
		assertEquals(exp.getBuyAmount(), act.getBuyAmount());
		assertEquals(exp.getSellAmount(), act.getSellAmount());
		assertEquals(exp.getRate(), act.getRate());
		assertEquals(exp.getTimestamp(), act.getTimestamp());
	}
	
	@Test
	public void testGetExchangeCommittedMessage() throws ParseException {
		boolean flag = true;
		String message = JSONUtil.getExchangeCommittedMessage(flag);
		
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		obj = (JSONObject) parser.parse(message);
		
		boolean act = (boolean)obj.get("committed");
		
		assertEquals(flag, act);
	}
	
}
