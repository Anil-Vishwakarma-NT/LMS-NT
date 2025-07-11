package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.config.ServicePrincipal;
import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/service-api/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;

    // Endpoint to update user progress when interacting with content
    @PostMapping("/update")
    public void updateProgress(@RequestBody UserProgressOutDTO progressDTO) {
        userProgressService.updateProgress(progressDTO);
    }

    @GetMapping("/meta")
    public CourseProgressWithMetaDTO getCourseProgressWithMetaWithId(@RequestParam Long userId, @RequestParam Long courseId) {
        return userProgressService.getCourseProgressWithMeta(userId, courseId);
    }

    @GetMapping("/meta-courseId")
    public CourseProgressWithMetaDTO getCourseProgressWithMetaCourseId( @RequestParam int courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new ResourceNotFoundException("Authentication failed");
        }
        System.out.println("USER" + principal);

          String userId = principal.getUserId();
        System.out.println("USERID" + userId);
        return userProgressService.getCourseProgressWithMeta(Long.parseLong(userId), (long)courseId);
    }

    @GetMapping("/last-position")
    public Integer getLastPosition(@RequestParam Long userId, @RequestParam Long courseId, @RequestParam Long contentId) {
        return userProgressService.getLastPosition(userId, courseId, contentId);
    }

    @GetMapping("/content")
    public Double getContentProgress(@RequestParam Long userId, @RequestParam Long courseId, @RequestParam Long contentId) {
        return userProgressService.getContentProgress(userId, courseId, contentId);
    }


}




