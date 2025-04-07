package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bundles/course_bundles")
@RequiredArgsConstructor
public class CourseBundleController {

    private final CourseBundleService courseBundleService;

    @PostMapping
    public ResponseEntity<CourseBundleDTO> createCourseBundle(@RequestBody CourseBundleDTO courseBundleDTO) {
        CourseBundleDTO createdBundle = courseBundleService.createCourseBundle(courseBundleDTO);
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
    public ResponseEntity<CourseBundleDTO> updateCourseBundle(
            @PathVariable("id") Long courseBundleId,
            @RequestBody CourseBundleDTO courseBundleDTO) {
        CourseBundleDTO updatedCourseBundle = courseBundleService.updateCourseBundle(courseBundleId, courseBundleDTO);
        return ResponseEntity.ok(updatedCourseBundle);
    }

}