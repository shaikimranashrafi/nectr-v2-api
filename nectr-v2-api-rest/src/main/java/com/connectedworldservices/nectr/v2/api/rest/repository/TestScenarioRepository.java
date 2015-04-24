package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.springframework.data.repository.CrudRepository;

import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;

public interface TestScenarioRepository extends CrudRepository<TestScenario, String> {

}
