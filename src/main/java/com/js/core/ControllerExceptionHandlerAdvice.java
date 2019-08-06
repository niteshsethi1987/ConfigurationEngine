/**
 *
 */
package com.js.core;

import orchestrator.ServiceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Advice to capture exception raised from controllers
 * and wrap them around application's {@link ServiceResponse}
 * @author goutam.mandal
 *
 */
@RestControllerAdvice({"com.js.ruleengine"})
public class ControllerExceptionHandlerAdvice {

	private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandlerAdvice.class);

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ServiceResponse handleInvalidParams (MissingServletRequestParameterException ex) {
		ServiceResponse response = new ServiceResponse();
		response.setResponseBodyHeader(400, String.format("Missing query/header param [%s]", ex.getParameterName()));
		return response;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ServiceResponse handleInvalidParams (MethodArgumentTypeMismatchException ex) {
		ServiceResponse response = new ServiceResponse();
		String message = null;
		if (ex.getCause() instanceof NumberFormatException) {
			message = String.format("Excepted number(s) for query/header param [%s]. Received [%s]", ex.getName(), ex.getValue());
		}
		else {
			message = String.format("Expected %s for query/header param [%s]. Received [%s]", ex.getRequiredType().getSimpleName(), ex.getName(), ex.getValue());
		}
		response.setResponseBodyHeader(400, message);
		return response;
	}

	@ExceptionHandler(Exception.class)
	public ServiceResponse handleAllExceptions (Exception ex) {
		ServiceResponse response = new ServiceResponse();
		response.setResponseBodyHeader(503, String.format("Something Went Wrong! Request Id : [%s]", MDC.get("requestId")));
		LOG.error("Could not process request", ex);
		return response;
	}
}
