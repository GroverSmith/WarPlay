package com.warplay.controller;

import com.warplay.dto.CreateUnitRequest;
import com.warplay.dto.UnitResponse;
import com.warplay.service.UnitService;
import com.warplay.util.AuthUtils;
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
@RequestMapping("/api/units")
@CrossOrigin(origins = {"http://localhost:3000", "https://warplay.org"}, allowCredentials = "true")
public class UnitController {
    
    private static final Logger logger = LoggerFactory.getLogger(UnitController.class);
    
    @Autowired
    private UnitService unitService;
    
    @Autowired
    private AuthUtils authUtils;
    
    /**
     * Create a new unit
     */
    @PostMapping
    public ResponseEntity<?> createUnit(
            @RequestBody CreateUnitRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to create unit: {}", request.getName());
            
            // Extract user ID from Authorization header
            Long userId = authUtils.validateAndExtractUserId(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to create unit");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            UnitResponse unit = unitService.createUnit(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(unit);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error creating unit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating unit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to create unit: " + e.getMessage()));
        }
    }
    
    /**
     * Get all units for a force
     */
    @GetMapping("/force/{forceId}")
    public ResponseEntity<?> getUnitsByForceId(@PathVariable Long forceId) {
        try {
            logger.debug("API request to fetch units for force: {}", forceId);
            List<UnitResponse> units = unitService.getUnitsByForceId(forceId);
            return ResponseEntity.ok(units);
        } catch (Exception e) {
            logger.error("Error fetching units for force {}: {}", forceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch units: " + e.getMessage()));
        }
    }
    
    /**
     * Get all units for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUnitsByUserId(@PathVariable Long userId) {
        try {
            logger.debug("API request to fetch units for user: {}", userId);
            List<UnitResponse> units = unitService.getUnitsByUserId(userId);
            return ResponseEntity.ok(units);
        } catch (Exception e) {
            logger.error("Error fetching units for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch units: " + e.getMessage()));
        }
    }
    
    /**
     * Get a unit by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUnitById(@PathVariable Long id) {
        try {
            logger.debug("API request to fetch unit: {}", id);
            return unitService.getUnitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching unit {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch unit: " + e.getMessage()));
        }
    }
    
    /**
     * Get all units
     */
    @GetMapping
    public ResponseEntity<?> getAllUnits() {
        try {
            logger.debug("API request to fetch all units");
            List<UnitResponse> units = unitService.getAllUnits();
            return ResponseEntity.ok(units);
        } catch (Exception e) {
            logger.error("Error fetching all units: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch units: " + e.getMessage()));
        }
    }
    
    /**
     * Update a unit
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUnit(
            @PathVariable Long id,
            @RequestBody CreateUnitRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to update unit: {}", id);
            
            // Extract user ID from Authorization header
            Long userId = authUtils.validateAndExtractUserId(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to update unit: {}", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            UnitResponse unit = unitService.updateUnit(id, request, userId);
            return ResponseEntity.ok(unit);
            
        } catch (RuntimeException e) {
            logger.warn("Error updating unit {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating unit {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to update unit: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a unit (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUnit(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("API request to delete unit: {}", id);
            
            // Extract user ID from Authorization header
            Long userId = authUtils.validateAndExtractUserId(authHeader);
            if (userId == null) {
                logger.warn("Unauthorized attempt to delete unit: {}", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            unitService.deleteUnit(id, userId);
            return ResponseEntity.ok(Map.of("message", "Unit deleted successfully"));
            
        } catch (RuntimeException e) {
            logger.warn("Error deleting unit {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting unit {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete unit: " + e.getMessage()));
        }
    }
    
}
