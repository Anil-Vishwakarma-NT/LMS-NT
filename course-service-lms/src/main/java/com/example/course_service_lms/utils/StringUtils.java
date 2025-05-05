package com.example.course_service_lms.utils;

import java.util.Locale;

/**
 * Utility class for String operations commonly used in the LMS Course Service.
 * Contains methods to transform and format string input values.
 */
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /**
     * Converts a given string to Proper Case (each word's first letter capitalized).
     * Handles null or empty strings gracefully.
     * <p>
     * Example: "introduction to java" → "Introduction To Java"
     *
     * @param input the input string to convert
     * @return the properly cased version of the input string, or original input if null/empty
     */
    public static String toProperCase(final String input) {

        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String[] words = input.trim().toLowerCase(Locale.ENGLISH).split("\\s+");
        StringBuilder properCaseString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                properCaseString.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return properCaseString.toString().trim();
    }
}
