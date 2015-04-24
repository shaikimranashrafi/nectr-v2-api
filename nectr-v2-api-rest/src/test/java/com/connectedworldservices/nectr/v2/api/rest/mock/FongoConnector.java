package com.connectedworldservices.nectr.v2.api.rest.mock;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fakemongo.Fongo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class FongoConnector extends MongoConnector {

    private boolean throwUnknownHostExceptionOnIntegrationConnection = false;

    private boolean throwUnknownHostExceptionOnApplicationConnection = false;

    private boolean throwExceptionOnCreateMongoTemplate = false;

    private boolean dbExists = true;

    private boolean collectionExists = true;

    @Autowired
    private ObjectMapper objectMapper;

    private MockedConnection<MockedIntegrationDocument> integrationConnection;

    private MockedConnection<MockedApplicationDocument> applicationConnection;

    public FongoConnector(String integrationUrl, String integrationDbName) {
        this(integrationUrl, integrationDbName, null, null);
    }

    public FongoConnector(String integrationUrl, String integrationDbName, String applicationUrl, String applicationDbName) {
        integrationConnection = new MockedConnection<>(integrationUrl, integrationDbName);

        if (!isEmpty(applicationUrl) && !isEmpty(applicationDbName)) {
            applicationConnection = new MockedConnection<>(applicationUrl, applicationDbName);
        }
    }

    @Override
    protected MongoClient createMongoClient(Host host) throws UnknownHostException {
        UnknownHostException exception = new UnknownHostException(format("[throwUnknownHostExceptionOnIntegrationConnection=%s]", throwUnknownHostExceptionOnIntegrationConnection));

        for (MockedConnection<?> mockedConnection : asList(integrationConnection, applicationConnection)) {
            if (mockedConnection == null) {
                continue;
            }

            if (mockedConnection == integrationConnection && throwUnknownHostExceptionOnIntegrationConnection) {
                throw exception;
            }

            if (mockedConnection == applicationConnection && throwUnknownHostExceptionOnApplicationConnection) {
                throw exception;
            }

            MongoClient mongoClient = mockedConnection.getMongoClient();

            if (format("Fongo (%s)", host.getUrl()).equals(mongoClient.toString())) {
                return mongoClient;
            }
        }

        throw exception;
    }

    @Override
    protected MongoTemplate createMongoTemplate(MongoClient mongoClient, Host host) {
        IllegalStateException exception = new IllegalStateException(format("[throwExceptionOnCreateMongoTemplate=%s]", throwExceptionOnCreateMongoTemplate));

        if (throwExceptionOnCreateMongoTemplate) {
            throw exception;
        }

        for (MockedConnection<?> mockedConnection : asList(integrationConnection, applicationConnection)) {
            if (mockedConnection == null) {
                continue;
            }

            if (mongoClient == mockedConnection.getMongoClient()) {
                return mockedConnection.getMongoTemplate();
            }
        }

        throw exception;
    }

    @Override
    protected boolean dbExists(MongoClient mongoClient, Host host) {
        return dbExists;
    }

    @Override
    protected boolean collectionExists(MongoClient mongoClient, Host host) {
        return collectionExists;
    }

    public void loadIntegrationData(String path, String collection, boolean reload) throws IOException {
        List<MockedIntegrationDocument> documents = objectMapper.readValue(classpathResourceAsStream(path), new TypeReference<List<MockedIntegrationDocument>>() {
        });

        integrationConnection.loadDocuments(documents, collection, reload);
    }

    public void loadApplicationData(String path, String collection, boolean reload) throws IOException {
        if (applicationConnection != null) {
            List<MockedApplicationDocument> documents = objectMapper.readValue(classpathResourceAsStream(path), new TypeReference<List<MockedApplicationDocument>>() {
            });

            applicationConnection.loadDocuments(documents, collection, reload);
        }
    }

    public void setDbExists(boolean dbExists) {
        this.dbExists = dbExists;
    }

    public void setCollectionExists(boolean collectionExists) {
        this.collectionExists = collectionExists;
    }

    public void setThrowUnknownHostExceptionOnIntegrationConnection(boolean throwException) {
        this.throwUnknownHostExceptionOnIntegrationConnection = throwException;
    }

    public void setThrowUnknownHostExceptionOnApplicationConnection(boolean throwException) {
        this.throwUnknownHostExceptionOnApplicationConnection = throwException;
    }

    public void setThrowExceptionOnCreateMongoTemplate(boolean throwExceptionOnCreateMongoTemplate) {
        this.throwExceptionOnCreateMongoTemplate = throwExceptionOnCreateMongoTemplate;
    }

    public void clearThrowUnknownHostExceptionOnIntegrationConnection() {
        setThrowUnknownHostExceptionOnIntegrationConnection(false);
    }

    public void clearThrowUnknownHostExceptionOnApplicationConnection() {
        setThrowUnknownHostExceptionOnApplicationConnection(false);
    }

    public void clearThrowExceptionOnCreateMongoTemplate() {
        setThrowExceptionOnCreateMongoTemplate(false);
    }

    public void clearDbExists() {
        setDbExists(true);
    }

    public void clearCollectionExists() {
        setCollectionExists(true);
    }

    private InputStream classpathResourceAsStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    @Data
    private static class MockedConnection<T> {
        private final String url;
        private final String dbName;
        private final MongoClient mongoClient;
        private final MongoTemplate mongoTemplate;

        MockedConnection(String url, String dbName) {
            this.url = url;
            this.dbName = dbName;
            this.mongoClient = new Fongo(url).getMongo();
            this.mongoTemplate = new MongoTemplate(mongoClient, dbName);
        }

        void loadDocuments(List<T> documents, String collection, boolean reload) throws IOException {
            MongoTemplate mongoTemplate = getMongoTemplate();

            if (reload) {
                mongoTemplate.dropCollection(collection);
            }

            for (T document : documents) {
                DBObject dbObject = new BasicDBObject();
                mongoTemplate.getConverter().write(document, dbObject);

                MongoClient mongoClient = getMongoClient();
                mongoClient.getDB(getDbName()).getCollection(collection).insert(dbObject);
            }
        }
    }

    @Data
    private static class MockedIntegrationDocument {
        @JsonProperty("_id")
        private String id;
        private Date timestamp;
        private String journeyId;
        private String resource;
        private String clientId;
        private String carrier;
        private String country;
        private String journeyName;
        private String payload;
        private String messageLocation;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    private static class MockedApplicationDocument extends HashMap<String, Object> {
        private static final long serialVersionUID = 1L;
        @JsonProperty("_id")
        private String id;
        private String journeyId;
        private Date createdTimestamp;
        private Date updatedTimestamp;
        private List<String> resourceUris;
    }
}
