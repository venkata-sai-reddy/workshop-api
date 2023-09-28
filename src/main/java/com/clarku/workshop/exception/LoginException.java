package com.clarku.workshop.exception;

import org.springframework.http.HttpStatus;

public class LoginException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoginException(String exp) {
		super(exp, null, false, false);
	}
	
	public LoginException(String message, HttpStatus unauthorized) {
		super(message, null, false, false);
	}
}
