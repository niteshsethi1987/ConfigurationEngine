/**
 *
 */
package com.js.ruleengine.nodes.profile;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.constants.ViewType;
import com.js.ruleengine.domains.JsPogProfile;

/**
 * @author goutam.mandal
 */
@Service
public class ProfileResponseRefiner extends WorkNode {

	@Value("#{${profile.view.vcard}}")
	private Map<String, String> vcard;
	@Value("#{${profile.view.basic}}")
	private Map<String, String> basic;
	@Value("#{${profile.view.shortview}}")
	private Map<String, String> shortView;
	@Value("#{${profile.view.pd}}")
	private Map<String, String> pd;
	@Value("#{${profile.view.logout}}")
	private Map<String, String> logout;
	@Value("#{${profile.view.super}}")
	private Map<String, String> superView;

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {

		Map<Integer, JsPogProfile> pogProfileMap = (Map<Integer, JsPogProfile>) context.getIOData("pogProfileMap");
		String viewType = (String) context.getIOData(IODataConstants.PROFILE_VIEW_TYPE);
		Map<String, List<String>> viewMap = loadViewMapping(viewType);

		pogProfileMap.forEach((profileId, pogProfileData) -> {
			// Continue in case of Business Error (no data)
			Map profileAttributes = pogProfileData.getAttributes();
			if (profileAttributes.get("be") != null) {
				return;
			}
			// Remove not required sections
			profileAttributes.entrySet().removeIf(e -> !viewMap.containsKey(((Entry) e).getKey()));
			// Remove not required section details
			profileAttributes.forEach((sectionName, sectionDetails) -> {
				if (sectionDetails == null || !(sectionDetails instanceof Map) || ((Map) sectionDetails).get("be") != null) {
					return;
				}
				((Map) sectionDetails).entrySet().removeIf(e -> viewMap.get(sectionName) != null && !viewMap.get(sectionName).contains(((Entry) e).getKey()));
			});
		});

		return true;
	}
/*
	private void printAllViews() {
		System.out.println("profile.view." + ViewType.BASIC + " = " + createView(ViewType.BASIC));
		System.out.println("");
		System.out.println("profile.view." + ViewType.VCARD + " = " + createView(ViewType.VCARD));
		System.out.println("");
		System.out.println("profile.view." + ViewType.SHORT + " = " + createView(ViewType.SHORT));
		System.out.println("");
		System.out.println("profile.view." + ViewType.SUPER + " = " + createView(ViewType.SUPER));
		System.out.println("");
		System.out.println("profile.view." + ViewType.PD + " = " + createView(ViewType.PD));
		System.out.println("");
		System.out.println("profile.view." + ViewType.LOGOUT + " = " + createView(ViewType.LOGOUT));
		System.out.println("END");
	}

	private String createView(String viewType) {
		Map<String, Map<String, String>> sectionChangeMapping = ProfileResultsMapping.getMapping();

		Map<String, List<String>> mapping = loadViewMapping(viewType);
		for (Entry<String, List<String>> entry : mapping.entrySet()) {
			String sectionName = entry.getKey();

			List<String> keys = entry.getValue();
			List<String> longKeys = new ArrayList<>();
			Map<String, String> keyMapping = ViewMapping.viewReverseMap.get(sectionName);
			if (Arrays.asList("aph","last_active","cfg","be").contains(sectionName)) {
				longKeys.addAll(keys);
				entry.setValue(longKeys);
				keyMapping = null;
			}
			if (keyMapping == null && !Arrays.asList("fi","lo","ca","ei","li","ai").contains(sectionName)) {
				if ("id".equals(sectionName)) {
					longKeys.add("profileid");
					longKeys.add("username");
					longKeys.add("profilechecksum");
					entry.setValue(longKeys);
				}
			} else {
				for (String key : keys) {
					String longKey = null;
					if ("be".equals(key)) {
						longKeys.add("be");
					} else if (keyMapping != null && keyMapping.get(key) != null) {
						longKey = keyMapping.get(key);
						longKeys.add(longKey);
					} else {
						if (sectionChangeMapping.get(sectionName) != null && sectionChangeMapping.get(sectionName).get(key) != null) {
							String mappedSection = sectionChangeMapping.get(sectionName).get(key);
							longKey = ViewMapping.viewReverseMap.get(mappedSection).get(key);
							if (longKey != null) {
								longKeys.add(longKey);
							} else {
								System.out.println("NULL:" + sectionName + key);
							}
						}
					}
				}
				entry.setValue(longKeys);
			}
		}
		mapping.remove("csi");
		mapping.remove("ni");
		mapping.remove("phs");
		mapping.remove("phns");
		return generateView(mapping);
	}
*/
	private static String generateView(Map<String, List<String>> mapping) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (Entry<String, List<String>> viewEntry : mapping.entrySet()) {
			builder.append(viewEntry.getKey()).append(":'");
			for (String key : viewEntry.getValue()) {
				builder.append(key).append(",");
			}
			builder.replace(builder.length() - 1, builder.length(), "',");
		}
		builder.replace(builder.length() - 1, builder.length(), "");
		builder.append("}");
		return builder.toString();
	}

	private Map<String, List<String>> loadViewMapping(String view) {
		Map<String, List<String>> mapViewFromProperties = null;
		switch (view) {
			case ViewType.BASIC:
				mapViewFromProperties = getTokenizedList(basic);
				break;
			case ViewType.VCARD:
				mapViewFromProperties = getTokenizedList(vcard);
				break;
			case ViewType.SHORT:
				mapViewFromProperties = getTokenizedList(shortView);
				break;
			case ViewType.SUPER:
				mapViewFromProperties = getTokenizedList(superView);
				break;
			case ViewType.PD:
				mapViewFromProperties = getTokenizedList(pd);
				break;
			case ViewType.LOGOUT:
				mapViewFromProperties = getTokenizedList(logout);
				break;
		}
		return mapViewFromProperties;
	}

	private Map<String, List<String>> getTokenizedList(Map<String, String> map) {
		Map<String, List<String>> tokenizedMap = new LinkedHashMap<>();
		map.forEach((sectionName, sectionkeys) -> tokenizedMap.put(sectionName, Arrays.asList(sectionkeys.split(","))));
		return tokenizedMap;
	}
}
