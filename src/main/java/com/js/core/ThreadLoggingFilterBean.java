package com.js.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Order(10)
@Component
public class ThreadLoggingFilterBean extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            boolean logEverythingForThisRequest = "true".equalsIgnoreCase(request.getParameter("trace"));
            ThreadLoggingSupport.logEverything(logEverythingForThisRequest);
            
            boolean logDebugForThisRequest = "true".equalsIgnoreCase(request.getParameter("debug"));
            ThreadLoggingSupport.logDebug(logDebugForThisRequest);
            
            chain.doFilter(request, response);
        } finally {
            ThreadLoggingSupport.cleanup();
        }
    }
}