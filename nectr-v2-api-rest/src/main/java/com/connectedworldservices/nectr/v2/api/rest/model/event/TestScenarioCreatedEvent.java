package com.connectedworldservices.nectr.v2.api.rest.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.context.ApplicationEvent;

@Data
@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = false)
public class TestScenarioCreatedEvent extends ApplicationEvent {

    private String message;

    public TestScenarioCreatedEvent(Object source) {
        this(source, null);
    }

    public TestScenarioCreatedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
