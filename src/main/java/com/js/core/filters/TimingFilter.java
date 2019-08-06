/**
 *
 */
package com.js.core.filters;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Filter to generate request id if not present
 * @author goutam.mandal
 *
 */
@Slf4j
@Order(30)
@Component
@WebFilter(urlPatterns = "/jsprofile/*", description = "Set request id in MDC")
public class TimingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		String timing = request.getParameter("timing");		
		MDC.put("timing", timing);
		try {
			chain.doFilter(request, response);
		}
		finally {
			MDC.remove("timing");
		}
	}

	@Override
	public void destroy() {
		// Nothing to do
	}

}
