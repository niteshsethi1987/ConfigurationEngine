package com.js.ruleengine.nodes.contact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aerospike.client.Log;
import com.js.channels.HttpChannel;
import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.constants.RequestParams;
import com.js.ruleengine.domains.JsContact;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContactDataExtractor extends WorkNode {

	@Autowired
	private HttpChannel httpChannel;

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {
		Set<Integer> viewedIds = (Set<Integer>) context.getIOData(IODataConstants.VIEWED_PROFILE_IDS);
		Integer viewerId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);
		Boolean debugFlag = (Boolean)context.getIOData(IODataConstants.DEBUG_FLAG);
		Boolean timingFlag = (Boolean)context.getIOData(IODataConstants.TIMING_FLAG);
		String requestID = (String)context.getIOData(IODataConstants.REQUEST_ID);
		//Create an map for version 
		Map<String,Object> versionMap = new HashMap<>();
		versionMap.put(IODataConstants.VERSION_V0,(String)context.getIOData(IODataConstants.VERSION_V0));
		log.debug("**Contact**"+"viewedIds : "+viewedIds+" viewerId : "+viewerId);
		if (viewerId == null) {
			return true;
		}
		
		Map<String, Object> items = httpChannel.getContactData(viewerId, viewedIds,debugFlag,timingFlag,requestID,versionMap);
		if (items.get("debugInfo") != null) {
			((List) items.get("debugInfo")).forEach(i -> {
				context.setDebugData(this.getClass().getSimpleName() + ":" + i);
			});
		}
		context.setIOData("contactData", items);
		return true;
	}

}
