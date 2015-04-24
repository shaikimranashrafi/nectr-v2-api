package com.connectedworldservices.nectr.v2.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestJourney;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestJourneyInfo;
import com.connectedworldservices.nectr.v2.api.rest.service.TestJourneyService;

@RestController
public class TestJourneyController {

    private TestJourneyService testJourneyService;

    @Autowired
    public TestJourneyController(TestJourneyService testJourneyService) {
        this.testJourneyService = testJourneyService;
    }

    //@formatter:off
    @RequestMapping(value = "/journeys/{environmentId}/{journeyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestJourney> getJourney(@PathVariable String environmentId, @PathVariable String journeyId) {

        return new ResponseEntity<TestJourney>(
                testJourneyService.loadTestJourney(environmentId, journeyId),
                HttpStatus.OK
                );
    }

    @RequestMapping(value = "/journeys/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestJourneyInfo>> searchJourneys(@RequestParam(value = "q", required = false) List<SearchCriteria> criteria) {

        return new ResponseEntity<List<TestJourneyInfo>>(
                testJourneyService.searchJourneys(criteria),
                HttpStatus.OK
                );
    }
    //@formatter:on
}
