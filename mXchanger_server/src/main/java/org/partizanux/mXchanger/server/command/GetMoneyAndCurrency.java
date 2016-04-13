package org.partizanux.mXchanger.server.command;

import org.apache.mina.core.session.IoSession;
import org.partizanux.mXchanger.service.MXchangerService;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMoneyAndCurrency implements Command<String, IoSession>{
	private static final Logger logger = LoggerFactory.getLogger(GetMoneyAndCurrency.class);
	
	@Override
	public void execute(MXchangerService<String> service, IoSession session, String data) {
		String answer;
		try {
			answer = service.getDealerMoneyPair(data);
			session.write(answer);
		} catch (ParseDataException e) {
			//swallow, log and do nothing
			logger.error("ParseDataException in GetMoneyAndCurrency \n" + e);
			session.close(false);
		}

		//true to close this session immediately. The pending write requests will simply be discarded. 
		//false to close this session after all queued write requests are flushed.
		session.close(false);
	}

}
