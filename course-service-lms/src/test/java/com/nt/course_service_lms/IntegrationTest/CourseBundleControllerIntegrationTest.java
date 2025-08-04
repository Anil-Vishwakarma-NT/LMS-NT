package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.BundleRepository;
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
class CourseBundleControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static Long testBundleId;
    private static Long testCourseId;
    private static Long testCourseId2;
    private static Long createdCourseBundleId;
    private static Long secondCourseBundleId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/course-bundles";
    }

    private String getBundleUrl() {
        return "http://localhost:" + port + "/api/service-api/bundles";
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
        // Create test bundle
        Bundle testBundle = Bundle.builder()
                .bundleName("TestBundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle = bundleRepository.save(testBundle);
        testBundleId = savedBundle.getBundleId();

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

    // ==================== CREATE COURSE BUNDLE TESTS ====================

    @Test
    @Order(2)
    void shouldCreateCourseBundleSuccessfully() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(testCourseId)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseBundle>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(testBundleId);
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId);
        assertThat(response.getBody().getData().isActive()).isTrue();

        createdCourseBundleId = response.getBody().getData().getCourseBundleId();
    }

    @Test
    @Order(3)
    void shouldCreateSecondCourseBundleForTesting() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(testCourseId2)
                .isActive(false)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseBundle>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        secondCourseBundleId = response.getBody().getData().getCourseBundleId();
    }

    @Test
    @Order(4)
    void shouldRejectDuplicateCourseBundle() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(testCourseId) // Same combination as first test
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

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
    @Order(5)
    void shouldRejectInvalidBundleId() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(999999L) // Non-existent bundle ID
                .courseId(testCourseId)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invalid Bundle ID");
    }

    @Test
    @Order(6)
    void shouldRejectInvalidCourseId() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(999999L) // Non-existent course ID
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invalid Course ID");
    }

    @Test
    @Order(7)
    void shouldRejectNullBundleId() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(null)
                .courseId(testCourseId)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //assertThat(response.getBody()).containsKey("bundleId");
    }

    @Test
    @Order(8)
    void shouldRejectNegativeCourseId() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(-1L)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("courseId");
    }

    // ==================== GET COURSE BUNDLE TESTS ====================

    @Test
    @Order(9)
    void shouldGetAllCourseBundles() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(10)
    void shouldGetCourseBundleById() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseBundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getCourseBundleId()).isEqualTo(createdCourseBundleId);
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(testBundleId);
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId);
    }

    @Test
    @Order(11)
    void shouldReturn404ForNonExistingCourseBundle() {
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
    void shouldGetAllCoursesByBundleId() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/bundle/" + testBundleId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isEqualTo(2);
    }

    @Test
    @Order(13)
    void shouldReturn404ForEmptyBundle() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/bundle/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== UPDATE COURSE BUNDLE TESTS ====================

    @Test
    @Order(14)
    void shouldUpdateCourseBundleSuccessfully() {
        UpdateCourseBundleInDTO updateRequest = UpdateCourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(testCourseId2)
                .isActive(false)
                .build();

        HttpEntity<UpdateCourseBundleInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).contains("Updated Successfully");
    }

    @Test
    @Order(15)
    void shouldReturn404WhenUpdatingNonExistingCourseBundle() {
        UpdateCourseBundleInDTO updateRequest = UpdateCourseBundleInDTO.builder()
                .bundleId(testBundleId)
                .courseId(testCourseId)
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseBundleInDTO> entity = new HttpEntity<>(updateRequest, createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    // ==================== UTILITY ENDPOINT TESTS ====================

    @Test
    @Order(17)
    void shouldGetBundlesInfo() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/info",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();

        BundleInfoOutDTO bundleInfo = response.getBody().getData().stream()
                .filter(info -> info.getBundleId().equals(testBundleId))
                .findFirst()
                .orElse(null);

        assertThat(bundleInfo).isNotNull();
        assertThat(bundleInfo.getTotalCourses()).isEqualTo(2L);
        assertThat(bundleInfo.getBundleName()).isEqualTo("TestBundle");
    }

    @Test
    @Order(18)
    void shouldGetRecentBundles() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<List<BundleSummaryOutDTO>>> response = restTemplate.exchange(
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
    @Order(19)
    void shouldFindCourseIdsByBundleId() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/bundle-id/" + testBundleId + "/course-ids",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Long>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(testCourseId2); // Updated course ID
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    @Order(20)
    void shouldReturn404WhenNoCourseIdsFound() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/bundle-id/999999/course-ids",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== DELETE COURSE BUNDLE TESTS ====================

    @Test
    @Order(21)
    void shouldDeleteCourseBundleSuccessfully() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondCourseBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify deletion
        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + secondCourseBundleId,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(22)
    void shouldReturn404WhenDeletingNonExistingCourseBundle() {
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

        // Clean up remaining course bundle
        restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );

        // Clean up test bundle
        restTemplate.exchange(
                getBundleUrl() + "/" + testBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );
    }
}
