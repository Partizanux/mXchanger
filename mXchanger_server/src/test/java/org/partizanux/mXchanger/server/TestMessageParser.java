package org.partizanux.mXchanger.server;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.partizanux.mXchanger.server.command.Command;
import org.partizanux.mXchanger.server.command.GetMoneyAndCurrency;
import org.partizanux.mXchanger.server.parser.MessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-parser-context.xml"})
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TestMessageParser {
	@Autowired
	private MessageParser<Command<String,?>> parser;
	
	@Test
	public void testParseToCommand() {
		Object message = TestUtil.getCSVCurrencyReq();
		Command<String, ?> command = (Command<String, ?>)parser.parseToCommand(message);
		
		assertEquals(command.getClass(), GetMoneyAndCurrency.class);
	}
	
	@Test
	public void testGetMessage() {
		String exp = TestUtil.getExchangeOffer();
		Object message = TestUtil.getCSVExchangeOffer();
		
		String act = (String) parser.getData(message);
		
		assertEquals(exp, act);
	}
}
