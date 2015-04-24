package com.connectedworldservices.nectr.v2.api.rest.model.dto;

import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
public class TestScenarioInfo {

    private String id;

    private String environmentId;

    private String carrier;

    private String country;

    private String clientId;

    @JsonInclude(Include.NON_NULL)
    private String journeyName;

    private String journeyId;

    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> metadata;
}
