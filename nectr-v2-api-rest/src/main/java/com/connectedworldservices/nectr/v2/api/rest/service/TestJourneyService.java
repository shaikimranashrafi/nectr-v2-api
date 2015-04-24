package com.connectedworldservices.nectr.v2.api.rest.service;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.StringUtils.isEmpty;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestJourney;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestJourneyInfo;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;

@Slf4j
@Service
public class TestJourneyService {

    private final MongoConnector mongoConnector;

    private final EnvironmentRepository environmentRepository;

    @Autowired
    public TestJourneyService(MongoConnector mongoConnector, EnvironmentRepository environmentRepository) {
        this.mongoConnector = mongoConnector;
        this.environmentRepository = environmentRepository;
    }

    public List<TestJourneyInfo> searchJourneys(List<SearchCriteria> criteria) {
        if (CollectionUtils.isEmpty(criteria) || !criteria.stream().anyMatch(o -> "environmentId".equals(o.getKey()))) {
            return Collections.emptyList();
        }

        SearchCriteria environmentIdCriteria = criteria.stream().filter(o -> "environmentId".equals(o.getKey())).findFirst().get();
        criteria.remove(environmentIdCriteria);

        if (criteria.isEmpty()) {
            return Collections.emptyList();
        }

        String environmentId = String.valueOf(environmentIdCriteria.getValue());

        Environment environment = environmentRepository.findOne(environmentId);
        if (environment == null) {
            return Collections.emptyList();
        }

        try {
            List<TestJourneyInfo> testJourneys = mongoConnector.connectAndAggregate(environment.getIntegrationHost(), Queries.searchTestJourneys(criteria), TestJourneyInfo.class);

            testJourneys.forEach(o -> o.setEnvironmentId(environmentId));

            return testJourneys;

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return Collections.emptyList();
    }

    public TestJourney loadTestJourney(String environmentId, String journeyId) {
        checkArgument(!isEmpty(environmentId), "environmentId cannot be null");
        checkArgument(!isEmpty(journeyId), "journeyId cannot be null");

        try {
            Environment environment = environmentRepository.findOne(environmentId);

            if (environment == null) {
                throw new IllegalArgumentException("environmentId not found: " + environmentId);
            }

            List<?> integrationDataList = mongoConnector.connectAndFind(environment.getIntegrationHost(), Queries.searchIntegrationDataByJourneyId(journeyId), LinkedHashMap.class);

            if (CollectionUtils.isEmpty(integrationDataList)) {
                throw new NotFoundException(journeyId + " doesn't exist");
            }

            TestJourney testJourney = new TestJourney();
            testJourney.setEnvironmentId(environmentId);
            testJourney.setJourneyId(journeyId);
            testJourney.setData(integrationDataList);

            return testJourney;
        } catch (UnknownHostException ex) {
            throw new NeCTRv2Exception(ex);
        }
    }

}
