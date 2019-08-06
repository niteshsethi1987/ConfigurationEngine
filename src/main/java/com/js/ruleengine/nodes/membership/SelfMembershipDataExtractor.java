package com.js.ruleengine.nodes.membership;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.HttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPgMembership;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

@Service
public class SelfMembershipDataExtractor extends WorkNode {

	@Autowired
	protected HttpChannel httpChannel;

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		String requestId = (String) context.getIOData(IODataConstants.REQUEST_ID);
		Boolean timing = (Boolean) context.getIOData(IODataConstants.TIMING_FLAG);
		Boolean debug = (Boolean) context.getIOData(IODataConstants.DEBUG_FLAG);
		
		if (loggedInId != null) {
			Set<Integer> loggedInIds = new LinkedHashSet<>();
			loggedInIds.add(loggedInId);
			Map<String, Object> pgMembershipMap = httpChannel.getMembershipDetailsV1(loggedInIds, JsPgMembership.class, 
					requestId, timing, debug);
			if (pgMembershipMap.get("debugInfo") != null) {
				((List) pgMembershipMap.get("debugInfo")).forEach(i -> {
					context.setDebugData(this.getClass().getSimpleName() + ":" + i);
				});
			}
			context.setIOData("pgMembership", pgMembershipMap);
		}
		return true;
	}
}
