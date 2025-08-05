package com.nt.course_service_lms.converterTest;

import com.nt.course_service_lms.converters.CourseBundleConvertor;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CourseBundleConvertorTest {

    @Test
    void testConvertDTOToEntity_withValidOutDTO() {
        CourseBundleOutDTO outDTO = new CourseBundleOutDTO();
        outDTO.setCourseBundleId(1L);
        outDTO.setBundleId(2L);
        outDTO.setCourseId(3L);

        CourseBundle entity = CourseBundleConvertor.convertDTOToEntity(outDTO);

        assertThat(entity).isNotNull();
        assertThat(entity.getCourseBundleId()).isEqualTo(1L);
        assertThat(entity.getBundleId()).isEqualTo(2L);
        assertThat(entity.getCourseId()).isEqualTo(3L);
    }

    @Test
    void testConvertDTOToEntityPost_withValidInDTO() {
        CourseBundleInDTO inDTO = new CourseBundleInDTO();
        inDTO.setCourseBundleId(10L);
        inDTO.setBundleId(20L);
        inDTO.setCourseId(30L);
        inDTO.setActive(true);

        CourseBundle entity = CourseBundleConvertor.convertDTOToEntityPost(inDTO);

        assertThat(entity).isNotNull();
        assertThat(entity.getCourseBundleId()).isEqualTo(10L);
        assertThat(entity.getBundleId()).isEqualTo(20L);
        assertThat(entity.getCourseId()).isEqualTo(30L);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void testConvertEntityToDTO_withValidEntity() {
        CourseBundle entity = new CourseBundle();
        entity.setCourseBundleId(11L);
        entity.setBundleId(22L);
        entity.setCourseId(33L);

        CourseBundleOutDTO dto = CourseBundleConvertor.convertEntityToDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getCourseBundleId()).isEqualTo(11L);
        assertThat(dto.getBundleId()).isEqualTo(22L);
        assertThat(dto.getCourseId()).isEqualTo(33L);
    }

    @Test
    void testConvertEntityToDTOPost_withValidEntity() {
        CourseBundle entity = new CourseBundle();
        entity.setCourseBundleId(99L);
        entity.setBundleId(77L);
        entity.setCourseId(55L);

        CourseBundleInDTO dto = CourseBundleConvertor.convertEntityToDTOPost(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getCourseBundleId()).isEqualTo(99L);
        assertThat(dto.getBundleId()).isEqualTo(77L);
        assertThat(dto.getCourseId()).isEqualTo(55L);
    }

//    @Test
//    void testPrivateConstructor_shouldThrowException() {
//        assertThatThrownBy(() -> {
//            var constructor = CourseBundleConvertor.class.getDeclaredConstructor();
//            constructor.setAccessible(true);
//            constructor.newInstance();
//        }).isInstanceOf(UnsupportedOperationException.class)
//                .hasMessage("This is a utility class and cannot be instantiated");
//    }
}
