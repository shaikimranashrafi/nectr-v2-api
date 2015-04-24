package com.connectedworldservices.nectr.v2.api.rest.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;

public final class Queries {

    private Queries() {
        //utility class can't be instantiated
    }

    public static Query searchApplicationData(String journeyId) {
        Query query = query(where("journeyId").is(journeyId)); //NOSONAR
        query.limit(1);
        query.fields().exclude("_id");
        return query;
    }

    public static Query searchIntegrationDataByJourneyId(String journeyId) {
        Query query = query(where("journeyId").is(journeyId).and("messageLocation").regex("NETWORK_(REQUEST|RESPONSE)")); //NOSONAR
        query.limit(100);
        query.fields().exclude("_id");
        return query;
    }

    public static Query searchIntegrationData(String journeyId, String journeyName, boolean applicationHostEnabled) {
        Query query = query(where("journeyId").is(journeyId).and("messageLocation").regex("NETWORK_(REQUEST|RESPONSE)")); //NOSONAR

        if (applicationHostEnabled) {
            query.addCriteria(where("journeyName").is(journeyName));
        }

        query.limit(100);
        query.fields().exclude("_id");
        return query;
    }

    public static Query searchById(String id) {
        return query(where("_id").is(id));
    }

    public static Aggregation searchTestJourneys(List<SearchCriteria> criteria) {
        return newAggregation(match(searchTestScenarios(criteria)), group("journeyId", "carrier", "country", "clientId").max("timestamp").as("timestamp"));
    }

    public static Criteria searchTestScenarios(List<SearchCriteria> criteria) {
        List<Criteria> criterias = new ArrayList<>(criteria.size());

        for (SearchCriteria entry : criteria) {
            if (">".equalsIgnoreCase(entry.getOperation())) {
                criterias.add(Criteria.where(entry.getKey()).gt(entry.getValue()));

            } else if ("<".equalsIgnoreCase(entry.getOperation())) {
                criterias.add(Criteria.where(entry.getKey()).lt(entry.getValue()));

            } else if (":".equalsIgnoreCase(entry.getOperation())) {
                criterias.add(Criteria.where(entry.getKey()).is(entry.getValue()));
            }
        }

        if (criterias.isEmpty()) {
            throw new IllegalStateException("Search criteria cannot be empty");
        }

        return new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
    }

    public static Query searchByTestId(String testId) {
        return query(where("testId").is(testId));
    }
}
