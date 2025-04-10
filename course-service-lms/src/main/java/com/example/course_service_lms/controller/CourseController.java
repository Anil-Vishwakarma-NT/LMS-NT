package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        log.info("Received request to create course: {}", courseDTO.getTitle());
        Course createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.ok(createdCourse);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        log.info("Received request to get all courses.");
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Course>> getCourseById(@PathVariable Long id) {
        log.info("Received request to get course by ID: {}", id);
        Optional<Course> course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        log.info("Received request to delete course with ID: {}", id);
        String response = courseService.deleteCourse(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable Long id,@Valid @RequestBody CourseDTO courseDTO) {
        log.info("Received request to update course with ID: {}", id);
        String response = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(response);
    }
}
