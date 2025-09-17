package org.eleks.interview.exercise.error.api;

import org.eleks.interview.exercise.model.ApplicationError;

import static org.eleks.interview.exercise.model.ErrorCode.INVALID_CITY_CODE;

public class InvalidCityCodeException extends CodedException {

    public InvalidCityCodeException(String code) {
        super(new ApplicationError(INVALID_CITY_CODE, "City code '%s' is not valid.".formatted(code)));
    }
}
