package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.AddCourseToBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.service.CourseBundleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing the relationship between Courses and Bundles
 * in the Course Service of the Learning Management System (LMS).
 * <p>
 * This controller provides endpoints for creating, retrieving, updating, and deleting
 * course-bundle associations, as well as querying bundle information and recent bundles.
 * It handles all HTTP requests related to course bundle operations and ensures proper
 * authorization through role-based access control.
 * </p>
 * <p>
 * The controller uses standardized response formats through {@link StandardResponseOutDTO}
 * and implements security through Spring Security's {@code @PreAuthorize} annotations.
 * All endpoints return appropriate HTTP status codes and meaningful response messages.
 * </p>
 *
 * @see CourseBundleService
 * @see CourseBundle
 * @see StandardResponseOutDTO
 */
@RestController
@RequestMapping("/api/service-api/course-bundles")
@RequiredArgsConstructor
@Slf4j
public class CourseBundleController {

    /**
     * Service component for handling course-bundle related business logic and operations.
     * <p>
     * This service is injected through constructor injection (facilitated by {@code @RequiredArgsConstructor})
     * and provides all the business logic for managing course-bundle relationships, including
     * creation, retrieval, updates, deletions, and complex queries.
     * </p>
     *
     * @see CourseBundleService
     */
    private final CourseBundleService courseBundleService;

