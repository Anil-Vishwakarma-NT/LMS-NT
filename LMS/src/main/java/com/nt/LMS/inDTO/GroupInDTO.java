package com.nt.LMS.inDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to accept input for group operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInDTO {

    /**
     * Name of the group.
     * Must contain only alphabets.
     */
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Group name must contain only alphabets."
    )
    private String groupName;

    /**
     * ID of the group.
     * Must be a positive number.
     */
    @Min(value = 1, message = "Group ID must be a positive number.")
    private long groupId;

    /**
     * ID of the user.
     * Must be a positive number.
     */
    @Min(value = 1, message = "User ID must be a positive number.")
    private long userId;
}
