package com.connectedworldservices.nectr.v2.api.rest.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioExportedEvent;
import com.connectedworldservices.nectr.v2.api.rest.model.event.TestScenarioImportedEvent;
import com.connectedworldservices.nectr.v2.api.rest.support.NeCTRv2Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class ImportExportService implements ApplicationContextAware {

    private final ObjectMapper objectMapper;

    private final MongoTemplate mongoTemplate;

    private final TestScenarioTemplate testScenarioTemplate;

    private ApplicationContext applicationContext;

    @Autowired
    public ImportExportService(ObjectMapper objectMapper, MongoTemplate mongoTemplate, MongoConnector mongoConnector) {
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.testScenarioTemplate = new TestScenarioTemplate(mongoTemplate, mongoConnector);
    }

    public Resource exportTestScenario(String testId) {
        checkNotNull(testId, "testId cannot be null");

        try {
            TestScenario testScenario = mongoTemplate.findById(testId, TestScenario.class, TestScenario.COLLECTION);

            if (testScenario == null) {
                throw new NotFoundException(testId + " doesn't exist");
            }

            File directory = NeCTRv2Utils.createTempDirectory("nectr-v2-export-");

            File export = new File(directory, format("%s.json", testScenario.getId()));

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(export, testScenario);

            File zip = new File(directory, format("%s.zip", testScenario.getId()));

            ZipUtil.packEntry(export, zip);

            publishEvent(new TestScenarioExportedEvent(testScenario, format("Test Scenario %s Exported: ", testScenario.getId())));

            return new FileSystemResource(zip);

        } catch (IOException ex) {
            throw new NeCTRv2Exception(ex);
        }
    }

    public void importTestScenario(Resource resource) {
        checkNotNull(resource, "resource cannot be null");

        File directory = null;
        List<String> created = new ArrayList<>();
        try {
            //Check if the resource is a zip
            checkArgument(new ZipInputStream(resource.getInputStream()).getNextEntry() != null);

            directory = NeCTRv2Utils.createTempDirectory("nectr-v2-import-");

            ZipUtil.unpack(resource.getInputStream(), directory);

            for (File file : directory.listFiles()) {

                TestScenario testScenario = objectMapper.readValue(file, TestScenario.class);

                testScenarioTemplate.validate(testScenario);

                testScenarioTemplate.create(testScenario, false);

                //store created so we can rollback if needed
                created.add(testScenario.getId());

                publishEvent(new TestScenarioImportedEvent(testScenario, format("Test Scenario %s Imported: ", testScenario.getId())));
            }
        } catch (Exception ex) {
            for (String testId : created) {
                testScenarioTemplate.delete(testId);
            }
            throw new NeCTRv2Exception(ex);
        } finally {
            if (directory != null) {
                NeCTRv2Utils.deleteDirectory(directory);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }
}
