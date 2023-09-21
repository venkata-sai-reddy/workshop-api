package com.clarku.workshop.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends Exception {

	private static final long serialVersionUID = 1L;

	public GlobalException(String exp) {
		super(exp, null, false, false);
	}
	
	public GlobalException(String message, HttpStatus unauthorized) {
		super(message, null, false, false);
	}
}
