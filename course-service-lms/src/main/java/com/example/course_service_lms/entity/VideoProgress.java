package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "video_progress")
public class VideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId; // Primary key

    @Column(name = "course_id", nullable = false)
    private Long courseId; // Direct field for course ID

    @Column(name = "course_content_id", nullable = false)
    private Long courseContentId; // Direct field for course content ID

    @Column(nullable = false)
    private Float lastWatchedTime = 0f; // Last playback position

    @Column(nullable = false)
    private Float percentageCompleted = 0f; // Completion percentage (0–100)

    @Column(nullable = true)
    private Float courseTotalProgress = 0f; // Optional total progress for the course

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now(); // Last updated timestamp

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now(); // Automatically set `lastUpdated`
    }
}
