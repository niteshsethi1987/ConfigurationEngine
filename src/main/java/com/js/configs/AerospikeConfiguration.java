package com.js.configs;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;

@Configuration
@EnableAerospikeRepositories(basePackages = { "com.js.ruleengine.repository" })
@EnableAutoConfiguration
@EnableTransactionManagement
@ConfigurationProperties("aerospike")
public class AerospikeConfiguration {
	@Getter
	@Setter
	private List<String> hosts;

	@Getter
	@Setter
	private List<Integer> ports;

	@Getter
	@Setter
	private String namespace; // default

	@Getter
	@Setter
	private int ttl;

	@Getter
	@Setter
	private String user;

	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private int maxConnsPerNode;

	public @Bean(destroyMethod = "close") AerospikeClient aerospikeClient() {
		ClientPolicy clientPolicy = new ClientPolicy();
		clientPolicy.timeout = ttl;
		clientPolicy.maxConnsPerNode = maxConnsPerNode;

		if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)) {
			clientPolicy.user = user;
			clientPolicy.password = password;
		}

		List<Host> hostsList = new ArrayList<>(hosts.size());
		for (int i = 0; i < hosts.size() && i < ports.size(); i++) {
			hostsList.add(new Host(hosts.get(i), ports.get(i)));
		}

		Host[] hostsArray = new Host[hostsList.size()];
		return new AerospikeClient(clientPolicy, hostsList.toArray(hostsArray));
	}

	public @Bean AerospikeTemplate aerospikeTemplate() {
		return new AerospikeTemplate(aerospikeClient(), namespace);
	}
}
