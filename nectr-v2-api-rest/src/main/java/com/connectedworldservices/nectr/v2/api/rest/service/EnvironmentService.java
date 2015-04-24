package com.connectedworldservices.nectr.v2.api.rest.service;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.google.common.collect.Lists;

@Service
public class EnvironmentService {

    private final String defaultEnvironmentId;

    private final EnvironmentRepository environmentRepository;

    private final MongoConnector mongoConnector;

    @Autowired
    public EnvironmentService(@Value("${default.environment.id}") String defaultEnvironmentId, EnvironmentRepository environmentRepository, MongoConnector mongoConnector) {
        this.defaultEnvironmentId = defaultEnvironmentId;
        this.environmentRepository = environmentRepository;
        this.mongoConnector = mongoConnector;
    }

    public List<Environment> loadEnvironments() {
        Iterable<Environment> environments = environmentRepository.findAll();

        environments.forEach(o -> o.setApplicationHost(!mongoConnector.isApplicationHostEnabled() ? null : o.getApplicationHost()));

        return Lists.newArrayList(environments);
    }

    public Environment loadEnvironment(String environmentId) {
        checkArgument(!isEmpty(environmentId), "environmentId cannot be null"); //NOSONAR

        Environment environment = get(environmentId);

        if (!mongoConnector.isApplicationHostEnabled()) {
            environment.setApplicationHost(null);
        }

        return environment;
    }

    public Environment createEnvironment(Environment environment) {
        checkArgument(!isEmpty(environment), "environment cannot be null");
        checkArgument(!isEmpty(environment.getId()), "environment.id cannot be null");
        checkArgument(!environment.getId().equals(defaultEnvironmentId), "cannot create an environment with the same id as the default environment");
        checkArgument(!isEmpty(environment.getIntegrationHost()), "environment.integrationHost cannot be null");
        checkArgument(!isEmpty(environment.getIntegrationHost().getUrl()), "environment.integrationHost.url cannot be null");
        checkArgument(!isEmpty(environment.getIntegrationHost().getDbName()), "environment.integrationHost.dbName cannot be null");
        checkArgument(!isEmpty(environment.getIntegrationHost().getCollection()), "environment.integrationHost.collection cannot be null");

        if (mongoConnector.isApplicationHostEnabled()) {
            checkArgument(!isEmpty(environment.getApplicationHost()), "environment.applicationHost cannot be null");
            checkArgument(!isEmpty(environment.getApplicationHost().getUrl()), "environment.applicationHost.url cannot be null");
            checkArgument(!isEmpty(environment.getApplicationHost().getDbName()), "environment.applicationHost.dbName cannot be null");
        }

        if (environmentRepository.exists(environment.getId())) {
            throw new IllegalArgumentException(environment.getId() + " already exists");
        }

        return environmentRepository.save(environment);
    }

    public Environment updateEnvironment(String environmentId, Host integrationHost, Host applicationHost) {
        checkArgument(!isEmpty(environmentId), "environmentId cannot be null"); //NOSONAR
        checkArgument(!environmentId.equals(defaultEnvironmentId), "cannot update an environment with the same id as the default environment");
        checkArgument(!isEmpty(integrationHost), "integrationHost cannot be null");
        checkArgument(!isEmpty(integrationHost.getUrl()), "integrationHost.url cannot be null");
        checkArgument(!isEmpty(integrationHost.getDbName()), "integrationHost.dbName cannot be null");
        checkArgument(!isEmpty(integrationHost.getCollection()), "integrationHost.collection cannot be null");

        if (mongoConnector.isApplicationHostEnabled()) {
            checkArgument(!isEmpty(applicationHost), "applicationHost cannot be null");
            checkArgument(!isEmpty(applicationHost.getUrl()), "applicationHost.url cannot be null");
            checkArgument(!isEmpty(applicationHost.getDbName()), "applicationHost.dbName cannot be null");
        }

        Environment env = get(environmentId);

        env.setIntegrationHost(integrationHost);

        if (mongoConnector.isApplicationHostEnabled()) {
            env.setApplicationHost(applicationHost);
        }

        return environmentRepository.save(env);
    }

    public void deleteEnvironment(String environmentId) {
        checkArgument(!isEmpty(environmentId), "environmentId cannot be null"); //NOSONAR
        checkArgument(!environmentId.equals(defaultEnvironmentId), "cannot delete the default environment");

        Environment environment = get(environmentId);

        environmentRepository.delete(environment);
    }

    public List<String> loadEnvironmentIds() {
        return environmentRepository.findAllEnvironmentIds();
    }

    private Environment get(final String environmentId) {
        Environment environment = environmentRepository.findOne(environmentId);

        if (environment == null) {
            throw new NotFoundException(environmentId + " doesn't exist");
        }

        return environment;
    }
}
