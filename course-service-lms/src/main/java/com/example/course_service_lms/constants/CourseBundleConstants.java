package com.example.course_service_lms.constants;

/**
 * Utility class that holds constant values for validation messages, error responses,
 * and exception details used in the Course-Bundle mapping functionality.
 * <p>
 * These constants help maintain consistency in messages across the application.
 */
public final class CourseBundleConstants {

    private CourseBundleConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /** Message indicating that the Bundle ID cannot be null. */
    public static final String BUNDLE_ID_NOT_NULL = "Bundle ID cannot be null";

    /** Message indicating that the Bundle ID must be a positive number. */
    public static final String BUNDLE_ID_POSITIVE = "Bundle ID must be a positive number";

    /** Message indicating that the Course ID cannot be null. */
    public static final String COURSE_ID_NOT_NULL = "Course ID cannot be null";

    /** Message indicating that the Course ID must be a positive number. */
    public static final String COURSE_ID_POSITIVE = "Course ID must be a positive number";

    /** Message used when no course-bundle records are found. */
    public static final String NO_COURSE_BUNDLES_FOUND = "No course-bundle records found";

    /** Message used when the specified bundle is not found. */
    public static final String BUNDLE_NOT_FOUND = "Bundle not found";

    /** Message used when the specified course is not found. */
    public static final String COURSE_NOT_FOUND = "Course not found";

    /** Message used when a course-bundle mapping is not found for the given ID. */
    public static final String COURSE_BUNDLE_NOT_FOUND_BY_ID = "Course-bundle record not found for ID: ";

    /** Message used when a duplicate course-bundle mapping already exists. */
    public static final String COURSE_BUNDLE_ALREADY_EXISTS =
            "Course-bundle mapping already exists for the provided Bundle ID and Course ID";

    /** Message used when the bundle ID provided is invalid. */
    public static final String INVALID_BUNDLE_ID = "Invalid Bundle ID: ";

    /** Message used when the course ID provided is invalid. */
    public static final String INVALID_COURSE_ID = "Invalid Course ID: ";

    /** Message used when an error occurs while fetching course-bundle records. */
    public static final String FAILED_TO_FETCH_COURSE_BUNDLE = "Failed to fetch course-bundle records";

    /** Message used when an error occurs while fetching a specific course-bundle by ID. */
    public static final String FAILED_TO_FETCH_COURSE_BUNDLE_BY_ID = "Failed to fetch course-bundle record with ID: ";

    /** Message used when an error occurs while deleting a course-bundle record. */
    public static final String FAILED_TO_DELETE_COURSE_BUNDLE =
            "Something went wrong while deleting course-bundle record with ID: ";

    /** Message used when an error occurs while updating a course-bundle record. */
    public static final String FAILED_TO_UPDATE_COURSE_BUNDLE =
            "Something went wrong while updating course-bundle record with ID: ";

    /** Message used when an error occurs while creating a course-bundle mapping. */
    public static final String FAILED_TO_CREATE_COURSE_BUNDLE = "Something went wrong while creating course-bundle mapping";
}
