package com.connectedworldservices.nectr.v2.api.rest.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.connectedworldservices.nectr.v2.api.rest.model.TestReplayHistory;
import com.connectedworldservices.nectr.v2.api.rest.repository.TestReplayHistoryRepository;

public class TestReplayHistoryInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TestReplayHistoryRepository testReplayHistoryRepository;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        checkNotNull(pathVariables.get("id")); //testId

        TestReplayHistory entry = new TestReplayHistory();

        if (request.getRemoteUser() != null) {
            entry.setUser(request.getRemoteUser());
        }

        entry.setTestId(pathVariables.get("id").toString());
        entry.setTimestamp(new Date());
        entry.setRequestURI(request.getRequestURI());
        entry.setRemoteAddress(request.getRemoteAddr());
        entry.setParameters(new HashMap<>());
        entry.getParameters().putAll(request.getParameterMap());

        testReplayHistoryRepository.save(entry);

        return super.preHandle(request, response, handler);
    }
}
