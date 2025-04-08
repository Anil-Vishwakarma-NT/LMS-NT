package com.example.course_service_lms.serviceImplTest;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.serviceImpl.BundleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BundleServiceImplTest {

    @InjectMocks
    private BundleServiceImpl bundleService;

    @Mock
    private BundleRepository bundleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // CREATE
    @Test
    void testCreateBundle_Success() {
        BundleDTO dto = new BundleDTO("JavaBundle");
        when(bundleRepository.existsByBundleName(dto.getBundleName())).thenReturn(false);

        Bundle savedBundle = new Bundle();
        savedBundle.setBundleId(1L);
        savedBundle.setBundleName(dto.getBundleName());

        when(bundleRepository.save(any(Bundle.class))).thenReturn(savedBundle);

        Bundle result = bundleService.createBundle(dto);

        assertNotNull(result);
        assertEquals("JavaBundle", result.getBundleName());
        verify(bundleRepository, times(1)).save(any(Bundle.class));
    }

    @Test
    void testCreateBundle_DuplicateName_ThrowsException() {
        BundleDTO dto = new BundleDTO("ExistingBundle");

        when(bundleRepository.existsByBundleName(dto.getBundleName())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> bundleService.createBundle(dto));
        verify(bundleRepository, never()).save(any());
    }

    // GET ALL
    @Test
    void testGetAllBundles_Success() {
        List<Bundle> mockList = Arrays.asList(new Bundle(1L, "Java"), new Bundle(2L, "Spring"));
        when(bundleRepository.findAll()).thenReturn(mockList);

        List<Bundle> result = bundleService.getAllBundles();

        assertEquals(2, result.size());
        verify(bundleRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBundles_EmptyList_ThrowsException() {
        when(bundleRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> bundleService.getAllBundles());
    }

    // GET BY ID
    @Test
    void testGetBundleById_Success() {
        Bundle bundle = new Bundle(1L, "JavaBundle");
        when(bundleRepository.findById(1L)).thenReturn(Optional.of(bundle));

        Optional<Bundle> result = bundleService.getBundleById(1L);

        assertTrue(result.isPresent());
        assertEquals("JavaBundle", result.get().getBundleName());
    }

    @Test
    void testGetBundleById_NotFound_ThrowsException() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.getBundleById(1L));
    }

    // UPDATE
    @Test
    void testUpdateBundle_Success() {
        Long bundleId = 1L;
        BundleDTO dto = new BundleDTO("UpdatedName");

        Bundle existing = new Bundle(bundleId, "OldName");
        when(bundleRepository.findById(bundleId)).thenReturn(Optional.of(existing));
        when(bundleRepository.existsByBundleName("UpdatedName")).thenReturn(false);
        when(bundleRepository.save(any(Bundle.class))).thenAnswer(i -> i.getArgument(0));

        BundleDTO updated = bundleService.updateBundle(bundleId, dto);

        assertEquals("UpdatedName", updated.getBundleName());
    }

    @Test
    void testUpdateBundle_NameAlreadyExists_ThrowsException() {
        Long bundleId = 1L;
        BundleDTO dto = new BundleDTO("ExistingBundle");

        Bundle existing = new Bundle(bundleId, "DifferentOldName");
        when(bundleRepository.findById(bundleId)).thenReturn(Optional.of(existing));
        when(bundleRepository.existsByBundleName("ExistingBundle")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> bundleService.updateBundle(bundleId, dto));
    }

    @Test
    void testUpdateBundle_NotFound_ThrowsException() {
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bundleService.updateBundle(1L, new BundleDTO("Any")));
    }

    // DELETE
    @Test
    void testDeleteBundle_Success() {
        Long id = 1L;
        Bundle bundle = new Bundle(id, "ToDelete");
        when(bundleRepository.findById(id)).thenReturn(Optional.of(bundle));

        bundleService.deleteBundle(id);

        verify(bundleRepository, times(1)).delete(bundle);
    }

    @Test
    void testDeleteBundle_NotFound_ThrowsException() {
        when(bundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bundleService.deleteBundle(1L));
    }
}
