package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.springframework.data.repository.CrudRepository;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;

public interface EnvironmentRepository extends CrudRepository<Environment, String>, EnvironmentRepositoryCustom {

}
