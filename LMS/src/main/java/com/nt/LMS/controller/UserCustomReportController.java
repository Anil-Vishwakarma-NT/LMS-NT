package com.nt.LMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.nt.LMS.service.serviceImpl.UserReportKPIQueries;
import com.nt.LMS.service.serviceImpl.UserPDFReportBuilder;
import com.nt.LMS.service.serviceImpl.UserExcelReportBuilder;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-api/custom-user-report")
public class UserCustomReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserPDFReportBuilder userPDFReportBuilder;

    @Autowired
    private UserExcelReportBuilder userExcelReportBuilder;

    @PostMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf(@RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> results = fetchKpiResults(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userPDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=user_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> results = fetchKpiResults(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userPDFReportBuilder.buildPdf(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    @PostMapping("/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> results = fetchKpiResults(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userExcelReportBuilder.buildExcel(results, out);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    private Map<String, Object> fetchKpiResults(Map<String, Object> payload) {
        List<String> kpis = (List<String>) payload.get("kpis");
        Long userId = payload.get("userId") != null ? Long.valueOf((String) payload.get("userId")) : null;

        Map<String, Object> results = new HashMap<>();

        for (String kpi : kpis) {
            String query = UserReportKPIQueries.user_kpi_queries.get(kpi);
            if (query != null) {
                String finalQuery = UserReportKPIQueries.base_query + " " + query;

                if (kpi.equals("User Details") ||
                        kpi.equals("Enrollment Details") || kpi.equals("Progress Details")) {

                    List<Map<String, Object>> list = jdbcTemplate.queryForList(finalQuery, userId);
                    results.put(kpi, list);

                } else if (query.contains("?")) {
                    Object value = jdbcTemplate.queryForObject(finalQuery, new Object[]{userId}, Object.class);
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
