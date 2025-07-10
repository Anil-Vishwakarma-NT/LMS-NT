package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.user_service_lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-api/enrollments")
public class UserCourseEnrollmentController {

    @Autowired
    private UserService userService;




}
