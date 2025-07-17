package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * This controller provides endpoints for CRUD operations on courses, course validation,
 * and course information retrieval for the LMS Course Service.
 */
@Slf4j
@RestController
@RequestMapping("/api/service-api/course")
public class CourseController {

    /**
     * Service layer dependency for handling course-related business logic operations.
     * Provides methods for creating, reading, updating, and deleting courses.
     */
    @Autowired
    private CourseService courseService;

    /**
     * Creates a new course in the system.
     *
     * @param courseInDTO the data transfer object containing course details to be created
     * @return ResponseEntity containing the created course wrapped in a standard response format
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> createCourse(@Valid @RequestBody final CourseInDTO courseInDTO) {
        log.info("Received request to create course: {}", courseInDTO.getTitle());
        final CourseOutDTO createdCourse = courseService.createCourse(courseInDTO);
        final StandardResponseOutDTO<CourseOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(
                createdCourse, "Course Created Successfully"
        );
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves all available courses from the system.
     *
     * @return ResponseEntity containing a list of all courses wrapped in a standard response format
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
     * @param id the unique identifier of the course to retrieve
     * @return ResponseEntity containing the course information wrapped in a standard response format
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
     * @param id the unique identifier of the course to delete
     * @return ResponseEntity containing a confirmation message wrapped in a standard response format
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
     * @param id                the unique identifier of the course to update
     * @param updateCourseInDTO the data transfer object containing updated course information
     * @return ResponseEntity containing the updated course wrapped in a standard response format
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
     * This endpoint is specifically used by other microservices for validation purposes.
     *
     * @param id the unique identifier of the course to check
     * @return ResponseEntity with boolean value indicating whether the course exists
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
     * @return ResponseEntity containing the total course count wrapped in a standard response format
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
     * @return ResponseEntity containing a list of recent course summaries wrapped in a standard response format
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
     * Retrieves only the name of a specific course by its unique identifier.
     *
     * @param id the unique identifier of the course
     * @return ResponseEntity containing the course name as a string
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
     * @return ResponseEntity containing a list of detailed course information wrapped in a standard response format
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
     * This endpoint is used for batch validation of course IDs.
     *
     * @param courseIds the list of course IDs to validate
     * @return ResponseEntity containing a list of existing course IDs
     */
    @PostMapping("/existing-ids")
    public ResponseEntity<List<Long>> getExistingCourseIds(@RequestBody final List<Long> courseIds) {
        final List<Long> existingIds = courseService.findExistingIds(courseIds);
        return ResponseEntity.ok(existingIds);
    }
}
