package com.nt.LMS.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank(message = "First name is required")
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "First name must contain only alphabets."
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Last name must contain only alphabets."
    )
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, message = "Username must be at least 4 characters long")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username must not contain spaces and can only include letters, numbers, dots, underscores, and hyphens."
    )
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format.")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9._%+-]*@nucleusteq\\.com$",
            message = "Email must be from the domain @nucleusteq.com and start with an alphabet."
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{5,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character."
    )
    private String password;

    @NotNull(message = "Role is required")
    private Long roleId;
}
