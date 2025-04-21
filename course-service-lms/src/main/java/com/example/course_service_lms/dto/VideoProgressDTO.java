package com.example.course_service_lms.dto;

import lombok.Data;

@Data
public class VideoProgressDTO {
    private Long courseId; // ID of the course
    private Long courseContentId; // ID of the course content (video)
    private Float lastWatchedTime; // Last playback position (in seconds)
    private Float percentageCompleted; // Completion percentage
    private Float courseTotalProgress; // Overall progress for the course (optional)

    // Constructor
    public VideoProgressDTO(Long courseId, Long courseContentId, Float lastWatchedTime, Float percentageCompleted, Float courseTotalProgress) {
        this.courseId = courseId;
        this.courseContentId = courseContentId;
        this.lastWatchedTime = lastWatchedTime;
        this.percentageCompleted = percentageCompleted;
        this.courseTotalProgress = courseTotalProgress;
    }
}