package com.connectedworldservices.nectr.v2.api.rest.model;

import java.util.List;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@Document(collection = IntegrationData.COLLECTION)
public class IntegrationData {

    public static final String COLLECTION = "integration-data";

    @Id
    private String id;

    private String testId;

    private String environmentId;

    private String journeyId;

    @JsonInclude(Include.NON_NULL)
    private String journeyName;

    private List<?> data;
}
