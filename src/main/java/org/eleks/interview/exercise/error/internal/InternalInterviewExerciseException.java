package org.eleks.interview.exercise.error.internal;

import org.eleks.interview.exercise.error.InterviewExerciseException;

/**
 * Parent exception for all exceptions thrown by the interview exercise that are not exposed to the client.
 */
public class InternalInterviewExerciseException extends InterviewExerciseException {
    public InternalInterviewExerciseException(String message) {
        super(message);
    }

    public InternalInterviewExerciseException(String message, Throwable cause) {
        super(message, cause);
    }
}
