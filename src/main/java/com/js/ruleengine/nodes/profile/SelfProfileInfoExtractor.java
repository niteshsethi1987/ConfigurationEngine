package com.js.ruleengine.nodes.profile;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.ETagAwareHttpChannel;
import com.js.channels.ProfileHttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPgProfile;

/**
 *
 * @author lavesh
 *
 */
@Service
public class SelfProfileInfoExtractor extends WorkNode {

	@Autowired
	private ETagAwareHttpChannel eTagAwareHttpChannel;

	@Autowired
	protected ProfileHttpChannel  profileHttpChannel;

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {

		Integer loggedInId= (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		if(loggedInId!=null){
			Set<Integer> loggedInIds = new LinkedHashSet<>();
			loggedInIds.add(loggedInId);
//			Map<Integer, JsPgProfile> pgProfileMap = eTagAwareHttpChannel.getProfilesV2Data(loggedInIds, JsPgProfile.class);
			Map<String, Object> pgProfileMap = profileHttpChannel.getProfilesV2Data(loggedInIds,JsPgProfile.class);
			if (pgProfileMap.get("debugInfo") != null) {
				((List) pgProfileMap.get("debugInfo")).forEach(i -> {
					context.setDebugData(this.getClass().getSimpleName() + ":" + i);
				});
			}
			context.setIOData("pgProfile", pgProfileMap);
		}
		return true;
	}
}
