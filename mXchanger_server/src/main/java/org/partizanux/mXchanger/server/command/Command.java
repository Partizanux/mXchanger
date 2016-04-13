package org.partizanux.mXchanger.server.command;

import org.partizanux.mXchanger.service.MXchangerService;

public interface Command <T,R>{
	void execute(MXchangerService<T> service, R session, T data);
}
