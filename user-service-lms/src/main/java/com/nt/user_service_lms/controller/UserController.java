package com.nt.user_service_lms.controller;


import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/service-api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/getUserId")
    public ResponseEntity<UserOutDTO> getUserIdByEmail(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if (user.isPresent()) {
            UserOutDTO userOutDTO = new UserOutDTO();
            userOutDTO.setUserId(user.get().getUserId());
            userOutDTO.setFirstName(user.get().getFirstName());
            userOutDTO.setLastName(user.get().getLastName());

            return ResponseEntity.ok(userOutDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/getUserDetails")
    public ResponseEntity<UserOutDTO> getUserIdByAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByEmailIgnoreCase(username);

        if (user.isPresent()) {
            UserOutDTO userOutDTO = new UserOutDTO();
            userOutDTO.setUserId(user.get().getUserId());
            userOutDTO.setFirstName(user.get().getFirstName());
            userOutDTO.setLastName(user.get().getLastName());

            return ResponseEntity.ok(userOutDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StandardResponseOutDTO<UserOutDTO>> getUserNameById(@PathVariable long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(StandardResponseOutDTO.error("User not found"));
        }

        User user = userOpt.get();
        UserOutDTO userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(user.getUserId());
        userOutDTO.setFirstName(user.getFirstName());
        userOutDTO.setLastName(user.getLastName());

        return ResponseEntity.ok(
                StandardResponseOutDTO.success(userOutDTO, "Fetched user info")
        );
    }

    @GetMapping("/getDeadlines")
    public ResponseEntity<StandardResponseOutDTO<List<CourseDeadlinesDTO>>> getUserDeadlines(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }
        String username = principal.getUserEmail();
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> deadlines = userService.deadlineCourses(username);
        return new  ResponseEntity<>(deadlines,HttpStatus.OK);
    }

    @GetMapping("/userCourses")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollDetails>>> getEnrolledCoursesByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }
        String userId = principal.getUserId();
        List<UserCourseEnrollDetails> enrolledCourses = userService.getUserEnrolledCourses(Long.parseLong(userId));
        return ResponseEntity.ok(StandardResponseOutDTO.success(enrolledCourses, "Fetched enrolled courses successfully"));
    }

}