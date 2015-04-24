package com.connectedworldservices.nectr.v2.api.rest.listener;

import static com.google.common.base.Preconditions.checkState;
import static org.springframework.util.StringUtils.isEmpty;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;

@Slf4j
@Component
public class LoadDefaultEnvironmentListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Environment defaultEnvironment;

    private final EnvironmentRepository environmentRepository;

    private final MongoConnector mongoConnector;

    @Autowired
    public LoadDefaultEnvironmentListener(Environment defaultEnvironment, EnvironmentRepository environmentRepository, MongoConnector mongoConnector) {
        this.defaultEnvironment = defaultEnvironment;
        this.environmentRepository = environmentRepository;
        this.mongoConnector = mongoConnector;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadDefaultEnvironment();
    }

    protected void loadDefaultEnvironment() {
        checkState(!isEmpty(defaultEnvironment), "default.environment cannot be null");
        checkState(!isEmpty(defaultEnvironment.getId()), "default.environment.id cannot be null");
        checkState(!isEmpty(defaultEnvironment.getIntegrationHost()), "default.environment.integration-host cannot be null");
        checkState(!isEmpty(defaultEnvironment.getIntegrationHost().getUrl()), "default.environment.integration-host.url cannot be null");
        checkState(!isEmpty(defaultEnvironment.getIntegrationHost().getDbName()), "default.environment.integration-host.dbName cannot be null");
        checkState(!isEmpty(defaultEnvironment.getIntegrationHost().getCollection()), "default.environment.integration-host.collection cannot be null");

        if (mongoConnector.isApplicationHostEnabled()) {
            checkState(!isEmpty(defaultEnvironment.getApplicationHost()), "default.environment.application-host cannot be null");
            checkState(!isEmpty(defaultEnvironment.getApplicationHost().getUrl()), "default.environment.application-host.url cannot be null");
            checkState(!isEmpty(defaultEnvironment.getApplicationHost().getDbName()), "default.environment.application-host.dbName cannot be null");
        }

        String environmentId = defaultEnvironment.getId();

        if (environmentExistsInRepository(environmentId)) {
            log.warn("Environment {} already exists. Overriding values in the database with ones defined in the properties file.", environmentId);
        }

        environmentRepository.save(new Environment(defaultEnvironment));

        log.info("Environment {} updated in the database.", environmentId);
    }

    protected boolean environmentExistsInRepository(String environmentId) {
        return environmentRepository.findOne(environmentId) != null;
    }

    @Configuration
    @ConfigurationProperties(ignoreUnknownFields = false, prefix = "default.environment")
    protected static class DefaultEnvironment extends Environment {

    }
}