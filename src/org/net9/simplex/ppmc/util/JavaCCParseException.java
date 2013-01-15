package org.net9.simplex.ppmc.util;

import java.text.ParseException;

public class JavaCCParseException extends ParseException {
	private static final long serialVersionUID = 1L;
	Throwable exception;
	
	public JavaCCParseException(Throwable e) {
		super(e.getMessage(), 0);
		exception = e;
	}
	
    public String getMessage() {
    	return exception.getMessage();
    } 
}
