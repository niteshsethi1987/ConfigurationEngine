package com.js.profile.services;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author ashok
 *
 */
@SpringBootApplication
@EnableSwagger2
public class Application {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.setDefaultProperties(Collections.singletonMap("spring.profiles.active", "dev"));
		application.run(args);
	}
}