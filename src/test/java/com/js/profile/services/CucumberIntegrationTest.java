package com.js.profile.services;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "json:build/cucumber/cucumber-report.json",
"html:build/cucumber/html" },glue = { "bdd.steps" }, features = { "classpath:bdd/features" })
public class CucumberIntegrationTest {
}