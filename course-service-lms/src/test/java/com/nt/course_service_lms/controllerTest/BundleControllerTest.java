package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.BundleController;
import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.BundleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BundleControllerTest {

    @InjectMocks
    private BundleController bundleController;

    @Mock
    private BundleService bundleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBundle() {
        BundleInDTO bundleInDTO = new BundleInDTO();
        bundleInDTO.setBundleName("Test Bundle");

        BundleOutDTO bundleOutDTO = new BundleOutDTO();
        bundleOutDTO.setBundleName("Test Bundle");

        when(bundleService.createBundle(bundleInDTO)).thenReturn(bundleOutDTO);

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = bundleController.createBundle(bundleInDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Test Bundle", response.getBody().getData().getBundleName());
    }

    @Test
    void testGetAllBundles() {
        BundleOutDTO b1 = new BundleOutDTO();
        b1.setBundleName("Bundle 1");

        BundleOutDTO b2 = new BundleOutDTO();
        b2.setBundleName("Bundle 2");

        when(bundleService.getAllBundles()).thenReturn(Arrays.asList(b1, b2));

        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> response = bundleController.getAllBundles();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllBundlesEmpty() {
        when(bundleService.getAllBundles()).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> response = bundleController.getAllBundles();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetBundleById() {
        Long id = 1L;

        BundleOutDTO bundle = new BundleOutDTO();
        bundle.setBundleName("Test Bundle");

        when(bundleService.getBundleById(id)).thenReturn(bundle);

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = bundleController.getBundleById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Bundle", response.getBody().getData().getBundleName());
    }

    @Test
    void testUpdateBundle() {
        Long id = 1L;
        UpdateBundleInDTO updateDTO = new UpdateBundleInDTO();
        updateDTO.setBundleName("Updated");

        BundleOutDTO updated = new BundleOutDTO();
        updated.setBundleName("Updated");

        when(bundleService.updateBundle(id, updateDTO)).thenReturn(updated);

        ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> response = bundleController.updateBundle(id, updateDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getData().getBundleName());
    }

    @Test
    void testDeleteBundle() {
        Long id = 1L;

        ResponseEntity<StandardResponseOutDTO<Void>> response = bundleController.deleteBundle(id);

        verify(bundleService, times(1)).deleteBundle(id);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Bundle deleted successfully", response.getBody().getMessage());
    }

    @Test
    void testCheckIfBundleExists_True() {
        Long id = 1L;
        when(bundleService.existsByBundleId(id)).thenReturn(true);

        ResponseEntity<StandardResponseOutDTO<Boolean>> response = bundleController.checkIfBundleExists(id);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData());
        assertEquals("Bundle exists", response.getBody().getMessage());
    }

    @Test
    void testCheckIfBundleExists_False() {
        Long id = 2L;
        when(bundleService.existsByBundleId(id)).thenReturn(false);

        ResponseEntity<StandardResponseOutDTO<Boolean>> response = bundleController.checkIfBundleExists(id);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().getData());
        assertEquals("Bundle does not exist", response.getBody().getMessage());
    }

    @Test
    void testGetBundleCount() {
        when(bundleService.countBundles()).thenReturn(5L);

        ResponseEntity<StandardResponseOutDTO<Long>> response = bundleController.getBundleCount();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5L, response.getBody().getData());
    }

    @Test
    void testGetBundleNameById() {
        Long id = 1L;
        when(bundleService.getBundleNameById(id)).thenReturn("Test Bundle");

        ResponseEntity<StandardResponseOutDTO<String>> response = bundleController.getBundleNameById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Bundle", response.getBody().getData());
    }

    @Test
    void testGetExistingBundleIds() {
        List<Long> input = Arrays.asList(1L, 2L, 3L);
        List<Long> output = Arrays.asList(1L, 3L);

        when(bundleService.findExistingIds(input)).thenReturn(output);

        ResponseEntity<List<Long>> response = bundleController.getExistingBundleIds(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(1L));
        assertFalse(response.getBody().contains(2L));
    }
}
