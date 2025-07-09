package com.nt.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's progress on course content.
 * Maps to the user_progress table in the database.
 */
@Entity
@Table(name = "user_progress")
@Getter
@Setter
@Builder
public class UserProgress {

    /**
     * Unique identifier for the user progress record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int progressId;

    /**
     * ID of the user associated with this progress record.
     */
    @Column(nullable = false)
    private int userId;

    /**
     * ID of the content (e.g., PDF or video) being tracked.
     */
    @Column(nullable = false)
    private int contentId;

    /**
     * ID of the course to which the content belongs.
     */
    @Column(nullable = false)
    private int courseId; // Added course ID to match the database table

    /**
     * Type of content, such as 'pdf' or 'video'.
     */
    @Column(nullable = false)
    private String contentType; // 'pdf' or 'video'

    /**
     * Last viewed position: PDF page number or video timestamp.
     */
    @Column(nullable = false)
    private double lastPosition = 0.0; // PDF page number or video timestamp

    /**
     * Percentage of the individual content completed by the user.
     */
    @Column(nullable = false)
    private double contentCompletionPercentage = 0.0; // Individual content completion

    /**
     * Overall percentage of the course completed by the user.
     */
    @Column(nullable = false)
    private double courseCompletionPercentage = 0.0; // Overall course completion

    /**
     * Indicates whether the user has completed the course.
     * Once true, it should not revert to false.
     */
    @Column(nullable = false)
    private boolean courseCompleted = false; // Ensures course doesn’t downgrade


    /**
     * Timestamp of the last progress update.
     */
    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    /**
     * Timestamp when the course was first completed by the user.
     */
    @Column(name = "first_completed_at")
    private LocalDateTime firstCompletedAt;

    /**
     * Default constructor.
     */
    public UserProgress() {
    }

    /**
     * Parameterized constructor to initialize all fields.
     */
    public UserProgress(int progressId, int userId, int contentId, int courseId, String contentType, double lastPosition, double contentCompletionPercentage, double courseCompletionPercentage, boolean courseCompleted, LocalDateTime lastUpdated, LocalDateTime firstCompletedAt) {
        this.progressId = progressId;
        this.userId = userId;
        this.contentId = contentId;
        this.courseId = courseId;
        this.contentType = contentType;
        this.lastPosition = lastPosition;
        this.contentCompletionPercentage = contentCompletionPercentage;
        this.courseCompletionPercentage = courseCompletionPercentage;
        this.courseCompleted = courseCompleted;
        this.lastUpdated = lastUpdated;
        this.firstCompletedAt = firstCompletedAt;
    }

    /**
     * Checks equality based on all fields.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserProgress that = (UserProgress) o;
        return progressId == that.progressId && userId == that.userId && contentId == that.contentId && courseId == that.courseId && Double.compare(lastPosition, that.lastPosition) == 0 && Double.compare(contentCompletionPercentage, that.contentCompletionPercentage) == 0 && Double.compare(courseCompletionPercentage, that.courseCompletionPercentage) == 0 && courseCompleted == that.courseCompleted && Objects.equals(contentType, that.contentType) && Objects.equals(lastUpdated, that.lastUpdated) && Objects.equals(firstCompletedAt, that.firstCompletedAt);
    }

    /**
     * Generates hash code based on all fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(progressId, userId, contentId, courseId, contentType, lastPosition, contentCompletionPercentage, courseCompletionPercentage, courseCompleted, lastUpdated, firstCompletedAt);
    }
}
