package org.eleks.interview.exercise.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.eleks.interview.exercise.error.api.CodedException;
import org.eleks.interview.exercise.error.api.InvalidCityCodeException;
import org.eleks.interview.exercise.model.ApplicationError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.eleks.interview.exercise.model.ErrorCode.UNKNOWN_ERROR;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CodedException.class)
    public ResponseEntity<ApplicationError> handleCodedException(CodedException ex) {
        ApplicationError error = ex.getError();
        log.error("An error has occurred: {}", error, ex);
        return ResponseEntity
                .status(error.code().getHttpStatusCode())
                .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApplicationError> handleParsingException(MethodArgumentTypeMismatchException ex) {
        log.error("An error has occurred while parsing the city code path parameter: {}", ex.getMessage(), ex);
        return handleCodedException((InvalidCityCodeException) ex.getCause().getCause());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationError> handleUnexpected(Exception ex) {
        log.error("An unexpected error has occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ApplicationError(UNKNOWN_ERROR, ex.getMessage()));
    }
}

