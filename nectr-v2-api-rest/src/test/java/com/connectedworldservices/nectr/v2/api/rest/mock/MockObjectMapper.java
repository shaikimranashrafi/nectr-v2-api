package com.connectedworldservices.nectr.v2.api.rest.mock;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 1L;

    private boolean throwExceptionOnWrite = false;
    private boolean throwExceptionOnRead = false;

    private Class<?> throwExceptionOnWriteForClass;
    private Class<?> throwExceptionOnReadForClass;

    @Override
    public void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        if (throwExceptionOnWrite && value.getClass().equals(throwExceptionOnWriteForClass)) {
            throw new MockIOException(format("[throwExceptionOnWrite=%s,throwExceptionOnWriteForClass=%s]", throwExceptionOnWrite, throwExceptionOnWriteForClass));
        }
        super.writeValue(resultFile, value);
    }

    @Override
    public <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        if (throwExceptionOnRead && valueType.equals(throwExceptionOnReadForClass)) {
            throw new MockIOException(format("[throwExceptionOnRead=%s,throwExceptionOnReadForClass=%s]", throwExceptionOnRead, throwExceptionOnReadForClass));
        }
        return super.readValue(src, valueType);
    }

    public void clearThrowExceptionOnWrite() {
        setThrowExceptionOnWrite(false, null);
    }

    public void setThrowExceptionOnWrite(boolean throwExceptionOnWrite, Class<?> throwExceptionOnWriteForClass) {
        this.throwExceptionOnWrite = throwExceptionOnWrite;
        this.throwExceptionOnWriteForClass = throwExceptionOnWriteForClass;
    }

    public void clearThrowExceptionOnRead() {
        setThrowExceptionOnRead(false, null);
    }

    public void setThrowExceptionOnRead(boolean throwExceptionOnRead, Class<?> throwExceptionOnReadForClass) {
        this.throwExceptionOnRead = throwExceptionOnRead;
        this.throwExceptionOnReadForClass = throwExceptionOnReadForClass;
    }

    private static class MockIOException extends IOException {

        private static final long serialVersionUID = 1L;

        public MockIOException(String message) {
            super(message);
        }
    }
}
