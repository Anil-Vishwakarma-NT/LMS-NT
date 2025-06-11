package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.inDTO.CourseInDTO;
import com.example.course_service_lms.dto.outDTO.CourseOutDTO;
import com.example.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.example.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.example.course_service_lms.entity.Course;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing courses in the LMS.
 * <p>
 * Provides operations for creating, retrieving, updating,
 * deleting courses and checking course existence.
 * </p>
 */
public interface CourseService {

    /**
     * Creates a new course using the provided DTO.
     *
     * @param courseInDTO the DTO containing course details
     * @return the created {@link Course} entity
     */
    CourseOutDTO createCourse(CourseInDTO courseInDTO);

    /**
     * Retrieves all available courses.
     *
     * @return a list of all {@link Course} entities
     */
    List<CourseOutDTO> getAllCourses();

    /**
     * Retrieves a course by its ID.
     *
     * @param courseId the ID of the course to retrieve
     * @return an {@link Optional} containing the course if found, or empty if not
     */
    Optional<CourseOutDTO> getCourseById(Long courseId);

    String getCourseNameById(Long courseId);

    /**
     * Deletes a course by its ID.
     *
     * @param courseId the ID of the course to delete
     * @return a message indicating the result of the deletion
     */
    String deleteCourse(Long courseId);

    /**
     * Updates an existing course using the given DTO and ID.
     *
     * @param courseId the ID of the course to update
     * @param updateCourseInDTO the DTO containing updated course data
     * @return a message indicating the result of the update
     */
    CourseOutDTO updateCourse(Long courseId, UpdateCourseInDTO updateCourseInDTO);

    /**
     * Checks whether a course exists by its ID.
     *
     * @param courseId the ID of the course
     * @return {@code true} if the course exists, {@code false} otherwise
     */
    boolean courseExistsById(Long courseId);
    long countCourses();
    List<CourseSummaryOutDTO> getRecentCourseSummaries();
    List<CourseInfoOutDTO> getCoursesInfo();
}