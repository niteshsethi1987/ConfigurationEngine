package com.js.ruleengine.nodes.membership.plan;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.WorkNode;
import orchestrator.exception.ArgumentException;

@Slf4j
@Service
public class MembershipPlanQueryTermValidator extends WorkNode {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {
		log.debug("====== executing QueryTermValidator =====");

		List<String> secids = (List<String>) context.getIOData(IODataConstants.SEC_IDS);
		if (null == secids) {
			throw new ArgumentException("SecId field blank");
		}

		List<Integer> secidsNumeric = new ArrayList<>();
		secids.stream().forEach(s -> {
			if (s.length() > 9 || !s.matches("[0-9]+")) {
				throw new ArgumentException("Invalid SecId {" + s + "}");
			}
			secidsNumeric.add(Integer.valueOf(s));
		});

		context.setIOData(IODataConstants.SEC_IDS, secidsNumeric);
		return true;
	}
}
