package com.nt.LMS.controller;

import com.nt.LMS.dto.inDTO.EnrollmentInDTO;
import com.nt.LMS.dto.inDTO.EnrollmentRequestDTO;
import com.nt.LMS.dto.outDTO.*;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.service.EnrollmentService;
import com.nt.LMS.service.EnrollmentsService;
import com.nt.LMS.service.UserBundleEnrollmentService;
import com.nt.LMS.service.UserCourseEnrollmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<Enrollment>> enroll(@Valid @RequestBody EnrollmentRequestDTO enrollmentRequestDTO) {
        return ResponseEntity.ok(enrollmentService.enroll(enrollmentRequestDTO));
    }
}