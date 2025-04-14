package com.nt.LMS.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInDTO {

    @Pattern(
            regexp = "^[0-9]",
            message = "Invalid Id"
    )
    private long userId;

    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Role name must contain only alphabets."
    )
    private String Role;


}
