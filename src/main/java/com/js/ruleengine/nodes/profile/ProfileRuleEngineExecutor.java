package com.js.ruleengine.nodes.profile;

import java.util.HashMap;
import java.util.Map;

import com.js.ruleengine.domains.*;
import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.HttpChannel;
import com.js.ruleengine.constants.IODataConstants;

/**
 * @author lavesh
 */
@Slf4j
@Service
public class ProfileRuleEngineExecutor extends WorkNode {
	@Autowired
	protected HttpChannel httpChannel;

	private final KieContainer kieContainer;

	@Autowired
	public ProfileRuleEngineExecutor(KieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {

		Map<Integer, JsPogProfile> pogProfileMap = (Map<Integer, JsPogProfile>) context.getIOData("pogProfileMap");

		JsPgProfile pgProfile = (JsPgProfile) context.getIOData("pgProfile");
		JsPgMembership pgMembership = (JsPgMembership) context.getIOData("pgMembership");
		Map<Integer, JsContact> contactData = (Map<Integer, JsContact>) context.getIOData("contactData");
		Map<Integer, JsAuth> authData = (Map<Integer, JsAuth>) context.getIOData(IODataConstants.PROFILE_LAST_LOGIN_DATA);
		Map<Integer, JsPresence> presenceData = (Map<Integer, JsPresence>) context.getIOData(IODataConstants.PROFILE_PRESENCE_DATA);

		pogProfileMap.forEach((profileId, pogProfile) -> {
			KieSession kieSession = kieContainer.newKieSession("profile-view-session");
			if (log.isDebugEnabled()) {
				kieSession.addEventListener(new DefaultAgendaEventListener() {

					private Map<String, Long> beforeMatchFiredTimestamp = new HashMap<>();

					@Override
					public void beforeMatchFired(BeforeMatchFiredEvent event) {
						String ruleName = event.getMatch().getRule().getName();
						beforeMatchFiredTimestamp.put(ruleName, System.currentTimeMillis());
						log.debug("Rule fired before: " + ruleName + " ,Facts handles: " + event.getMatch().getFactHandles());
					}

					@Override
					public void afterMatchFired(AfterMatchFiredEvent event) {
						String ruleName = event.getMatch().getRule().getName();
						log.debug("Rule fired after: " + ruleName + " ,Facts handles: " + event.getMatch().getFactHandles());// +"Meta
						context.setDebugData("Rule fired: " + ruleName + ":" + (System.currentTimeMillis() - beforeMatchFiredTimestamp.get(ruleName)) + " ms");
					}
				});
			}

			if (pgProfile != null) {
				kieSession.insert(pgProfile);
			}
			if (pgMembership != null) {
				kieSession.insert(pgMembership);
			}
			if (contactData != null) {
				kieSession.insert(contactData.get(profileId));
			}
			if (authData != null) {
				kieSession.insert(authData.get(profileId));
			}
			if (presenceData != null) {
				kieSession.insert(presenceData.get(profileId));
			}
			kieSession.insert(pogProfile);
			long sTime = System.currentTimeMillis();
			kieSession.fireAllRules();
			context.setDebugData("ProfileRuleEngineExecutor fireAllRules = " + (System.currentTimeMillis() - sTime));
			kieSession.dispose();
		});

		return true;
	}
}
