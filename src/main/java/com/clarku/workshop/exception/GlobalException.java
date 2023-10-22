package com.clarku.workshop.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends Exception {

	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;
	
	public GlobalException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
	public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
