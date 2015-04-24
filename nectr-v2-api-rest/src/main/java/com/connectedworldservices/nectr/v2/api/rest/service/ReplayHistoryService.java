package com.connectedworldservices.nectr.v2.api.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.connectedworldservices.nectr.v2.api.rest.model.TestReplayHistory;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;

@Service
public class ReplayHistoryService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public ReplayHistoryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<TestReplayHistory> loadTestReplayHistory(String testId) {
        return mongoTemplate.find(Queries.searchByTestId(testId), TestReplayHistory.class);
    }

}
