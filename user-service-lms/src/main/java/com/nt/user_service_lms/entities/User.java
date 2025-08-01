package com.nt.user_service_lms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a user in the system. This entity contains the user's details,
 * including their username, first name, last name, email, password, manager ID, and role ID.
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    /**
     * The username of the user, which must be unique.
     */
    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    /**
     * The first name of the user.
     */
    @Column(name = "firstname", nullable = false)
    private String firstName;

    /**
     * The last name of the user.
     */
    @Column(name = "lastname", nullable = false)
    private String lastName;

    /**
     * The email address of the user, which must be unique.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * The password for the user.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The ID of the user's manager. Defaults to the ADMIN_ID.
     */
    @Column(name = "manager_id")
    private Long managerId;

    /**
     * The ID of the role assigned to the user.
     */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * The timestamp when the user was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;


    /**
     * The timestamp when the user's details were last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * The status for the user.
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "Boolean Default True")
    private boolean active = true;


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
        User user = (User) o;
        return userId == user.userId && active == user.active
                && Objects.equals(userName, user.userName) && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email)
                && Objects.equals(password, user.password) && Objects.equals(managerId, user.managerId)
                && Objects.equals(roleId, user.roleId) && Objects.equals(createdAt, user.createdAt)
                && Objects.equals(updatedAt, user.updatedAt);
    }

    /**
     * generates hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, firstName, lastName, email, password, managerId,
                roleId, createdAt, updatedAt, active);
    }
}
