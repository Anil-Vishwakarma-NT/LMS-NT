package com.nt.LMS.service.serviceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.nt.LMS.dto.outDTO.CourseContentReport;
import com.nt.LMS.dto.outDTO.CourseEnrolledUserReport;
import com.nt.LMS.dto.outDTO.CourseReport;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CoursePdfGeneratorService {

    public ByteArrayOutputStream generateReportPdf(CourseReport report) {
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
            PdfPCell titleCell = new PdfPCell(new Phrase("Course Report", titleFont));
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

            // --- Course Info ---
            Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Course Name: " + report.getName(), bold));
            document.add(new Paragraph("Level: " + report.getLevel()));
            document.add(new Paragraph("Description: " + report.getDescription()));
            document.add(new Paragraph("Created At: " + formatDate(report.getCreatedAt())));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // --- Contents Table ---
            PdfPTable contentsTable = new PdfPTable(3);
            contentsTable.setWidthPercentage(100);
            contentsTable.setWidths(new float[]{3, 4, 2});
            contentsTable.setSpacingBefore(10);
            contentsTable.setSpacingAfter(10);

            addTableHeader(contentsTable, "Content Name", "Description", "Created At");

            for (CourseContentReport content : report.getContents()) {
                contentsTable.addCell(defaultCell(content.getContentName()));
                contentsTable.addCell(defaultCell(content.getContentDescription()));
                contentsTable.addCell(defaultCell(formatDate(content.getContentCreatedAt())));
            }

            document.add(new Paragraph("Course Contents:", bold));
            document.add(contentsTable);
            document.add(Chunk.NEWLINE);

            // --- Enrolled Users Table ---
            PdfPTable usersTable = new PdfPTable(6);
            usersTable.setWidthPercentage(100);
            usersTable.setWidths(new float[]{4, 3, 3, 3, 3, 3});
            usersTable.setSpacingBefore(10);

            addTableHeader(usersTable, "User", "Progress (%)", "Last Viewed", "Deadline", "Status", "Adherence");

            for (CourseEnrolledUserReport user : report.getEnrolledUsers()) {
                usersTable.addCell(defaultCell(user.getUserEnrolled()));
                usersTable.addCell(defaultCell(user.getPercentageCompleted() != null
                        ? String.format("%.2f", user.getPercentageCompleted()) : "N/A"));
                usersTable.addCell(defaultCell(user.getLastViewed() != null
                        ? formatDate(user.getLastViewed()) : "N/A"));
                usersTable.addCell(defaultCell(user.getDeadline() != null
                        ? formatDate(user.getDeadline()) : "N/A"));
                usersTable.addCell(defaultCell(determineStatus(user.getPercentageCompleted(), user.getDeadline())));
                usersTable.addCell(defaultCell(determineAdherence(user.getFirstCompletedAt(), user.getDeadline())));
            }

            document.add(new Paragraph("Enrolled Users:", bold));
            document.add(usersTable);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating course PDF", e);
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
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "N/A"));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        return cell;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().toString() : "N/A";
    }

    private String determineStatus(Double percentage, LocalDateTime deadline) {
        if (deadline == null) return "No Deadline";

        LocalDateTime now = LocalDateTime.now();
        boolean deadlinePassed = now.isAfter(deadline);

        if (percentage == null || percentage == 0.0) {
            return deadlinePassed ? "Completion Failed" : "Not Started";
        }

        if (percentage >= 95.0) {
            return "Completed";
        }

        return deadlinePassed ? "Completion Failed" : "In Progress";
    }

    private String determineAdherence(LocalDateTime firstCompletedAt, LocalDateTime deadline) {
        if (firstCompletedAt == null) return "Not Applicable";
        if (deadline == null) return "Not Applicable";
        return !firstCompletedAt.isAfter(deadline) ? "Adhered" : "Not Adhered";
    }
}

