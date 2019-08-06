package com.js.channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.common.utils.EtagUtil;
import com.js.ruleengine.constants.ETagConstants;
import com.js.ruleengine.domains.ETagEntity;
import com.js.ruleengine.repository.EtagRepository;

@Slf4j
public abstract class GenericETagAwareHttpChannel extends GenericHttpChannel {

	@Autowired
	EtagRepository etagRepository;

	public Map<String, Object> get(String path, MultiValueMap<String, String> queryParams, Map<String, List<String>> header, List<String> etagProfileKeys,
			String keyType) {
		List<String> eTags = new ArrayList<>();
		Map<String, String> eTagPresentMapping = new HashMap<>();
		// this needs to be done in generic way
		long startTime = System.currentTimeMillis();
		Iterable<ETagEntity> profilesDataCache = etagRepository.findAll(etagProfileKeys);
		log.debug("eTag repo {}", System.currentTimeMillis() - startTime);
		for (ETagEntity eTagEntity2 : profilesDataCache) {
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
		String eTagPfidsStr = StringUtils.join(eTags, ",");
		queryParams.add("Etag-list", eTagPfidsStr);
		long pStartTime = System.currentTimeMillis();
		Map<String, Object> httpData = super.get(path, queryParams, header);
		List<Map<String, Object>> items = (List<Map<String, Object>>) httpData.get("items");
		log.debug("Profile service {}", System.currentTimeMillis() - pStartTime);
		// need to fetch data here.

		List<ETagEntity> eTagEntitiesToInsert = new ArrayList<>();

		// need to convert into another form.

		items.forEach((profileData) -> {
			Map<String, Object> etagData = (Map<String, Object>) profileData.get(ETagConstants.ETAG_SECTION);
			String etagKey = EtagUtil.generateEtagKey((Integer) etagData.get(ETagConstants.PROFILE_ID), keyType);

			if (etagData.get(ETagConstants.RESPONSE_CODE).equals(ETagConstants.RESPONSE_CODE_200)) {
				// save data to aerospike
				ETagEntity eTagEntity = new ETagEntity();

				eTagEntity.setId(etagKey);
				ObjectMapper mapper = new ObjectMapper();
				String json = null;
				try {
					json = mapper.writeValueAsString(profileData);
				}
				catch (JsonProcessingException e) {
					log.debug(e.getMessage());
				}
				eTagEntity.setObj(json);
				eTagEntity.setETag((String) etagData.get(ETagConstants.ETAG));
				eTagEntitiesToInsert.add(eTagEntity);
			} else {

				for (ETagEntity etagProfileData : profilesDataCache) {
					if (etagProfileData.getId().equals(etagKey)) {
						TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
						Map<String, Object> newMap = null;
						try {
							newMap = new ObjectMapper().readValue(etagProfileData.getObj(), typeRef);
						}
						catch (IOException e) {
							log.debug(e.getMessage());
						}
						profileData.putAll(newMap);
					}
				}
			}
		});

		if (!eTagEntitiesToInsert.isEmpty()) {
			etagRepository.save(eTagEntitiesToInsert);
		}
		log.debug("eTag get method {}", System.currentTimeMillis() - startTime);
		return httpData;
	}

	public Map<String, Object> getWithoutETag(String path, MultiValueMap<String, String> queryParams, Map<String, List<String>> header) {
		return super.get(path, queryParams, header);
	}

	public abstract List<String> getEtagKey(MultiValueMap<String, String> qParams, Map<String, List<String>> headers);
}