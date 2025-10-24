package com.warplay.util;

import org.springframework.stereotype.Component;

/**
 * Centralized validation utilities to reduce code duplication across services
 */
@Component
public class ValidationUtils {
    
    // Common validation constants
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_DISCORD_HANDLE_LENGTH = 50;
    public static final int MAX_SHORT_NAME_LENGTH = 20;
    public static final int MAX_PUBLISHER_LENGTH = 100;
    public static final int COUNTRY_CODE_LENGTH = 2;
    
    /**
     * Validate that a string is not null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }
    
    /**
     * Validate that a string is not null or empty (with custom message)
     */
    public static void validateNotEmpty(String value, String fieldName, String customMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(customMessage);
        }
    }
    
    /**
     * Validate string length
     */
    public static void validateLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
    
    /**
     * Validate string length (with custom message)
     */
    public static void validateLength(String value, String fieldName, int maxLength, String customMessage) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(customMessage);
        }
    }
    
    /**
     * Validate that a string is not null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    /**
     * Validate that a string is not null (with custom message)
     */
    public static void validateNotNull(Object value, String fieldName, String customMessage) {
        if (value == null) {
            throw new IllegalArgumentException(customMessage);
        }
    }
    
    /**
     * Validate exact string length
     */
    public static void validateExactLength(String value, String fieldName, int exactLength) {
        if (value != null && value.length() != exactLength) {
            throw new IllegalArgumentException(fieldName + " must be exactly " + exactLength + " characters");
        }
    }
    
    /**
     * Validate exact string length (with custom message)
     */
    public static void validateExactLength(String value, String fieldName, int exactLength, String customMessage) {
        if (value != null && value.length() != exactLength) {
            throw new IllegalArgumentException(customMessage);
        }
    }
    
    /**
     * Validate name field (common pattern: not empty, max 100 chars)
     */
    public static void validateName(String name, String fieldName) {
        validateNotEmpty(name, fieldName);
        validateLength(name, fieldName, MAX_NAME_LENGTH);
    }
    
    /**
     * Validate email field (optional, but if provided, max 100 chars)
     */
    public static void validateEmail(String email, String fieldName) {
        if (email != null && !email.trim().isEmpty()) {
            validateLength(email, fieldName, MAX_EMAIL_LENGTH);
        }
    }
    
    /**
     * Validate country code (exactly 2 characters)
     */
    public static void validateCountryCode(String countryCode, String fieldName) {
        validateNotNull(countryCode, fieldName);
        validateExactLength(countryCode, fieldName, COUNTRY_CODE_LENGTH);
    }
    
    /**
     * Validate short name (optional, but if provided, max 20 chars)
     */
    public static void validateShortName(String shortName, String fieldName) {
        if (shortName != null && !shortName.trim().isEmpty()) {
            validateLength(shortName, fieldName, MAX_SHORT_NAME_LENGTH);
        }
    }
    
    /**
     * Validate publisher (optional, but if provided, max 100 chars)
     */
    public static void validatePublisher(String publisher, String fieldName) {
        if (publisher != null && !publisher.trim().isEmpty()) {
            validateLength(publisher, fieldName, MAX_PUBLISHER_LENGTH);
        }
    }
    
    /**
     * Validate Discord handle (optional, but if provided, max 50 chars)
     */
    public static void validateDiscordHandle(String discordHandle, String fieldName) {
        if (discordHandle != null && !discordHandle.trim().isEmpty()) {
            validateLength(discordHandle, fieldName, MAX_DISCORD_HANDLE_LENGTH);
        }
    }
}
