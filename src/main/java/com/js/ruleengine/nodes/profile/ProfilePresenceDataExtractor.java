package com.js.ruleengine.nodes.profile;

import java.util.Map;
import java.util.Set;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.js.channels.BaseHttpChannel;
import com.js.configs.HttpConfiguration;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPresence;

@Service
public class ProfilePresenceDataExtractor extends WorkNode {

	@Autowired
	private BaseHttpChannel baseHttpChannel;

	@Autowired
	protected HttpConfiguration httpConfiguration;

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		// TODO Auto-generated method stub
		// make hit to presence api
		String url = httpConfiguration.getPresenceUrl()+"/jspresence/v1/presencestatus";
		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		Set<Integer> viewedIds = (Set<Integer>) context.getIOData(IODataConstants.VIEWED_PROFILE_IDS);

		String viewedIdsStr = StringUtils.join(viewedIds, ",");
		MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
		if (loggedInId != null) {
			viewedIdsStr += "," + loggedInId;
		}
		qParams.add("pfids", viewedIdsStr);
		Map<Integer, JsPresence> profilePresenceInfo = baseHttpChannel.getAPIPresence(url, qParams, JsPresence.class, viewedIds);
		context.setIOData(IODataConstants.PROFILE_PRESENCE_DATA, profilePresenceInfo);
		return true;
	}
}
