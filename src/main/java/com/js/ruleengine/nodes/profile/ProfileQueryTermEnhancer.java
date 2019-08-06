/**
 * 
 */
package com.js.ruleengine.nodes.profile;

import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.constants.RequestParams;

import orchestrator.IOrchestratorContext;
import orchestrator.ServiceRequest;
import orchestrator.WorkNode;

/**
* @author Lavesh
*/
@Service
public class ProfileQueryTermEnhancer extends WorkNode {

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {		
		ServiceRequest serviceRequest = (ServiceRequest) context.getParamData();
		if (serviceRequest.getReqParamData(RequestParams.PID) != null) {
			context.setIOData(IODataConstants.LOGGEDIN_ID, serviceRequest.getReqParamData(RequestParams.PID));
		}
		context.setIOData(IODataConstants.VIEWED_PROFILE_IDS, serviceRequest.getReqParamData(RequestParams.PROFILE_IDS));
		context.setIOData(IODataConstants.PROFILE_VIEW_TYPE, serviceRequest.getReqParamData(RequestParams.PROFILE_VIEW_TYPE));
		context.setIOData(IODataConstants.DEBUG_FLAG, serviceRequest.getReqParamData(RequestParams.DEBUG_FLAG));
		context.setIOData(IODataConstants.TIMING_FLAG, serviceRequest.getReqParamData(RequestParams.TIMING_FLAG));
		context.setIOData(IODataConstants.REQUEST_ID, serviceRequest.getReqParamData(RequestParams.REQUEST_ID));
		context.setIOData(IODataConstants.VERSION_V0, serviceRequest.getReqParamData(RequestParams.VERSION));
		return true;
	}
}