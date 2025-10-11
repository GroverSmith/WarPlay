package com.warplay.controller;

import com.warplay.dto.CreateForceRequest;
import com.warplay.entity.Force;
import com.warplay.service.ForceService;
import com.warplay.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forces")
@CrossOrigin(origins = {"http://localhost:3000", "https://warplay.org"}, allowCredentials = "true")
public class ForceController {
    
    @Autowired
    private ForceService forceService;
    
    @Autowired
    private LoggingService loggingService;
    
    /**
     * Create a new force
     */
    @PostMapping
    public ResponseEntity<?> createForce(
            @RequestBody CreateForceRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            loggingService.log("ForceController.createForce", "Creating force: " + request.getName());
            
            // Extract Google user ID from Authorization header
            String googleUserId = extractUserIdFromAuth(authHeader);
            if (googleUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            Force force = forceService.createForce(request, googleUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(force);
            
        } catch (IllegalArgumentException e) {
            loggingService.log("ForceController.createForce", "Validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            loggingService.log("ForceController.createForce", "Error creating force: " + e.getMessage());
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
            loggingService.log("ForceController.getForcesByClubId", "Fetching forces for club: " + clubId);
            List<Force> forces = forceService.getForcesByClubId(clubId);
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            loggingService.log("ForceController.getForcesByClubId", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to fetch forces: " + e.getMessage()));
        }
    }
    
    /**
     * Get all forces for a crusade
     */
    @GetMapping("/crusade/{crusadeId}")
    public ResponseEntity<?> getForcesByCrusadeId(@PathVariable Long crusadeId) {
        try {
            loggingService.log("ForceController.getForcesByCrusadeId", "Fetching forces for crusade: " + crusadeId);
            List<Force> forces = forceService.getForcesByCrusadeId(crusadeId);
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            loggingService.log("ForceController.getForcesByCrusadeId", "Error: " + e.getMessage());
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
            loggingService.log("ForceController.getForcesByUserId", "Fetching forces for user: " + userId);
            List<Force> forces = forceService.getForcesByUserId(userId);
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            loggingService.log("ForceController.getForcesByUserId", "Error: " + e.getMessage());
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
            loggingService.log("ForceController.getForceById", "Fetching force: " + id);
            return forceService.getForceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            loggingService.log("ForceController.getForceById", "Error: " + e.getMessage());
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
            loggingService.log("ForceController.getAllForces", "Fetching all forces");
            List<Force> forces = forceService.getAllForces();
            return ResponseEntity.ok(forces);
        } catch (Exception e) {
            loggingService.log("ForceController.getAllForces", "Error: " + e.getMessage());
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
            loggingService.log("ForceController.updateForce", "Updating force: " + id);
            
            // Extract Google user ID from Authorization header
            String googleUserId = extractUserIdFromAuth(authHeader);
            if (googleUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            Force force = forceService.updateForce(id, request, googleUserId);
            return ResponseEntity.ok(force);
            
        } catch (RuntimeException e) {
            loggingService.log("ForceController.updateForce", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            loggingService.log("ForceController.updateForce", "Error: " + e.getMessage());
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
            loggingService.log("ForceController.deleteForce", "Deleting force: " + id);
            
            // Extract Google user ID from Authorization header
            String googleUserId = extractUserIdFromAuth(authHeader);
            if (googleUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
            }
            
            forceService.deleteForce(id, googleUserId);
            return ResponseEntity.ok(Map.of("message", "Force deleted successfully"));
            
        } catch (RuntimeException e) {
            loggingService.log("ForceController.deleteForce", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            loggingService.log("ForceController.deleteForce", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete force: " + e.getMessage()));
        }
    }
    
    /**
     * Extract user ID from Authorization header
     */
    private String extractUserIdFromAuth(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

