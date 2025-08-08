package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByCourseIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByUserIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserQuizAttemptDetailsOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.entity.QuizAttempt;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.repository.QuizAttemptRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class QuizAttemptControllerIntegrationTest {


    @LocalServerPort
    private int port;


    @Autowired
    private TestRestTemplate restTemplate;


    @Autowired
    private QuizAttemptRepository quizAttemptRepository;


    @Autowired
    private QuizRepository quizRepository;


    @Autowired
    private CourseRepository courseRepository;


    private static Long testQuizId;
    private static Long testUserId = 1L;
    private static Long secondUserId = 2L;
    private static Long testCourseId;
    private static Long testAttemptId;
    private static Long secondAttemptId;


    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/quiz-attempt";
    }


    private HttpHeaders createAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-admin");
        headers.set("X-Test-Role", "ADMIN");
        return headers;
    }


    // ==================== SETUP TEST DATA ====================


    @Test
    @Order(1)
    void setupTestData() {
        // Create test course
        Course testCourse = Course.builder()
                .title("Java Programming Course")
                .ownerId(1L)
                .description("Comprehensive Java Course")
                .level("BEGINNER")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse = courseRepository.save(testCourse);
        testCourseId = savedCourse.getCourseId();


        // Create test quiz
        Quiz testQuiz = Quiz.builder()
                .parentType("course")
                .parentId(testCourseId)
                .title("Java Basics Quiz")
                .description("Test your Java fundamentals")
                .timeLimit(60)
                .attemptsAllowed(3)
                .passingScore(new BigDecimal("70.00"))
                .randomizeQuestions(false)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Quiz savedQuiz = quizRepository.save(testQuiz);
        testQuizId = savedQuiz.getQuizId();


        // Create test quiz attempts
        QuizAttempt attempt1 = QuizAttempt.builder()
                .attempt(1L)
                .quizId(testQuizId)
                .userId(testUserId)
                .startedAt(LocalDateTime.now().minusHours(1))
                .finishedAt(LocalDateTime.now().minusMinutes(30))
                .scoreDetails("{\"totalScore\":80,\"maxScore\":100}")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusMinutes(30))
                .build();
        QuizAttempt savedAttempt1 = quizAttemptRepository.save(attempt1);
        testAttemptId = savedAttempt1.getQuizAttemptId();


        QuizAttempt attempt2 = QuizAttempt.builder()
                .attempt(1L)
                .quizId(testQuizId)
                .userId(secondUserId)
                .startedAt(LocalDateTime.now().minusMinutes(30))
                .status("IN_PROGRESS")
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .updatedAt(LocalDateTime.now().minusMinutes(30))
                .build();
        QuizAttempt savedAttempt2 = quizAttemptRepository.save(attempt2);
        secondAttemptId = savedAttempt2.getQuizAttemptId();
    }


    // ==================== CREATE QUIZ ATTEMPT TESTS ====================


    @Test
    @Order(2)
    void shouldCreateQuizAttemptSuccessfully() {
        QuizAttemptCreateInDTO request = QuizAttemptCreateInDTO.builder()
                .quizId(testQuizId)
                .userId(3L) // New user
                .build();


        HttpEntity<QuizAttemptCreateInDTO> entity = new HttpEntity<>(request, createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getQuizId()).isEqualTo(testQuizId);
        assertThat(response.getBody().getUserId()).isEqualTo(3L);
        assertThat(response.getBody().getAttempt()).isEqualTo(1L);
        assertThat(response.getBody().getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(response.getBody().getStartedAt()).isNotNull();
        assertThat(response.getBody().getCreatedAt()).isNotNull();
    }


    @Test
    @Order(3)
    void shouldReturnExistingActiveAttempt() {
        QuizAttemptCreateInDTO request = QuizAttemptCreateInDTO.builder()
                .quizId(testQuizId)
                .userId(secondUserId) // User with existing IN_PROGRESS attempt
                .build();


        HttpEntity<QuizAttemptCreateInDTO> entity = new HttpEntity<>(request, createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getQuizAttemptId()).isEqualTo(secondAttemptId);
        assertThat(response.getBody().getStatus()).isEqualTo("IN_PROGRESS");
    }


    @Test
    @Order(4)
    void shouldRejectQuizAttemptForNonExistentQuiz() {
        QuizAttemptCreateInDTO request = QuizAttemptCreateInDTO.builder()
                .quizId(999999L) // Non-existent quiz
                .userId(1L)
                .build();


        HttpEntity<QuizAttemptCreateInDTO> entity = new HttpEntity<>(request, createAdminHeaders());


        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Quiz Not Found");
    }


    @Test
    @Order(5)
    void shouldRejectInvalidQuizAttemptData() {
        QuizAttemptCreateInDTO request = QuizAttemptCreateInDTO.builder()
                .quizId(null) // Invalid - null
                .userId(-1L) // Invalid - negative
                .build();


        HttpEntity<QuizAttemptCreateInDTO> entity = new HttpEntity<>(request, createAdminHeaders());


        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("quizId");
        assertThat(response.getBody()).containsKey("userId");
    }


    // ==================== GET QUIZ ATTEMPT TESTS ====================


    @Test
    @Order(6)
    void shouldGetQuizAttemptById() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + testAttemptId,
                HttpMethod.GET,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getQuizAttemptId()).isEqualTo(testAttemptId);
        assertThat(response.getBody().getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getBody().getScoreDetails()).isNotNull();
    }


    @Test
    @Order(7)
    void shouldReturn404ForNonExistingAttempt() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                String.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


