package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuizControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseContentRepository courseContentRepository;

    private static Long createdQuizId;
    private static Long secondQuizId;
    private static Long thirdQuizId;
    private static Long testCourseId;
    private static Long testCourseContentId;
    private static Long secondCourseId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/quizzes";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ==================== SETUP TEST DATA ====================

    @Test
    @Order(1)
    void setupTestData() {
        // Create test courses directly using repository
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

        // Create second test course
        Course secondCourse = Course.builder()
                .title("Advanced Spring Course")
                .ownerId(2L)
                .description("Advanced Spring Framework")
                .level("ADVANCED")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedSecondCourse = courseRepository.save(secondCourse);
        secondCourseId = savedSecondCourse.getCourseId();

        // Create test course content (removed contentType field)
        CourseContent testCourseContent = CourseContent.builder()
                .courseId(testCourseId)
                .title("Introduction Module")
                .description("Module covering basic concepts")
                .resourceLink("https://example.com/intro")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CourseContent savedContent = courseContentRepository.save(testCourseContent);
        testCourseContentId = savedContent.getCourseContentId();

        // Create test quizzes directly using repository
        Quiz testQuiz1 = Quiz.builder()
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
        Quiz savedQuiz1 = quizRepository.save(testQuiz1);
        createdQuizId = savedQuiz1.getQuizId();

        Quiz testQuiz2 = Quiz.builder()
                .parentType("course")
                .parentId(testCourseId)
                .title("Java Advanced Quiz")
                .description("Advanced Java concepts")
                .timeLimit(90)
                .attemptsAllowed(2)
                .passingScore(new BigDecimal("80.00"))
                .randomizeQuestions(true)
                .showResults(false)
                .isActive(false)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Quiz savedQuiz2 = quizRepository.save(testQuiz2);
        secondQuizId = savedQuiz2.getQuizId();

        Quiz testQuiz3 = Quiz.builder()
                .parentType("course-content")
                .parentId(testCourseContentId)
                .title("Module Assessment Quiz")
                .description("Assessment for introduction module")
                .timeLimit(30)
                .attemptsAllowed(1)
                .passingScore(new BigDecimal("60.00"))
                .randomizeQuestions(false)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Quiz savedQuiz3 = quizRepository.save(testQuiz3);
        thirdQuizId = savedQuiz3.getQuizId();
    }

    // ==================== CREATE QUIZ TESTS ====================

    @Test
    @Order(2)
    void shouldCreateQuizSuccessfully() {
        QuizCreateInDTO request = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(testCourseId)
                .title("New Quiz Creation Test")
                .description("Testing quiz creation functionality")
                .timeLimit(120)
                .attemptsAllowed(5)
                .passingScore(new BigDecimal("75.00"))
                .randomizeQuestions(true)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .build();

        HttpEntity<QuizCreateInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTitle()).isEqualTo("New Quiz Creation Test");
        assertThat(response.getBody().getData().getParentType()).isEqualTo("course");
        assertThat(response.getBody().getData().getParentId()).isEqualTo(testCourseId);
        assertThat(response.getBody().getData().getTimeLimit()).isEqualTo(120);
        assertThat(response.getBody().getData().getAttemptsAllowed()).isEqualTo(5);
        assertThat(response.getBody().getData().getIsActive()).isTrue();
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();
    }

    @Test
    @Order(3)
    void shouldCreateCourseContentQuizSuccessfully() {
        QuizCreateInDTO request = QuizCreateInDTO.builder()
                .parentType("course-content")
                .parentId(testCourseContentId)
                .title("Content Quiz Test")
                .description("Quiz for course content")
                .timeLimit(45)
                .attemptsAllowed(2)
                .passingScore(new BigDecimal("65.00"))
                .randomizeQuestions(false)
                .showResults(false)
                .isActive(true)
                .createdBy(2)
                .build();

        HttpEntity<QuizCreateInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getParentType()).isEqualTo("course-content");
        assertThat(response.getBody().getData().getShowResults()).isFalse();
    }

    @Test
    @Order(4)
    void shouldRejectDuplicateQuizTitle() {
        QuizCreateInDTO request = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(testCourseId)
                .title("Java Basics Quiz") // Same title and parent as existing quiz
                .description("Another quiz with same title")
                .timeLimit(60)
                .attemptsAllowed(1)
                .isActive(true)
                .createdBy(1)
                .build();

        HttpEntity<QuizCreateInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("Quiz Exists");
    }

    @Test
    @Order(5)
    void shouldAllowSameTitleForDifferentParent() {
        QuizCreateInDTO request = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(secondCourseId) // Different parent
                .title("Java Basics Quiz") // Same title as existing quiz but different parent
                .description("Same title but different course")
                .timeLimit(60)
                .attemptsAllowed(1)
                .isActive(true)
                .createdBy(2)
                .build();

        HttpEntity<QuizCreateInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getParentId()).isEqualTo(secondCourseId);
    }

    @Test
    @Order(6)
    void shouldRejectInvalidQuizData() {
        QuizCreateInDTO request = QuizCreateInDTO.builder()
                .parentType("invalid-parent") // Invalid parent type
                .parentId(testCourseId)
                .title("AB") // Too short
                .attemptsAllowed(0) // Invalid - must be at least 1
                .isActive(true)
                .build();

        HttpEntity<QuizCreateInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("parentType");
        assertThat(response.getBody()).containsKey("attemptsAllowed");
    }

    @Test
    @Order(7)
    void shouldRejectQuizForNonExistentParent() {
        QuizCreateInDTO request = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(999999L) // Non-existent course
                .title("Valid Quiz Title")
                .attemptsAllowed(1)
                .isActive(true)
                .createdBy(1)
                .build();

        HttpEntity<QuizCreateInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Not Found");
    }

    // ==================== GET QUIZ TESTS ====================

    @Test
    @Order(8)
    void shouldGetAllActiveQuizzes() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        // Should only return active quizzes (secondQuiz is inactive)
        assertThat(response.getBody().getData()).allMatch(quiz -> quiz.getIsActive());
    }

    @Test
    @Order(9)
    void shouldGetQuizById() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdQuizId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getQuizId()).isEqualTo(createdQuizId);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Java Basics Quiz");
        assertThat(response.getBody().getData().getTimeLimit()).isEqualTo(60);
    }

    @Test
    @Order(10)
    void shouldReturn404ForNonExistingQuiz() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("No Quiz With ID");
    }

    @Test
    @Order(11)
    void shouldGetQuizzesByCourse() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/course/" + testCourseId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        // Should only return active quizzes for the course
        assertThat(response.getBody().getData()).allMatch(quiz ->
                quiz.getParentType().equals("course") &&
                        quiz.getParentId().equals(testCourseId) &&
                        quiz.getIsActive()
        );
    }

    @Test
    @Order(12)
    void shouldReturn404ForCourseWithNoActiveQuizzes() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/course/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("No Quiz Found for this CourseID");
    }

    @Test
    @Order(13)
    void shouldGetQuizzesByCourseContent() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/course-content/" + testCourseContentId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData()).allMatch(quiz ->
                quiz.getParentType().equals("course-content") &&
                        quiz.getParentId().equals(testCourseContentId) &&
                        quiz.getIsActive()
        );
    }

    @Test
    @Order(14)
    void shouldReturn404ForCourseContentWithNoActiveQuizzes() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/course-content/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("No Quiz Found for this Coursecontent");
    }

    // ==================== UPDATE QUIZ TESTS ====================

    @Test
    @Order(15)
    void shouldUpdateQuizSuccessfully() {
        QuizUpdateInDTO updateRequest = QuizUpdateInDTO.builder()
                .title("Updated Java Basics Quiz")
                .description("Updated description for Java basics")
                .timeLimit(90)
                .attemptsAllowed(5)
                .passingScore(new BigDecimal("75.00"))
                .randomizeQuestions(true)
                .showResults(false)
                .isActive(false)
                .build();

        HttpEntity<QuizUpdateInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdQuizId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Updated Java Basics Quiz");
        assertThat(response.getBody().getData().getTimeLimit()).isEqualTo(90);
        assertThat(response.getBody().getData().getAttemptsAllowed()).isEqualTo(5);
        assertThat(response.getBody().getData().getRandomizeQuestions()).isTrue();
        assertThat(response.getBody().getData().getShowResults()).isFalse();
        assertThat(response.getBody().getData().getIsActive()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(16)
    void shouldAllowUpdateWithSameTitle() {
        QuizUpdateInDTO updateRequest = QuizUpdateInDTO.builder()
                .title("Updated Java Basics Quiz") // Same title as current
                .description("Same title but different description")
                .timeLimit(100)
                .build();

        HttpEntity<QuizUpdateInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdQuizId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTimeLimit()).isEqualTo(100);
    }

    @Test
    @Order(17)
    void shouldReturn404WhenUpdatingNonExistingQuiz() {
        QuizUpdateInDTO updateRequest = QuizUpdateInDTO.builder()
                .title("Valid Update Title")
                .description("Valid description")
                .build();

        HttpEntity<QuizUpdateInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("No Quiz Found");
    }

    @Test
    @Order(18)
    void shouldValidateUpdateData() {
        QuizUpdateInDTO updateRequest = QuizUpdateInDTO.builder()
                .title("AB") // Too short
                .timeLimit(0) // Invalid
                .attemptsAllowed(11) // Exceeds maximum
                .build();

        HttpEntity<QuizUpdateInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdQuizId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    // ==================== DELETE QUIZ TESTS ====================

    @Test
    @Order(19)
    void shouldSoftDeleteQuizSuccessfully() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondQuizId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo("Quiz deleted successfully");
        assertThat(response.getBody().getMessage()).isEqualTo("Quiz deleted successfully");

        // Verify soft delete - quiz should still exist but be inactive
        Quiz deletedQuiz = quizRepository.findById(secondQuizId).orElse(null);
        assertThat(deletedQuiz).isNotNull();
        assertThat(deletedQuiz.getIsActive()).isFalse();
    }

    @Test
    @Order(20)
    void shouldReturn404WhenDeletingNonExistingQuiz() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("No Quiz Found");
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    @Order(21)
    void shouldHandleInvalidPathVariable() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/invalid-id",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("must be of type");
    }

    @Test
    @Order(22)
    void shouldHandleEmptyRequestBody() {
        HttpEntity<String> entity = new HttpEntity<>("{}", createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("parentType");
        assertThat(response.getBody()).containsKey("parentId");
        assertThat(response.getBody()).containsKey("title");
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(23)
    void cleanUpTestData() {
        // Clean up quizzes
        quizRepository.deleteAll();

        // Clean up course content
        courseContentRepository.deleteAll();

        // Clean up courses
        courseRepository.deleteAll();

        // Verify cleanup
        assertThat(quizRepository.count()).isEqualTo(0);
        assertThat(courseRepository.count()).isEqualTo(0);
        assertThat(courseContentRepository.count()).isEqualTo(0);
    }
}