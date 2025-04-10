package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.service.CourseContentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/course-content")
public class CourseContentController {

    @Autowired
    private CourseContentService courseContentService;

    @PostMapping
    public ResponseEntity<CourseContent> createCourseContent(@Valid @RequestBody CourseContentDTO courseContentDTO) {
        log.info("Received request to create course content: {}", courseContentDTO.getTitle());
        CourseContent courseContent = courseContentService.createCourseContent(courseContentDTO);
        log.info("Course content created with ID: {}", courseContent.getCourseContentId());
        return ResponseEntity.ok(courseContent);
    }

    @GetMapping
    public ResponseEntity<List<CourseContent>> getAllCourseContents() {
        log.info("Received request to fetch all course contents.");
        List<CourseContent> courseContents = courseContentService.getAllCourseContents();
        log.info("Total course contents fetched: {}", courseContents.size());
        return ResponseEntity.ok(courseContents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<CourseContent>> getCourseContentById(@PathVariable Long id) {
        log.info("Received request to get course content by ID: {}", id);
        Optional<CourseContent> courseContent = courseContentService.getCourseContentById(id);
        if (courseContent.isPresent()) {
            log.info("Course content found for ID: {}", id);
        } else {
            log.warn("No course content found for ID: {}", id);
        }
        return ResponseEntity.ok(courseContent);
    }

    @GetMapping("/courseid/{id}")
    public ResponseEntity<List<CourseContent>> getCourseContentByCourseId(@PathVariable Long id){
        log.info("Received request to fetch all course contents of course.", id);
        List<CourseContent> courseContents = courseContentService.getAllCourseContentByCourseId(id);
        log.info("Total course contents fetched: {}", courseContents.size());
        return ResponseEntity.ok(courseContents);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCourseContent(@PathVariable Long id) {
        log.info("Received request to delete course content with ID: {}", id);
        String message = courseContentService.deleteCourseContent(id);
        log.info("Delete response: {}", message);
        return ResponseEntity.ok(message);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateCourseContent(@PathVariable Long id, @Valid @RequestBody CourseContentDTO courseContentDTO) {
        log.info("Received request to update course content with ID: {}", id);
        String message = courseContentService.updateCourseContent(id, courseContentDTO);
        log.info("Update response: {}", message);
        return ResponseEntity.ok(message);
    }
}
