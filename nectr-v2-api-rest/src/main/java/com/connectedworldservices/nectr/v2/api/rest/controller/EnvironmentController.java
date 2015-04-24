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
import org.springframework.web.bind.annotation.RestController;

import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.service.EnvironmentService;

@RestController
public class EnvironmentController {

    private EnvironmentService environmentService;

    @Autowired
    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    //@formatter:off
    @RequestMapping(value = "/environments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Environment>> getEnvironments() {

        return new ResponseEntity<List<Environment>>(
                environmentService.loadEnvironments(),
                HttpStatus.OK
                );
    }

    @RequestMapping(value = "/environments", params = "fields=id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getEnvironmentIds() {

        return new ResponseEntity<List<String>>(
                environmentService.loadEnvironmentIds(),
                HttpStatus.OK
                );
    }

    @RequestMapping(value = "/environments/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Environment> getEnvironment(@PathVariable String id) {

        return new ResponseEntity<Environment>(
                environmentService.loadEnvironment(id),
                HttpStatus.OK
                );
    }

    @RequestMapping(value = "/environments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Environment> createEnvironment(@RequestBody Environment environment) {

        return new ResponseEntity<Environment>(
                environmentService.createEnvironment(environment),
                HttpStatus.CREATED
                );
    }

    @RequestMapping(value = "/environments/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Environment> updateEnvironment(@PathVariable String id, @RequestBody Environment environment) {

        return new ResponseEntity<Environment>(
                environmentService.updateEnvironment(id, environment.getIntegrationHost(), environment.getApplicationHost()),
                HttpStatus.OK
                );

    }

    @RequestMapping(value = "/environments/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteEnvironment(@PathVariable String id) {

        environmentService.deleteEnvironment(id);

        return new ResponseEntity<Void>(
                HttpStatus.NO_CONTENT
                );
    }
    //@formatter:on
}
