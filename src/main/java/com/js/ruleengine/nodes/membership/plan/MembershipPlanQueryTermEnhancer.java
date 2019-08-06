package com.js.ruleengine.nodes.membership.plan;

import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.constants.RequestParams;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.ServiceRequest;
import orchestrator.WorkNode;

@Slf4j
@Service
public class MembershipPlanQueryTermEnhancer extends WorkNode {

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		log.debug("====== executing MembershipPlanQueryTermEnhancer =====");
		ServiceRequest serviceRequest = (ServiceRequest) context.getParamData();
		if (serviceRequest.getReqParamData(RequestParams.PID) != null) {
			context.setIOData(IODataConstants.LOGGEDIN_ID, serviceRequest.getReqParamData(RequestParams.PID));
		}
		context.setIOData(IODataConstants.DEBUG_FLAG, serviceRequest.getReqParamData(RequestParams.DEBUG_FLAG));
		context.setIOData(IODataConstants.TIMING_FLAG, serviceRequest.getReqParamData(RequestParams.TIMING_FLAG));
		context.setIOData(IODataConstants.REQUEST_ID, serviceRequest.getReqParamData(RequestParams.REQUEST_ID));
		context.setIOData(IODataConstants.IS_LOGGED_IN, serviceRequest.getReqParamData(RequestParams.IS_LOGGED_IN));
		context.setIOData(IODataConstants.SEC_IDS, serviceRequest.getReqParamData(RequestParams.SECTION_IDS));
		context.setIOData(IODataConstants.CURRENCY_ID, serviceRequest.getReqParamData(RequestParams.CURRENCY_ID));
		return true;
	}
}
