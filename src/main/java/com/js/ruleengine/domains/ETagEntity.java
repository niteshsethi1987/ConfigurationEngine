package com.js.ruleengine.domains;

import lombok.Data;

import org.springframework.data.annotation.Id;

@Data
public class ETagEntity {
	@Id
	private String id;
	private String eTag;
	private String obj;
}