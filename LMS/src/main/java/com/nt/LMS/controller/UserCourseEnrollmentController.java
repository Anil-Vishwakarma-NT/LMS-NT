package com.nt.LMS.controller;

import com.nt.LMS.dto.UserCourseEnrollmentOutDTO;
import com.nt.LMS.service.UserCourseEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/enrollments")
public class UserCourseEnrollmentController {

    @Autowired
    private UserCourseEnrollmentService userCourseEnrollmentService;

    @GetMapping
    public ResponseEntity<List<UserCourseEnrollmentOutDTO>> getUsersEnrolledFilterByCourse() {
        List<UserCourseEnrollmentOutDTO> userCourseEnrollmentOutDTOS = userCourseEnrollmentService.getUserEnrollmentsByCourse();
        return ResponseEntity.ok(userCourseEnrollmentOutDTOS);
    }
}
