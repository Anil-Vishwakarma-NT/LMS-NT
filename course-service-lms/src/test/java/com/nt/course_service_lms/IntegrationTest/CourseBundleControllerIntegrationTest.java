package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.AddCourseToBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.BundleRepository;
import com.nt.course_service_lms.repository.CourseBundleRepository;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CourseBundleControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseBundleRepository courseBundleRepository;

    private static Long testBundleId1;
    private static Long testBundleId2;
    private static Long testCourseId1;
    private static Long testCourseId2;
    private static Long testCourseId3;
    private static Long createdCourseBundleId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/course-bundles";
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
        // Create test bundles
        Bundle testBundle1 = Bundle.builder()
                .bundleName("JavaFundamentalsBundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle1 = bundleRepository.save(testBundle1);
        testBundleId1 = savedBundle1.getBundleId();

        Bundle testBundle2 = Bundle.builder()
                .bundleName("SpringFrameworkBundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle2 = bundleRepository.save(testBundle2);
        testBundleId2 = savedBundle2.getBundleId();

        // Create test courses
        Course testCourse1 = Course.builder()
                .title("Java Basics")
                .level("BEGINNER")
                .isActive(true)
                .ownerId(1L)
                .description("Introduction to Java programming")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse1 = courseRepository.save(testCourse1);
        testCourseId1 = savedCourse1.getCourseId();

        Course testCourse2 = Course.builder()
                .title("Java OOP Concepts")
                .level("INTERMEDIATE")
                .isActive(true)
                .ownerId(1L)
                .description("Object-Oriented Programming in Java")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse2 = courseRepository.save(testCourse2);
        testCourseId2 = savedCourse2.getCourseId();

        Course testCourse3 = Course.builder()
                .title("Spring Boot Fundamentals")
                .level("INTERMEDIATE")
                .isActive(true)
                .ownerId(1L)
                .description("Getting started with Spring Boot")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse3 = courseRepository.save(testCourse3);
        testCourseId3 = savedCourse3.getCourseId();

        System.out.println("Test data created - Bundle1: " + testBundleId1 +
                ", Bundle2: " + testBundleId2 +
                ", Course1: " + testCourseId1 +
                ", Course2: " + testCourseId2 +
                ", Course3: " + testCourseId3);
    }

    // ==================== CREATE COURSE BUNDLE TESTS ====================

    @Test
    @Order(2)
    void shouldCreateCourseBundleSuccessfully() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(testCourseId1)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseBundle>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(testBundleId1);
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId1);
        assertThat(response.getBody().getData().isActive()).isTrue();

        createdCourseBundleId = response.getBody().getData().getCourseBundleId();
    }

    @Test
    @Order(3)
    void shouldRejectDuplicateCourseBundle() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(testCourseId1) // Same combination as previous test
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

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
    void shouldRejectInvalidBundleId() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(999999L) // Non-existent bundle
                .courseId(testCourseId1)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Bundle ID");
    }

    @Test
    @Order(5)
    void shouldRejectInvalidCourseId() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(999999L) // Non-existent course
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Course ID");
    }

    @Test
    @Order(6)
    void shouldRejectInvalidCourseBundleData() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(-1L) // Invalid negative ID
                .courseId(testCourseId1)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("bundleId");
    }

    @Test
    @Order(7)
    void shouldDenyCourseBundleCreationForNonAdmin() {
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(testCourseId2)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> entity = new HttpEntity<>(request, createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== GET COURSE BUNDLE TESTS ====================

    @Test
    @Order(8)
    void shouldGetAllCourseBundlesAsAdmin() {
        // Create another course bundle for testing
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(testCourseId2)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> createEntity = new HttpEntity<>(request, createAdminHeaders());
        restTemplate.exchange(getBaseUrl(), HttpMethod.POST, createEntity,
                new ParameterizedTypeReference<StandardResponseOutDTO<CourseBundle>>() {
                });

        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(9)
    void shouldGetAllCourseBundlesAsEmployee() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
    }

    @Test
    @Order(10)
    void shouldGetCourseBundleById() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseBundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getCourseBundleId()).isEqualTo(createdCourseBundleId);
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(testBundleId1);
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId1);
    }

    @Test
    @Order(11)
    void shouldReturn404ForNonExistingCourseBundle() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== UPDATE COURSE BUNDLE TESTS ====================

    @Test
    @Order(12)
    void shouldUpdateCourseBundleSuccessfully() {
        UpdateCourseBundleInDTO updateRequest = UpdateCourseBundleInDTO.builder()
                .bundleId(testBundleId2)
                .courseId(testCourseId3)
                .isActive(false)
                .build();

        HttpEntity<UpdateCourseBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).contains("Updated Successfully");
    }

    @Test
    @Order(13)
    void shouldReturn404WhenUpdatingNonExistingCourseBundle() {
        UpdateCourseBundleInDTO updateRequest = UpdateCourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(testCourseId1)
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(14)
    void shouldDenyUpdateForNonAdmin() {
        UpdateCourseBundleInDTO updateRequest = UpdateCourseBundleInDTO.builder()
                .bundleId(testBundleId1)
                .courseId(testCourseId1)
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseBundleInDTO> entity = new HttpEntity<>(updateRequest, createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== DELETE COURSE BUNDLE TESTS ====================

    @Test
    @Order(15)
    void shouldDeleteCourseBundleAsAdmin() {
        // Create a course bundle to delete
        CourseBundleInDTO request = CourseBundleInDTO.builder()
                .bundleId(testBundleId2)
                .courseId(testCourseId1)
                .isActive(true)
                .build();

        HttpEntity<CourseBundleInDTO> createEntity = new HttpEntity<>(request, createAdminHeaders());
        ResponseEntity<StandardResponseOutDTO<CourseBundle>> createResponse = restTemplate.exchange(
                getBaseUrl(), HttpMethod.POST, createEntity,
                new ParameterizedTypeReference<StandardResponseOutDTO<CourseBundle>>() {
                });

        Long courseBundleToDelete = createResponse.getBody().getData().getCourseBundleId();

        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + courseBundleToDelete,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @Order(16)
    void shouldReturn404WhenDeletingNonExistingCourseBundle() {
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
    @Order(17)
    void shouldDenyDeleteForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdCourseBundleId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== BUNDLE-SPECIFIC ENDPOINT TESTS ====================

    @Test
    @Order(18)
    void shouldGetAllCoursesByBundleId() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/bundle/" + testBundleId1,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
    }

    @Test
    @Order(19)
    void shouldGetCoursesToAddInBundle() {
        String url = getBaseUrl() + "/bundle/courses?bundleId=" + testBundleId1;
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    @Order(20)
    void shouldAddCourseToBundle() {
        AddCourseToBundleInDTO request = AddCourseToBundleInDTO.builder()
                .bundleId(testBundleId2)
                .courses(Arrays.asList(testCourseId2, testCourseId3))
                .build();

        HttpEntity<AddCourseToBundleInDTO> entity = new HttpEntity<>(request, createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/bundle/addCourse",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getMessage()).contains("added");
    }

    @Test
    @Order(21)
    void shouldGetBundleCourses() {
        String url = getBaseUrl() + "/bundle/bundlecourses?bundleId=" + testBundleId2;
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
    }

    @Test
    @Order(22)
    void shouldRemoveCourseFromBundle() {
        String url = getBaseUrl() + "/bundle/removeCourse?bundleId=" + testBundleId2 + "&courseId=" + testCourseId2;
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getMessage()).contains("Removed");
    }

    // ==================== INFO ENDPOINT TESTS ====================

    @Test
    @Order(23)
    void shouldGetAllBundleInfo() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/info",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().get(0).getBundleId()).isNotNull();
        assertThat(response.getBody().getData().get(0).getBundleName()).isNotNull();
    }

    @Test
    @Order(24)
    void shouldGetRecentBundles() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<BundleSummaryOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/recent",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    @Order(25)
    void shouldDenyRecentBundlesForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/recent",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(26)
    void shouldFindCourseIdsByBundleId() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/bundle-id/" + testBundleId1 + "/course-ids",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @Order(27)
    void shouldReturn404ForNonExistentBundleInCoursesByBundle() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/bundle/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(28)
    void shouldReturn404ForNonExistentBundleInAddCourse() {
        AddCourseToBundleInDTO request = AddCourseToBundleInDTO.builder()
                .bundleId(999999L)
                .courses(Arrays.asList(testCourseId1))
                .build();

        HttpEntity<AddCourseToBundleInDTO> entity = new HttpEntity<>(request, createEmployeeHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/bundle/addCourse",
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(29)
    void shouldReturn404ForNonExistentCourseInRemove() {
        String url = getBaseUrl() + "/bundle/removeCourse?bundleId=" + testBundleId1 + "&courseId=999999";
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(30)
    void cleanUpTestData() {
        // Clean up course bundles
        courseBundleRepository.deleteAll();

        // Clean up courses
        if (testCourseId1 != null) {
            courseRepository.deleteById(testCourseId1);
        }
        if (testCourseId2 != null) {
            courseRepository.deleteById(testCourseId2);
        }
        if (testCourseId3 != null) {
            courseRepository.deleteById(testCourseId3);
        }

        // Clean up bundles
        if (testBundleId1 != null) {
            bundleRepository.deleteById(testBundleId1);
        }
        if (testBundleId2 != null) {
            bundleRepository.deleteById(testBundleId2);
        }

        System.out.println("Test data cleanup completed");
    }
}