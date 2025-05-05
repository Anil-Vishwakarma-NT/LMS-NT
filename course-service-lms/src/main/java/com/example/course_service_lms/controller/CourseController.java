package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.dto.CourseSummaryDTO;
import com.example.course_service_lms.dto.CourseInfoDTO;
import com.example.course_service_lms.dto.MessageOutDTO;
import com.example.course_service_lms.dto.UpdateCourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * REST Controller for managing courses in the LMS Course Service.
 */
@Slf4j
@RestController
@RequestMapping("/api/course")
@CrossOrigin("http://localhost:3000")
public class CourseController {

    /**
     * Service for handling course-related business logic.
     */
    @Autowired
    private CourseService courseService;

    /**
     * Creates a new course.
     *
     * @param courseDTO DTO containing course details to be created.
     * @return ResponseEntity containing the created Course entity.
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody final CourseDTO courseDTO) {
        log.info("Received request to create course: {}", courseDTO.getTitle());
        Course createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.ok(createdCourse);
    }

    /**
     * Retrieves all available courses.
     *
     * @return ResponseEntity containing a list of all Course entities.
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        log.info("Received request to get all courses.");
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Retrieves a specific course by its ID.
     *
     * @param id ID of the course to retrieve.
     * @return ResponseEntity containing the Course wrapped in Optional.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Course>> getCourseById(@PathVariable final Long id) {
        log.info("Received request to get course by ID: {}", id);
        Optional<Course> course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id ID of the course to delete.
     * @return ResponseEntity containing a confirmation message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable final Long id) {
        log.info("Received request to delete course with ID: {}", id);
        String response = courseService.deleteCourse(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing course by its ID.
     *
     * @param id              ID of the course to update.
     * @param updateCourseDTO DTO containing updated course information.
     * @return ResponseEntity containing a confirmation message.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable final Long id, @Valid @RequestBody final UpdateCourseDTO updateCourseDTO) {
        log.info("Received request to update course with ID: {}", id);
        String response = courseService.updateCourse(id, updateCourseDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if a course exists by its ID.
     * Used specifically by the user microservice for validation.
     *
     * @param id ID of the course to check.
     * @return ResponseEntity with true if course exists, false otherwise.
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkIfCourseExists(@PathVariable final Long id) {
        log.info("Fetching course with ID: {}", id);
        boolean exists = courseService.courseExistsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    public ResponseEntity<MessageOutDTO> getCourseCount() {
        log.info("Received request to get total course count.");
        long count = courseService.countCourses();
        log.info("Total course count retrieved: {}", count);
        return ResponseEntity.ok(new MessageOutDTO("" + count));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<CourseSummaryDTO>> getRecentCourses() {
        List<CourseSummaryDTO> recentCourses = courseService.getRecentCourseSummaries();
        return ResponseEntity.ok(recentCourses);
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getCourseNameById(@PathVariable("id") Long id) {
        log.info("Received request to get course name.");
        String courseName = courseService.getCourseNameById(id);
        log.info("Course name retrieved");
        return ResponseEntity.ok(courseName);
    }

    @GetMapping("/info")
    public ResponseEntity<List<CourseInfoDTO>> getCoursesInfo() {
        List<CourseInfoDTO> courseDTOS = courseService.getCoursesInfo();
        return ResponseEntity.ok(courseDTOS);
    }
}
