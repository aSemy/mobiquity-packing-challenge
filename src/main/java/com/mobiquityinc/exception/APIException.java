package com.mobiquityinc.exception;

public class APIException extends IllegalStateException {

	private static final long serialVersionUID = 5199629890017767754L;

	public APIException() {
		super();
	}

	public APIException(String message, Throwable cause) {
		super(message, cause);
	}

	public APIException(String message) {
		super(message);
	}

	public APIException(Throwable cause) {
		super(cause);
	}
}
