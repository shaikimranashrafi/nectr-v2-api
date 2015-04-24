package com.connectedworldservices.nectr.v2.api.rest.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.connectedworldservices.nectr.v2.api.rest.service.ImportExportService;

@RestController
public class ImportExportController {

    private final ImportExportService importExportService;

    @Autowired
    public ImportExportController(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    //@formatter:off
    @RequestMapping(value = "/tests/{id}/export", method = RequestMethod.GET)
    public ResponseEntity<Resource> exportTest(@PathVariable String id, HttpServletResponse response) {

        Resource file = importExportService.exportTestScenario(id);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"");

        return new ResponseEntity<Resource>(
                file,
                HttpStatus.OK
                );
    }

    @RequestMapping(value = "/tests/import", method = RequestMethod.POST)
    public ResponseEntity<Void> importTest(MultipartFile file) throws IOException {

        importExportService.importTestScenario(new ByteArrayResource(file.getBytes()));

        return new ResponseEntity<Void>(
                HttpStatus.CREATED
                );
    }
    //@formatter:on
}
