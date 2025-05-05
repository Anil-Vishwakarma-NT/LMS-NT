package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.UserProgressDTO;
import com.example.course_service_lms.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping
    public Double getCourseProgress(@RequestParam int userId, @RequestParam int courseId) {
        return userProgressService.getCourseProgress(userId, courseId);
    }
}



