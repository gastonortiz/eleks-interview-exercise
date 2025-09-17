package org.eleks.interview.exercise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eleks.interview.exercise.model.deserializer.ErrorDeserializer;

@JsonDeserialize(using = ErrorDeserializer.class)
public record ApplicationError(ErrorCode code, String message) {

    @Override
    public String toString() {
        return "%s: %s".formatted(code, message);
    }
}
