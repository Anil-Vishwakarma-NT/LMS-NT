//package com.nt.course_service_lms.IntegrationTest;
//
//import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
//import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
//import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@WithMockUser(roles = "ADMIN")
//class BundleControllerIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private static Long createdBundleId;
//
//    private String getBaseUrl() {
//        return "http://localhost:" + port + "/api/service-api/bundles";
//    }
//
//
//    @Test
//    @Order(1)
//    void shouldCreateBundle() {
//        BundleInDTO request = new BundleInDTO();
//        request.setBundleName("Integration Bundle");
//        request.setActive(true);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, headers);
//
//        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
//                new ParameterizedTypeReference<>() {
//                };
//
//        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                responseType
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData().getBundleName()).isEqualTo("Integration Bundle");
//
//        // Store the ID from the nested data object
//        createdBundleId = response.getBody().getData().getBundleId();

//
//    @Test
//    @Order(2)
//    void shouldGetBundleById() {
//        ResponseEntity<BundleOutDTO> response = restTemplate.getForEntity(getBaseUrl() + "/" + createdBundleId, BundleOutDTO.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getBundleId()).isEqualTo(createdBundleId);
//    }
//
//    @Test
//    @Order(3)
//    void shouldReturnAllBundles() {
//        ResponseEntity<BundleOutDTO[]> response = restTemplate.getForEntity(getBaseUrl() + "/all", BundleOutDTO[].class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotEmpty();
//    }
//
//    @Test
//    @Order(4)
//    void shouldUpdateBundle() {
//        BundleInDTO updateRequest = new BundleInDTO();
//        updateRequest.setBundleName("Updated Bundle Name");
//        updateRequest.setActive(false);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<BundleInDTO> entity = new HttpEntity<>(updateRequest, headers);
//
//        ResponseEntity<BundleOutDTO> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId,
//                HttpMethod.PUT,
//                entity,
//                BundleOutDTO.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getBundleName()).isEqualTo("Updated Bundle Name");
//        assertThat(response.getBody().isActive()).isFalse();
//    }
//
//    @Test
//    @Order(5)
//    void shouldReturn404ForNonExistingBundle() {
//        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/999999", String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(6)
//    void shouldDeleteBundle() {
//        restTemplate.delete(getBaseUrl() + "/" + createdBundleId);
//
//        // Confirm deletion
//        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/" + createdBundleId, String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(7)
//    void shouldReturn404WhenDeletingNonExistingBundle() {
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.DELETE,
//                null,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//}
//
//package com.nt.course_service_lms.IntegrationTest;
//
//import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
//import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
//import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
//import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class BundleControllerIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private static Long createdBundleId;
//
//    private String getBaseUrl() {
//        return "http://localhost:" + port + "/api/service-api/bundles";
//    }
//
//    @Test
//    @Order(1)
//    @WithMockUser(roles = "ADMIN")
//    void shouldCreateBundle() {
//        // Given
//        BundleInDTO request = new BundleInDTO();
//        request.setBundleName("IntegrationTestBundle");
//        request.setActive(true);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, headers);
//
//        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData().getBundleName()).isEqualTo("IntegrationTestBundle");
//        assertThat(response.getBody().getData().isActive()).isTrue();
//
//        createdBundleId = response.getBody().getData().getBundleId();
//        assertThat(createdBundleId).isNotNull();
//    }
//
//    @Test
//    @Order(2)
//    void shouldGetBundleById() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId,
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData().getBundleId()).isEqualTo(createdBundleId);
//        assertThat(response.getBody().getData().getBundleName()).isEqualTo("IntegrationTestBundle");
//    }
//
//    @Test
//    @Order(3)
//    @WithMockUser(roles = "ADMIN")
//    void shouldGetAllBundles() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<List<BundleOutDTO>>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isNotEmpty();
//        assertThat(response.getBody().getData().size()).isGreaterThan(0);
//    }
//
//    @Test
//    @Order(4)
//    @WithMockUser(roles = "ADMIN")
//    void shouldUpdateBundle() {
//        // Given
//        UpdateBundleInDTO updateRequest = new UpdateBundleInDTO();
//        updateRequest.setBundleName("UpdatedBundleName");
//        updateRequest.setActive(false);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, headers);
//
//        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId,
//                HttpMethod.PUT,
//                entity,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isNotNull();
//        assertThat(response.getBody().getData().getBundleName()).isEqualTo("UpdatedBundleName");
//        assertThat(response.getBody().getData().isActive()).isFalse();
//    }
//
//    @Test
//    @Order(5)
//    void shouldCheckIfBundleExists() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<Boolean>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId + "/exists",
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isTrue();
//    }
//
//    @Test
//    @Order(6)
//    @WithMockUser(roles = "ADMIN")
//    void shouldGetBundleCount() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<Long>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
//                getBaseUrl() + "/count",
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isGreaterThan(0L);
//    }
//
//    @Test
//    @Order(7)
//    void shouldGetBundleNameById() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<String>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId + "/name",
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isEqualTo("UpdatedBundleName");
//    }
//
//    @Test
//    @Order(8)
//    void shouldGetExistingBundleIds() {
//        // Given
//        List<Long> bundleIds = Arrays.asList(createdBundleId, 999999L, 888888L);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<List<Long>> entity = new HttpEntity<>(bundleIds, headers);
//
//        ParameterizedTypeReference<List<Long>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<List<Long>> response = restTemplate.exchange(
//                getBaseUrl() + "/existing-ids",
//                HttpMethod.POST,
//                entity,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody()).contains(createdBundleId);
//        assertThat(response.getBody()).doesNotContain(999999L, 888888L);
//    }
//
//    @Test
//    @Order(9)
//    void shouldReturn404ForNonExistingBundle() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(10)
//    void shouldReturn404ForNonExistingBundleExists() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<Boolean>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
//                getBaseUrl() + "/999999/exists",
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getData()).isFalse();
//        assertThat(response.getBody().getMessage()).isEqualTo("Bundle does not exist");
//    }
//
//    @Test
//    @Order(11)
//    @WithMockUser(roles = "ADMIN")
//    void shouldReturn400ForInvalidBundleCreation() {
//        // Given - Invalid bundle name (starts with digit)
//        BundleInDTO request = new BundleInDTO();
//        request.setBundleName("123InvalidName");
//        request.setActive(true);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, headers);
//
//        // When
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//    }
//
//    @Test
//    @Order(12)
//    @WithMockUser(roles = "ADMIN")
//    void shouldReturn409ForDuplicateBundleName() {
//        // Given - Try to create bundle with existing name
//        BundleInDTO request = new BundleInDTO();
//        request.setBundleName("UpdatedBundleName"); // This name already exists from update test
//        request.setActive(true);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, headers);
//
//        // When
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//    }
//
//    @Test
//    @Order(13)
//    @WithMockUser(roles = "ADMIN")
//    void shouldReturn404WhenUpdatingNonExistingBundle() {
//        // Given
//        UpdateBundleInDTO updateRequest = new UpdateBundleInDTO();
//        updateRequest.setBundleName("NonExistentBundle");
//        updateRequest.setActive(true);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, headers);
//
//        // When
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.PUT,
//                entity,
//                String.class
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(14)
//    void shouldDeleteBundle() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<Void>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId,
//                HttpMethod.DELETE,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
//        assertThat(response.getBody().getMessage()).isEqualTo("Bundle deleted successfully");
//    }
//
//    @Test
//    @Order(15)
//    void shouldConfirmBundleDeletion() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<BundleOutDTO>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When - Try to get the deleted bundle
//        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
//                getBaseUrl() + "/" + createdBundleId,
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(16)
//    void shouldReturn404WhenDeletingNonExistingBundle() {
//        // When
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/999999",
//                HttpMethod.DELETE,
//                null,
//                String.class
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(17)
//    void shouldReturn404WhenGettingNameOfNonExistingBundle() {
//        // Given
//        ParameterizedTypeReference<StandardResponseOutDTO<String>> responseType =
//                new ParameterizedTypeReference<>() {};
//
//        // When
//        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
//                getBaseUrl() + "/999999/name",
//                HttpMethod.GET,
//                null,
//                responseType
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @Order(18)
//    void shouldReturn404WhenNoExistingIdsFound() {
//        // Given - List of non-existing IDs
//        List<Long> nonExistingIds = Arrays.asList(999999L, 888888L, 777777L);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<List<Long>> entity = new HttpEntity<>(nonExistingIds, headers);
//
//        // When
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl() + "/existing-ids",
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//}
package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

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
        request.setBundleName("IntegrationBundle");
        request.setActive(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BundleInDTO> entity = new HttpEntity<>(request, headers);
        String rawJson = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        ).getBody();

        System.out.println("Raw JSON response:\n" + rawJson);

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleName()).isEqualTo("IntegrationBundle");

        createdBundleId = response.getBody().getData().getBundleId();
    }

    @Test
    @Order(2)
    void shouldGetBundleById() {

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getBundleId()).isEqualTo(createdBundleId);
    }

    @Test
    @Order(3)
    void shouldReturnAllBundles() {

        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
    }

    @Test
    @Order(4)
    void shouldUpdateBundle() {
        UpdateBundleInDTO updateRequest = new UpdateBundleInDTO();
        updateRequest.setBundleName("UpdatedBundleName");
        updateRequest.setActive(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateBundleInDTO> entity = new HttpEntity<>(updateRequest, headers);
        String rawJson = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        ).getBody();

        System.out.println("Raw JSON response:\n" + rawJson);

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
    }

    @Test
    @Order(5)
    void shouldCheckIfBundleExists() {
        ResponseEntity<StandardResponseOutDTO<Boolean>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId + "/exists",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isTrue();
    }

    @Test
    @Order(6)
    void shouldGetBundleNameById() {
        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId + "/name",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo("Updated Bundle Name");
        System.out.println("Response: " + response.getBody());
        System.out.println("Status: " + response.getStatusCode());

    }

    @Test
    @Order(7)
    void shouldGetBundleCount() {
        ResponseEntity<StandardResponseOutDTO<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/count",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isGreaterThan(0L);
    }

    @Test
    @Order(8)
    void shouldGetExistingBundleIds() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Long>> entity = new HttpEntity<>(Arrays.asList(createdBundleId, 999999L), headers);

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                getBaseUrl() + "/existing-ids",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(createdBundleId);
        assertThat(response.getBody()).doesNotContain(999999L);
    }

    @Test
    @Order(9)
    void shouldReturn404ForNonExistingBundle() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/999999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(10)
    void shouldDeleteBundle() {
        ResponseEntity<StandardResponseOutDTO<Void>> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + createdBundleId,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Confirm deletion
        ResponseEntity<String> confirm = restTemplate.getForEntity(getBaseUrl() + "/" + createdBundleId, String.class);
        assertThat(confirm.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(11)
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

