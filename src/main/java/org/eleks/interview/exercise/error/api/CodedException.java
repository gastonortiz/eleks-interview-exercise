package org.eleks.interview.exercise.error.api;

import lombok.Getter;
import org.eleks.interview.exercise.error.InterviewExerciseException;
import org.eleks.interview.exercise.model.ApplicationError;

/**
 * Parent exception for all exceptions that are exposed to the client.
 */
@Getter
public class CodedException extends InterviewExerciseException {
    private final ApplicationError error;

    public CodedException(ApplicationError error) {
        super(error.message());
        this.error = error;
    }

    public CodedException(ApplicationError error, Throwable cause) {
        super(error.message(), cause);
        this.error = error;
    }
}
