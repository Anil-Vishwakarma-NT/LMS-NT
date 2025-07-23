package com.nt.lms.api_gateway_lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

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
