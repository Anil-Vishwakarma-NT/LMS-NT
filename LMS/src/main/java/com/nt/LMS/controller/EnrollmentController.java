package com.nt.LMS.controller;

import com.nt.LMS.dto.*;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.exception.UnauthorizedAccessException;
import com.nt.LMS.service.EnrollmentService;
import com.nt.LMS.service.UserBundleEnrollmentService;
import com.nt.LMS.service.UserCourseEnrollmentService;
import com.nt.LMS.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private UserService userService;

    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        String response = enrollmentService.enrollUser(enrollmentDTO);
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
    public ResponseEntity<EnrollmentDashBoardStatsDTO> getEnrollmentStats() {
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
    public ResponseEntity<List<UserEnrollmentsDTO>> getUserEnrollmentsFilterByUser() {
        List<UserEnrollmentsDTO> userEnrollmentsDTOS = enrollmentService.getEnrollmentsForUser();
        return ResponseEntity.ok(userEnrollmentsDTOS);
    }

    @GetMapping("/user/{userId}/enrolled-courses")
    public ResponseEntity<List<UserEnrollDetails>> getUserEnrolledCourses(@PathVariable Long userId) {
        List<UserEnrollDetails> enrollments = userCourseEnrollmentService.getUserEnrolledCourses(userId);
        return ResponseEntity.ok(enrollments);
    }

//    @GetMapping("/user/enrolled-courses")
//    public ResponseEntity<List<UserEnrollDetails>> getUserEnrolledCourses() {
//        try {
//            User authenticatedUser = userService.getAuthenticatedUser();
//
//        List<UserEnrollDetails> enrollments = userCourseEnrollmentService.getUserEnrolledCourses(authenticatedUser.getUserId());
//        return ResponseEntity.ok(enrollments);
//
//        } catch (UnauthorizedAccessException e) {
//            log.error("Unauthorized access attempt: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } catch (ResourceNotFoundException e) {
//            log.error("User not found: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        } catch (Exception e) {
//            log.error("Error retrieving user statistics: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
