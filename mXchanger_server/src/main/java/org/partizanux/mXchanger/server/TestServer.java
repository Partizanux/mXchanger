package org.partizanux.mXchanger.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

// For tests only

public class TestServer {
	
	private static final Logger logger = LoggerFactory.getLogger(TestServer.class);
	
	private static ApplicationContext ctx;
	
	private static final int PORT = 9123;
	private IoAcceptor acceptor;

	@Autowired
	public TestServer(IoHandler handler) throws IOException {
		acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        // NOTE - TextLineCodecFactory does exactly what it's title implies.
        // It means - Processing a MESSAGE_RECEIVED for session 1 - gives message for IoHandler
        // only after it finds end line symbols: "\r\n"
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));

        acceptor.setHandler(handler);

        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress(PORT) );
	}
	
	@PreDestroy
	public void unbind(){
		acceptor.dispose();
	}
	
	public static void reinitializeDB() {
		String script = "hsqldb_init.sql";
		
		try {
			
			DataSource dataSource = (DataSource) ctx.getBean(DriverManagerDataSource.class);
			
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.addScript(new ClassPathResource(script));
		    populator.setContinueOnError(true);
			DatabasePopulatorUtils.execute(populator , dataSource);
			
		} catch (Exception e) {
			logger.error("error while reinitializeDB\n", e);
		}
		
	}
	
	public static Clock getClock() {
		Clock clock = null;
		try {
			clock = (Clock) ctx.getBean("clock");
			return clock;
		} catch (Exception e) {
			logger.error("error while obtaining clock from application context\n", e);
			return null;
		}
	}
	
	public static void main(String[] args) {
		try {
			// test-service-context.xml - conf with fixed clock
			ctx = new ClassPathXmlApplicationContext("classpath:test-service-context.xml", "classpath:dao-context.xml",
					"classpath:testServer-context.xml");
			
			//Shutting down the Spring IoC container gracefully in non-web applications
			((AbstractApplicationContext) ctx).registerShutdownHook();
		} catch (BeansException e) {
			logger.error("\nCan't start TestServer", e);
			throw new RuntimeException (e);
		}
	}

}
