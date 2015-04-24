package com.connectedworldservices.nectr.v2.api.rest.service;

import static java.lang.String.format;

import java.util.Date;

import org.apache.commons.collections.MapUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.CollectionUtils;

import com.connectedworldservices.nectr.v2.api.rest.model.ApplicationData;
import com.connectedworldservices.nectr.v2.api.rest.model.IntegrationData;
import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;

class TestScenarioTemplate {

    private final MongoTemplate mongoTemplate;
    private final MongoConnector mongoConnector;

    TestScenarioTemplate(MongoTemplate mongoTemplate, MongoConnector mongoConnector) {
        this.mongoTemplate = mongoTemplate;
        this.mongoConnector = mongoConnector;
    }

    TestScenario create(TestScenario testScenario, boolean cleanOnFail) {
        IntegrationData integrationData = testScenario.getIntegrationData();
        ApplicationData applicationData = testScenario.getApplicationData();

        try {
            validate(testScenario);

            // this should be already null but if we are importing then the id must be set to null
            integrationData.setId(null);

            mongoTemplate.save(integrationData);

            if (mongoConnector.isApplicationHostEnabled()) {

                // this should be already null but if we are importing then the id must be set to null
                applicationData.setId(null);

                mongoTemplate.save(applicationData);
            }

            // mongodb audit can't set the Create timestamp when an id is explicitly set. must do it manually. :/
            testScenario.setCreated(new Date());
            testScenario.setIntegrationData(integrationData);
            testScenario.setApplicationData(applicationData);

            mongoTemplate.save(testScenario);

            return testScenario;

        } catch (Exception ex) {
            if (cleanOnFail && shouldCleanData(integrationData)) {
                mongoTemplate.remove(integrationData);

                if (shouldCleanData(applicationData)) {
                    mongoTemplate.remove(applicationData);
                }
            }

            throw new NeCTRv2Exception(ex);
        }
    }

    TestScenario get(final String testId) {
        TestScenario testScenario = mongoTemplate.findById(testId, TestScenario.class);

        if (testScenario == null) {
            throw new NotFoundException(testId + " doesn't exist");
        }
        return testScenario;
    }

    boolean shouldCleanData(final IntegrationData integrationData) {
        return integrationData != null && mongoTemplate.exists(Queries.searchById(integrationData.getId()), IntegrationData.class);
    }

    boolean shouldCleanData(final ApplicationData applicationData) {
        return applicationData != null && mongoTemplate.exists(Queries.searchById(applicationData.getId()), ApplicationData.class);
    }

    boolean exists(final String testId) {
        return mongoTemplate.exists(Queries.searchById(testId), TestScenario.class);
    }

    void validate(final TestScenario testScenario) throws ValidationException {
        validateTestId(testScenario);
        validateIntegrationData(testScenario);

        if (mongoConnector.isApplicationHostEnabled()) {
            validateApplicationData(testScenario);
        }
    }

    void validateTestId(final TestScenario testScenario) throws ValidationException {
        if (exists(testScenario.getId())) {
            throw new ValidationException(testScenario.getId() + " already exists");
        }
    }

    void validateIntegrationData(final TestScenario testScenario) throws ValidationException {
        if (CollectionUtils.isEmpty(testScenario.getIntegrationData().getData())) {
            throw new ValidationException(format("Integration data for journey %s not found in test scenario %s", testScenario.getJourneyId(), testScenario.getId()));
        }
    }

    void validateApplicationData(final TestScenario testScenario) throws ValidationException {
        if (MapUtils.isEmpty(testScenario.getApplicationData().getData())) {
            throw new ValidationException(format("Application data for journey %s not found in test scenario %s", testScenario.getJourneyId(), testScenario.getId()));
        }
    }

    TestScenario delete(String testId) {
        TestScenario testScenario = get(testId);

        mongoTemplate.remove(Queries.searchByTestId(testId), ApplicationData.class);
        mongoTemplate.remove(Queries.searchByTestId(testId), IntegrationData.class);
        mongoTemplate.remove(Queries.searchById(testId), TestScenario.class);

        return testScenario;
    }
}
