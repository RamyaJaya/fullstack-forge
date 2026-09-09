package com.babycharting.exception;

import java.util.HashMap;
import java.util.Map;

import com.babycharting.service.BabyNotFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return badRequestWithFieldErrors(errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		if (exception.getCause() instanceof InvalidFormatException invalidFormatException) {
			return badRequestWithFieldErrors(Map.of(
					resolveFieldName(invalidFormatException),
					"Invalid value"));
		}

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				"Malformed JSON request body");
		problemDetail.setTitle("Bad Request");
		return problemDetail;
	}

	@ExceptionHandler(BadRequestException.class)
	public ProblemDetail handleBadRequestException(BadRequestException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				exception.getMessage());
		problemDetail.setTitle("Bad Request");
		return problemDetail;
	}

	@ExceptionHandler(BabyNotFoundException.class)
	public ProblemDetail handleBabyNotFoundException(BabyNotFoundException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.NOT_FOUND,
				exception.getMessage());
		problemDetail.setTitle("Not Found");
		return problemDetail;
	}

	private ProblemDetail badRequestWithFieldErrors(Map<String, String> errors) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				"Validation failed");
		problemDetail.setTitle("Bad Request");
		problemDetail.setProperty("errors", errors);
		return problemDetail;
	}

	private String resolveFieldName(InvalidFormatException exception) {
		if (exception.getPath().isEmpty()) {
			return "request";
		}
		String propertyName = exception.getPath().get(exception.getPath().size() - 1).getPropertyName();
		return propertyName != null ? propertyName : "request";
	}

}
