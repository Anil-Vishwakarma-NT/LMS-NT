package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.CourseRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    private static Long createdCourseId;
    private static Long secondCourseId;
    private static Long thirdCourseId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/course";
    }

    private HttpHeaders createAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-admin");
        headers.set("X-Test-Role", "ADMIN");
        return headers;
    }

    private HttpHeaders createUserHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-user");
        headers.set("X-Test-Role", "USER");
        return headers;
    }

    // ==================== SETUP TEST DATA ====================

    @Test
    @Order(1)
    void setupTestData() {
        // Create test courses directly using repository
        Course testCourse1 = Course.builder()
                .title("Introduction To Spring Boot")
                .ownerId(1L)
                .description("Comprehensive Guide To Spring Boot Framework")
                .level("BEGINNER")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse1 = courseRepository.save(testCourse1);
        createdCourseId = savedCourse1.getCourseId();

        Course testCourse2 = Course.builder()
                .title("Advanced Java Programming")
                .ownerId(2L)
                .description("Deep Dive Into Advanced Java Concepts")
                .level("ADVANCED")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse2 = courseRepository.save(testCourse2);
        secondCourseId = savedCourse2.getCourseId();

        Course testCourse3 = Course.builder()
                .title("Introduction To Spring Boot")  // Same title, different owner
                .ownerId(3L)
                .description("Spring Boot Course By Different Owner")
                .level("BEGINNER")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse3 = courseRepository.save(testCourse3);
        thirdCourseId = savedCourse3.getCourseId();
    }

    // ==================== CREATE COURSE TESTS ====================

    @Test
    @Order(2)
    void shouldCreateCourseSuccessfully() {
        CourseInDTO request = CourseInDTO.builder()
                .title("New Course Creation Test")
                .ownerId(4L)
                .description("Testing course creation functionality")
                .courseLevel("INTERMEDIATE")
                .Active(true)
                .build();

        HttpEntity<CourseInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTitle()).isEqualTo("New Course Creation Test");
        assertThat(response.getBody().getData().getOwnerId()).isEqualTo(4L);
        assertThat(response.getBody().getData().isActive()).isTrue();
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();
    }

    @Test
    @Order(3)
    void shouldRejectDuplicateCourseTitle() {
        CourseInDTO request = CourseInDTO.builder()
                .title("Introduction To Spring Boot") // Same title and owner as setup course
                .ownerId(1L)
                .description("Another Spring Boot course")
                .courseLevel("INTERMEDIATE")
                .Active(true)
                .build();

        HttpEntity<CourseInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("already exists");
    }

    @Test
    @Order(4)
    void shouldAllowSameTitleForDifferentOwner() {
        CourseInDTO request = CourseInDTO.builder()
                .title("Advanced Java Programming") // Same title as setup course but different owner
                .ownerId(5L)
                .description("Java course by different owner")
                .courseLevel("ADVANCED")
                .Active(true)
                .build();

        HttpEntity<CourseInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getOwnerId()).isEqualTo(5L);
    }

    @Test
    @Order(5)
    void shouldRejectInvalidCourseData() {
        CourseInDTO request = CourseInDTO.builder()
                .title("AB") // Too short (min 3 characters)
                .ownerId(1L)
                .description("Valid description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        HttpEntity<CourseInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("title");
    }

    @Test
    @Order(6)
    void shouldRejectNullOwnerId() {
        CourseInDTO request = CourseInDTO.builder()
                .title("Valid Title")
                .ownerId(null) // Null owner ID
                .description("Valid description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        HttpEntity<CourseInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("ownerId");
    }

    @Test
    @Order(7)
    void shouldDenyCourseCreationForNonAdmin() {
        CourseInDTO request = CourseInDTO.builder()
                .title("Unauthorized Course")
                .ownerId(1L)
                .description("Valid description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        HttpEntity<CourseInDTO> entity = new HttpEntity<>(request, createUserHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== GET COURSE TESTS ====================

    @Test
    @Order(8)
    void shouldGetAllCoursesAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @Order(9)
    void shouldDenyGetAllCoursesForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @Order(10)
    void shouldGetCourseById() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseInfoOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(createdCourseId);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Introduction To Spring Boot");
    }

    @Test
    @Order(11)
    void shouldReturn404ForNonExistingCourse() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(12)
    void shouldCheckCourseExistence() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<Boolean> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId + "/exists",
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }

    @Test
    @Order(13)
    void shouldReturnFalseForNonExistingCourseCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<Boolean> response = restTemplate.exchange(
                getBaseUrl() + "/999999/exists",
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    @Order(14)
    void shouldGetCourseNameById() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId + "/name",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Introduction To Spring Boot");
    }

    // ==================== UPDATE COURSE TESTS ====================

    @Test
    @Order(15)
    void shouldUpdateCourseSuccessfully() {
        UpdateCourseInDTO updateRequest = UpdateCourseInDTO.builder()
                .title("Updated Spring Boot Course")
                .ownerId(1L)
                .description("Updated comprehensive guide to Spring Boot")
                .courseLevel("INTERMEDIATE")
                .Active(false)
                .build();

        HttpEntity<UpdateCourseInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Updated Spring Boot Course");
        assertThat(response.getBody().getData().getLevel()).isEqualTo("INTERMEDIATE");
        assertThat(response.getBody().getData().isActive()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(16)
    void shouldAllowUpdateWithSameTitle() {
        UpdateCourseInDTO updateRequest = UpdateCourseInDTO.builder()
                .title("Updated Spring Boot Course") // Same title as current
                .ownerId(1L)
                .description("Same title but different description")
                .courseLevel("ADVANCED")
                .Active(true)
                .build();

        HttpEntity<UpdateCourseInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().isActive()).isTrue();
    }

    @Test
    @Order(17)
    void shouldRejectUpdateWithDuplicateTitle() {
        // Ensure we have a valid course ID
        assertThat(createdCourseId).isNotNull();

        UpdateCourseInDTO updateRequest = UpdateCourseInDTO.builder()
                .title("Advanced Java Programming") // Title exists for owner 2
                .ownerId(2L) // Same owner as the existing course
                .description("Trying to create duplicate")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        HttpEntity<UpdateCourseInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        // Based on your GlobalExceptionHandler, ResourceNotValidException maps to UNAUTHORIZED
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).containsAnyOf("duplicate", "already exists", "owner");
    }

    @Test
    @Order(18)
    void shouldReturn404WhenUpdatingNonExistingCourse() {
        UpdateCourseInDTO updateRequest = UpdateCourseInDTO.builder()
                .title("Valid Title")
                .ownerId(1L)
                .description("Valid description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        HttpEntity<UpdateCourseInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(19)
    void shouldDenyUpdateForNonAdmin() {
        UpdateCourseInDTO updateRequest = UpdateCourseInDTO.builder()
                .title("Unauthorized Update")
                .ownerId(1L)
                .description("Valid description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        HttpEntity<UpdateCourseInDTO> entity = new HttpEntity<>(updateRequest, createUserHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== UTILITY ENDPOINT TESTS ====================

    @Test
    @Order(20)
    void shouldGetCourseCountAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/count",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(3L);
    }

    @Test
    @Order(21)
    void shouldGetRecentCoursesAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseSummaryOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/recent",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isLessThanOrEqualTo(5);
    }

    @Test
    @Order(22)
    void shouldGetCoursesInfoAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/info",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
    }

    @Test
    @Order(23)
    void shouldGetExistingCourseIds() {
        List<Long> testIds = Arrays.asList(createdCourseId, secondCourseId, 999999L);
        HttpEntity<List<Long>> entity = new HttpEntity<>(testIds, createUserHeaders());

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/existing-ids",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<Long>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(createdCourseId, secondCourseId);
        assertThat(response.getBody()).doesNotContain(999999L);
    }

    @Test
    @Order(24)
    void shouldReturn404WhenNoExistingIdsFound() {
        List<Long> nonExistentIds = Arrays.asList(999998L, 999999L);
        HttpEntity<List<Long>> entity = new HttpEntity<>(nonExistentIds, createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/existing-ids",
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== DELETE COURSE TESTS ====================

    @Test
    @Order(25)
    void shouldDeleteCourseAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondCourseId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify deletion - should return 404 NOT_FOUND
        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + secondCourseId,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(26)
    void shouldReturn404WhenDeletingNonExistingCourse() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(27)
    void shouldDenyDeleteForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(28)
    void cleanUpTestData() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        // Clean up remaining courses
        restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );

        restTemplate.exchange(
                getBaseUrl() + "/" + thirdCourseId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );

        // Clean up any additional courses created during tests
        ResponseEntity<StandardResponseOutDTO<List<CourseOutDTO>>> allCourses = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        if (allCourses.getStatusCode() == HttpStatus.OK && !allCourses.getBody().getData().isEmpty()) {
            allCourses.getBody().getData().stream()
                    .filter(course -> course.getOwnerId() >= 4L) // Clean up test courses with owner ID >= 4
                    .forEach(course -> {
                        restTemplate.exchange(
                                getBaseUrl() + "/" + course.getCourseId(),
                                HttpMethod.DELETE,
                                entity,
                                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
                        );
                    });
        }
    }
}