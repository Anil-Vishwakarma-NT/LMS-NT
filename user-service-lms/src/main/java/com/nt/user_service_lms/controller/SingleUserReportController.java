package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.SingleUserReportOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.service.serviceImpl.SingleUserReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-api/reports")
@RequiredArgsConstructor
public class SingleUserReportController {

    private final SingleUserReportServiceImpl reportService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<StandardResponseOutDTO<SingleUserReportOutDTO>> getSingleUserReport(@PathVariable Long userId) {
        SingleUserReportOutDTO report = reportService.getUserReport(userId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(report, "Single User Report retrieved successfully"));
    }
}
