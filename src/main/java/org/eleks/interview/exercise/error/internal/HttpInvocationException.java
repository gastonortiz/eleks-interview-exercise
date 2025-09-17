package org.eleks.interview.exercise.error.internal;

public class HttpInvocationException extends InternalInterviewExerciseException {
    private final Object parsedResponse;

    public HttpInvocationException(Object parsedResponse, String url, int statusCode) {
        super("An error response (%s) occurred while invoking '%s'".formatted(statusCode, url));
        this.parsedResponse = parsedResponse;
    }

    /**
     * Adding this method as such because extensions of Throwable arent allowed to have a generic type.
     */
    public <T> T getParsedResponse() {
        return (T) parsedResponse;
    }
}
