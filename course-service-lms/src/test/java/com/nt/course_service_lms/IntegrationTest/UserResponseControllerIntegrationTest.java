//package com.nt.course_service_lms.IntegrationTest;
//
//import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
//import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
//import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
//import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
//import com.nt.course_service_lms.entity.UserResponse;
//import com.nt.course_service_lms.exception.ErrorResponse;
//import com.nt.course_service_lms.repository.UserResponseRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.data.domain.Page;
//import org.springframework.http.*;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * Integration tests for UserResponseController.
// * Tests all REST endpoints with proper authentication and authorization,
// * using TestRestTemplate to simulate real HTTP requests.
// */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class UserResponseControllerIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private UserResponseRepository userResponseRepository;
//
//    private static Long createdResponseId;
//    private static Long secondResponseId;
//    private static Long thirdResponseId;
//
//    private String getBaseUrl() {
//        return "http://localhost:" + port + "/api/service-api/v1/user-responses";
//    }
//
//    private HttpHeaders createAdminHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-Test-User", "test-admin");
//        headers.set("X-Test-Role", "ADMIN");
//        return headers;
//    }
//
//    private HttpHeaders createEmployeeHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-Test-User", "test-employee");
//        headers.set("X-Test-Role", "EMPLOYEE");
//        return headers;
//    }
//
//    private HttpHeaders createUserHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-Test-User", "test-user");
//        headers.set("X-Test-Role", "USER");
//        return headers;
//    }
//
//    // ==================== SETUP TEST DATA ====================
//
//    @Test
//    @Order(1)
//    void setupTestData() {
//        // Create test user responses directly using repository
//        UserResponse testResponse1 = UserResponse.builder()
//                .userId(1L)
//                .quizId(1L)
//                .questionId(1L)
//                .attempt(1L)
//                .userAnswer("{\"selectedOption\": \"A\"}")
//                .isCorrect(true)
//                .pointsEarned(BigDecimal.valueOf(5.0))
//                .answeredAt(LocalDateTime.now())
//                .build();
//        UserResponse savedResponse1 = userResponseRepository.save(testResponse1);
//        createdResponseId = savedResponse1.getResponseId();
//
//        UserResponse testResponse2 = UserResponse.builder()
//                .userId(1L)
//                .quizId(1L)
//                .questionId(2L)
//                .attempt(1L)
//                .userAnswer("{\"selectedOption\": \"B\"}")
//                .isCorrect(false)
//                .pointsEarned(BigDecimal.valueOf(0.0))
//                .answeredAt(LocalDateTime.now())
//                .build();
//        UserResponse savedResponse2 = userResponseRepository.save(testResponse2);
//        secondResponseId = savedResponse2.getResponseId();
//
//        UserResponse testResponse3 = UserResponse.builder()
//                .userId(2L)
//                .quizId(2L)
//                .questionId(3L)
//                .attempt(1L)
//                .userAnswer("{\"textAnswer\": \"Sample answer\"}")
//                .isCorrect(true)
//                .pointsEarned(BigDecimal.valueOf(3.5))
//                .answeredAt(LocalDateTime.now())
//                .build();
//        UserResponse savedResponse3 = userResponseRepository.save(testResponse3);
//        thirdResponseId = savedResponse3.getResponseId();
//
//        System.out.println("Setup completed. Created response IDs: " + createdResponseId + ", " + secondResponseId + ", " + thirdResponseId);
//    }
//
//    // ==================== CREATE USER RESPONSE TESTS ====================
//
//    @Test
//    @Order(2)
//    void shouldCreateUserResponsesSuccessfully() {
//        List<UserResponseInDTO> userResponses = Arrays.asList(
//                UserResponseInDTO.builder()
//                        .userId(3L)
//                        .quizId(3L)
//                        .questionId(1L)
//                        .attempt(1L)
//                        .userAnswer("{\"selectedOption\": \"C\"}")
//                        .answeredAt(LocalDateTime.now())
//                        .build(),
//                UserResponseInDTO.builder()
//                        .userId(3L)
//                        .quizId(3L)
//                        .questionId(2L)
//                        .attempt(1L)
//                        .userAnswer("{\"selectedOption\": \"D\"}")
//                        .answeredAt(LocalDateTime.now())
//                        .build()
//        );
//
//        HttpEntity<List<UserResponseInDTO>> entity = new HttpEntity<>(userResponses, createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getMessage()).contains("Successfully created 2 user responses");
//        assertThat(response.getBody().getData()).hasSize(2);
//    }
//
//    @Test
//    @Order(3)
//    void shouldRejectEmptyUserResponseList() {
//        List<UserResponseInDTO> emptyList = Arrays.asList();
//        HttpEntity<List<UserResponseInDTO>> entity = new HttpEntity<>(emptyList, createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                StandardResponseOutDTO.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("ERROR");
//        assertThat(response.getBody().getMessage()).isEqualTo("User response list cannot be empty");
//    }
//
//    @Test
//    @Order(4)
//    void shouldRejectInvalidUserResponseData() {
//        List<UserResponseInDTO> invalidResponses = Arrays.asList(
//                UserResponseInDTO.builder()
//                        .userId(null) // Invalid: null userId
//                        .quizId(1L)
//                        .questionId(1L)
//                        .attempt(1L)
//                        .userAnswer("{\"selectedOption\": \"A\"}")
//                        .build()
//        );
//
//        HttpEntity<List<UserResponseInDTO>> entity = new HttpEntity<>(invalidResponses, createAdminHeaders());
//
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<Map<String, String>>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).containsKey("userId");
//    }
//
//    @Test
//    @Order(5)
//    void shouldRejectBlankUserAnswer() {
//        List<UserResponseInDTO> invalidResponses = Arrays.asList(
//                UserResponseInDTO.builder()
//                        .userId(1L)
//                        .quizId(1L)
//                        .questionId(1L)
//                        .attempt(1L)
//                        .userAnswer("") // Invalid: blank answer
//                        .build()
//        );
//
//        HttpEntity<List<UserResponseInDTO>> entity = new HttpEntity<>(invalidResponses, createAdminHeaders());
//
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<Map<String, String>>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).containsKey("userAnswer");
//    }
//
//    @Test
//    @Order(6)
//    void shouldDenyUserResponseCreationForNonAuthorizedUser() {
//        List<UserResponseInDTO> userResponses = Arrays.asList(
//                UserResponseInDTO.builder()
//                        .userId(1L)
//                        .quizId(1L)
//                        .questionId(1L)
//                        .attempt(1L)
//                        .userAnswer("{\"selectedOption\": \"A\"}")
//                        .build()
//        );
//
//        HttpEntity<List<UserResponseInDTO>> entity = new HttpEntity<>(userResponses, createUserHeaders());
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    // ==================== GET USER RESPONSE BY ID TESTS ====================
//
//    @Test
//    @Order(7)
//    void shouldGetUserResponseByIdAsAdmin() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<UserResponseOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData().getResponseId()).isEqualTo(createdResponseId);
//        assertThat(response.getBody().getData().getUserId()).isEqualTo(1L);
//    }
//
//    @Test
//    @Order(8)
//    void shouldGetUserResponseByIdAsEmployee() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<UserResponseOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().getData().getResponseId()).isEqualTo(createdResponseId);
//    }
//
//    @Test
//    @Order(9)
//    void shouldReturn404ForNonExistingResponseId() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.GET,
//                entity,
//                ErrorResponse.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(10)
//    void shouldDenyGetUserResponseForUnauthorizedUser() {
//        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    // ==================== UPDATE USER RESPONSE TESTS ====================
//
//    @Test
//    @Order(11)
//    void shouldUpdateUserResponseAsAdmin() {
//        UserResponseUpdateInDTO updateDTO = UserResponseUpdateInDTO.builder()
//                .userAnswer("{\"selectedOption\": \"D\"}")
//                .isCorrect(false)
//                .pointsEarned(BigDecimal.valueOf(0.0))
//                .answeredAt(LocalDateTime.now())
//                .build();
//
//        HttpEntity<UserResponseUpdateInDTO> entity = new HttpEntity<>(updateDTO, createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<UserResponseOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.PUT,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData().getUserAnswer()).isEqualTo("{\"selectedOption\": \"D\"}");
//        assertThat(response.getBody().getData().getIsCorrect()).isFalse();
//    }
//
//    @Test
//    @Order(12)
//    void shouldRejectUpdateWithInvalidData() {
//        UserResponseUpdateInDTO invalidUpdate = UserResponseUpdateInDTO.builder()
//                .userAnswer("") // Invalid: blank answer
//                .isCorrect(true)
//                .pointsEarned(BigDecimal.valueOf(5.0))
//                .build();
//
//        HttpEntity<UserResponseUpdateInDTO> entity = new HttpEntity<>(invalidUpdate, createAdminHeaders());
//
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.PUT,
//                entity,
//                new ParameterizedTypeReference<Map<String, String>>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).containsKey("userAnswer");
//    }
//
//    @Test
//    @Order(13)
//    void shouldReturn404WhenUpdatingNonExistingResponse() {
//        UserResponseUpdateInDTO updateDTO = UserResponseUpdateInDTO.builder()
//                .userAnswer("{\"selectedOption\": \"A\"}")
//                .isCorrect(true)
//                .pointsEarned(BigDecimal.valueOf(5.0))
//                .build();
//
//        HttpEntity<UserResponseUpdateInDTO> entity = new HttpEntity<>(updateDTO, createAdminHeaders());
//
//        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.PUT,
//                entity,
//                ErrorResponse.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(14)
//    void shouldDenyUpdateForNonAdmin() {
//        UserResponseUpdateInDTO updateDTO = UserResponseUpdateInDTO.builder()
//                .userAnswer("{\"selectedOption\": \"A\"}")
//                .isCorrect(true)
//                .pointsEarned(BigDecimal.valueOf(5.0))
//                .build();
//
//        HttpEntity<UserResponseUpdateInDTO> entity = new HttpEntity<>(updateDTO, createEmployeeHeaders());
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.PUT,
//                entity,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    // ==================== DELETE USER RESPONSE TESTS ====================
//
//    @Test
//    @Order(15)
//    void shouldDeleteUserResponseAsAdmin() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + secondResponseId,
//                HttpMethod.DELETE,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//
//        // Verify deletion
//        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
//                getBaseUrl() + "/" + secondResponseId,
//                HttpMethod.GET,
//                entity,
//                ErrorResponse.class
//        );
//        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(16)
//    void shouldReturn404WhenDeletingNonExistingResponse() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.DELETE,
//                entity,
//                ErrorResponse.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(17)
//    void shouldDenyDeleteForNonAdmin() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.DELETE,
//                entity,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    // ==================== GET ALL USER RESPONSES TESTS ====================
//
//    @Test
//    @Order(18)
//    void shouldGetAllUserResponsesAsAdmin() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "?page=0&size=10",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData().getContent()).isNotEmpty();
//    }
//
//    @Test
//    @Order(19)
//    void shouldDenyGetAllUserResponsesForNonAdmin() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "?page=0&size=10",
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    // ==================== GET USER RESPONSES BY USER ID TESTS ====================
//
//    @Test
//    @Order(20)
//    void shouldGetUserResponsesByUserIdAsEmployee() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotEmpty();
//        assertThat(response.getBody().getData().get(0).getUserId()).isEqualTo(1L);
//    }
//
//    @Test
//    @Order(21)
//    void shouldGetUserResponsesByUserIdPaginated() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1/paginated?page=0&size=5",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//    }
//
//    // ==================== GET USER RESPONSES BY QUIZ ID TESTS ====================
//
//    @Test
//    @Order(22)
//    void shouldGetUserResponsesByQuizId() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/quiz/1",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotEmpty();
//    }
//
//    @Test
//    @Order(23)
//    void shouldGetUserResponsesByQuizIdPaginated() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/quiz/1/paginated?page=0&size=5",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//    }
//
//    // ==================== GET USER RESPONSES BY USER AND QUIZ TESTS ====================
//
//    @Test
//    @Order(24)
//    void shouldGetUserResponsesByUserIdAndQuizId() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1/quiz/1",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotEmpty();
//    }
//
//    @Test
//    @Order(25)
//    void shouldGetUserResponsesByUserIdQuizIdAndAttempt() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1/quiz/1/attempt/1",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotEmpty();
//    }
//
//    // ==================== SCORING AND STATISTICS TESTS ====================
//
//    @Test
//    @Order(26)
//    void shouldGetTotalScore() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<BigDecimal>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1/quiz/1/attempt/1/total-score",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
//    }
//
//    @Test
//    @Order(27)
//    void shouldCountCorrectAnswers() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1/quiz/1/attempt/1/correct-count",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(0L);
//    }
//
//    @Test
//    @Order(28)
//    void shouldGetMaxAttemptNumber() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/1/quiz/1/max-attempt",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(1L);
//    }
//
//    // ==================== EDGE CASE TESTS ====================
//
//    @Test
//    @Order(29)
//    void shouldHandleInvalidUserIdInPath() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/user/invalid/quiz/1",
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//    }
//
//    @Test
//    @Order(30)
//    void shouldHandleNegativeUserIdInPath() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
//                getBaseUrl() + "/user/-1/quiz/1",
//                HttpMethod.GET,
//                entity,
//                ErrorResponse.class
//        );
//
//        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(31)
//    void shouldReturnEmptyListForNonExistingUserResponses() {
//        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl() + "/user/999999",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEmpty();
//    }
//
//    @Test
//    @Order(32)
//    void shouldValidatePointsEarnedRange() {
//        UserResponseUpdateInDTO invalidUpdate = UserResponseUpdateInDTO.builder()
//                .userAnswer("{\"selectedOption\": \"A\"}")
//                .isCorrect(true)
//                .pointsEarned(BigDecimal.valueOf(1000.0)) // Exceeds max value
//                .build();
//
//        HttpEntity<UserResponseUpdateInDTO> entity = new HttpEntity<>(invalidUpdate, createAdminHeaders());
//
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.PUT,
//                entity,
//                new ParameterizedTypeReference<Map<String, String>>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).containsKey("pointsEarned");
//    }
//
//    @Test
//    @Order(33)
//    void shouldValidateNegativePointsEarned() {
//        UserResponseUpdateInDTO invalidUpdate = UserResponseUpdateInDTO.builder()
//                .userAnswer("{\"selectedOption\": \"A\"}")
//                .isCorrect(false)
//                .pointsEarned(BigDecimal.valueOf(-1.0)) // Negative value
//                .build();
//
//        HttpEntity<UserResponseUpdateInDTO> entity = new HttpEntity<>(invalidUpdate, createAdminHeaders());
//
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdResponseId,
//                HttpMethod.PUT,
//                entity,
//                new ParameterizedTypeReference<Map<String, String>>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).containsKey("pointsEarned");
//    }
//
//    // ==================== CLEAN UP ====================
//
//    @Test
//    @Order(34)
//    void cleanUpTestData() {
//        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());
//
//        // Clean up remaining user responses
//        if (createdResponseId != null) {
//            restTemplate.exchange(
//                    getBaseUrl() + "/" + createdResponseId,
//                    HttpMethod.DELETE,
//                    entity,
//                    new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
//            );
//        }
//
//        if (thirdResponseId != null) {
//            restTemplate.exchange(
//                    getBaseUrl() + "/" + thirdResponseId,
//                    HttpMethod.DELETE,
//                    entity,
//                    new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
//            );
//        }
//
//        // Clean up any additional responses created during tests
//        ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> allResponses = restTemplate.exchange(
//                getBaseUrl() + "?page=0&size=100",
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        if (allResponses.getStatusCode() == HttpStatus.OK &&
//                !allResponses.getBody().getData().getContent().isEmpty()) {
//            allResponses.getBody().getData().getContent().stream()
//                    .filter(response -> response.getUserId().equals(3L)) // Clean up test user 3 responses
//                    .forEach(response -> {
//                        restTemplate.exchange(
//                                getBaseUrl() + "/" + response.getResponseId(),
//                                HttpMethod.DELETE,
//                                entity,
//                                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
//                        );
//                    });
//        }
//
//        System.out.println("Cleanup completed successfully");
//    }
//}