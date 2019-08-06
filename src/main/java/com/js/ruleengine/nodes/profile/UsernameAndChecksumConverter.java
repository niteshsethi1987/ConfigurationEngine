/**
 *
 */
package com.js.ruleengine.nodes.profile;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.ResponseBodyHeader;
import orchestrator.WorkNode;
import orchestrator.exception.ArgumentException;
import orchestrator.exception.ResourceException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.ProfileHttpChannel;
import com.js.common.utils.ProfileCheckSumUtil;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.constants.ProfileNonVisibilityReason;
import com.js.ruleengine.domains.JsPogProfile;

/**
 * @author goutam.mandal
 */
@Slf4j
@Service
public class UsernameAndChecksumConverter extends WorkNode {

	@Autowired
	ProfileHttpChannel profileHttpChannel;

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {

		Set<String> viewedIds = (Set<String>) context.getIOData(IODataConstants.VIEWED_PROFILE_IDS);

		if (viewedIds.isEmpty() || viewedIds.contains("0")) {
			throw new ArgumentException("Empty or invalid username/pchecksum received. " + viewedIds);
		}

		boolean isUsername = checkIfUsername(viewedIds.iterator().next());

		Set<Integer> validatedIds = null;
		if (isUsername) {
			validatedIds = validateAndConvertUsername(viewedIds);
			if (validatedIds == null) {
				context.setResponseBodyData(createWrongUsernameResponse(viewedIds.iterator().next()));
				context.setResponseBodyHeader(new ResponseBodyHeader(200, null));
				return false;
			}
		} else {
			validatedIds = validateAndConvertPCsum(viewedIds);
		}

		if (validatedIds.isEmpty()) {
			context.setDebugData("Invalid username/pchecksum received. " + viewedIds);
		}
		context.setIOData(IODataConstants.VIEWED_PROFILE_IDS, validatedIds);

		return true;
	}

	private Map<String, Object> createWrongUsernameResponse(String username) {

		Map<String, Object> dataMap = new LinkedHashMap<>();
		Map<String, Object> profileMap = new LinkedHashMap<>();
		profileMap.put("be", ProfileNonVisibilityReason.INVALID_USERNAME.asMap());
		Map<String, Object> idSection = new LinkedHashMap<>();
		idSection.put("pid", -1);
		idSection.put("uname", username);
		idSection.put("csum", null);
		profileMap.put("id", idSection);
		List<Map<String, Object>> items = Arrays.asList(profileMap);
		dataMap.put("count", items.size());
		dataMap.put("items", items);

		return dataMap;
	}

	private Set<Integer> validateAndConvertUsername(Set<String> viewedIds) {
		Map<String, Object> pogProfileIds = profileHttpChannel.getProfileId(viewedIds, JsPogProfile.class);
		if ((int) pogProfileIds.get("serviceStatusCode") == 4000) {
			//throw new ArgumentException("Invalid username/pchecksum received. " + viewedIds);
			return null;
		} else if ((int) pogProfileIds.get("serviceStatusCode") == 5000) {
			throw new ResourceException("Could not fetch data. " + viewedIds);
		}
		return ((Map<Integer, Object>) pogProfileIds.get("data")).keySet();
	}

	private Set<Integer> validateAndConvertPCsum(Set<String> viewProfileIds) {
		//try {
			return new LinkedHashSet<>(ProfileCheckSumUtil.profileCheckSumToProfileID(viewProfileIds).values());
		/*}
		catch (ArgumentException ex) {
			log.error("Invalid Profile checksum(s) received. {}", viewProfileIds, ex);
			return Collections.emptySet();
		}*/
	}

	private boolean checkIfUsername(String id) {
		return id.length() <= 33 && !StringUtils.isNumeric(id);
	}

}
