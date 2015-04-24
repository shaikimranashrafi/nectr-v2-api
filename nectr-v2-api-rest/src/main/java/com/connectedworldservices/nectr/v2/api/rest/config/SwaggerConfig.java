package com.connectedworldservices.nectr.v2.api.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@Configuration
@EnableSwagger
public class SwaggerConfig {

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    @Value("${info.version}")
    private String version;

    //@formatter:off
    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
        .apiInfo(apiInfo())
        .apiVersion(version)
        .includePatterns(
                "/tests.*",
                "/journeys.*",
                "/environments.*"
                );
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "NeCTR v2 API",
                "",
                "",
                "",
                "",
                ""
                );
    }
    //@formatter:on
}
