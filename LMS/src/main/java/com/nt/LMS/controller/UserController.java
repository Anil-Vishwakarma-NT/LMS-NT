package com.nt.LMS.controller;


import com.nt.LMS.outDTO.UserOutDTO;
import lombok.extern.slf4j.Slf4j;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
}