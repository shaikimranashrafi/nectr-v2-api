package com.connectedworldservices.nectr.v2.api.rest.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = Sequence.COLLECTION)
public class Sequence {

    public static final String COLLECTION = "test-ids";

    @Id
    private String id;

    private long counter;
}
