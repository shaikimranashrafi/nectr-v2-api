package com.connectedworldservices.nectr.v2.api.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.connectedworldservices.nectr.v2.api.rest.NeCTRv2;
import com.connectedworldservices.nectr.v2.api.rest.config.LogbackConfig;
import com.connectedworldservices.nectr.v2.api.rest.config.SwaggerConfig;
import com.connectedworldservices.nectr.v2.api.rest.mock.FongoConnector;
import com.connectedworldservices.nectr.v2.api.rest.mock.MockObjectMapper;
import com.connectedworldservices.nectr.v2.api.rest.mock.MockTemplate;
import com.connectedworldservices.nectr.v2.api.rest.service.MongoConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;

import de.codecentric.boot.admin.config.SpringBootAdminClientAutoConfiguration;

//@formatter:off
@Configuration
@EnableMongoAuditing
@EnableAutoConfiguration(exclude = {
        SpringBootAdminClientAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@ComponentScan(excludeFilters = {
        //we're using TestNeCTRv2.class instead for mocked components
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NeCTRv2.class),
        //we don't need to enable Swagger for tests
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SwaggerConfig.class),
        //we don't need to enable Logback access for tests
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = LogbackConfig.class),
        //we use a mocked version, the FongoConnector.class
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MongoConnector.class),
        //we use a mocked version, the MockTemplate.class
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MongoTemplate.class),
        //we use a mocked version, the MockObjectMapper.class
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ObjectMapper.class)
})
public class TestNeCTRv2 extends SpringBootServletInitializer {

    @Bean
    public MongoConnector mongoConnector() {
        return new FongoConnector("integration:27017", "integration", "application:27017", "application");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MockTemplate(mongoClient(), "nectr-v2");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new MockObjectMapper();
    }

    @Bean
    public MongoClient mongoClient() {
        return new Fongo("nectr-v2").getMongo();
    }
}
//@formatter:on
