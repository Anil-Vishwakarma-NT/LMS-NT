package com.nt.LMS.dto.inDTO;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public class EnrollmentRequestDTO {

    @NotNull(message = "Assigned by user ID is required")
    private Long assignedBy;

    // Entity Information - either userIds OR groupIds should be provided
    private List<Long> userIds;
    private List<Long> groupIds;

    // Learning Content - either courseIds OR bundleIds should be provided
    private List<Long> courseIds;
    private List<Long> bundleIds;

    private LocalDateTime deadline;
    private String status = "ACTIVE";

    // Optional: Force enrollment even if conflicts exist
    private boolean forceEnrollment = false;

    // Optional: Skip creating individual enrollments for group enrollments
    private boolean groupEnrollmentOnly = false;

    public EnrollmentRequestDTO() {}

    // Getters and Setters
    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }

    public List<Long> getBundleIds() {
        return bundleIds;
    }

    public void setBundleIds(List<Long> bundleIds) {
        this.bundleIds = bundleIds;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isForceEnrollment() {
        return forceEnrollment;
    }

    public void setForceEnrollment(boolean forceEnrollment) {
        this.forceEnrollment = forceEnrollment;
    }

    public boolean isGroupEnrollmentOnly() {
        return groupEnrollmentOnly;
    }

    public void setGroupEnrollmentOnly(boolean groupEnrollmentOnly) {
        this.groupEnrollmentOnly = groupEnrollmentOnly;
    }

    // Validation helper methods
    public boolean hasUsers() {
        return userIds != null && !userIds.isEmpty();
    }

    public boolean hasGroups() {
        return groupIds != null && !groupIds.isEmpty();
    }

    public boolean hasCourses() {
        return courseIds != null && !courseIds.isEmpty();
    }

    public boolean hasBundles() {
        return bundleIds != null && !bundleIds.isEmpty();
    }

    public boolean isValid() {
        // Either users or groups must be provided
        boolean hasTargets = hasUsers() || hasGroups();
        // Either courses or bundles must be provided
        boolean hasContent = hasCourses() || hasBundles();
        // Cannot have both users and groups, or both courses and bundles
        boolean noConflicts = !(hasUsers() && hasGroups()) && !(hasCourses() && hasBundles());

        return hasTargets && hasContent && noConflicts;
    }
}
