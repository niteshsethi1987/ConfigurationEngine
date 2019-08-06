package com.js.ruleengine.services;

import javax.annotation.PostConstruct;

import com.js.ruleengine.nodes.profile.*;
import lombok.Getter;
import orchestrator.OrchestratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.ruleengine.nodes.contact.ContactDataExtractor;
import com.js.ruleengine.nodes.membership.MembershipDataExtractor;
import com.js.ruleengine.nodes.membership.SelfMembershipDataExtractor;

/**
 * @author lavesh
 */
@Service
public class ProfileService {

	@Getter
	protected OrchestratorService profileServiceV1;

	@Autowired
	private ProfileQueryTermEnhancer profileQueryTermEnhancer;

	@Autowired
	private UsernameAndChecksumConverter usernameAndChecksumConverter;

	@Autowired
	private SelfViewChecker selfViewChecker;

	@Autowired
	private SelfProfileInfoExtractor selfInfoExtractor;

	@Autowired
	private ProfileDataExtractor profileDataExtractor;

	@Autowired
	private ProfileRuleEngineExecutor profileRuleEngineExecutor;

	@Autowired
	private ProfileRuleEngineObjectVerifier profileRuleEngineObjectVerifier;

	@Autowired
	private ProfileResponseRefiner profileResponseRefiner;

	@Autowired
	private ProfileResponseCreator profileResponseCreator;

	@Autowired
	private SelfMembershipDataExtractor selfMembershipDataExtractor;

	@Autowired
	private MembershipDataExtractor membershipDataExtractor;

	@Autowired
	private ContactButtonResponseCreator contactButtonResponseCreator;

	@Autowired
	private ContactDataExtractor contactDataExtractor;

	@Autowired
	private ProfilePresenceDataExtractor profilePresenceDataExtractor;

	@Autowired
	private PdResponseCollator pdResponseCollator;

	@Autowired
	private PresenceResponse presenceResponse;

	@PostConstruct
	public void init() {
		loadProfileServiceV1();
	}

	private void loadProfileServiceV1() {
		profileServiceV1 = new OrchestratorService();
		profileServiceV1.addExecutorNode(profileQueryTermEnhancer);
		profileServiceV1.addExecutorNode(usernameAndChecksumConverter);
		profileServiceV1.addExecutorNode(selfViewChecker);
		profileServiceV1.addExecutorNode(profileRuleEngineObjectVerifier, profileDataExtractor, membershipDataExtractor, contactDataExtractor,
				selfMembershipDataExtractor, selfInfoExtractor, profilePresenceDataExtractor);
		profileServiceV1.addExecutorNode(presenceResponse);

		profileServiceV1.addExecutorNode(profileRuleEngineExecutor);
		profileServiceV1.addExecutorNode(profileResponseRefiner);
		profileServiceV1.addExecutorNode(pdResponseCollator);
		profileServiceV1.addExecutorNode(contactButtonResponseCreator);
		profileServiceV1.addExecutorNode(profileResponseCreator);
	}
}
