package com.warplay.controller;

import com.warplay.service.MfmRawTextParserService;
import com.warplay.service.MfmValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mfm/raw-parser")
@CrossOrigin(origins = "*")
public class MfmRawParserController {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmRawParserController.class);
    
    @Autowired
    private MfmRawTextParserService mfmRawTextParserService;
    
    @Autowired
    private MfmValidationService mfmValidationService;
    
    /**
     * Parse a raw MFM text file and store in database
     */
    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parseMfmFile(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received MFM file for parsing: {}", file.getOriginalFilename());
            
            // Save uploaded file temporarily
            Path tempFile = Files.createTempFile("mfm_", ".txt");
            Files.write(tempFile, file.getBytes());
            
            // Parse the file
            MfmRawTextParserService.MfmParseResult result = mfmRawTextParserService.parseAndStoreMfmFile(tempFile.toString());
            
            // Clean up temp file
            Files.deleteIfExists(tempFile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("version", result.getVersion());
            response.put("unitsCount", result.getUnitsCount());
            response.put("enhancementsCount", result.getEnhancementsCount());
            response.put("factionsCount", result.getFactionsCount());
            response.put("detachmentsCount", result.getDetachmentsCount());
            response.put("message", "MFM file parsed and stored successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error parsing MFM file", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to parse MFM file");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Parse a raw MFM text file from file path (for server-side files)
     */
    @PostMapping("/parse-file")
    public ResponseEntity<Map<String, Object>> parseMfmFileFromPath(@RequestParam("filePath") String filePath) {
        try {
            logger.info("Parsing MFM file from path: {}", filePath);
            
            // Parse the file
            MfmRawTextParserService.MfmParseResult result = mfmRawTextParserService.parseAndStoreMfmFile(filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("version", result.getVersion());
            response.put("unitsCount", result.getUnitsCount());
            response.put("enhancementsCount", result.getEnhancementsCount());
            response.put("factionsCount", result.getFactionsCount());
            response.put("detachmentsCount", result.getDetachmentsCount());
            response.put("message", "MFM file parsed and stored successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error parsing MFM file from path: {}", filePath, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to parse MFM file");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Validate parsed data by comparing with original file
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateMfmData(
            @RequestParam("version") String version,
            @RequestParam("originalFilePath") String originalFilePath) {
        try {
            logger.info("Validating MFM data for version: {} against file: {}", version, originalFilePath);
            
            // Validate the data
            MfmValidationService.MfmValidationResult result = mfmValidationService.validateMfmData(version, originalFilePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("version", result.getVersion());
            response.put("matches", result.getMatches());
            response.put("differencesCount", result.getDifferences().size());
            response.put("isPerfectMatch", result.isPerfectMatch());
            response.put("matchPercentage", result.getMatchPercentage());
            response.put("differences", result.getDifferences());
            response.put("message", "Validation completed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating MFM data", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to validate MFM data");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Regenerate MFM file from database
     */
    @GetMapping("/regenerate/{version}")
    public ResponseEntity<Map<String, Object>> regenerateMfmFile(@PathVariable String version) {
        try {
            logger.info("Regenerating MFM file for version: {}", version);
            
            // Regenerate the file
            String regeneratedContent = mfmValidationService.regenerateMfmFile(version);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("version", version);
            response.put("content", regeneratedContent);
            response.put("message", "MFM file regenerated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error regenerating MFM file for version: {}", version, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to regenerate MFM file");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Generate and save validation report
     */
    @PostMapping("/validation-report")
    public ResponseEntity<Map<String, Object>> generateValidationReport(
            @RequestParam("version") String version,
            @RequestParam("originalFilePath") String originalFilePath,
            @RequestParam("outputPath") String outputPath) {
        try {
            logger.info("Generating validation report for version: {}", version);
            
            // Validate the data
            MfmValidationService.MfmValidationResult result = mfmValidationService.validateMfmData(version, originalFilePath);
            
            // Save the report
            mfmValidationService.saveValidationReport(result, outputPath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("version", result.getVersion());
            response.put("matches", result.getMatches());
            response.put("differencesCount", result.getDifferences().size());
            response.put("isPerfectMatch", result.isPerfectMatch());
            response.put("matchPercentage", result.getMatchPercentage());
            response.put("reportPath", outputPath);
            response.put("message", "Validation report generated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating validation report", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to generate validation report");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get parsing statistics for a version
     */
    @GetMapping("/stats/{version}")
    public ResponseEntity<Map<String, Object>> getParsingStats(@PathVariable String version) {
        try {
            logger.info("Getting parsing stats for version: {}", version);
            
            // This would require additional repository methods to get counts
            // For now, return a placeholder response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("version", version);
            response.put("message", "Stats endpoint - implementation needed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting parsing stats", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to get parsing stats");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
