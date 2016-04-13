package org.partizanux.mXchanger.server.listeners;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.partizanux.mXchanger.service.MXchangerService;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeListener implements IoFutureListener<ReadFuture>{

	private static final Logger logger = LoggerFactory.getLogger(ExchangeListener.class);
	private MXchangerService<String> service;
	private IoSession session;
	private String order;
	
	public ExchangeListener(MXchangerService<String> service, IoSession session, String order) {
		this.service = service;
		this.session = session;
		this.order = order;
	}
	
	//Invoked when the operation associated with the IoFuture has been completed 
	//even if you add the listener after the completion - !!!
	@Override
	public void operationComplete(ReadFuture future) {
		try {
			String confirm = null;
			String message = (String)future.getMessage();
			
			String[] arr = message.toString().split(",");
			String method = arr[0];
			if (method.equals("offer"))
				return;
				
			confirm = message;

			if(service.confirmEx(confirm)) {
				String answer = service.doExchange(order);
				session.write(answer);
				}
		} catch (ParseDataException e) {
			//swallow, log and do nothing
			logger.error("ParseDataException in ExchangeListener \n", e);
			session.close(false);// - ???
		} catch (NullPointerException npe) {
			//swallow, log and do nothing
			logger.error("NullPointerException in ExchangeListener \n", npe);
			// so do nothing, that means service doesn't do exchange
			session.close(false);
		}
		
		session.close(false);
	}

}
