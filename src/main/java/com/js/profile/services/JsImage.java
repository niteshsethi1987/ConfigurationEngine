package com.js.profile.services;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class JsImage {
	 Map<String, Object> attributes;	
	
	public int getC(){
		return (Integer) attributes.get("c");
	}
	
}
