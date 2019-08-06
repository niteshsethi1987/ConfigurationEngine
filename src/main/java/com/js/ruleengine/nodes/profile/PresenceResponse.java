package com.js.ruleengine.nodes.profile;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPogProfile;
import com.js.ruleengine.domains.JsPresence;
import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PresenceResponse extends WorkNode {
    public boolean execute(IOrchestratorContext context) throws Exception {
        Map<Integer, JsPogProfile> pogProfileMap = (Map<Integer, JsPogProfile>) context.getIOData("pogProfileMap");
        Map<Integer, JsPresence> presenceData = (Map<Integer, JsPresence>) context.getIOData(IODataConstants.PROFILE_PRESENCE_DATA);
        if(presenceData == null){
            log.info("Presence Data is null in Rule Engine:"+presenceData);
            pogProfileMap.forEach((profileId, pogProfile) -> {
                pogProfile.getAttributes().put("last_active_before",null);
                pogProfile.getAttributes().put("online",false);
            });
        }
        else{
            log.debug("Presence Data : {}", presenceData);
            pogProfileMap.forEach((profileId, pogProfile) -> {
                if(presenceData!=null) {
                    pogProfile.addOrUpdateField(presenceData.get(pogProfile.getField("id", "profileid")).getField("last_active_before"), "pr", "last_active_before");
                    pogProfile.addOrUpdateField(presenceData.get(pogProfile.getField("id", "profileid")).getField("online"), "pr", "online");
                }
//                pogProfile.getAttributes().put("last_active_before",presenceData.get(pogProfile.getField("id", "profileid")).getField("last_active_before"));
//                pogProfile.getAttributes().put("online",presenceData.get(pogProfile.getField("id", "profileid")).getField("online"));

            });
        }


        return true;
    }
}
