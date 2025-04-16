package com.example.course_service_lms.constants;

/**
 * Utility class containing constant messages used throughout the Course module.
 * These constants include validation messages, exception texts, and operation success messages
 * to maintain consistency across the application.
 */
public final class CourseConstants {

    private CourseConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /** Message indicating that a course with the given title already exists. */
    public static final String COURSE_ALREADY_EXISTS = "A course with this name already exists";

    /** Message used when no course is found in the database. */
    public static final String COURSE_NOT_FOUND = "No Course Found";

    /** Message used when a course with the same title already exists for a specific owner. */
    public static final String COURSE_DUPLICATE_FOR_OWNER = "A course with this title already exists for this owner";

    /** Success message when a course is deleted successfully. */
    public static final String COURSE_DELETED_SUCCESSFULLY = "Course Deleted Successfully";

    /** Success message when a course is updated successfully. */
    public static final String COURSE_UPDATED_SUCCESSFULLY = "Course Updated Successfully";

    /** Validation message indicating that the course title cannot be blank. */
    public static final String TITLE_BLANK = "Title cannot be blank";

    /** Validation message indicating the minimum length required for a course title. */
    public static final String TITLE_MIN_LENGTH = "Title must be at least 3 characters long";

    /** Validation message indicating that the owner ID must not be blank. */
    public static final String OWNER_ID_BLANK = "OwnerId cannot be blank";

    /** Validation message indicating that the owner ID provided is invalid. */
    public static final String OWNER_ID_INVALID = "Valid Owner ID required";

    /** Validation message indicating that the course description must not be blank. */
    public static final String DESCRIPTION_BLANK = "Description cannot be blank";

    /** Validation message indicating the minimum length required for a course description. */
    public static final String DESCRIPTION_MIN_LENGTH = "Description must be at least 3 characters long";

    /** Validation message indicating that the course level must be provided. */
    public static final String COURSE_LEVEL_REQUIRED = "Course level is required";
}
