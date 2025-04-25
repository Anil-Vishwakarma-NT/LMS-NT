package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "enrollment_history")
public class EnrollmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "bundle_id")
    private Long bundleId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}