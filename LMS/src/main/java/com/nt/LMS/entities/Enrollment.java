package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
@Data
@NoArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private long enrollmentId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "course_id")
    private long courseId;

    @Column(name = "bundle_id")
    private long bundleId;

    @Column(name = "assigned_by")
    private long assignedBy;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "is_enrolled")
    private boolean isEnrolled;

    @Column(name = "is_individual_assigned")
    private boolean isIndividualAssigned;

    @Column(name = "is_group_assigned")
    private boolean isGroupAssigned;
}
