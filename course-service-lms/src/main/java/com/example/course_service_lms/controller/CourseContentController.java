package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.service.CourseContentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

/**
 * REST Controller for managing Course Content in the Course Service of the LMS.
 */
@Slf4j
@RestController
@RequestMapping("/api/course-content")
public class CourseContentController {

    /**
     * Service for handling course-content related business logic.
     */
    @Autowired
    private CourseContentService courseContentService;

    /**
     * Creates a new CourseContent for a given course.
     *
     * @param courseContentInDTO DTO containing course content details.
     * @return ResponseEntity containing the created CourseContent entity.
     */
    @PostMapping
    public ResponseEntity<CourseContent> createCourseContent(@Valid @RequestBody final CourseContentInDTO courseContentInDTO) {
        log.info("Received request to create course content: {}", courseContentInDTO.getTitle());
        CourseContent courseContent = courseContentService.createCourseContent(courseContentInDTO);
        log.info("Course content created with ID: {}", courseContent.getCourseContentId());
        return ResponseEntity.ok(courseContent);
    }

    /**
     * Retrieves all course content entries.
     *
     * @return ResponseEntity containing a list of CourseContent entities.
     */
    @GetMapping
    public ResponseEntity<List<CourseContent>> getAllCourseContents() {
        log.info("Received request to fetch all course contents.");
        List<CourseContent> courseContents = courseContentService.getAllCourseContents();
        log.info("Total course contents fetched: {}", courseContents.size());
        return ResponseEntity.ok(courseContents);
    }

    /**
     * Retrieves course content by its unique ID.
     *
     * @param id ID of the course content to retrieve.
     * @return ResponseEntity containing the CourseContent wrapped in Optional.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<CourseContent>> getCourseContentById(@PathVariable final Long id) {
        log.info("Received request to get course content by ID: {}", id);
        Optional<CourseContent> courseContent = courseContentService.getCourseContentById(id);
        if (courseContent.isPresent()) {
            log.info("Course content found for ID: {}", id);
        } else {
            log.warn("No course content found for ID: {}", id);
        }
        return ResponseEntity.ok(courseContent);
    }

    /**
     * Retrieves all course content entries associated with a specific course.
     *
     * @param id ID of the course whose contents are to be fetched.
     * @return ResponseEntity containing a list of CourseContent entities.
     */
    @GetMapping("/courseid/{id}")
    public ResponseEntity<List<CourseContent>> getCourseContentByCourseId(@PathVariable final Long id) {
        log.info("Received request to fetch all course contents of course.", id);
        List<CourseContent> courseContents = courseContentService.getAllCourseContentByCourseId(id);
        log.info("Total course contents fetched: {}", courseContents.size());
        return ResponseEntity.ok(courseContents);
    }

    /**
     * Deletes a course content entry by its ID.
     *
     * @param id ID of the course content to delete.
     * @return ResponseEntity containing a confirmation message.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCourseContent(@PathVariable final Long id) {
        log.info("Received request to delete course content with ID: {}", id);
        String message = courseContentService.deleteCourseContent(id);
        log.info("Delete response: {}", message);
        return ResponseEntity.ok(message);
    }

    /**
     * Updates a course content entry.
     *
     * @param id ID of the course content to update.
     * @param updateCourseContentInDTO DTO containing updated course content information.
     * @return ResponseEntity containing a confirmation message.
     */
    @PutMapping("{id}")
    public ResponseEntity<String> updateCourseContent(@PathVariable final Long id,
                                                      @Valid @RequestBody final UpdateCourseContentInDTO updateCourseContentInDTO) {
        log.info("Received request to update course content with ID: {}", id);
        String message = courseContentService.updateCourseContent(id, updateCourseContentInDTO);
        log.info("Update response: {}", message);
        return ResponseEntity.ok(message);
    }
}
