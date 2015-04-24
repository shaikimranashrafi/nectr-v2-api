package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.repository.SequenceRepositoryImpl;

@RunWith(MockitoJUnitRunner.class)
public class SequenceRepositoryImplTest extends AbstractTest {

    private SequenceRepositoryImpl sequenceRepositoryImpl;

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        sequenceRepositoryImpl = new SequenceRepositoryImpl(mongoTemplate);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_when_sequence_is_not_in_db() throws Exception {
        //given
        String sequenceId = "UNKNOWN";

        //when
        sequenceRepositoryImpl.incrementSequenceId(sequenceId);

        //then throw exception
    }

}
