package com.warplay.util;

import com.warplay.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Common authentication utilities for consistent user ID extraction across controllers
 */
@Component
public class AuthUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * Extract user ID from Authorization header
     * 
     * @param authHeader The Authorization header value
     * @return User ID if valid token, null otherwise
     */
    public Long extractUserIdFromAuth(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return jwtService.extractUserId(token);
            } catch (Exception e) {
                logger.warn("Error extracting user ID from token: {}", e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Validate authorization header and extract user ID
     * 
     * @param authHeader The Authorization header value
     * @return User ID if valid, null if invalid or missing
     */
    public Long validateAndExtractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Invalid or missing Authorization header");
            return null;
        }
        
        return extractUserIdFromAuth(authHeader);
    }
}
