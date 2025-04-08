package com.example.course_service_lms.converterTest;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.converters.CourseBundleConvertor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseBundleConvertorTest {

    @Test
    void testConvertDTOToEntity() {
        // Prepare input DTO
        CourseBundleDTO dto = new CourseBundleDTO();
        dto.setCourseBundleId(1L);
        dto.setBundleId(101L);
        dto.setCourseId(201L);

        // Call method
        CourseBundle result = CourseBundleConvertor.convertDTOToEntity(dto);

        // Verify result
        assertNotNull(result);
        assertEquals(1L, result.getCourseBundleId());
        assertEquals(101L, result.getBundleId());
        assertEquals(201L, result.getCourseId());
    }

    @Test
    void testConvertEntityToDTO() {
        // Prepare input entity
        CourseBundle entity = new CourseBundle();
        entity.setCourseBundleId(1L);
        entity.setBundleId(101L);
        entity.setCourseId(201L);

        // Call method
        CourseBundleDTO result = CourseBundleConvertor.convertEntityToDTO(entity);

        // Verify result
        assertNotNull(result);
        assertEquals(1L, result.getCourseBundleId());
        assertEquals(101L, result.getBundleId());
        assertEquals(201L, result.getCourseId());
    }
}