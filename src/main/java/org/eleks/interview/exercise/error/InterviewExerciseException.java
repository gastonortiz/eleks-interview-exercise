package org.eleks.interview.exercise.error;

/**
 * Parent exception for all exceptions thrown by the interview excercise.
 */
public class InterviewExerciseException extends RuntimeException {
    public InterviewExerciseException(String message) {
        super(message);
    }

    public InterviewExerciseException(String message, Throwable cause) {
        super(message, cause);
    }
}
