package com.connectedworldservices.nectr.v2.api.rest.model;

import java.util.Map;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = ApplicationData.COLLECTION)
public class ApplicationData {

    public static final String COLLECTION = "application-data";

    @Id
    private String id;

    private String testId;

    private String environmentId;

    private String journeyId;

    private String journeyName;

    private Map<?, ?> data;
}