//    @Test
//    @Order(8)
//    void shouldGetAllQuizAttemptsWithPagination() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//
//        ResponseEntity<Page<QuizAttemptOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "?page=0&size=10",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<Page<QuizAttemptOutDTO>>() {}
//        );
//        System.out.println("Response Status: " + response.getStatusCode());
//        System.out.println("Response Body: " + response.getBody());
//
//
//
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().getContent()).isNotEmpty();
//        assertThat(response.getBody().getContent().size()).isGreaterThanOrEqualTo(2);
//    }


    @Test
    @Order(9)
    void shouldGetQuizAttemptsByUserId() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<List<QuizAttemptOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/user/" + testUserId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<QuizAttemptOutDTO>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allMatch(attempt -> attempt.getUserId().equals(testUserId));
    }


    @Test
    @Order(10)
    void shouldGetQuizAttemptsByQuizId() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<List<QuizAttemptOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/quiz/" + testQuizId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<QuizAttemptOutDTO>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allMatch(attempt -> attempt.getQuizId().equals(testQuizId));
    }


    @Test
    @Order(11)
    void shouldGetQuizAttemptsByUserAndQuiz() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<List<QuizAttemptOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/user/" + testUserId + "/quiz/" + testQuizId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<QuizAttemptOutDTO>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allMatch(attempt ->
                attempt.getUserId().equals(testUserId) && attempt.getQuizId().equals(testQuizId));
    }


    @Test
    @Order(12)
    void shouldGetQuizAttemptsByStatus() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<List<QuizAttemptOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/status/COMPLETED",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<QuizAttemptOutDTO>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allMatch(attempt -> "COMPLETED".equals(attempt.getStatus()));
    }


    @Test
    @Order(13)
    void shouldGetLatestAttemptByUserAndQuiz() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/user/" + testUserId + "/quiz/" + testQuizId + "/latest",
                HttpMethod.GET,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUserId()).isEqualTo(testUserId);
        assertThat(response.getBody().getQuizId()).isEqualTo(testQuizId);
    }


    // ==================== UPDATE QUIZ ATTEMPT TESTS ====================


    @Test
    @Order(14)
    void shouldUpdateQuizAttemptSuccessfully() {
        QuizAttemptUpdateInDTO updateRequest = QuizAttemptUpdateInDTO.builder()
                .status("COMPLETED")
                .scoreDetails("{\"totalScore\":90,\"maxScore\":100}")
                .finishedAt(LocalDateTime.now())
                .build();


        HttpEntity<QuizAttemptUpdateInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondAttemptId,
                HttpMethod.PUT,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getBody().getScoreDetails()).contains("totalScore\":90");
        assertThat(response.getBody().getFinishedAt()).isNotNull();
        assertThat(response.getBody().getUpdatedAt()).isNotNull();
    }


    @Test
    @Order(15)
    void shouldRejectInvalidStatusUpdate() {
        QuizAttemptUpdateInDTO updateRequest = QuizAttemptUpdateInDTO.builder()
                .status("INVALID_STATUS")
                .build();


        HttpEntity<QuizAttemptUpdateInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());


        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + testAttemptId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("status");
    }


    @Test
    @Order(16)
    void shouldReturn404WhenUpdatingNonExistingAttempt() {
        QuizAttemptUpdateInDTO updateRequest = QuizAttemptUpdateInDTO.builder()
                .status("COMPLETED")
                .build();


        HttpEntity<QuizAttemptUpdateInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());


        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    // ==================== QUIZ ATTEMPT LIFECYCLE TESTS ====================


    @Test
    @Order(17)
    void shouldCompleteAttemptSuccessfully() {
        // Create a new IN_PROGRESS attempt for testing completion
        QuizAttempt newAttempt = QuizAttempt.builder()
                .attempt(2L)
                .quizId(testQuizId)
                .userId(testUserId)
                .startedAt(LocalDateTime.now().minusMinutes(10))
                .status("IN_PROGRESS")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .updatedAt(LocalDateTime.now().minusMinutes(10))
                .build();
        QuizAttempt savedAttempt = quizAttemptRepository.save(newAttempt);


        String scoreDetails = "{\"totalScore\":85,\"maxScore\":100}";
        HttpEntity<String> entity = new HttpEntity<>(scoreDetails, createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + savedAttempt.getQuizAttemptId() + "/complete",
                HttpMethod.PATCH,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getBody().getFinishedAt()).isNotNull();
        assertThat(response.getBody().getScoreDetails()).isEqualTo(scoreDetails);
    }


    @Test
    @Order(18)
    void shouldAbandonAttemptSuccessfully() {
        // Create a new IN_PROGRESS attempt for testing abandonment
        QuizAttempt newAttempt = QuizAttempt.builder()
                .attempt(1L)
                .quizId(testQuizId)
                .userId(4L)
                .startedAt(LocalDateTime.now().minusMinutes(5))
                .status("IN_PROGRESS")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .updatedAt(LocalDateTime.now().minusMinutes(5))
                .build();
        QuizAttempt savedAttempt = quizAttemptRepository.save(newAttempt);


        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + savedAttempt.getQuizAttemptId() + "/abandon",
                HttpMethod.PATCH,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("ABANDONED");
        assertThat(response.getBody().getFinishedAt()).isNotNull();
    }


    @Test
    @Order(19)
    void shouldTimeOutAttemptSuccessfully() {
        // Create a new IN_PROGRESS attempt for testing timeout
        QuizAttempt newAttempt = QuizAttempt.builder()
                .attempt(1L)
                .quizId(testQuizId)
                .userId(5L)
                .startedAt(LocalDateTime.now().minusHours(2))
                .status("IN_PROGRESS")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();
        QuizAttempt savedAttempt = quizAttemptRepository.save(newAttempt);


        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<QuizAttemptOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + savedAttempt.getQuizAttemptId() + "/timeout",
                HttpMethod.PATCH,
                entity,
                QuizAttemptOutDTO.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("TIMED_OUT");
        assertThat(response.getBody().getFinishedAt()).isNotNull();
    }


    // ==================== UTILITY ENDPOINT TESTS ====================


    @Test
    @Order(20)
    void shouldCheckQuizAttemptExists() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + testAttemptId,
                HttpMethod.HEAD,
                entity,
                Void.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    @Order(21)
    void shouldReturn404ForNonExistingAttemptCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.HEAD,
                entity,
                Void.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @Order(22)
    void shouldCountAttemptsByUserAndQuiz() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<Long> response = restTemplate.exchange(
                getBaseUrl() + "/user/" + testUserId + "/quiz/" + testQuizId + "/count",
                HttpMethod.GET,
                entity,
                Long.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isGreaterThanOrEqualTo(1L);
    }


    @Test
    @Order(23)
    void shouldGetHealthCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/health",
                HttpMethod.GET,
                entity,
                String.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("healthy");
    }


    @Test
    @Order(24)
    void shouldGetUserAttemptDetails() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<StandardResponseOutDTO<List<UserQuizAttemptDetailsOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/user/" + testUserId + "/quiz/course/" + testCourseId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<List<UserQuizAttemptDetailsOutDTO>>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("Fetched user attempt details");
    }


    @Test
    @Order(25)
    void shouldGetQuizAttemptDetailsByUserId() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<StandardResponseOutDTO<List<QuizAttemptDetailsByUserIDOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/quiz-attempt-details/" + testUserId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<List<QuizAttemptDetailsByUserIDOutDTO>>>() {
                }
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("User Attempt Details Fetched Successfully");
    }


    @Test
    @Order(26)
    void shouldGetQuizAttemptDetailsByCourseId() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<StandardResponseOutDTO<List<QuizAttemptDetailsByCourseIDOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/quiz-attempt-details/course/" + testCourseId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<List<QuizAttemptDetailsByCourseIDOutDTO>>>() {
                }
        );
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("User Attempt Details Fetched Successfully");
    }


    // ==================== DELETE QUIZ ATTEMPT TESTS ====================


    @Test
    @Order(27)
    void shouldDeleteQuizAttemptSuccessfully() {
        // Create a temporary attempt for deletion
        QuizAttempt tempAttempt = QuizAttempt.builder()
                .attempt(1L)
                .quizId(testQuizId)
                .userId(6L)
                .startedAt(LocalDateTime.now())
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        QuizAttempt savedAttempt = quizAttemptRepository.save(tempAttempt);


        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + savedAttempt.getQuizAttemptId(),
                HttpMethod.DELETE,
                entity,
                Void.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        // Verify deletion
        ResponseEntity<String> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + savedAttempt.getQuizAttemptId(),
                HttpMethod.GET,
                entity,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @Order(28)
    void shouldReturn404WhenDeletingNonExistingAttempt() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    // ==================== EDGE CASES AND ERROR HANDLING ====================


    @Test
    @Order(29)
    void shouldHandleInvalidPathVariable() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());


        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/invalid-id",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("must be of type");
    }


    // ==================== CLEAN UP ====================


    @Test
    @Order(30)
    void cleanUpTestData() {
        // Clean up quiz attempts
        quizAttemptRepository.deleteAll();


        // Clean up quizzes
        quizRepository.deleteAll();


        // Clean up courses
        courseRepository.deleteAll();


        // Verify cleanup
        assertThat(quizAttemptRepository.count()).isEqualTo(0);
        assertThat(quizRepository.count()).isEqualTo(0);
        assertThat(courseRepository.count()).isEqualTo(0);
    }
}

