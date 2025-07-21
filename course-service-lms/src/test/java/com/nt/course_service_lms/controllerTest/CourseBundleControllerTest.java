package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.CourseBundleController;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.service.CourseBundleService;
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

public class CourseBundleControllerTest {

    @InjectMocks
    private CourseBundleController courseBundleController;

    @Mock
    private CourseBundleService courseBundleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourseBundle() {
        CourseBundleInDTO inDTO = new CourseBundleInDTO();
        CourseBundle bundle = new CourseBundle();
        bundle.setCourseBundleId(1L);

        when(courseBundleService.createCourseBundle(inDTO)).thenReturn(bundle);

        ResponseEntity<StandardResponseOutDTO<CourseBundle>> response = courseBundleController.createCourseBundle(inDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getData().getCourseBundleId());
        assertEquals("Course Bundle created successfully.", response.getBody().getMessage());
    }

    @Test
    void testGetAllCourseBundles() {
        CourseBundleOutDTO b1 = new CourseBundleOutDTO();
        CourseBundleOutDTO b2 = new CourseBundleOutDTO();

        when(courseBundleService.getAllCourseBundles()).thenReturn(Arrays.asList(b1, b2));

        ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> response = courseBundleController.getAllCourseBundles();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllCourseBundlesEmpty() {
        when(courseBundleService.getAllCourseBundles()).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<CourseBundleOutDTO>>> response = courseBundleController.getAllCourseBundles();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetCourseBundleById() {
        Long id = 1L;
        CourseBundleOutDTO outDTO = new CourseBundleOutDTO();

        when(courseBundleService.getCourseBundleById(id)).thenReturn(outDTO);

        ResponseEntity<StandardResponseOutDTO<CourseBundleOutDTO>> response = courseBundleController.getCourseBundleById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("retrieved successfully"));
    }

    @Test
    void testDeleteCourseBundle() {
        Long id = 1L;

        ResponseEntity<StandardResponseOutDTO<Void>> response = courseBundleController.deleteCourseBundle(id);

        verify(courseBundleService, times(1)).deleteCourseBundle(id);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Course-bundle with ID " + id + " deleted successfully.", response.getBody().getMessage());
    }

    @Test
    void testUpdateCourseBundle() {
        Long id = 1L;
        UpdateCourseBundleInDTO updateDTO = new UpdateCourseBundleInDTO();

        String serviceResponse = "Updated Successfully";

        when(courseBundleService.updateCourseBundle(id, updateDTO)).thenReturn(serviceResponse);

        ResponseEntity<StandardResponseOutDTO<String>> response = courseBundleController.updateCourseBundle(id, updateDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(serviceResponse, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("updated successfully"));
    }

    @Test
    void testGetAllCoursesByBundleId() {
        Long bundleId = 1L;
        CourseBundle cb1 = new CourseBundle();
        CourseBundle cb2 = new CourseBundle();

        when(courseBundleService.getAllCoursesByBundle(bundleId)).thenReturn(Arrays.asList(cb1, cb2));

        ResponseEntity<StandardResponseOutDTO<List<CourseBundle>>> response = courseBundleController.getAllCoursesByBundleId(bundleId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("retrieved successfully"));
    }

    @Test
    void testGetAllCoursesByBundleIdEmpty() {
        when(courseBundleService.getAllCoursesByBundle(100L)).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<CourseBundle>>> response = courseBundleController.getAllCoursesByBundleId(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetAllBundleInfo() {
        BundleInfoOutDTO dto1 = new BundleInfoOutDTO();
        BundleInfoOutDTO dto2 = new BundleInfoOutDTO();

        when(courseBundleService.getBundlesInfo()).thenReturn(Arrays.asList(dto1, dto2));

        ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> response = courseBundleController.getALlBundleInfo();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("Bundles info retrieved successfully.", response.getBody().getMessage());
    }

    @Test
    void testGetRecentBundles() {
        BundleSummaryOutDTO dto1 = new BundleSummaryOutDTO();

        when(courseBundleService.getRecentBundleSummaries()).thenReturn(Arrays.asList(dto1));

        ResponseEntity<StandardResponseOutDTO<List<BundleSummaryOutDTO>>> response = courseBundleController.getRecentBundles();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Recent bundles retrieved successfully.", response.getBody().getMessage());
    }

    @Test
    void testFindCourseIdsByBundleId() {
        Long bundleId = 1L;
        List<Long> courseIds = Arrays.asList(101L, 102L);

        when(courseBundleService.findCourseIdsByBundleId(bundleId)).thenReturn(courseIds);

        ResponseEntity<List<Long>> response = courseBundleController.findCourseIdsByBundleId(bundleId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(101L));
    }
}
