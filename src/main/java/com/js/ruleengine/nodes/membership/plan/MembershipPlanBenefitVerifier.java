package com.js.ruleengine.nodes.membership.plan;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.JoinWorkNode;
import orchestrator.ResponseBodyHeader;

@Slf4j
@Service
public class MembershipPlanBenefitVerifier extends JoinWorkNode {

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		log.debug("====== executing MembershipPlanBenefitExtractor =====");
		Boolean isLoggedIn = (Boolean) context.getIOData(IODataConstants.IS_LOGGED_IN);
		log.debug("is user logged in {}", isLoggedIn.toString());
		if (isLoggedIn == false && isResponseFromMembershipPlanApiValid(context)) {
			return true;
		}
		if (isLoggedIn == true && isResponseFromMembershipPlanApiValid(context)
				&& isResponseFromMembershipDiscountApiValid(context) && isMotherToungeValid(context)
				&& isResponseFromProfileSelectedCartApiValid(context)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean isResponseFromMembershipPlanApiValid(IOrchestratorContext context) {

		Map<String, Object> membershipPlanBenefit = (Map<String, Object>) context
				.getIOData(IODataConstants.MEMBERSHIP_PLAN_BENEFIT);

		if (null == membershipPlanBenefit || null == membershipPlanBenefit.get("header")) {
			log.error("error extractingdata from planbenefit api null response from api");
			return false;
		}

		Map<String, Object> headerFromMembership = (Map<String, Object>) membershipPlanBenefit.get("header");
		if ((Integer) (headerFromMembership.get("status")) != HttpStatus.OK.value()) {
			log.error("error extractingdata from planbenefit api {}", headerFromMembership.get("errorMsg"));
			context.setResponseBodyHeader(new ResponseBodyHeader((Integer) (headerFromMembership.get("status")),
					(String) headerFromMembership.get("errorMsg")));
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean isResponseFromMembershipDiscountApiValid(IOrchestratorContext context) {

		Map<String, Object> membershipProfileDiscount = (Map<String, Object>) context
				.getIOData(IODataConstants.MEMBERSHIP_PROFILE_DISCOUNT);

		if (null == membershipProfileDiscount || null == membershipProfileDiscount.get("header")) {
			log.error("error extractingdata from profilediscount api null response from api");
			return false;
		}

		Map<String, Object> headerFromMembership = (Map<String, Object>) membershipProfileDiscount.get("header");
		if ((Integer) (headerFromMembership.get("status")) != HttpStatus.OK.value()) {
			log.error("error extractingdata from profilediscount api {}", headerFromMembership.get("errorMsg"));
			context.setResponseBodyHeader(new ResponseBodyHeader((Integer) (headerFromMembership.get("status")),
					(String) headerFromMembership.get("errorMsg")));
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean isResponseFromProfileSelectedCartApiValid(IOrchestratorContext context) {

		Map<String, Object> profileSelectedCart = (Map<String, Object>) context
				.getIOData(IODataConstants.PROFILE_SELECTED_CART);

		if (null == profileSelectedCart || null == profileSelectedCart.get("header")) {
			log.error("error extractingdata from profileSelectedCart api null response from api");
			return false;
		}

		Map<String, Object> headerFromMembership = (Map<String, Object>) profileSelectedCart.get("header");
		if ((Integer) (headerFromMembership.get("status")) != HttpStatus.OK.value()) {
			log.error("error extractingdata from profileSelectedCart api {}", headerFromMembership.get("errorMsg"));
			context.setResponseBodyHeader(new ResponseBodyHeader((Integer) (headerFromMembership.get("status")),
					(String) headerFromMembership.get("errorMsg")));
			return false;
		}
		return true;
	}

	private Boolean isMotherToungeValid(IOrchestratorContext context) {
		Integer mt = (Integer) context.getIOData(IODataConstants.MOTHER_TONGUE);
		if (mt == null) {
			Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
			log.error("error extractingdata mtongue for profileid {}", loggedInId.toString());
			return false;
		}
		return true;
	}
}
