package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.VideoProgressDTO;
import com.example.course_service_lms.service.VideoProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video-progress")
public class VideoProgressController {

    private final VideoProgressService videoProgressService;

    public VideoProgressController(VideoProgressService videoProgressService) {
        this.videoProgressService = videoProgressService;
    }

    // GET endpoint to fetch video progress by courseId and courseContentId
    @GetMapping("/{courseId}/{contentId}")
    public ResponseEntity<VideoProgressDTO> getVideoProgress(
            @PathVariable Long courseId,
            @PathVariable Long contentId
    ) {
        VideoProgressDTO progress = videoProgressService.getProgress(courseId, contentId);
        return ResponseEntity.ok(progress); // Always returns 200 with default or actual progress
    }

    // POST endpoint to update video progress
    @PostMapping("/update")
    public ResponseEntity<VideoProgressDTO> updateVideoProgress(
            @RequestBody VideoProgressDTO videoProgressDTO
    ) {
        try {
            System.out.println("Received DTO: " + videoProgressDTO); // Debugging log
            VideoProgressDTO updatedProgress = videoProgressService.updateProgress(videoProgressDTO);
            return ResponseEntity.ok(updatedProgress);
        } catch (RuntimeException e) {
            System.err.println("Error updating progress: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}