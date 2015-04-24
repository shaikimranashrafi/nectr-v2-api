package com.connectedworldservices.nectr.v2.api.rest.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;

public class EnvironmentRepositoryImpl implements EnvironmentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public EnvironmentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> findAllEnvironmentIds() {
        return mongoTemplate.getCollection(Environment.COLLECTION).distinct("_id");
    }

}
