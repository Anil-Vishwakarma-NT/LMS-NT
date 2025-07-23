package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;

import java.util.List;

/**
 * Service interface for managing courses in the Learning Management System (LMS).
 *
 * <p>This interface defines the contract for course-related operations including
 * creation, retrieval, updating, deletion, and various query operations for courses.</p>
 *
 * @author Course Service Team
 * @version 1.0
 * @since 1.0
 */
public interface CourseService {

    /**
     * Creates a new course in the system.
     *
     * @param courseInDTO the data transfer object containing course creation details
     * @return the created course as a {@link CourseOutDTO}
     * @throws IllegalArgumentException if the courseInDTO is null or invalid
     */
    CourseOutDTO createCourse(CourseInDTO courseInDTO);

    /**
     * Retrieves all available courses from the system.
     *
     * @return a list of all courses as {@link CourseOutDTO} objects
     */
    List<CourseOutDTO> getAllCourses();

    /**
     * Retrieves detailed information about a specific course by its identifier.
     *
     * @param courseId the unique identifier of the course to retrieve
     * @return the course information as a {@link CourseInfoOutDTO}
     * @throws IllegalArgumentException if courseId is null or invalid
     */
    CourseInfoOutDTO getCourseById(Long courseId);

    /**
     * Retrieves the name of a course by its identifier.
     *
     * @param courseId the unique identifier of the course
     * @return the name of the course as a string
     * @throws IllegalArgumentException if courseId is null or invalid
     */
    String getCourseNameById(Long courseId);

    /**
     * Deletes a course from the system by its identifier.
     *
     * @param courseId the unique identifier of the course to delete
     * @return a status message indicating the result of the deletion operation
     * @throws IllegalArgumentException if courseId is null or invalid
     */
    String deleteCourse(Long courseId);

    /**
     * Updates an existing course with new information.
     *
     * @param courseId          the unique identifier of the course to update
     * @param updateCourseInDTO the data transfer object containing updated course information
     * @return the updated course as a {@link CourseOutDTO}
     * @throws IllegalArgumentException if courseId is null or updateCourseInDTO is invalid
     */
    CourseOutDTO updateCourse(Long courseId, UpdateCourseInDTO updateCourseInDTO);

    /**
     * Checks whether a course exists in the system by its identifier.
     *
     * @param courseId the unique identifier of the course to check
     * @return {@code true} if the course exists, {@code false} otherwise
     * @throws IllegalArgumentException if courseId is null
     */
    boolean courseExistsById(Long courseId);

    /**
     * Retrieves the total number of courses in the system.
     *
     * @return the total count of courses as a long value
     */
    long countCourses();

    /**
     * Retrieves summary information of recently created courses.
     *
     * @return a list of recent course summaries as {@link CourseSummaryOutDTO} objects
     */
    List<CourseSummaryOutDTO> getRecentCourseSummaries();

    /**
     * Retrieves detailed information for all courses in the system.
     *
     * @return a list of all course information as {@link CourseInfoOutDTO} objects
     */
    List<CourseInfoOutDTO> getCoursesInfo();

    /**
     * Filters and returns existing course identifiers from the provided list.
     *
     * @param courseIds the list of course identifiers to validate
     * @return a list containing only the identifiers that exist in the system
     * @throws IllegalArgumentException if courseIds is null
     */
    List<Long> findExistingIds(List<Long> courseIds);
}
