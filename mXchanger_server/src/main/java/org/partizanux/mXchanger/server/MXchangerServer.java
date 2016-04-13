package org.partizanux.mXchanger.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import javax.annotation.PreDestroy;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MXchangerServer {
	
	private static final Logger logger = LoggerFactory.getLogger(MXchangerServer.class);
	
	private static final int PORT = 9123;
	private IoAcceptor acceptor;

	@Autowired
	public MXchangerServer(IoHandler handler) throws IOException {
		acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        // NOTE - TextLineCodecFactory does exactly what it's title implies.
        // It means - Processing a MESSAGE_RECEIVED for session 1 - gives message for IoHandler
        // only after it finds end line symbols: "\r\n"
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));

        acceptor.setHandler(handler);

        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress(PORT) );//maybe separate this part in init-method ???
	}
	
	@PreDestroy
	public void unbind(){
		acceptor.dispose();
	}
	
	public static void main(String[] args) {
		try {
			@SuppressWarnings("resource")
			ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:service-context.xml", "classpath:dao-context.xml",
					"classpath:server-context.xml");
			
			//Shutting down the Spring IoC container gracefully in non-web applications
			((AbstractApplicationContext) ctx).registerShutdownHook();
		} catch (BeansException e) {
			logger.error("\nCan't start MXchangerServer", e);
			throw new RuntimeException (e);
		}
	}

}
