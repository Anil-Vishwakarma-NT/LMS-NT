package com.nt.user_service_lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nt.user_service_lms.service.serviceImpl.UserReportKPIQueries;
import com.nt.user_service_lms.service.serviceImpl.UserPDFReportBuilder;
import com.nt.user_service_lms.service.serviceImpl.UserExcelReportBuilder;
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
 * REST controller for generating custom user reports in various formats.
 * Provides endpoints for creating, previewing, and downloading user reports
 * in PDF and Excel formats based on specified KPIs and user data.
 *
 * @author Generated
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/service-api/custom-user-report")
public class UserCustomReportController {

    /**
     * Spring JDBC template for executing database queries.
     * Used to fetch user-related data and KPI metrics from the database.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Builder component for generating PDF reports focused on user data.
     * Handles the creation and formatting of user reports in PDF format.
     */
    @Autowired
    private UserPDFReportBuilder userPDFReportBuilder;

    /**
     * Builder component for generating Excel reports focused on user data.
     * Handles the creation and formatting of user reports in Excel format.
     */
    @Autowired
    private UserExcelReportBuilder userExcelReportBuilder;

    /**
     * Generates and returns a PDF report for preview (inline display).
     * Creates a PDF report based on the provided KPIs and user data,
     * returning it as a byte array for inline viewing in the browser.
     *
     * @param payload the request payload containing KPIs and user information
     * @return ResponseEntity containing the PDF report as byte array with inline content disposition
     * @throws Exception if an error occurs during PDF generation or data fetching
     */
    @PostMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf(@RequestBody final Map<String, Object> payload) throws Exception {
        final Map<String, Object> results = fetchKpiResults(payload);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        userPDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=user_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    /**
     * Generates and returns a PDF report for download.
     * Creates a PDF report based on the provided KPIs and user data,
     * returning it as a downloadable attachment.
     *
     * @param payload the request payload containing KPIs and user information
     * @return ResponseEntity containing the PDF report as byte array with attachment content disposition
     * @throws Exception if an error occurs during PDF generation or data fetching
     */
    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody final Map<String, Object> payload) throws Exception {
        final Map<String, Object> results = fetchKpiResults(payload);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        userPDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    /**
     * Generates and returns an Excel report for download.
     * Creates an Excel spreadsheet report based on the provided KPIs and user data,
     * returning it as a downloadable attachment.
     *
     * @param payload the request payload containing KPIs and user information
     * @return ResponseEntity containing the Excel report as byte array with attachment content disposition
     * @throws Exception if an error occurs during Excel generation or data fetching
     */
    @PostMapping("/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody final Map<String, Object> payload) throws Exception {
        final Map<String, Object> results = fetchKpiResults(payload);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        userExcelReportBuilder.buildExcel(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    /**
     * Fetches KPI results from the database based on the provided payload.
     * Processes the list of KPIs and optional user ID from the payload, executes
     * corresponding database queries, and returns the results in a structured format.
     * Handles both user-specific queries and general queries that don't require a user ID.
     *
     * @param payload the request payload containing KPIs list and optional user ID
     * @return Map containing KPI names as keys and their corresponding data as values
     */
    private Map<String, Object> fetchKpiResults(final Map<String, Object> payload) {
        final List<String> kpis = (List<String>) payload.get("kpis");
        final Long userId = payload.get("userId") != null ? Long.valueOf((String) payload.get("userId")) : null;

        final Map<String, Object> results = new HashMap<>();

        for (final String kpi : kpis) {
            final String query = UserReportKPIQueries.user_kpi_queries.get(kpi);
            if (query != null) {
                final String finalQuery = UserReportKPIQueries.base_query + " " + query;

                if (kpi.equals("User Details")
                        || kpi.equals("Enrollment Details") || kpi.equals("Progress Details")) {

                    final List<Map<String, Object>> list = jdbcTemplate.queryForList(finalQuery, userId);
                    results.put(kpi, list);

                } else if (query.contains("?")) {
                    final Object value = jdbcTemplate.queryForObject(finalQuery, new Object[]{userId}, Object.class);
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
