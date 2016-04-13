package org.partizanux.mXchanger.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.partizanux.mXchanger.server.command.Command;
import org.partizanux.mXchanger.server.parser.MessageParser;
import org.partizanux.mXchanger.service.MXchangerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MinaHandler extends IoHandlerAdapter {	
	private final Logger logger = LoggerFactory.getLogger(MinaHandler.class);
	@Autowired
	private MXchangerService<?> service;
	@Autowired
	private MessageParser<Command<?,?>> parser;
	
	@Override
	@SuppressWarnings("unchecked")
    public void messageReceived(IoSession session, Object message) throws Exception {
		Command<Object,IoSession> command = (Command<Object, IoSession>) parser.parseToCommand(message);
		Object data = parser.getData(message);
		command.execute((MXchangerService<Object>)service, session, data);
    }
	
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        logger.error("In IoHandler exceptionCaught:\n", cause);
    }
    
    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        logger.info( "IDLE " + session.getIdleCount( status ));
    }
    
}
