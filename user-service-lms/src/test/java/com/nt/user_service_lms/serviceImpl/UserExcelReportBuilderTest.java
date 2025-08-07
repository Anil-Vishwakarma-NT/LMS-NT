package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.service.serviceImpl.UserExcelReportBuilder;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserExcelReportBuilderTest {

    private final UserExcelReportBuilder builder = new UserExcelReportBuilder();

    @Test
    void testBuildExcelWithAllData() throws Exception {
        Map<String, Object> data = new HashMap<>();

        data.put("User Details", List.of(
                Map.of("Name", "Alice", "Age", 30, "Join Date", java.sql.Date.valueOf("2023-01-01"))
        ));

        data.put("Enrollment Details", List.of(
                Map.of("Course", "Java", "Completion", 80.0)
        ));

        data.put("Progress Details", List.of(
                Map.of("Module", "OOP", "Percentage", 90.0)
        ));

        data.put("User: Completion Rate", List.of(
                Map.of("value", 75.5)
        ));

        data.put("Global: Overall Score", List.of(
                Map.of("value", 88.8)
        ));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildExcel(data, out);

        assertTrue(out.size() > 0);
        var workbook = WorkbookFactory.create(new java.io.ByteArrayInputStream(out.toByteArray()));
        assertEquals("User Report", workbook.getSheetName(0));
        workbook.close();
    }

    @Test
    void testBuildExcelWithEmptyData() throws Exception {
        Map<String, Object> emptyData = new HashMap<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.buildExcel(emptyData, out);
        assertTrue(out.size() > 0);
    }

    @Test
    void testAddTableHandlesNullOrEmptyRows() throws Exception {
        var method = UserExcelReportBuilder.class.getDeclaredMethod("addTable",
                org.apache.poi.ss.usermodel.Sheet.class, int.class, String.class,
                List.class, org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class
        );
        method.setAccessible(true);

        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Test");

        int result = (int) method.invoke(builder, sheet, 0, "Test Table",
                null,
                workbook.createCellStyle(),
                workbook.createCellStyle(),
                workbook.createCellStyle(),
                workbook.createCellStyle()
        );

        assertEquals(0, result);

        result = (int) method.invoke(builder, sheet, 0, "Test Table",
                Collections.emptyList(),
                workbook.createCellStyle(),
                workbook.createCellStyle(),
                workbook.createCellStyle(),
                workbook.createCellStyle()
        );

        assertEquals(0, result);
        workbook.close();
    }

    @Test
    void testSetCellValueWithVariousTypes() throws Exception {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();
        var row = sheet.createRow(0);

        var percentageStyle = workbook.createCellStyle();
        percentageStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        var numberStyle = workbook.createCellStyle();

        var method = UserExcelReportBuilder.class.getDeclaredMethod("setCellValueWithFormat",
                org.apache.poi.ss.usermodel.Cell.class,
                Object.class,
                String.class,
                org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class
        );
        method.setAccessible(true);

        method.invoke(builder, row.createCell(0), null, "any", percentageStyle, numberStyle);
        method.invoke(builder, row.createCell(1), 88.8, "Score", percentageStyle, numberStyle);
        method.invoke(builder, row.createCell(2), 50.0, "Completion Percentage", percentageStyle, numberStyle);
        method.invoke(builder, row.createCell(3), java.sql.Date.valueOf("2024-01-01"), "Date", percentageStyle, numberStyle);
        method.invoke(builder, row.createCell(4), Timestamp.valueOf("2024-01-01 12:00:00"), "Timestamp", percentageStyle, numberStyle);
        method.invoke(builder, row.createCell(5), "Plain Text", "Text", percentageStyle, numberStyle);

        assertEquals("N/A", row.getCell(0).getStringCellValue());
        assertEquals(88.8, row.getCell(1).getNumericCellValue(), 0.001);
        assertEquals(0.5, row.getCell(2).getNumericCellValue(), 0.001);
        assertEquals("2024-01-01", row.getCell(3).getStringCellValue());
        assertEquals("2024-01-01", row.getCell(4).getStringCellValue());
        assertEquals("Plain Text", row.getCell(5).getStringCellValue());

        workbook.close();
    }

    @Test
    void testAddKpiTablesSkipsInvalidEntries() throws Exception {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("KPI");

        Map<String, Object> data = new HashMap<>();
        data.put("User: Incomplete KPI", List.of("Not a map"));
        data.put("Global: Missing Value", List.of(Map.of("label", "Score")));

        var method = UserExcelReportBuilder.class.getDeclaredMethod("addKpiTables",
                org.apache.poi.ss.usermodel.Sheet.class, int.class, Map.class,
                org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class,
                org.apache.poi.ss.usermodel.CellStyle.class
        );
        method.setAccessible(true);

        int finalRow = (int) method.invoke(builder, sheet, 0, data,
                workbook.createCellStyle(), workbook.createCellStyle(), workbook.createCellStyle(), workbook.createCellStyle());

        assertTrue(finalRow > 0);
        workbook.close();
    }
}
