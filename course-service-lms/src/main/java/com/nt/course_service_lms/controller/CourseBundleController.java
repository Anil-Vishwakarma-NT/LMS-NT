package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.AddCourseToBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.*;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing the relationship between Courses and Bundles
 * in the Course Service of the LMS.
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
     * @param courseBundleInDTO DTO containing details for creating a CourseBundle.
     * @return ResponseEntity containing the created CourseBundlePostDTO.
     */
    @PostMapping
    public ResponseEntity<StandardResponseOutDTO<CourseBundle>> createCourseBundle(@RequestBody final CourseBundleInDTO courseBundleInDTO) {
        CourseBundle createdBundle = courseBundleService.createCourseBundle(courseBundleInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(StandardResponseOutDTO.success(createdBundle, "Course Bundle created successfully." ));
    }

    /**
     * Retrieves all existing CourseBundle associations.
     *
     * @return ResponseEntity containing a list of CourseBundleDTOs.
     */
    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> getAllCourseBundles() {
        List<CourseBundleOutDTO> courseBundles = courseBundleService.getAllCourseBundles();
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundles, "All course bundles retrieved successfully."));
    }

    /**
     * Retrieves a specific CourseBundle by its ID.
     *
     * @param courseBundleId ID of the CourseBundle to retrieve.
     * @return ResponseEntity containing the CourseBundleDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<CourseBundleOutDTO>> getCourseBundleById(@PathVariable("id") final Long courseBundleId) {
        CourseBundleOutDTO courseBundle = courseBundleService.getCourseBundleById(courseBundleId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundle, "Course Bundle with id:" + courseBundleId + " retrieved successfully."));
    }

    /**
     * Deletes a CourseBundle by its ID.
     *
     * @param courseBundleId ID of the CourseBundle to delete.
     * @return ResponseEntity containing a success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteCourseBundle(@PathVariable("id") final Long courseBundleId) {
        courseBundleService.deleteCourseBundle(courseBundleId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(null, "Course-bundle with ID " + courseBundleId + " deleted successfully."));
    }

    /**
     * Updates an existing CourseBundle with new data.
     *
     * @param courseBundleId ID of the CourseBundle to update.
     * @param updateCourseBundleInDTO DTO containing updated data for the CourseBundle.
     * @return ResponseEntity containing the updated CourseBundlePostDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<String>> updateCourseBundle(
            @PathVariable("id") final Long courseBundleId,
            @RequestBody final UpdateCourseBundleInDTO updateCourseBundleInDTO) {
        String response = courseBundleService.updateCourseBundle(courseBundleId, updateCourseBundleInDTO);
        return ResponseEntity.ok(StandardResponseOutDTO.success(response, "Course bundle with id" + courseBundleId + " updated successfully."));
    }

    /**
     * Retrieves all CourseBundle associations for a specific Bundle ID.
     *
     * @param bundleId ID of the bundle for which to fetch associated courses.
     * @return ResponseEntity containing a list of CourseBundle entities.
     */
    @GetMapping("/bundle/{id}")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getAllCoursesByBundleId(@PathVariable("id") final Long bundleId) {
        log.info("Received request to get all courses.");
        List<CourseInfoOutDTO> courseBundles = courseBundleService.getAllCoursesByBundle(bundleId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(courseBundles, "Course bundles with id retrieved successfully."));
    }

    @GetMapping("/bundle/courses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCoursesToAddInBundle(@RequestParam Long bundleId ){
     List<CourseInfoOutDTO> courses = courseBundleService.getCoursesToAdd(bundleId);
     return new ResponseEntity<>(StandardResponseOutDTO.success(courses , "Courses Retrieved") , HttpStatus.OK);
    }


    @PostMapping("/bundle/addCourse")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> addCourseToBundle(@RequestBody AddCourseToBundleInDTO addCourseToBundleInDTO){
           StandardResponseOutDTO<MessageOutDTO> response = courseBundleService.addCourseToBundle(addCourseToBundleInDTO);
           return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @GetMapping("/bundle/bundlecourses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getBundleCourses(@RequestParam Long bundleId) {

        StandardResponseOutDTO<List<CourseInfoOutDTO>> response = courseBundleService.getBundleCourses(bundleId);

        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @DeleteMapping("/bundle/removeCourse")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> removeCourseFromBundle(@RequestParam Long bundleId , @RequestParam Long courseId){
        StandardResponseOutDTO<MessageOutDTO> response = courseBundleService.removeCourse(bundleId ,courseId);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }
    @GetMapping("/info")
    public ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> getALlBundleInfo() {
        List<BundleInfoOutDTO> bundleInfoOutDTOS = courseBundleService.getBundlesInfo();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleInfoOutDTOS, "Bundles info retrieved successfully."));
    }

    @GetMapping("/recent")
    public ResponseEntity<StandardResponseOutDTO<List<BundleSummaryOutDTO>>> getRecentBundles() {
        List<BundleSummaryOutDTO> bundleSummaries = courseBundleService.getRecentBundleSummaries();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleSummaries, "Recent bundles retrieved successfully."));
    }

    @GetMapping("/bundle-id/{id}/course-ids")
    public ResponseEntity<List<Long>> findCourseIdsByBundleId(@PathVariable("id") Long bundleId) {
        return ResponseEntity.ok(courseBundleService.findCourseIdsByBundleId(bundleId));
    }


}
