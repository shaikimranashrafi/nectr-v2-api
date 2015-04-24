package com.connectedworldservices.nectr.v2.api.rest.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;
import com.connectedworldservices.nectr.v2.api.rest.service.TestJourneyService;

@RunWith(MockitoJUnitRunner.class)
public class TestJourneyServiceTest extends AbstractTest {

    private TestJourneyService testJourneyService;

    @Mock
    private MongoConnector mongoConnector;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Before
    public void setUp() throws Exception {
        testJourneyService = new TestJourneyService(mongoConnector, environmentRepository);

        Environment environment = new Environment("TEST", new Host("integration:27017", "integration", "integration"));

        when(environmentRepository.findOne(environment.getId())).thenReturn(environment);

        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("journeyId", "705e98d5-7d16-43b4-9a78-fddc767cbb25");
        data.put("journeyName", "new-connection");

        when(
                mongoConnector.connectAndFind(environment.getIntegrationHost(), Queries.searchIntegrationData(data.get("journeyId"), data.get("journeyName"), false),
                        LinkedHashMap.class)).thenReturn(asList(data));
    }

    @Test
    public void should_throw_exception_when_environment_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            testJourneyService.loadTestJourney(null, "705e98d5-7d16-43b4-9a78-fddc767cbb25");
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_when_journey_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            testJourneyService.loadTestJourney("TEST", null);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }
}
