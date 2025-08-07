package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.DashboardDataOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing courses in the Learning Management System (LMS).
 *
 * <p>This controller provides comprehensive endpoints for CRUD operations on courses,
 * course validation, and course information retrieval for the LMS Course Service.
 * It handles HTTP requests related to course management and delegates business logic
 * to the {@link CourseService}.</p>
 *
 * <p>The controller implements role-based access control using Spring Security
 * annotations, ensuring that only authorized users can perform specific operations.
 * Most administrative operations require 'ADMIN' role, while some read operations
 * are accessible to all authenticated users.</p>
 *
 * <p>All responses follow a standard format using {@link StandardResponseOutDTO}
 * to ensure consistency across the API. The controller also implements comprehensive
 * logging for monitoring and debugging purposes.</p>
 *
 * @author Course Service Team
 * @version 1.0
 * @see CourseService
 * @see StandardResponseOutDTO
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/service-api/course")
public class CourseController {

    /**
     * Service layer dependency for handling course-related business logic operations.
     *
     * <p>This service provides comprehensive methods for creating, reading, updating,
     * and deleting courses. It encapsulates all business logic related to course
     * management, including data validation, transformation, and persistence operations.</p>
     *
     * <p>The service is automatically injected by Spring's dependency injection
     * container and handles the interaction with the data access layer.</p>
     *
     * @see CourseService
     */
    @Autowired
    private CourseService courseService;

