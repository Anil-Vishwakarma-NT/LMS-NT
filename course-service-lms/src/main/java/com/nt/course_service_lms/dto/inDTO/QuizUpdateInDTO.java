package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for updating an existing quiz.
 */
@Data
public class QuizUpdateInDTO {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 600, message = "Time limit must not exceed 600 minutes (10 hours)")
    private Integer timeLimit;

    @Min(value = 1, message = "At least 1 attempt must be allowed")
    @Max(value = 10, message = "Maximum 10 attempts allowed")
    private Integer attemptsAllowed;

    @DecimalMin(value = "0.00", message = "Passing score must be at least 0")
    @Digits(integer = 4, fraction = 2, message = "Passing score must have at most 4 integer digits and 2 decimal places")
    private BigDecimal passingScore;

    private Boolean randomizeQuestions;

    private Boolean showResults;

    private Boolean isActive;

    public QuizUpdateInDTO() {
    }

    public QuizUpdateInDTO(String title, String description, Integer timeLimit, Integer attemptsAllowed, BigDecimal passingScore, Boolean randomizeQuestions, Boolean showResults, Boolean isActive) {
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.attemptsAllowed = attemptsAllowed;
        this.passingScore = passingScore;
        this.randomizeQuestions = randomizeQuestions;
        this.showResults = showResults;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizUpdateInDTO that = (QuizUpdateInDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(timeLimit, that.timeLimit) && Objects.equals(attemptsAllowed, that.attemptsAllowed) && Objects.equals(passingScore, that.passingScore) && Objects.equals(randomizeQuestions, that.randomizeQuestions) && Objects.equals(showResults, that.showResults) && Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, timeLimit, attemptsAllowed, passingScore, randomizeQuestions, showResults, isActive);
    }
}
