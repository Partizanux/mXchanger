package org.partizanux.mXchanger.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.Clock;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.partizanux.mXchanger.service.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//Ignore
public class ServerTest {

	private static final Logger logger = LoggerFactory.getLogger(ServerTest.class);

	private static Object serverIn = new Object();
	private static Object fire = new Object();
	private static Thread junitThread = Thread.currentThread();
	private static Thread serverThread;
	private static AbstractApplicationContext ctx;

	private static final int PORT = 9123;
	private static final String HOSTNAME = "localhost";

	private static IoConnector connector = new NioSocketConnector();
	private static IoSession session; // make local
	private static IoHandler testHandler = new TestHandler();
	
	private static Clock testClock;// = Clock.fixed(Instant.now(), ZoneId.systemDefault());

	//@BeforeClass
	public synchronized static void start() throws IOException, InterruptedException {

		// server
		Runnable server = () -> {
			synchronized (serverIn) {
				try {
					//OR DukascopyServer.main() - ?
					ctx = new ClassPathXmlApplicationContext("classpath:dao-context.xml",
							"classpath:service-context.xml", "classpath:server-context.xml",
							"classpath:test-server-context.xml");
					testClock = (Clock)ctx.getBean("testClock");
					ctx.registerShutdownHook();
				} catch (BeansException e) {
					logger.error("failed to start test server\n", e);
					junitThread.interrupt();
				}
				serverIn.notifyAll();// server is initialized
			}
		};

		synchronized (serverIn) {
			serverThread = new Thread(server);
			serverThread.start();
			
			// wait for a server initializing
			serverIn.wait();
		}

		// client
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

		connector.setHandler(testHandler);

		connector.getSessionConfig().setReadBufferSize(2048);
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

	}

	//@AfterClass
	public synchronized static void dispose(){
		connector.dispose();
	}
	
	//@Test
	public synchronized void testGetCurrencyPairRateManyTimes() throws InterruptedException{
		for(int i = 0; i < 1; i++){
			ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
			future.awaitUninterruptibly();
			session = future.getSession();
			getCurrencyPairRate();
			if (session != null)
				session.getCloseFuture().awaitUninterruptibly();
			if (!session.isClosing())
				session.close(false);
		}
	}
	
	private synchronized void getCurrencyPairRate() throws InterruptedException {
		Object message = TestUtil.getCSVCurrencyReq();
		session.write(message);

		// for now json currency doesn't include timestamp, so income = testGetJsonCurrency
		Object exp = TestUtil.getJsonCurrency();
		Object act;
		
		synchronized(fire) {
			fire.wait(3000L);
			act = ((TestHandler) testHandler).getIncome();
		}

		assertEquals(exp, act);
	}
	
	//@Test
	public void testOfferAndDoExchange() throws InterruptedException {
		ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
		future.awaitUninterruptibly();
		session = future.getSession();
		
		Object offer = TestUtil.getCSVExchangeOffer();
		session.write(offer);
		
		Object jsonOrder;
		synchronized(fire) {
			fire.wait(3000L);
			jsonOrder = ((TestHandler) testHandler).getIncome();
		}
		assertEquals(TestUtil.getJsonOrder(testClock), jsonOrder);
		
		// - !
		Object confirm = TestUtil.getConfirmMessage();
		session.write(confirm);
		
		Object committed;
		synchronized(fire) {
			fire.wait(3000L);
			committed = ((TestHandler) testHandler).getIncome();
		}
		
		assertEquals(JSONUtil.getExchangeCommittedMessage(true), committed);
		
		if (session != null)
			session.getCloseFuture().awaitUninterruptibly();
		if (!session.isClosing())
			session.close(false);
	}

	private static class TestHandler extends IoHandlerAdapter {
		private Object income;// response from server

		public Object getIncome() {
			return income;
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			synchronized(fire) {
				income = message;
				fire.notifyAll();
			}
		}
	}
}
