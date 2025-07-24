package com.nt.user_service_lms.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for group input operations.
 * Represents the input data for creating, updating, or managing groups
 * within the learning management system.
 *
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
     * List of bundle IDs assigned to this group.
     * Contains the unique identifiers of bundles that group members should complete.
     */
    private List<Long> bundles;

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
     * Constructs a new {@code GroupInDTO} instance with the provided values.
     *
     * @param teamAlpha       the name of the group or team.
     * @param l               the ID of the group (purpose should be clarified).
     * @param l1              the ID of the owner or creator (purpose should be clarified).
     * @param list            the list of user IDs or members associated with the group.
     * @param list1           the list of admin IDs or roles (purpose should be clarified).
     * @param localDateTime   the creation date and time of the group.
     * @param now             the current date and time for reference or modification.
     * @param <T>
     */
    public <T> GroupInDTO(final String teamAlpha, final long l, final long l1, final List<T> list, final List<T> list1,
                          final LocalDateTime localDateTime, final LocalDateTime now) {
    }

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
        GroupInDTO that = (GroupInDTO) o;
        return groupId == that.groupId && userId == that.userId
                && Objects.equals(groupName, that.groupName) && Objects.equals(employees, that.employees)
                && Objects.equals(courses, that.courses) && Objects.equals(deadline, that.deadline)
                && Objects.equals(assignedAt, that.assignedAt);
    }

    /**
     * generates hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(groupName, groupId, userId, employees, courses, deadline, assignedAt);
    }
}
