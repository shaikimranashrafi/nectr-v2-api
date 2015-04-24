package com.connectedworldservices.nectr.v2.api.rest.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.connectedworldservices.nectr.v2.api.rest.model.ApplicationData;
import com.connectedworldservices.nectr.v2.api.rest.repository.ApplicationDataRepository;
import com.connectedworldservices.nectr.v2.api.rest.repository.TestScenarioRepository;

@Slf4j
@Service
public class ReplayDataService {

    private final ApplicationDataRepository applicationDataRepository;
    private final TestScenarioRepository testScenarioRepository;

    @Autowired
    public ReplayDataService(ApplicationDataRepository applicationDataRepository, TestScenarioRepository testScenarioRepository) {
        this.applicationDataRepository = applicationDataRepository;
        this.testScenarioRepository = testScenarioRepository;
    }

    public Map<?, ?> replayApplicationData(String testId) { //NOSONAR
        checkNotNull(testId, "testId cannot be null");

        if (!testScenarioRepository.exists(testId)) {
            throw new NotFoundException(testId + " doesn't exist");
        }

        try {

            ApplicationData applicationData = applicationDataRepository.findByTestId(testId);

            if (applicationData != null && !MapUtils.isEmpty(applicationData.getData())) {
                return applicationData.getData();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return Collections.emptyMap();
    }
}
