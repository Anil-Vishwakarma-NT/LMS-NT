package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_bundle_enrollment")
public class GroupBundleEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long assignmentId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "bundle_id", nullable = false)
    private Long bundleId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}