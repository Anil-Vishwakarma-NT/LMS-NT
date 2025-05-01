package com.nt.LMS.controller;


import com.nt.LMS.dto.*;
import com.nt.LMS.service.UserService;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
import com.nt.LMS.serviceImpl.UserServiceImpl;
import com.nt.LMS.serviceImpl.AdminServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {



    @Autowired
     private UserService userService;
//      @GetMapping("/{userId}/user-enrollments")
//      public ResponseEntity<Long> getUserEnrollments(@PathVariable Long userId){
//                long count = userService.userEnrollments(userId);
//                return new ResponseEntity<>(count , HttpStatus.OK);
//      }


}
