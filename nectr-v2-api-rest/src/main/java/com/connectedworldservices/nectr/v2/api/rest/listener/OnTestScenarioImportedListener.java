package com.connectedworldservices.nectr.v2.api.rest.listener;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioImportedEvent;

@Slf4j
@Component
public class OnTestScenarioImportedListener implements ApplicationListener<TestScenarioImportedEvent> {

    @Override
    public void onApplicationEvent(TestScenarioImportedEvent event) {
        log.info("Test scenario {} imported at {}", ((TestScenario) event.getSource()).getId(), ((TestScenario) event.getSource()).getCreated());
    }

}
