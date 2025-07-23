package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Represents a detailed view of a user including role, manager information, and creation timestamp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDetailsViewDTO {

    /**
     * The full name of the user.
     */
    private String fullName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The role assigned to the user.
     */
    private String role;

    /**
     * The name of the user's manager.
     */
    private String managerName;

    /**
     * The timestamp when the user was created.
     */
    private Timestamp createdAt;
}
