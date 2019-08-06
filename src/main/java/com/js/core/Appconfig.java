package com.js.core;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;

//@EnableRetry
@Configuration
public class Appconfig {
	@Bean
	public Docket petApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class).genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(newRule(
						typeResolver.resolve(DeferredResult.class,
								typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET,
						newArrayList(new ResponseMessageBuilder().code(500).message("500 message")
								.responseModel(new ModelRef("Error")).build()))
				.securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext()));
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
	    return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("IST"));
	}

	@Autowired
	private TypeResolver typeResolver;

	private ApiKey apiKey() {
		return new ApiKey("mykey", "api_key", "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/anyPath.*"))
				.build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("mykey", authorizationScopes));
	}

	@Bean
	SecurityConfiguration security() {
		return new SecurityConfiguration("test-app-client-id", "test-app-realm", "test-app", "apiKey");
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl");
	}

	// @Bean
	// public EmbeddedServletContainerFactory jettyConfigBean() {
	// JettyEmbeddedServletContainerFactory jef = new
	// JettyEmbeddedServletContainerFactory();
	// jef.addServerCustomizers(new JettyServerCustomizer() {
	//
	// @Override
	// public void customize(Server server) {
	// HandlerCollection handlers = new HandlerCollection();
	// for (Handler handler : server.getHandlers()) {
	// handlers.addHandler(handler);
	// }
	//// RequestLogHandler reqLogs = new RequestLogHandler();
	//// NCSARequestLog reqLogImpl = new
	// NCSARequestLog("/projects/logs/access2-yyyy_mm_dd.log");
	//// reqLogImpl.setRetainDays(30);
	//// reqLogImpl.setAppend(true);
	//// reqLogImpl.setExtended(false);
	//// reqLogImpl.setLogTimeZone("GMT");
	//// reqLogImpl.setLogServer(true);
	//// reqLogImpl.setLogLatency(true);
	//// reqLogs.setRequestLog(reqLogImpl);
	//// handlers.addHandler(reqLogs);
	//// server.setHandler(handlers);
	//
	// // For Jetty 9.3+, use the following
	// RequestLogHandler reqLogs = new RequestLogHandler();
	// reqLogs.setServer(server);
	// RequestLogImpl rli = new RequestLogImpl();
	// rli.setResource("calsspath:logback-access.xml");
	// rli.setQuiet(false);
	// rli.start();
	// reqLogs.setRequestLog(rli);
	//
	// handlers.addHandler(reqLogs);
	// server.setHandler(handlers);
	// }
	// });
	// return jef;
	// }

	// @Bean
	// public JettyEmbeddedServletContainerFactory
	// jettyEmbeddedServletContainerFactory(
	// @Value("${server.port:8080}") final String mainPort,
	// @Value("${server.nonsecured.port}") final String nonsecuredPort) {
	//
	// final JettyEmbeddedServletContainerFactory factory = new
	// JettyEmbeddedServletContainerFactory(
	// Integer.valueOf(mainPort));
	//
	// // Add customized Jetty configuration with non blocking connection
	// // handler
	// factory.addServerCustomizers(new JettyServerCustomizer() {
	// @Override
	// public void customize(final Server server) {
	// final NetworkTrafficServerConnector connector = new
	// NetworkTrafficServerConnector(server);
	// connector.setPort(Integer.valueOf(nonsecuredPort));
	// server.addConnector(connector);
	// }
	// });
	// return factory;
	// }

	// @Bean
	// public RetryTemplate retryTemplate() {
	// RetryTemplate retryTemplate = new RetryTemplate();
	//
	// FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
	// fixedBackOffPolicy.setBackOffPeriod(2000l);
	// retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
	//
	// SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
	// retryPolicy.setMaxAttempts(20);
	//
	// retryTemplate.setRetryPolicy(retryPolicy);
	//
	// return retryTemplate;
	// }
	
	@Bean
	public KieContainer kieContainer() {
		return KieServices.Factory.get().newKieClasspathContainer();
	}
}
