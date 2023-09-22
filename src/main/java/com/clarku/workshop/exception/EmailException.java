package com.clarku.workshop.exception;

import org.springframework.http.HttpStatus;

public class EmailException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmailException(String exp) {
		super(exp, null, false, false);
	}
	
	public EmailException(String message, HttpStatus unauthorized) {
		super(message, null, false, false);
	}
}
