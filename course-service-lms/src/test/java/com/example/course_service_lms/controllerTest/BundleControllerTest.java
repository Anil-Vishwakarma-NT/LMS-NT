package com.example.course_service_lms.controllerTest;

import com.example.course_service_lms.controller.BundleController;
import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.service.BundleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BundleControllerTest {

    @InjectMocks
    private BundleController bundleController;

    @Mock
    private BundleService bundleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBundle() {
        BundleDTO bundleDTO = new BundleDTO("Test Bundle");
        Bundle bundle = new Bundle();
        bundle.setBundleId(1L);
        bundle.setBundleName("Test Bundle");

        when(bundleService.createBundle(bundleDTO)).thenReturn(bundle);

        ResponseEntity<Bundle> response = bundleController.createBundle(bundleDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bundle, response.getBody());
    }

    @Test
    public void testGetAllBundles() {
        List<Bundle> bundleList = Arrays.asList(
                new Bundle(1L, "Bundle 1"),
                new Bundle(2L, "Bundle 2")
        );

        when(bundleService.getAllBundles()).thenReturn(bundleList);

        ResponseEntity<List<Bundle>> response = bundleController.getAllBundles();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bundleList, response.getBody());
    }

    @Test
    public void testGetBundleById() {
        Bundle bundle = new Bundle(1L, "Bundle 1");

        when(bundleService.getBundleById(1L)).thenReturn(Optional.of(bundle));

        ResponseEntity<Optional<Bundle>> response = bundleController.getBundleById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isPresent());
        assertEquals("Bundle 1", response.getBody().get().getBundleName());
    }

    @Test
    public void testUpdateBundle() {
        Long bundleId = 1L;
        BundleDTO inputDTO = new BundleDTO("Updated Bundle");
        BundleDTO returnedDTO = new BundleDTO("Updated Bundle");

        when(bundleService.updateBundle(bundleId, inputDTO)).thenReturn(returnedDTO);

        ResponseEntity<BundleDTO> response = bundleController.updateBundle(bundleId, inputDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Bundle", response.getBody().getBundleName());
    }

    @Test
    public void testDeleteBundle() {
        Long bundleId = 1L;

        doNothing().when(bundleService).deleteBundle(bundleId);

        ResponseEntity<String> response = bundleController.deleteBundle(bundleId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Bundle with ID 1 deleted successfully.", response.getBody());
    }
}
