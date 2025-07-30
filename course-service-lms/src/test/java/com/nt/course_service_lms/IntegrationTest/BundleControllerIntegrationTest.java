
package com.nt.course_service_lms.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper objectMapper;

    @Autowired
    private BundleRepository bundleRepository;

    private static Long createdBundleId;
    private static Long secondBundleId;

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

    private HttpHeaders createEmployeeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-employee");
        headers.set("X-Test-Role", "EMPLOYEE");
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
                .bundleName("IntegrationTestBundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle1 = bundleRepository.save(testBundle1);
        createdBundleId = savedBundle1.getBundleId();

        Bundle testBundle2 = Bundle.builder()
                .bundleName("SecondTestBundle")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Bundle savedBundle2 = bundleRepository.save(testBundle2);
        secondBundleId = savedBundle2.getBundleId();
    }

    // ==================== CREATE BUNDLE TESTS ====================

    @Test
    @Order(2)
    void shouldCreateBundleWithValidData() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("NewBundleForCreation")
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("NewBundleForCreation");
        assertThat(response.getBody().getData().isActive()).isTrue();
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(3)
    void shouldRejectDuplicateBundleName() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("IntegrationTestBundle") // Same name as setup bundle
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("already exists");
    }

    @Test
    @Order(4)
    void shouldRejectInvalidBundleNameTooShort() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("AB") // Too short (min 3 characters)
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
    void shouldRejectInvalidBundleNamePattern() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("123InvalidName") // Starts with number
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
    @Order(6)
    void shouldRejectBlankBundleName() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("") // Blank name
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
    @Order(7)
    void shouldDenyBundleCreationForNonAdminRole() {
        BundleInDTO request = BundleInDTO.builder()
                .bundleName("UnauthorizedBundle")
                .active(true)
                .build();

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== GET BUNDLE TESTS ====================

    @Test
    @Order(8)
    void shouldGetBundleByIdSuccessfully() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(createdBundleId);
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("IntegrationTestBundle");
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
    void shouldHandleInvalidBundleIdType() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/invalid-id",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("must be of type");
    }

    @Test
    @Order(11)
    void shouldGetAllBundlesAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(12)
    void shouldDenyGetAllBundlesForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== UPDATE BUNDLE TESTS ====================

    @Test
    @Order(13)
    void shouldUpdateBundleSuccessfully() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("UpdatedBundleName")
                .isActive(false)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("UpdatedBundleName");
        assertThat(response.getBody().getData().isActive()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(14)
    void shouldAllowUpdateWithSameName() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("UpdatedBundleName") // Same name as current
                .isActive(true)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().isActive()).isTrue();
    }

    @Test
    @Order(15)
    void shouldRejectUpdateWithExistingName() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("SecondTestBundle") // Name of second bundle
                .isActive(false)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("already exists");
    }

    @Test
    @Order(16)
    void shouldRejectUpdateWithInvalidData() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("AB") // Too short
                .isActive(true)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("bundleName");
    }

    @Test
    @Order(17)
    void shouldReturn404WhenUpdatingNonExistingBundle() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("ValidName")
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
    }

    @Test
    @Order(18)
    void shouldDenyUpdateForNonAdmin() {
        UpdateBundleInDTO updateRequest = UpdateBundleInDTO.builder()
                .bundleName("UnauthorizedUpdate")
                .isActive(false)
                .build();

        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== UTILITY ENDPOINT TESTS ====================

    @Test
    @Order(19)
    void shouldCheckBundleExistence() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId + "/exists",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isTrue();
    }

    @Test
    @Order(20)
    void shouldReturnFalseForNonExistingBundleCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
                getBaseUrl() + "/999999/exists",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isFalse();
    }

    @Test
    @Order(21)
    void shouldGetBundleNameById() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId + "/name",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo("UpdatedBundleName");
    }

    @Test
    @Order(22)
    void shouldReturn404WhenGettingNameOfNonExistingBundle() {
        HttpEntity<Void> entity = new HttpEntity<>(createUserHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999/name",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(23)
    void shouldGetBundleCountAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/count",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(2L);
    }

    @Test
    @Order(24)
    void shouldDenyBundleCountForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/count",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @Order(25)
    void shouldGetExistingBundleIds() {
        List<Long> testIds = Arrays.asList(createdBundleId, secondBundleId, 999999L);
        HttpEntity<List<Long>> entity = new HttpEntity<>(testIds, createUserHeaders());

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/existing-ids",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(createdBundleId, secondBundleId);
        assertThat(response.getBody()).doesNotContain(999999L);
    }

    @Test
    @Order(26)
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

    // ==================== DELETE BUNDLE TESTS ====================

    @Test
    @Order(27)
    void shouldDeleteBundleAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify deletion
        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + secondBundleId,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(28)
    void shouldReturn404WhenDeletingNonExistingBundle() {
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
    @Order(29)
    void shouldDenyDeleteForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(30)
    void cleanUpTestData() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {}
        );
    }
}