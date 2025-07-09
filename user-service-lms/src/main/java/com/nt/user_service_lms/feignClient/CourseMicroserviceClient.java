package com.nt.user_service_lms.feignClient;


import com.nt.user_service_lms.config.FeignTokenInterceptor;
import com.nt.user_service_lms.dto.outDTO.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "course-service", url = "http://localhost:8080/api/service-api",configuration = FeignTokenInterceptor.class)
public interface CourseMicroserviceClient {

    @GetMapping("/course/{id}/exists")
    boolean courseExistsById(@PathVariable("id") Long courseId);

    @GetMapping("/bundles/{id}/exists")
    boolean bundleExistsById(@PathVariable("id") Long bundleId);

    @GetMapping("/bundles/course-bundles/bundle/{id}")
    ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> getAllCoursesByBundleId(@PathVariable("id") Long bundleId);

    @GetMapping("/course/{id}/name")
    public ResponseEntity<String> getCourseNameById(@PathVariable("id") Long id);

    @GetMapping("/bundles/{id}/name")
    public ResponseEntity<StandardResponseOutDTO<String>> getBundleNameById(@PathVariable("id") Long id);

    @GetMapping("/course/info")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getCourseInfo();

    @GetMapping("/bundles/course-bundles/info")
    public  ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> getBundleInfo();

    @GetMapping("/user-progress")
    public ResponseEntity<Double> getCourseProgress(@RequestParam int userId, @RequestParam int courseId);

    @PostMapping("/course/existing-ids")
    public ResponseEntity<List<Long>> getExistingCourseIds(@RequestBody List<Long> courseIds);

    @PostMapping("/bundles/existing-ids")
    public ResponseEntity<List<Long>> getExistingBundleIds(@RequestBody List<Long> bundleIds);

    @GetMapping("/course-bundles/bundle-id/{id}/course-ids")
    public ResponseEntity<List<Long>> findCourseIdsByBundleId(@PathVariable("id") Long bundleId);

    @GetMapping("user-progress/meta")
    public CourseProgressWithMetaDTO getCourseProgressWithMeta(@RequestParam int userId, @RequestParam int courseId);

    @GetMapping("/course/{id}")
    public ResponseEntity<StandardResponseOutDTO<CourseInfoOutDTO>> getCourseById(@PathVariable final Long id) ;

    }
