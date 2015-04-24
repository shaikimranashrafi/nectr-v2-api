package com.connectedworldservices.nectr.v2.api.rest.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestScenarioInfo;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioCreatedEvent;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;
import com.connectedworldservices.nectr.v2.api.rest.service.TestIdGenerator;
import com.connectedworldservices.nectr.v2.api.rest.service.TestScenarioService;

@RunWith(MockitoJUnitRunner.class)
public class TestScenarioServiceTest extends AbstractTest {

    private TestScenarioService testScenarioService;

    @Mock
    private MongoConnector mongoConnector;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private TestIdGenerator testIdGenerator;

    @Mock
    private ApplicationContext applicationContext;

    //@formatter:off
    @Before
    public void setUp() throws Exception {
        testScenarioService = new TestScenarioService(mongoTemplate, mongoConnector, environmentRepository, testIdGenerator);
        testScenarioService.setApplicationContext(applicationContext);

        Environment environment = new Environment("TEST", new Host("integration:27017", "integration", "integration"));

        when(environmentRepository.findOne(environment.getId())).
        thenReturn(environment);

        when(testIdGenerator.generateTestId(environment, "TT", "TT")).
        thenReturn("TTTT-TEST-0001");

        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("journeyId", "705e98d5-7d16-43b4-9a78-fddc767cbb25");
        data.put("journeyName", "new-connection");

        when(mongoConnector.connectAndFind(environment.getIntegrationHost(), Queries.searchIntegrationData(data.get("journeyId"), data.get("journeyName"), false), LinkedHashMap.class)).
        thenReturn(asList(data));

        TestScenario testScenario = new TestScenario();
        testScenario.setId("TTTT-TEST-0001");

        when(mongoTemplate.findById("TTTT-TEST-0001", TestScenario.class)).
        thenReturn(testScenario);

        TestScenarioInfo testScenarioInfo = new TestScenarioInfo();
        testScenarioInfo.setId("TTTT-TEST-0001");

        when(mongoTemplate.findById("TTTT-TEST-0001", TestScenarioInfo.class, TestScenario.COLLECTION)).
        thenReturn(testScenarioInfo);
    }

    @Test
    public void should_throw_exception_when_environment_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            testScenarioService.createTestScenario(null, "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_when_journey_name_is_empty_and_application_host_is_disabled() {
        //given
        Exception actual = null;

        when(mongoConnector.isApplicationHostEnabled()).thenReturn(true);

        //when
        try {
            testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", null, "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_when_environment_is_unkown() {
        //given
        Exception actual = null;

        //when
        try {
            testScenarioService.createTestScenario("UNKNOWN", "SPRINT", "TT", "TT",  "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_create_test_scenario_without_metadata() throws Exception {
        //given

        //when
        TestScenarioInfo actual = testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT",  "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        //then
        assertEquals(actual.getId(), "TTTT-TEST-0001");
        verify(environmentRepository, times(1)).findOne(any());
        verify(mongoConnector, times(1)).connectAndFind(any(), any(), any());
    }

    @Test
    public void should_throw_exception_when_journey_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", null, Collections.emptyMap());
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_raise_test_scenario_created_event() throws Exception {
        //given
        ArgumentCaptor<TestScenarioCreatedEvent> argument = forClass(TestScenarioCreatedEvent.class);

        //when
        TestScenarioInfo actual = testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT",  "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        //then
        assertEquals(actual.getId(), "TTTT-TEST-0001");
        verify(applicationContext).publishEvent(argument.capture());
    }

    @Test
    public void should_raise_test_scenario_deleted_event() throws Exception {
        //given
        ArgumentCaptor<TestScenarioCreatedEvent> argument = forClass(TestScenarioCreatedEvent.class);

        //when
        testScenarioService.deleteTestScenario("TTTT-TEST-0001");

        //then
        verify(applicationContext).publishEvent(argument.capture());
    }
    //@formatter:on
}
