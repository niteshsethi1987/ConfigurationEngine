package com.js.ruleengine.nodes.membership.plan;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.js.channels.MembershipHttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsMembershipDiscount;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

@Slf4j
@Service
public class ProfileDiscountExtractor extends WorkNode {

	@Autowired
	protected MembershipHttpChannel membershipHttpChannel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		log.debug("====== executing ProfileDiscountExtractor =====");
		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		String requestId = (String) context.getIOData(IODataConstants.REQUEST_ID);
		Boolean timing = (Boolean) context.getIOData(IODataConstants.TIMING_FLAG);
		Boolean debug = (Boolean) context.getIOData(IODataConstants.DEBUG_FLAG);
		Boolean isLoggedIn = (Boolean) context.getIOData(IODataConstants.IS_LOGGED_IN);
		if (isLoggedIn == false)
			return true;
		String path = "/jsmembership/v1/discount";
		MultiValueMap<String, String> queryParams = getQueryParam(timing, debug, loggedInId);
		Map<String, Object> profileDiscount = membershipHttpChannel.getRemoteEntity(JsMembershipDiscount.class, path,
				queryParams, requestId, "sid");

		if (profileDiscount.get("debugInfo") != null) {
			((List) profileDiscount.get("debugInfo")).forEach(i -> {
				context.setDebugData(this.getClass().getSimpleName() + ":" + i);
			});
		}
		context.setIOData(IODataConstants.MEMBERSHIP_PROFILE_DISCOUNT, profileDiscount);
		return true;
	}

	private MultiValueMap<String, String> getQueryParam(Boolean timing, Boolean debug, Integer loggedInId) {
		// query parameter creation
		String pidString = loggedInId.toString();
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("debug", debug.toString());
		queryParams.add("timing", timing.toString());
		queryParams.add("pid", pidString);
		return queryParams;
	}
}
