package org.partizanux.mXchanger.server.command;

import org.apache.mina.core.session.IoSession;
import org.partizanux.mXchanger.service.MXchangerService;

public class NoCommand  implements Command<String, IoSession>{

	@Override
	public void execute(MXchangerService<String> service, IoSession session, String data) {
		//do nothing
	}

}
