package org.partizanux.mXchanger.server.command;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.partizanux.mXchanger.server.listeners.ExchangeListener;
import org.partizanux.mXchanger.service.MXchangerService;
import org.partizanux.mXchanger.service.exception.ParseDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfferAndDoExchange implements Command<String, IoSession> {

	private static final Logger logger = LoggerFactory.getLogger(GetMoneyAndCurrency.class);

	public void execute(MXchangerService<String> service, IoSession session, String offer) {
		try {
			String order = service.requestEx(offer);
			session.write(order);

			session.getConfig().setUseReadOperation(true);

			session.read();// returns offer from queue
			ReadFuture future = session.read();// returns confirm from queue, thats we need
			
			// delegates to listener further logic
			future.addListener(new ExchangeListener(service, session, order));

		} catch (ParseDataException e) {
			// swallow, log and do nothing
			logger.error("ParseDataException in GetMoneyAndCurrency \n", e);
			session.close(false);
		}

	}

}
