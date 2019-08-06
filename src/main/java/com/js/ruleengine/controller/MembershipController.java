package com.js.ruleengine.controller;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.js.core.ControllerExceptionHandlerAdvice;
import com.js.ruleengine.constants.RequestParams;
import com.js.ruleengine.services.MembershipPlanService;
import com.js.utils.ResponseCodeMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import orchestrator.ServiceRequest;
import orchestrator.ServiceResponse;

/**
 * Basic validation and uncaught exceptions handled by
 * {@link ControllerExceptionHandlerAdvice}
 * <p>
 * Multiple request mappings provided for backward compatibility
 *
 * @author abhijeet.raj
 */
@CrossOrigin
@Api(value = "Plan Benefits")
@RestController
@RequestMapping("/membership/plans/")
public class MembershipController {

	@Autowired
	private MembershipPlanService membershipPlanService;

	@CrossOrigin
	@ApiOperation(value = "Get plan and benefit details")
	@RequestMapping(value = "v1/benefits", method = RequestMethod.GET)
	public ResponseEntity<ServiceResponse> getPlanBenefitsV1(
			@RequestHeader(value = "JS-Request-Id", defaultValue = "false", required = false) String requestId,
			@RequestParam(value = "pid", required = true) Integer pid,
			@RequestParam(value="isLoggedIn",defaultValue="false") Boolean isLoggedIn,
			@ApiParam(name = "ids", value = "The Section Ids to be fetched", required = true) @RequestParam(value = "ids", required = true) List<String> secids,
			@ApiParam(name = "currencyid", value = "currency based on location", required = true) @RequestParam(value = "currencyid", required = true) Integer currencyid,
			@ApiParam(name = "debug", defaultValue = "false", required = false) @RequestParam(value = "debug", defaultValue = "false", required = false) Boolean debug) {
		ServiceRequest request = new ServiceRequest();

		String reqId = MDC.get("requestId");
		Boolean timing = "true".equalsIgnoreCase(MDC.get("timing")) ? true : false;

		request.setReqParamData(RequestParams.REQUEST_ID, reqId);
		request.setReqParamData(RequestParams.IS_LOGGED_IN, isLoggedIn);
		request.setReqParamData(RequestParams.DEBUG_FLAG, debug);
		request.setReqParamData(RequestParams.TIMING_FLAG, timing);
		request.setReqParamData(RequestParams.PID, pid);
		request.setReqParamData(RequestParams.CURRENCY_ID, currencyid);
		request.setReqParamData(RequestParams.SECTION_IDS, secids);
		ServiceResponse serviceResponse = membershipPlanService.getPlanOrcestratorServiceV1().doActivities(request);
		return new ResponseEntity<>(serviceResponse,
				ResponseCodeMapper.getHttpStatus(serviceResponse.getHeader().getStatus()));
	}
}