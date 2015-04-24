package com.connectedworldservices.nectr.v2.api.rest.service;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.service.EnvironmentService;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentServiceTest extends AbstractTest {

    private EnvironmentService environmentService;

    @Mock
    private MongoConnector mongoConnector;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Before
    public void setUp() throws Exception {
        environmentService = new EnvironmentService("TEST", environmentRepository, mongoConnector);
    }

    @After
    public void tearDown() throws Exception {
        mongoConnector.setApplicationHostEnabled(false);
    }

    @Test
    public void should_throw_exception_on_create_when_environment_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            environmentService.createEnvironment(null);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_on_update_when_environment_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            environmentService.updateEnvironment(null, new Host("integration:27017", "integration", "integration"), new Host("application:27017", "application", null));
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_on_update_when_application_host_is_null_and_application_host_is_enabled() {
        //given
        when(mongoConnector.isApplicationHostEnabled()).thenReturn(true);
        Exception actual = null;

        //when
        try {
            environmentService.updateEnvironment("TEST-ENV", new Host("integration:27017", "integration", "integration"), null);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_on_delete_when_environment_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            environmentService.deleteEnvironment(null);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_on_load_when_environment_id_is_null() {
        //given
        Exception actual = null;

        //when
        try {
            environmentService.loadEnvironment(null);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

}
