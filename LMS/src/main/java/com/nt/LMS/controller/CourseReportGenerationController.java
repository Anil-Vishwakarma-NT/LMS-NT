package com.nt.LMS.controller;

import com.nt.LMS.dto.outDTO.CourseReport;
import com.nt.LMS.service.serviceImpl.CourseReportGeneration;
import com.nt.LMS.service.serviceImpl.CoursePdfGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class CourseReportGenerationController {

    @Autowired
    private CourseReportGeneration courseReportGeneration;

    @Autowired
    private CoursePdfGeneratorService coursePdfGeneratorService;

    @GetMapping("/course/{id}")
    public ResponseEntity<CourseReport> getCourseReport(@PathVariable("id") Long courseId) {
        CourseReport report = courseReportGeneration.generateReportForCourse(courseId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @GetMapping("/course/{id}/pdf")
    public void getCourseReportAsPdf(@PathVariable("id") Long courseId, HttpServletResponse response) throws IOException {
        CourseReport report = courseReportGeneration.generateReportForCourse(courseId);
        if (report == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ByteArrayOutputStream pdfStream = coursePdfGeneratorService.generateReportPdf(report);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=course-report-" + courseId + ".pdf");
        response.getOutputStream().write(pdfStream.toByteArray());
    }

    @GetMapping("/course/{id}/pdf/download")
    public void downloadCourseReportAsPdf(@PathVariable("id") Long courseId, HttpServletResponse response) throws IOException {
        CourseReport report = courseReportGeneration.generateReportForCourse(courseId);
        if (report == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ByteArrayOutputStream pdfStream = coursePdfGeneratorService.generateReportPdf(report);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course-report-" + courseId + ".pdf");
        response.getOutputStream().write(pdfStream.toByteArray());
    }
}

