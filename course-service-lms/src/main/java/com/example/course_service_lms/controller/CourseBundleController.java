package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseBundlePostDTO;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

/**
 * REST Controller for managing the relationship between Courses and Bundles
 * in the Course Service of the LMS.
 */
@RestController
@RequestMapping("/api/bundles/course-bundles")
@RequiredArgsConstructor
@Slf4j
public class CourseBundleController {

    /**
     * Service for handling course-bundle related business logic.
     */
    private final CourseBundleService courseBundleService;

    /**
     * Creates a new CourseBundle (i.e., associates a Course with a Bundle).
     *
     * @param courseBundlePostDTO DTO containing details for creating a CourseBundle.
     * @return ResponseEntity containing the created CourseBundlePostDTO.
     */
    @PostMapping
    public ResponseEntity<CourseBundlePostDTO> createCourseBundle(@RequestBody final CourseBundlePostDTO courseBundlePostDTO) {
        CourseBundlePostDTO createdBundle = courseBundleService.createCourseBundle(courseBundlePostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBundle);
    }

    /**
     * Retrieves all existing CourseBundle associations.
     *
     * @return ResponseEntity containing a list of CourseBundleDTOs.
     */
    @GetMapping
    public ResponseEntity<List<CourseBundleDTO>> getAllCourseBundles() {
        List<CourseBundleDTO> courseBundles = courseBundleService.getAllCourseBundles();
        return ResponseEntity.ok(courseBundles);
    }

    /**
     * Retrieves a specific CourseBundle by its ID.
     *
     * @param courseBundleId ID of the CourseBundle to retrieve.
     * @return ResponseEntity containing the CourseBundleDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseBundleDTO> getCourseBundleById(@PathVariable("id") final Long courseBundleId) {
        CourseBundleDTO courseBundle = courseBundleService.getCourseBundleById(courseBundleId);
        return ResponseEntity.ok(courseBundle);
    }

    /**
     * Deletes a CourseBundle by its ID.
     *
     * @param courseBundleId ID of the CourseBundle to delete.
     * @return ResponseEntity containing a success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourseBundle(@PathVariable("id") final Long courseBundleId) {
        courseBundleService.deleteCourseBundle(courseBundleId);
        return ResponseEntity.ok("Course-bundle with ID " + courseBundleId + " deleted successfully.");
    }

    /**
     * Updates an existing CourseBundle with new data.
     *
     * @param courseBundleId ID of the CourseBundle to update.
     * @param courseBundlePostDTO DTO containing updated data for the CourseBundle.
     * @return ResponseEntity containing the updated CourseBundlePostDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseBundlePostDTO> updateCourseBundle(
            @PathVariable("id") final Long courseBundleId,
            @RequestBody final CourseBundlePostDTO courseBundlePostDTO) {
        CourseBundlePostDTO updatedCourseBundle = courseBundleService.updateCourseBundle(courseBundleId, courseBundlePostDTO);
        return ResponseEntity.ok(updatedCourseBundle);
    }

    /**
     * Retrieves all CourseBundle associations for a specific Bundle ID.
     *
     * @param bundleId ID of the bundle for which to fetch associated courses.
     * @return ResponseEntity containing a list of CourseBundle entities.
     */
    @GetMapping("/bundle/{id}")
    public ResponseEntity<List<CourseBundle>> getAllCoursesByBundleId(@PathVariable("id") final Long bundleId) {
        log.info("Received request to get all courses.");
        List<CourseBundle> courseBundles = courseBundleService.getAllCoursesByBundle(bundleId);
        return ResponseEntity.ok(courseBundles);
    }

}
