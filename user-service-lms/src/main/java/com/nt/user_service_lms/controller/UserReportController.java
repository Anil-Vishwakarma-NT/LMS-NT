package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.service.serviceImpl.UserReportServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/service-api/report")
public class UserReportController {

    private final UserReportServiceImpl service;

    public UserReportController(UserReportServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/users")
    public ResponseEntity<StandardResponseOutDTO<Map<String, Object>>> getReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> data = service.getAllUserKpis(page, size);
        return ResponseEntity.ok(StandardResponseOutDTO.success(data, "User KPI Report retrieved successfully"));
    }

}
