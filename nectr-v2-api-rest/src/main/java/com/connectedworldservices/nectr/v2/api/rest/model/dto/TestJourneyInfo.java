package com.connectedworldservices.nectr.v2.api.rest.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TestJourneyInfo {

    private String environmentId;

    private String journeyId;

    private Date timestamp;

    private String carrier;

    private String country;

    private String clientId;

}
