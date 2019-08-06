/**
 * 
 */
package com.js.ruleengine.constants;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

/**
 * TODO Review of the privacy options
 * @author goutam.mandal
 */
public enum PhotoShowStatus {
	
	/**
	 * yes
	 */
	SHOW(100, "Show. Privacy check passed."),
	
	/**
	 * filteredPhoto
	 */
	DPP_FILTER(101, "DPP match filter not passed"),
	
	/**
	 * contactAcceptedPhoto
	 */
	ON_INTEREST_ACCEPTANCE(102, "Show on acceptance of interest"),
	
	/**
	 * underScreeningPhoto
	 */
	UNDER_SCREENING(103, "Photos under Screening"),
	
	/**
	 * nonLoggedInPhoto
	 */
	SHOW_LOGGED_IN(104, "Show to logged in users only"),
	
	/**
	 * requestPhoto
	 */
	NO_PHOTO(105, "Photo not available"),
	
	/**
	 * Logged in, not subscribed & photo not uploaded within 15 days of profile activation
	 */
	SELF_PHOTO_NOT_UPLOADED(106, "Self photo not uploaded"),
	
	/**
	 * For case not matching in {@link PictureLogicEnums}
	 */
	UNKNOWN_REASON(-1, "No matching picture logic case"),
	;
	
	@Getter
	private int errorCode = -1;
	
	@Getter
	private String errorMsg = "";
	
	private PhotoShowStatus(int errorCode, String defaultMsg) {
		this.errorCode = errorCode;
		this.errorMsg = defaultMsg;
	}
	
	public Map<String, Object> asMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("errorCode", errorCode);
		map.put("errorMsg", errorMsg);
		return map;
	}
	
}
