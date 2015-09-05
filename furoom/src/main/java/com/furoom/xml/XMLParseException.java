package com.furoom.xml;

import com.furoom.exception.SuperException;

public class XMLParseException extends SuperException {

	public XMLParseException() {
	}

	public XMLParseException(String message) {
		super(message);
	}

	public XMLParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
