package com.mobiquityinc.packer.validation;

public class ValidationError {

	private final String message;

	public ValidationError(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
