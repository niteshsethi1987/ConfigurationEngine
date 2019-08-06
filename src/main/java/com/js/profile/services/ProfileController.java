package com.js.profile.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.js.channels.HttpChannel;
import com.js.constants.ViewType;
import com.js.core.ControllerExceptionHandlerAdvice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import orchestrator.ResponseBodyHeader;
import orchestrator.ServiceRequest;
import orchestrator.ServiceResponse;

/**
 * Validation and uncaught exceptions handled by
 * {@link ControllerExceptionHandlerAdvice}
 * 
 * @author Esha / goutam.mandal
 *
 */
@Slf4j
@CrossOrigin
@Api(value = "Profile", description = "Operations pertaining to Profile")
@ComponentScan({ "com", "com.*", "com.js.db.repo.*" })
@RestController
@RequestMapping("/jsprofile")
public class ProfileController {

	@Autowired
	protected ProfileService profileService;

	@Autowired
	private JewelleryShopService jewelleryShopService;

	@Autowired
	JsProfileImageService JsProfileImageService;

	@Autowired
	private HttpChannel httpChannel;

	// @Autowired
	// public ProfileController(JewelleryShopService jewelleryShopService) {
	// this.jewelleryShopService = jewelleryShopService;
	// }

	@RequestMapping(value = "/getDiscount", method = RequestMethod.GET, produces = "application/json")
	public ServiceResponse getQuestions(@RequestParam(required = true) String type) {

		Product product = new Product();
		product.setType(type);

		jewelleryShopService.getProductDiscount(product);

		log.info(product.toString());

		// Object data = context.getResponseBodyData();
		ServiceResponse a = new ServiceResponse();
		a.setResponseBodyHeader(new ResponseBodyHeader(200, ""));
		a.setResponseBodyData(product);

		return a;
	}

	@RequestMapping(value = "/getProfile", method = RequestMethod.GET, produces = "application/json")
	public ServiceResponse getProfile(@RequestParam(required = true) String type) {
		JsProfile p = httpChannel.getProfiles();
		JsImage img = httpChannel.getImage();

		JsProfileImageService.getProductDiscount(p,img);

		ServiceResponse a = new ServiceResponse();
		a.setResponseBodyHeader(new ResponseBodyHeader(200, ""));
		a.setResponseBodyData(p);
		return a;
	}

	@CrossOrigin
	@ApiOperation(value = "Get details of profile(s)")
	@RequestMapping(value = "/v1/profiles", method = RequestMethod.GET)
	public ServiceResponse getProfiles(
			@RequestHeader(value = "JB-Profile-Identifier", defaultValue = "", required = false) Integer pid,
			@RequestHeader(value = "JB-Profile-Identifier-Authchecksum", defaultValue = "", required = false) String authChecksum,
			@RequestHeader(value = "JB-Raw-Data", defaultValue = "false", required = false) Boolean outputFormatFlag,
			@RequestHeader(value = "JB-Internal", defaultValue = "false", required = false) Boolean jbInternal,
			@RequestHeader(value = "JS-Bucket", defaultValue = "0", required = false) Integer abBucket,
			@RequestHeader(value = "User-Agent", defaultValue = "false", required = false) String userAgent,

			@ApiParam(name = "pfids", value = "The Profile Ids to be viewed", required = true) @RequestParam(value = "pfids", required = true) Set<String> pfids,

			@ApiParam(name = "view", value = "Type of view, like shortview, basic, vcard", defaultValue = ViewType.SUPER, required = false) @RequestParam(value = "view", defaultValue = ViewType.SUPER, required = false) String viewType,

			@ApiParam(name = "fl", defaultValue = "", required = false) @RequestParam(value = "fl", defaultValue = "", required = false) Set<String> filters,

			@ApiParam(name = "sk", defaultValue = "false", required = false) @RequestParam(value = "sk", defaultValue = "false", required = false) Boolean shortKey) {
		ServiceRequest request = new ServiceRequest();

		// request.setReqParamData(RequestParams.PID, pid);
		// request.setReqHeaderData(RequestParams.AUTHCHECKSUM, authChecksum);
		// request.setReqHeaderData(RequestParams.OUTPUT_FORMAT_FLAG,
		// outputFormatFlag);
		// request.setReqHeaderData(RequestParams.JB_INTERNAL, jbInternal);
		// request.setReqHeaderData(RequestParams.USER_AGENT, userAgent);
		// request.setReqHeaderData(RequestParams.AB_FLAG, abBucket);
		//
		// request.setReqParamData(RequestParams.PROFILE_IDS, pfids);
		// request.setReqParamData(RequestParams.VIEW_TYPE, viewType);
		// request.setReqParamData(RequestParams.FILTERS, filters);
		// request.setReqParamData(RequestParams.SHORT_KEY, shortKey);

		return profileService.getOsSelectProfileV1().doActivities(request);
	}
}
