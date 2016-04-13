package org.partizanux.mXchanger.server.parser;

public interface MessageParser <Command>{
	Command parseToCommand(Object message);
	Object getData(Object message);
}
