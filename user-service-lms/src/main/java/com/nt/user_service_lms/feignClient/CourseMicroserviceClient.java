package com.nt.user_service_lms.feignClient;


import com.nt.user_service_lms.config.FeignTokenInterceptor;
import com.nt.user_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.user_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for communicating with the Course Microservice.
 */
@FeignClient(name = "course-service", url = "http://localhost:8080/api/service-api", configuration = FeignTokenInterceptor.class)
public interface CourseMicroserviceClient {

    /**
     * Checks if a course exists by its ID.
     *
     * @param courseId the course ID
     * @return true if the course exists, false otherwise
     */
    @GetMapping("/course/{id}/exists")
    boolean courseExistsById(@PathVariable("id") Long courseId);

    /**
     * Checks if a bundle exists by its ID.
     *
     * @param bundleId the bundle ID
     * @return true if the bundle exists, false otherwise
     */
    @GetMapping("/bundles/{id}/exists")
    boolean bundleExistsById(@PathVariable("id") Long bundleId);

    /**
     * Retrieves all courses by bundle ID.
     *
     * @param bundleId the bundle ID
     * @return a response entity containing a list of course bundles
     */
    @GetMapping("/bundles/course-bundles/bundle/{id}")
    ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> getAllCoursesByBundleId(@PathVariable("id") Long bundleId);

    /**
     * Retrieves the name of a course by its ID.
     *
     * @param id the course ID
     * @return a response entity containing the course name
     */
    @GetMapping("/course/{id}/name")
    ResponseEntity<String> getCourseNameById(@PathVariable("id") Long id);

    /**
     * Retrieves the name of a bundle by its ID.
     *
     * @param id the bundle ID
     * @return a response entity containing the bundle name
     */
    @GetMapping("/bundles/{id}/name")
    public ResponseEntity<StandardResponseOutDTO<String>> getBundleNameById(@PathVariable("id") Long id);

    /**
     * Retrieves information for all courses.
     *
     * @return a response entity containing a list of course info DTOs
     */
    @GetMapping("/course/info")
    ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCourseInfo();

    /**
     * Retrieves information for all bundles.
     *
     * @return a response entity containing a list of bundle info DTOs
     */
    @GetMapping("/bundles/course-bundles/info")
    ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> getBundleInfo();

    /**
     * Retrieves the progress of a user in a course.
     *
     * @param userId   the user ID
     * @param courseId the course ID
     * @return a response entity containing the course progress as a double
     */
    @GetMapping("/user-progress")
    public ResponseEntity<Double> getCourseProgress(@RequestParam Long userId, @RequestParam Long courseId);

    /**
     * Retrieves the existing course IDs from a list.
     *
     * @param courseIds the list of course IDs
     * @return a response entity containing the list of existing course IDs
     */
    @PostMapping("/course/existing-ids")
    ResponseEntity<List<Long>> getExistingCourseIds(@RequestBody List<Long> courseIds);

    /**
     * Retrieves the existing bundle IDs from a list.
     *
     * @param bundleIds the list of bundle IDs
     * @return a response entity containing the list of existing bundle IDs
     */
    @PostMapping("/bundles/existing-ids")
    ResponseEntity<List<Long>> getExistingBundleIds(@RequestBody List<Long> bundleIds);

    /**
     * Finds course IDs by bundle ID.
     *
     * @param bundleId the bundle ID
     * @return a response entity containing the list of course IDs
     */
    @GetMapping("/course-bundles/bundle-id/{id}/course-ids")
    ResponseEntity<List<Long>> findCourseIdsByBundleId(@PathVariable("id") Long bundleId);

    /**
     * Retrieves course progress with meta information for a user and course.
     *
     * @param userId   the user ID
     * @param courseId the course ID
     * @return a DTO containing course progress with meta information
     */
    @GetMapping("user-progress/meta")
    public CourseProgressWithMetaDTO getCourseProgressWithMeta(@RequestParam Long userId, @RequestParam Long courseId);

    /**
     * Retrieves course information by course ID.
     *
     * @param id the course ID
     * @return a response entity containing the course info DTO
     */
    @GetMapping("/course/{id}")
    ResponseEntity<StandardResponseOutDTO<CourseInfoOutDTO>> getCourseById(@PathVariable Long id);
}
