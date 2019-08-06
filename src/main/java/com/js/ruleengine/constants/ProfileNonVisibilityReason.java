package com.js.ruleengine.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * @author lavesh
 */
public enum ProfileNonVisibilityReason {

	HIDDEN(101, "HIDDEN"),

	DELETED(102, "DELETED"),

	UNDERSCREENING(103, "UNDERSCREENING"),

	NOT_ACTIVATED(104, "NOT_ACTIVATED"),

	EMPTY(105, "EMPTY"),

	FILTERED(106, "FILTERED"),

	LOGIN_MUST_REGISTERED(107, "LOGIN_MUST"),

	PROFILE_NOT_VALID(108, "PROFILE_NOT_EXISTS"),

	BLOCKED(109, "BLOCKED"),

	INCOMPLETE(110, "PROFILE_INCOMPLETE"),

	MARK_PHONE_NOT_VERFIIED(111, "PROFILE_PHONR_NOT_VERFIED"),

	INVALID_USERNAME(112, "INVALID_USERNAME"),

	// 113 - Invalid profile id

	SHOW_CONTACTED(114, "PRIVACY_SHOW_CONTACTED"),

	PG_PROFILE_INCOMPLETE(115, "PG_PROFILE_INCOMPLETE"),

	PG_PROFILE_DELETED(116, "PG_PROFILE_DELETED"),

	DELETED_BACKEND(117, "DELETED_BACKEND"),
	;

	@Getter
	private final Integer	eCode;
	@Getter
	private final String	defaultMsg;

	/**
	 * @param eCode
	 * @param defaultMsg
	 */
	private ProfileNonVisibilityReason(Integer eCode, String defaultMsg) {
		this.eCode = eCode;
		this.defaultMsg = defaultMsg;
	}

	public Map<String, Object> asMap() {
		Map<String, Object> errMap = new HashMap<>();
		errMap.put("ecode", this.getECode());
		errMap.put("msg", this.getDefaultMsg());
		return errMap;
	}
}