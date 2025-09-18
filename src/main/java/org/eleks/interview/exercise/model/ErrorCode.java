package org.eleks.interview.exercise.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    INVALID_CITY_CODE(BAD_REQUEST),
    CITY_NAME_MISCONFIGURATION(INTERNAL_SERVER_ERROR),
    API_NOT_FOUND(INTERNAL_SERVER_ERROR),
    MISSING_API_KEY(INTERNAL_SERVER_ERROR),
    INVALID_API_KEY(INTERNAL_SERVER_ERROR),
    EXCEEDED_API_CALLS_LIMIT(FORBIDDEN),
    DISABLED_API_KEY(FORBIDDEN),
    UNKNOWN_ERROR(INTERNAL_SERVER_ERROR);

    private final int httpStatusCode;

    ErrorCode(HttpStatus httpStatusCode) {
        this.httpStatusCode = httpStatusCode.value();
    }
}
