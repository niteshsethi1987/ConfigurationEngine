package com.js.channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.common.utils.EtagUtil;
import com.js.configs.HttpConfiguration;
import com.js.ruleengine.constants.ETagConstants;
import com.js.ruleengine.domains.ETagEntity;
import com.js.ruleengine.domains.JsProfile;
import com.js.ruleengine.domains.RemoteEntity;
import com.js.ruleengine.repository.EtagRepository;

/**
 * Channel to fetch data from other services over Http
 * 
 * @author goutam.mandal
 */
@Slf4j
@Service
public class ETagAwareHttpChannel extends BaseHttpChannel {

	@Autowired
	protected HttpConfiguration httpConfiguration;
	// aerospikeRepository : AerospikeRepository
	@Autowired
	EtagRepository profileRepository;

	@SuppressWarnings("unchecked")
	protected <T extends JsProfile> Collection<Map<String, Map<String, Object>>> getProfilesV2Data0000(Set<Integer> viewedIds, List<String> eTagPfids,
			Class<T> clazz) {

		// uri
		String uri = httpConfiguration.getProfileUrl() + "/jsprofile/v2/profiles";

		// param
		String viewedIdsStr = StringUtils.join(viewedIds, ",");
		MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
		qParams.add("ids", viewedIdsStr);

		String eTagPfidsStr = StringUtils.join(eTagPfids, ",");
		qParams.add("Etag-list", eTagPfidsStr);
		// header
		Map<String, List<String>> headers = new HashMap<>();
		// get etags

		// get items
		Collection<Map<String, Map<String, Object>>> items = getAPI(uri, qParams, headers);

		return items;
	}

	protected <T extends RemoteEntity> Map<Integer, T> getDecoratedResponse(Collection<Map<String, Map<String, Object>>> items, Class<T> clazz) {
		Map<Integer, T> finalResponse = new HashMap<>();
		items.forEach(profileData -> {
			T p = null;
			try {
				p = clazz.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			p.setAttributes(profileData);
			Integer profileid;
			if (profileData.get(ETagConstants.ETAG_SECTION) != null) {
				profileid = (Integer) (profileData.get(ETagConstants.ETAG_SECTION).get(ETagConstants.PROFILE_ID));
			} else {
				profileid = (Integer) (profileData.get("id").get("pid"));
			}
			finalResponse.put(profileid, p);
		});
		return finalResponse;
	}

	public <T extends JsProfile> Map<Integer, T> getProfilesV2Data(Set<Integer> viewedIds, Class<T> clazz) {

		List<String> etagProfileKeys = new ArrayList<>();

		// generate keys to fetch from aerospike
		viewedIds.forEach((profileid) -> {
			etagProfileKeys.add(EtagUtil.generateEtagKey(profileid,com.js.ruleengine.constants.ETagConstants.ETAG_PROFILE_KEY));
		});

		List<String> eTags = new ArrayList<>();
		Map<String, String> eTagPresentMapping = new HashMap<>();
		// get repository
		Iterable<ETagEntity> profileList = profileRepository.findAll(etagProfileKeys);
		for (ETagEntity eTagEntity2 : profileList) {
			eTagPresentMapping.put(eTagEntity2.getId(), eTagEntity2.getETag());
		}

		// get all etags
		for (String keys : etagProfileKeys) {
			// streams
			if (eTagPresentMapping.get(keys) != null) {
				eTags.add(eTagPresentMapping.get(keys));
			} else {
				eTags.add("0");
			}
		}

		List<ETagEntity> eTagEntitiesToInsert = new ArrayList<>();

		Collection<Map<String, Map<String, Object>>> items;

		// fetch data from server
		items = getProfilesV2Data0000(viewedIds, eTags, clazz);

		items.forEach((profileData) -> {
			if ( profileData.get(ETagConstants.ETAG_SECTION).get(ETagConstants.RESPONSE_CODE) == (ETagConstants.RESPONSE_CODE_200)) {
				// save data to aerospike
				ETagEntity eTagEntity = new ETagEntity();

				eTagEntity.setId(ETagConstants.ETAG_PROFILE_KEY
						+ profileData.get(ETagConstants.ETAG_SECTION).get(ETagConstants.PROFILE_ID));
				ObjectMapper mapper = new ObjectMapper();
				String json = null;
				try {
					json = mapper.writeValueAsString(profileData);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					log.debug(e.getMessage());
				}
				eTagEntity.setObj(json);
				eTagEntity.setETag((String) profileData.get(ETagConstants.ETAG_SECTION).get(ETagConstants.ETAG));
				eTagEntitiesToInsert.add(eTagEntity);
			} else {
				String etagTempKey = ETagConstants.ETAG_PROFILE_KEY + profileData.get(ETagConstants.ETAG_SECTION).get(ETagConstants.PROFILE_ID);
				Map<String, Object> originalEtag = profileData.get(ETagConstants.ETAG_SECTION);
				for (ETagEntity eTagEntity2 : profileList) {
					if (eTagEntity2.getId().equals(etagTempKey)) {
						TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
						};
						Map<String, Map<String, Object>> newMap = null;
						try {
							newMap = new ObjectMapper().readValue(eTagEntity2.getObj(), typeRef);
						} catch (IOException e) {
							log.debug(e.getMessage());
						}

						newMap.put(ETagConstants.ETAG_SECTION, originalEtag);
						profileData.putAll(newMap);
					}
				}
			}
		});

		Map<Integer, T> httpResponse = getDecoratedResponse(items, clazz);

		if (!eTagEntitiesToInsert.isEmpty()) {
			profileRepository.save(eTagEntitiesToInsert);
		}
		return httpResponse;
	}

}