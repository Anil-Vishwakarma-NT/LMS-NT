package com.example.course_service_lms.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Converter
public class JsonConverter implements AttributeConverter<String, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Convert String to JSON if needed
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Convert JSON back to String
        return dbData;
    }
}
