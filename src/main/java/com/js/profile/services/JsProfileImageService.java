package com.js.profile.services;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsProfileImageService {

	private final KieContainer kieContainer;

	@Autowired
	public JsProfileImageService(KieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

	public JsProfile getProductDiscount(JsProfile product, JsImage image) {
		KieSession kieSession = kieContainer.newKieSession("prulesSession");
		kieSession.insert(product);
		kieSession.insert(image);
		kieSession.fireAllRules();
		kieSession.dispose();
		return product;
	}
}