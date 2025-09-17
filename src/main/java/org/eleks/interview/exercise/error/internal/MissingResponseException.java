package org.eleks.interview.exercise.error.internal;

public class MissingResponseException extends InternalInterviewExerciseException {

    public MissingResponseException() {
        super("Response from API is missing.");
    }
}
