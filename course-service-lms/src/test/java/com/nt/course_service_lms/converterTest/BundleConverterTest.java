package com.nt.course_service_lms.converterTest;

import com.nt.course_service_lms.converters.BundleConverter;
import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BundleConverterTest {

    private BundleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new BundleConverter();
    }

    @Test
    void testToEntity_withValidInput() {
        BundleInDTO inDTO = new BundleInDTO();
        inDTO.setBundleName("Spring Boot Bundle");
        inDTO.setActive(true);

        Bundle bundle = converter.toEntity(inDTO);

        assertThat(bundle).isNotNull();
        assertThat(bundle.getBundleName()).isEqualTo("Spring Boot Bundle");
        assertThat(bundle.isActive()).isTrue();
        assertThat(bundle.getCreatedAt()).isNotNull();
        assertThat(bundle.getUpdatedAt()).isNotNull();
        assertThat(bundle.getCreatedAt()).isEqualTo(bundle.getUpdatedAt());
    }

    @Test
    void testToEntity_withNullInput() {
        Bundle bundle = converter.toEntity(null);
        assertThat(bundle).isNull();
    }

    @Test
    void testUpdateEntity_withValidInputs() {
        Bundle existing = new Bundle();
        existing.setBundleId(1L);
        existing.setBundleName("Old Name");
        existing.setActive(false);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setUpdatedAt(LocalDateTime.now().minusDays(1));

        UpdateBundleInDTO updateDTO = new UpdateBundleInDTO();
        updateDTO.setBundleName("New Name");
        updateDTO.setActive(true);

        Bundle updated = converter.updateEntity(existing, updateDTO);

        assertThat(updated).isNotNull();
        assertThat(updated.getBundleId()).isEqualTo(1L);
        assertThat(updated.getBundleName()).isEqualTo("New Name");
        assertThat(updated.isActive()).isTrue();
        assertThat(updated.getCreatedAt()).isBefore(updated.getUpdatedAt());
    }

    @Test
    void testUpdateEntity_withNullUpdateDTO() {
        Bundle existing = new Bundle();
        existing.setBundleName("Existing");

        Bundle updated = converter.updateEntity(existing, null);

        assertThat(updated).isSameAs(existing);
        assertThat(updated.getBundleName()).isEqualTo("Existing");
    }

    @Test
    void testUpdateEntity_withNullEntity() {
        UpdateBundleInDTO updateDTO = new UpdateBundleInDTO();
        updateDTO.setBundleName("Should Not Matter");
        updateDTO.setActive(false);

        Bundle updated = converter.updateEntity(null, updateDTO);
        assertThat(updated).isNull();
    }

    @Test
    void testToDTO_withValidEntity() {
        Bundle bundle = new Bundle();
        bundle.setBundleName("DTO Test");
        bundle.setActive(true);

        BundleInDTO dto = converter.toDTO(bundle);

        assertThat(dto).isNotNull();
        assertThat(dto.getBundleName()).isEqualTo("DTO Test");
        assertThat(dto.isActive()).isTrue();
    }

    @Test
    void testToDTO_withNullEntity() {
        BundleInDTO dto = converter.toDTO(null);
        assertThat(dto).isNull();
    }

    @Test
    void testToOutDTO_withValidEntity() {
        LocalDateTime now = LocalDateTime.now();

        Bundle bundle = new Bundle();
        bundle.setBundleId(10L);
        bundle.setBundleName("OutDTO Test");
        bundle.setActive(false);
        bundle.setCreatedAt(now.minusDays(1));
        bundle.setUpdatedAt(now);

        BundleOutDTO outDTO = converter.toOutDTO(bundle);

        assertThat(outDTO).isNotNull();
        assertThat(outDTO.getBundleId()).isEqualTo(10L);
        assertThat(outDTO.getBundleName()).isEqualTo("OutDTO Test");
        assertThat(outDTO.isActive()).isFalse();
        assertThat(outDTO.getCreatedAt()).isEqualTo(now.minusDays(1));
        assertThat(outDTO.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testToOutDTO_withNullEntity() {
        BundleOutDTO outDTO = converter.toOutDTO(null);
        assertThat(outDTO).isNull();
    }

    @Test
    void testUpdateBundleInDTONoArgsConstructor() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO();

        assertThat(dto).isNotNull();
        assertThat(dto.getBundleName()).isNull();
        assertThat(dto.isActive()).isFalse();
    }
}
