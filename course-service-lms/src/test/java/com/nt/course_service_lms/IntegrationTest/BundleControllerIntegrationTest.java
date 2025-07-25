package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithMockUser(roles = "ADMIN")
class BundleControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Long createdBundleId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/bundles";
    }


    @Test
    @Order(1)
    void shouldCreateBundle() {
        BundleInDTO request = new BundleInDTO();
        request.setBundleName("Integration Bundle");
        request.setActive(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, headers);

        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("Integration Bundle");

        // Store the ID from the nested data object
        createdBundleId = response.getBody().getData().getBundleId();
    }

    @Test
    @Order(2)
    void shouldGetBundleById() {
        ResponseEntity<BundleOutDTO> response = restTemplate.getForEntity(getBaseUrl() + "/" + createdBundleId, BundleOutDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBundleId()).isEqualTo(createdBundleId);
    }

    @Test
    @Order(3)
    void shouldReturnAllBundles() {
        ResponseEntity<BundleOutDTO[]> response = restTemplate.getForEntity(getBaseUrl() + "/all", BundleOutDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @Order(4)
    void shouldUpdateBundle() {
        BundleInDTO updateRequest = new BundleInDTO();
        updateRequest.setBundleName("Updated Bundle Name");
        updateRequest.setActive(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BundleInDTO> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<BundleOutDTO> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.PUT,
                entity,
                BundleOutDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBundleName()).isEqualTo("Updated Bundle Name");
        assertThat(response.getBody().isActive()).isFalse();
    }

    @Test
    @Order(5)
    void shouldReturn404ForNonExistingBundle() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(6)
    void shouldDeleteBundle() {
        restTemplate.delete(getBaseUrl() + "/" + createdBundleId);

        // Confirm deletion
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/" + createdBundleId, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(7)
    void shouldReturn404WhenDeletingNonExistingBundle() {
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
