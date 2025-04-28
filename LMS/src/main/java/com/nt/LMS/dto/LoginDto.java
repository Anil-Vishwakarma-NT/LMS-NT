package com.nt.LMS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
     * The password of the user.
     * This is required for login and cannot be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

}
