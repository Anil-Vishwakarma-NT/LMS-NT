package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BundleDTO {
    @NotBlank(message = "Bundle Name cannot be empty")
    @Size(min = 3, message = "Bundle Name must be at least 3 characters long")
    @Pattern(
            regexp = "^(?!\\d)(?!\\s)[A-Za-z][A-Za-z0-9]*(?<!\\s)$",
            message = "Bundle Name invalid"
    )

    private String bundleName;
}


