package com.nt.LMS.feignClient;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "course-service-lms", url = "http:// localhost:8080/api")
public interface CourseClient {

}
