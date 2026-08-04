




package com.la.northwind_java.config.exceptions;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


import jakarta.persistence.EntityNotFoundException;




@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	//Validaciones de exceptiones para entidades no encontradas
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handlerResourceNotFoundException(ResourceNotFoundException ex){
		logger.warn("Resource not found: {}", ex.getMessage());
		Map<String, String> response = new HashMap<>();
		response.put("error", "Resource not found");
		response.put("message", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	//Manejo de validaciones fallidas en @Valid (Errores en request body)
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex){
		logger.warn("Validation failed: {}", ex.getBindingResult().toString());
	
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach( error -> {
			if(error instanceof FieldError fieldError) {
				errors.put(fieldError.getField(), fieldError.getDefaultMessage());
			}else {
				errors.put(error.getObjectName(), error.getDefaultMessage());
			}
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Map<String, String>> handleInvalidRequestException(InvalidRequestException ex){
		logger.warn("Invalid request: {}", ex.getMessage());
		Map<String, String> response = new HashMap<>();
		response.put("error", "Invalid request");
		response.put("message", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	
	
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<Map<String, String>> handleDatabaseException(DatabaseException ex){
		logger.error("Database error: {}", ex.getMessage());
		Map<String, String> response = new HashMap<>();
		response.put("error", "Database");
		response.put("message", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex){
		logger.warn("Access denied: {}", ex.getMessage());
		Map<String, String> response = new HashMap<>();
		response.put("error", "Access denied");
		response.put("message", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}
	
	
	
	//Manejo general de exceptiones inesperadas
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex){
		logger.error("Unexpected error: {}",ex.getMessage(), ex);
		Map<String, String> response = new HashMap<>();
		response.put("error", "Internal server error");
		response.put("message", "An unexpected error occurred");
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}








