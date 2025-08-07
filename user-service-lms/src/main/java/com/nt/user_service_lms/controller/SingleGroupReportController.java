package com.nt.user_service_lms.controller;


import com.nt.user_service_lms.dto.outDTO.SingleGroupReportOutDTO;
import com.nt.user_service_lms.service.serviceImpl.SingleGroupReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-api/reports")
@RequiredArgsConstructor
public class SingleGroupReportController {

    private final SingleGroupReportServiceImpl groupReportService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<SingleGroupReportOutDTO> getGroupReport(@PathVariable Long groupId) {
        SingleGroupReportOutDTO dto = groupReportService.getGroupReport(groupId);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
