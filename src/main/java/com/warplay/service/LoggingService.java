package com.warplay.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    // User action logging
    public void logUserAction(String userId, String action, String details) {
        MDC.put("userId", userId != null ? userId : "anonymous");
        MDC.put("actionType", "USER_ACTION");
        MDC.put("timestamp", LocalDateTime.now().toString());

        logger.info("User Action: {} performed {} - {}", userId, action, details);

        MDC.clear();
    }

    // Database operation logging
    public void logDatabaseOperation(String table, String operation, boolean success, String details) {
        MDC.put("table", table);
        MDC.put("operation", operation);
        MDC.put("operationType", "DATABASE");
        MDC.put("success", String.valueOf(success));

        if (success) {
            logger.info("DB Operation: {} on {} successful - {}", operation, table, details);
        } else {
            logger.error("DB Operation: {} on {} failed - {}", operation, table, details);
        }

        MDC.clear();
    }

    // Error logging with context
    public void logError(String context, Exception e, Map<String, String> additionalContext) {
        MDC.put("errorContext", context);
        MDC.put("operationType", "ERROR");

        if (additionalContext != null) {
            additionalContext.forEach(MDC::put);
        }

        logger.error("Error in {}: {}", context, e.getMessage(), e);

        MDC.clear();
    }

    // Performance logging
    public void logPerformance(String operation, long durationMs, Map<String, String> metadata) {
        MDC.put("operation", operation);
        MDC.put("duration", String.valueOf(durationMs));
        MDC.put("operationType", "PERFORMANCE");

        if (metadata != null) {
            metadata.forEach(MDC::put);
        }

        if (durationMs > 1000) {
            logger.warn("Slow Operation: {} took {}ms", operation, durationMs);
        } else {
            logger.info("Operation: {} completed in {}ms", operation, durationMs);
        }

        MDC.clear();
    }
}