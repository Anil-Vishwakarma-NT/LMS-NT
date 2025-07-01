package com.nt.LMS.controller;

import com.nt.LMS.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.LMS.dto.outDTO.*;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.service.EnrollmentService;
import com.nt.LMS.service.serviceImpl.EnrollmentServiceImpl;
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
@RequestMapping("/api/service-api/enrollment")
public class EnrollmentController {

    @Autowired
    EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<StandardResponseOutDTO<List<EnrollmentOutDTO>>> enroll(@Valid @RequestBody EnrollmentRequestInDTO enrollmentRequestInDTO) {
        List<EnrollmentOutDTO> enrollments =  enrollmentService.enroll(enrollmentRequestInDTO);
        StandardResponseOutDTO<List<EnrollmentOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(enrollments, "Enrollment Successful");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StandardResponseOutDTO<EnrollmentDashBoardStatsOutDTO>> getEnrollmentStatistics() {
            EnrollmentDashBoardStatsOutDTO stats = enrollmentService.getEnrollmentStats();
            StandardResponseOutDTO<EnrollmentDashBoardStatsOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(stats, "Fetched Enrollment Statistics");
            return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/user-enrollments/{id}")
    public ResponseEntity<StandardResponseOutDTO<UserEnrollmentsOutDTO>> getUserEnrollmentsByUserId(@PathVariable("id") Long userId) {
        UserEnrollmentsOutDTO userEnrollmentsOutDTO = enrollmentService.getUserEnrollmentsByUserID(userId);
        StandardResponseOutDTO<UserEnrollmentsOutDTO> standardResponseOutDTO = StandardResponseOutDTO.success(userEnrollmentsOutDTO, "User Enrollment Fetched");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/user-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserEnrollmentsOutDTO>>> getUserEnrollments() {
        List<UserEnrollmentsOutDTO> userEnrollmentsOutDTOs = enrollmentService.getAllUsersEnrollments();
        StandardResponseOutDTO<List<UserEnrollmentsOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(userEnrollmentsOutDTOs, "User Enrollment Fetched");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/user-course-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>>> getUserCourseEnrollments() {
        List<UserCourseEnrollmentOutDTO> userCourseEnrollmentOutDTOS = enrollmentService.getIndividualCourseEnrollments();
        StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(userCourseEnrollmentOutDTOS, "Fetched Course Enrollments for User");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/user-bundle-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserBundleEnrollmentOutDTO>>> getUserBundleEnrollments() {
        List<UserBundleEnrollmentOutDTO> userBundleEnrollmentOutDTOS = enrollmentService.getIndividualBundleEnrollments();
        StandardResponseOutDTO<List<UserBundleEnrollmentOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(userBundleEnrollmentOutDTOS, "Fetched Bundle Enrollments for User");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/userCourses/{userId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollDetails>>> getEnrolledCoursesByUserId(@PathVariable Long userId) {
        List<UserCourseEnrollDetails> enrolledCourses = enrollmentService.getUserEnrolledCourses(userId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(enrolledCourses, "Fetched enrolled courses successfully"));
    }

}