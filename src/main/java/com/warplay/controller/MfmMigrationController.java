package com.warplay.controller;

import com.warplay.service.MfmMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/mfm")
@CrossOrigin(origins = "*")
public class MfmMigrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmMigrationController.class);
    
    @Autowired
    private MfmMigrationService mfmMigrationService;
    
    /**
     * Manually trigger MFM migration
     */
    @PostMapping("/migrate")
    public ResponseEntity<Map<String, Object>> triggerMigration() {
        logger.info("Manual MFM migration triggered");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            mfmMigrationService.runMigration();
            response.put("success", true);
            response.put("message", "MFM migration completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during manual MFM migration", e);
            response.put("success", false);
            response.put("message", "Error during MFM migration: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get migration status and configuration
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMigrationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("migrationEnabled", true); // Could be made configurable
        status.put("migrationDirectory", "../gameSystems/40K/mfm");
        status.put("overwriteExisting", false);
        status.put("message", "MFM migration system is active");
        
        return ResponseEntity.ok(status);
    }
}
