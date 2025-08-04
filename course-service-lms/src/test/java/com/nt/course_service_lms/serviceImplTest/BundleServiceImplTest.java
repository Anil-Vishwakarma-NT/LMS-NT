package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.converters.BundleConverter;
import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.BundleRepository;
import com.nt.course_service_lms.service.serviceImpl.BundleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BundleServiceImplTest {

    @Mock
    private BundleRepository bundleRepository;

    @Mock
    private BundleConverter bundleConverter;

    @InjectMocks
    private BundleServiceImpl bundleService;

    private AutoCloseable closeable;

    private Bundle testBundle;
    private BundleOutDTO testBundleOutDTO;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        testBundle = Bundle.builder()
                .bundleId(1L)
                .bundleName("JavaMaster")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testBundleOutDTO = new BundleOutDTO(
                testBundle.getBundleId(),
                testBundle.getBundleName(),
                testBundle.isActive(),
                testBundle.getCreatedAt(),
                testBundle.getUpdatedAt()
        );
    }

    @Test
    void createBundle_success() {
        BundleInDTO dto = new BundleInDTO("JavaMaster", true);
        when(bundleRepository.existsByBundleName("JavaMaster")).thenReturn(false);
        when(bundleConverter.toEntity(dto)).thenReturn(testBundle);
        when(bundleRepository.save(testBundle)).thenReturn(testBundle);
        when(bundleConverter.toOutDTO(testBundle)).thenReturn(testBundleOutDTO);

        BundleOutDTO result = bundleService.createBundle(dto);

        assertEquals("JavaMaster", result.getBundleName());
        verify(bundleRepository).save(testBundle);
    }

    @Test
    void createBundle_throwsDuplicateException() {
        BundleInDTO dto = new BundleInDTO("JavaMaster", true);
        when(bundleRepository.existsByBundleName("JavaMaster")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> bundleService.createBundle(dto));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(bundleRepository, never()).save(any());
    }

    @Test
    void createBundle_runtimeException() {
        BundleInDTO dto = new BundleInDTO("JavaMaster", true);
        when(bundleRepository.existsByBundleName("JavaMaster")).thenReturn(false);
        when(bundleConverter.toEntity(dto)).thenThrow(new RuntimeException("DB failure"));

        assertThrows(RuntimeException.class, () -> bundleService.createBundle(dto));
    }

    @Test
    void getAllBundles_success() {
        when(bundleRepository.findAll()).thenReturn(Arrays.asList(testBundle));
        when(bundleConverter.toOutDTO(testBundle)).thenReturn(testBundleOutDTO);

        List<BundleOutDTO> result = bundleService.getAllBundles();

        assertEquals(1, result.size());
        assertEquals("JavaMaster", result.get(0).getBundleName());
    }

    @Test
    void getAllBundles_throwsResourceNotFoundException() {
        when(bundleRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.getAllBundles());
    }

    @Test
    void getBundleById_success() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.of(testBundle));
        when(bundleConverter.toOutDTO(testBundle)).thenReturn(testBundleOutDTO);

        BundleOutDTO result = bundleService.getBundleById(1L);

        assertEquals("JavaMaster", result.getBundleName());
    }

    @Test
    void getBundleById_notFound() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.getBundleById(1L));
    }

    @Test
    void updateBundle_success() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("JavaUpdated", true);
        Bundle updatedBundle = Bundle.builder()
                .bundleId(1L)
                .bundleName("JavaUpdated")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(bundleRepository.findById(1L)).thenReturn(Optional.of(testBundle));
        when(bundleRepository.existsByBundleName("JavaUpdated")).thenReturn(false);
        when(bundleConverter.updateEntity(testBundle, dto)).thenReturn(updatedBundle);
        when(bundleRepository.save(updatedBundle)).thenReturn(updatedBundle);
        when(bundleConverter.toOutDTO(updatedBundle)).thenReturn(testBundleOutDTO);

        BundleOutDTO result = bundleService.updateBundle(1L, dto);

        assertNotNull(result);
        verify(bundleRepository).save(updatedBundle);
    }

    @Test
    void updateBundle_throwsDuplicateException() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("AnotherBundle", true);

        when(bundleRepository.findById(1L)).thenReturn(Optional.of(testBundle));
        when(bundleRepository.existsByBundleName("AnotherBundle")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> bundleService.updateBundle(1L, dto));
    }

    @Test
    void updateBundle_notFound() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("JavaUpdated", true);
        when(bundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.updateBundle(1L, dto));
    }

    @Test
    void deleteBundle_success() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.of(testBundle));

        assertDoesNotThrow(() -> bundleService.deleteBundle(1L));
        verify(bundleRepository).delete(testBundle);
    }

    @Test
    void deleteBundle_notFound() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.deleteBundle(1L));
    }

    @Test
    void existsByBundleId_true() {
        when(bundleRepository.existsById(1L)).thenReturn(true);

        assertTrue(bundleService.existsByBundleId(1L));
    }

    @Test
    void existsByBundleId_false() {
        when(bundleRepository.existsById(1L)).thenReturn(false);

        assertFalse(bundleService.existsByBundleId(1L));
    }

    @Test
    void countBundles_success() {
        when(bundleRepository.count()).thenReturn(10L);

        assertEquals(10L, bundleService.countBundles());
    }

    @Test
    void countBundles_exception_returnsZero() {
        when(bundleRepository.count()).thenThrow(new RuntimeException("error"));

        assertEquals(0L, bundleService.countBundles());
    }

    @Test
    void getBundleNameById_success() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.of(testBundle));

        String name = bundleService.getBundleNameById(1L);
        assertEquals("JavaMaster", name);
    }

    @Test
    void getBundleNameById_notFound() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.getBundleNameById(1L));
    }

    @Test
    void findExistingIds_success() {
        List<Long> inputIds = Arrays.asList(1L, 2L);
        List<Long> existingIds = Arrays.asList(1L);

        when(bundleRepository.findExistingIds(inputIds)).thenReturn(existingIds);

        List<Long> result = bundleService.findExistingIds(inputIds);
        assertEquals(1, result.size());
    }

    @Test
    void findExistingIds_empty_throwsException() {
        List<Long> inputIds = Arrays.asList(1L, 2L);
        when(bundleRepository.findExistingIds(inputIds)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.findExistingIds(inputIds));
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

