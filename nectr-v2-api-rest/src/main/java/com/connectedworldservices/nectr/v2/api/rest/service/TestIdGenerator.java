package com.connectedworldservices.nectr.v2.api.rest.service;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.repository.SequenceRepository;

@Slf4j
@Component
class TestIdGenerator {

    private SequenceRepository sequenceRepository;

    @Autowired
    TestIdGenerator(SequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    String generateTestId(Environment environment, String country, String clientId) {
        checkArgument(!isEmpty(environment), "environment cannot be null");
        checkArgument(!isEmpty(environment.getId()), "environment.id cannot be null");
        checkArgument(!isEmpty(country), "country cannot be null");
        checkArgument(!isEmpty(clientId), "clientId cannot be null");

        final String testIdPrefix = testIdPrefix(country, clientId);

        final String testId = format("%s-%s-%04d", testIdPrefix, environment.getId(), nextSequenceId(testIdPrefix));

        log.debug("Generated testId: {}", testId);

        return testId.toUpperCase();
    }

    private long nextSequenceId(String testIdPrefix) {
        return sequenceRepository.incrementSequenceId(testIdPrefix);
    }

    public static String testIdPrefix(String country, String clientId) {
        return format("%s%s", country, clientId);
    }
}
