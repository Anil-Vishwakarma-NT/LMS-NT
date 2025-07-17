package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.converters.BundleConverter;
import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.BundleRepository;
import com.nt.course_service_lms.service.serviceImpl.BundleServiceImpl;
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

import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_ALREADY_EXISTS;
import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NOT_FOUND_BY_ID;
import static com.nt.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.nt.course_service_lms.constants.BundleConstants.NO_BUNDLES_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BundleServiceImplTest {

    @InjectMocks
    private BundleServiceImpl service;

    @Mock
    private BundleRepository repo;

    @Mock
    private BundleConverter converter;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    // Utilities
    private BundleInDTO makeInDTO(String name) {
        BundleInDTO dto = new BundleInDTO(name, true);
        return dto;
    }

    private Bundle makeBundle(Long id, String name) {
        Bundle b = new Bundle();
        b.setBundleId(id);
        b.setBundleName(name);
        b.setActive(true);
        b.setCreatedAt(LocalDateTime.now());
        b.setUpdatedAt(LocalDateTime.now());
        return b;
    }

    @Test
    void createBundle_success() {
        var dto = makeInDTO("New");
        when(repo.existsByBundleName("New")).thenReturn(false);
        var entity = makeBundle(null, "New");
        when(converter.toEntity(dto)).thenReturn(entity);
        var saved = makeBundle(1L, "New");
        when(repo.save(entity)).thenReturn(saved);
        var out = new BundleOutDTO();
        out.setBundleId(1L);
        out.setBundleName("New");
        when(converter.toOutDTO(saved)).thenReturn(out);

        var result = service.createBundle(dto);

        assertThat(result).isEqualTo(out);
        verify(repo).existsByBundleName("New");
        verify(repo).save(entity);
    }

    @Test
    void createBundle_dupName_throws() {
        var dto = makeInDTO("Exists");
        when(repo.existsByBundleName("Exists")).thenReturn(true);

        assertThatThrownBy(() -> service.createBundle(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(String.format(BUNDLE_ALREADY_EXISTS, "Exists"));
    }

    @Test
    void createBundle_generalError_wraps() {
        var dto = makeInDTO("Any");
        when(repo.existsByBundleName("Any")).thenReturn(false);
        when(converter.toEntity(dto)).thenReturn(new Bundle());
        when(repo.save(any())).thenThrow(new RuntimeException("fail"));

        assertThatThrownBy(() -> service.createBundle(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(GENERAL_ERROR);
    }

    @Test
    void getAllBundles_success() {
        Bundle b1 = makeBundle(1L, "A"), b2 = makeBundle(2L, "B");
        when(repo.findAll()).thenReturn(Arrays.asList(b1, b2));
        when(converter.toOutDTO(b1)).thenReturn(new BundleOutDTO(1L, "A", true, b1.getCreatedAt(), b1.getUpdatedAt()));
        when(converter.toOutDTO(b2)).thenReturn(new BundleOutDTO(2L, "B", true, b2.getCreatedAt(), b2.getUpdatedAt()));

        List<BundleOutDTO> list = service.getAllBundles();

        assertThat(list).hasSize(2)
                .extracting(BundleOutDTO::getBundleName)
                .containsExactly("A", "B");
    }

    @Test
    void getAllBundles_empty_throws() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.getAllBundles())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(NO_BUNDLES_FOUND);
    }

    @Test
    void getBundleById_found() {
        var b = makeBundle(5L, "X");
        when(repo.findById(5L)).thenReturn(Optional.of(b));
        when(converter.toOutDTO(b)).thenReturn(new BundleOutDTO(5L, "X", true, b.getCreatedAt(), b.getUpdatedAt()));

        var got = service.getBundleById(5L);
        assertThat(got.getBundleName()).isEqualTo("X");
    }

    @Test
    void getBundleById_notFound() {
        when(repo.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getBundleById(7L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format(BUNDLE_NOT_FOUND_BY_ID, 7L));
    }

    @Test
    void updateBundle_success_sameName() {
        var b = makeBundle(2L, "Same");
        when(repo.findById(2L)).thenReturn(Optional.of(b));
        when(repo.existsByBundleName("Same")).thenReturn(true);
        var update = new UpdateBundleInDTO("Same", false);
        when(converter.updateEntity(b, update)).thenReturn(b);
        var out = new BundleOutDTO();
        out.setBundleId(2L);
        when(repo.save(b)).thenReturn(b);
        when(converter.toOutDTO(b)).thenReturn(out);

        var result = service.updateBundle(2L, update);
        assertThat(result.getBundleId()).isEqualTo(2L);
    }

    @Test
    void updateBundle_success_newName() {
        var b = makeBundle(3L, "Old");
        when(repo.findById(3L)).thenReturn(Optional.of(b));
        when(repo.existsByBundleName("New")).thenReturn(false);
        var update = new UpdateBundleInDTO("New", false);
        when(converter.updateEntity(b, update)).thenReturn(b);
        when(repo.save(b)).thenReturn(b);
        when(converter.toOutDTO(b)).thenReturn(new BundleOutDTO());

        var result = service.updateBundle(3L, update);
        assertThat(result).isNotNull();
    }

    @Test
    void updateBundle_notFound() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        var upd = new UpdateBundleInDTO("New", true);

        assertThatThrownBy(() -> service.updateBundle(9L, upd))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format(BUNDLE_NOT_FOUND_BY_ID, 9L));
    }

    @Test
    void updateBundle_nameExistsOther_throws() {
        var b = makeBundle(4L, "A");
        when(repo.findById(4L)).thenReturn(Optional.of(b));
        when(repo.existsByBundleName("B")).thenReturn(true);
        var upd = new UpdateBundleInDTO("B", true);
        assertThatThrownBy(() -> service.updateBundle(4L, upd))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void updateBundle_generalWraps() {
        var b = makeBundle(6L, "O");
        when(repo.findById(6L)).thenReturn(Optional.of(b));
        var upd = new UpdateBundleInDTO("New", true);
        when(repo.existsByBundleName("New")).thenReturn(false);
        when(repo.save(b)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> service.updateBundle(6L, upd))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(GENERAL_ERROR);
    }

    @Test
    void deleteBundle_success() {
        var b = makeBundle(8L, "D");
        when(repo.findById(8L)).thenReturn(Optional.of(b));
        doNothing().when(repo).delete(b);

        service.deleteBundle(8L);
        verify(repo).delete(b);
    }

    @Test
    void deleteBundle_notFound() {
        when(repo.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteBundle(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format(BUNDLE_NOT_FOUND_BY_ID, 10L));
    }

    @Test
    void existsByBundleId_true() {
        when(repo.existsById(12L)).thenReturn(true);
        assertThat(service.existsByBundleId(12L)).isTrue();
    }

    @Test
    void existsByBundleId_false_onRepoFalse() {
        when(repo.existsById(13L)).thenReturn(false);
        assertThat(service.existsByBundleId(13L)).isFalse();
    }

    @Test
    void existsByBundleId_false_onException() {
        when(repo.existsById(14L)).thenThrow(new RuntimeException());
        assertThat(service.existsByBundleId(14L)).isFalse();
    }

    @Test
    void countBundles_success() {
        when(repo.count()).thenReturn(5L);
        assertThat(service.countBundles()).isEqualTo(5);
    }

    @Test
    void countBundles_returnZeroOnError() {
        when(repo.count()).thenThrow(new RuntimeException());
        assertThat(service.countBundles()).isZero();
    }

    @Test
    void getBundleNameById_success() {
        var b = makeBundle(20L, "NameX");
        when(repo.findById(20L)).thenReturn(Optional.of(b));
        assertThat(service.getBundleNameById(20L)).isEqualTo("NameX");
    }

    @Test
    void getBundleNameById_notFound() {
        when(repo.findById(25L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getBundleNameById(25L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format(BUNDLE_NOT_FOUND_BY_ID, 25L));
    }

    @Test
    void getBundleNameById_wrapsOther() {
        when(repo.findById(30L)).thenThrow(new RuntimeException("fail"));
        assertThatThrownBy(() -> service.getBundleNameById(30L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Something went wrong while fetching bundle name");
    }

    @Test
    void findExistingIds_success() {
        when(repo.findExistingIds(Arrays.asList(1L, 2L, 3L))).thenReturn(Arrays.asList(1L, 3L));
        var out = service.findExistingIds(Arrays.asList(1L, 2L, 3L));
        assertThat(out).containsExactly(1L, 3L);
    }

    @Test
    void findExistingIds_noneFound() {
        when(repo.findExistingIds(any())).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.findExistingIds(Arrays.asList(7L, 8L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No Bundle IDs found");
    }

    @Test
    void findExistingIds_wrapOther() {
        when(repo.findExistingIds(any())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> service.findExistingIds(Arrays.asList(9L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("SERVER ERROR");
    }
}
