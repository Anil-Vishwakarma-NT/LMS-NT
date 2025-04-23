package com.nt.LMS.entities;

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
    private Long groupId;

    /**
     * The name of the group.
     */
    @Column(nullable = false)
    private String groupName;

    /**
     * The ID of the user who created the group.
     */
    @Column(nullable = false)
    private long creatorId;

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
