package com.connectedworldservices.nectr.v2.api.rest.model;

import java.util.Date;
import java.util.Map;

import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Document(collection = TestScenario.COLLECTION)
public class TestScenario {

    public static final String COLLECTION = "test-scenarios";

    @Id
    private String id;

    private String environmentId;

    @JsonInclude(Include.NON_NULL)
    private String journeyName;

    private String journeyId;

    private String carrier;

    private String country;

    private String clientId;

    @CreatedDate
    private Date created;

    @LastModifiedDate
    private Date modified;

    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> metadata;

    @DBRef
    @Field("integration")
    @JsonProperty("integration")
    private IntegrationData integrationData;

    @DBRef
    @Field("application")
    @JsonProperty("application")
    @JsonInclude(Include.NON_NULL)
    private ApplicationData applicationData;

}
