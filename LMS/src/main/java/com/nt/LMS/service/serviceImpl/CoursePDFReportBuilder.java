package com.nt.LMS.service.serviceImpl;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class CoursePDFReportBuilder {

    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void buildPdf(Map<String, Object> data, OutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        ImageData imageData = ImageDataFactory.create("C:\\Users\\DELL\\Desktop\\LMS Backend\\LMS-NT\\LMS\\src\\main\\resources\\static\\logo.png");
        Image logo = new Image(imageData);
        logo.scaleToFit(130, 150);
        logo.setFixedPosition(pdf.getDefaultPageSize().getWidth() - 150, pdf.getDefaultPageSize().getTop() - 50);
        document.add(logo);

        document.add(new Paragraph("Course Report")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT));
        document.add(new Paragraph("\n\n"));

        if (data.get("Course Details") != null) {
            addSection(document, "Course Details", (List<Map<String, Object>>) data.get("Course Details"));
            document.add(new Paragraph("\n"));
        }
        if (data.get("Course Content Details") != null) {
            addSection(document, "Course Content Details", (List<Map<String, Object>>) data.get("Course Content Details"));
            document.add(new Paragraph("\n"));
        }
        if (data.get("Course-User Enrollment Details") != null) {
            addSection(document, "Course-User Enrollment Details", (List<Map<String, Object>>) data.get("Course-User Enrollment Details"));
            document.add(new Paragraph("\n"));
        }

        addKpiSection(document, data);

        document.close();
    }

    private void addSection(Document document, String title, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) return;

        document.add(new Paragraph(title).setBold());
        Map<String, Object> firstRow = rows.get(0);
        Table table = new Table(UnitValue.createPercentArray(firstRow.keySet().size())).useAllAvailableWidth();

        firstRow.keySet().forEach(header ->
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setBold().setTextAlignment(TextAlignment.CENTER))));

        for (Map<String, Object> row : rows) {
            row.forEach((colName, value) ->
                    table.addCell(new Cell()
                            .add(new Paragraph(formatValue(value, colName)))
                            .setTextAlignment(TextAlignment.CENTER)));
        }

        document.add(table);
    }

    private void addKpiSection(Document document, Map<String, Object> data) {
        Table courseTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
        Table globalTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
        boolean hasCourseKpi = false;
        boolean hasGlobalKpi = false;

        courseTable.addHeaderCell(new Cell().add(new Paragraph("KPI").setBold().setTextAlignment(TextAlignment.CENTER)));
        courseTable.addHeaderCell(new Cell().add(new Paragraph("Value").setBold().setTextAlignment(TextAlignment.CENTER)));
        globalTable.addHeaderCell(new Cell().add(new Paragraph("KPI").setBold().setTextAlignment(TextAlignment.CENTER)));
        globalTable.addHeaderCell(new Cell().add(new Paragraph("Value").setBold().setTextAlignment(TextAlignment.CENTER)));

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!(value instanceof List)) continue;
            List list = (List) value;
            if (list.isEmpty() || !(list.get(0) instanceof Map)) continue;

            Map map = (Map) list.get(0);
            if (!map.containsKey("value")) continue;

            if (key.startsWith("Course:")) {
                courseTable.addCell(new Cell().add(new Paragraph(key)).setTextAlignment(TextAlignment.CENTER));
                courseTable.addCell(new Cell().add(new Paragraph(formatValue(map.get("value"), key))).setTextAlignment(TextAlignment.CENTER));
                hasCourseKpi = true;
            } else if (key.startsWith("Global:")) {
                globalTable.addCell(new Cell().add(new Paragraph(key)).setTextAlignment(TextAlignment.CENTER));
                globalTable.addCell(new Cell().add(new Paragraph(formatValue(map.get("value"), key))).setTextAlignment(TextAlignment.CENTER));
                hasGlobalKpi = true;
            }
        }

        if (hasCourseKpi) {
            document.add(new Paragraph("Course KPIs").setBold());
            document.add(courseTable);
            document.add(new Paragraph("\n"));
        }
        if (hasGlobalKpi) {
            document.add(new Paragraph("Global KPIs").setBold());
            document.add(globalTable);
            document.add(new Paragraph("\n"));
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
