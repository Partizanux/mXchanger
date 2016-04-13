package org.partizanux.mXchanger.service.exception;

public class ParseDataException extends Exception{

	private static final long serialVersionUID = 3133237089878754984L;
	
	public ParseDataException() {
		
	}
	
	public ParseDataException(Throwable cause) {
		super(cause);
	}
	
	public ParseDataException(String message) {
		super(message);
	}
	
	public ParseDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
