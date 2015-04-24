package com.connectedworldservices.nectr.v2.api.rest.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = false)
public class TestScenarioDeletedEvent extends TestScenarioCreatedEvent {

    private String message;

    public TestScenarioDeletedEvent(Object source) {
        this(source, null);
    }

    public TestScenarioDeletedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
