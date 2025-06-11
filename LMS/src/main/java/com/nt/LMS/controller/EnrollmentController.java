package com.nt.LMS.controller;

import com.nt.LMS.inDTO.EnrollmentInDTO;
import com.nt.LMS.outDTO.*;
import com.nt.LMS.service.EnrollmentService;
import com.nt.LMS.service.UserBundleEnrollmentService;
import com.nt.LMS.service.UserCourseEnrollmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserCourseEnrollmentService userCourseEnrollmentService;

    @Autowired
    private UserBundleEnrollmentService userBundleEnrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@Valid @RequestBody EnrollmentInDTO enrollmentInDTO) {
        String response = enrollmentService.enrollUser(enrollmentInDTO);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/count")
    public ResponseEntity<MessageOutDto> getTotalEnrollmentCount() {
        log.info("Fetching total enrollments count");
        long count = enrollmentService.countEnrollments();
        log.info("Total enrollments count retrieved: {}", count);
        MessageOutDto message =new MessageOutDto();
        message.setMessage(""+count);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/stats")
    public ResponseEntity<EnrollmentDashBoardStatsOutDTO> getEnrollmentStats() {
        return ResponseEntity.ok(enrollmentService.getEnrollmentStats());
    }

    @GetMapping("/user-course")
    public ResponseEntity<List<UserCourseEnrollmentOutDTO>> getUsersEnrolledFilterByCourse() {
        List<UserCourseEnrollmentOutDTO> userCourseEnrollmentOutDTOS = userCourseEnrollmentService.getUserEnrollmentsByCourse();
        return ResponseEntity.ok(userCourseEnrollmentOutDTOS);
    }

    @GetMapping("/user-bundle")
    public ResponseEntity<List<UserBundleEnrollmentOutDTO>> getUsersEnrolledFilterByBundle() {
        List<UserBundleEnrollmentOutDTO> userBundleEnrollmentOutDTOS = userBundleEnrollmentService.getUserEnrollmentsByBundle();
        return ResponseEntity.ok(userBundleEnrollmentOutDTOS);
    }

    @GetMapping("/user-enrollments")
    public ResponseEntity<List<UserEnrollmentsOutDTO>> getUserEnrollmentsFilterByUser() {
        List<UserEnrollmentsOutDTO> userEnrollmentsOutDTOS = enrollmentService.getEnrollmentsForUser();
        return ResponseEntity.ok(userEnrollmentsOutDTOS);
    }

    @GetMapping("/user/{userId}/enrolled-courses")
    public ResponseEntity<List<UserEnrollDetailsOutDTO>> getUserEnrolledCourses(@PathVariable Long userId) {
        List<UserEnrollDetailsOutDTO> enrollments = userCourseEnrollmentService.getUserEnrolledCourses(userId);
        return ResponseEntity.ok(enrollments);
    }
}
