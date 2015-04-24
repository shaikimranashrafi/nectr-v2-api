package com.connectedworldservices.nectr.v2.api.rest.model;

import java.util.Date;
import java.util.Map;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = TestReplayHistory.COLLECTION)
public class TestReplayHistory {

    public static final String COLLECTION = "test-history";

    @Id
    private String id;

    private String testId;

    private String user;

    private Date timestamp;

    private String requestURI;

    private String remoteAddress;

    private Map<String, Object> parameters;

}
