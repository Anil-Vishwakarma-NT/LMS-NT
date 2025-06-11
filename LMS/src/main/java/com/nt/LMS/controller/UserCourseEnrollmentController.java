package com.nt.LMS.controller;

import com.nt.LMS.outDTO.UserCourseEnrollmentOutDTO;
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
    public ResponseEntity<List<UserCourseEnrollmentOutDTO>> getUsersEnrolledFilterByCourse() {
        List<UserCourseEnrollmentOutDTO> userCourseEnrollmentOutDTOS = userCourseEnrollmentService.getUserEnrollmentsByCourse();
        return ResponseEntity.ok(userCourseEnrollmentOutDTOS);
    }

    @GetMapping("/{userId}/statistics")
    public ResponseEntity<Map<String,Long>> getUserEnrollments(@PathVariable Long userId){
        System.out.println(userId + "USERID *****");
        Map<String , Long> stats = userService.userStatistics(userId);
        return new ResponseEntity<>(stats , HttpStatus.OK);
    }


}
