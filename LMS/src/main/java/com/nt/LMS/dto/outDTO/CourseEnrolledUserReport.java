package com.nt.LMS.dto.outDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
public class CourseEnrolledUserReport {
    private String userEnrolled;
    private Double percentageCompleted; // Can be null
    private LocalDateTime lastViewed;
    private LocalDateTime deadline;
    private LocalDateTime firstCompletedAt;

    public CourseEnrolledUserReport(String userEnrolled, Double percentageCompleted, LocalDateTime last_viewed, LocalDateTime deadline, LocalDateTime firstCompletedAt) {
        this.userEnrolled = userEnrolled;
        this.percentageCompleted = percentageCompleted;
        this.lastViewed = last_viewed;
        this.deadline = deadline;
        this.firstCompletedAt = firstCompletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseEnrolledUserReport)) return false;
        CourseEnrolledUserReport that = (CourseEnrolledUserReport) o;
        return Objects.equals(userEnrolled, that.userEnrolled) &&
                Objects.equals(percentageCompleted, that.percentageCompleted) &&
                Objects.equals(lastViewed, that.lastViewed) &&
                Objects.equals(deadline, that.deadline) &&
                Objects.equals(firstCompletedAt, that.firstCompletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEnrolled, percentageCompleted, lastViewed, deadline, firstCompletedAt);
    }
}
