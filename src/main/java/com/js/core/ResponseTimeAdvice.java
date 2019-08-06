/**
 *
 */
package com.js.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import orchestrator.ServiceResponse;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * This advice intercepts response from controllers returning
 * {@link ServiceResponse} and adds the total processing time (in ms) taken for
 * delivering the response. The filter is used to mark the start of processing.
 * 
 * @author goutam.mandal
 *
 */
@Order(20)
@RestControllerAdvice("com.js.ruleengine")
public class ResponseTimeAdvice extends OncePerRequestFilter implements ResponseBodyAdvice<ServiceResponse> {

	private ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public ServiceResponse beforeBodyWrite(ServiceResponse body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		int elapsedTime = -1;
		if (startTime.get() != null) {
			elapsedTime = (int) (System.currentTimeMillis() - startTime.get());
		}
		body.getHeader().setResponseTime(elapsedTime);
		return body;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (startTime.get() == null) {
			startTime.set(System.currentTimeMillis());
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			startTime.remove();
		}
	}
}
