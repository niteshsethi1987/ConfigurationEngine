package com.js.ruleengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**	
 * @author ashok
 *
 */
@SpringBootApplication
@EnableSwagger2
@ComponentScan({ "com.js.*" })
public class JsRuleEngine {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(JsRuleEngine.class);
		application.run(args);
	}
}