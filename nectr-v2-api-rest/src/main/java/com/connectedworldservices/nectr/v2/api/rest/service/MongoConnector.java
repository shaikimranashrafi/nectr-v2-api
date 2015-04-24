package com.connectedworldservices.nectr.v2.api.rest.service;

import static java.lang.String.format;

import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.mongodb.MongoClient;

@Component
public class MongoConnector {

    @Value("${enable.application-host:false}")
    private boolean applicationHostEnabled;

    public <T> List<T> connectAndFind(Host host, Query query, Class<T> entityClass) throws UnknownHostException {
        return connectAndFind(host, query, entityClass, host.getCollection());
    }

    public <T> List<T> connectAndAggregate(Host host, Aggregation aggregation, Class<T> entityClass) throws UnknownHostException {
        return connectAndAggregate(host, aggregation, entityClass, host.getCollection());
    }

    public <T> List<T> connectAndFind(Host host, Query query, Class<T> entityClass, String collection) throws UnknownHostException {
        return queryInternal(host, entityClass, collection, (MongoTemplate mongoTemplate) -> mongoTemplate.find(query, entityClass, collection));
    }

    public <T> List<T> connectAndAggregate(Host host, Aggregation aggregation, Class<T> entityClass, String collection) throws UnknownHostException {
        return queryInternal(host, entityClass, collection, (MongoTemplate mongoTemplate) -> mongoTemplate.aggregate(aggregation, collection, entityClass).getMappedResults());
    }

    private <T> List<T> queryInternal(Host host, Class<T> entityClass, String collection, Function<MongoTemplate, List<T>> func) throws UnknownHostException {
        MongoClient mongoClient = null;

        try {
            mongoClient = createMongoClient(host);
            if (!dbExists(mongoClient, host) || !collectionExists(mongoClient, host)) {
                throw new IllegalStateException(format("Can't retrieve data from host %s, either the dbName %s or collection %s don't exist.", host.getUrl(), host.getDbName(),
                        host.getCollection()));
            }

            return func.apply(createMongoTemplate(mongoClient, host));
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public <T> T connectAndFindOne(Host host, Query query, Class<T> entityClass) throws UnknownHostException {
        return connectAndFindOne(host, query, entityClass, host.getCollection());
    }

    public <T> T connectAndFindOne(Host host, Query query, Class<T> entityClass, String collection) throws UnknownHostException {
        query.limit(1);
        List<T> results = connectAndFind(host, query, entityClass, collection);
        return results.isEmpty() ? null : results.get(0);
    }

    protected MongoTemplate createMongoTemplate(MongoClient mongoClient, Host host) {
        return new MongoTemplate(mongoClient, host.getDbName());
    }

    protected MongoClient createMongoClient(Host host) throws UnknownHostException {
        return new MongoClient(host.serverAddressList(), host.credentialsList(), host.clientOptions());
    }

    protected boolean dbExists(MongoClient mongoClient, Host host) {
        return mongoClient.getDatabaseNames().contains(host.getDbName());
    }

    protected boolean collectionExists(MongoClient mongoClient, Host host) {
        if (dbExists(mongoClient, host)) {
            return mongoClient.getDB(host.getDbName()).collectionExists(host.getCollection());
        }
        return false;
    }

    public boolean isApplicationHostEnabled() {
        return applicationHostEnabled;
    }

    public void setApplicationHostEnabled(boolean value) {
        this.applicationHostEnabled = value;
    }

}
