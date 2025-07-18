package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * DTO for quiz submission input containing user responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionInDTO {

    @Valid
    private List<UserResponseInDTO> userResponses;

    // Optional metadata
    private String notes;
    private Long timeSpent; // total time spent on quiz in seconds

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizSubmissionInDTO that = (QuizSubmissionInDTO) o;
        return Objects.equals(userResponses, that.userResponses) && Objects.equals(notes, that.notes) && Objects.equals(timeSpent, that.timeSpent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userResponses, notes, timeSpent);
    }
}