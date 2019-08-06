package com.js.ruleengine.domains;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@ToString(callSuper = true)
public class JsContact extends RemoteEntity implements JsContactButtonState {
	@Getter
	private String contactButtonState;
	
	@Override
	public void setContactButtonState(String contactButtonState) {
		// TODO Auto-generated method stub
		log.debug("contactDataMap=="+contactButtonState);
		this.contactButtonState=contactButtonState;
		}

}
