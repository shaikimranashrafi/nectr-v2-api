package com.connectedworldservices.nectr.v2.api.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.connectedworldservices.nectr.v2.api.rest.service.ReplayDataService;

@RestController
public class ReplayDataController {

    private final ReplayDataService applicationDataService;

    @Autowired
    public ReplayDataController(ReplayDataService applicationDataService) {
        this.applicationDataService = applicationDataService;
    }

    //@formatter:off
    @RequestMapping(value = "/replay/{id}", params = "fields=application", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<?, ?>> replayApplicationData(@PathVariable String id) { //NOSONAR

        return new ResponseEntity<Map<?, ?>>(
                applicationDataService.replayApplicationData(id),
                HttpStatus.OK
                );
    }
    //@formatter:on
}
