package com.js.ruleengine.nodes.membership.plan;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.ProfileHttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPgProfile;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

@Slf4j
@Service
public class ProfileMTongueExtractor extends WorkNode {

	@Autowired
	protected ProfileHttpChannel profileHttpChannel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		log.debug("====== executing ProfileDiscountExtractor =====");
		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		// String requestId = (String) context.getIOData(IODataConstants.REQUEST_ID);
		// Boolean timing = (Boolean) context.getIOData(IODataConstants.TIMING_FLAG);
		// Boolean debug = (Boolean) context.getIOData(IODataConstants.DEBUG_FLAG);
		Boolean isLoggedIn = (Boolean) context.getIOData(IODataConstants.IS_LOGGED_IN);
		if (isLoggedIn == false)
			return true;
		Set<Integer> loggedInIdSet = getLoggedInIdAsSet(loggedInId);
		Map<String, Object> profileMtongue = profileHttpChannel.getProfilesV2Data(loggedInIdSet, JsPgProfile.class);

		if (profileMtongue.get("debugInfo") != null) {
			((List) profileMtongue.get("debugInfo")).forEach(i -> {
				context.setDebugData(this.getClass().getSimpleName() + ":" + i);
			});
		}
		Integer mt = getMotheTongueOfLoggedInProfile(loggedInId, profileMtongue);
		if (mt == null) {
			log.error("mother tongue not received from profile service for profileid : {}", loggedInId);
			mt=0;
		}
		context.setIOData(IODataConstants.MOTHER_TONGUE, mt);
		return true;
	}

	@SuppressWarnings("unchecked")
	private Integer getMotheTongueOfLoggedInProfile(Integer loggedInId, Map<String, Object> profileMtongue) {

		Map<Integer, JsPgProfile> remoteItem = (Map<Integer, JsPgProfile>) profileMtongue.get("data");

		JsPgProfile jsProfile = remoteItem.get(loggedInId);
		if (jsProfile == null) {
			return null;
		}

		Map<String, Object> jsProfileAttributes = jsProfile.getAttributes();
		if (jsProfileAttributes == null) {
			return null;
		}

		Map<String, Object> bi = (Map<String, Object>) jsProfileAttributes.get("bi");
		if (bi == null) {
			return null;
		}

		Integer mt = (Integer) bi.get("mt");
		return mt;
	}

	private Set<Integer> getLoggedInIdAsSet(Integer loggedInId) {
		Set<Integer> loggedInIdSet = new HashSet<>();
		loggedInIdSet.add(loggedInId);
		return loggedInIdSet;
	}

}
