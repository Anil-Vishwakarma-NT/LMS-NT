package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.CourseRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseContentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    private static Long testCourseId;
    private static Long testCourseId2;
    private static Long createdContentId;
    private static Long secondContentId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/course-content";
    }

    private HttpHeaders createHeaders() {
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
        // Create test courses
        Course testCourse1 = Course.builder()
                .title("TestCourse1")
                .description("Test Course 1 Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse1 = courseRepository.save(testCourse1);
        testCourseId = savedCourse1.getCourseId();

        Course testCourse2 = Course.builder()
                .title("TestCourse2")
                .description("Test Course 2 Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse2 = courseRepository.save(testCourse2);
        testCourseId2 = savedCourse2.getCourseId();
    }

    // ==================== CREATE COURSE CONTENT TESTS ====================

    @Test
    @Order(2)
    void shouldCreateCourseContentSuccessfully() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Introduction to Java")
                .description("This lesson covers the basics of Java programming language")
                .resourceLink("https://example.com/java-basics")
                .isActive(true)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Introduction to Java");
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId);
        assertThat(response.getBody().getData().isActive()).isTrue();
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();

        createdContentId = response.getBody().getData().getCourseContentId();
    }

    @Test
    @Order(3)
    void shouldCreateSecondCourseContentForTesting() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Advanced Java Topics")
                .description("Advanced concepts in Java programming")
                .resourceLink("https://example.com/advanced-java")
                .isActive(false)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        secondContentId = response.getBody().getData().getCourseContentId();
    }

    @Test
    @Order(4)
    void shouldRejectDuplicateContentTitle() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Introduction to Java") // Same title as first content
                .description("Duplicate content description")
                .resourceLink("https://example.com/duplicate")
                .isActive(true)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("Course Content Already Present");
    }

    @Test
    @Order(5)
    void shouldRejectInvalidCourseId() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(999999L) // Non-existent course ID
                .title("Valid Title")
                .description("Valid description")
                .resourceLink("https://example.com/valid")
                .isActive(true)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Course does not exists");
    }

    @Test
    @Order(6)
    void shouldRejectBlankTitle() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("") // Blank title
                .description("Valid description")
                .resourceLink("https://example.com/valid")
                .isActive(true)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("title");
    }

    @Test
    @Order(7)
    void shouldRejectInvalidResourceLink() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Valid Title")
                .description("Valid description")
                .resourceLink("invalid-url") // Invalid URL format
                .isActive(true)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("resourceLink");
    }

    @Test
    @Order(8)
    void shouldAcceptEmptyResourceLink() {
        CourseContentInDTO request = CourseContentInDTO.builder()
                .courseId(testCourseId2)
                .title("Content Without Link")
                .description("Content that doesn't require external resources")
                .resourceLink("") // Empty resource link should be allowed
                .isActive(true)
                .build();

        HttpEntity<CourseContentInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getResourceLink()).isEmpty();
    }

    // ==================== GET COURSE CONTENT TESTS ====================

    @Test
    @Order(9)
    void shouldGetAllCourseContents() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @Order(10)
    void shouldGetCourseContentById() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getCourseContentId()).isEqualTo(createdContentId);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Introduction to Java");
    }

    @Test
    @Order(11)
    void shouldReturn404ForNonExistingContent() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

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
    void shouldGetCourseContentByCourseId() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/course/" + testCourseId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isEqualTo(2);

        // Verify all contents belong to the correct course
        response.getBody().getData().forEach(content -> {
            assertThat(content.getCourseId()).isEqualTo(testCourseId);
        });
    }

    @Test
    @Order(13)
    void shouldReturn404ForNonExistingCourse() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/course/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(14)
    void shouldGetCourseContentCount() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<Integer>> response = restTemplate.exchange(
                getBaseUrl() + "/course/" + testCourseId + "/count",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(2);
    }

    // ==================== UPDATE COURSE CONTENT TESTS ====================

    @Test
    @Order(15)
    void shouldUpdateCourseContentSuccessfully() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Updated Java Introduction")
                .description("Updated description for Java basics")
                .resourceLink("https://example.com/updated-java")
                .isActive(false)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Updated Java Introduction");
        assertThat(response.getBody().getData().isActive()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(16)
    void shouldAllowUpdateWithSameTitle() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Updated Java Introduction") // Same title as current
                .description("Same title but different description")
                .resourceLink("https://example.com/same-title")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().isActive()).isTrue();
    }

    @Test
    @Order(17)
    void shouldRejectUpdateWithDuplicateTitle() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Advanced Java Topics") // Title of second content
                .description("Trying to use existing title")
                .resourceLink("https://example.com/duplicate")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("Course content with the same title already exists for this course");
    }

    @Test
    @Order(18)
    void shouldReturn404WhenUpdatingNonExistingContent() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Valid Title")
                .description("Valid description")
                .resourceLink("https://example.com/valid")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

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
    void shouldRejectUpdateWithInvalidData() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("") // Blank title
                .description("Valid description")
                .resourceLink("https://example.com/valid")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("title");
    }

    // ==================== UTILITY ENDPOINT TESTS ====================

    @Test
    @Order(20)
    void shouldGetHealthCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/health",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo("UP");
    }

    // ==================== DELETE COURSE CONTENT TESTS ====================

    @Test
    @Order(21)
    void shouldDeleteCourseContentSuccessfully() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondContentId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("Course Content Deleted Successfully");

        // Verify deletion
        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + secondContentId,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(22)
    void shouldReturn404WhenDeletingNonExistingContent() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(23)
    void cleanUpTestData() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        // Clean up remaining course content
        restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {
                }
        );

        // Clean up the content without link
        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> allContents = restTemplate.exchange(
                getBaseUrl() + "/course/" + testCourseId2,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        if (allContents.getStatusCode() == HttpStatus.OK && !allContents.getBody().getData().isEmpty()) {
            Long contentToDeleteId = allContents.getBody().getData().get(0).getCourseContentId();
            restTemplate.exchange(
                    getBaseUrl() + "/" + contentToDeleteId,
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {
                    }
            );
        }
    }
}
