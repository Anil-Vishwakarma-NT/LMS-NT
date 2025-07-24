package com.nt.user_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for enrollment request input.
 * Represents the request data for enrolling users or groups in courses or bundles.
 * Provides validation logic to ensure proper enrollment request structure.
 *
 * @author Generated
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestInDTO {

    /**
     * The unique identifier of the user who is assigning this enrollment.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Assigned by user ID is required")
    private Long assignedBy;

    /**
     * List of user IDs to be enrolled.
     * Either userIds or groupIds should be provided, but not both.
     */
    private List<Long> userIds;

    /**
     * List of group IDs to be enrolled.
     * Either userIds or groupIds should be provided, but not both.
     */
    private List<Long> groupIds;

    /**
     * List of course IDs for enrollment.
     * Either courseIds or bundleIds should be provided, but not both.
     */
    private List<Long> courseIds;

    /**
     * List of bundle IDs for enrollment.
     * Either courseIds or bundleIds should be provided, but not both.
     */
    private List<Long> bundleIds;

    /**
     * The deadline for completing the enrollment.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    /**
     * The status of the enrollment request.
     * Defaults to "ACTIVE" if not specified.
     */
    private String status = "ACTIVE";

    /**
     * Gets the user ID who assigned this enrollment.
     *
     * @return the assigned by user ID
     */
    public Long getAssignedBy() {
        return assignedBy;
    }

    /**
     * Sets the user ID who assigned this enrollment.
     *
     * @param assignedBy the assigned by user ID to set
     */
    public void setAssignedBy(final Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    /**
     * Gets the list of user IDs to be enrolled.
     *
     * @return the list of user IDs
     */
    public List<Long> getUserIds() {
        return userIds;
    }

    /**
     * Sets the list of user IDs to be enrolled.
     *
     * @param userIds the list of user IDs to set
     */
    public void setUserIds(final List<Long> userIds) {
        this.userIds = userIds;
    }

    /**
     * Gets the list of group IDs to be enrolled.
     *
     * @return the list of group IDs
     */
    public List<Long> getGroupIds() {
        return groupIds;
    }

    /**
     * Sets the list of group IDs to be enrolled.
     *
     * @param groupIds the list of group IDs to set
     */
    public void setGroupIds(final List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    /**
     * Gets the list of course IDs for enrollment.
     *
     * @return the list of course IDs
     */
    public List<Long> getCourseIds() {
        return courseIds;
    }

    /**
     * Sets the list of course IDs for enrollment.
     *
     * @param courseIds the list of course IDs to set
     */
    public void setCourseIds(final List<Long> courseIds) {
        this.courseIds = courseIds;
    }

    /**
     * Gets the list of bundle IDs for enrollment.
     *
     * @return the list of bundle IDs
     */
    public List<Long> getBundleIds() {
        return bundleIds;
    }

    /**
     * Sets the list of bundle IDs for enrollment.
     *
     * @param bundleIds the list of bundle IDs to set
     */
    public void setBundleIds(final List<Long> bundleIds) {
        this.bundleIds = bundleIds;
    }

    /**
     * Gets the deadline for completing the enrollment.
     *
     * @return the deadline
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline for completing the enrollment.
     *
     * @param deadline the deadline to set
     */
    public void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    /**
     * Gets the status of the enrollment request.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the enrollment request.
     *
     * @param status the status to set
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * Checks if the request contains user IDs.
     *
     * @return true if userIds list is not null and not empty, false otherwise
     */
    public boolean hasUsers() {
        return userIds != null && !userIds.isEmpty();
    }

    /**
     * Checks if the request contains group IDs.
     *
     * @return true if groupIds list is not null and not empty, false otherwise
     */
    public boolean hasGroups() {
        return groupIds != null && !groupIds.isEmpty();
    }

    /**
     * Checks if the request contains course IDs.
     *
     * @return true if courseIds list is not null and not empty, false otherwise
     */
    public boolean hasCourses() {
        return courseIds != null && !courseIds.isEmpty();
    }

    /**
     * Checks if the request contains bundle IDs.
     *
     * @return true if bundleIds list is not null and not empty, false otherwise
     */
    public boolean hasBundles() {
        return bundleIds != null && !bundleIds.isEmpty();
    }

    /**
     * Validates the enrollment request structure.
     * Ensures that either users or groups are provided (but not both),
     * and either courses or bundles are provided (but not both).
     *
     * @return true if the request is valid, false otherwise
     */
    public boolean isValid() {
        final boolean hasTargets = hasUsers() || hasGroups();
        final boolean hasContent = hasCourses() || hasBundles();
        final boolean noConflicts = !(hasUsers() && hasGroups()) && !(hasCourses() && hasBundles());

        return hasTargets && hasContent && noConflicts;
    }

    /**
     * Checks if the object is equal.
     *
     * @param o
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnrollmentRequestInDTO that = (EnrollmentRequestInDTO) o;
        return Objects.equals(assignedBy, that.assignedBy) && Objects.equals(userIds, that.userIds)
                && Objects.equals(groupIds, that.groupIds) && Objects.equals(courseIds, that.courseIds)
                && Objects.equals(bundleIds, that.bundleIds) && Objects.equals(deadline, that.deadline)
                && Objects.equals(status, that.status);
    }

    /**
     * generates hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(assignedBy, userIds, groupIds, courseIds, bundleIds, deadline, status);
    }
}
