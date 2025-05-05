package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int progressId;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private int contentId;

    @Column(nullable = false)
    private int courseId; // Added course ID to match the database table

    @Column(nullable = false)
    private String contentType; // 'pdf' or 'video'

    @Column(nullable = false)
    private double lastPosition = 0.0; // PDF page number or video timestamp

    @Column(nullable = false)
    private double contentCompletionPercentage = 0.0; // Individual content completion

    @Column(nullable = false)
    private double courseCompletionPercentage = 0.0; // Overall course completion

    @Column(nullable = false)
    private boolean courseCompleted = false; // Ensures course doesn’t downgrade

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
}