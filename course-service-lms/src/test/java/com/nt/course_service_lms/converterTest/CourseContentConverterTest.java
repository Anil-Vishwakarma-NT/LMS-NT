//package com.nt.course_service_lms.converterTest;
//
//import com.nt.course_service_lms.converters.CourseContentConverters;
//import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
//import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
//import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
//import com.nt.course_service_lms.entity.CourseContent;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class CourseContentConverterTest {
//
//    @Test
//    void testCourseContentInDtoToEntity_withValidInput() {
//        CourseContentInDTO dto = new CourseContentInDTO();
//        dto.setCourseId(1L);
//        dto.setTitle("Java Basics");
//        dto.setDescription("Intro");
//        dto.setResourceLink("http://link");
//        dto.setActive(true);
//
//        CourseContent entity = CourseContentConverters.courseContentInDtoToEntity(dto);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getCourseId()).isEqualTo(1L);
//        assertThat(entity.getTitle()).isEqualTo("Java Basics");
//        assertThat(entity.getDescription()).isEqualTo("Intro");
//        assertThat(entity.getResourceLink()).isEqualTo("http://link");
//        assertThat(entity.isActive()).isTrue();
//    }
//
//    @Test
//    void testCourseContentInDtoToEntity_withNullInput() {
//        assertThat(CourseContentConverters.courseContentInDtoToEntity(null)).isNull();
//    }
//
//    @Test
//    void testEntityToOutDto_withValidInput() {
//        LocalDateTime now = LocalDateTime.now();
//        CourseContent entity = new CourseContent();
//        entity.setCourseContentId(10L);
//        entity.setCourseId(2L);
//        entity.setTitle("Advanced Java");
//        entity.setDescription("OOP Concepts");
//        entity.setResourceLink("http://example.com");
//        entity.setActive(true);
//        entity.setCreatedAt(now.minusDays(1));
//        entity.setUpdatedAt(now);
//
//        CourseContentOutDTO dto = CourseContentConverters.entityToOutDto(entity);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getCourseContentId()).isEqualTo(10L);
//        assertThat(dto.getCourseId()).isEqualTo(2L);
//        assertThat(dto.getTitle()).isEqualTo("Advanced Java");
//        assertThat(dto.getDescription()).isEqualTo("OOP Concepts");
//        assertThat(dto.getResourceLink()).isEqualTo("http://example.com");
//        assertThat(dto.isActive()).isTrue();
//        assertThat(dto.getCreatedAt()).isEqualTo(now.minusDays(1));
//        assertThat(dto.getUpdatedAt()).isEqualTo(now);
//    }
//
//    @Test
//    void testEntityToOutDto_withNullInput() {
//        assertThat(CourseContentConverters.entityToOutDto(null)).isNull();
//    }
//
//    @Test
//    void testEntityListToOutDtoList_withMultipleValidEntities() {
//        CourseContent content1 = new CourseContent();
//        content1.setCourseContentId(1L);
//        content1.setCourseId(101L);
//        content1.setTitle("T1");
//        content1.setDescription("D1");
//        content1.setResourceLink("link1");
//        content1.setActive(true);
//
//        CourseContent content2 = new CourseContent();
//        content2.setCourseContentId(2L);
//        content2.setCourseId(102L);
//        content2.setTitle("T2");
//        content2.setDescription("D2");
//        content2.setResourceLink("link2");
//        content2.setActive(false);
//
//        List<CourseContentOutDTO> dtos = CourseContentConverters.entityListToOutDtoList(Arrays.asList(content1, content2));
//
//        assertThat(dtos).hasSize(2);
//        assertThat(dtos.get(0).getCourseId()).isEqualTo(101L);
//        assertThat(dtos.get(1).isActive()).isFalse();
//    }
//
//    @Test
//    void testEntityListToOutDtoList_withEmptyList() {
//        List<CourseContentOutDTO> dtos = CourseContentConverters.entityListToOutDtoList(Collections.emptyList());
//        assertThat(dtos).isEmpty();
//    }
//
//    @Test
//    void testEntityListToOutDtoList_withNullInput() {
//        assertThat(CourseContentConverters.entityListToOutDtoList(null)).isNull();
//    }
//
//    @Test
//    void testUpdateEntityFromDto_withValidInputs() {
//        CourseContent existing = new CourseContent();
//        existing.setCourseId(1L);
//        existing.setTitle("Old");
//        existing.setDescription("Old Desc");
//        existing.setResourceLink("old-link");
//        existing.setActive(false);
//
//        UpdateCourseContentInDTO updateDTO = new UpdateCourseContentInDTO();
//        updateDTO.setCourseId(5L);
//        updateDTO.setTitle("New");
//        updateDTO.setDescription("New Desc");
//        updateDTO.setResourceLink("new-link");
//        updateDTO.setActive(true);
//
//        CourseContentConverters.updateEntityFromDto(existing, updateDTO);
//
//        assertThat(existing.getCourseId()).isEqualTo(5L);
//        assertThat(existing.getTitle()).isEqualTo("New");
//        assertThat(existing.getDescription()).isEqualTo("New Desc");
//        assertThat(existing.getResourceLink()).isEqualTo("new-link");
//        assertThat(existing.isActive()).isTrue();
//    }
//
//    @Test
//    void testUpdateEntityFromDto_withNullEntity() {
//        UpdateCourseContentInDTO updateDTO = new UpdateCourseContentInDTO();
//        updateDTO.setCourseId(1L);
//        updateDTO.setTitle("T");
//        updateDTO.setDescription("D");
//        updateDTO.setResourceLink("link");
//        updateDTO.setActive(true);
//
//        CourseContentConverters.updateEntityFromDto(null, updateDTO); // no exception expected
//    }
//
//    @Test
//    void testUpdateEntityFromDto_withNullDTO() {
//        CourseContent entity = new CourseContent();
//        entity.setTitle("Stay Same");
//        CourseContentConverters.updateEntityFromDto(entity, null);
//        assertThat(entity.getTitle()).isEqualTo("Stay Same");
//    }
//
//    @Test
//    void testUpdateDtoToEntity_withValidInput() {
//        UpdateCourseContentInDTO updateDTO = new UpdateCourseContentInDTO();
//        updateDTO.setCourseId(11L);
//        updateDTO.setTitle("Updated");
//        updateDTO.setDescription("Upd Desc");
//        updateDTO.setResourceLink("upd-link");
//        updateDTO.setActive(true);
//
//        CourseContent entity = CourseContentConverters.updateDtoToEntity(updateDTO);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getCourseId()).isEqualTo(11L);
//        assertThat(entity.getTitle()).isEqualTo("Updated");
//        assertThat(entity.getDescription()).isEqualTo("Upd Desc");
//        assertThat(entity.getResourceLink()).isEqualTo("upd-link");
//        assertThat(entity.isActive()).isTrue();
//    }
//
//    @Test
//    void testUpdateDtoToEntity_withNullInput() {
//        assertThat(CourseContentConverters.updateDtoToEntity(null)).isNull();
//    }
//
//    @Test
//    void testCourseContentDtoToCourseContent_deprecatedMethodDelegation() {
//        CourseContentInDTO dto = new CourseContentInDTO();
//        dto.setCourseId(3L);
//        dto.setTitle("Dep");
//        dto.setDescription("Desc");
//        dto.setResourceLink("link");
//        dto.setActive(true);
//
//        CourseContent entity = CourseContentConverters.courseContentDtoToCourseContent(dto);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getCourseId()).isEqualTo(3L);
//        assertThat(entity.getTitle()).isEqualTo("Dep");
//    }
//
//    @Test
//    void testPrivateConstructor_throwsException() throws Exception {
//        var constructor = CourseContentConverters.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//
//        assertThrows(UnsupportedOperationException.class, constructor::newInstance);
//    }
//
//    @Test
//    void testUpdateCourseContentInDTONoArgsConstructor() {
//        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO();
//        assertThat(dto).isNotNull();
//    }
//}
