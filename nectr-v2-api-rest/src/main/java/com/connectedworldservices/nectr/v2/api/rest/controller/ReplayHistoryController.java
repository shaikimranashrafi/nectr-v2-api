package com.connectedworldservices.nectr.v2.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.connectedworldservices.nectr.v2.api.rest.model.TestReplayHistory;
import com.connectedworldservices.nectr.v2.api.rest.service.ReplayHistoryService;

@RestController
public class ReplayHistoryController {

    private final ReplayHistoryService testReplayHistoryService;

    @Autowired
    public ReplayHistoryController(ReplayHistoryService testReplayHistoryService) {
        this.testReplayHistoryService = testReplayHistoryService;
    }

    //@formatter:off
    @RequestMapping(value = "/history/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestReplayHistory>> getTestReplayHistory(@PathVariable String id) {

        return new ResponseEntity<List<TestReplayHistory>>(
                testReplayHistoryService.loadTestReplayHistory(id),
                HttpStatus.OK
                );
    }
    //@formatter:on
}