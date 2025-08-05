package com.nt.course_service_lms.service.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.dto.inDTO.BulkQuestionRowDTO;
import com.nt.course_service_lms.dto.inDTO.BulkQuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.BulkUploadResultDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.service.BulkUploadService;
import com.nt.course_service_lms.service.QuizQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of BulkUploadService with JSON support
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkUploadServiceImpl implements BulkUploadService {

    private final QuizQuestionService quizQuestionService;
    private final ObjectMapper objectMapper;

    @Override
    public BulkUploadResultDTO bulkUploadQuestions(BulkQuizQuestionInDTO bulkQuizQuestionInDTO) {
        log.info("Starting bulk upload for quiz ID: {}", bulkQuizQuestionInDTO.getQuizId());

        List<BulkQuestionRowDTO> questionRows = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            // Parse file based on type
            String fileName = bulkQuizQuestionInDTO.getFile().getOriginalFilename();
            if (fileName.endsWith(".csv")) {
                questionRows = parseCsvFile(bulkQuizQuestionInDTO.getFile());
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                questionRows = parseExcelFile(bulkQuizQuestionInDTO.getFile());
            } else if (fileName.endsWith(".txt")) {
                questionRows = parseTextFile(bulkQuizQuestionInDTO.getFile());
            } else {
                errors.add("Unsupported file format. Please use CSV, Excel, or TXT files.");
                return new BulkUploadResultDTO(0, 0, 0, errors, new ArrayList<>());
            }

        } catch (Exception e) {
            log.error("Error parsing file: {}", e.getMessage());
            errors.add("Error parsing file: " + e.getMessage());
            return new BulkUploadResultDTO(0, 0, 0, errors, new ArrayList<>());
        }

        // Process questions
        return processQuestions(questionRows, bulkQuizQuestionInDTO.getQuizId(), bulkQuizQuestionInDTO.isSkipErrors());
    }

    private List<BulkQuestionRowDTO> parseCsvFile(MultipartFile file) throws IOException {
        List<BulkQuestionRowDTO> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] columns = parseCSVLineImproved(line);
                if (columns.length >= 7) {
                    BulkQuestionRowDTO question = new BulkQuestionRowDTO();
                    question.setQuestionText(cleanQuotedString(columns[0].trim()));
                    question.setQuestionType(cleanQuotedString(columns[1].trim()));
                    question.setOptions(processJsonField(cleanQuotedString(columns[2].trim())));
                    question.setCorrectAnswer(processJsonField(cleanQuotedString(columns[3].trim())));

                    try {
                        question.setPoints(new BigDecimal(cleanQuotedString(columns[4].trim())));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid points value: " + columns[4].trim());
                    }

                    question.setExplanation(cleanQuotedString(columns[5].trim()));
                    question.setRequired(Boolean.parseBoolean(cleanQuotedString(columns[6].trim())));

                    questions.add(question);
                }
            }
        }

        return questions;
    }

    private List<BulkQuestionRowDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<BulkQuestionRowDTO> questions = new ArrayList<>();

        Workbook workbook = null;
        try {
            // Try to create workbook based on file extension
            if (file.getOriginalFilename().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                workbook = new HSSFWorkbook(file.getInputStream());
            }

            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    BulkQuestionRowDTO question = new BulkQuestionRowDTO();
                    question.setQuestionText(getCellValueAsString(row.getCell(0)));
                    question.setQuestionType(getCellValueAsString(row.getCell(1)));
                    question.setOptions(processJsonField(getCellValueAsString(row.getCell(2))));
                    question.setCorrectAnswer(processJsonField(getCellValueAsString(row.getCell(3))));

                    try {
                        question.setPoints(new BigDecimal(getCellValueAsString(row.getCell(4))));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid points value at row " + (i + 1));
                    }

                    question.setExplanation(getCellValueAsString(row.getCell(5)));
                    question.setRequired(Boolean.parseBoolean(getCellValueAsString(row.getCell(6))));

                    questions.add(question);
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return questions;
    }

    private List<BulkQuestionRowDTO> parseTextFile(MultipartFile file) throws IOException {
        List<BulkQuestionRowDTO> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            BulkQuestionRowDTO currentQuestion = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("QUESTION:")) {
                    // Save previous question if exists
                    if (currentQuestion != null) {
                        questions.add(currentQuestion);
                    }

                    currentQuestion = new BulkQuestionRowDTO();
                    currentQuestion.setQuestionText(line.substring(9).trim());
                    currentQuestion.setRequired(true); // Default
                    currentQuestion.setPoints(new BigDecimal("1.0")); // Default
                    currentQuestion.setOptions("[]"); // Default empty JSON array

                } else if (line.startsWith("TYPE:") && currentQuestion != null) {
                    currentQuestion.setQuestionType(line.substring(5).trim());

                } else if (line.startsWith("OPTIONS:") && currentQuestion != null) {
                    String optionsValue = line.substring(8).trim();
                    // If empty or just whitespace, set as empty JSON array
                    if (optionsValue.isEmpty()) {
                        currentQuestion.setOptions("[]");
                    } else {
                        currentQuestion.setOptions(processJsonField(optionsValue));
                    }

                } else if (line.startsWith("ANSWER:") && currentQuestion != null) {
                    currentQuestion.setCorrectAnswer(processJsonField(line.substring(7).trim()));

                } else if (line.startsWith("POINTS:") && currentQuestion != null) {
                    try {
                        currentQuestion.setPoints(new BigDecimal(line.substring(7).trim()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid points value: " + line.substring(7).trim());
                    }

                } else if (line.startsWith("EXPLANATION:") && currentQuestion != null) {
                    currentQuestion.setExplanation(line.substring(12).trim());

                } else if (line.startsWith("REQUIRED:") && currentQuestion != null) {
                    currentQuestion.setRequired(Boolean.parseBoolean(line.substring(9).trim()));
                }
            }

            // Add the last question
            if (currentQuestion != null) {
                questions.add(currentQuestion);
            }
        }

        return questions;
    }

    private BulkUploadResultDTO processQuestions(List<BulkQuestionRowDTO> questionRows, Long quizId, boolean skipErrors) {
        List<QuizQuestionOutDTO> uploadedQuestions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < questionRows.size(); i++) {
            BulkQuestionRowDTO row = questionRows.get(i);

            try {
                // Validate and convert row to QuizQuestionInDTO
                QuizQuestionInDTO questionInDTO = convertToQuizQuestionInDTO(row, quizId);

                // Create question using existing service
                QuizQuestionOutDTO createdQuestion = quizQuestionService.createQuestion(questionInDTO);
                uploadedQuestions.add(createdQuestion);
                successCount++;

            } catch (Exception e) {
                String error = String.format("Row %d: %s", i + 2, e.getMessage()); // +2 for header and 0-based index
                errors.add(error);
                failureCount++;

                log.warn("Failed to process question at row {}: {}", i + 2, e.getMessage());

                if (!skipErrors) {
                    // If not skipping errors, stop processing
                    break;
                }
            }
        }

        log.info("Bulk upload completed for quiz ID: {}. Success: {}, Failed: {}", quizId, successCount, failureCount);

        return new BulkUploadResultDTO(
                questionRows.size(),
                successCount,
                failureCount,
                errors,
                uploadedQuestions
        );
    }

    private QuizQuestionInDTO convertToQuizQuestionInDTO(BulkQuestionRowDTO row, Long quizId) {
        QuizQuestionInDTO dto = new QuizQuestionInDTO();
        dto.setQuizId(quizId);
        dto.setQuestionText(row.getQuestionText());
        dto.setQuestionType(row.getQuestionType());

        // Validate and set options (should already be in JSON format)
        dto.setOptions(validateAndProcessJsonField(row.getOptions(), "options"));

        // Validate and set correct answer (should already be in JSON format)
        dto.setCorrectAnswer(validateAndProcessJsonField(row.getCorrectAnswer(), "correct answer"));

        dto.setPoints(row.getPoints());
        dto.setExplanation(row.getExplanation());
        dto.setRequired(row.getRequired());

        // Validate required fields
        validateQuestionRow(dto);

        return dto;
    }

    private String validateAndProcessJsonField(String jsonField, String fieldName) {
        if (jsonField == null || jsonField.trim().isEmpty()) {
            return "[]"; // Return empty JSON array for null/empty fields
        }

        try {
            // Validate that it's proper JSON by parsing it
            List<String> parsed = objectMapper.readValue(jsonField, new TypeReference<List<String>>() {
            });
            // Return the original JSON string if parsing was successful
            return jsonField;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format for " + fieldName + ": " + jsonField);
        }
    }

    private void validateQuestionRow(QuizQuestionInDTO dto) {
        if (dto.getQuestionText() == null || dto.getQuestionText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }

        if (dto.getQuestionType() == null || dto.getQuestionType().trim().isEmpty()) {
            throw new IllegalArgumentException("Question type is required");
        }

        if (!Arrays.asList("MCQ_SINGLE", "MCQ_MULTIPLE", "SHORT_ANSWER").contains(dto.getQuestionType())) {
            throw new IllegalArgumentException("Invalid question type: " + dto.getQuestionType());
        }

        if (dto.getCorrectAnswer() == null || dto.getCorrectAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Correct answer is required");
        }

        if (dto.getPoints() == null || dto.getPoints().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Points must be non-negative");
        }

        try {
            // Validate options JSON format
            List<String> options = objectMapper.readValue(dto.getOptions(), new TypeReference<List<String>>() {
            });
            List<String> correctAnswers = objectMapper.readValue(dto.getCorrectAnswer(), new TypeReference<List<String>>() {
            });

            // Validate MCQ questions have options
            if (("MCQ_SINGLE".equals(dto.getQuestionType()) || "MCQ_MULTIPLE".equals(dto.getQuestionType())) &&
                    options.isEmpty()) {
                throw new IllegalArgumentException("Options are required for multiple choice questions");
            }

            // Validate that correct answers exist in options for MCQ questions
            if ("MCQ_SINGLE".equals(dto.getQuestionType()) || "MCQ_MULTIPLE".equals(dto.getQuestionType())) {
                for (String correctAnswer : correctAnswers) {
                    if (!options.contains(correctAnswer)) {
                        throw new IllegalArgumentException("Correct answer '" + correctAnswer + "' not found in options");
                    }
                }
            }

            // Validate MCQ_SINGLE has only one correct answer
            if ("MCQ_SINGLE".equals(dto.getQuestionType()) && correctAnswers.size() != 1) {
                throw new IllegalArgumentException("MCQ_SINGLE questions must have exactly one correct answer");
            }

            // Validate MCQ_MULTIPLE has at least one correct answer
            if ("MCQ_MULTIPLE".equals(dto.getQuestionType()) && correctAnswers.isEmpty()) {
                throw new IllegalArgumentException("MCQ_MULTIPLE questions must have at least one correct answer");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error validating JSON fields: " + e.getMessage());
        }
    }

    @Override
    public boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        return fileName.endsWith(".csv") || fileName.endsWith(".xlsx") ||
                fileName.endsWith(".xls") || fileName.endsWith(".txt");
    }

    // Improved CSV parsing methods
    private String[] parseCSVLineImproved(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Check if this is an escaped quote
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); // Add the escaped quote
                    i++; // Skip the next quote
                } else {
                    inQuotes = !inQuotes; // Toggle quote state
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
    }

    private String cleanQuotedString(String str) {
        if (str == null) {
            return "";
        }

        // Remove outer quotes if present
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length() - 1);
        }

        // Replace escaped quotes with regular quotes
        str = str.replace("\"\"", "\"");

        return str;
    }

    private String processJsonField(String field) {
        if (field == null || field.trim().isEmpty()) {
            return "[]";
        }

        // If it's already a valid JSON string, return as is
        try {
            objectMapper.readTree(field);
            return field;
        } catch (Exception e) {
            // Not valid JSON, might need processing
        }

        // Handle cases where the field might be malformed JSON
        String trimmed = field.trim();

        // If it looks like a JSON array but has unescaped quotes, fix it
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                // Try to parse as is first
                objectMapper.readValue(trimmed, new TypeReference<List<String>>() {
                });
                return trimmed;
            } catch (Exception e) {
                // If parsing fails, it might be because of unescaped quotes
                // This is a fallback - the CSV should ideally have proper JSON
                log.warn("Attempting to fix malformed JSON: {}", trimmed);

                // Simple fix for common cases where quotes are missing around strings
                String fixed = trimmed.replaceAll("([^\\[\\],]+)", "\"$1\"")
                        .replaceAll("\"\"", "\"");

                try {
                    objectMapper.readValue(fixed, new TypeReference<List<String>>() {
                    });
                    return fixed;
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Unable to parse JSON field: " + field);
                }
            }
        }

        return field;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}