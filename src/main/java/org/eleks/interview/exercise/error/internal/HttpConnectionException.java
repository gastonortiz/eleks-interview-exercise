package org.eleks.interview.exercise.error.internal;

public class HttpConnectionException extends InternalInterviewExerciseException {
    public HttpConnectionException(Throwable cause) {
        super("An error occurred while connecting to the server. This can be because of cancellation, a connectivity problem or timeout.", cause);
    }
}
