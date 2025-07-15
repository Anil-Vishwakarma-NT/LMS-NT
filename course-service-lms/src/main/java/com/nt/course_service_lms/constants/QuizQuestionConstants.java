package com.nt.course_service_lms.constants;

/**
 * Utility class that contains constant values used across the Quiz Question module.
 * <p>
 * This class is not meant to be instantiated.
 * All members are static and provide standardized messages for validations and exceptions.
 */
public final class QuizQuestionConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private QuizQuestionConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generic error message for unexpected situations.
     */
    public static final String GENERAL_ERROR = "An error occurred while processing the request";

    /**
     * Message used when a question is not found for a given ID.
     */
    public static final String QUESTION_NOT_FOUND = "Question not found with ID: %d";

    /**
     * Message used when a question with the same position already exists in a quiz.
     */
    public static final String POSITION_EXISTS = "Question with position %d already exists in quiz %d";
}
