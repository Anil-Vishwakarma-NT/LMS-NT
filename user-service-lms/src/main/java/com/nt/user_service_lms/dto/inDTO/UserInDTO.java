package com.nt.user_service_lms.dtoTest.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user input data including user details and role.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInDTO {

    /**
     * Unique identifier of the user.
     * Must consist of digits.
     */
    @Pattern(
            regexp = "^[0-9]+$",
            message = "Invalid Id"
    )
    private long userId;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Username of the user.
     */
    private String userName;

    /**
     * Role assigned to the user.
     * Must contain only alphabets.
     */
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Role name must contain only alphabets."
    )
    private String role;
}
