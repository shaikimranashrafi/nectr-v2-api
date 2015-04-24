package com.connectedworldservices.nectr.v2.api.rest.listener;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.connectedworldservices.nectr.v2.api.rest.service.ImportExportService;
import com.connectedworldservices.nectr.v2.api.rest.service.TestDataService;

@Slf4j
@Component
public class LoadTestDataListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private ImportExportService importExportService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadDefaultTestData();
    }

    protected void loadDefaultTestData() {
        try {
            List<String> testData = testDataService.loadTestData("master");

            if (CollectionUtils.isEmpty(testData)) {
                log.info("Didn't find any test data in git: {}", testDataService.getUri());
                return;
            }

            log.info("Found {} test data files in git: {}", testData.size(), testDataService.getUri());

            for (String file : testData) {
                importExportService.importTestScenario(new FileSystemResource(file));
            }
        } catch (Exception ex) { //NOSONAR
            log.warn("Failed to import test data from git repo. Please check your configuration: {}", ex.getMessage());
        }

    }
}