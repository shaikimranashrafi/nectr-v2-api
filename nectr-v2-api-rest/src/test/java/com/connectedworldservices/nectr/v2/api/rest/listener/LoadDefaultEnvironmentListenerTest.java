package com.connectedworldservices.nectr.v2.api.rest.listener;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.listener.LoadDefaultEnvironmentListener;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.repository.EnvironmentRepository;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;

@RunWith(MockitoJUnitRunner.class)
public class LoadDefaultEnvironmentListenerTest extends AbstractTest {

    private LoadDefaultEnvironmentListener defaultEnvironmentListener;

    @Mock
    private Environment defaultEnvironment;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private MongoConnector mongoConnector;

    @Mock
    private ContextRefreshedEvent MOCK_EVENT;

    @Before
    public void setUp() throws Exception {
        defaultEnvironmentListener = new LoadDefaultEnvironmentListener(defaultEnvironment, environmentRepository, mongoConnector);

        when(defaultEnvironment.getId()).thenReturn("TEST");
        when(defaultEnvironment.getIntegrationHost()).thenReturn(new Host("integration:27017", "integration", "integration"));
        when(defaultEnvironment.getApplicationHost()).thenReturn(new Host("application:27017", "application", null));

        Environment environment = new Environment(defaultEnvironment);
        when(environmentRepository.save(any(Environment.class))).thenReturn(environment);
    }

    @Test
    public void should_throw_exception_on_start_when_environment_is_null() {
        //given
        defaultEnvironmentListener = new LoadDefaultEnvironmentListener(null, environmentRepository, mongoConnector);
        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_environment_id_is_null() {
        //given
        when(defaultEnvironment.getId()).thenReturn(null);
        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_integration_host_is_null() {
        //given
        when(defaultEnvironment.getIntegrationHost()).thenReturn(null);
        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_integration_host_url_is_null() {
        //given
        when(defaultEnvironment.getIntegrationHost()).thenReturn(new Host(null, "integration", "integration"));
        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_integration_host_dbname_is_null() {
        //given
        when(defaultEnvironment.getIntegrationHost()).thenReturn(new Host("integration:27017", null, "integration"));
        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_integration_host_collection_is_null() {
        //given
        when(defaultEnvironment.getIntegrationHost()).thenReturn(new Host("integration:27017", "integration", null));
        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_application_host_is_null_and_application_host_is_enabled() {
        //given
        when(mongoConnector.isApplicationHostEnabled()).thenReturn(true);
        when(defaultEnvironment.getApplicationHost()).thenReturn(null);

        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_application_host_url_is_null_and_application_host_is_enabled() {
        //given
        when(mongoConnector.isApplicationHostEnabled()).thenReturn(true);
        when(defaultEnvironment.getApplicationHost()).thenReturn(new Host(null, "application", null));

        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_throw_exception_on_start_when_application_host_dbname_is_null_and_application_host_is_enabled() {
        //given
        when(mongoConnector.isApplicationHostEnabled()).thenReturn(true);
        when(defaultEnvironment.getApplicationHost()).thenReturn(new Host("application:27017", null, null));

        Exception actual = null;

        //when
        try {
            defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalStateException.class));
    }

    @Test
    public void should_create_default_environment_when_it_doesnt_exists() {
        //given setUp()

        //when
        defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);

        //then
        verify(environmentRepository, times(1)).save(any(Environment.class));
    }

    @Test
    public void should_create_default_environment_when_it_doesnt_exists_and_application_host_is_enabled() {
        //given
        when(mongoConnector.isApplicationHostEnabled()).thenReturn(true);

        //when
        defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);

        //then
        verify(environmentRepository, times(1)).save(any(Environment.class));
    }

    @Test
    public void should_create_default_environment_when_it_already_exists() {
        //given setUp()
        when(environmentRepository.findOne("TEST")).thenReturn(new Environment("TEST", new Host("integration:27017", "integration", "integration")));

        //when
        defaultEnvironmentListener.onApplicationEvent(MOCK_EVENT);

        //then
        verify(environmentRepository, times(1)).save(any(Environment.class));
    }
}
