package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDetailsByCourseIDOutDTO {
    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private List<UserQuizAttemptDetailsOutDTO> userQuizAttemptDetailsOutDTOS;
}
