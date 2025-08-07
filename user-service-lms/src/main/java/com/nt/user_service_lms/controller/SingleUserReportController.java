package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.SingleUserReportOutDTO;
import com.nt.user_service_lms.service.serviceImpl.SingleUserReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-api/reports")
@RequiredArgsConstructor
public class SingleUserReportController {

    private final SingleUserReportServiceImpl reportService;

    @GetMapping("/user/{userId}")
    public SingleUserReportOutDTO getSingleUserReport(@PathVariable Long userId) {
        return reportService.getUserReport(userId);
    }
}
