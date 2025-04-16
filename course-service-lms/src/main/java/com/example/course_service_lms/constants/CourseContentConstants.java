package com.example.course_service_lms.constants;

/**
 * Utility class that contains constant messages related to course content operations.
 * These include validation messages, error responses, and success notifications to ensure
 * consistency across the Course Content module.
 */
public final class CourseContentConstants {

    private CourseContentConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /** Validation message when Course ID is missing. */
    public static final String COURSE_ID_NOT_NULL = "Course ID cannot be blank";

    /** Validation message when Course ID is invalid. */
    public static final String COURSE_ID_VALID = "Valid Course ID required";

    /** Validation message when the title field is blank. */
    public static final String TITLE_NOT_BLANK = "Title cannot be blank";

    /** Validation message when the title length exceeds the limit. */
    public static final String TITLE_SIZE_EXCEED = "Title cannot exceed 100 characters";

    /** Validation message when the title length exceeds the limit. */
    public static final int TITLE_SIZE_EXCEED_VALUE = 100;

    /** Validation message when the description field is blank. */
    public static final String DESCRIPTION_NOT_BLANK = "Description cannot be blank";

    /** Validation message when the description length exceeds the limit. */
    public static final String DESCRIPTION_SIZE_EXCEED = "Description cannot exceed 1000 characters";

    /** Validation when the description length exceeds the limit. */
    public static final int DESCRIPTION_SIZE_EXCEED_VALUE = 1000;

    /** Validation message when the video link is blank. */
    public static final String VIDEO_LINK_NOT_BLANK = "video link cannot be blank";

    /** Validation message when the video link is not a valid URL. */
    public static final String VIDEO_LINK_INVALID = "Video link must be a valid URL";

    /** Validation message when the resource link is not a valid URL. */
    public static final String RESOURCE_LINK_INVALID = "Resource link must be a valid URL";

    /** Message indicating that course content with the same parameters already exists. */
    public static final String COURSE_CONTENT_ALREADY_PRESENT = "Course Content Already Present";

    /** Error message when the course is not found. */
    public static final String COURSE_NOT_FOUND = "Course does not exists";

    /** Error message when a specific course content record is not found. */
    public static final String COURSE_CONTENT_NOT_FOUND = "Course Content Not Found";

    /** Message used when no course content records are found. */
    public static final String CONTENT_NOT_FOUND = "No Contents Found";

    /** Message indicating duplicate course content title for a course. */
    public static final String COURSE_CONTENT_DUPLICATE = "Course content with the same title already exists for this course.";

    /** Success message when course content is deleted. */
    public static final String COURSE_CONTENT_DELETED = "Course Content Deleted Successfully";

    /** Success message when course content is updated. */
    public static final String COURSE_CONTENT_UPDATED = "Course content updated successfully";

    /** Message used when no course contents are available in the database. */
    public static final String NO_COURSE_CONTENTS_FOUND = "No Course Contents Found";
}
