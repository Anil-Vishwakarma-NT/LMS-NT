package com.nt.LMS.controller;

import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import com.nt.LMS.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.LMS.service.UserCourseEnrollmentService;
import com.nt.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/enrollments")
public class UserCourseEnrollmentController {

    @Autowired
    private UserCourseEnrollmentService userCourseEnrollmentService;


    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>>> getUsersEnrolledFilterByCourse() {
        List<UserCourseEnrollmentOutDTO> userCourseEnrollmentOutDTOS = userCourseEnrollmentService.getUserEnrollmentsByCourse();
        StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>> standardResponseOutDTO = StandardResponseOutDTO.success(userCourseEnrollmentOutDTOS, "Fetched Users Enrolled");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    @GetMapping("/{userId}/statistics")
    public ResponseEntity<StandardResponseOutDTO<Map<String,Long>>> getUserEnrollments(@PathVariable Long userId){
        System.out.println(userId + "USERID *****");
        Map<String , Long> stats = userService.userStatistics(userId);
        StandardResponseOutDTO<Map<String,Long>> standardResponseOutDTO = StandardResponseOutDTO.success(stats, "Fetched Users Enrolled");
        return ResponseEntity.ok(standardResponseOutDTO);
    }


}
