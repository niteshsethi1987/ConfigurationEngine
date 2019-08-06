package com.js.ruleengine.repository;

import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.stereotype.Repository;

import com.js.ruleengine.domains.ETagEntity;

@Repository
public interface EtagRepository extends AerospikeRepository<ETagEntity, String> {

}
