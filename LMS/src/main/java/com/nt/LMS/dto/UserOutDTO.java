package com.nt.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user output, including user details and manager information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOutDTO {

    /**
     * The ID of the user.
     */
    private long userId;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The manager of the user.
     * Can be null if the user does not have a manager.
     */
    private String manager;

    /**
     * The manager of the user.
     * Can be null if the user does not have a manager.
     */
    private String role;

    /**
     * The message associated with the user.
     * Can be used for status messages or notifications.
     */
    private String message;
}
