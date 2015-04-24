package com.connectedworldservices.nectr.v2.api.rest.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4JServletContextListener;
import ch.qos.logback.access.servlet.TeeFilter;
import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration
@ConditionalOnExpression("${logback.access.enabled:true}")
public class LogbackConfig {

    //this bean enables the PatternLayout converters %fullRequest and %fullResponse
    @Bean
    public FilterRegistrationBean teeFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TeeFilter());
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        registration.addUrlPatterns("/tests/*", "/journeys/*", "/environments/*", "/imex/*");
        return registration;
    }

    //this bean ensures sysout of the teefilter is channelled through slf4j
    @Bean
    public ServletContextListener sysOutOverSLF4JServletContextListener() {
        return new SysOutOverSLF4JServletContextListener();
    }

    //this bean install the logback tomcat valve in the container
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {

                if (container instanceof TomcatEmbeddedServletContainerFactory) {
                    TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;

                    LogbackValve logbackValve = new LogbackValve();
                    logbackValve.setQuiet(true);
                    logbackValve.setFilename("src/main/resources/logback-access.xml");
                    containerFactory.addContextValves(logbackValve);
                }
            }
        };
    }
}
