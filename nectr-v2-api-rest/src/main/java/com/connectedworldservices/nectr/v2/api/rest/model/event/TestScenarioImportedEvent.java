package com.connectedworldservices.nectr.v2.api.rest.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.context.ApplicationEvent;

@Data
@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = false)
public class TestScenarioImportedEvent extends ApplicationEvent {

    private String message;

    public TestScenarioImportedEvent(Object source) {
        this(source, null);
    }

    public TestScenarioImportedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
