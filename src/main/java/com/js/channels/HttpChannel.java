package com.js.channels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.configs.HttpConfiguration;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsContact;
import com.js.ruleengine.domains.JsMembership;

/**
 * Channel to fetch data from other services over Http
 *
 * @author goutam.mandal
 */
@Service
@Slf4j
public class HttpChannel {

//	@Value("${service.membership.maxlatency}")
//	private Integer membershipLatency;

	@Autowired
	protected HttpConfiguration	httpConfiguration;

	@Autowired
	protected RestTemplate		restTemplate;

	/**
	 * @author - Lavesh
	 * @param viewedIds
	 * @return finalResponse
	 */
//	@SuppressWarnings("unchecked")
//	public <T extends JsProfile> Map<Integer, T> getProfilesV2Data(Set<Integer> viewedIds, Class<T> clazz) {
//
//		String viewedIdsStr = StringUtils.join(viewedIds, ",");
//
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(httpConfiguration.getProfileUrl() + "/jsprofile/v2/profiles?ids=" + viewedIdsStr
//				+ "&Etag-list=1234");
//
//		Map<String, Object> serviceResponse = restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, null, Map.class).getBody();
//		Collection<Map<String, Map<String, Object>>> items = (Collection<Map<String, Map<String, Object>>>) ((Map<String, Object>) serviceResponse.get("data")).get("items");
//
//		Map<Integer, T> finalResponse = new HashMap<>();
//		items.forEach(profileData -> {
//			T p = null;
//			try {
//				p = clazz.newInstance();
//			}
//			catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//			p.setAttributes(profileData);
//			Integer profileid = (Integer) profileData.get("id").get("pid");
//			finalResponse.put(profileid, p);
//		});
//		return finalResponse;
//	}



//	protected <T extends JsProfile> Map<Integer, T> getDecoratedResponse(Collection<Map<String, Map<String, Object>>> items, Class<T> clazz)
//	{
//		Map<Integer, T> finalResponse = new HashMap<>();
//		items.forEach(profileData -> {
//			T p = null;
//			try {
//				p = clazz.newInstance();
//			}
//			catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//			p.setAttributes(profileData);
//			Integer profileid;
//			if (profileData.get("id") == null) {
//				profileid = (Integer) profileData.get("eTag").get("eTagProfileId");
//			} else {
//				profileid = (Integer) profileData.get("id").get("pid");
//			}
//			finalResponse.put(profileid, p);
//		});
//		return finalResponse;
//	}

	/**
	 * @author - Ayush
	 * @param List of ProfileIds
	 * @return finalResponse -> Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	public <T extends JsMembership> Map<String, Object> getMembershipDetailsV1(Set<Integer> pIds, Class<T> clazz,
			String requestId, Boolean timing, Boolean debug) {

		String idString = StringUtils.join(pIds, ",");
 		String path = "/jsmembership/v1/membership";

 		// query parameter creation and handling
 		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

 		if (debug) {
 			queryParams.add("debug", "true");
		}
		if (timing) {
			queryParams.add("timing", "true");
		}

		queryParams.add("ids", idString);

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
			response = restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, entity, Map.class);
		}
		catch (HttpStatusCodeException e) {
			Map<String, Object> responseMap = null;
			try {
				responseMap = new ObjectMapper().readValue(e.getResponseBodyAsString(), new TypeReference<HashMap<String, Object>>() {});
			}
			catch (Exception ex) {
				log.warn("JSON parse exception for response from Membership service to Rule Engine {}",response.getStatusCode());
			}
			responseData = new HashMap<>();
			responseData.put("header", responseMap.get("header"));
			responseData.put("items", new ArrayList<>());
			responseData.put("debugInfo", responseMap.get("debugInfo"));
			responseData.put("serviceStatusCode", e.getRawStatusCode());

//			if(membershipLatency > (responseMap.get("header")).get("responseTime"))
			return responseData;
		}

		Map<String, Object> serviceResponseBody = response.getBody();
		List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) serviceResponseBody.get("data")).get("items");
		List<String> debugInfo =  (List<String>) serviceResponseBody.get("debugInfo");
		Map<Integer, T> finalResponse = new HashMap<>();

		try {
			items.forEach(membershipDetail -> {
				T membership = null;
				try {
					membership = clazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				membership.setAttributes(membershipDetail);
				Integer pId = (Integer) membershipDetail.get("pid");
				finalResponse.put(pId, membership);
			});
		}catch(RuntimeException ex){
			log.error("Error in getting membership details {}" ,response.getStatusCode(),ex);
		}

		responseData.put("header", serviceResponseBody.get("header"));
		responseData.put("items", finalResponse);
		responseData.put("serviceStatusCode", response.getStatusCodeValue());
		responseData.put("debugInfo", debugInfo);

		return responseData;
	}

	public Map<String, Object> getContactData(Integer viewerId, Set<Integer> viewedIds,Boolean debugFlag,Boolean timingFlag,String requestID,Map<String,Object> versionMap) {
		String ids = StringUtils.join(viewedIds, ",");
		log.debug("**Contact**"+"Get contact data from RuleEngine"+viewerId);
		MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
		if(debugFlag) {
			queryParam.add("debug", "true");
		}

		if(timingFlag) {
			queryParam.add("timing","true");
		}


		queryParam.add("ids",ids);
		String reqID = requestID+":"+UUID.randomUUID().toString();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(httpConfiguration.getContactUrl() + "/jscontact/v1/contact");

		builder.queryParams(queryParam);

		HttpHeaders headers = new HttpHeaders();
		headers.set("viewer-id", String.valueOf(viewerId));
		headers.set("JS-Request-Id",reqID);
		headers.set(IODataConstants.VERSION_V0,(String)versionMap.get(IODataConstants.VERSION_V0));
		HttpEntity<String> entity = new HttpEntity<>(headers);



		ResponseEntity response=null;
		//Map<String, Object> serviceResponse
		try {
			 response= restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, entity, Map.class);
		}catch(HttpStatusCodeException e) {
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
			responseData.put("statusCode", e.getRawStatusCode());
			responseData.put("debugInfo", responseMap.get("debugInfo"));
			return responseData;
		}

		Map<String,Object> serviceResponse = (Map<String, Object>) response.getBody();
		Integer statusCode = response.getStatusCodeValue();
		Map<Integer, JsContact> finalResponse = new LinkedHashMap<>();

		List<Map<String, Object>> items = ((Map<String, List<Map<String, Object>>>) serviceResponse.get("data")).get("items");
		List<String> debugInfo =  (List<String>) serviceResponse.get("debugInfo");
		Iterator<Map<String, Object>> it = items.iterator();

		while (it.hasNext()) {
			Map<String, Object> data = it.next();
			Integer requester = (Integer) data.get("pg");
			Integer requestee = (Integer) data.get("pog");
			Integer key;
			key = (viewerId.equals(requester)) ? requestee : requester;
			JsContact jsContact = new JsContact();
			jsContact.setAttributes(data);
			finalResponse.put(key, jsContact);

		}
		log.debug("**Contact**"+"Final response from ContactService to rule engine : "+finalResponse);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("header", serviceResponse.get("header"));
		responseData.put("items", finalResponse);
		responseData.put("statusCode", response.getStatusCodeValue());
		responseData.put("debugInfo", debugInfo);

		return responseData;

	}

}