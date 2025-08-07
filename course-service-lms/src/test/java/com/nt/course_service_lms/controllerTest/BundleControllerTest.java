package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.BundleController;
import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.BundleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BundleController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class BundleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BundleService bundleService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private BundleInDTO bundleInDTO;
    private UpdateBundleInDTO updateBundleInDTO;
    private BundleOutDTO bundleOutDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        bundleInDTO = BundleInDTO.builder()
                .bundleName("TestBundle")
                .isActive(true)
                .build();

        updateBundleInDTO = UpdateBundleInDTO.builder()
                .bundleName("UpdatedBundle")
                .isActive(false)
                .build();

        bundleOutDTO = BundleOutDTO.builder()
                .bundleId(1L)
                .bundleName("TestBundle")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createBundle_ShouldReturnCreatedBundle_WhenValidInput() throws Exception {
        // Given
        when(bundleService.createBundle(any(BundleInDTO.class))).thenReturn(bundleOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bundleInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle Created Successfully"))
                .andExpect(jsonPath("$.data.bundleId").value(1L))
                .andExpect(jsonPath("$.data.bundleName").value("TestBundle"));
    }

    @Test
    void createBundle_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid bundle with empty name
        BundleInDTO invalidBundle = BundleInDTO.builder()
                .bundleName("")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBundle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBundle_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/bundles")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bundleInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBundles_ShouldReturnListOfBundles_WhenBundlesExist() throws Exception {
        // Given
        List<BundleOutDTO> bundles = Arrays.asList(bundleOutDTO);
        when(bundleService.getAllBundles()).thenReturn(bundles);

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("All bundles retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bundleId").value(1L))
                .andExpect(jsonPath("$.data[0].bundleName").value("TestBundle"));
    }

    @Test
    void getAllBundles_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/bundles")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBundleById_ShouldReturnBundle_WhenBundleExists() throws Exception {
        // Given
        when(bundleService.getBundleById(1L)).thenReturn(bundleOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle retrieved successfully"))
                .andExpect(jsonPath("$.data.bundleId").value(1L))
                .andExpect(jsonPath("$.data.bundleName").value("TestBundle"));
    }

    @Test
    void getBundleById_ShouldReturnNotFound_WhenBundleDoesNotExist() throws Exception {
        // Given
        when(bundleService.getBundleById(999L)).thenThrow(new RuntimeException("Bundle not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateBundle_ShouldReturnUpdatedBundle_WhenValidInput() throws Exception {
        // Given
        BundleOutDTO updatedBundle = BundleOutDTO.builder()
                .bundleId(1L)
                .bundleName("UpdatedBundle")
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(bundleService.updateBundle(anyLong(), any(UpdateBundleInDTO.class))).thenReturn(updatedBundle);

        // When & Then
        mockMvc.perform(patch("/api/service-api/bundles/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBundleInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle updated successfully"))
                .andExpect(jsonPath("$.data.bundleId").value(1L))
                .andExpect(jsonPath("$.data.bundleName").value("UpdatedBundle"));
    }

    @Test
    void updateBundle_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with empty name
        UpdateBundleInDTO invalidUpdate = UpdateBundleInDTO.builder()
                .bundleName("")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(patch("/api/service-api/bundles/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBundle_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/service-api/bundles/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBundleInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBundle_ShouldReturnSuccess_WhenBundleExists() throws Exception {
        // Given
        doNothing().when(bundleService).deleteBundle(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/bundles/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteBundle_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/bundles/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkIfBundleExists_ShouldReturnTrue_WhenBundleExists() throws Exception {
        // Given
        when(bundleService.existsByBundleId(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/1/exists")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle exists"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void checkIfBundleExists_ShouldReturnFalse_WhenBundleDoesNotExist() throws Exception {
        // Given
        when(bundleService.existsByBundleId(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/999/exists")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle does not exist"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void getBundleCount_ShouldReturnCount_WhenCalled() throws Exception {
        // Given
        when(bundleService.countBundles()).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/count")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle count retrieved successfully"))
                .andExpect(jsonPath("$.data").value(5L));
    }

    @Test
    void getBundleCount_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/count")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBundleNameById_ShouldReturnBundleName_WhenBundleExists() throws Exception {
        // Given
        when(bundleService.getBundleNameById(1L)).thenReturn("TestBundle");

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/1/name")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle name retrieved successfully"))
                .andExpect(jsonPath("$.data").value("TestBundle"));
    }

    @Test
    void getBundleNameById_ShouldReturnNotFound_WhenBundleDoesNotExist() throws Exception {
        // Given
        when(bundleService.getBundleNameById(999L)).thenThrow(new ResourceNotFoundException(String.format("Bundle with ID %d not found", 999L)));

        // When & Then
        mockMvc.perform(get("/api/service-api/bundles/999/name")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getExistingBundleIds_ShouldReturnExistingIds_WhenIdsProvided() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList(1L, 2L, 3L, 999L);
        List<Long> existingIds = Arrays.asList(1L, 2L, 3L);
        when(bundleService.findExistingIds(anyList())).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/bundles/existing-ids")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(1L))
                .andExpect(jsonPath("$[1]").value(2L))
                .andExpect(jsonPath("$[2]").value(3L));
    }

    @Test
    void getExistingBundleIds_ShouldReturnEmptyList_WhenNoIdsExist() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList(999L, 998L);
        List<Long> existingIds = Arrays.asList();
        when(bundleService.findExistingIds(anyList())).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/bundles/existing-ids")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getExistingBundleIds_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/bundles/existing-ids")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}