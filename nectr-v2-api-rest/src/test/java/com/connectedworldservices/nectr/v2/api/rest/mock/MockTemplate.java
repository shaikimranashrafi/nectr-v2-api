package com.connectedworldservices.nectr.v2.api.rest.mock;

import static java.lang.String.format;

import java.util.List;

import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MockTemplate extends MongoTemplate {

    private boolean throwExceptionOnSave = false;
    private boolean throwExceptionOnFind = false;

    private Class<?> throwExceptionOnSaveForClass;
    private Class<?> throwExceptionOnFindForClass;

    public MockTemplate(Mongo mongo, String databaseName, UserCredentials userCredentials) {
        super(mongo, databaseName, userCredentials);
    }

    public MockTemplate(Mongo mongo, String databaseName) {
        super(mongo, databaseName);
    }

    public MockTemplate(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) {
        super(mongoDbFactory, mongoConverter);
    }

    public MockTemplate(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }

    public void setThrowExceptionOnFind(boolean throwExceptionOnFind, Class<?> forClass) {
        this.throwExceptionOnFind = throwExceptionOnFind;
        this.throwExceptionOnFindForClass = forClass;
    }

    public void clearThrowExceptionOnFind() {
        this.setThrowExceptionOnFind(false, null);
    }

    public void setThrowExceptionOnSave(boolean throwExceptionOnSave, Class<?> forClass) {
        this.throwExceptionOnSave = throwExceptionOnSave;
        this.throwExceptionOnSaveForClass = forClass;
    }

    public void clearThrowExceptionOnSave() {
        this.setThrowExceptionOnSave(false, null);
    }

    @Override
    protected Object saveDBObject(String collectionName, DBObject dbDoc, Class<?> entityClass) {
        if (throwExceptionOnSave && throwExceptionOnSaveForClass == entityClass) {
            throw new MockTemplateException(format("[throwExceptionOnSave=true,throwExceptionForClass=%s]", throwExceptionOnSaveForClass));
        }

        return super.saveDBObject(collectionName, dbDoc, entityClass);
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        throwExceptionOnFind(entityClass);
        return super.find(query, entityClass, collectionName);
    }

    @Override
    protected <T> List<T> doFind(String collectionName, DBObject query, DBObject fields, Class<T> entityClass) {
        throwExceptionOnFind(entityClass);
        return super.doFind(collectionName, query, fields, entityClass);
    }

    @Override
    protected <T> T doFindOne(String collectionName, DBObject query, DBObject fields, Class<T> entityClass) {
        throwExceptionOnFind(entityClass);
        return super.doFindOne(collectionName, query, fields, entityClass);
    }

    private <T> void throwExceptionOnFind(Class<T> entityClass) {
        if (throwExceptionOnFind && throwExceptionOnFindForClass == entityClass) {
            throw new MockTemplateException(format("[throwExceptionOnFind=true,throwExceptionForClass=%s]", throwExceptionOnFindForClass));
        }
    }

    private static class MockTemplateException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public MockTemplateException(String message) {
            super(message);
        }
    }
}
