package com.connectedworldservices.nectr.v2.api.rest.model.dto;

import java.util.Map;

import lombok.Data;

@Data
public class CreateTestScenario {

    private String environmentId;

    private String carrier;

    private String country;

    private String clientId;

    private String journeyName;

    private String journeyId;

    private Map<String, Object> metadata;
}
