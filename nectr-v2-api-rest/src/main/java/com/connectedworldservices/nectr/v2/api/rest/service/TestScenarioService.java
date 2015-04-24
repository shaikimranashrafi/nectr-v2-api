package com.connectedworldservices.nectr.v2.api.rest.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.connectedworldservices.nectr.v2.api.rest.model.ApplicationData;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.IntegrationData;
import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestScenarioInfo;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioCreatedEvent;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioDeletedEvent;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;

@Slf4j
@Service
public class TestScenarioService implements ApplicationContextAware {

    private final MongoTemplate mongoTemplate;

    private final MongoConnector mongoConnector;

    private final TestIdGenerator testIdGenerator;

    private final TestScenarioTemplate testScenarioTemplate;

    private final EnvironmentRepository environmentRepository;

    private ApplicationContext applicationContext;

    @Autowired
    public TestScenarioService(MongoTemplate mongoTemplate, MongoConnector mongoConnector, EnvironmentRepository environmentRepository, TestIdGenerator testIdGenerator) {
        this.mongoTemplate = mongoTemplate;
        this.mongoConnector = mongoConnector;
        this.testIdGenerator = testIdGenerator;
        this.environmentRepository = environmentRepository;
        this.testScenarioTemplate = new TestScenarioTemplate(mongoTemplate, mongoConnector);
    }

    public TestScenarioInfo createTestScenario(String environmentId, String carrier, String country, String clientId, String journeyName, String journeyId,
            Map<String, Object> metadata) {
        checkArgument(!isEmpty(environmentId), "environmentId cannot be null");
        checkArgument(!isEmpty(journeyId), "journeyId cannot be null");
        checkArgument(!isEmpty(carrier), "carrier cannot be null");
        checkArgument(!isEmpty(country), "country cannot be null");
        checkArgument(!isEmpty(clientId), "clientId cannot be null");

        //require the journeyName only if the application host is enabled
        if (mongoConnector.isApplicationHostEnabled()) {
            checkArgument(!isEmpty(journeyName), "journeyName cannot be null");
        }

        Environment environment = environmentRepository.findOne(environmentId);
        if (environment == null) {
            throw new IllegalArgumentException("environmentId not found: " + environmentId);
        }

        TestScenario testScenario = new TestScenario();
        testScenario.setId(generateTestId(environment, country, clientId));
        testScenario.setEnvironmentId(environmentId);
        testScenario.setJourneyName(journeyName);
        testScenario.setJourneyId(journeyId);
        testScenario.setCountry(country);
        testScenario.setCarrier(carrier);
        testScenario.setClientId(clientId);
        testScenario.setMetadata(metadata);

        try {
            List<?> integrationDataList = mongoConnector.connectAndFind(environment.getIntegrationHost(),
                    Queries.searchIntegrationData(journeyId, journeyName, mongoConnector.isApplicationHostEnabled()), LinkedHashMap.class);

            IntegrationData integrationData = new IntegrationData();
            integrationData.setData(integrationDataList);
            integrationData.setEnvironmentId(environmentId);
            integrationData.setJourneyId(journeyId);
            integrationData.setJourneyName(journeyName);
            integrationData.setTestId(testScenario.getId());
            testScenario.setIntegrationData(integrationData);

        } catch (UnknownHostException ex) {
            throw new NeCTRv2Exception(ex);
        }

        if (mongoConnector.isApplicationHostEnabled()) {
            try {
                Map<?, ?> applicationDataMap = mongoConnector.connectAndFindOne(environment.getApplicationHost(), Queries.searchApplicationData(journeyId), LinkedHashMap.class,
                        journeyName);

                ApplicationData applicationData = new ApplicationData();
                applicationData.setData(applicationDataMap);
                applicationData.setEnvironmentId(environmentId);
                applicationData.setJourneyId(journeyId);
                applicationData.setJourneyName(journeyName);
                applicationData.setTestId(testScenario.getId());
                testScenario.setApplicationData(applicationData);

            } catch (UnknownHostException ex) {
                throw new NeCTRv2Exception(ex);
            }
        }

        testScenarioTemplate.create(testScenario, true);

        publishEvent(new TestScenarioCreatedEvent(testScenario, format("Test Scenario %s Created: ", testScenario.getId())));

        return mongoTemplate.findById(testScenario.getId(), TestScenarioInfo.class, TestScenario.COLLECTION);
    }

    protected String generateTestId(Environment environment, String country, String clientId) {
        return testIdGenerator.generateTestId(environment, country, clientId);
    }

    public List<TestScenarioInfo> searchTestScenarios(List<SearchCriteria> criteria) {
        if (CollectionUtils.isEmpty(criteria)) {
            return Collections.emptyList();
        }

        try {
            return mongoTemplate.find(new Query(Queries.searchTestScenarios(criteria)), TestScenarioInfo.class, TestScenario.COLLECTION);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return Collections.emptyList();
    }

    public TestScenario loadTestScenario(String testId) {
        checkNotNull(testId, "testId cannot be null"); //NOSONAR
        return testScenarioTemplate.get(testId);
    }

    public TestScenarioInfo updateTestScenario(String testId, Map<String, Object> metadata) {
        checkNotNull(testId, "testId cannot be null"); //NOSONAR

        TestScenario testScenario = testScenarioTemplate.get(testId);

        testScenario.setMetadata(metadata);

        mongoTemplate.save(testScenario);

        return mongoTemplate.findById(testScenario.getId(), TestScenarioInfo.class, TestScenario.COLLECTION);
    }

    public void deleteTestScenario(String testId) {
        checkNotNull(testId, "testId cannot be null"); //NOSONAR

        TestScenario testScenario = testScenarioTemplate.delete(testId);

        publishEvent(new TestScenarioDeletedEvent(testScenario, format("Test Scenario %s Deleted: ", testScenario.getId())));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }
}
