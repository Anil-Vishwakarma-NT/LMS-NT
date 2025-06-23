package com.nt.LMS.service.serviceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.nt.LMS.dto.outDTO.UserCourseReport;
import com.nt.LMS.dto.outDTO.UserReport;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class UserPdfGeneratorService {

    public ByteArrayOutputStream generateReportPdf(UserReport report) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Title + Logo Header ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new int[]{8, 2});
            headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Phrase titlePhrase = new Phrase("User Report", titleFont);
            PdfPCell titleCell = new PdfPCell(titlePhrase);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(titleCell);

            try {
                Image logo = Image.getInstance("C:\\Users\\DELL\\Desktop\\LMS Backend\\LMS-NT\\LMS\\src\\main\\resources\\static\\logo.png");
                logo.scaleToFit(130, 150);
                PdfPCell imageCell = new PdfPCell(logo, false);
                imageCell.setBorder(Rectangle.NO_BORDER);
                imageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                headerTable.addCell(imageCell);
            } catch (IOException e) {
                PdfPCell emptyCell = new PdfPCell(new Phrase(""));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            headerTable.setSpacingAfter(20);
            document.add(headerTable);

            // --- User Info ---
            Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Name: " + report.getName(), bold));
            document.add(new Paragraph("Username: " + report.getUsername()));
            document.add(new Paragraph("Email: " + report.getEmail()));
            document.add(new Paragraph("Role: " + report.getRole()));
            document.add(new Paragraph("Created At: " + formatDate(report.getCreatedAt().toString())));
            document.add(Chunk.NEWLINE);

            // --- Course Table ---
            PdfPTable table = new PdfPTable(9); // Increased column count
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2, 2, 2, 2, 2, 2, 2});
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableHeader(table, "Course Name", "Level", "Progress (%)", "Last Viewed", "Assigned By", "Assigned At", "Deadline", "Status", "Adherence");

            for (UserCourseReport course : report.getEnrolledCourses()) {
                table.addCell(defaultCell(course.getCourseName()));
                table.addCell(defaultCell(course.getCourseLevel()));
                table.addCell(defaultCell(course.getCourseCompletionPercentage() != null
                        ? String.format("%.2f", course.getCourseCompletionPercentage()) : "N/A"));
                table.addCell(defaultCell(course.getLastViewed() != null
                        ? formatDate(course.getLastViewed().toString()) : "N/A"));
                table.addCell(defaultCell(course.getAssignedBy()));
                table.addCell(defaultCell(course.getAssignedAt() != null
                        ? formatDate(course.getAssignedAt().toString()) : "N/A"));
                table.addCell(defaultCell(course.getDeadline() != null
                        ? formatDate(course.getDeadline().toString()) : "N/A"));

                String status = determineStatus(course.getCourseCompletionPercentage(), course.getLastViewed(), course.getDeadline());
                String adherence = determineAdherence(course.getFirstCompletedAt(), course.getDeadline());

                table.addCell(defaultCell(status));
                table.addCell(defaultCell(adherence));
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out;
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setPadding(6f);
            table.addCell(headerCell);
        }
    }

    private PdfPCell defaultCell(String value) {
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "N/A", dataFont));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        return cell;
    }

    private String formatDate(String isoDateTime) {
        return isoDateTime.length() >= 10 ? isoDateTime.substring(0, 10) : isoDateTime;
    }

    private String determineStatus(Double percentage, LocalDateTime lastViewed, LocalDateTime deadline) {
        if (deadline == null) return "No Deadline";

        LocalDateTime now = LocalDateTime.now();
        boolean deadlinePassed = now.isAfter(deadline);
        boolean lastViewedOnTime = lastViewed != null && (lastViewed.isBefore(deadline) || lastViewed.isEqual(deadline));

        if (percentage == null || percentage == 0.0) {
            return deadlinePassed ? "Completion Failed" : "Not Started";
        }

        if (percentage >= 95.0) {
            return lastViewedOnTime ? "Completed" : "Late Completion";
        }

        return deadlinePassed ? "Completion Failed" : "In Progress";
    }

    private String determineAdherence(LocalDateTime firstCompletedAt, LocalDateTime deadline) {
        if (deadline == null || firstCompletedAt == null) return "N/A";

        return firstCompletedAt.isBefore(deadline) || firstCompletedAt.isEqual(deadline)
                ? "Completed On Time"
                : "Completed Late";
    }
}

