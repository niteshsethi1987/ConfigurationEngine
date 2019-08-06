package com.js.ruleengine.nodes.membership;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.HttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPogMembership;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

@Service
public class MembershipDataExtractor extends WorkNode{

	@Autowired
	protected HttpChannel httpChannel;

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		@SuppressWarnings("unchecked")
		Set<Integer> viewedIds = (Set<Integer>) context.getIOData(IODataConstants.VIEWED_PROFILE_IDS);
		String requestId = (String) context.getIOData(IODataConstants.REQUEST_ID);
		Boolean timing = (Boolean) context.getIOData(IODataConstants.TIMING_FLAG);
		Boolean debug = (Boolean) context.getIOData(IODataConstants.DEBUG_FLAG);
		
		Map<String, Object> response = httpChannel.getMembershipDetailsV1(viewedIds, JsPogMembership.class,  
				requestId, timing, debug);
		if (response.get("debugInfo") != null) {
			((List) response.get("debugInfo")).forEach(i -> {
				context.setDebugData(this.getClass().getSimpleName() + ":" + i);
			});
		}
		context.setIOData("pogMembershipMap", response);
		return true;
	}
}
