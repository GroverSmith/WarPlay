package com.warplay.controller;

import com.warplay.dto.ArmyResponse;
import com.warplay.dto.CreateArmyRequest;
import com.warplay.service.ArmyService;
import com.warplay.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/armies")
@CrossOrigin(origins = {"http://localhost:3000", "https://warplay.org"}, allowCredentials = "true")
public class ArmyController {
    
    private static final Logger logger = LoggerFactory.getLogger(ArmyController.class);
    
    @Autowired
    private ArmyService armyService;
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * Create a new army
     */
    @PostMapping
    public ResponseEntity<?> createArmy(
            @RequestBody CreateArmyRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to create army: {}", request.getName());
            
            // Extract user ID from Authorization header
            Long userId = extractUserIdFromAuth(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to create army");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            ArmyResponse army = armyService.createArmy(request, userId);
            logger.info("Army created successfully with ID: {}", army.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(army);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request to create army: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating army", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to create army"));
        }
    }
    
    /**
     * Get army by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getArmy(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to get army: {}", id);
            
            // Extract user ID from Authorization header
            Long userId = extractUserIdFromAuth(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to get army");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            ArmyResponse army = armyService.getArmyById(id, userId);
            return ResponseEntity.ok(army);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request to get army: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting army", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to get army"));
        }
    }
    
    /**
     * Get all armies for a force
     */
    @GetMapping
    public ResponseEntity<?> getArmiesByForce(
            @RequestParam(required = false) Long forceId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to get armies for force: {}", forceId);
            
            // Extract user ID from Authorization header
            Long userId = extractUserIdFromAuth(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to get armies");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            List<ArmyResponse> armies;
            if (forceId != null) {
                armies = armyService.getArmiesByForceId(forceId, userId);
            } else {
                armies = armyService.getArmiesByUserId(userId);
            }
            
            return ResponseEntity.ok(armies);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request to get armies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting armies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to get armies"));
        }
    }
    
    /**
     * Update army
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArmy(
            @PathVariable Long id,
            @RequestBody CreateArmyRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to update army: {}", id);
            
            // Extract user ID from Authorization header
            Long userId = extractUserIdFromAuth(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to update army");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            ArmyResponse army = armyService.updateArmy(id, request, userId);
            logger.info("Army updated successfully: {}", army.getId());
            
            return ResponseEntity.ok(army);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request to update army: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating army", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to update army"));
        }
    }
    
    /**
     * Delete army
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArmy(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to delete army: {}", id);
            
            // Extract user ID from Authorization header
            Long userId = extractUserIdFromAuth(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to delete army");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            armyService.deleteArmy(id, userId);
            logger.info("Army deleted successfully: {}", id);
            
            return ResponseEntity.ok(Map.of("message", "Army deleted successfully"));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request to delete army: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting army", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete army"));
        }
    }
    
    /**
     * Extract user ID from Authorization header
     */
    private Long extractUserIdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        
        try {
            String token = authHeader.substring(7);
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            logger.warn("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }
}
