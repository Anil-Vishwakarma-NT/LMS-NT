package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.BundleController;
import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.BundleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BundleController.class)
@Import(BundleControllerTest.TestConfig.class)
class BundleControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BundleService bundleService() {
            return Mockito.mock(BundleService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BundleService bundleService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private BundleOutDTO sampleBundle;

    @BeforeEach
    void setup() {
        sampleBundle = BundleOutDTO.builder()
                .bundleId(1L)
                .bundleName("JavaBundle")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createBundle_ReturnsCreated() throws Exception {
        BundleInDTO input = new BundleInDTO("JavaBundle", true);
        Mockito.when(bundleService.createBundle(any())).thenReturn(sampleBundle);

        mockMvc.perform(post("/api/service-api/bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.bundleId").value(1))
                .andExpect(jsonPath("$.message").value("Bundle created successfully"));
    }

    @Test
    void createBundle_InvalidInput_ReturnsBadRequest() throws Exception {
        BundleInDTO invalid = new BundleInDTO("", true);

        mockMvc.perform(post("/api/service-api/bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBundles_ReturnsList() throws Exception {
        Mockito.when(bundleService.getAllBundles()).thenReturn(Arrays.asList(sampleBundle));

        mockMvc.perform(get("/api/service-api/bundles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].bundleId").value(1));
    }

    @Test
    void getAllBundles_Empty_ReturnsNotFound() throws Exception {
        Mockito.when(bundleService.getAllBundles()).thenThrow(new ResourceNotFoundException("No bundles found"));

        mockMvc.perform(get("/api/service-api/bundles"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBundleById_ReturnsBundle() throws Exception {
        Mockito.when(bundleService.getBundleById(1L)).thenReturn(sampleBundle);

        mockMvc.perform(get("/api/service-api/bundles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bundleName").value("JavaBundle"));
    }

    @Test
    void getBundleById_NotFound_Returns404() throws Exception {
        Mockito.when(bundleService.getBundleById(99L)).thenThrow(new ResourceNotFoundException("Bundle not found"));

        mockMvc.perform(get("/api/service-api/bundles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBundle_ReturnsUpdated() throws Exception {
        UpdateBundleInDTO updateDto = new UpdateBundleInDTO("Updated", true);
        Mockito.when(bundleService.updateBundle(eq(1L), any())).thenReturn(sampleBundle);

        mockMvc.perform(put("/api/service-api/bundles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bundle updated successfully"));
    }

    @Test
    void updateBundle_DuplicateName_ReturnsConflict() throws Exception {
        UpdateBundleInDTO updateDto = new UpdateBundleInDTO("Duplicate", true);
        Mockito.when(bundleService.updateBundle(eq(1L), any()))
                .thenThrow(new ResourceAlreadyExistsException("Already exists"));

        mockMvc.perform(put("/api/service-api/bundles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteBundle_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/service-api/bundles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bundle deleted successfully"));
    }

    @Test
    void deleteBundle_NotFound_Returns404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Not found")).when(bundleService).deleteBundle(999L);

        mockMvc.perform(delete("/api/service-api/bundles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkIfBundleExists_True() throws Exception {
        Mockito.when(bundleService.existsByBundleId(1L)).thenReturn(true);

        mockMvc.perform(get("/api/service-api/bundles/1/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Bundle exists"));
    }

    @Test
    void checkIfBundleExists_False() throws Exception {
        Mockito.when(bundleService.existsByBundleId(10L)).thenReturn(false);

        mockMvc.perform(get("/api/service-api/bundles/10/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false))
                .andExpect(jsonPath("$.message").value("Bundle does not exist"));
    }

    @Test
    void getBundleCount_ReturnsCount() throws Exception {
        Mockito.when(bundleService.countBundles()).thenReturn(5L);

        mockMvc.perform(get("/api/service-api/bundles/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    void getBundleNameById_ReturnsName() throws Exception {
        Mockito.when(bundleService.getBundleNameById(1L)).thenReturn("JavaBundle");

        mockMvc.perform(get("/api/service-api/bundles/1/name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("JavaBundle"));
    }

    @Test
    void getExistingBundleIds_ReturnsExistingIds() throws Exception {
        List<Long> requestIds = Arrays.asList(1L, 2L, 3L);
        List<Long> existingIds = Arrays.asList(1L, 3L);

        Mockito.when(bundleService.findExistingIds(requestIds)).thenReturn(existingIds);

        mockMvc.perform(post("/api/service-api/bundles/existing-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(3));
    }
}
