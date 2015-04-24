package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.connectedworldservices.nectr.v2.api.rest.model.TestReplayHistory;

@Service
public interface TestReplayHistoryRepository extends CrudRepository<TestReplayHistory, String> {

}
