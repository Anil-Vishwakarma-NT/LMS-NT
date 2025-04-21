package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.VideoProgressDTO;

public interface VideoProgressService {
    // Fetch video progress by courseId and courseContentId
    VideoProgressDTO getProgress(Long courseId, Long courseContentId);

    // Update video progress
    VideoProgressDTO updateProgress(VideoProgressDTO videoProgressDTO);
}