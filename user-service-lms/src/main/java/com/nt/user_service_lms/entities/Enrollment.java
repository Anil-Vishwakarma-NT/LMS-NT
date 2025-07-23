package com.nt.user_service_lms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an enrollment record in the learning management system.
 * This class tracks user enrollments in courses, bundles, or groups, including
 * assignment details, deadlines, and completion status.
 *
 * @author System Generated
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    /**
     * Unique identifier for the enrollment record.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    /**
     * Foreign key reference to the user who is enrolled.
     * Links to the user table's primary key.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Foreign key reference to the group (if applicable).
     * Used when enrollment is for a group-based learning activity.
     * Can be null for individual enrollments.
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * Foreign key reference to the course.
     * Used when enrollment is for a specific course.
     * Can be null if enrollment is for a bundle or group activity.
     */
    @Column(name = "course_id")
    private Long courseId;

    /**
     * Foreign key reference to the bundle.
     * Used when enrollment is for a course bundle/package.
     * Can be null for individual course enrollments.
     */
    @Column(name = "bundle_id")
    private Long bundleId;

    /**
     * Foreign key reference to the user who assigned this enrollment.
     * Typically an administrator, instructor, or manager.
     * Cannot be null - every enrollment must have an assigner.
     */
    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    /**
     * Timestamp when the enrollment was assigned.
     * Automatically set to current time when enrollment is created.
     * Cannot be null.
     */
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    /**
     * Optional deadline for completing the enrollment.
     * Can be null if there's no specific deadline.
     */
    @Column(name = "deadline")
    private LocalDateTime deadline;

    /**
     * Current status of the enrollment.
     * Common values: "active", "completed", "cancelled", "expired"
     * Defaults to "active" for new enrollments.
     * Cannot be null.
     */
    @Column(name = "status", nullable = false)
    private String status = "active";

    /**
     * Source or method of enrollment creation.
     * Examples: "manual", "bulk_import", "self_enrollment", "api"
     * Cannot be null - tracks how the enrollment was created.
     */
    @Column(name = "enrollment_source", nullable = false)
    private String enrollmentSource;

    /**
     * Timestamp when the user actually started the enrolled content.
     * Can be null if the user hasn't started yet.
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * Timestamp when the user completed the enrolled content.
     * Can be null if not yet completed.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Timestamp when the enrollment record was created in the database.
     * Automatically set to current time when record is created.
     * Cannot be null.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when the enrollment record was last updated.
     * Can be null if never updated after creation.
     */
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    /**
     * Soft delete flag indicating if the enrollment is active.
     * True means the enrollment is active and visible.
     * False means the enrollment is soft-deleted/archived.
     * Defaults to true for new enrollments.
     * Cannot be null.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Gets the active status of the enrollment.
     *
     * @return true if the enrollment is active, false if soft-deleted
     */
    public Boolean getActive() {
        return isActive;
    }

    /**
     * Sets the active status of the enrollment.
     * Used for soft delete operations.
     *
     * @param active true to activate the enrollment, false to soft-delete it
     */
    public void setActive(final Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(enrollmentId, that.enrollmentId) && Objects.equals(userId, that.userId) && Objects.equals(groupId, that.groupId) && Objects.equals(courseId, that.courseId) && Objects.equals(bundleId, that.bundleId) && Objects.equals(assignedBy, that.assignedBy) && Objects.equals(assignedAt, that.assignedAt) && Objects.equals(deadline, that.deadline) && Objects.equals(status, that.status) && Objects.equals(enrollmentSource, that.enrollmentSource) && Objects.equals(startedAt, that.startedAt) && Objects.equals(completedAt, that.completedAt) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId, userId, groupId, courseId, bundleId, assignedBy, assignedAt, deadline, status, enrollmentSource, startedAt, completedAt, createdAt, updatedAt, isActive);
    }
}
