package org.eleks.interview.exercise.controller.exception;

import org.eleks.interview.exercise.error.api.InvalidCityCodeException;
import org.eleks.interview.exercise.model.ApplicationError;
import org.eleks.interview.exercise.model.CityCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.eleks.interview.exercise.model.ErrorCode.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleCodedException_usesErrorStatusAndBody() {
        // given
        InvalidCityCodeException ex = new InvalidCityCodeException("XXX");
        ApplicationError expected = ex.getError();

        // when
        ResponseEntity<ApplicationError> resp = handler.handleCodedException(ex);

        // then
        assertEquals(expected.code().getHttpStatusCode(), resp.getStatusCode().value());
        assertSame(expected, resp.getBody());
    }

    @Test
    void handleParsingException_unwrapsInvalidCityCode_andDelegates() {
        // given: wrap InvalidCityCodeException as cause -> cause of MethodArgumentTypeMismatchException
        InvalidCityCodeException root = new InvalidCityCodeException("bue");
        Throwable wrapped = new IllegalArgumentException("wrapped", root);
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "bue",                // value
                        CityCode.class,       // required type
                        "code",               // parameter name
                        null,                 // MethodParameter (not needed here)
                        wrapped               // cause chain
                );

        // when
        ResponseEntity<ApplicationError> resp = handler.handleParsingException(ex);

        // then
        ApplicationError expected = root.getError();
        assertEquals(expected.code().getHttpStatusCode(), resp.getStatusCode().value());
        assertSame(expected, resp.getBody());
    }

    @Test
    void handleUnexpected_returns500WithUnknownError_andMessage() {
        // given
        String message = "boom";

        // when
        ResponseEntity<ApplicationError> resp = handler.handleUnexpected(new RuntimeException(message));

        // then
        assertEquals(INTERNAL_SERVER_ERROR.value(), resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(UNKNOWN_ERROR, resp.getBody().code());
        assertEquals(message, resp.getBody().message());
    }
}
