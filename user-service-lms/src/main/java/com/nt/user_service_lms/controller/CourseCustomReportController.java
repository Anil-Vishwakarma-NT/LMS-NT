package com.nt.user_service_lms.controllerTest;

import com.nt.user_service_lms.service.serviceImpl.CourseExcelReportBuilder;
import com.nt.user_service_lms.service.serviceImpl.CoursePDFReportBuilder;
import com.nt.user_service_lms.service.serviceImpl.CourseReportKPIQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for generating custom course reports in various formats.
 * Provides endpoints for creating, previewing, and downloading course reports
 * in PDF and Excel formats based on specified KPIs and course data.
 *
 * @author Generated
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/service-api/custom-report")
public class CourseCustomReportController {

    /**
     * Spring JDBC template for executing database queries.
     * Used to fetch course-related data and KPI metrics from the database.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Builder component for generating PDF reports.
     * Handles the creation and formatting of course reports in PDF format.
     */
    @Autowired
    private CoursePDFReportBuilder coursePDFReportBuilder;

    /**
     * Builder component for generating Excel reports.
     * Handles the creation and formatting of course reports in Excel format.
     */
    @Autowired
    private CourseExcelReportBuilder courseExcelReportBuilder;

    /**
     * Generates and returns a PDF report for preview (inline display).
     * Creates a PDF report based on the provided KPIs and course data,
     * returning it as a byte array for inline viewing in the browser.
     *
     * @param payload the request payload containing KPIs and course information
     * @return ResponseEntity containing the PDF report as byte array with inline content disposition
     * @throws Exception if an error occurs during PDF generation or data fetching
     */
    @PostMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf(@RequestBody final Map<String, Object> payload) throws Exception {
        final Map<String, Object> results = fetchKpiResults(payload);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        coursePDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=course_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    /**
     * Generates and returns a PDF report for download.
     * Creates a PDF report based on the provided KPIs and course data,
     * returning it as a downloadable attachment.
     *
     * @param payload the request payload containing KPIs and course information
     * @return ResponseEntity containing the PDF report as byte array with attachment content disposition
     * @throws Exception if an error occurs during PDF generation or data fetching
     */
    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody final Map<String, Object> payload) throws Exception {
        final Map<String, Object> results = fetchKpiResults(payload);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        coursePDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    /**
     * Generates and returns an Excel report for download.
     * Creates an Excel spreadsheet report based on the provided KPIs and course data,
     * returning it as a downloadable attachment.
     *
     * @param payload the request payload containing KPIs and course information
     * @return ResponseEntity containing the Excel report as byte array with attachment content disposition
     * @throws Exception if an error occurs during Excel generation or data fetching
     */
    @PostMapping("/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody final Map<String, Object> payload) throws Exception {
        final Map<String, Object> results = fetchKpiResults(payload);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        courseExcelReportBuilder.buildExcel(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course_report.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    /**
     * Fetches KPI results from the database based on the provided payload.
     * Processes the list of KPIs and course ID from the payload, executes
     * corresponding database queries, and returns the results in a structured format.
     *
     * @param payload the request payload containing KPIs list and course ID
     * @return Map containing KPI names as keys and their corresponding data as values
     */
    private Map<String, Object> fetchKpiResults(final Map<String, Object> payload) {
        final List<String> kpis = (List<String>) payload.get("kpis");
        final Long courseId = Long.valueOf((String) payload.get("courseId"));

        final Map<String, Object> results = new HashMap<>();

        for (final String kpi : kpis) {
            final String query = CourseReportKPIQueries.course_kpi_queries.get(kpi);
            if (query != null) {
                final String finalQuery = CourseReportKPIQueries.base_query + " " + query;

                if (kpi.equals("Course Details")
                        || kpi.equals("Course Content Details")
                        || kpi.equals("Course-User Enrollment Details")) {

                    final List<Map<String, Object>> list = jdbcTemplate.queryForList(finalQuery, courseId);
                    results.put(kpi, list);

                } else if (query.contains("?")) {
                    final Object value = jdbcTemplate.queryForObject(finalQuery, new Object[]{courseId}, Object.class);
                    results.put(kpi, Collections.singletonList(Collections.singletonMap("value", value)));

                } else {
                    final Object value = jdbcTemplate.queryForObject(finalQuery, Object.class);
                    results.put(kpi, Collections.singletonList(Collections.singletonMap("value", value)));
                }
            }
        }

        return results;
    }
}
