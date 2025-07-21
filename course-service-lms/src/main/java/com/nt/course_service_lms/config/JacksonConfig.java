package com.nt.course_service_lms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for Jackson JSON serialization and deserialization.
 * <p>
 * This configuration provides a customized ObjectMapper bean that handles
 * Java 8 time types (LocalDateTime, LocalDate, etc.) properly and ensures
 * consistent date/time formatting across the application.
 * </p>
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures a primary ObjectMapper bean for JSON processing.
     * <p>
     * This ObjectMapper is configured with the following features:
     * <ul>
     *   <li>JavaTimeModule registration for handling Java 8 time types</li>
     *   <li>Disables timestamp serialization for dates (uses ISO-8601 format instead)</li>
     * </ul>
     * </p>
     *
     * @return A configured ObjectMapper instance with Java 8 time support
     * and ISO-8601 date formatting
     * @see ObjectMapper
     * @see JavaTimeModule
     * @see SerializationFeature#WRITE_DATES_AS_TIMESTAMPS
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
