package com.nt.LMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.nt.LMS.service.serviceImpl.CourseReportKPIQueries;
import com.nt.LMS.service.serviceImpl.CoursePDFReportBuilder;
import com.nt.LMS.service.serviceImpl.CourseExcelReportBuilder;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/custom-report")
public class CourseCustomReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CoursePDFReportBuilder coursePDFReportBuilder;

    @Autowired
    private CourseExcelReportBuilder courseExcelReportBuilder;

    @PostMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf(@RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> results = fetchKpiResults(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        coursePDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=course_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> results = fetchKpiResults(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        coursePDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    @PostMapping("/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> results = fetchKpiResults(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        courseExcelReportBuilder.buildExcel(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course_report.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    private Map<String, Object> fetchKpiResults(Map<String, Object> payload) {
        List<String> kpis = (List<String>) payload.get("kpis");
        Long courseId = Long.valueOf((String) payload.get("courseId"));

        Map<String, Object> results = new HashMap<>();

        for (String kpi : kpis) {
            String query = CourseReportKPIQueries.course_kpi_queries.get(kpi);
            if (query != null) {
                String finalQuery = CourseReportKPIQueries.base_query + " " + query;

                if (kpi.equals("Course Details") ||
                        kpi.equals("Course Content Details") ||
                        kpi.equals("Course-User Enrollment Details")) {

                    List<Map<String, Object>> list = jdbcTemplate.queryForList(finalQuery, courseId);
                    results.put(kpi, list);

                } else if (query.contains("?")) {
                    Object value = jdbcTemplate.queryForObject(finalQuery, new Object[]{courseId}, Object.class);
                    results.put(kpi, Collections.singletonList(Collections.singletonMap("value", value)));

                } else {
                    Object value = jdbcTemplate.queryForObject(finalQuery, Object.class);
                    results.put(kpi, Collections.singletonList(Collections.singletonMap("value", value)));
                }
            }
        }

        return results;
    }
}
