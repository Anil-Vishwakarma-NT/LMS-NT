package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for creating a new quiz.
 */
@Data
public class QuizCreateInDTO {

    @NotBlank(message = "Parent type is required")
    @Size(max = 16, message = "Parent type must not exceed 16 characters")
    @Pattern(regexp = "^(course|bundle|course-content)$", message = "Parent type must be 'course', 'bundle', or 'course-content'")
    private String parentType;

    @NotNull(message = "Parent ID is required")
    @Positive(message = "Parent ID must be positive")
    private Long parentId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 600, message = "Time limit must not exceed 600 minutes (10 hours)")
    private Integer timeLimit;

    @NotNull(message = "Attempts allowed is required")
    @Min(value = 1, message = "At least 1 attempt must be allowed")
    @Max(value = 10, message = "Maximum 10 attempts allowed")
    private Integer attemptsAllowed = 1;

    @DecimalMin(value = "0.00", message = "Passing score must be at least 0")
    @Digits(integer = 4, fraction = 2, message = "Passing score must have at most 4 integer digits and 2 decimal places")
    private BigDecimal passingScore;

    private Boolean randomizeQuestions = false;

    private Boolean showResults = false;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @Positive(message = "Created by must be positive")
    private Integer createdBy;
}
