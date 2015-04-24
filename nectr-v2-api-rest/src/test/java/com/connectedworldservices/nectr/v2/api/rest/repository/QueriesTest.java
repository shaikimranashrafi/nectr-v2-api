package com.connectedworldservices.nectr.v2.api.rest.repository;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;

import com.connectedworldservices.nectr.v2.api.rest.AbstractTest;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;

public class QueriesTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_an_exception_if_a_bad_operator_is_used() {
        //given
        List<SearchCriteria> criterias = asList(new SearchCriteria("id", "|", "TTTT-TEST-0001"));

        //when
        Queries.searchTestScenarios(criterias);

        //then throw exception
    }

    @Test
    public void should_return_an_invalid_query_if_a_bad_operator_is_used() {
        //given
        List<SearchCriteria> criterias = asList(new SearchCriteria("id", "|", "TTTT-TEST-0001"), new SearchCriteria("created", ">", "01/01/15"));

        //when
        Query query = new Query(Queries.searchTestScenarios(criterias));

        //then
        assertEquals(query.getQueryObject().toString(), "{ \"$and\" : [ { \"created\" : { \"$gt\" : \"01/01/15\"}}]}");
    }

    @Test
    public void should_return_an_valid_query_if_an_equals_operator_is_used() {
        //given
        List<SearchCriteria> criterias = asList(new SearchCriteria("id", ":", "TTTT-TEST-0001"));

        //when
        Query query = new Query(Queries.searchTestScenarios(criterias));

        //then
        assertEquals(query.getQueryObject().toString(), "{ \"$and\" : [ { \"id\" : \"TTTT-TEST-0001\"}]}");
    }
}
