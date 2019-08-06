package com.js.reuleengine.nodes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.channels.HttpChannel;
import com.js.profile.services.JsProfile;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.ResponseBodyHeader;
import orchestrator.ServiceResponse;
import orchestrator.WorkNode;

@Slf4j
@Service
public class ProfileExtractor extends WorkNode {

	@Autowired
	private HttpChannel httpChannel;

	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		JsProfile p = httpChannel.getProfiles();
		
		
		
		
		ServiceResponse a = new ServiceResponse();
		a.setResponseBodyHeader(new ResponseBodyHeader(200, ""));
		a.setResponseBodyData(p);
		context.setResponseBodyData(a);
		return true;
	}
}