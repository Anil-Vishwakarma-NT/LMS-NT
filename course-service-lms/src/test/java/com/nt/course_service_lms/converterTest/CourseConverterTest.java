package com.nt.course_service_lms.converterTest;

import com.nt.course_service_lms.converters.CourseConvertors;
import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.entity.Course;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourseConverterTest {

//    @Test
//    void testPrivateConstructor_shouldThrowException() throws Exception {
//        var constructor = CourseConvertors.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        assertThrows(UnsupportedOperationException.class, constructor::newInstance);
//    }

    @Test
    void testCourseInDTOToCourse() {
        CourseInDTO dto = new CourseInDTO();
        dto.setTitle("java");
        dto.setOwnerId(1L);
        dto.setDescription("description");
        dto.setCourseLevel("BEGINNER");
        dto.setIsActive(true);

        Course entity = CourseConvertors.courseInDTOToCourse(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Java");
        assertThat(entity.getOwnerId()).isEqualTo(1L);
        assertThat(entity.getDescription()).isEqualTo("Description");
        assertThat(entity.getLevel()).isEqualTo("BEGINNER");
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void testCourseToCourseOutDTO() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("python");
        course.setOwnerId(2L);
        course.setDescription("learn python");
        course.setLevel("INTERMEDIATE");
        course.setActive(true);
        course.setCreatedAt(LocalDateTime.now().minusDays(1));
        course.setUpdatedAt(LocalDateTime.now());

        CourseOutDTO dto = CourseConvertors.courseToCourseOutDTO(course);

        assertThat(dto).isNotNull();
        assertThat(dto.getCourseId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Python");
        assertThat(dto.getOwnerId()).isEqualTo(2L);
        assertThat(dto.getDescription()).isEqualTo("Learn Python");
        assertThat(dto.getLevel()).isEqualTo("INTERMEDIATE");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getCreatedAt()).isNotNull();
        assertThat(dto.getUpdatedAt()).isNotNull();
    }

    @Test
    void testUpdateCourseFromDTO() {
        Course course = new Course();
        course.setTitle("Old Title");
        course.setDescription("Old Desc");
        course.setLevel("BEGINNER");
        course.setOwnerId(1L);
        course.setActive(false);

        UpdateCourseInDTO updateDTO = new UpdateCourseInDTO();
        updateDTO.setTitle("new title");
        updateDTO.setOwnerId(5L);
        updateDTO.setDescription("new desc");
        updateDTO.setCourseLevel("ADVANCED");
        updateDTO.setActive(true);

        CourseConvertors.updateCourseFromDTO(course, updateDTO);

        assertThat(course.getTitle()).isEqualTo("New Title");
        assertThat(course.getDescription()).isEqualTo("New Desc");
        assertThat(course.getLevel()).isEqualTo("ADVANCED");
        assertThat(course.getOwnerId()).isEqualTo(5L);
        assertThat(course.isActive()).isTrue();
    }

    @Test
    void testCourseToCourseInfoOutDTO() {
        Course course = new Course();
        course.setCourseId(7L);
        course.setTitle("React");
        course.setDescription("frontend");
        course.setOwnerId(10L);
        course.setLevel("INTERMEDIATE");
        course.setActive(true);
        course.setUpdatedAt(LocalDateTime.now());

        CourseInfoOutDTO dto = CourseConvertors.courseToCourseInfoOutDTO(course);

        assertThat(dto).isNotNull();
        assertThat(dto.getCourseId()).isEqualTo(7L);
        assertThat(dto.getTitle()).isEqualTo("React");
        assertThat(dto.getDescription()).isEqualTo("frontend");
        assertThat(dto.getOwnerId()).isEqualTo(10L);
        assertThat(dto.getCourseLevel()).isEqualTo("INTERMEDIATE");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getUpdatedAt()).isNotNull();
    }

    @Test
    void testCourseToCourseSummaryOutDTO() {
        Course course = new Course();
        course.setTitle("Angular");
        course.setDescription("Web dev");
        course.setLevel("INTERMEDIATE");
        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now.minusDays(2));
        course.setUpdatedAt(now);

        CourseSummaryOutDTO dto = CourseConvertors.courseToCourseSummaryOutDTO(course);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo("Angular");
        assertThat(dto.getDescription()).isEqualTo("Web dev");
        assertThat(dto.getLevel()).isEqualTo("INTERMEDIATE");
        assertThat(dto.getCreatedAt()).isEqualTo(now.minusDays(2));
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testCourseToCourseInDTO() {
        Course course = new Course();
        course.setTitle("Django");
        course.setOwnerId(22L);
        course.setDescription("Backend Dev");
        course.setLevel("BEGINNER");
        course.setActive(false);

        CourseInDTO dto = CourseConvertors.courseToCourseInDTO(course);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo("Django");
        assertThat(dto.getOwnerId()).isEqualTo(22L);
        assertThat(dto.getDescription()).isEqualTo("Backend Dev");
        assertThat(dto.getCourseLevel()).isEqualTo("BEGINNER");
        assertThat(dto.getIsActive()).isFalse();
    }

    @Test
    void testCourseToUpdateCourseInDTO() {
        Course course = new Course();
        course.setTitle("ML");
        course.setOwnerId(100L);
        course.setDescription("Machine Learning");
        course.setLevel("ADVANCED");
        course.setActive(true);

        UpdateCourseInDTO dto = CourseConvertors.courseToUpdateCourseInDTO(course);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo("ML");
        assertThat(dto.getOwnerId()).isEqualTo(100L);
        assertThat(dto.getDescription()).isEqualTo("Machine Learning");
        assertThat(dto.getCourseLevel()).isEqualTo("ADVANCED");
        assertThat(dto.isActive()).isTrue();
    }
}
