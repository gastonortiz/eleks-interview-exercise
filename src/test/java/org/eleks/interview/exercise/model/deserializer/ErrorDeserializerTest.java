package org.eleks.interview.exercise.model.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.eleks.interview.exercise.model.ApplicationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.eleks.interview.exercise.model.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ApplicationError.class, new ErrorDeserializer());
        mapper.registerModule(module);
    }

    private ApplicationError parse(int code, String message) throws Exception {
        String json = """
            {
              "error": {
                "code": %d,
                "message": "%s"
              }
            }
            """.formatted(code, message.replace("\"", "\\\""));
        return mapper.readValue(json, ApplicationError.class);
    }

    @Test
    public void maps1002_toMissingApiKey_andPreservesMessage() throws Exception {
        ApplicationError err = parse(1002, "API key required");
        assertEquals(MISSING_API_KEY, err.code());
        assertEquals("API key required", err.message());
    }

    @Test
    public void maps1005_toApiNotFound() throws Exception {
        ApplicationError err = parse(1005, "API not found");
        assertEquals(API_NOT_FOUND, err.code());
        assertEquals("API not found", err.message());
    }

    @Test
    public void maps1006_toCityNameMisconfiguration() throws Exception {
        ApplicationError err = parse(1006, "City name misconfiguration");
        assertEquals(CITY_NAME_MISCONFIGURATION, err.code());
        assertEquals("City name misconfiguration", err.message());
    }

    @ParameterizedTest
    @ValueSource(ints = {2006, 2008, 2009})
    public void maps2006_2008_2009_toInvalidApiKey(int errorCode) throws Exception {
        ApplicationError err = parse(errorCode, "Invalid API key");
        assertEquals(INVALID_API_KEY, err.code());
        assertEquals("Invalid API key", err.message());
    }

    @Test
    public void maps2007_toExceededApiCallsLimit() throws Exception {
        ApplicationError err = parse(2007, "Quota exceeded");
        assertEquals(EXCEEDED_API_CALLS_LIMIT, err.code());
        assertEquals("Quota exceeded", err.message());
    }

    @Test
    public void mapsUnknownCode_toUnknownError() throws Exception {
        ApplicationError err = parse(9999, "Something else");
        assertEquals(UNKNOWN_ERROR, err.code());
        assertEquals("Something else", err.message());
    }
}
