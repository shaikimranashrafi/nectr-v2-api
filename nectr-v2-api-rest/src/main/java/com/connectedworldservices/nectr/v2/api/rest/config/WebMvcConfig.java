package com.connectedworldservices.nectr.v2.api.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.connectedworldservices.nectr.v2.api.rest.support.StringToSearchCriteriaListConverter;
import com.connectedworldservices.nectr.v2.api.rest.support.TestReplayHistoryInterceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testReplayHistoryInterceptor()).addPathPatterns("/replay/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToSearchCriteriaListConverter(conversionService()));
    }

    @Bean
    public TestReplayHistoryInterceptor testReplayHistoryInterceptor() {
        return new TestReplayHistoryInterceptor();
    }

    @Bean
    public ConversionService conversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.afterPropertiesSet();
        return bean.getObject();
    }
}
