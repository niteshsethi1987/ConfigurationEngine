package com.js.channels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.configs.HttpConfiguration;
import com.js.ruleengine.domains.RemoteEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MembershipHttpChannel extends GenericHttpChannel {

	@Autowired
	protected HttpConfiguration httpConfiguration;

	@Override
	public String getUri() {
		return httpConfiguration.getMembershipUrl();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends RemoteEntity> Map<String, Object> getRemoteEntity(Class<T> clazz, String path,
			MultiValueMap<String, String> queryParams, String requestId, String idForCurrRemoteEntity) {

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(httpConfiguration.getMembershipUrl() + path);
		builder.queryParams(queryParams);

		// build headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("JS-Request-Id", requestId + ":" + UUID.randomUUID().toString());
		headers.set("JS-Internal", "true");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		Map<String, Object> responseData = new HashMap<>();
		ResponseEntity<Map> response = null;

		try {
			log.info(builder.build(false).toUri().toString(),"url=========");
			response = restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, entity, Map.class);
			log.debug(response.toString());
		} catch (HttpStatusCodeException e) {
			Map<String, Object> responseMap = null;
			try {
				responseMap = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<HashMap<String, Object>>() {
						});
			} catch (Exception ex) {
				log.warn("JSON parse exception for response from Membership service to Rule Engine");
			}
			responseData.put("header", responseMap.get("header"));
			responseData.put("items", new ArrayList<>());
			responseData.put("debugInfo", responseMap.get("debugInfo"));
			responseData.put("serviceStatusCode", e.getRawStatusCode());

			return responseData;
		}

		Map<String, Object> serviceResponseBody = response.getBody();
		Map<String, Object> data = ((Map<String, Object>) serviceResponseBody.get("data"));
		List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
		Map<Integer, T> finalResponse = getFinalResponseItems(clazz, items, idForCurrRemoteEntity);
		responseData.put("items", finalResponse);

		List<String> debugInfo = (List<String>) serviceResponseBody.get("debugInfo");
		responseData.put("header", serviceResponseBody.get("header"));
		responseData.put("serviceStatusCode", response.getStatusCodeValue());
		responseData.put("debugInfo", debugInfo);

		return responseData;
	}

	private <T extends RemoteEntity> Map<Integer, T> getFinalResponseItems(Class<T> clazz,
			List<Map<String, Object>> items,String idForCurrRemoteEntity) {
		Map<Integer, T> finalResponse = new HashMap<>();
		if(items==null) {
			return finalResponse;
		}
		
		items.forEach(remoteitem -> {
			T item = null;
			try {
				item = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			item.setAttributes(remoteitem);
			Integer id = (Integer) remoteitem.get(idForCurrRemoteEntity);
			finalResponse.put(id, item);
		});
		return finalResponse;
	}

}