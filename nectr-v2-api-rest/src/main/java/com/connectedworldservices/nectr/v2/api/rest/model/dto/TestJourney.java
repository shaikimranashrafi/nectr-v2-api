package com.connectedworldservices.nectr.v2.api.rest.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class TestJourney {

    private String environmentId;

    private String journeyId;

    private List<?> data;
}
