package com.nt.user_service_lms.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * DTO representing user input data including user details and role.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
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
        UserInDTO userInDTO = (UserInDTO) o;
        return userId == userInDTO.userId && Objects.equals(email, userInDTO.email)
                && Objects.equals(firstName, userInDTO.firstName) && Objects.equals(lastName, userInDTO.lastName)
                && Objects.equals(userName, userInDTO.userName) && Objects.equals(role, userInDTO.role);
    }

    /**
     * generates hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, email, firstName, lastName, userName, role);
    }
}
