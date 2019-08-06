/**
 *
 */
package com.js.ruleengine.nodes.profile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import orchestrator.IOrchestratorContext;
import orchestrator.ResponseBodyHeader;
import orchestrator.WorkNode;

import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsContact;
import com.js.ruleengine.domains.JsPgMembership;
import com.js.ruleengine.domains.JsPgProfile;
import com.js.ruleengine.domains.JsPogProfile;

/**
 * @author lavesh
 */
@Service
public class ProfileResponseCreator extends WorkNode {

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {

		Map<Integer, JsPogProfile> pogProfileMap = (Map<Integer, JsPogProfile>) context.getIOData("pogProfileMap");
		Map<String, Object> responseData = new LinkedHashMap<>();
		responseData.put("count", pogProfileMap.size());
		List<Map> items = new ArrayList<>(pogProfileMap.size());

		// Start contactInfo(HoroScope & PhotoRequest)
		Map<Integer, JsContact> contactDataMap = (Map<Integer, JsContact>) context.getIOData("contactData");

			pogProfileMap.forEach((profileID, pogProfile) -> {
				boolean photoFlag;
				boolean hrFlag;
				if (contactDataMap != null) {
					photoFlag = contactDataMap.get(pogProfile.getField("id", "profileid")).getField("pgPrDt") != null ? true
							: false;
					hrFlag = contactDataMap.get(pogProfile.getField("id", "profileid")).getField("pgHrDt") != null ? true
							: false;
					pogProfile.getAttributes().put("phRq", photoFlag);
					pogProfile.getAttributes().put("hrRq", hrFlag);
				}else {
					pogProfile.getAttributes().put("phRq", null);
					pogProfile.getAttributes().put("hrRq", null);
				}

			});

		// end

		pogProfileMap.forEach((profileId, pogProfileData) -> items.add(pogProfileData.getAttributes()));

		responseData.put("items", items);

		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		if (loggedInId != null) {
			Map<String, Object> viewerData = new LinkedHashMap<>();

			JsPgProfile pgProfile = (JsPgProfile) context.getIOData("pgProfile");
			Map<String, Object> id = new LinkedHashMap<>();
			id.put("profileid", pgProfile.getField("id", "profileid"));
			id.put("username", pgProfile.getField("id", "username"));
			id.put("profilechecksum", pgProfile.getField("id", "profilechecksum"));
			Map<String, Object> basicInfo = new LinkedHashMap<>();
			basicInfo.put("activated", pgProfile.getField("bi", "activated"));
			basicInfo.put("age", pgProfile.getField("bi", "age"));
			basicInfo.put("caste", pgProfile.getField("bi", "caste"));
			basicInfo.put("gender", pgProfile.getField("bi", "gender"));
			basicInfo.put("email", pgProfile.getField("bi", "email"));
			basicInfo.put("income", pgProfile.getField("bi", "income"));
			basicInfo.put("mtongue", pgProfile.getField("bi", "mtongue"));
			basicInfo.put("mstatus", pgProfile.getField("bi", "mstatus"));
			basicInfo.put("religion", pgProfile.getField("bi", "religion"));
			Map<String, Object> astroInfo = new LinkedHashMap<>();
			astroInfo.put("present", pgProfile.getField("ai", "ki") == null ? false : true);
			astroInfo.put("ki", pgProfile.getField("ai", "ki"));
			Map<String, Object> locationInfo = new LinkedHashMap<>();
			locationInfo.put("cityRes", pgProfile.getField("lo", "cityRes"));
			locationInfo.put("countryRes", pgProfile.getField("lo", "countryRes"));

			viewerData.put("id", id);
			viewerData.put("bi", basicInfo);
			viewerData.put("ai", astroInfo);
			viewerData.put("lo", locationInfo);

			JsPgMembership pgMembership = (JsPgMembership) context.getIOData("pgMembership");
			Map<String, Object> subscription = new LinkedHashMap<>();
			subscription.put("paid", pgMembership.getField("paid"));
			subscription.put("mm", pgMembership.getField("mmem"));
			subscription.put("aom", pgMembership.getField("addOns"));
			viewerData.put("sb", subscription);

			Map<String, Object> aphSection = new LinkedHashMap<>();

 			if ( pgProfile.getField("aph","items") != null && ((ArrayList<Object>)pgProfile.getField("aph","items")).get(0) != null )
 			{
 				aphSection.put("selfThumbail", ((Map<String, Object>)((ArrayList<Object>)pgProfile.getField("aph","items")).get(0)).get("thumbailUrl"));
 				viewerData.put("aph", aphSection);
 			}


			responseData.put("viewerData", viewerData);
		}

		context.setResponseBodyHeader(new ResponseBodyHeader(200, null));
		context.setResponseBodyData(responseData);

		return true;

	}

}
