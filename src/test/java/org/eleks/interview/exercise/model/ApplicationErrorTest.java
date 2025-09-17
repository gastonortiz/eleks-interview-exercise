package org.eleks.interview.exercise.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.eleks.interview.exercise.model.ErrorCode.MISSING_API_KEY;
import static org.eleks.interview.exercise.model.ErrorCode.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationErrorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializesUsingAnnotatedErrorDeserializer_knownCode() throws Exception {
        String json = """
            {
              "error": {
                "code": 1002,
                "message": "API key required"
              }
            }
            """;

        ApplicationError err = mapper.readValue(json, ApplicationError.class);

        assertEquals(MISSING_API_KEY, err.code());
        assertEquals("API key required", err.message());
        assertEquals("MISSING_API_KEY: API key required", err.toString());
    }

    @Test
    public void deserializesUnknownCode_toUnknownError() throws Exception {
        String json = """
            {
              "error": {
                "code": 9999,
                "message": "Something else"
              }
            }
            """;

        ApplicationError err = mapper.readValue(json, ApplicationError.class);

        assertEquals(UNKNOWN_ERROR, err.code());
        assertEquals("Something else", err.message());
        assertEquals("UNKNOWN_ERROR: Something else", err.toString());
    }
}
