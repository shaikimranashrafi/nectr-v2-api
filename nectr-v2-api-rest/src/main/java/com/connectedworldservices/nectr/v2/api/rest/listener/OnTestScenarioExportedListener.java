package com.connectedworldservices.nectr.v2.api.rest.listener;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioExportedEvent;

@Slf4j
@Component
public class OnTestScenarioExportedListener implements ApplicationListener<TestScenarioExportedEvent> {

    @Override
    public void onApplicationEvent(TestScenarioExportedEvent event) {
        log.info("Test scenario {} exported at {}", ((TestScenario) event.getSource()).getId(), new Date());
    }

}
