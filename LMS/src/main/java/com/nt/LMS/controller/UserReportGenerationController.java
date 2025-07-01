package com.nt.LMS.controller;

import com.nt.LMS.dto.outDTO.UserReport;
import com.nt.LMS.service.serviceImpl.UserPdfGeneratorService;
import com.nt.LMS.service.serviceImpl.UserReportGeneration;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/service-api/report")
@RequiredArgsConstructor
public class UserReportGenerationController {

    @Autowired
    private UserReportGeneration userReportGeneration;

    @Autowired
    private UserPdfGeneratorService userPdfGeneratorService;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserReport> getUserReport(@PathVariable("id") Long userId) {
        UserReport report = userReportGeneration.generateUserReport(userId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @GetMapping("/user/{id}/pdf")
    public void getUserReportAsPdf(@PathVariable("id") Long userId, HttpServletResponse response) throws IOException {
        UserReport report = userReportGeneration.generateUserReport(userId);
        if (report == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ByteArrayOutputStream pdfStream = userPdfGeneratorService.generateReportPdf(report);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=user-report-" + userId + ".pdf");
        response.getOutputStream().write(pdfStream.toByteArray());
    }

    @GetMapping("/user/{id}/pdf/download")
    public void downloadUserReportAsPdf(@PathVariable("id") Long userId, HttpServletResponse response) throws IOException {
        UserReport report = userReportGeneration.generateUserReport(userId);
        if (report == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ByteArrayOutputStream pdfStream = userPdfGeneratorService.generateReportPdf(report);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user-report-" + userId + ".pdf");
        response.getOutputStream().write(pdfStream.toByteArray());
    }
}