    /**
     * Creates a new course in the system.
     *
     * <p>This endpoint accepts course details through a data transfer object and
     * creates a new course entry in the system. The operation is restricted to
     * users with 'ADMIN' role for security purposes.</p>
     *
     * <p>The input data is automatically validated using Bean Validation annotations
     * before processing. Upon successful creation, the endpoint returns the created
     * course information along with a success message.</p>
     *
     * @param courseInDTO the data transfer object containing course details to be created.
     *                    Must not be null and should contain valid course information
     *                    including title, description, and other required fields
     * @return ResponseEntity containing the created course wrapped in a standard response format.
     * Returns HTTP 201 (CREATED) status with the course details on success
     * @throws SecurityException if the user lacks required permissions
     * @see CourseInDTO
     * @see CourseOutDTO
     * @see StandardResponseOutDTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> createCourse(@Valid @RequestBody final CourseInDTO courseInDTO) {
        log.info("Received request to create course: {}", courseInDTO.getTitle());
        final CourseOutDTO createdCourse = courseService.createCourse(courseInDTO);
        final StandardResponseOutDTO<CourseOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(
                createdCourse, "Course Created Successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(standardResponseOutDTO);
    }

    /**
     * Retrieves all available courses from the system.
     *
     * <p>This endpoint returns a comprehensive list of all courses stored in the system.
     * Access is restricted to users with 'ADMIN' role to protect sensitive course information.
     * The returned list includes basic course information suitable for administrative overview.</p>
     *
     * <p>If no courses exist in the system, an empty list is returned rather than an error,
     * following REST API best practices.</p>
     *
     * @return ResponseEntity containing a list of all courses wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with the complete course list
     * @throws SecurityException if the user lacks required permissions
     * @see CourseOutDTO
     * @see StandardResponseOutDTO
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseOutDTO>>> getAllCourses() {
        log.info("Received request to get all courses.");
        final List<CourseOutDTO> courses = courseService.getAllCourses();
        final StandardResponseOutDTO<List<CourseOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(
                courses, "Fetched Courses Successfully"
        );
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves a specific course by its unique identifier.
     *
     * <p>This endpoint provides detailed information about a single course identified
     * by its unique ID. Unlike the general course list endpoint, this provides
     * comprehensive course information including detailed descriptions, metadata,
     * and associated resources.</p>
     *
     * <p>This endpoint is accessible to all authenticated users, making it suitable
     * for displaying course details to potential students or enrolled users.</p>
     *
     * @param id the unique identifier of the course to retrieve. Must be a positive
     *           long value representing an existing course ID
     * @return ResponseEntity containing the course information wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with detailed course information
     * @throws IllegalArgumentException if the provided ID is null or invalid
     * @see CourseInfoOutDTO
     * @see StandardResponseOutDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<CourseInfoOutDTO>> getCourseById(@PathVariable final Long id) {
        log.info("Received request to get course by ID: {}", id);
        final CourseInfoOutDTO course = courseService.getCourseById(id);
        final StandardResponseOutDTO<CourseInfoOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(
                course, "Fetched Course Details"
        );
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Deletes a course from the system by its unique identifier.
     *
     * <p>This endpoint permanently removes a course from the system. The operation
     * is restricted to users with 'ADMIN' role due to its destructive nature.
     * Once deleted, the course and all associated data will be permanently removed.</p>
     *
     * <p>The operation includes cascade deletion of related entities and proper
     * cleanup of dependencies to maintain data integrity. A confirmation message
     * is returned upon successful deletion.</p>
     *
     * @param id the unique identifier of the course to delete. Must be a positive
     *           long value representing an existing course ID
     * @return ResponseEntity containing a confirmation message wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with deletion confirmation
     * @throws IllegalArgumentException if the provided ID is null or invalid
     * @throws SecurityException        if the user lacks required permissions
     * @see StandardResponseOutDTO
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteCourse(@PathVariable final Long id) {
        log.info("Received request to delete course with ID: {}", id);
        final String response = courseService.deleteCourse(id);
        final StandardResponseOutDTO<Void> standardResponseOutDTO = StandardResponseOutDTO.success(null, response);
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Updates an existing course with new information.
     *
     * <p>This endpoint allows modification of existing course details through a
     * partial update mechanism. Only the provided fields in the update DTO will
     * be modified, while other fields remain unchanged. The operation is restricted
     * to users with 'ADMIN' role.</p>
     *
     * <p>Input validation is performed on the update data to ensure data integrity.
     * The updated course information is returned upon successful modification.</p>
     *
     * @param id                the unique identifier of the course to update. Must be a positive
     *                          long value representing an existing course ID
     * @param updateCourseInDTO the data transfer object containing updated course information.
     *                          Contains only the fields that need to be modified
     * @return ResponseEntity containing the updated course wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with the updated course details
     * @throws IllegalArgumentException if the provided ID is null or invalid
     * @throws SecurityException        if the user lacks required permissions
     * @see UpdateCourseInDTO
     * @see CourseOutDTO
     * @see StandardResponseOutDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> updateCourse(
            @PathVariable final Long id, @Valid @RequestBody final UpdateCourseInDTO updateCourseInDTO
    ) {
        log.info("Received request to update course with ID: {}", id);
        final CourseOutDTO courseOutDTO = courseService.updateCourse(id, updateCourseInDTO);
        final StandardResponseOutDTO<CourseOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(
                courseOutDTO, "Course Updated Successfully"
        );
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Checks if a course exists in the system by its unique identifier.
     *
     * <p>This endpoint is specifically designed for inter-service communication
     * and validation purposes. It provides a lightweight way for other microservices
     * to verify course existence without retrieving full course details.</p>
     *
     * <p>The endpoint returns a simple boolean response, making it efficient for
     * batch validation scenarios or quick existence checks. It's accessible to
     * all authenticated services without role restrictions.</p>
     *
     * @param id the unique identifier of the course to check. Must be a positive
     *           long value representing a potential course ID
     * @return ResponseEntity with boolean value indicating whether the course exists.
     * Returns HTTP 200 (OK) status with true if course exists, false otherwise
     * @throws IllegalArgumentException if the provided ID is null or invalid
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkIfCourseExists(@PathVariable final Long id) {
        log.info("Fetching course with ID: {}", id);
        final boolean exists = courseService.courseExistsById(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * Retrieves the total count of courses in the system.
     *
     * <p>This endpoint provides statistical information about the total number
     * of courses available in the system. It's useful for dashboard displays,
     * pagination calculations, and system monitoring purposes.</p>
     *
     * <p>Access is restricted to users with 'ADMIN' role as this information
     * might be considered sensitive for business intelligence purposes.</p>
     *
     * @return ResponseEntity containing the total course count wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with the numerical count of courses
     * @throws SecurityException if the user lacks required permissions
     * @see StandardResponseOutDTO
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<Long>> getCourseCount() {
        log.info("Received request to get total course count.");
        final long count = courseService.countCourses();
        final StandardResponseOutDTO<Long> standardResponseOutDTO = StandardResponseOutDTO.success(
                count, "Fetched Course Count"
        );
        log.info("Total course count retrieved: {}", count);
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves a list of recently created courses with summary information.
     *
     * <p>This endpoint provides a curated list of the most recently created courses,
     * typically used for dashboard displays or administrative monitoring. The summary
     * format includes essential course information without overwhelming detail.</p>
     *
     * <p>The number of recent courses returned is determined by the service layer
     * configuration. Access is restricted to users with 'ADMIN' role.</p>
     *
     * @return ResponseEntity containing a list of recent course summaries wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with the list of recent course summaries
     * @throws SecurityException if the user lacks required permissions
     * @see CourseSummaryOutDTO
     * @see StandardResponseOutDTO
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseSummaryOutDTO>>> getRecentCourses() {
        final List<CourseSummaryOutDTO> recentCourses = courseService.getRecentCourseSummaries();
        final StandardResponseOutDTO<List<CourseSummaryOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(
                recentCourses, "Fetched Recent Courses"
        );
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves recent dashboard data including courses and bundles.
     *
     * <p>This endpoint provides comprehensive dashboard data that includes recent
     * courses and bundle information in a single response. It's optimized for
     * dashboard views that require multiple data types to minimize API calls.</p>
     *
     * <p>The endpoint aggregates data from multiple sources to provide a unified
     * view suitable for administrative dashboards. Access is restricted to users
     * with 'ADMIN' role.</p>
     *
     * @return ResponseEntity containing dashboard data with recent courses and bundles.
     * Returns HTTP 200 (OK) status with comprehensive dashboard information
     * @throws SecurityException if the user lacks required permissions
     * @see DashboardDataOutDTO
     */
    @GetMapping("/recent-course-and-bundle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDataOutDTO> getRecentDashboardData() {
        log.info("Request received to fetch recent dashboard data");
        final DashboardDataOutDTO dashboardData = courseService.getRecentDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * Retrieves only the name of a specific course by its unique identifier.
     *
     * <p>This endpoint provides a lightweight way to fetch just the course name
     * without retrieving complete course details. It's useful for dropdown lists,
     * breadcrumb navigation, or any scenario where only the course title is needed.</p>
     *
     * <p>This endpoint is accessible to all authenticated users, making it suitable
     * for various user interface components that need to display course names.</p>
     *
     * @param id the unique identifier of the course. Must be a positive long value
     *           representing an existing course ID
     * @return ResponseEntity containing the course name as a string.
     * Returns HTTP 200 (OK) status with the course name
     * @throws IllegalArgumentException if the provided ID is null or invalid
     */
    @GetMapping("/{id}/name")
    public ResponseEntity<String> getCourseNameById(@PathVariable("id") final Long id) {
        log.info("Received request to get course name.");
        final String courseName = courseService.getCourseNameById(id);
        log.info("Course name retrieved");
        return ResponseEntity.ok(courseName);
    }

    /**
     * Retrieves detailed information for all courses in the system.
     *
     * <p>This endpoint returns comprehensive information for all courses, including
     * detailed descriptions, metadata, and associated resources. Unlike the basic
     * course list endpoint, this provides complete course information suitable for
     * detailed administrative views.</p>
     *
     * <p>Due to the comprehensive nature of the data returned, access is restricted
     * to users with 'ADMIN' role. The response may be large for systems with many courses.</p>
     *
     * @return ResponseEntity containing a list of detailed course information wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with comprehensive course details
     * @throws SecurityException if the user lacks required permissions
     * @see CourseInfoOutDTO
     * @see StandardResponseOutDTO
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCoursesInfo() {
        final List<CourseInfoOutDTO> courseDTOS = courseService.getCoursesInfo();
        final StandardResponseOutDTO<List<CourseInfoOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(
                courseDTOS, "Fetched Course Information"
        );
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Filters a list of course IDs and returns only those that exist in the system.
     *
     * <p>This endpoint is designed for batch validation of course IDs. It accepts
     * a list of potential course IDs and returns only those that correspond to
     * existing courses in the system. This is particularly useful for data validation,
     * bulk operations, or inter-service communication.</p>
     *
     * <p>The endpoint is optimized for performance when dealing with large lists of IDs.
     * It's accessible to all authenticated services without role restrictions.</p>
     *
     * @param courseIds the list of course IDs to validate. Should contain potential
     *                  course identifiers that need existence verification
     * @return ResponseEntity containing a list of existing course IDs.
     * Returns HTTP 200 (OK) status with filtered list of valid IDs
     * @throws IllegalArgumentException if the courseIds list is null
     */
    @PostMapping("/existing-ids")
    public ResponseEntity<List<Long>> getExistingCourseIds(@RequestBody final List<Long> courseIds) {
        final List<Long> existingIds = courseService.findExistingIds(courseIds);
        return ResponseEntity.ok(existingIds);
    }

    /**
     * Retrieves course information for a specific list of course IDs.
     *
     * <p>This endpoint accepts a list of course IDs and returns detailed information
     * for all existing courses from that list. It combines the functionality of
     * ID validation and course information retrieval in a single operation, making
     * it efficient for batch operations.</p>
     *
     * <p>If some IDs in the provided list don't correspond to existing courses,
     * they are silently ignored, and only valid course information is returned.
     * The endpoint is accessible to all authenticated users.</p>
     *
     * @param courseIds the list of course IDs for which to retrieve information.
     *                  Should contain valid course identifiers
     * @return ResponseEntity containing course information for the specified IDs wrapped in a standard response format.
     * Returns HTTP 200 (OK) status with course details for valid IDs
     * @throws IllegalArgumentException if the courseIds list is null
     * @see CourseInfoOutDTO
     * @see StandardResponseOutDTO
     */
    @PostMapping("/courses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCoursesByIds(
            @RequestBody final List<Long> courseIds
    ) {
        final StandardResponseOutDTO<List<CourseInfoOutDTO>> response = courseService.getCoursesByIds(courseIds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
