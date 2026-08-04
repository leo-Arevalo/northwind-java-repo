package com.la.northwind_java.config.exceptions;

public class DatabaseException extends RuntimeException {

	public DatabaseException(String message) {
		super(message);
	}
	
	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
