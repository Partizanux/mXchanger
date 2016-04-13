package org.partizanux.mXchanger.client;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.Clock;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.partizanux.mXchanger.server.TestServer;
import org.partizanux.mXchanger.server.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);
	
	private static Object waitForServer = new Object();
	
	private static Clock serverClock;
	
	private Client client;
	
	private volatile static boolean wasSignalled = false;
	
	private static void doNotify() {
		wasSignalled = true;
	}
	
	@BeforeClass
	public static void startTestServer() throws Exception {
		
		Runnable testServer = () -> { 
			synchronized (waitForServer) {
				String[] args = {};
				try {
					TestServer.main(args);
					serverClock = TestServer.getClock();
				} catch (Exception e) {
					logger.error("Exception, can't start Server thread\n", e);
					Thread.currentThread().interrupt();
				}
				doNotify();
				waitForServer.notifyAll();
			}
		};
		
		synchronized (waitForServer) {
			Thread serverThread = new Thread(testServer);
			serverThread.start();
			
			while(!wasSignalled) {
				try {
					waitForServer.wait(3000L);
				} catch (InterruptedException e) {
					logger.error("InterruptedException in test thread\n", e);
					Thread.currentThread().interrupt();
				}
			}
			
			if(serverThread.isAlive()) {
				if (serverThread.isInterrupted()) {
					throw new Exception ("serverThread isInterrupted");
					}
			} else {
				throw new Exception ("serverThread is not alive");
			}
			
			//clear signal and continue running.
		    wasSignalled = false;
		}
		
	}
	
	@After
	public void cleanup() throws IOException, InterruptedException {
		client.dispose();
		client = null;
		TestServer.reinitializeDB();
	}
	
	@Test
	public void testSendMessage() {
		
		String req = (String) TestUtil.getCSVAllDealerMoneyReq();
		String exp = TestUtil.getJsonAllDealerMoney();// + "\n";
		
		client = new Client();
		String act = client.sendMessage(req, false);
		
		assertEquals(exp, act);
	}
	
	@Test
	public void testSendMessageInSession() {
				
		String offer = (String) TestUtil.getCSVExchangeOffer();
		String expOrder = TestUtil.getJsonOrder(serverClock);
		
		client = new Client();
		String actOrder = client.sendMessage(offer, true);
		
		assertEquals(expOrder, actOrder);
		
		// ensure sending confirm message without exceptions
		String confirm = TestUtil.getConfirmMessage();
		client.sendMessageInSession(confirm);
		
	}
	
	// test that dealer money changes enough fast after confirm message
	// and next request from client will get changed values
	@Test
	public void testDealerMoneyAfterExchange() {
		String offer = (String) TestUtil.getCSVExchangeOffer();
		
		client = new Client();
		client.sendMessage(offer, true);
		
		//assertEquals(actOrder, expOrder);
		
		String confirm = TestUtil.getConfirmMessage();
		String answer = client.sendMessageInSession(confirm);
		
		String act = client.sendMessage((String) TestUtil.getCSVAllDealerMoneyReq(), false);
		String exp = TestUtil.getJsonAllDealerMoneyAfterExchange();
		
		assertEquals(exp, act);
		
	}
	
	
}
