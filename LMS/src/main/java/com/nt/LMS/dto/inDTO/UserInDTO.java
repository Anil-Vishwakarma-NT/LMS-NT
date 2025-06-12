package com.nt.LMS.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user input, including user ID and role.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInDTO {

    /**
     * The ID of the user.
     * Must match the pattern of digits.
     */
    @Pattern(
            regexp = "^[0-9]",
            message = "Invalid Id"
    )
    private long userId;

    private String email;
    private String firstName;
    private String lastName;
    private String userName;


    /**
     * The role of the user.
     * Must contain only alphabets.
     */
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Role name must contain only alphabets."
    )
    private String role;


//    private String userName;
//    private String firstName;
//    private String lastName;
//    private String email;


}
