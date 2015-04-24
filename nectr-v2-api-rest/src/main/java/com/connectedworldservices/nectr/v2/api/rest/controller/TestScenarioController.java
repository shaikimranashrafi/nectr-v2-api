package com.connectedworldservices.nectr.v2.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.CreateTestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestScenarioInfo;
import com.connectedworldservices.nectr.v2.api.rest.model.dto.UpdateTestScenario;
import com.connectedworldservices.nectr.v2.api.rest.service.ReplayDataService;
import com.connectedworldservices.nectr.v2.api.rest.service.TestScenarioService;

@RestController
public class TestScenarioController {

    private final TestScenarioService testScenarioService;

    @Autowired
    public TestScenarioController(TestScenarioService testScenarioService, ReplayDataService applicationDataService) {
        this.testScenarioService = testScenarioService;
    }

    //@formatter:off
    @RequestMapping(value = "/tests/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestScenario> getTest(@PathVariable String id) {

        return new ResponseEntity<TestScenario>(
                testScenarioService.loadTestScenario(id),
                HttpStatus.OK
                );
    }

    @RequestMapping(value = "/tests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestScenarioInfo> createTest(@RequestBody CreateTestScenario input) {

        return new ResponseEntity<TestScenarioInfo>(
                testScenarioService.createTestScenario(
                        input.getEnvironmentId(),
                        input.getCarrier(),
                        input.getCountry(),
                        input.getClientId(),
                        input.getJourneyName(),
                        input.getJourneyId(),
                        input.getMetadata()
                        ),
                        HttpStatus.CREATED
                );
    }

    @RequestMapping(value = "/tests/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestScenarioInfo> updateTest(@RequestBody UpdateTestScenario input, @PathVariable String id) {

        return new ResponseEntity<TestScenarioInfo>(
                testScenarioService.updateTestScenario(
                        id,
                        input.getMetadata()
                        ),
                        HttpStatus.OK
                );
    }

    @RequestMapping(value = "/tests/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteTest(@PathVariable String id) {

        testScenarioService.deleteTestScenario(id);

        return new ResponseEntity<Void>(
                HttpStatus.NO_CONTENT
                );
    }

    @RequestMapping(value = "/tests/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestScenarioInfo>> searchTest(@RequestParam(value = "q", required = false) List<SearchCriteria> criteria) {

        return new ResponseEntity<List<TestScenarioInfo>>(
                testScenarioService.searchTestScenarios(criteria),
                HttpStatus.OK
                );
    }
    //@formatter:on
}
