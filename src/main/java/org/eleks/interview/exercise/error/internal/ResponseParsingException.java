package org.eleks.interview.exercise.error.internal;

public class ResponseParsingException extends InternalInterviewExerciseException {
    public ResponseParsingException(Throwable cause) {
        super("An error occurred while parsing the response from an HTTP invocation.", cause);
    }
}
