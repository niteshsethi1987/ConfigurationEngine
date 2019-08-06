package com.js.profile.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.js.reuleengine.nodes.ProfileExtractor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import orchestrator.OrchestratorService;

/**
 * @author Esha
 */
@Slf4j
@Service
public class ProfileService {

	@Getter
	protected OrchestratorService osSelectProfileV1;
	
	@Autowired
	ProfileExtractor profileExtractor;

	@PostConstruct
	public void init() {
		log.info("====== Loading  ProfileService =====");
		loadProfileV1Orchestrator();
	}

	protected void loadProfileV1Orchestrator() {
		loadProfileV1();
	}

	


	protected void loadProfileV1() {
		log.info("====== Loading osProfileV1 =====");
		osSelectProfileV1 = new OrchestratorService();
		osSelectProfileV1.addExecutorNode(profileExtractor);
		
	}

	
	
}
