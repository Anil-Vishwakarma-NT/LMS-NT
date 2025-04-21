package com.nt.LMS.controller;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
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
    @GetMapping("/count")
    public ResponseEntity<MessageOutDto> getTotalEnrollmentCount() {
        log.info("Fetching total enrollments count");
        long count = enrollmentService.countEnrollments();
        log.info("Total enrollments count retrieved: {}", count);
        MessageOutDto message =new MessageOutDto();
        message.setMessage(""+count);
        return ResponseEntity.ok(message);
    }
}
