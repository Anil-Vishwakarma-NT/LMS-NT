package com.nt.LMS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.nt.LMS.constants.RegisterDtoConstants.MINIMUM_PASSWORD_LENGTH;
import static com.nt.LMS.constants.RegisterDtoConstants.USERNAME_MIN_LENGTH;

/**
 * Data Transfer Object (DTO) for user registration.
 */
@Data
public class RegisterDto {

    /**
     * First name of the user. It must contain only alphabets.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 1, message = "First name cannot be empty or just spaces.")
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "First name must contain only alphabets."
    )
    private String firstName;

    /**
     * Last name of the user. It must contain only alphabets.
     */
    @NotBlank(message = "Last name is required")
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Last name must contain only alphabets."
    )
    private String lastName;

    /**
     * Username of the user. It must be at least 4 characters long and only contain letters, numbers,
     * dots, underscores, and hyphens.
     */
    @NotBlank(message = "Username is required")
    @Size(min = USERNAME_MIN_LENGTH, message = "Username must be at least 4 characters long")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username must not contain spaces and can only include letters, numbers, dots, underscores, and hyphens."
    )
    private String userName;

    /**
     * Email of the user. It must be in a valid email format and from the domain @nucleusteq.com.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format.")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9._%+-]*@nucleusteq\\.com$",
            message = "Email must be from the domain @nucleusteq.com and start with an alphabet."
    )
    private String email;

    /**
     * Password of the user. It must contain at least one digit, one lowercase letter,
     * one uppercase letter, and one special character.
     */
    @NotBlank(message = "Password is required")
    @Size(min = MINIMUM_PASSWORD_LENGTH, message = "Password must be at least 5 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{5,}$",
            message = "Password must contain at least one digit, one lowercase letter,"
                    + " one uppercase letter, and one special character."
    )
    private String password;

    /**
     * Role ID assigned to the user.
     */
    @NotNull(message = "Role is required")
    private Long roleId;
}
