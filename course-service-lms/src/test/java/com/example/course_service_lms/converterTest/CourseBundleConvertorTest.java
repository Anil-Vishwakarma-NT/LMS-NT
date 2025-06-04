package com.example.course_service_lms.converterTest;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseBundlePostDTO;
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
    void testConvertDTOToEntityForPost() {
        // Prepare input PostDTO
        CourseBundlePostDTO postDTO = new CourseBundlePostDTO();
        postDTO.setCourseBundleId(2L);
        postDTO.setBundleId(102L);
        postDTO.setCourseId(202L);

        // Call method
        CourseBundle result = CourseBundleConvertor.convertDTOToEntityPost(postDTO);

        // Verify result
        assertNotNull(result, "Converted entity should not be null");
        assertEquals(2L, result.getCourseBundleId(), "CourseBundleId should match");
        assertEquals(102L, result.getBundleId(), "BundleId should match");
        assertEquals(202L, result.getCourseId(), "CourseId should match");
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

    @Test
    void testConvertEntityToDTOPost() {
        // Prepare input entity
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(3L);
        courseBundle.setBundleId(103L);
        courseBundle.setCourseId(203L);

        // Call method
        CourseBundlePostDTO result = CourseBundleConvertor.convertEntityToDTOPost(courseBundle);

        // Verify result
        assertNotNull(result, "Converted DTO should not be null");
        assertEquals(3L, result.getCourseBundleId(), "CourseBundleId should match");
        assertEquals(103L, result.getBundleId(), "BundleId should match");
        assertEquals(203L, result.getCourseId(), "CourseId should match");
        }
    }
