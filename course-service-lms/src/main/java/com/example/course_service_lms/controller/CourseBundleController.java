package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CreateCourseBundleDTO;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bundles/course-bundles")
@RequiredArgsConstructor
public class CourseBundleController {

    private final CourseBundleService courseBundleService;

    @PostMapping
    public ResponseEntity<CourseBundle> createCourseBundle(@RequestBody CreateCourseBundleDTO createCourseBundleDTO) {
        CourseBundle createdBundle = courseBundleService.createCourseBundle(createCourseBundleDTO);
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
    public ResponseEntity<String> updateCourseBundle(
            @PathVariable("id") Long courseBundleId,
            @RequestBody CreateCourseBundleDTO createCourseBundleDTO) {
        String updatedCourseBundle = courseBundleService.updateCourseBundle(courseBundleId, createCourseBundleDTO);
        return ResponseEntity.ok(updatedCourseBundle);
    }

}