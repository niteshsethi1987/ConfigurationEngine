package com.js.ruleengine.nodes.profile;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.JoinWorkNode;
import orchestrator.exception.ArgumentException;
import orchestrator.exception.ResourceException;

import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsPgMembership;

/**
 * @author lavesh
 */
@Slf4j
@Service
public class ProfileRuleEngineObjectVerifier extends JoinWorkNode {

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(IOrchestratorContext context) throws Exception {

		Integer loggedInId = (Integer) context.getIOData(IODataConstants.LOGGEDIN_ID);

		if (loggedInId != null) {
			Map<String, Object> pgProfileMap = (Map<String, Object>) context.getIOData("pgProfile");
			if (context.getIOData("pgProfile") == null) {
				throw new ResourceException("pgProfile is null");
			} else if ((int) pgProfileMap.get("serviceStatusCode") != 200) {
				switch ((int) pgProfileMap.get("serviceStatusCode")) {
				case 400:
				case 4000:
					throw new ArgumentException("400 error detected in profile service for pgProfile");
				default:
					throw new ResourceException((int) pgProfileMap.get("serviceStatusCode") + " error code detected in profile service for pgProfile");

				}
			}
			context.setIOData("pgProfile", ((Map<String, Object>) pgProfileMap.get("data")).get(loggedInId));

			Map<String, Object> contactMap = (Map<String, Object>) context.getIOData("contactData");
			if (context.getIOData("contactData") == null) {
				throw new ResourceException("contactData is null");
			}else if((int) contactMap.get("statusCode")!=200) {
				switch((int)contactMap.get("statusCode")) {
					case 400:
					throw new ArgumentException("");
					case 500:
						throw new ResourceException("");
				}
			}
			context.setIOData("contactData", contactMap.get("items"));
			}

		Map<String, Object> pogProfileMap = (Map<String, Object>) context.getIOData("pogProfileMap");
		if (context.getIOData("pogProfileMap") == null) {
			throw new ResourceException("pogProfileMap is null");
		} else if ((int) pogProfileMap.get("serviceStatusCode") != 200) {
			switch ((int) pogProfileMap.get("serviceStatusCode")) {
				case 400:
				case 4000:
					throw new ArgumentException("400 error detected in profile service for pogProfile");
				default:
					throw new ResourceException((int) pogProfileMap.get("serviceStatusCode") + " error code detected in profile service for pogProfile");

			}
		}
		context.setIOData("pogProfileMap", pogProfileMap.get("data"));



		// validate POG Membership Object
        Map<String, Object> pogMembership = (Map<String, Object>) context.getIOData("pogMembershipMap");

		if (pogMembership == null) {
			throw new ResourceException("pogMembership is null");
		}
		else if ( (int) pogMembership.get("serviceStatusCode") != 200 ) {
			int pogMembershipServiceCode = (int) pogMembership.get("serviceStatusCode");
			switch( pogMembershipServiceCode ) {

			case 400:
				throw new ArgumentException("400 error detected in membership service for pogMembership");
			case 500:
				throw new ResourceException("500 error detected in membership service for pogMembership");
			}
		}
		context.setIOData("pogMembershipMap", pogMembership.get("items"));

		// validate Pg Membership object
		Map<String, Object> pgMembership = (Map<String, Object>) context.getIOData("pgMembership");

		if(loggedInId != null) {

			if(pgMembership == null) {
				throw new ResourceException("LoggedIn id detected but Membership returned null");
			}

			else if ( (int) pgMembership.get("serviceStatusCode") != 200) {

				int pgMembershipServiceCode = (int) pgMembership.get("serviceStatusCode");
				switch( pgMembershipServiceCode) {
				case 400:
					throw new ArgumentException("400 error detected in membership service for pgMembership");
				case 500:
					throw new ResourceException("500 error detected in membership service for pgMembership");
				}
			}

			Set<Integer> loggedInIds = new LinkedHashSet<>();
			loggedInIds.add(loggedInId);

			Map<Integer, JsPgMembership> pgMembershipMap = (Map<Integer, JsPgMembership>)pgMembership.get("items");
			JsPgMembership pgMembershipFinal = pgMembershipMap.get(loggedInId);
			context.setIOData("pgMembership", pgMembershipFinal);
		}

		return true;
	}
}