package com.connectedworldservices.nectr.v2.api.rest.listener;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioCreatedEvent;

@Slf4j
@Component
public class OnTestScenarioCreatedListener implements ApplicationListener<TestScenarioCreatedEvent> {

    @Override
    public void onApplicationEvent(TestScenarioCreatedEvent event) {
        log.info("Test scenario {} created at {}", ((TestScenario) event.getSource()).getId(), ((TestScenario) event.getSource()).getCreated());
    }

}
