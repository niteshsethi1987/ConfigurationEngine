package com.js.profile.services;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.js.ruleengine.JsRuleEngine;

@ContextConfiguration
@SpringBootTest(classes = JsRuleEngine.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApplicationFeatureTest {

}
