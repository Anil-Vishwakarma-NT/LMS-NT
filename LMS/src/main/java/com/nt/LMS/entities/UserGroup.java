package com.nt.LMS.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing the relationship between a user and a group.
 */
@Entity
@Table(name = "user_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
     * Constructor to create a new UserGroup association.
     *
     * @param userId the ID of the user. Must not be null.
     * @param groupId the ID of the group. Must not be null.
     */
    public UserGroup(final Long userId, final Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }
}
