package com.nt.user_service_lms.serviceImpl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.nt.user_service_lms.service.serviceImpl.UserPDFReportBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserPDFReportBuilderTest {

    private UserPDFReportBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new UserPDFReportBuilder();
    }

    @Test
    void testBuildPdfWithAllData() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("User Details", List.of(
                Map.of("Name", "Alice", "Join Date", java.sql.Date.valueOf("2024-01-01"))
        ));
        data.put("Enrollment Details", List.of(
                Map.of("Course", "Java", "Score", 90)
        ));
        data.put("Progress Details", List.of(
                Map.of("Module", "Basics", "Percentage", 85.5)
        ));
        data.put("User: Retention Rate", List.of(Map.of("value", 72.8)));
        data.put("Global: Avg Score", List.of(Map.of("value", 87.2)));

        File logo = new File("user-service-lms/src/main/resources/static/logo.png");
        if (!logo.exists()) {
            // Create dummy logo for test
            logo.getParentFile().mkdirs();
            try (OutputStream os = new FileOutputStream(logo)) {
                os.write(new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}); // Minimal PNG header to make iText happy
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildPdf(data, out);
        assertTrue(out.size() > 0);

        // Read content for verification
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(out.toByteArray())));
        String content = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
        assertTrue(content.contains("User Report"));
        assertTrue(content.contains("User Details"));
        assertTrue(content.contains("Enrollment Details"));
        assertTrue(content.contains("Progress Details"));
        assertTrue(content.contains("User KPIs"));
        assertTrue(content.contains("Global KPIs"));
        pdfDoc.close();
    }

    @Test
    void testBuildPdfWithEmptyData() throws Exception {
        Map<String, Object> emptyData = new HashMap<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildPdf(emptyData, out);
        assertTrue(out.size() > 0);
    }

    @Test
    void testFormatValueForAllTypes() throws Exception {
        var method = UserPDFReportBuilder.class.getDeclaredMethod("formatValue", Object.class, String.class);
        method.setAccessible(true);

        assertEquals("N/A", method.invoke(builder, null, "any"));
        assertEquals("25%", method.invoke(builder, 25.0, "completion percentage"));
        assertEquals("45", method.invoke(builder, 45, "score"));
        assertEquals("2024-01-01", method.invoke(builder, java.sql.Date.valueOf("2024-01-01"), "date"));
        assertEquals("2024-01-01", method.invoke(builder, Timestamp.valueOf("2024-01-01 12:00:00"), "timestamp"));
        assertEquals("Custom Text", method.invoke(builder, "Custom Text", "label"));
    }

    @Test
    void testAddSectionWithEmptyRows() throws Exception {
        var method = UserPDFReportBuilder.class.getDeclaredMethod("addSection", com.itextpdf.layout.Document.class, String.class, List.class);
        method.setAccessible(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdf);

        method.invoke(builder, doc, "Test Section", Collections.emptyList());
        doc.close();
        assertTrue(out.size() > 0);
    }

    @Test
    void testAddKpiSectionWithInvalidEntries() throws Exception {
        var method = UserPDFReportBuilder.class.getDeclaredMethod("addKpiSection", com.itextpdf.layout.Document.class, Map.class);
        method.setAccessible(true);

        Map<String, Object> invalidData = Map.of(
                "User: BadFormat", "String instead of list",
                "Global: EmptyList", List.of(),
                "User: NoValue", List.of(Map.of("name", "test")),
                "Global: GoodValue", List.of(Map.of("value", 88.8))
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdf);

        method.invoke(builder, doc, invalidData);
        doc.close();
        assertTrue(out.size() > 0);
    }
}
