package com.nt.LMS.controller;

import com.nt.LMS.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.LMS.dto.outDTO.EnrollmentStatsDTO;
import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing enrollments in the Learning Management System.
 * Provides endpoints for user enrollment operations, statistics, and enrollment data retrieval.
 */
@Slf4j
@RestController
@RequestMapping("/api/enrollment")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EnrollmentController {

    @Autowired
    EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<StandardResponseOutDTO<List<Enrollment>>> enroll(@Valid @RequestBody EnrollmentRequestInDTO enrollmentRequestInDTO) {
        List<Enrollment> enrollments =  enrollmentService.enroll(enrollmentRequestInDTO);
        StandardResponseOutDTO<List<Enrollment>> standardResponseOutDTO = StandardResponseOutDTO.success(enrollments, "Enrollment Successful");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StandardResponseOutDTO<EnrollmentStatsDTO>> getEnrollmentStatistics() {
            EnrollmentStatsDTO stats = enrollmentService.getEnrollmentStatistics();
            StandardResponseOutDTO<EnrollmentStatsDTO> standardResponseOutDTO = StandardResponseOutDTO.success(stats, "Fetched Enrollment Statistics");
            return ResponseEntity.ok(standardResponseOutDTO);
    }
}