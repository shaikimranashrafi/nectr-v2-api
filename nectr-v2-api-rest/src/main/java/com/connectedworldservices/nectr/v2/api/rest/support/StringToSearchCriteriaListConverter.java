package com.connectedworldservices.nectr.v2.api.rest.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.connectedworldservices.nectr.v2.api.rest.model.dto.SearchCriteria;

public class StringToSearchCriteriaListConverter implements Converter<String, List<SearchCriteria>> {

    private static final Pattern QUERY_PATTERN = Pattern.compile("(.+?)(:|<|>)(.+?),");

    private ConversionService conversionService;

    public StringToSearchCriteriaListConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public List<SearchCriteria> convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        Matcher matcher = QUERY_PATTERN.matcher(source + (source.endsWith(",") ? "" : ","));

        List<SearchCriteria> criteria = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            String op = matcher.group(2);
            Object value = matcher.group(3);

            try {
                value = conversionService.convert(value, Integer.class);
            } catch (ConversionFailedException ex1) { // NOSONAR
                try {
                    value = conversionService.convert(value, Boolean.class);
                } catch (ConversionFailedException ex2) { // NOSONAR
                    try {
                        value = conversionService.convert(value, Date.class);
                    } catch (ConversionFailedException ex3) { // NOSONAR
                        value = conversionService.convert(NeCTRv2Utils.unquote(value), String.class);
                    }
                }
            }

            criteria.add(new SearchCriteria(key, op, value));
        }

        return criteria;
    }
}
