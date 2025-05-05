package com.nt.LMS.controller;

import com.nt.LMS.dto.UserProgressDTO;
import com.nt.LMS.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequestMapping("/api/user-progress")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;

    // Endpoint to update user progress when interacting with content
    @PostMapping("/update")
    public void updateProgress(@RequestBody UserProgressDTO progressDTO) {
        userProgressService.updateProgress(progressDTO);
    }
}