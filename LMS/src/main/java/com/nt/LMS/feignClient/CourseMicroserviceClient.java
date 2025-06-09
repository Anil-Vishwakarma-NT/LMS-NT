package com.nt.LMS.feignClient;


import com.nt.LMS.config.FeignClientConfig;
import com.nt.LMS.dto.BundleInfoDTO;
import com.nt.LMS.dto.CourseBundleDTO;
import com.nt.LMS.dto.CourseInfoDTO;
import com.nt.LMS.dto.StandardResponseOutDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "course-service-lms", url = "http://localhost:8080/api",configuration = FeignClientConfig.class)
public interface CourseMicroserviceClient {

    @GetMapping("/course/{id}/exists")
    boolean courseExistsById(@PathVariable("id") Long courseId);

    @GetMapping("/bundles/{id}/exists")
    boolean bundleExistsById(@PathVariable("id") Long bundleId);

    @GetMapping("/bundles/course-bundles/bundle/{id}")
    ResponseEntity<StandardResponseOutDTO<List<CourseBundleDTO>>> getAllCoursesByBundleId(@PathVariable("id") Long bundleId);

    @GetMapping("/course/{id}/name")
    public ResponseEntity<String> getCourseNameById(@PathVariable("id") Long id);

    @GetMapping("/bundles/{id}/name")
    public ResponseEntity<String> getBundleNameById(@PathVariable("id") Long id);

    @GetMapping("/course/info")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoDTO>>> getCourseInfo();

    @GetMapping("/bundles/course-bundles/info")
    public  ResponseEntity<StandardResponseOutDTO<List<BundleInfoDTO>>> getBundleInfo();

    @GetMapping("/user-progress")
    public ResponseEntity<Double> getCourseProgress(@RequestParam int userId, @RequestParam int courseId);
}
