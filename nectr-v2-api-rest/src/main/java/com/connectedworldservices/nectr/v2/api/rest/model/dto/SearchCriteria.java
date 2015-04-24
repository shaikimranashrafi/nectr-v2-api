package com.connectedworldservices.nectr.v2.api.rest.model.dto;

import lombok.Data;

@Data
public class SearchCriteria {

    private String key;

    private String operation;

    private Object value;

    public SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
}
