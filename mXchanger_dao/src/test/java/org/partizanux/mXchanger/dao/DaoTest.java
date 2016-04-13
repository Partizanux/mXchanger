package org.partizanux.mXchanger.dao;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.partizanux.mXchanger.dao.JDBCTemplateDao;
import org.partizanux.mXchanger.domain.Currency;
import org.partizanux.mXchanger.domain.DealerMoney;
import org.partizanux.mXchanger.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:test-dao-context.xml",
		"classpath:test-transaction-context.xml"
})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
	TransactionalTestExecutionListener.class })
public class DaoTest {
	@Autowired
	private JDBCTemplateDao dao;
	
	@Test
	public void testGetDealerMoney() {
		DealerMoney dealerMoney = dao.getDealerMoney(1L, "usd");
		
		assertEquals(1L, dealerMoney.getDealerID());
		assertEquals("usd", dealerMoney.getMoneyID());
		assertThat(dealerMoney.getAmount(), isA(BigDecimal.class));
		assertTrue(dealerMoney.getAmount().signum() >= 0);
	}
	
	@Test
	public void testGetAllDealerMoney() {
		List<DealerMoney> list = dao.getAllDealerMoney(1L);
		list.forEach(item -> {
			assertEquals(1L, item.getDealerID());
			assertThat(item.getMoneyID(), isA(String.class));
			assertEquals(item.getMoneyID().length(), 3);
			assertTrue(item.getMoneyID().equals("eur") || item.getMoneyID().equals("usd"));
			assertThat(item.getAmount(), isA(BigDecimal.class));
			assertTrue(item.getAmount().signum() >= 0);
			});
	}
	
	@Test
	public void testGetCurrency() {
		Currency currency = dao.getCurrencyPairRate("eur", "usd");
		
		assertEquals(currency.getMoney1(), "eur");
		assertEquals(currency.getMoney2(), "usd");
		assertThat(currency.getTimestamp(), isA(Timestamp.class));
		assertThat(currency.getBid(), isA(BigDecimal.class));
		assertThat(currency.getAsk(), isA(BigDecimal.class));
		assertTrue(currency.getBid().signum() >= 0);
		assertTrue(currency.getAsk().signum() >= 0);
		assertTrue(currency.getBid().compareTo(currency.getAsk()) < 0);
	}
	
	
	@Test
	@Transactional
	public void testUpdateDealerMoneyAmount() {
		BigDecimal exp = new BigDecimal("500.00");
		dao.updateDealerMoneyAmount(1L, "eur", new BigDecimal("500.00"));
		DealerMoney dealerMoney = dao.getDealerMoney(1L, "eur");
		
		assertEquals(exp, dealerMoney.getAmount());
	}
	
	@Test
	@Transactional
	public void testPlusDealerMoneyAmount() {
		BigDecimal exp = new BigDecimal("1500.00");
		dao.plusDealerMoneyAmount(1L, "eur", new BigDecimal("500.00"));
		DealerMoney dealerMoney = dao.getDealerMoney(1L, "eur");
		
		assertEquals(exp, dealerMoney.getAmount());
	}
	
	@Test
	@Transactional
	public void testMinusDealerMoneyAmount() {
		BigDecimal exp = new BigDecimal("500.00");
		dao.minusDealerMoneyAmount(1L, "eur", new BigDecimal("500.00"));
		DealerMoney dealerMoney = dao.getDealerMoney(1L, "eur");
		
		assertEquals(exp, dealerMoney.getAmount());
	}
	
	@Test
	public void testGetCurrencyPairRateArgsOrder() {
		Currency c1 = dao.getCurrencyPairRate("eur", "usd");
		Currency c2 = dao.getCurrencyPairRate("usd", "eur");
		
		assertEquals(c1.getMoney1(), c2.getMoney2());
		assertEquals(c1.getMoney2(), c2.getMoney1());
		assertEquals(c1.getTimestamp(), c2.getTimestamp());
		// scale = 2
		assertEquals(c1.getBid().setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal("1.00").divide(c2.getAsk(), 2, BigDecimal.ROUND_HALF_UP));
		assertEquals(c1.getAsk().setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal("1.00").divide(c2.getBid(), 2, BigDecimal.ROUND_HALF_UP));
	}
	
	@Test
	@Transactional
	public void testSaveOrder() {
		Order order = new Order();
		order.setDealerID(1L);
		order.setMoneyToSell("eur");
		order.setMoneyToBuy("usd");
		order.setBuyAmount(new BigDecimal("100.00"));
		order.setSellAmount(new BigDecimal("105.35"));
		order.setRate(new BigDecimal("1.25"));
		order.setTimestamp(new Timestamp(new Date().getTime()));
		
		dao.saveOrder(order);
	}
	
}
