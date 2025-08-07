package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.SingleBundleReportOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.service.serviceImpl.SingleBundleReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-api/reports")
public class SingleBundleReportController {

    private final SingleBundleReportServiceImpl bundleReportService;

    @GetMapping("/bundle/{bundleId}")
    public ResponseEntity<StandardResponseOutDTO<SingleBundleReportOutDTO>> getBundleReport(@PathVariable Long bundleId) {
        SingleBundleReportOutDTO report = bundleReportService.getBundleReport(bundleId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(report, "Single Bundle Report retrieved successfully"));
    }
}
