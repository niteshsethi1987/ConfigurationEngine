package com.js.ruleengine.nodes.profile;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.springframework.stereotype.Service;

import com.js.ruleengine.domains.JsContact;
import com.js.ruleengine.domains.JsPogProfile;

@Slf4j
@Service
public class ContactButtonResponseCreator extends WorkNode {

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {

		Map<Integer, JsPogProfile> pogProfileMap = (Map<Integer, JsPogProfile>) context.getIOData("pogProfileMap");
		Map<Integer, JsContact> contactDataMap = (Map<Integer, JsContact>) context.getIOData("contactData");

		if (contactDataMap==null ) {
			log.debug("**Contact**"+"contactDataMap==null");
			pogProfileMap.forEach((profileId, pogProfile) -> {
				pogProfile.getAttributes().put("cBtn","NO_CONTACT");
				Map<String,Object> contactMap = new HashMap<>();
				contactMap.put("state",null);
				contactMap.put("stateSeen",null);
				pogProfile.getAttributes().put("contactMap",contactMap);
			});
		} else {
			log.debug("**Contact**"+"contactDataMap=="+contactDataMap.toString());
			pogProfileMap.forEach((profileId, pogProfile) -> {
				pogProfile.getAttributes().put("cBtn",
						contactDataMap.get(pogProfile.getField("id", "profileid")).getContactButtonState());
				String contactState = (String)contactDataMap.get(pogProfile.getField("id", "profileid")).getField("state");
				if("RI".equals(contactState)) {
					pogProfile.getAttributes().put("eoiMessage",
							contactDataMap.get(pogProfile.getField("id", "profileid")).getField("message"));
					pogProfile.getAttributes().put("eoi_msg_dt",
							contactDataMap.get(pogProfile.getField("id", "profileid")).getField("eoiMsgDate"));
				}
				pogProfile.getAttributes().put("isBookmark",
						contactDataMap.get(pogProfile.getField("id", "profileid")).getField("pgShDt"));
				pogProfile.getAttributes().put("isIgnored",
						contactDataMap.get(pogProfile.getField("id", "profileid")).getField("pgBlDt"));

				Map<String,Object> contactMap = new HashMap<>();
				contactMap.put("state",contactDataMap.get(pogProfile.getField("id", "profileid")).getField("state") );
				contactMap.put("stateSeen",contactDataMap.get(pogProfile.getField("id", "profileid")).getField("stateSeen"));
				contactMap.put("stateDate",contactDataMap.get(pogProfile.getField("id", "profileid")).getField("ctDt"));
				pogProfile.getAttributes().put("contactMap",contactMap);
			});
		}

		return true;
	}
}
