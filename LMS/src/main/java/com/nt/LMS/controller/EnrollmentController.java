package com.nt.LMS.controller;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        String response = enrollmentService.enroll(enrollmentDTO);
        return ResponseEntity.ok(response);
    }
}
