package com.nt.user_service_lms.service.serviceImpl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class UserPDFReportBuilder {

    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void buildPdf(Map<String, Object> data, OutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add logo
        ImageData imageData = ImageDataFactory.create("C:\\Users\\DELL\\Desktop\\LMS-NT\\LMS\\src\\main\\resources\\static\\logo.png");
        Image logo = new Image(imageData);
        logo.scaleToFit(130, 150);

        document.add(new Paragraph("User Report")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT));

        logo.setFixedPosition(
                pdf.getDefaultPageSize().getWidth() - 150,
                pdf.getDefaultPageSize().getTop() - 50);
        document.add(logo);

        document.add(new Paragraph("\n\n"));

        if (data.containsKey("User Details") && data.get("User Details") instanceof List && !((List<?>) data.get("User Details")).isEmpty()) {
            addSection(document, "User Details", (List<Map<String, Object>>) data.get("User Details"));
            document.add(new Paragraph("\n"));
        }

        if (data.containsKey("Enrollment Details") && data.get("Enrollment Details") instanceof List && !((List<?>) data.get("Enrollment Details")).isEmpty()) {
            addSection(document, "Enrollment Details", (List<Map<String, Object>>) data.get("Enrollment Details"));
            document.add(new Paragraph("\n"));
        }

        if (data.containsKey("Progress Details") && data.get("Progress Details") instanceof List && !((List<?>) data.get("Progress Details")).isEmpty()) {
            addSection(document, "Progress Details", (List<Map<String, Object>>) data.get("Progress Details"));
            document.add(new Paragraph("\n"));
        }

        addKpiSection(document, data);

        document.close();
    }

    private void addSection(Document document, String title, List<Map<String, Object>> rows) {
        document.add(new Paragraph(title).setBold());

        if (rows != null && !rows.isEmpty()) {
            Map<String, Object> firstRow = rows.get(0);
            Table table = new Table(UnitValue.createPercentArray(firstRow.keySet().size()))
                    .useAllAvailableWidth();

            // Header
            firstRow.keySet().forEach(header ->
                    table.addHeaderCell(new Cell()
                            .add(new Paragraph(header).setBold().setTextAlignment(TextAlignment.CENTER))));

            // Rows
            for (Map<String, Object> row : rows) {
                row.forEach((colName, value) ->
                        table.addCell(new Cell()
                                .add(new Paragraph(formatValue(value, colName)))
                                .setTextAlignment(TextAlignment.CENTER)));
            }

            document.add(table);
        }
    }

    private void addKpiSection(Document document, Map<String, Object> data) {
        boolean hasUserKpi = false;
        boolean hasGlobalKpi = false;

        Table userTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();
        userTable.addHeaderCell(new Cell().add(new Paragraph("KPI").setBold().setTextAlignment(TextAlignment.CENTER)));
        userTable.addHeaderCell(new Cell().add(new Paragraph("Value").setBold().setTextAlignment(TextAlignment.CENTER)));

        Table globalTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();
        globalTable.addHeaderCell(new Cell().add(new Paragraph("KPI").setBold().setTextAlignment(TextAlignment.CENTER)));
        globalTable.addHeaderCell(new Cell().add(new Paragraph("Value").setBold().setTextAlignment(TextAlignment.CENTER)));

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Only process keys that are explicitly User or Global KPIs
            if (!(key.startsWith("User: ") || key.startsWith("Global: "))) {
                continue;
            }

            if (!(value instanceof List)) continue;
            List<?> list = (List<?>) value;
            if (list.isEmpty() || !(list.get(0) instanceof Map)) continue;
            Map<?, ?> map = (Map<?, ?>) list.get(0);
            if (!map.containsKey("value")) continue;

            if (key.startsWith("User: ")) {
                userTable.addCell(new Cell().add(new Paragraph(key)).setTextAlignment(TextAlignment.CENTER));
                userTable.addCell(new Cell().add(new Paragraph(formatValue(map.get("value"), key))).setTextAlignment(TextAlignment.CENTER));
                hasUserKpi = true;
            } else {
                globalTable.addCell(new Cell().add(new Paragraph(key)).setTextAlignment(TextAlignment.CENTER));
                globalTable.addCell(new Cell().add(new Paragraph(formatValue(map.get("value"), key))).setTextAlignment(TextAlignment.CENTER));
                hasGlobalKpi = true;
            }
        }

        if (hasUserKpi) {
            document.add(new Paragraph("User KPIs").setBold());
            document.add(userTable);
            document.add(new Paragraph("\n"));
        }

        if (hasGlobalKpi) {
            document.add(new Paragraph("Global KPIs").setBold());
            document.add(globalTable);
        }
    }

    private String formatValue(Object value, String columnName) {
        if (value == null) return "N/A";
        if (value instanceof Number) {
            if (columnName.toLowerCase().contains("percentage")) {
                return decimalFormat.format(((Number) value).doubleValue()) + "%";
            } else {
                double d = ((Number) value).doubleValue();
                if (d == Math.floor(d)) {
                    return String.format("%.0f", d);
                } else {
                    return String.valueOf(d);
                }
            }
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().format(dateFormatter);
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate().format(dateFormatter);
        }
        return String.valueOf(value);
    }
}
