package com.js.ruleengine.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.ruleengine.nodes.membership.plan.MembershipPlanBenefitVerifier;
import com.js.ruleengine.nodes.membership.plan.MembershipPlanQueryTermEnhancer;
import com.js.ruleengine.nodes.membership.plan.MembershipPlanQueryTermValidator;
import com.js.ruleengine.nodes.membership.plan.MembershipResultCollator;
import com.js.ruleengine.nodes.membership.plan.PlanBenefitExtractor;
import com.js.ruleengine.nodes.membership.plan.ProfileDiscountExtractor;
import com.js.ruleengine.nodes.membership.plan.ProfileMTongueExtractor;
import com.js.ruleengine.nodes.membership.plan.ProfileSelectedCartExtractor;

import lombok.Getter;
import orchestrator.OrchestratorService;

@Service
public class MembershipPlanService {

	@Getter
	protected OrchestratorService planOrcestratorServiceV1;

	@Autowired
	private MembershipPlanQueryTermEnhancer planQueryTermEnhancer;

	@Autowired
	private MembershipPlanQueryTermValidator planQueryTermValidator;

	@Autowired
	private MembershipPlanBenefitVerifier membershipPlanBenefitVerifier;

	@Autowired
	private PlanBenefitExtractor planBenefitExtractor;

	@Autowired
	private ProfileMTongueExtractor profileMTongueExtractor;

	@Autowired
	private ProfileDiscountExtractor profileDiscountExtractor;
	
	@Autowired
	private ProfileSelectedCartExtractor profileSelectedCartExtractor;

	@Autowired
	private MembershipResultCollator resultCollator;

	@PostConstruct
	public void init() {
		loadPlanOrcestratorServiceV1();
	}

	private void loadPlanOrcestratorServiceV1() {
		planOrcestratorServiceV1 = new OrchestratorService();

		planOrcestratorServiceV1.addExecutorNode(planQueryTermEnhancer);
		planOrcestratorServiceV1.addExecutorNode(planQueryTermValidator);
		planOrcestratorServiceV1.addExecutorNode(membershipPlanBenefitVerifier, planBenefitExtractor,
				profileMTongueExtractor, profileDiscountExtractor,profileSelectedCartExtractor);
		planOrcestratorServiceV1.addExecutorNode(resultCollator);
	}

}
