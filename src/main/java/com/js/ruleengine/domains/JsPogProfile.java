package com.js.ruleengine.domains;

import lombok.Getter;

public class JsPogProfile extends JsProfile {
	
	@Getter
	private Boolean filterPassed = null;
	
	public void addDppMatchStatus(String parameter, int passStatus) {	
		addOrUpdateField(passStatus, "dt", parameter);
	}

	public int getDppMatchStatus(String parameter) {
		Integer status = (Integer) getField("dt", parameter);
		if (status == null) {
			return -1;
		}
		return (int) getField("dt", parameter);
	}
}
