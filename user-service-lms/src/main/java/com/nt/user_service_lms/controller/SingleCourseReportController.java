package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.SingleCourseReportOutDTO;
import com.nt.user_service_lms.service.serviceImpl.SingleCourseReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-api/reports")
@RequiredArgsConstructor
public class SingleCourseReportController {

    private final SingleCourseReportServiceImpl courseReportService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<SingleCourseReportOutDTO> getCourseReport(@PathVariable Long courseId) {
        SingleCourseReportOutDTO report = courseReportService.getCourseReport(courseId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}
