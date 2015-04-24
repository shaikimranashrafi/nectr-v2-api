package com.connectedworldservices.nectr.v2.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import de.codecentric.boot.admin.config.EnableAdminServer;

@EnableAdminServer
@EnableMongoAuditing
@SpringBootApplication
@EnableConfigurationProperties
public class NeCTRv2 extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(NeCTRv2.class);
    }

    public static void main(String[] args) throws Exception { //NOSONAR
        SpringApplication.run(NeCTRv2.class, args);
    }
}
