package org.partizanux.mXchanger.service;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.partizanux.mXchanger.domain.Order;
import org.partizanux.mXchanger.service.MXchangerService;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.partizanux.mXchanger.service.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-service-context.xml",
		"classpath:test-dao-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
	TransactionalTestExecutionListener.class })
public class TestService {
	@Autowired
	private MXchangerService<String> service;
	@Autowired
	private Clock testClock;
	
	@Test
	public void testGetAllDealerMoney() throws ParseDataException {
		String req = TestUtil.getAllDealerMoneyRequest();
		String act = service.getAllDealerMoney(req);
		String exp = TestUtil.getJsonAllDealerMoney();
		
		assertEquals(exp, act);
	}
	
	@Test
	public void testGetDealerMoneyPair() throws ParseDataException {
		String exp = TestUtil.getJsonCurrency();
		String req = TestUtil.getCurrencyRequest();
		
		String message = service.getDealerMoneyPair(req);
		
		assertEquals(exp, message);
	}
	
	@Test
	public void testOfferEx() throws ParseDataException {
		String exp = TestUtil.getJsonOrder(testClock);
		
		String offer = TestUtil.getJsonOffer();
		String order = service.requestEx(offer);
		
		assertEquals(exp, order);//timestamp in exp and order can differ in one second
		
		//assertEquals(exp.getDealerID(), order.getDealerID());
		//assertEquals(exp.getMoneyToBuy(), order.getMoneyToBuy());
		//assertEquals(exp.getMoneyToSell(), order.getMoneyToSell());
		//assertEquals(exp.getBuyAmount(), order.getBuyAmount());
		//assertEquals(exp.getSellAmount(), order.getSellAmount());
		//assertEquals(exp.getRate(), order.getRate());
		
	}
	
	@Test
	public void testConfirmEx() throws ParseDataException {
		boolean conf = service.confirmEx(TestUtil.getConfirmMessage());
		//TestUtil gives the message with true
		assertTrue(conf);
	}
	
	@Test
	@Transactional
	public void testDoExchange() throws ParseDataException {
		//ensure doExchange runs without exceptions and gives correct message
		String act = service.doExchange(TestUtil.getJsonOrder(testClock));
		String exp = JSONUtil.getExchangeCommittedMessage(true);
		
		assertEquals(exp, act);
	}
	
	@Test
	public void testCheckDealerMoney() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = service.getClass().getDeclaredMethod("checkDealerMoney", Order.class);
		method.setAccessible(true);
		
		Order order = TestUtil.getOrder(testClock);
		//make this order incorrect
		//set more sum that dealer has
		order.setSellAmount(new BigDecimal("1999.00"));
		
		boolean flag = (boolean)method.invoke(service, order);
		assertFalse(flag);
	}
	
	@Test
	public void testCheckIfCurrencyNotChanged() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = service.getClass().getDeclaredMethod("checkIfCurrencyNotChanged", Order.class);
		method.setAccessible(true);
		
		Order order = TestUtil.getOrder(testClock);

		//change a timestamp in order to older than a currency timestamp date
		order.setTimestamp(new Timestamp(new Date().getTime() - (long)3e16));//(long)3e16 - almost year in nanosec
		
		boolean flag = (boolean)method.invoke(service, order);
		assertFalse(flag);
	}
	
	@Test
	public void testParseDataException(){
		String meaningless = "blablabla";

		int i = 0;
		while(i < 4){
			try {
				switch(i) {
					case 0:
						service.confirmEx(meaningless);
					case 1:
						service.doExchange(meaningless);
					case 2:
						service.getDealerMoneyPair(meaningless);
					case 3:
						service.requestEx(meaningless);
				}
			} catch (ParseDataException e) {
				i++;
			}
		}
		
		
		assertEquals(4, i);
		//or use 
		//com.googlecode.catchexception.CatchException.verifyException(service, ParseDataException.class).confirmEx(meaningless);
	}
	
}
