package com.nt.LMS.controller;


import com.nt.LMS.dto.*;
import com.nt.LMS.service.UserService;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
import com.nt.LMS.serviceImpl.UserServiceImpl;
import com.nt.LMS.serviceImpl.AdminServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUserId")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getUserId());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}