package com.nt.course_service_lms.constants;

/**
 * Utility class that contains constant values used across the Quiz module.
 * <p>
 * This class is not meant to be instantiated.
 * All members are static and provide standardized messages for validations and exceptions.
 */
public final class QuizConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private QuizConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generic error message for unexpected situations.
     */
    public static final String GENERAL_ERROR = "Something went wrong";

    /**
     * Message used when a quiz already exists.
     */
    public static final String QUIZ_EXISTS = "Quiz Exists";

    /**
     * Message used when no quiz is found.
     */
    public static final String NO_QUIZ_FOUND = "No Quiz Found";

    /**
     * Message used when no quiz is found with a specific ID.
     */
    public static final String NO_QUIZ_WITH_ID = "No Quiz With ID";

    /**
     * Message used when no quiz is found for a specific course ID.
     */
    public static final String NO_QUIZ_FOR_COURSE_ID = "No Quiz Found for this CourseID";

    /**
     * Message used when no quiz is found for a specific course content.
     */
    public static final String NO_QUIZ_FOR_COURSE_CONTENT = "No Quiz Found for this Coursecontent";
}

