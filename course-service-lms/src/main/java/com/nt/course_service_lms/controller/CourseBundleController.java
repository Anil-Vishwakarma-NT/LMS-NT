package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.service.CourseBundleService;
import jakarta.validation.Valid;
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
 * <p>
 * This controller provides endpoints for creating, retrieving, updating, and deleting
 * course-bundle associations, as well as querying bundle information and recent bundles.
 */
@RestController
@RequestMapping("/api/service-api/course-bundles")
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
     * @param courseBundleInDTO DTO containing details for creating a CourseBundle
     * @return ResponseEntity containing the created CourseBundle wrapped in StandardResponseOutDTO
     */
    @PostMapping
    public ResponseEntity<StandardResponseOutDTO<CourseBundle>> createCourseBundle(
            @Valid @RequestBody final CourseBundleInDTO courseBundleInDTO
    ) {
        final CourseBundle createdBundle = courseBundleService.createCourseBundle(courseBundleInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseOutDTO.success(
                        createdBundle, "Course Bundle created successfully."
                )
        );
    }

    /**
     * Retrieves all existing CourseBundle associations.
     *
     * @return ResponseEntity containing a list of CourseBundleOutDTO wrapped in StandardResponseOutDTO
     */
    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> getAllCourseBundles() {
        final List<CourseBundleOutDTO> courseBundles = courseBundleService.getAllCourseBundles();
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundles, "All course bundles retrieved successfully."));
    }

    /**
     * Retrieves a specific CourseBundle by its ID.
     *
     * @param courseBundleId ID of the CourseBundle to retrieve
     * @return ResponseEntity containing the CourseBundleOutDTO wrapped in StandardResponseOutDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<CourseBundleOutDTO>> getCourseBundleById(
            @PathVariable("id") final Long courseBundleId
    ) {
        final CourseBundleOutDTO courseBundle = courseBundleService.getCourseBundleById(courseBundleId);
        return ResponseEntity.ok(
                StandardResponseOutDTO.success(
                        courseBundle, "Course Bundle with id:" + courseBundleId + " retrieved successfully."
                ));
    }

    /**
     * Deletes a CourseBundle by its ID.
     *
     * @param courseBundleId ID of the CourseBundle to delete
     * @return ResponseEntity containing a success message wrapped in StandardResponseOutDTO
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteCourseBundle(@PathVariable("id") final Long courseBundleId) {
        courseBundleService.deleteCourseBundle(courseBundleId);
        return ResponseEntity.ok(
                StandardResponseOutDTO.success(
                        null, "Course-bundle with ID " + courseBundleId + " deleted successfully."
                )
        );
    }

    /**
     * Updates an existing CourseBundle with new data.
     *
     * @param courseBundleId          ID of the CourseBundle to update
     * @param updateCourseBundleInDTO DTO containing updated data for the CourseBundle
     * @return ResponseEntity containing the update response string wrapped in StandardResponseOutDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<String>> updateCourseBundle(
            @PathVariable("id") final Long courseBundleId,
            @Valid @RequestBody final UpdateCourseBundleInDTO updateCourseBundleInDTO) {
        final String response = courseBundleService.updateCourseBundle(courseBundleId, updateCourseBundleInDTO);
        return ResponseEntity.ok(StandardResponseOutDTO.success(
                        response, "Course bundle with id" + courseBundleId + " updated successfully."
                )
        );
    }

    /**
     * Retrieves all CourseBundle associations for a specific Bundle ID.
     *
     * @param bundleId ID of the bundle for which to fetch associated courses
     * @return ResponseEntity containing a list of CourseBundle entities wrapped in StandardResponseOutDTO
     */
    @GetMapping("/bundle/{id}")
    public ResponseEntity<StandardResponseOutDTO<List<CourseBundle>>> getAllCoursesByBundleId(
            @PathVariable("id") final Long bundleId) {
        log.info("Received request to get all courses.");
        final List<CourseBundle> courseBundles = courseBundleService.getAllCoursesByBundle(bundleId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundles, "Course bundles with id retrieved successfully."));
    }

    /**
     * Retrieves information about all bundles.
     *
     * @return ResponseEntity containing a list of BundleInfoOutDTO wrapped in StandardResponseOutDTO
     */
    @GetMapping("/info")
    public ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> getALlBundleInfo() {
        final List<BundleInfoOutDTO> bundleInfoOutDTOS = courseBundleService.getBundlesInfo();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleInfoOutDTOS, "Bundles info retrieved successfully."));
    }

    /**
     * Retrieves recent bundle summaries.
     *
     * @return ResponseEntity containing a list of BundleSummaryOutDTO wrapped in StandardResponseOutDTO
     */
    @GetMapping("/recent")
    public ResponseEntity<StandardResponseOutDTO<List<BundleSummaryOutDTO>>> getRecentBundles() {
        final List<BundleSummaryOutDTO> bundleSummaries = courseBundleService.getRecentBundleSummaries();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleSummaries, "Recent bundles retrieved successfully."));
    }

    /**
     * Finds course IDs associated with a specific bundle ID.
     *
     * @param bundleId ID of the bundle for which to find course IDs
     * @return ResponseEntity containing a list of course IDs
     */
    @GetMapping("/bundle-id/{id}/course-ids")
    public ResponseEntity<List<Long>> findCourseIdsByBundleId(@PathVariable("id") final Long bundleId) {
        return ResponseEntity.ok(courseBundleService.findCourseIdsByBundleId(bundleId));
    }
}
