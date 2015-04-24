package com.connectedworldservices.nectr.v2.api.rest.service;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.repository.SequenceRepository;
import com.connectedworldservices.nectr.v2.api.rest.service.TestIdGenerator;

@RunWith(MockitoJUnitRunner.class)
public class TestIdGeneratorTest extends AbstractTest {

    private TestIdGenerator testIdGenerator;

    @Mock
    private SequenceRepository sequenceRepository;

    private final String COUNTRY = "TT";
    private final String CLIENT_ID = "TT";
    private final Environment ENVIRONMENT = new Environment("TEST", new Host("integration:27017", "integration", "integration"));

    @Before
    public void setUp() throws Exception {
        testIdGenerator = new TestIdGenerator(sequenceRepository);
    }

    @Test
    public void should_throw_exception_when_environment_is_null() throws Exception {
        //given
        Exception actual = null;
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(1L);

        //when
        try {
            testIdGenerator.generateTestId(null, COUNTRY, CLIENT_ID);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_when_environment_id_is_null() throws Exception {
        //given
        Exception actual = null;
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(1L);

        //when
        try {
            testIdGenerator.generateTestId(new Environment(), COUNTRY, CLIENT_ID);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_when_country_is_null() throws Exception {
        //given
        Exception actual = null;
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(1L);

        //when
        try {
            testIdGenerator.generateTestId(ENVIRONMENT, null, CLIENT_ID);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_throw_exception_when_client_id_is_null() throws Exception {
        //given
        Exception actual = null;
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(1L);

        //when
        try {
            testIdGenerator.generateTestId(ENVIRONMENT, COUNTRY, null);
        } catch (Exception ex) {
            actual = ex;
        }

        //then
        assertThat(actual, instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void should_generate_padded_test_id_when_sequence_number_magnitude_is_1() throws Exception {
        //given
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(1L);

        //when
        String testId = testIdGenerator.generateTestId(ENVIRONMENT, COUNTRY, CLIENT_ID);

        //then
        assertEquals("TTTT-TEST-0001", testId);
    }

    @Test
    public void should_increment_padding_when_sequence_number_magnitude_is_5() throws Exception {
        //given
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(10000L);

        //when
        String testId = testIdGenerator.generateTestId(ENVIRONMENT, COUNTRY, CLIENT_ID);

        //then
        assertEquals("TTTT-TEST-10000", testId);
    }

    @Test
    public void should_increment_padding_when_sequence_number_magnitude_is_8() throws Exception {
        //given
        when(sequenceRepository.incrementSequenceId(TestIdGenerator.testIdPrefix(COUNTRY, CLIENT_ID))).thenReturn(10000000L);

        //when
        String testId = testIdGenerator.generateTestId(ENVIRONMENT, COUNTRY, CLIENT_ID);

        //then
        assertEquals("TTTT-TEST-10000000", testId);
    }
}
