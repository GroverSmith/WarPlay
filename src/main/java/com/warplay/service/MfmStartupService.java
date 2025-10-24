package com.warplay.service;

import com.warplay.entity.*;
import com.warplay.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MfmStartupService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmStartupService.class);
    
    @Value("${import.mfm.files:}")
    private String importMfmFiles;
    
    @Value("${verify.mfm.files:}")
    private String verifyMfmFiles;
    
    @Autowired
    private MfmVersionRepository mfmVersionRepository;
    
    @Autowired
    private MfmFactionRepository mfmFactionRepository;
    
    @Autowired
    private MfmUnitRepository mfmUnitRepository;
    
    @Autowired
    private MfmUnitVariantRepository mfmUnitVariantRepository;
    
    @Autowired
    private MfmDetachmentRepository mfmDetachmentRepository;
    
    @Autowired
    private MfmEnhancementRepository mfmEnhancementRepository;
    
    @Autowired
    private MfmRawTextParserService mfmRawTextParserService;
    
    @Autowired
    private MfmValidationService mfmValidationService;
    
    @Autowired
    private MfmFeedbackService mfmFeedbackService;
    
    @Autowired
    private MfmVersionManagementService mfmVersionManagementService;
    
    // Patterns for parsing
    private static final Pattern VERSION_PATTERN = Pattern.compile("VERSION\\s+(\\d+\\.\\d+)");
    
    @EventListener(ApplicationReadyEvent.class)
    public void processMfmFilesOnStartup() {
        logger.info("Starting MFM file processing on application startup");
        
        try {
            // Process import files
            if (importMfmFiles != null && !importMfmFiles.trim().isEmpty()) {
                processImportFiles();
            }
            
            // Note: Verification replaced with regenerated file output in quick summary
            
            logger.info("MFM file processing completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during MFM file processing on startup", e);
        }
    }
    
    private void processImportFiles() {
        logger.info("Processing import files: {}", importMfmFiles);
        
        String[] files = importMfmFiles.split(",");
        for (String fileName : files) {
            fileName = fileName.trim();
            if (!fileName.isEmpty()) {
                processImportFile(fileName);
            }
        }
    }
    
    private void processVerificationFiles() {
        logger.info("Processing verification files: {}", verifyMfmFiles);
        
        String[] files = verifyMfmFiles.split(",");
        for (String fileName : files) {
            fileName = fileName.trim();
            if (!fileName.isEmpty()) {
                processVerificationFile(fileName);
            }
        }
    }
    
    private void processImportFile(String fileName) {
        try {
            logger.info("Processing import file: {}", fileName);
            
            // Load file content
            String filePath = "mfm-files/" + fileName + ".txt";
            String content = loadFileContent(filePath);
            
            // Extract version from file
            String version = extractVersion(content);
            if (version == null) {
                logger.warn("Could not extract version from file: {}", fileName);
                return;
            }
            
            logger.info("Extracted version {} from file {}", version, fileName);
            
            // Check if version already exists in database
            Optional<MfmVersion> existingVersion = mfmVersionRepository.findByVersion(version);
            if (existingVersion.isPresent()) {
                logger.info("Version {} already exists, dropping existing data and re-importing", version);
                mfmVersionManagementService.deleteVersion(version);
            }
            
            // Parse and import the file - handle both development and production scenarios
            MfmRawTextParserService.MfmParseResult result;
            try {
                // Try to get file path for development
                String absolutePath = new ClassPathResource(filePath).getFile().getAbsolutePath();
                result = mfmRawTextParserService.parseAndStoreMfmFile(absolutePath);
            } catch (Exception e) {
                // Fallback for production (JAR resources) - create temporary file
                logger.info("File not accessible as regular file, creating temporary file for JAR resource");
                result = parseAndStoreMfmFileFromResource(filePath, content);
            }
            
            logger.info("Successfully imported {} from file {}: {} units, {} enhancements, {} factions, {} detachments",
                       version, fileName, result.getUnitsCount(), result.getEnhancementsCount(), 
                       result.getFactionsCount(), result.getDetachmentsCount());
            
            // Generate quick summary with faction statistics
            mfmFeedbackService.generateQuickSummary(version);
            
        } catch (Exception e) {
            logger.error("Error processing import file: {}", fileName, e);
        }
    }
    
    private void processVerificationFile(String fileName) {
        try {
            logger.info("Processing verification file: {}", fileName);
            
            // Load file content
            String filePath = "mfm-files/" + fileName + ".txt";
            String content = loadFileContent(filePath);
            
            // Extract version from file
            String version = extractVersion(content);
            if (version == null) {
                logger.warn("Could not extract version from file: {}", fileName);
                return;
            }
            
            // Check if version exists in database
            Optional<MfmVersion> existingVersion = mfmVersionRepository.findByVersion(version);
            if (existingVersion.isEmpty()) {
                logger.warn("Version {} not found in database, skipping verification", version);
                return;
            }
            
            // Run validation - handle both development and production scenarios
            MfmValidationService.MfmValidationResult result;
            try {
                // Try to get file path for development
                String originalFilePath = new ClassPathResource(filePath).getFile().getAbsolutePath();
                result = mfmValidationService.validateMfmData(version, originalFilePath);
            } catch (Exception e) {
                // Fallback for production (JAR resources) - create temporary file
                logger.info("File not accessible as regular file, creating temporary file for JAR resource validation");
                Path tempFile = Files.createTempFile("mfm-validation-", ".txt");
                try {
                    Files.write(tempFile, content.getBytes());
                    result = mfmValidationService.validateMfmData(version, tempFile.toString());
                } finally {
                    Files.deleteIfExists(tempFile);
                }
            }
            
            // Generate and save report
            String reportPath = "mfm-validation-report-" + version + "-" + 
                               LocalDateTime.now().toString().replace(":", "-") + ".txt";
            mfmValidationService.saveValidationReport(result, reportPath);
            
            logger.info("Verification completed for version {}: {} matches, {} differences, {:.1f}% match rate",
                       version, result.getMatches(), result.getDifferences().size(), result.getMatchPercentage());
            
            if (result.isPerfectMatch()) {
                logger.info("✓ Perfect match for version {}", version);
            } else {
                logger.warn("⚠ Found {} differences for version {}", result.getDifferences().size(), version);
                // Generate detailed feedback report for debugging
                mfmFeedbackService.generateFeedbackReport(version);
            }
            
        } catch (Exception e) {
            logger.error("Error processing verification file: {}", fileName, e);
        }
    }
    
    private String loadFileContent(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        return Files.readString(Paths.get(resource.getURI()));
    }
    
    private String extractVersion(String content) {
        Matcher matcher = VERSION_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * Parse and store MFM file from JAR resource by creating a temporary file
     */
    private MfmRawTextParserService.MfmParseResult parseAndStoreMfmFileFromResource(String filePath, String content) throws IOException {
        // Create temporary file
        Path tempFile = Files.createTempFile("mfm-", ".txt");
        try {
            // Write content to temporary file
            Files.write(tempFile, content.getBytes());
            
            // Parse using the temporary file
            MfmRawTextParserService.MfmParseResult result = mfmRawTextParserService.parseAndStoreMfmFile(tempFile.toString());
            
            return result;
        } finally {
            // Clean up temporary file
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                logger.warn("Could not delete temporary file: {}", tempFile, e);
            }
        }
    }
    
}
