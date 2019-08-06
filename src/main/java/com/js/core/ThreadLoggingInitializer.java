package com.js.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ThreadLoggingInitializer {

    private static Log log  = LogFactory.getLog(ThreadLoggingInitializer.class);

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        LoggerContext loggerContext = ((Logger) LoggerFactory.getLogger("")).getLoggerContext();
        loggerContext.addTurboFilter(new TurboFilter() {
            @Override
            public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
            	if (ThreadLoggingSupport.shouldLogEverything()
            			|| (ThreadLoggingSupport.shouldLogDebug() && level.isGreaterOrEqual(Level.DEBUG))) {
            		return FilterReply.ACCEPT;
            	}
            	return FilterReply.NEUTRAL;
            }
        });
        log.info("ThreadLogging support initialized");
    }
}
