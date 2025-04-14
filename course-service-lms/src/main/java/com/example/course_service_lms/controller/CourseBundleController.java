package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseBundlePostDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bundles/course-bundles")
@RequiredArgsConstructor
@Slf4j
public class CourseBundleController {

    private final CourseBundleService courseBundleService;

    @PostMapping
    public ResponseEntity<CourseBundlePostDTO> createCourseBundle(@RequestBody CourseBundlePostDTO courseBundlePostDTO) {
        CourseBundlePostDTO createdBundle = courseBundleService.createCourseBundle(courseBundlePostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBundle);
    }

    // GET All CourseBundles
    @GetMapping
    public ResponseEntity<List<CourseBundleDTO>> getAllCourseBundles() {
        List<CourseBundleDTO> courseBundles = courseBundleService.getAllCourseBundles();
        return ResponseEntity.ok(courseBundles);
    }

    // GET CourseBundle by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseBundleDTO> getCourseBundleById(@PathVariable("id") Long courseBundleId) {
        CourseBundleDTO courseBundle = courseBundleService.getCourseBundleById(courseBundleId);
        return ResponseEntity.ok(courseBundle);
    }

    // DELETE CourseBundle by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourseBundle(@PathVariable("id") Long courseBundleId) {
        courseBundleService.deleteCourseBundle(courseBundleId);
        return ResponseEntity.ok("Course-bundle with ID " + courseBundleId + " deleted successfully.");
    }

    // UPDATE CourseBundle by ID
    @PutMapping("/{id}")
    public ResponseEntity<CourseBundlePostDTO> updateCourseBundle(
            @PathVariable("id") Long courseBundleId,
            @RequestBody CourseBundlePostDTO courseBundlePostDTO) {
        CourseBundlePostDTO updatedCourseBundle = courseBundleService.updateCourseBundle(courseBundleId, courseBundlePostDTO);
        return ResponseEntity.ok(updatedCourseBundle);
    }

    @GetMapping("/bundle/{id}")
    public ResponseEntity<List<CourseBundle>> getAllCoursesByBundleId(@PathVariable("id") Long bundleId) {
        log.info("Received request to get all courses.");
        List<CourseBundle> courseBundles = courseBundleService.getAllCoursesByBundle(bundleId);
        return ResponseEntity.ok(courseBundles);
    }

}