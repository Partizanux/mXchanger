package org.partizanux.mXchanger.client;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// Ignore

public class TestClientMina {
	
	private static final int PORT = 9123;
	private static NioSocketAcceptor acceptor = new NioSocketAcceptor();
	
	private static TestHandler testHandler = new TestHandler();
	
	private static AbstractApplicationContext ctx;
	
	//@BeforeClass
	public static void start() throws IOException {
		// start server for test
		acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));

        acceptor.setHandler(testHandler);

        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress(PORT) );
		
		// and start our client
        ctx = new ClassPathXmlApplicationContext("classpath:test-client-context.xml");
		ctx.registerShutdownHook();
	}
	
	//@AfterClass
	public synchronized static void dispose(){
		acceptor.dispose();
	}
	
	
	//@Test
	public void testSendMessage() {
		ClientMina client = ctx.getBean(ClientMina.class);
		
		String request = "request";
		String response = client.sendMessage(request);
		
		String exp = testHandler.getResponse();
		
		assertEquals(response, exp);
	}
	
	private static class TestHandler extends IoHandlerAdapter {	
		private final String RESPONSE = "test response";
		
		public String getResponse() {
			return RESPONSE;
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			session.write(RESPONSE);
		}
	}
}
