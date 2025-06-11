package com.example.course_service_lms.service;


import com.example.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.example.course_service_lms.entity.CourseContent;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing course content.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting
 * course content associated with a course.
 * </p>
 */
public interface CourseContentService {

    /**
     * Creates new course content.
     *
     * @param courseContentInDTO the data transfer object containing content details
     * @return the created {@link CourseContent} entity
     */
    CourseContent createCourseContent(CourseContentInDTO courseContentInDTO);

    /**
     * Retrieves all course content records.
     *
     * @return a list of all {@link CourseContent} entities
     */
    List<CourseContent> getAllCourseContents();

    /**
     * Retrieves a specific course content record by its ID.
     *
     * @param courseContentId the ID of the course content to retrieve
     * @return an {@link Optional} containing the found {@link CourseContent} or empty if not found
     */
    Optional<CourseContent> getCourseContentById(Long courseContentId);

    /**
     * Deletes a course content record by its ID.
     *
     * @param courseContentId the ID of the course content to delete
     * @return a success message indicating the outcome
     */
    String deleteCourseContent(Long courseContentId);

    /**
     * Updates course content for a given course ID.
     *
     * @param courseId the ID of the course whose content is to be updated
     * @param updateCourseContentInDTO the updated content data
     * @return a success message indicating the outcome
     */
    String updateCourseContent(Long courseId, UpdateCourseContentInDTO updateCourseContentInDTO);

    /**
     * Retrieves all course content items associated with a specific course.
     *
     * @param courseId the ID of the course
     * @return a list of {@link CourseContent} records associated with the course
     */
    List<CourseContent> getAllCourseContentByCourseId(Long courseId);
}
