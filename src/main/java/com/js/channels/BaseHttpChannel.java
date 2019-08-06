package com.js.channels;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.JsonObject;
import com.js.ruleengine.domains.RemoteEntity;

/**
 * Channel to fetch data from other services over Http
 *
 * @author goutam.mandal
 */
@Slf4j
@Service
public class BaseHttpChannel {

	@Autowired
	protected RestTemplate restTemplate;

	// which json
	protected Collection<Map<String, Map<String, Object>>> getAPI(String uri, MultiValueMap<String, String> qParams, Map<String, List<String>> header) {

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

		builder.queryParams(qParams);


		// add data to header
		HttpHeaders headers = new HttpHeaders();
		if (header != null) {
			header.forEach((k, v) -> {
				headers.put(k, v);
			});
		}

		String transactionId = MDC.get("requestId") + ":" + UUID.randomUUID().toString();
		headers.put("JS-Request-Id", Arrays.asList(transactionId));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		Map<String, Object> serviceResponse = restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, entity, Map.class).getBody();

		log.debug("Getting http channel for ids: {}", serviceResponse.values());
		log.debug("URL : [{}], Header : [{}]", builder.build(false).toUri().toString(), entity);

		Collection<Map<String, Map<String, Object>>> items = (Collection<Map<String, Map<String, Object>>>) ((Map<String, Object>) serviceResponse.get("data")).get("items");

		return items;
	}

	public <T extends RemoteEntity> Map<Integer, T> getAPIPresence(String uri, MultiValueMap<String, String> qParams, Class<T> clazz, Set<Integer> ids) {

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

		builder.queryParams(qParams);
		// String viewedIdsStr = StringUtils.join(qParams, "&");

		// add data to header
		HttpHeaders headers = new HttpHeaders();

		String transactionId = MDC.get("requestId") + ":" + UUID.randomUUID().toString();
		headers.put("JS-Request-Id", Arrays.asList(transactionId));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		Map<String, Object> serviceResponse = restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, entity, Map.class).getBody();

		log.debug("Getting http channel for ids: {}", serviceResponse.values());
		log.debug("URL : [{}], Header : [{}]", builder.build(false).toUri().toString(), entity);

		Map<String, Object> data = (Map<String, Object>) serviceResponse.get("data");
		Map<Integer, T> finalResponse = new HashMap<>();
		((List<Map<String, Object>>) data.get("items")).forEach(presenceData -> {
			T p = null;
			try {
				p = clazz.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}

			p.setAttributes(presenceData);
			Integer profileid = (Integer) presenceData.get("profileId");
			finalResponse.put(profileid, p);
		});
		return finalResponse;
	}

	protected JsonObject postAPI(String uri, Map<String, String> qParams, Map<String, String> header, JsonObject body) {
		return null;
	}

	protected JsonObject putAPI(String uri, Map<String, String> qParams, Map<String, String> header, JsonObject body) {
		return null;
	}

	protected JsonObject deleteAPI(String uri, Map<String, String> qParams, Map<String, String> header) {
		return null;
	}
}