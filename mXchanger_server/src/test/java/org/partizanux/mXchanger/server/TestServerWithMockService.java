package org.partizanux.mXchanger.server;

import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

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
import org.partizanux.mXchanger.service.MXchangerService;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.partizanux.mXchanger.service.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestServerWithMockService {

	private static final Logger logger = LoggerFactory.getLogger(TestServerWithMockService.class);

	private static Object serverIn = new Object();
	private static Object fire = new Object();
	private static Thread junitThread = Thread.currentThread();
	private static Thread serverThread;
	private static AbstractApplicationContext ctx;

	private static final int PORT = 9123;
	private static final String HOSTNAME = "localhost";

	private static IoConnector connector = new NioSocketConnector();
	private static IoHandler testHandler = new TestHandler();
	
	private static Clock testClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

	@BeforeClass
	public synchronized static void start() throws IOException, InterruptedException {

		// server
		Runnable server = () -> {
			synchronized (serverIn) {
				try {
					//OR DukascopyServer.main() - ?
					ctx = new ClassPathXmlApplicationContext("classpath:mock-server-context.xml");
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

	@AfterClass
	public synchronized static void dispose(){
		connector.dispose();
	}
	
	@Test
	public void testGetAllDealerMoney() throws ParseDataException, InterruptedException {
		@SuppressWarnings("unchecked")
		MXchangerService<Object> service_mock = (MXchangerService<Object>) ctx.getBean("service_mock");
		
		Object data = TestUtil.getAllDealerMoneyReq();
		
		expect(service_mock.getAllDealerMoney(data)).andReturn(TestUtil.getJsonAllDealerMoney());
		
		replay(service_mock);
		
		ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		
		Object message = TestUtil.getCSVAllDealerMoneyReq();
		session.write(message);
		
		synchronized(fire) {
			fire.wait(3000L);
		}
		
		if (session != null)
			session.getCloseFuture().awaitUninterruptibly();
		if (!session.isClosing())
			session.close(false);
		
		reset(service_mock);
		
	}
	
	@Test
	public synchronized void testGetCurrencyPairRateManyTimes() throws InterruptedException, ParseDataException{
		int times = 10;
		
		@SuppressWarnings("unchecked")
		MXchangerService<Object> service_mock = (MXchangerService<Object>) ctx.getBean("service_mock");
		
		Object data = TestUtil.getCurrencyReq();
		
		expect(service_mock.getDealerMoneyPair(data)).andReturn(TestUtil.getJsonCurrency()).times(times);
		
		replay(service_mock);
		
		for(int i = 0; i < times; i++){
			ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
			future.awaitUninterruptibly();
			IoSession session = future.getSession();
			
			Object message = TestUtil.getCSVCurrencyReq();
			session.write(message);
			
			synchronized(fire) {
				fire.wait(3000L);
			}
			
			if (session != null)
				session.getCloseFuture().awaitUninterruptibly();
			if (!session.isClosing())
				session.close(false);
		}
		
		verify(service_mock);
		
		reset(service_mock);
	}
	
	
	@Test
	public void testOfferAndDoExchange() throws InterruptedException, ParseDataException {
		
		Object order = TestUtil.getJsonOrder(testClock);
		
		@SuppressWarnings("unchecked")
		MXchangerService<Object> service_mock = (MXchangerService<Object>) ctx.getBean("service_mock");
		
		expect(service_mock.requestEx(TestUtil.getExchangeOffer())).andReturn(order);
		replay(service_mock);
		
		ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
				
		session.write(TestUtil.getCSVExchangeOffer());
		
		//wait for order comes to this thread(client)
		synchronized(fire) {
			fire.wait(3000L);
		}
		
		verify(service_mock);
		
		reset(service_mock);
		
		Object confirm = TestUtil.getConfirmMessage();
		
		expect(service_mock.confirmEx(confirm)).andReturn(true);
		
		Object commited = JSONUtil.getExchangeCommittedMessage(true);
		expect(service_mock.doExchange(order)).andReturn(commited);
		//service_mock.doExchange(order);
		replay(service_mock);
		
		session.write(confirm);
		
		// !!!
		TimeUnit.SECONDS.sleep(1);
		
		verify(service_mock);
		
		reset(service_mock);
		
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
