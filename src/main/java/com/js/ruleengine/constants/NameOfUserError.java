package com.js.ruleengine.constants;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

/**
 * @author goutam.mandal
 */
public enum NameOfUserError {

	POG_NAME_HIDDEN(101, "Name not shown to others"),

	SELF_NAME_NOT_FILLED(102, "Self name not filled"),
	
	SELF_NAME_NOT_SCREENED(106, "Self name under screening"),

	SELF_NAME_HIDDEN(103, "Self name not shown to others"),

	POG_NAME_NOT_FILLED(104, "Name not filled "),
	
	POG_NAME_NOT_SCREENED(107, "Name not Screened"),

	NOT_LOGGED_IN(105, "Name not shown to Guest Users"),

	;

	@Getter
	private final Integer errorCode;
	@Getter
	private final String defaultMsg;

	/**
	 * @param errorCode
	 * @param defaultMsg
	 */
	private NameOfUserError(Integer errorCode, String defaultMsg) {
		this.errorCode = errorCode;
		this.defaultMsg = defaultMsg;
	}

	/**
	 * @return
	 */
	public Map<String, Object> asMap() {
		Map<String, Object> errMap = new LinkedHashMap<>();
		errMap.put("ecode", this.getErrorCode());
		errMap.put("msg", this.getDefaultMsg());
		return errMap;
	}
}