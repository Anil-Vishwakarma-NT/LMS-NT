package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.service.serviceImpl.CoursePDFReportBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoursePDFReportBuilderTest {

    private CoursePDFReportBuilder pdfReportBuilder;

    @BeforeEach
    void setUp() {
        pdfReportBuilder = new CoursePDFReportBuilder();
    }

    @Test
    void buildPdf_withAllSections_shouldGeneratePDFSuccessfully() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("Course Details", List.of(
                Map.of("Course Name", "Java Basics", "Start Date", Date.valueOf("2023-01-01"))
        ));
        data.put("Course Content Details", List.of(
                Map.of("Module", "OOP", "Duration", 45.0)
        ));
        data.put("Course-User Enrollment Details", List.of(
                Map.of("User", "Vaani", "Enrolled On", Timestamp.valueOf("2023-01-15 10:00:00"))
        ));
        data.put("Course: Completion Rate", List.of(
                Map.of("value", 89.34)
        ));
        data.put("Global: Enrollment Count", List.of(
                Map.of("value", 120)
        ));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfReportBuilder.buildPdf(data, out));

        byte[] pdfBytes = out.toByteArray();
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void buildPdf_withEmptySections_shouldStillGeneratePDF() {
        Map<String, Object> data = new HashMap<>();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfReportBuilder.buildPdf(data, out));
        assertTrue(out.toByteArray().length > 0);
    }

    @Test
    void buildPdf_withEmptyKpiLists_shouldNotBreak() {
        Map<String, Object> data = new HashMap<>();
        data.put("Course: Empty KPI", List.of());
        data.put("Global: Empty KPI", List.of());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfReportBuilder.buildPdf(data, out));
    }

    @Test
    void buildPdf_withKpiMissingValue_shouldSkipIt() {
        Map<String, Object> data = new HashMap<>();
        data.put("Course: NoValue", List.of(Map.of("wrongKey", 123)));
        data.put("Global: NoValue", List.of(Map.of("anotherKey", 456)));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfReportBuilder.buildPdf(data, out));
    }

    @Test
    void formatValue_shouldHandleVariousTypes() {
        assertEquals("N/A", callFormat(null, "column"));
        assertEquals("90.00%", callFormat(90.0, "percentage"));
        assertEquals("75", callFormat(75.0, "score"));
        assertEquals("75.5", callFormat(75.5, "score"));
        assertEquals("2023-01-01", callFormat(Date.valueOf("2023-01-01"), "Date"));
        assertEquals("2023-01-01", callFormat(Timestamp.valueOf("2023-01-01 10:00:00"), "Date"));
        assertEquals("Test", callFormat("Test", "any"));
    }

    // Helper to call private formatValue() using reflection
    private String callFormat(Object value, String columnName) {
        try {
            var method = CoursePDFReportBuilder.class.getDeclaredMethod("formatValue", Object.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(pdfReportBuilder, value, columnName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

