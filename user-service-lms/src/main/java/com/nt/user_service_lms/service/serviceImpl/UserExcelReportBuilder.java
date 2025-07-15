package com.nt.user_service_lms.service.serviceImpl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class UserExcelReportBuilder {

    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void buildExcel(Map<String, Object> data, OutputStream outputStream) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Report");
        int rowNum = 0;

        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(boldFont);
        headerStyle.setAlignment(HorizontalAlignment.LEFT);

        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle percentageStyle = workbook.createCellStyle();
        percentageStyle.setDataFormat(dataFormat.getFormat("0.00%"));
        percentageStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setAlignment(HorizontalAlignment.LEFT);

        if (data.containsKey("User Details")) {
            rowNum = addTable(sheet, rowNum, "User Details", (List<Map<String, Object>>) data.get("User Details"), headerStyle, boldStyle, percentageStyle, numberStyle);
            rowNum += 2;
        }

        if (data.containsKey("Enrollment Details")) {
            rowNum = addTable(sheet, rowNum, "Enrollment Details", (List<Map<String, Object>>) data.get("Enrollment Details"), headerStyle, boldStyle, percentageStyle, numberStyle);
            rowNum += 2;
        }

        if (data.containsKey("Progress Details")) {
            rowNum = addTable(sheet, rowNum, "Progress Details", (List<Map<String, Object>>) data.get("Progress Details"), headerStyle, boldStyle, percentageStyle, numberStyle);
            rowNum += 2;
        }

        rowNum = addKpiTables(sheet, rowNum, data, headerStyle, boldStyle, percentageStyle, numberStyle);

        int maxCol = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                maxCol = Math.max(maxCol, row.getLastCellNum());
            }
        }

        for (int i = 0; i < maxCol; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    private int addTable(Sheet sheet, int rowNum, String title, List<Map<String, Object>> rows, CellStyle headerStyle, CellStyle titleStyle, CellStyle percentageStyle, CellStyle numberStyle) {
        if (rows == null || rows.isEmpty()) {
            return rowNum;
        }

        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);

        Map<String, Object> firstRow = rows.get(0);
        Row headerRow = sheet.createRow(rowNum++);
        int col = 0;
        for (String header : firstRow.keySet()) {
            Cell cell = headerRow.createCell(col++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);
        }

        for (Map<String, Object> row : rows) {
            Row dataRow = sheet.createRow(rowNum++);
            col = 0;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                Cell cell = dataRow.createCell(col++);
                setCellValueWithFormat(cell, entry.getValue(), entry.getKey(), percentageStyle, numberStyle);
            }
        }

        return rowNum;
    }

    private int addKpiTables(Sheet sheet, int rowNum, Map<String, Object> data, CellStyle headerStyle, CellStyle titleStyle, CellStyle percentageStyle, CellStyle numberStyle) {
        // User KPIs
        Row userTitleRow = sheet.createRow(rowNum++);
        Cell userTitleCell = userTitleRow.createCell(0);
        userTitleCell.setCellValue("User KPIs");
        userTitleCell.setCellStyle(titleStyle);

        Row userHeaderRow = sheet.createRow(rowNum++);
        userHeaderRow.createCell(0).setCellValue("KPI");
        userHeaderRow.getCell(0).setCellStyle(headerStyle);
        userHeaderRow.createCell(1).setCellValue("Value");
        userHeaderRow.getCell(1).setCellStyle(headerStyle);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!entry.getKey().startsWith("User: ")) {
                continue;
            }
            if (!(entry.getValue() instanceof List)) {
                continue;
            }

            List<?> list = (List<?>) entry.getValue();
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) list.get(0);
                if (map.containsKey("value")) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(entry.getKey());
                    Cell valueCell = row.createCell(1);
                    setCellValueWithFormat(valueCell, map.get("value"), entry.getKey(), percentageStyle, numberStyle);
                }
            }
        }

        rowNum++;

        // Global KPIs
        Row globalTitleRow = sheet.createRow(rowNum++);
        Cell globalTitleCell = globalTitleRow.createCell(0);
        globalTitleCell.setCellValue("Global KPIs");
        globalTitleCell.setCellStyle(titleStyle);

        Row globalHeaderRow = sheet.createRow(rowNum++);
        globalHeaderRow.createCell(0).setCellValue("KPI");
        globalHeaderRow.getCell(0).setCellStyle(headerStyle);
        globalHeaderRow.createCell(1).setCellValue("Value");
        globalHeaderRow.getCell(1).setCellStyle(headerStyle);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!entry.getKey().startsWith("Global: ")) {
                continue;
            }
            if (!(entry.getValue() instanceof List)) {
                continue;
            }

            List<?> list = (List<?>) entry.getValue();
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) list.get(0);
                if (map.containsKey("value")) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(entry.getKey());
                    Cell valueCell = row.createCell(1);
                    setCellValueWithFormat(valueCell, map.get("value"), entry.getKey(), percentageStyle, numberStyle);
                }
            }
        }

        return rowNum;
    }

    private void setCellValueWithFormat(Cell cell, Object value, String columnName, CellStyle percentageStyle, CellStyle numberStyle) {
        if (value == null) {
            cell.setCellValue("N/A");
            return;
        }

        if (value instanceof Number) {
            if (columnName.toLowerCase().contains("percentage")) {
                double val = ((Number) value).doubleValue() / 100.0;
                cell.setCellValue(val);
                cell.setCellStyle(percentageStyle);
            } else {
                cell.setCellValue(((Number) value).doubleValue());
                cell.setCellStyle(numberStyle);
            }
            return;
        }

        if (value instanceof java.sql.Date) {
            cell.setCellValue(((java.sql.Date) value).toLocalDate().format(dateFormatter));
            return;
        }

        if (value instanceof java.sql.Timestamp) {
            cell.setCellValue(((java.sql.Timestamp) value).toLocalDateTime().toLocalDate().format(dateFormatter));
            return;
        }

        cell.setCellValue(String.valueOf(value));
    }
}
