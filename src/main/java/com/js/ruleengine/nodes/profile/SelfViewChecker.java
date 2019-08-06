/**
 *
 */
package com.js.ruleengine.nodes.profile;

import java.util.Set;

import orchestrator.IOrchestratorContext;
import orchestrator.ResponseBodyHeader;
import orchestrator.WorkNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.ProfileHttpChannel;
import com.js.ruleengine.constants.IODataConstants;

/**
 * @author goutam.mandal
 */
@Service
public class SelfViewChecker extends WorkNode {

	@Autowired
	ProfileHttpChannel profileHttpChannel;

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {

		Set<Integer> viewedIds = (Set<Integer>) context.getIOData(IODataConstants.VIEWED_PROFILE_IDS);
		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);

		if (!viewedIds.isEmpty() && viewedIds.contains(loggedInId)) {
			context.setResponseBodyData(null);
			context.setResponseBodyHeader(new ResponseBodyHeader(200, "SELF_VIEW"));
			return false;
		}
		return true;
	}
}
