package com.warplay.controller;

import com.warplay.dto.CreateForceRequest;
import com.warplay.dto.ForceResponse;
import com.warplay.service.ForceService;
import com.warplay.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/forces")
@CrossOrigin(origins = {"http://localhost:3000", "https://warplay.org"}, allowCredentials = "true")
public class ForceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ForceController.class);
    
    @Autowired
    private ForceService forceService;
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * Create a new force
     */
    @PostMapping
    public ResponseEntity<?> createForce(
            @RequestBody CreateForceRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to create force: {}", request.getName());
            
            // Extract Google user ID from Authorization header
            String googleUserId = extractUserIdFromAuth(authHeader);
            if (googleUserId == null) {
                logger.warn("Unauthorized attempt to create force");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            ForceResponse force = forceService.createForce(request, googleUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(force);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error creating force: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating force: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to create force: " + e.getMessage()));
        }
    }
    
    /**
     * Get all forces for a club
     */
    @GetMapping("/club/{clubId}")
    public ResponseEntity<?> getForcesByClubId(@PathVariable Long clubId) {
        try {
            logger.debug("API request to fetch forces for club: {}", clubId);
            List<ForceResponse> forces = forceService.getForcesByClubId(clubId);
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            logger.error("Error fetching forces for club {}: {}", clubId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch forces: " + e.getMessage()));
        }
    }
    
    /**
     * Get all forces for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getForcesByUserId(@PathVariable Long userId) {
        try {
            logger.debug("API request to fetch forces for user: {}", userId);
            List<ForceResponse> forces = forceService.getForcesByUserId(userId);
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            logger.error("Error fetching forces for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch forces: " + e.getMessage()));
        }
    }
    
    /**
     * Get a force by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getForceById(@PathVariable Long id) {
        try {
            logger.debug("API request to fetch force: {}", id);
            return forceService.getForceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching force {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch force: " + e.getMessage()));
        }
    }
    
    /**
     * Get all forces
     */
    @GetMapping
    public ResponseEntity<?> getAllForces() {
        try {
            logger.debug("API request to fetch all forces");
            List<ForceResponse> forces = forceService.getAllForces();
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            logger.error("Error fetching all forces: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch forces: " + e.getMessage()));
        }
    }
    
    /**
     * Update a force
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateForce(
            @PathVariable Long id,
            @RequestBody CreateForceRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to update force: {}", id);
            
            // Extract Google user ID from Authorization header
            String googleUserId = extractUserIdFromAuth(authHeader);
            if (googleUserId == null) {
                logger.warn("Unauthorized attempt to update force: {}", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            ForceResponse force = forceService.updateForce(id, request, googleUserId);
            return ResponseEntity.ok(force);
            
        } catch (RuntimeException e) {
            logger.warn("Error updating force {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating force {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to update force: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a force (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteForce(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to delete force: {}", id);
            
            // Extract Google user ID from Authorization header
            String googleUserId = extractUserIdFromAuth(authHeader);
            if (googleUserId == null) {
                logger.warn("Unauthorized attempt to delete force: {}", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            forceService.deleteForce(id, googleUserId);
            return ResponseEntity.ok(Map.of("message", "Force deleted successfully"));
            
        } catch (RuntimeException e) {
            logger.warn("Error deleting force {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting force {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete force: " + e.getMessage()));
        }
    }
    
    /**
     * Extract Google user ID from Authorization header
     */
    private String extractUserIdFromAuth(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Optional<Map<String, Object>> userInfo = jwtService.validateToken(token);
                if (userInfo.isPresent()) {
                    return (String) userInfo.get().get("googleId");
                }
            } catch (Exception e) {
                logger.warn("Failed to extract user ID from token: {}", e.getMessage());
            }
        }
        return null;
    }
}

