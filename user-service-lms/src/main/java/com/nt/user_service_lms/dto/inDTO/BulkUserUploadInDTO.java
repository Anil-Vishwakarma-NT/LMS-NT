package com.nt.user_service_lms.dto.inDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUserUploadInDTO {

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only alphabets")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only alphabets")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._%+-]*@nucleusteq\\.com$",
            message = "Email must be from @nucleusteq.com domain")
    private String email;

    @NotBlank(message = "Employee number is required")
    private String employeeNumber;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(EMPLOYEE|MANAGER)$", message = "Role must be either 'EMPLOYEE' or 'MANAGER'")
    private String role;

    private int rowNumber;
}

