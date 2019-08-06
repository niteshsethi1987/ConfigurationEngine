package com.js.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public abstract class GenericHttpChannel {

	@Autowired
	protected RestTemplate restTemplate;

	public abstract String getUri();

	private static final long NETWORK_OVERHEAD_THRESHOLD = 10;

	@SuppressWarnings("unchecked")
	public Map<String, Object> get(String path, MultiValueMap<String, String> queryParams, Map<String, List<String>> header) {

		if (log.isDebugEnabled()) {
			queryParams.add("debug", "true");
		}
		if ("true".equals(MDC.get("timing"))) {
			queryParams.add("timing", "true");
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUri() + path);
		builder.queryParams(queryParams);

		/**
		 * Build Header
		 */
		HttpHeaders headers = new HttpHeaders();
		header.forEach(headers::put);

		String transactionId = MDC.get("requestId") + ":" + UUID.randomUUID().toString();
		headers.put("JS-Request-Id", Arrays.asList(transactionId));
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> serviceResponse = null;
		long serviceCallStart = System.currentTimeMillis();
		long serviceNetworkElapse;
		try {
			serviceResponse = restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, entity, Map.class);
		}
		catch (HttpStatusCodeException e) {
			Map<String, Object> responseData = new HashMap<>();
			Map<String, Object> responseMap = null;
			try {
				responseMap = new ObjectMapper().readValue(e.getResponseBodyAsString(), new TypeReference<HashMap<String, Object>>() {});
			}
			catch (Exception ex) {
				log.warn("JSON parse exception");
			}
			responseData.put("header", responseMap.get("header"));
			responseData.put("items", new ArrayList<>());
			responseData.put("debugInfo", responseMap.get("debugInfo"));
			responseData.put("statusCode", e.getRawStatusCode());
			return responseData;
		}
		finally {
			serviceNetworkElapse = System.currentTimeMillis() - serviceCallStart;
		}
		Map<String, Object> responseMap = serviceResponse.getBody();

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("header", responseMap.get("header"));
		responseData.put("items", ((Map<String, Object>) responseMap.get("data")).get("items"));
		responseData.put("debugInfo", responseMap.get("debugInfo"));
		responseData.put("statusCode", serviceResponse.getStatusCodeValue());

		long serviceExecutionTime = (int) ((Map<String, Object>) responseMap.get("header")).get("responseTime");

		/*if (serviceNetworkElapse - serviceExecutionTime > NETWORK_OVERHEAD_THRESHOLD) {
			log.warn("Service Execution Time: {}, Network Time: {}, Difference: {}", serviceExecutionTime, serviceNetworkElapse, serviceNetworkElapse - serviceExecutionTime);
		}*/

		return responseData;
	}
}