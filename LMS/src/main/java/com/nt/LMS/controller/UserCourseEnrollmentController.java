package com.nt.LMS.controller;

import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import com.nt.LMS.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.LMS.service.UserService;
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

    @GetMapping("/{userId}/statistics")
    public ResponseEntity<StandardResponseOutDTO<Map<String,Long>>> getUserEnrollments(@PathVariable Long userId){
        System.out.println(userId + "USERID *****");
        Map<String , Long> stats = userService.userStatistics(userId);
        StandardResponseOutDTO<Map<String,Long>> standardResponseOutDTO = StandardResponseOutDTO.success(stats, "Fetched Users Enrolled");
        return ResponseEntity.ok(standardResponseOutDTO);
    }


}
