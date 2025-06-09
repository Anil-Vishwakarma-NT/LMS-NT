package com.example.course_service_lms.controller;

import com.example.course_service_lms.inDTO.CourseInDTO;
import com.example.course_service_lms.outDTO.CourseOutDTO;
import com.example.course_service_lms.outDTO.CourseSummaryOutDTO;
import com.example.course_service_lms.dto.CourseInfoOutDTO;
import com.example.course_service_lms.dto.MessageOutDTO;
import com.example.course_service_lms.inDTO.UpdateCourseInDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.outDTO.StandardResponseOutDTO;
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
     * @param courseInDTO DTO containing course details to be created.
     * @return ResponseEntity containing the created Course entity.
     */
    @PostMapping
    public ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> createCourse(@Valid @RequestBody final CourseInDTO courseInDTO) {
        log.info("Received request to create course: {}", courseInDTO.getTitle());
        CourseOutDTO createdCourse = courseService.createCourse(courseInDTO);
        StandardResponseOutDTO<CourseOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(createdCourse, "Course Created Successfully");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves all available courses.
     *
     * @return ResponseEntity containing a list of all Course entities.
     */
    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<List<CourseOutDTO>>> getAllCourses() {
        log.info("Received request to get all courses.");
        List<CourseOutDTO> courses = courseService.getAllCourses();
        StandardResponseOutDTO<List<CourseOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(courses, "Fetched Courses Successfully");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves a specific course by its ID.
     *
     * @param id ID of the course to retrieve.
     * @return ResponseEntity containing the Course wrapped in Optional.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<Optional<CourseOutDTO>>> getCourseById(@PathVariable final Long id) {
        log.info("Received request to get course by ID: {}", id);
        Optional<CourseOutDTO> course = courseService.getCourseById(id);
        StandardResponseOutDTO<Optional<CourseOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(course,"Fetched Course Details");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id ID of the course to delete.
     * @return ResponseEntity containing a confirmation message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteCourse(@PathVariable final Long id) {
        log.info("Received request to delete course with ID: {}", id);
        String response = courseService.deleteCourse(id);
        StandardResponseOutDTO<Void> standardResponseOutDTO = StandardResponseOutDTO.success(null, response);
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Updates an existing course by its ID.
     *
     * @param id              ID of the course to update.
     * @param updateCourseInDTO DTO containing updated course information.
     * @return ResponseEntity containing a confirmation message.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> updateCourse(@PathVariable final Long id, @Valid @RequestBody final UpdateCourseInDTO updateCourseInDTO) {
        log.info("Received request to update course with ID: {}", id);
        CourseOutDTO courseOutDTO = courseService.updateCourse(id, updateCourseInDTO);
        StandardResponseOutDTO<CourseOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(courseOutDTO, "Course Updated Successfully");
        return ResponseEntity.ok(standardResponseOutDTO);
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
    public ResponseEntity<StandardResponseOutDTO<Long>> getCourseCount() {
        log.info("Received request to get total course count.");
        long count = courseService.countCourses();
        StandardResponseOutDTO<Long> standardResponseOutDTO = StandardResponseOutDTO.success(count, "Fetched Course Count");
        log.info("Total course count retrieved: {}", count);
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/recent")
    public ResponseEntity<StandardResponseOutDTO<List<CourseSummaryOutDTO>>> getRecentCourses() {
        List<CourseSummaryOutDTO> recentCourses = courseService.getRecentCourseSummaries();
        StandardResponseOutDTO<List<CourseSummaryOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(recentCourses, "Fetched Recent Courses");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getCourseNameById(@PathVariable("id") Long id) {
        log.info("Received request to get course name.");
        String courseName = courseService.getCourseNameById(id);
        log.info("Course name retrieved");
        return ResponseEntity.ok(courseName);
    }

    @GetMapping("/info")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCoursesInfo() {
        List<CourseInfoOutDTO> courseDTOS = courseService.getCoursesInfo();
        StandardResponseOutDTO<List<CourseInfoOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(courseDTOS, "Fetched Course Information");
        return ResponseEntity.ok(standardResponseOutDTO);
    }
}
