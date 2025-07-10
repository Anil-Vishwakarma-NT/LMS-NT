package com.nt.user_service_lms.dto.outDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object representing a report of a user's enrollment in a course.
 * Contains information about the user's enrollment, progress, and relevant timestamps.
 */
@Data
@Builder
public final class CourseEnrolledUserReport {
    /**
     * The identifier of the enrolled user.
     */
    private String userEnrolled;

    /**
     * The percentage of course completion.
     */
    private Double percentageCompleted;

    /**
     * The timestamp when the course was last accessed by the user.
     */
    private LocalDateTime lastViewed;

    /**
     * The deadline for course completion.
     */
    private LocalDateTime deadline;

    /**
     * The timestamp when the course was first completed by the user.
     */
    private LocalDateTime firstCompletedAt;

    /**
     * Constructs a new CourseEnrolledUserReport with the specified parameters.
     *
     * @param userEnrolled the identifier of the enrolled user
     * @param percentageCompleted the percentage of course completion
     * @param lastViewed the timestamp of last course access
     * @param deadline the deadline for course completion
     * @param firstCompletedAt the timestamp of first course completion
     */
    public CourseEnrolledUserReport(final String userEnrolled, final Double percentageCompleted,
                                    final LocalDateTime lastViewed, final LocalDateTime deadline,
                                    final LocalDateTime firstCompletedAt) {
        this.userEnrolled = userEnrolled;
        this.percentageCompleted = percentageCompleted;
        this.lastViewed = lastViewed;
        this.deadline = deadline;
        this.firstCompletedAt = firstCompletedAt;
    }

    /**
     * Compares this CourseEnrolledUserReport with another object for equality.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseEnrolledUserReport)) {
            return false;
        }
        CourseEnrolledUserReport that = (CourseEnrolledUserReport) o;
        return Objects.equals(userEnrolled, that.userEnrolled)
            && Objects.equals(percentageCompleted, that.percentageCompleted)
            && Objects.equals(lastViewed, that.lastViewed)
            && Objects.equals(deadline, that.deadline)
            && Objects.equals(firstCompletedAt, that.firstCompletedAt);
    }

    /**
     * Generates a hash code for this CourseEnrolledUserReport.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(userEnrolled, percentageCompleted, lastViewed, deadline, firstCompletedAt);
    }
}
