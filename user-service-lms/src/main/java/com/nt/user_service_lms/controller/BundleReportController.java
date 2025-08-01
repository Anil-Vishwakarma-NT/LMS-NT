package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.service.serviceImpl.BundleReportServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/service-api/report")
public class BundleReportController {

    private final BundleReportServiceImpl service;

    public BundleReportController(BundleReportServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/bundles")
    public ResponseEntity<StandardResponseOutDTO<Map<String, Object>>> getReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> data = service.getAllBundleKpis(page, size);
        return ResponseEntity.ok(StandardResponseOutDTO.success(data, "Bundle KPI Report retrieved successfully"));
    }

}
