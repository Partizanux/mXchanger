package org.partizanux.mXchanger.server.parser;

import org.partizanux.mXchanger.server.command.Command;
import org.partizanux.mXchanger.server.command.GetAllDealerMoney;
import org.partizanux.mXchanger.server.command.GetMoneyAndCurrency;
import org.partizanux.mXchanger.server.command.NoCommand;
import org.partizanux.mXchanger.server.command.OfferAndDoExchange;
import org.springframework.stereotype.Component;

@Component
public class CSVMessageParser implements MessageParser<Command<String,?>>{
	private static final String REGEX = "([A-Za-z0-9-_]+),";
	
	public Command<String,?> parseToCommand(Object message) {
		String[] arr = message.toString().split(",");
		String method = arr[0];
				
		if (method.equals("getCurrency"))
			return new GetMoneyAndCurrency();
		if (method.equals("offer"))
			return new OfferAndDoExchange();
		if (method.equals("getAllDealerMoney"))
			return new GetAllDealerMoney();
		
		return new NoCommand();
	}

	public Object getData(Object message) {
		return message.toString().replaceFirst(REGEX, "");
	}

}
