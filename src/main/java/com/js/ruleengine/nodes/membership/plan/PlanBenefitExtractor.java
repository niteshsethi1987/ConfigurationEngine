package com.js.ruleengine.nodes.membership.plan;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.js.channels.MembershipHttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsMembershipPlan;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

@Slf4j
@Service
public class PlanBenefitExtractor extends WorkNode {

	@Autowired
	protected MembershipHttpChannel membershipHttpChannel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {

		log.debug("====== executing PlanBenefitExtractor =====");
		String requestId = (String) context.getIOData(IODataConstants.REQUEST_ID);
		Boolean timing = (Boolean) context.getIOData(IODataConstants.TIMING_FLAG);
		Boolean debug = (Boolean) context.getIOData(IODataConstants.DEBUG_FLAG);
		List<Integer> secids = (List<Integer>) context.getIOData(IODataConstants.SEC_IDS);

		String path = "/jsmembership/v1/plans";
		MultiValueMap<String, String> queryParams = getQueryParam(timing, debug, secids);

		Map<String, Object> membershipPlanBenefit = membershipHttpChannel.getRemoteEntity(JsMembershipPlan.class,
				path, queryParams, requestId,"sectionid");

		if (membershipPlanBenefit.get("debugInfo") != null) {
			((List) membershipPlanBenefit.get("debugInfo")).forEach(i -> {
				context.setDebugData(this.getClass().getSimpleName() + ":" + i);
			});
		}

		context.setIOData(IODataConstants.MEMBERSHIP_PLAN_BENEFIT, membershipPlanBenefit);
		return true;
	}

	private MultiValueMap<String, String> getQueryParam(Boolean timing, Boolean debug, List<Integer> secids) {
		// query parameter creation
		String idString = StringUtils.join(secids, ",");
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("debug", debug.toString());
		queryParams.add("timing", timing.toString());
		queryParams.add("ids", idString);
		return queryParams;
	}

}
