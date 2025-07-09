package com.nt.user_service_lms.service.serviceImpl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class CourseExcelReportBuilder {

    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void buildExcel(Map<String, Object> data, OutputStream outputStream) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Course Report");
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

        if (data.get("Course Details") != null) {
            rowNum = addTable(sheet, rowNum, "Course Details", (List<Map<String, Object>>) data.get("Course Details"),
                    headerStyle, boldStyle, percentageStyle, numberStyle);
            rowNum += 2;
        }
        if (data.get("Course Content Details") != null) {
            rowNum = addTable(sheet, rowNum, "Course Content Details", (List<Map<String, Object>>) data.get("Course Content Details"),
                    headerStyle, boldStyle, percentageStyle, numberStyle);
            rowNum += 2;
        }
        if (data.get("Course-User Enrollment Details") != null) {
            rowNum = addTable(sheet, rowNum, "Course-User Enrollment Details", (List<Map<String, Object>>) data.get("Course-User Enrollment Details"),
                    headerStyle, boldStyle, percentageStyle, numberStyle);
            rowNum += 2;
        }

        rowNum = addKpiTable(sheet, rowNum, data, headerStyle, boldStyle, percentageStyle, numberStyle);

        for (int i = 0; i < sheet.getRow(1).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    private int addTable(Sheet sheet, int rowNum, String title, List<Map<String, Object>> rows,
                         CellStyle headerStyle, CellStyle titleStyle, CellStyle percentageStyle, CellStyle numberStyle) {
        if (rows == null || rows.isEmpty()) return rowNum;

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

    private int addKpiTable(Sheet sheet, int rowNum, Map<String, Object> data,
                            CellStyle headerStyle, CellStyle titleStyle, CellStyle percentageStyle, CellStyle numberStyle) {
        boolean hasCourseKpi = false, hasGlobalKpi = false;

        // Prepare course KPI table
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("Course:") && isKpiValid(entry)) {
                if (!hasCourseKpi) {
                    Row titleRow = sheet.createRow(rowNum++);
                    Cell titleCell = titleRow.createCell(0);
                    titleCell.setCellValue("Course KPIs");
                    titleCell.setCellStyle(titleStyle);

                    Row headerRow = sheet.createRow(rowNum++);
                    headerRow.createCell(0).setCellValue("KPI");
                    headerRow.getCell(0).setCellStyle(headerStyle);
                    headerRow.createCell(1).setCellValue("Value");
                    headerRow.getCell(1).setCellStyle(headerStyle);

                    hasCourseKpi = true;
                }
                rowNum = addKpiRow(sheet, rowNum, entry, percentageStyle, numberStyle);
            }
        }

        if (hasCourseKpi) rowNum += 2;

        // Prepare global KPI table
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("Global:") && isKpiValid(entry)) {
                if (!hasGlobalKpi) {
                    Row titleRow = sheet.createRow(rowNum++);
                    Cell titleCell = titleRow.createCell(0);
                    titleCell.setCellValue("Global KPIs");
                    titleCell.setCellStyle(titleStyle);

                    Row headerRow = sheet.createRow(rowNum++);
                    headerRow.createCell(0).setCellValue("KPI");
                    headerRow.getCell(0).setCellStyle(headerStyle);
                    headerRow.createCell(1).setCellValue("Value");
                    headerRow.getCell(1).setCellStyle(headerStyle);

                    hasGlobalKpi = true;
                }
                rowNum = addKpiRow(sheet, rowNum, entry, percentageStyle, numberStyle);
            }
        }

        return rowNum;
    }

    private boolean isKpiValid(Map.Entry<String, Object> entry) {
        if (!(entry.getValue() instanceof List)) return false;
        List list = (List) entry.getValue();
        if (list.isEmpty() || !(list.get(0) instanceof Map)) return false;
        Map map = (Map) list.get(0);
        return map.containsKey("value");
    }

    private int addKpiRow(Sheet sheet, int rowNum, Map.Entry<String, Object> entry,
                          CellStyle percentageStyle, CellStyle numberStyle) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(entry.getKey());
        Cell valueCell = row.createCell(1);
        List list = (List) entry.getValue();
        Map map = (Map) list.get(0);
        setCellValueWithFormat(valueCell, map.get("value"), entry.getKey(), percentageStyle, numberStyle);
        return rowNum;
    }

    private void setCellValueWithFormat(Cell cell, Object value, String columnName,
                                        CellStyle percentageStyle, CellStyle numberStyle) {
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
