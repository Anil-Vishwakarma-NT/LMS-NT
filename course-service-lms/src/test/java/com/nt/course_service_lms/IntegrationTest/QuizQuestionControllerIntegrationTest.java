package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
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
class QuizQuestionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static Long testQuizId;
    private static Long testQuestionId;
    private static Long secondQuestionId;
    private static Long thirdQuestionId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/quiz-questions";
    }

    private HttpHeaders createAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-admin");
        headers.set("X-Test-Role", "ADMIN");
        return headers;
    }

    private HttpHeaders createEmployeeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-employee");
        headers.set("X-Test-Role", "EMPLOYEE");
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

        // Create test quiz
        Quiz testQuiz = Quiz.builder()
                .parentType("course")
                .parentId(savedCourse.getCourseId())
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

        // Create test quiz questions with proper JSON format
        QuizQuestion question1 = QuizQuestion.builder()
                .quizId(testQuizId)
                .questionText("What is the correct syntax for declaring a variable in Java?")
                .questionType("MCQ_SINGLE")
                .options("[\"int x;\", \"var x;\", \"x int;\", \"declare x;\"]")
                .correctAnswer("[\"int x;\"]")
                .points(new BigDecimal("5.00"))
                .explanation("The correct syntax is 'int x;' to declare an integer variable.")
                .required(true)
                .position(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        QuizQuestion savedQuestion1 = quizQuestionRepository.save(question1);
        testQuestionId = savedQuestion1.getQuestionId();

        QuizQuestion question2 = QuizQuestion.builder()
                .quizId(testQuizId)
                .questionText("Which of the following are valid Java keywords?")
                .questionType("MCQ_MULTIPLE")
                .options("[\"class\", \"interface\", \"variable\", \"abstract\"]")
                .correctAnswer("[\"class\", \"interface\", \"abstract\"]")
                .points(new BigDecimal("7.50"))
                .explanation("class, interface, and abstract are Java keywords.")
                .required(true)
                .position(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        QuizQuestion savedQuestion2 = quizQuestionRepository.save(question2);
        secondQuestionId = savedQuestion2.getQuestionId();

        QuizQuestion question3 = QuizQuestion.builder()
                .quizId(testQuizId)
                .questionText("Explain the difference between String and StringBuilder.")
                .questionType("SHORT_ANSWER")
                .options(null)
                .correctAnswer("\"String is immutable while StringBuilder is mutable\"")
                .points(new BigDecimal("10.00"))
                .explanation("String objects cannot be modified after creation, StringBuilder allows modification.")
                .required(false)
                .position(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        QuizQuestion savedQuestion3 = quizQuestionRepository.save(question3);
        thirdQuestionId = savedQuestion3.getQuestionId();
    }

    // ==================== CREATE QUIZ QUESTION TESTS ====================

    @Test
    @Order(2)
    void shouldCreateQuizQuestionSuccessfully() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(testQuizId)
                .questionText("What is polymorphism in Java?")
                .questionType("SHORT_ANSWER")
                .options(null)
                .correctAnswer("\"Polymorphism allows objects to take multiple forms\"")
                .points(new BigDecimal("8.00"))
                .explanation("Polymorphism is achieved through method overriding and overloading")
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getQuestionText()).isEqualTo("What is polymorphism in Java?");
        assertThat(response.getBody().getData().getQuestionType()).isEqualTo("SHORT_ANSWER");
        assertThat(response.getBody().getData().getPosition()).isEqualTo(4); // Auto-assigned next position
        assertThat(response.getBody().getData().getQuizId()).isEqualTo(testQuizId);
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();
    }

    @Test
    @Order(3)
    void shouldCreateMCQQuestionWithOptions() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(testQuizId)
                .questionText("Which access modifier provides the most restricted access?")
                .questionType("MCQ_SINGLE")
                .options("[\"public\", \"protected\", \"private\", \"default\"]")
                .correctAnswer("[\"private\"]")
                .points(new BigDecimal("3.00"))
                .explanation("Private provides the most restricted access")
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getOptions()).isNotNull();
        assertThat(response.getBody().getData().getPosition()).isEqualTo(5);
    }

    @Test
    @Order(4)
    void shouldRejectQuestionForNonExistentQuiz() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(999999L) // Non-existent quiz
                .questionText("Valid question text")
                .questionType("SHORT_ANSWER")
                .correctAnswer("\"Valid answer\"")
                .points(new BigDecimal("5.00"))
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Quiz not found");
    }

    @Test
    @Order(5)
    void shouldRejectMCQQuestionWithoutOptions() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(testQuizId)
                .questionText("What is the output?")
                .questionType("MCQ_SINGLE")
                .options("") // Empty string instead of null to pass DTO validation
                .correctAnswer("[\"A\"]")
                .points(new BigDecimal("5.00"))
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Options are required for multiple choice questions");
    }

    @Test
    @Order(6)
    void shouldRejectInvalidQuestionData() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(testQuizId)
                .questionText("") // Invalid - blank
                .questionType("INVALID_TYPE") // Invalid type
                .correctAnswer("") // Invalid - blank
                .points(new BigDecimal("-1.00")) // Invalid - negative
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("questionText");
        assertThat(response.getBody()).containsKey("questionType");
        assertThat(response.getBody()).containsKey("correctAnswer");
        assertThat(response.getBody()).containsKey("points");
    }

    // ==================== GET QUIZ QUESTION TESTS ====================

    @Test
    @Order(7)
    void shouldGetQuestionById() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + testQuestionId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getQuestionId()).isEqualTo(testQuestionId);
        assertThat(response.getBody().getData().getQuestionText()).contains("syntax for declaring");
        assertThat(response.getBody().getData().getPosition()).isEqualTo(1);
    }

    @Test
    @Order(8)
    void shouldReturn404ForNonExistingQuestion() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Question not found");
    }

    @Test
    @Order(9)
    void shouldGetQuestionsByQuizId() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/quiz/" + testQuizId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).hasSize(5); // 3 setup + 2 created in tests
        // Verify they are ordered by position
        List<QuizQuestionOutDTO> questions = response.getBody().getData();
        for (int i = 0; i < questions.size() - 1; i++) {
            assertThat(questions.get(i).getPosition()).isLessThan(questions.get(i + 1).getPosition());
        }
    }

    @Test
    @Order(10)
    void shouldReturn404ForQuizWithNoQuestions() {
        // Create a quiz without questions
        Quiz emptyQuiz = Quiz.builder()
                .parentType("course")
                .parentId(1L)
                .title("Empty Quiz")
                .description("Quiz with no questions")
                .timeLimit(30)
                .attemptsAllowed(1)
                .passingScore(new BigDecimal("50.00"))
                .randomizeQuestions(false)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Quiz savedEmptyQuiz = quizRepository.save(emptyQuiz);

        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/quiz/" + savedEmptyQuiz.getQuizId(),
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("No Questions Found");
    }

    // ==================== UPDATE QUIZ QUESTION TESTS ====================

    @Test
    @Order(11)
    void shouldUpdateQuestionSuccessfully() {
        UpdateQuizQuestionInDTO updateRequest = UpdateQuizQuestionInDTO.builder()
                .questionText("Updated: What is the correct syntax for declaring a variable in Java?")
                .questionType("MCQ_SINGLE")
                .options("[\"int x;\", \"var x;\", \"x int;\", \"declare x;\", \"Integer x;\"]")
                .correctAnswer("[\"int x;\"]")
                .points(new BigDecimal("6.00"))
                .explanation("Updated explanation: The correct syntax is 'int x;' to declare an integer variable.")
                .required(false)
                .position(1) // Keep same position
                .build();

        HttpEntity<UpdateQuizQuestionInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + testQuestionId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getQuestionText()).startsWith("Updated:");
        assertThat(response.getBody().getData().getPoints()).isEqualTo(new BigDecimal("6.00"));
        assertThat(response.getBody().getData().getRequired()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(12)
    void shouldUpdateQuestionPosition() {
        UpdateQuizQuestionInDTO updateRequest = UpdateQuizQuestionInDTO.builder()
                .questionText("Which of the following are valid Java keywords?")
                .questionType("MCQ_MULTIPLE")
                .options("[\"class\", \"interface\", \"variable\", \"abstract\"]")
                .correctAnswer("[\"class\", \"interface\", \"abstract\"]")
                .points(new BigDecimal("7.50"))
                .explanation("class, interface, and abstract are Java keywords.")
                .required(true)
                .position(5) // Move from position 2 to position 5
                .build();

        HttpEntity<UpdateQuizQuestionInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondQuestionId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getPosition()).isEqualTo(5);

        // Verify other questions were reordered
        HttpEntity<Void> getEntity = new HttpEntity<>(createAdminHeaders());
        ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> getResponse = restTemplate.exchange(
                getBaseUrl() + "/quiz/" + testQuizId,
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        List<QuizQuestionOutDTO> questions = getResponse.getBody().getData();
        // Verify positions are sequential
        for (int i = 0; i < questions.size() - 1; i++) {
            assertThat(questions.get(i).getPosition()).isLessThan(questions.get(i + 1).getPosition());
        }
    }

    @Test
    @Order(13)
    void shouldRejectInvalidPositionUpdate() {
        UpdateQuizQuestionInDTO updateRequest = UpdateQuizQuestionInDTO.builder()
                .questionText("Valid question text")
                .questionType("SHORT_ANSWER")
                .correctAnswer("\"Valid answer\"")
                .points(new BigDecimal("5.00"))
                .required(true)
                .position(100) // Invalid position - too high
                .build();

        HttpEntity<UpdateQuizQuestionInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + testQuestionId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Position must be between");
    }

    @Test
    @Order(14)
    void shouldReturn404WhenUpdatingNonExistingQuestion() {
        UpdateQuizQuestionInDTO updateRequest = UpdateQuizQuestionInDTO.builder()
                .questionText("Valid question text")
                .questionType("SHORT_ANSWER")
                .correctAnswer("\"Valid answer\"")
                .points(new BigDecimal("5.00"))
                .required(true)
                .position(1)
                .build();

        HttpEntity<UpdateQuizQuestionInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Question not found");
    }

    // ==================== DELETE QUIZ QUESTION TESTS ====================

    @Test
    @Order(15)
    void shouldDeleteQuestionSuccessfully() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + thirdQuestionId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("Question deleted successfully");

        // Verify question is deleted
        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + thirdQuestionId,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Verify remaining questions were reordered
        ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> listResponse = restTemplate.exchange(
                getBaseUrl() + "/quiz/" + testQuizId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        List<QuizQuestionOutDTO> remainingQuestions = listResponse.getBody().getData();
        // Verify positions are still sequential
        for (int i = 0; i < remainingQuestions.size() - 1; i++) {
            assertThat(remainingQuestions.get(i).getPosition()).isLessThan(remainingQuestions.get(i + 1).getPosition());
        }
    }

    @Test
    @Order(16)
    void shouldReturn404WhenDeletingNonExistingQuestion() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Question not found");
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    @Order(17)
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

    @Test
    @Order(18)
    void shouldHandleInvalidJSONInOptions() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(testQuizId)
                .questionText("What is the output?")
                .questionType("MCQ_SINGLE")
                .options("invalid json") // Invalid JSON
                .correctAnswer("[\"A\"]")
                .points(new BigDecimal("5.00"))
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invalid JSON format");
    }

    @Test
    @Order(19)
    void shouldHandleInvalidJSONInCorrectAnswer() {
        QuizQuestionInDTO request = QuizQuestionInDTO.builder()
                .quizId(testQuizId)
                .questionText("What is polymorphism?")
                .questionType("SHORT_ANSWER")
                .options(null)
                .correctAnswer("invalid json without quotes") // Invalid JSON
                .points(new BigDecimal("5.00"))
                .required(true)
                .build();

        HttpEntity<QuizQuestionInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invalid JSON format");
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(20)
    void cleanUpTestData() {
        // Clean up quiz questions
        quizQuestionRepository.deleteAll();

        // Clean up quizzes
        quizRepository.deleteAll();

        // Clean up courses
        courseRepository.deleteAll();

        // Verify cleanup
        assertThat(quizQuestionRepository.count()).isEqualTo(0);
        assertThat(quizRepository.count()).isEqualTo(0);
        assertThat(courseRepository.count()).isEqualTo(0);
    }
}