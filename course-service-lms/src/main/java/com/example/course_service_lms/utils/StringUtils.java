package com.example.course_service_lms.utils;

public class StringUtils {

    public static String toProperCase(String input) {

        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String[] words = input.trim().toLowerCase().split("\\s+");
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
