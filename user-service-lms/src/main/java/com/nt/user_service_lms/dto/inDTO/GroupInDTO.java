package com.nt.user_service_lms.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for group input operations.
 * Represents the input data for creating, updating, or managing groups
 * within the learning management system.
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInDTO {

    /**
     * The name of the group.
     * Must contain only alphabetic characters (a-z, A-Z).
     * Special characters and numbers are not allowed.
     */
    @Pattern(
            regexp = "^[a-zA-Z0-9 _-]+$",
            message = "Group name must contain only alphabets."
    )
    private String groupName;

    /**
     * The unique identifier of the group.
     * Represents the primary key for the group entity.
     */
    private long groupId;

    /**
     * The unique identifier of the user associated with this group operation.
     * Typically represents the user performing the group operation or the group owner.
     */
    private long userId;

    /**
     * List of employee IDs who are members of this group.
     * Contains the unique identifiers of users assigned to the group.
     */
    private List<Long> employees;

    /**
     * List of course IDs assigned to this group.
     * Contains the unique identifiers of courses that group members should complete.
     */
    private List<Long> courses;

    /**
     * The deadline for completing group-assigned tasks or courses.
     * Represents the date and time by which group activities should be completed.
     */
    private LocalDateTime deadline;

    /**
     * The timestamp when the group assignment was created.
     * Represents when the group or its assignments were initially assigned.
     */
    private LocalDateTime assignedAt;

    /**
     * Sets the name of the group.
     *
     * @param groupName the group name to set
     */
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    /**
     * Sets the unique identifier of the group.
     *
     * @param groupId the group ID to set
     */
    public void setGroupId(final long groupId) {
        this.groupId = groupId;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param userId the user ID to set
     */
    public void setUserId(final long userId) {
        this.userId = userId;
    }

    /**
     * Sets the list of employee IDs for the group.
     *
     * @param employees the list of employee IDs to set
     */
    public void setEmployees(final List<Long> employees) {
        this.employees = employees;
    }

    /**
     * Sets the list of course IDs assigned to the group.
     *
     * @param courses the list of course IDs to set
     */
    public void setCourses(final List<Long> courses) {
        this.courses = courses;
    }

    /**
     * Sets the deadline for group activities.
     *
     * @param deadline the deadline to set
     */
    public void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    /**
     * Sets the timestamp when the group assignment was created.
     *
     * @param assignedAt the assignment timestamp to set
     */
    public void setAssignedAt(final LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
