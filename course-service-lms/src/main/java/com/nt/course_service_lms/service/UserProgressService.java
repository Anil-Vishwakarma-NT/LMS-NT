package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;

/**
 * Service interface for managing user progress in courses within the Learning Management System.
 * This service provides operations to track, update, and retrieve user progress data
 * for courses and their associated content.
 *
 * @author Course Service Team
 * @version 1.0
 * @since 1.0
 */
public interface UserProgressService {

    /**
     * Updates the progress information for a user in a course.
     * This method processes the provided progress data and persists the changes
     * to the underlying data store.
     *
     * @param progressDTO the progress data transfer object containing user progress information
     *                   including user ID, course ID, content ID, progress percentage, and position
     * @throws IllegalArgumentException if progressDTO is null or contains invalid data
     */
    void updateProgress(UserProgressOutDTO progressDTO);

    /**
     * Retrieves comprehensive course progress information along with metadata for a specific user and course.
     * This method returns detailed progress data including completion status, overall progress percentage,
     * and additional metadata about the course progress.
     *
     * @param userId the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @return CourseProgressWithMetaDTO containing complete progress information and metadata
     * @throws IllegalArgumentException if userId or courseId is null or invalid
     */
    CourseProgressWithMetaDTO getCourseProgressWithMeta(Long userId, Long courseId);

    /**
     * Retrieves the last position (timestamp or sequence number) where the user left off
     * in a specific content item within a course.
     * This is useful for resuming content consumption from where the user previously stopped.
     *
     * @param userId the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @param contentId the unique identifier of the content item
     * @return Integer representing the last position in the content, or null if no progress exists
     * @throws IllegalArgumentException if any of the provided IDs are null or invalid
     */
    Integer getLastPosition(Long userId, Long courseId, Long contentId);

    /**
     * Retrieves the progress percentage for a specific content item within a course for a user.
     * The progress is returned as a percentage value between 0.0 and 100.0.
     *
     * @param userId the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @param contentId the unique identifier of the content item
     * @return Double representing the progress percentage (0.0 to 100.0), or null if no progress exists
     * @throws IllegalArgumentException if any of the provided IDs are null or invalid
     */
    Double getContentProgress(Long userId, Long courseId, Long contentId);
}
