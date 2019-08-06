package com.js.ruleengine.controller;

import java.util.Set;

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
import com.js.ruleengine.constants.ViewType;
import com.js.ruleengine.services.ProfileService;
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
 * @author goutam.mandal
 */
@CrossOrigin
@Api(value = "Profile API")
@RestController
//@ComponentScan({ "com.js.ruleengine.nodes.profile.*" })
@RequestMapping("/ruleengine/profile/")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@CrossOrigin
	@ApiOperation(value = "Get details of profile(s)")
	@RequestMapping(value = "/v1/profiles", method = RequestMethod.GET)
	public ResponseEntity<ServiceResponse> getProfilesV1(
			@RequestHeader(value = "JS-User-Agent", defaultValue = "false", required = false) String userAgent,
			@RequestHeader(value = "JS-Request-Id", defaultValue = "false", required = false) String requestId,
			@RequestHeader(value = "version",defaultValue = "v8",required=false)String version,

			@RequestHeader(value = "JS-Profile-Identifier", required = false)
			@RequestParam(value = "pid", required = false) Integer pid,

			@ApiParam(name = "ids", value = "The Profile Ids to be viewed", required = true)
			@RequestParam(value = "ids", required = true) Set<String> pfids,

			@ApiParam(name = "vt", value = "Type of view, like shortview, basic, vcard", defaultValue = ViewType.SUPER, required = false)
			@RequestParam(value = "vt", defaultValue = ViewType.SUPER, required = false) String viewType,

			@ApiParam(name = "debug", defaultValue = "false", required = false)
			@RequestParam(value = "debug", defaultValue = "false", required = false) Boolean debug

	) {
		ServiceRequest request = new ServiceRequest();
		
		String reqId = MDC.get("requestId");
		Boolean timing = "true".equalsIgnoreCase(MDC.get("timing")) ? true : false;
		request.setReqParamData(RequestParams.VERSION, version);
		request.setReqParamData(RequestParams.REQUEST_ID, reqId);
		request.setReqParamData(RequestParams.DEBUG_FLAG, debug);
		request.setReqParamData(RequestParams.TIMING_FLAG, timing);
		request.setReqParamData(RequestParams.PID, pid);
		request.setReqParamData(RequestParams.PROFILE_IDS, pfids);
		request.setReqParamData(RequestParams.PROFILE_VIEW_TYPE, viewType);
		ServiceResponse serviceResponse = profileService.getProfileServiceV1().doActivities(request);
		return new ResponseEntity<>(serviceResponse, ResponseCodeMapper.getHttpStatus(serviceResponse.getHeader().getStatus()));
	}
}