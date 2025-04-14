package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_enrollment")
@Data
public class GroupEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_enrollment_id")
    private long groupEnrollmentId;
    @Column(name = "group_id")
    private long groupId;
    @Column(name = "course_id")
    private long courseId;
    @Column(name = "bundle_id")
    private long bundleId;
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    @Column(name = "assigned_by")
    private long assignedBy;
}
