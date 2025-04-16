package com.nt.LMS.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for login credentials.
 * Contains email and password used for user authentication.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    /**
     * The email address of the user.
     * This is required for login and cannot be blank.
     */
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * The password of the user.
     * This is required for login and cannot be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

}
