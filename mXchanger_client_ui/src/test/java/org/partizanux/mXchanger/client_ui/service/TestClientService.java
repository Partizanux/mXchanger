package org.partizanux.mXchanger.client_ui.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.partizanux.mXchanger.client.Client;
import org.partizanux.mXchanger.client_ui.TestClient;
import org.partizanux.mXchanger.client_ui.model.CurrencyRate;
import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.model.UOrder;
import org.partizanux.mXchanger.client_ui.service.ClientService;
import org.partizanux.mXchanger.client_ui.util.JsonClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javafx.collections.ObservableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mock-client-context.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class TestClientService extends TestClient{
	
	@Autowired
	private Client client;
	
	@Autowired
	private ClientService service;
	
	@After
	public void resetMock() {
		reset(client);
	}
	
	@Test
	public void testGetDealerMoney() {
		
		long id = 1L;
		String expMoney1 = "usd";
		String expMoney2 = "eur";
		BigDecimal expAmount1 = new BigDecimal("100.00");
		BigDecimal expAmount2 = new BigDecimal("120.00");
		
		String request = JsonClientUtil.getDealerMoneyRequest(id);
		String response = getAllDealerMoneyResponse(expMoney1, expMoney2, expAmount1, expAmount2);
		
		expect(client.sendMessage(request, false)).andReturn(response);
		replay(client);
		
		ObservableList<DealerMoney> list = service.getDealerMoney();
		DealerMoney dm1 = list.get(0);
		DealerMoney dm2 = list.get(1);
		
		assertEquals(expMoney1, dm1.getMoney().getValue());
		assertEquals(expAmount1, dm1.getAmount().getValue());
		assertEquals(expMoney2, dm2.getMoney().getValue());
		assertEquals(expAmount2, dm2.getAmount().getValue());
		
	}
	
	@Test
	public void testGetCurrency() {
		
		long id = 1L;
		String expMoney1 = "usd";
		String expMoney2 = "eur";
		BigDecimal expBid = new BigDecimal("11.34");
		BigDecimal expAsk = new BigDecimal("12.02");
		
		String request = JsonClientUtil.getCurrencyRequest(id, expMoney1, expMoney2);
		String response = getJsonCurrencyResponse(expMoney1, expMoney2, expBid, expAsk);
		
		expect(client.sendMessage(request, false)).andReturn(response);
		replay(client);
		
		CurrencyRate curr = service.getCurrency(expMoney1, expMoney2);
		
		assertEquals(expMoney1, curr.getMoney1().getValue());
		assertEquals(expMoney2, curr.getMoney2().getValue());
		assertEquals(expBid, curr.getBid().getValue());
		assertEquals(expAsk, curr.getAsk().getValue());
		
	}
	
	@Test
	public void testOfferEx() {
		
		Long expId = 1l;
		String expMoneyToSell = "eur";
		String expMoneyToBuy = "usd";
		BigDecimal expAmountToSell = new BigDecimal("10.14");
		BigDecimal expAmountToBuy = new BigDecimal("12.53");
		BigDecimal expRate = new BigDecimal("7.08");
		
		String request = JsonClientUtil.getOfferExRequest(expId, expMoneyToSell, expMoneyToBuy, expAmountToSell);
		String response = getJsonOrder(expId, expMoneyToSell, expMoneyToBuy, expAmountToSell, expAmountToBuy, expRate);
		
		expect(client.sendMessage(request, true)).andReturn(response);
		replay(client);
		
		UOrder uo = service.offerEx(expId, expMoneyToSell, expMoneyToBuy, expAmountToSell);
		
		assertEquals(expId, uo.getDealer().getValue());
		assertEquals(expMoneyToSell, uo.getMoneyToSell().getValue());
		assertEquals(expMoneyToBuy, uo.getMoneyToBuy().getValue());
		assertEquals(expAmountToSell, uo.getAmountToSell().getValue());
		assertEquals(expAmountToBuy, uo.getAmountToBuy().getValue());
		assertEquals(expRate, uo.getRate().getValue());
		// ignore timestamp
		
	}
	
	@Test
	public void testConfirmEx() {
		
		boolean flag = true;
		String request = JsonClientUtil.getConfirmRequest(flag);
		
		expect(client.sendMessageInSession(request)).andReturn("we don't care");;
		replay(client);
		
		service.confirmEx(flag);
		
		verify(client);
		
	}
	
}
