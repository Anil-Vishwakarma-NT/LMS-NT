package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "bundle_id")
    private Long bundleId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now(); // default current datetime

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "status", nullable = false)
    private String status = "active";  // default value

    @Column(name = "enrollment_source", nullable = false)
    private String enrollmentSource;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // default current datetime

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;  // nullable now

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // default true

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}