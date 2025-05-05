package com.nt.LMS.feignClient;


import com.nt.LMS.dto.BundleInfoDTO;
import com.nt.LMS.dto.CourseBundleDTO;
import com.nt.LMS.dto.CourseInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "course-service-lms", url = "http://localhost:8080/api")
public interface CourseMicroserviceClient {

    @GetMapping("/course/{id}/exists")
    boolean courseExistsById(@PathVariable("id") Long courseId);

    @GetMapping("/bundles/{id}/exists")
    boolean bundleExistsById(@PathVariable("id") Long bundleId);

    @GetMapping("/bundles/course-bundles/bundle/{id}")
    ResponseEntity<List<CourseBundleDTO>> getAllCoursesByBundleId(@PathVariable("id") Long bundleId);

    @GetMapping("/course/{id}/name")
    public ResponseEntity<String> getCourseNameById(@PathVariable("id") Long id);

    @GetMapping("/bundles/{id}/name")
    public ResponseEntity<String> getBundleNameById(@PathVariable("id") Long id);

    @GetMapping("/course/info")
    public ResponseEntity<List<CourseInfoDTO>> getCourseInfo();

    @GetMapping("/bundles/course-bundles/info")
    public  ResponseEntity<List<BundleInfoDTO>> getBundleInfo();
}
