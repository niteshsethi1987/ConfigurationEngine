package com.js.ruleengine.nodes.profile;

import java.util.Map;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.springframework.stereotype.Service;

import com.js.ruleengine.domains.JsPogMembership;
import com.js.ruleengine.domains.JsPogProfile;

@Service
public class PdResponseCollator extends WorkNode {

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {

		Map<Integer, JsPogProfile> pogProfile = (Map<Integer, JsPogProfile>) context.getIOData("pogProfileMap");
		Map<Integer, JsPogMembership> pogMembership = (Map<Integer, JsPogMembership>) context.getIOData("pogMembershipMap");

		pogProfile.forEach((profileId, profile) -> {
			JsPogMembership membership = pogMembership.get(profileId);
			profile.addOrUpdateField(membership.getField("paid"), "sb", "paid");
			profile.addOrUpdateField(membership.getField("mmem"), "sb", "mm");
			profile.addOrUpdateField(membership.getField("addOns"), "sb", "aom");
		});

		return true;
	}
}
