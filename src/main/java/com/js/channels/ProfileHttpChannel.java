package com.js.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.js.common.utils.EtagUtil;
import com.js.configs.HttpConfiguration;
import com.js.ruleengine.constants.ETagConstants;
import com.js.ruleengine.domains.JsProfile;

@Slf4j
@Service
public class ProfileHttpChannel extends GenericETagAwareHttpChannel {

	@Autowired
	protected HttpConfiguration httpConfiguration;

	@Override
	public String getUri() {
		return httpConfiguration.getProfileUrl();
	}

	/**
	 * Generate EtagKeys Identifier
	 */
	@Override
	public List<String> getEtagKey(MultiValueMap<String, String> qParams, Map<String, List<String>> headers) {

		List<String> listOfIds = qParams.get("ids");
		List<String> viewedIds = new ArrayList<>();
		listOfIds.forEach(item -> viewedIds.addAll(Arrays.asList(item.split(","))));

		List<String> etagProfileKeys = new ArrayList<>();

		viewedIds.forEach(profileid -> etagProfileKeys.add(EtagUtil.generateEtagKey(Integer.parseInt(profileid),ETagConstants.ETAG_PROFILE_KEY)));
		return etagProfileKeys;

	}

	/**
	 * Generate Query Params
	 *
	 * @param viewedIds
	 * @return
	 */
	private MultiValueMap<String, String> getQueryParams(Set<Integer> viewedIds) {
		String viewedIdsStr = StringUtils.join(viewedIds, ",");
		MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
		qParams.add("ids", viewedIdsStr);
		return qParams;
	}

	/**
	 * Public method available to fetch profile data.
	 *
	 * @param viewedIds
	 * @param clazz
	 * @return
	 */
	public <T extends JsProfile> Map<String, Object> getProfilesV2Data(Set<Integer> viewedIds, Class<T> clazz) {
		MultiValueMap<String, String> queryParams = getQueryParams(viewedIds);
		Map<String, List<String>> headers = new HashMap<>();
		List<String> etagProfiles = getEtagKey(queryParams, headers);


		long startTime = System.currentTimeMillis();
		Map<String, Object> response = get("/jsprofile/v2/profiles", queryParams, headers,etagProfiles,ETagConstants.ETAG_PROFILE_KEY);
		log.debug("Profile Response {}", System.currentTimeMillis() - startTime);
		Map<Integer, T> data = getTypedResponse(response, clazz);

		Map<String, Object> retVal = new HashMap<>();
		retVal.put("statusCode", response.get("statusCode"));
		retVal.put("serviceStatusCode", ((Map<String, Object>) response.get("header")).get("status"));
		retVal.put("debugInfo", response.get("debugInfo"));
		retVal.put("errorMsg", ((Map<String, Object>) response.get("header")).get("errorMsg"));
		retVal.put("data", data);

		return retVal;

	}

	public <T extends JsProfile> Map<String, Object> getProfileId(Set<String> viewedIds, Class<T> clazz) {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("username", String.join(",", viewedIds));
		Map<String, List<String>> headers = new HashMap<>();
		Map<String, Object> response = getWithoutETag("/jsprofile/v1/profileid", queryParams, headers);
		Map<Integer, T> data = getTypedResponse(response, clazz);

		Map<String, Object> retVal = new HashMap<>();
		retVal.put("statusCode", response.get("statusCode"));
		retVal.put("serviceStatusCode", ((Map<String, Object>) response.get("header")).get("status"));
		retVal.put("data", data);

		return retVal;

	}

	/**
	 * Typed response - Child class of JsProfile (JsPgProfile,JsPogProfile)
	 *
	 * @param response
	 * @param clazz
	 * @return
	 */
	private <T extends JsProfile> Map<Integer, T> getTypedResponse(Map<String, Object> response, Class<T> clazz) {
		Map<Integer, T> finalResponse = new HashMap<>();
		((List<Map<String, Object>>) response.get("items")).forEach(profileData -> {
			T p = null;
			try {
				p = clazz.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}

			p.setAttributes(profileData);
			Integer profileid;
			profileid = (Integer) ((Map) profileData.get("id")).get("profileid");
			if (profileid == null || profileid == 0) {
				profileid = (Integer) ((Map) profileData.get("id")).get("pid");
			}
			finalResponse.put(profileid, p);
		});
		return finalResponse;
	}
}