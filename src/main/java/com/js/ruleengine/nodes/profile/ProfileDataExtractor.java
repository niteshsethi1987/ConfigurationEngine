package com.js.ruleengine.nodes.profile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.ProfileHttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPogProfile;


/**
 * @author lavesh
 */

@Service
public class ProfileDataExtractor extends WorkNode {

	@Autowired
	protected ProfileHttpChannel  profileHttpChannel;

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {
		Set<Integer> viewedIds = (Set<Integer>) context.getIOData(IODataConstants.VIEWED_PROFILE_IDS);
		Map<String, Object> pogProfileMap = profileHttpChannel.getProfilesV2Data(viewedIds,JsPogProfile.class);
		if (pogProfileMap.get("debugInfo") != null) {
			((List) pogProfileMap.get("debugInfo")).forEach(i -> {
				context.setDebugData(this.getClass().getSimpleName() + ":" + i);
			});
		}
		context.setIOData("pogProfileMap", pogProfileMap);
		return true;
	}

	private void printSuper(Map attributes) {
		Map<String, List<String>> superView = new LinkedHashMap<>();
		for (Object entry : attributes.entrySet()) {
			String sectionName = (String) ((Entry) entry).getKey();
			Map<String, Object> sectionData = (Map<String, Object>) ((Entry) entry).getValue();
			if (superView.get(sectionName) == null) {
				superView.put(sectionName, new ArrayList<>());
			}
			if (sectionData != null) {
				superView.get(sectionName).addAll(sectionData.keySet());
			}
		}
		//System.out.println(superView);

		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (Entry<String, List<String>> entry : superView.entrySet()) {
			builder.append(entry.getKey()).append(":'");
			for (String section : entry.getValue()) {
				builder.append(section).append(",");
			}
			builder.replace(builder.length() - 1, builder.length(), "',");
		}
		builder.replace(builder.length() - 1, builder.length(), "");
		builder.append("}");
		//System.out.println(builder.toString());
	}
}
