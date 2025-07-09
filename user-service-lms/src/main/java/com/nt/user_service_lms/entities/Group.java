package com.nt.user_service_lms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents a user-created group entity in the system.
 */
@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    /**
     * The unique identifier for the group.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    /**
     * The name of the group.
     */
    @Column(name = "group_name", nullable = false)
    private String groupName;

    /**
     * The ID of the user who created the group.
     */
    @Column(name = "creator_id", nullable = false)
    private long creatorId;

    /**
     * Soft delete flag indicating if the group is active.
     * True means the group is active and visible.
     * False means the group is soft-deleted/archived.
     * Defaults to true for new groups.
     * Cannot be null.
     */
    @Column(name = "is_active")
    private boolean isActive = true;

    /**
     * Constructor to initialize group with name and creator ID.
     *
     * @param name the group name
     * @param creatorId the creator's user ID
     */
    public Group(final String name, final long creatorId) {
        this.groupName = name;
        this.creatorId = creatorId;
    }
}
