package com.nt.LMS.controller;


import com.nt.LMS.dto.outDTO.CourseDeadlinesDTO;
import com.nt.LMS.dto.outDTO.CourseInfoOutDTO;
import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import com.nt.LMS.dto.outDTO.UserOutDTO;
import com.nt.LMS.service.UserService;
import lombok.extern.slf4j.Slf4j;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.UserRepository;
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
        String username = authentication.getName();
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> deadlines = userService.deadlineCourses(username);
        return new  ResponseEntity<>(deadlines,HttpStatus.OK);
    }

}