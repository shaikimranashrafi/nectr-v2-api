package com.connectedworldservices.nectr.v2.api.rest.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NeCTRv2Exception extends RuntimeException {

    public NeCTRv2Exception(Exception ex) {
        super(ex);
    }

}
