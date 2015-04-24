package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.springframework.data.repository.CrudRepository;

import com.connectedworldservices.nectr.v2.api.rest.model.ApplicationData;

public interface ApplicationDataRepository extends CrudRepository<ApplicationData, String> {

    public ApplicationData findByTestId(String testId);
}
