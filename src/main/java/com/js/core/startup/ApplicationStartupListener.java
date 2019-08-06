/**
 *
 */
package com.js.core.startup;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is used for performing any activities at startup and cleanup
 * operations before container shutdown.
 * 
 * @author goutam.mandal
 *
 */
@Slf4j
@Component
public class ApplicationStartupListener {

	/**
	 * Before dispatcherServlet and before jetty is up Ideal for performing
	 * pre-startup activities
	 */
	@EventListener(ApplicationPreparedEvent.class)
	public void onApplicationPreparedEvent() {
		log.info("ApplicationPreparedEvent -- START");
		// fieldMapLoader.loadCasteHierarchyData();
		log.info("ApplicationPreparedEvent -- END");
	}

	/**
	 * Called just after ApplicationPreparedEvent AND once at context
	 * initialization Not good for performing startup actions as it is called
	 * twice Should be used when activities need to be performed at all
	 * ApplicationContext refresh Default value for @EventListener
	 * (ThreadLoggingInitializer)
	 */
	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshedEvent() {
		log.info("ContextRefreshedEvent -- START");

		log.info("ContextRefreshedEvent -- END");
	}

	// not working
	@EventListener(ContextStartedEvent.class)
	public void onContextStartedEvent() {
		log.info("ContextStartedEvent -- START");

		log.info("ContextStartedEvent -- END");
	}

	// not tested
	@EventListener(ContextStoppedEvent.class)
	public void onContextStoppedEvent() {
		log.info("ContextStoppedEvent -- START");

		log.info("ContextStoppedEvent -- END");
	}

	/**
	 * Called after ApplicationFailedEvent when application fails to start
	 */
	@EventListener(ContextClosedEvent.class)
	public void onContextClosedEvent() {
		log.info("ContextClosedEvent -- START");

		log.info("ContextClosedEvent -- END");
	}

	// not working
	@EventListener(ApplicationEnvironmentPreparedEvent.class)
	public void onApplicationEnvironmentPreparedEvent() {
		log.info("ApplicationEnvironmentPreparedEvent -- START");

		log.info("ApplicationEnvironmentPreparedEvent -- END");
	}

	/**
	 * Called after stopping jetty service connector and destroying Spring
	 * dispatcherServlet
	 */
	@EventListener(ApplicationFailedEvent.class)
	public void onApplicationFailedEvent() {
		log.info("ApplicationFailedEvent -- START");

		log.info("ApplicationFailedEvent -- END");
	}

	/**
	 * Called after jetty listener is up
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReadyEvent() {
		log.info("ApplicationReadyEvent -- START");

		log.info("ApplicationReadyEvent -- END");
	}

	// not working
	@EventListener(ApplicationStartingEvent.class)
	public void onApplicationStartingEvent() {
		log.info("ApplicationStartingEvent -- START");

		log.info("ApplicationStartingEvent -- END");
	}

	// not working
	@EventListener(ApplicationStartedEvent.class)
	public void onApplicationStartedEvent() {
		log.info("ApplicationStartedEvent -- START");

		log.info("ApplicationStartedEvent -- END");
	}
}
