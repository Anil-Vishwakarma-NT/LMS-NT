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

import java.util.Objects;

/**
 * Entity representing the relationship between a user and a group.
 */
@Entity
@Table(name = "user_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup {

    /**
     * Unique identifier for the user-group relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the user associated with the group.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * The ID of the group associated with the user.
     */
    @Column(nullable = false)
    private Long groupId;

    /**
     * A flag for soft deletion.
     */
    @Column(name = "is_active")
    private boolean isActive = true;

    /**
     * Constructor to create a new UserGroup association.
     *
     * @param userId  the ID of the user. Must not be null.
     * @param groupId the ID of the group. Must not be null.
     */
    public UserGroup(final Long userId, final Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup userGroup = (UserGroup) o;
        return isActive == userGroup.isActive && Objects.equals(id, userGroup.id) && Objects.equals(userId, userGroup.userId) && Objects.equals(groupId, userGroup.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, groupId, isActive);
    }
}
