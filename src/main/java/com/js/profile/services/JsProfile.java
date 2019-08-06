package com.js.profile.services;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class JsProfile {
	Map<String, Map<String, Object>> attributes;	
	
	public int getAge(){
		return 25;
	}
	public void setAge(int n){
		attributes.get("bi").put("ag",new Integer(n));
	}
}