    /**
     * Creates a new CourseBundle association between a course and a bundle.
     * <p>
     * This endpoint allows administrators to establish a relationship between an existing
     * course and an existing bundle. The operation requires ADMIN role privileges.
     * </p>
     *
     * @param courseBundleInDTO DTO containing the necessary information for creating a CourseBundle,
     *                          including course ID and bundle ID
     * @return ResponseEntity containing the created {@link CourseBundle} entity wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 201 (CREATED)
     * @see CourseBundleInDTO
     * @see CourseBundle
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
     * Retrieves all existing CourseBundle associations in the system.
     * <p>
     * This endpoint returns a comprehensive list of all course-bundle relationships
     * currently established in the system. Access is restricted to ADMIN and EMPLOYEE roles.
     * </p>
     *
     * @return ResponseEntity containing a list of {@link CourseBundleOutDTO} wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see CourseBundleOutDTO
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> getAllCourseBundles() {
        final List<CourseBundleOutDTO> courseBundles = courseBundleService.getAllCourseBundles();
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundles, "All course bundles retrieved successfully."));
    }

    /**
     * Retrieves a specific CourseBundle by its unique identifier.
     * <p>
     * This endpoint fetches detailed information about a single course-bundle relationship
     * based on the provided ID. Access is restricted to ADMIN and EMPLOYEE roles.
     * </p>
     *
     * @param courseBundleId the unique identifier of the CourseBundle to retrieve
     * @return ResponseEntity containing the {@link CourseBundleOutDTO} wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see CourseBundleOutDTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
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
     * Deletes a CourseBundle association by its unique identifier.
     * <p>
     * This endpoint permanently removes the relationship between a course and a bundle.
     * Only administrators are authorized to perform this operation as it affects
     * the structural integrity of the learning paths.
     * </p>
     *
     * @param courseBundleId the unique identifier of the CourseBundle to delete
     * @return ResponseEntity containing a success message wrapped in {@link StandardResponseOutDTO}
     * with HTTP status 200 (OK)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteCourseBundle(@PathVariable("id") final Long courseBundleId) {
        courseBundleService.deleteCourseBundle(courseBundleId);
        return ResponseEntity.ok(
                StandardResponseOutDTO.success(
                        null, "Course-bundle with ID " + courseBundleId + " deleted successfully."
                )
        );
    }

    /**
     * Updates an existing CourseBundle with new information.
     * <p>
     * This endpoint allows administrators to modify the properties of an existing
     * course-bundle relationship. The operation maintains data integrity while
     * allowing for necessary adjustments to the association.
     * </p>
     *
     * @param courseBundleId          the unique identifier of the CourseBundle to update
     * @param updateCourseBundleInDTO DTO containing the updated information for the CourseBundle
     * @return ResponseEntity containing the update response message wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see UpdateCourseBundleInDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
     * Retrieves all courses associated with a specific bundle.
     * <p>
     * This endpoint returns detailed information about all courses that are part of
     * a specified bundle, providing a comprehensive view of the bundle's contents.
     * Access is restricted to ADMIN and EMPLOYEE roles.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle for which to fetch associated courses
     * @return ResponseEntity containing a list of {@link CourseInfoOutDTO} wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see CourseInfoOutDTO
     */
    @GetMapping("/bundle/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getAllCoursesByBundleId(
            @PathVariable("id") final Long bundleId
    ) {
        log.info("Received request to get all courses for bundle ID: {}", bundleId);
        List<CourseInfoOutDTO> courseBundles = courseBundleService.getAllCoursesByBundle(bundleId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundles, "Course bundles with id retrieved successfully."));
    }

    /**
     * Retrieves all courses that can be added to a specific bundle.
     * <p>
     * This endpoint returns a list of courses that are not currently associated
     * with the specified bundle, making them available for addition to the bundle.
     * This is useful for bundle management interfaces.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle for which to find available courses
     * @return ResponseEntity containing a list of {@link CourseInfoOutDTO} representing
     * courses available for addition, wrapped in {@link StandardResponseOutDTO}
     * with HTTP status 200 (OK)
     * @see CourseInfoOutDTO
     */
    @GetMapping("/bundle/courses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCoursesToAddInBundle(
            @RequestParam final Long bundleId
    ) {
        List<CourseInfoOutDTO> courses = courseBundleService.getCoursesToAdd(bundleId);
        return new ResponseEntity<>(StandardResponseOutDTO.success(courses, "Courses Retrieved"), HttpStatus.OK);
    }

    /**
     * Adds a course to an existing bundle.
     * <p>
     * This endpoint creates a new association between a course and a bundle,
     * effectively adding the course to the bundle's curriculum. The operation
     * ensures no duplicate associations are created.
     * </p>
     *
     * @param addCourseToBundleInDTO DTO containing the course ID and bundle ID for the new association
     * @return ResponseEntity containing a {@link MessageOutDTO} with operation result
     * wrapped in {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see AddCourseToBundleInDTO
     * @see MessageOutDTO
     */
    @PostMapping("/bundle/addCourse")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> addCourseToBundle(
            @RequestBody final AddCourseToBundleInDTO addCourseToBundleInDTO
    ) {
        StandardResponseOutDTO<MessageOutDTO> response = courseBundleService.addCourseToBundle(addCourseToBundleInDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all courses currently in a specific bundle.
     * <p>
     * This endpoint provides detailed information about all courses that are
     * currently associated with the specified bundle, offering a complete
     * view of the bundle's course composition.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle for which to retrieve courses
     * @return ResponseEntity containing a list of {@link CourseInfoOutDTO} wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see CourseInfoOutDTO
     */
    @GetMapping("/bundle/bundlecourses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getBundleCourses(@RequestParam final Long bundleId) {
        StandardResponseOutDTO<List<CourseInfoOutDTO>> response = courseBundleService.getBundleCourses(bundleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Removes a course from a bundle.
     * <p>
     * This endpoint breaks the association between a specific course and bundle,
     * effectively removing the course from the bundle's curriculum. The operation
     * maintains referential integrity while allowing for flexible bundle management.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle from which to remove the course
     * @param courseId the unique identifier of the course to be removed from the bundle
     * @return ResponseEntity containing a {@link MessageOutDTO} with operation result
     * wrapped in {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see MessageOutDTO
     */
    @DeleteMapping("/bundle/removeCourse")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> removeCourseFromBundle(
            @RequestParam final Long bundleId,
            @RequestParam final Long courseId
    ) {
        StandardResponseOutDTO<MessageOutDTO> response = courseBundleService.removeCourse(bundleId, courseId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves comprehensive information about all bundles in the system.
     * <p>
     * This endpoint provides detailed information about all available bundles,
     * including metadata and summary statistics. Access is restricted to
     * ADMIN and EMPLOYEE roles for administrative and reporting purposes.
     * </p>
     *
     * @return ResponseEntity containing a list of {@link BundleInfoOutDTO} wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see BundleInfoOutDTO
     */
    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> getALlBundleInfo() {
        final List<BundleInfoOutDTO> bundleInfoOutDTOS = courseBundleService.getBundlesInfo();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleInfoOutDTOS, "Bundles info retrieved successfully."));
    }

    /**
     * Retrieves summary information for recently created or updated bundles.
     * <p>
     * This endpoint provides administrators with a quick overview of recent
     * bundle activity, including newly created bundles and recently modified ones.
     * This is useful for monitoring system activity and administrative oversight.
     * </p>
     *
     * @return ResponseEntity containing a list of {@link BundleSummaryOutDTO} wrapped in
     * {@link StandardResponseOutDTO} with HTTP status 200 (OK)
     * @see BundleSummaryOutDTO
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<List<BundleSummaryOutDTO>>> getRecentBundles() {
        final List<BundleSummaryOutDTO> bundleSummaries = courseBundleService.getRecentBundleSummaries();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleSummaries, "Recent bundles retrieved successfully."));
    }

    /**
     * Finds and returns all course IDs associated with a specific bundle.
     * <p>
     * This endpoint provides a lightweight way to retrieve only the identifiers
     * of courses associated with a bundle, useful for operations that only need
     * the IDs rather than full course information. Access is restricted to
     * ADMIN and EMPLOYEE roles.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle for which to find course IDs
     * @return ResponseEntity containing a list of course IDs (Long) with HTTP status 200 (OK)
     */
    @GetMapping("/bundle-id/{id}/course-ids")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<Long>> findCourseIdsByBundleId(@PathVariable("id") final Long bundleId) {
        return ResponseEntity.ok(courseBundleService.findCourseIdsByBundleId(bundleId));
    }
}
