package org.partizanux.mXchanger.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import javax.annotation.PreDestroy;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClientMina {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientMina.class);
	
	private static final String HOST = "localhost";
	private static final int PORT = 9123;
	
	private static final long CONN_TIMEOUT = 10*1000L;//10 seconds
	
	private static ClientHandler handler = new ClientHandler();
	
	private static Object fire = new Object();
	
	private NioSocketConnector connector;
	
	public ClientMina() {
		connector = new NioSocketConnector();
		
		connector.getFilterChain().addLast("logging", new LoggingFilter());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName("UTF-8") ) ));
		
		connector.setConnectTimeoutMillis(CONN_TIMEOUT);	
		
		connector.setHandler(handler);
	}
	
	@PreDestroy
	public void dispose() {
		connector.dispose();
	}
	
	private IoSession connect() {
		ConnectFuture future = connector.connect(new InetSocketAddress(HOST, PORT));
		future.awaitUninterruptibly();// Wait until the connection attempt is finished
		
		return future.getSession();
	}
	
	public String sendMessage(String message) {
		IoSession session = connect();
		session.write(message);
		
		//session.getConfig().setUseReadOperation(true);
		//ReadFuture future = session.read();
		//future.awaitUninterruptibly();
		//return (String) future.getMessage();
		
		synchronized(fire) {
			try {
				fire.wait(3000L);// waiting for response max 3 s
			} catch (InterruptedException e) {
				logger.error("InterruptedException in sendMessage()\n", e);
				Thread.currentThread().interrupt();
			}
		}
		
		String response = handler.getIncome();
		System.out.println(response);
		return response;
	}
	
	private static class ClientHandler extends IoHandlerAdapter {
		private String income;// response from server

		public String getIncome() {
			return income;
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			synchronized(fire) {
				income = (String) message;
				fire.notifyAll();
			}
		}
		
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			logger.error("Exception caught in ClientHandler\n", cause);
		}
	}
}
