package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.service.serviceImpl.CourseExcelReportBuilder;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CourseExcelReportBuilderTest {

    private CourseExcelReportBuilder reportBuilder;

    @BeforeEach
    void setUp() {
        reportBuilder = new CourseExcelReportBuilder();
    }

    @Test
    void buildExcel_withAllDataTypes_shouldGenerateWorkbook() throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();

        List<Map<String, Object>> courseDetails = new ArrayList<>();
        Map<String, Object> courseRow = new LinkedHashMap<>();
        courseRow.put("Course Name", "Java Basics");
        courseRow.put("Percentage Completed", 80);
        courseRow.put("Rating", 4.5);
        courseRow.put("Created Date", new java.sql.Date(System.currentTimeMillis()));
        courseRow.put("Updated Timestamp", new java.sql.Timestamp(System.currentTimeMillis()));
        courseRow.put("Remarks", null);
        courseDetails.add(courseRow);
        data.put("Course Details", courseDetails);

        List<Map<String, Object>> contentDetails = new ArrayList<>();
        Map<String, Object> contentRow = new LinkedHashMap<>();
        contentRow.put("Title", "Introduction");
        contentRow.put("Duration", 45);
        contentDetails.add(contentRow);
        data.put("Course Content Details", contentDetails);

        List<Map<String, Object>> enrollmentDetails = new ArrayList<>();
        Map<String, Object> enrollmentRow = new LinkedHashMap<>();
        enrollmentRow.put("User", "test@example.com");
        enrollmentRow.put("Progress Percentage", 75);
        enrollmentDetails.add(enrollmentRow);
        data.put("Course-User Enrollment Details", enrollmentDetails);

        Map<String, Object> kpiMap = new LinkedHashMap<>();
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("value", 87.5);
        kpiMap.put("Course: Completion Rate", Collections.singletonList(valueMap));
        kpiMap.put("Global: Average Time", Collections.singletonList(valueMap));
        data.putAll(kpiMap);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reportBuilder.buildExcel(data, outputStream);

        assertDoesNotThrow(() -> WorkbookFactory.create(new java.io.ByteArrayInputStream(outputStream.toByteArray())));
    }

//    @Test
//    void buildExcel_withEmptyTables_shouldGenerateMinimalWorkbook() throws Exception {
//        Map<String, Object> data = new HashMap<>();
//        data.put("Course Details", Collections.emptyList());
//        data.put("Course Content Details", Collections.emptyList());
//        data.put("Course-User Enrollment Details", Collections.emptyList());
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        reportBuilder.buildExcel(data, outputStream);
//        assertTrue(outputStream.size() > 0);
//    }
//
//    @Test
//    void buildExcel_withInvalidKpiData_shouldSkipKpiSection() throws Exception {
//        Map<String, Object> data = new HashMap<>();
//        data.put("Course: Invalid KPI", Arrays.asList("not_a_map"));
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        reportBuilder.buildExcel(data, outputStream);
//        assertTrue(outputStream.size() > 0);
//    }
}
