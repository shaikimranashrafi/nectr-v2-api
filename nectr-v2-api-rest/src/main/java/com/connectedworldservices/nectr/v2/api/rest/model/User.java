package com.connectedworldservices.nectr.v2.api.rest.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = User.COLLECTION)
public class User {

    public static final String COLLECTION = "users";

    @Id
    private String id;
}
