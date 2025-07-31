
package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.BundleRepository;
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
class BundleControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BundleRepository bundleRepository;

    private static Long createdBundleId;
    private static Long secondBundleId;
    private static Long thirdBundleId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/bundles";
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
        // Create test bundles directly using repository
        Bundle testBundle1 = Bundle.builder()
                .bundleName("Spring Boot Fundamentals Bundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle1 = bundleRepository.save(testBundle1);
        createdBundleId = savedBundle1.getBundleId();

        Bundle testBundle2 = Bundle.builder()
                .bundleName("Advanced Java Bundle")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle2 = bundleRepository.save(testBundle2);
        secondBundleId = savedBundle2.getBundleId();

        Bundle testBundle3 = Bundle.builder()
                .bundleName("Microservices Bundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle3 = bundleRepository.save(testBundle3);
        thirdBundleId = savedBundle3.getBundleId();
    }

    // ==================== CREATE BUNDLE TESTS ====================

    @Test
    @Order(2)
    void shouldCreateBundleSuccessfully() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("New Bundle Creation Test")
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("New Bundle Creation Test");
        assertThat(response.getBody().getData().isActive()).isTrue();
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();
    }

    @Test
    @Order(3)
    void shouldRejectDuplicateBundleName() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("Spring Boot Fundamentals Bundle") // Same name as setup bundle
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("already exists");
    }

    @Test
    @Order(4)
    void shouldRejectInvalidBundleData() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("AB") // Too short (assuming min 3 characters validation)
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("bundleName");
    }

    @Test
    @Order(5)
    void shouldDenyBundleCreationForNonAdmin() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("Unauthorized Bundle")
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Access Denied");
    }

    // ==================== GET BUNDLE TESTS ====================

    @Test
    @Order(6)
    void shouldGetAllBundlesAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<List<BundleOutDTO>>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @Order(7)
    void shouldDenyGetAllBundlesForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Access Denied");
    }

    @Test
    @Order(8)
    void shouldGetBundleById() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(createdBundleId);
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("Spring Boot Fundamentals Bundle");
    }

    @Test
    @Order(9)
    void shouldReturn404ForNonExistingBundle() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("not found");
    }

    @Test
    @Order(10)
    void shouldCheckBundleExistence() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId + "/exists",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Boolean>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isTrue();
    }

    @Test
    @Order(11)
    void shouldReturnFalseForNonExistingBundleCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
                getBaseUrl() + "/999999/exists",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Boolean>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isFalse();
    }

    @Test
    @Order(12)
    void shouldGetBundleNameById() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId + "/name",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo("Spring Boot Fundamentals Bundle");
    }

    // ==================== UPDATE BUNDLE TESTS ====================

    @Test
    @Order(13)
    void shouldUpdateBundleSuccessfully() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("Updated Spring Boot Bundle")
                .isActive(false)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("Updated Spring Boot Bundle");
        assertThat(response.getBody().getData().isActive()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(14)
    void shouldAllowUpdateWithSameTitle() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("Updated Spring Boot Bundle") // Same name as current
                .isActive(true)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().isActive()).isTrue();
    }

    @Test
    @Order(15)
    void shouldRejectUpdateWithDuplicateTitle() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("Advanced Java Bundle") // Name exists for another bundle
                .isActive(true)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).containsAnyOf("duplicate", "already exists");
    }

    @Test
    @Order(16)
    void shouldReturn404WhenUpdatingNonExistingBundle() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("Valid Title")
                .isActive(true)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @Order(17)
    void shouldDenyUpdateForNonAdmin() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("Unauthorized Update")
                .isActive(true)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
    }

    // ==================== UTILITY ENDPOINT TESTS ====================

    @Test
    @Order(18)
    void shouldGetBundleCountAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/count",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Long>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(3L);
    }

    @Test
    @Order(19)
    void shouldGetExistingBundleIds() {
        List<Long> testIds = Arrays.asList(createdBundleId, secondBundleId, 999999L);
        HttpEntity<List<Long>> entity = new HttpEntity<>(testIds, createUserHeaders());

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/existing-ids",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<Long>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(createdBundleId, secondBundleId);
        assertThat(response.getBody()).doesNotContain(999999L);
    }

    @Test
    @Order(20)
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
        assertThat(response.getBody()).isNotNull();
    }

    // ==================== DELETE BUNDLE TESTS ====================

    @Test
    @Order(21)
    void shouldDeleteBundleAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");

        // Verify deletion - should return 404 NOT_FOUND
        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + secondBundleId,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(22)
    void shouldReturn404WhenDeletingNonExistingBundle() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @Order(23)
    void shouldDenyDeleteForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(24)
    void cleanUpTestData() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        // Clean up remaining bundles
        restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );

        restTemplate.exchange(
                getBaseUrl() + "/" + thirdBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );

        // Clean up any additional bundles created during tests
        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> allBundles = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<List<BundleOutDTO>>>() {}
        );

        if (allBundles.getStatusCode() == HttpStatus.OK &&
                allBundles.getBody() != null &&
                !allBundles.getBody().getData().isEmpty()) {
            allBundles.getBody().getData().stream()
                    .filter(bundle -> bundle.getBundleName().contains("Test") ||
                            bundle.getBundleName().contains("New"))
                    .forEach(bundle -> {
                        restTemplate.exchange(
                                getBaseUrl() + "/" + bundle.getBundleId(),
                                HttpMethod.DELETE,
                                entity,
                                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
                        );
                    });
        }
    }
}