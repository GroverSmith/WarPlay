package com.warplay.service;

import com.warplay.entity.*;
import com.warplay.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MfmFeedbackService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmFeedbackService.class);
    
    @Value("${generate.mfm.feedback:false}")
    private boolean generateFeedback;
    
    @Autowired
    private MfmVersionRepository mfmVersionRepository;
    
    @Autowired
    private MfmFactionRepository mfmFactionRepository;
    
    @Autowired
    private MfmUnitRepository mfmUnitRepository;
    
    @Autowired
    private MfmDetachmentRepository mfmDetachmentRepository;
    
    @Autowired
    private MfmEnhancementRepository mfmEnhancementRepository;
    
    @Autowired
    private MfmValidationService mfmValidationService;
    
    /**
     * Generate comprehensive feedback report for parser debugging
     */
    public void generateFeedbackReport(String version) {
        if (!generateFeedback) {
            return;
        }
        
        try {
            logger.info("Generating feedback report for version: {}", version);
            
            StringBuilder report = new StringBuilder();
            report.append("=== MFM Parser Feedback Report ===\n");
            report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
            report.append("Version: ").append(version).append("\n\n");
            
            // 1. Database Statistics
            appendDatabaseStats(report, version);
            
            // 2. Validation Results
            appendValidationResults(report, version);
            
            // 3. Sample Data
            appendSampleData(report, version);
            
            // 4. Parser Issues
            appendParserIssues(report, version);
            
            // Save report
            String fileName = String.format("mfm-feedback-report-%s-%s.txt", 
                                          version.replace(".", "_"), 
                                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
            
            Path reportPath = Paths.get("logs", fileName);
            Files.createDirectories(reportPath.getParent());
            Files.write(reportPath, report.toString().getBytes());
            
            logger.info("Feedback report saved to: {}", reportPath.toAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Error generating feedback report for version: {}", version, e);
        }
    }
    
    private void appendDatabaseStats(StringBuilder report, String version) {
        report.append("1. DATABASE STATISTICS\n");
        report.append("=====================\n");
        
        try {
            Optional<MfmVersion> mfmVersion = mfmVersionRepository.findByVersion(version);
            if (mfmVersion.isPresent()) {
                MfmVersion v = mfmVersion.get();
                report.append("Version: ").append(v.getVersion()).append("\n");
                report.append("Date: ").append(v.getDate()).append("\n");
                report.append("Is Latest: ").append(v.getIsLatest()).append("\n");
                report.append("Created: ").append(v.getCreatedTimestamp()).append("\n");
                report.append("Updated: ").append(v.getUpdatedTimestamp()).append("\n");
            } else {
                report.append("ERROR: Version not found in database\n");
            }
        } catch (Exception e) {
            report.append("ERROR: Could not retrieve version data: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n");
    }
    
    private void appendValidationResults(StringBuilder report, String version) {
        report.append("2. VALIDATION RESULTS\n");
        report.append("=====================\n");
        
        try {
            // Try to find the original file
            String[] possibleFiles = {
                "src/main/resources/mfm-files/RAW_MFM_" + version.replace(".", "_") + "_Aug25.txt",
                "src/main/resources/mfm-files/RAW_MFM_" + version.replace(".", "_") + "_Sep25.txt",
                "src/main/resources/mfm-files/RAW_MFM_" + version.replace(".", "_") + "_Oct25.txt"
            };
            
            String originalFilePath = null;
            for (String file : possibleFiles) {
                if (Files.exists(Paths.get(file))) {
                    originalFilePath = file;
                    break;
                }
            }
            
            if (originalFilePath != null) {
                MfmValidationService.MfmValidationResult result = mfmValidationService.validateMfmData(version, originalFilePath);
                
                report.append("Original File: ").append(originalFilePath).append("\n");
                report.append("Total Matches: ").append(result.getMatches()).append("\n");
                report.append("Total Differences: ").append(result.getDifferences().size()).append("\n");
                report.append("Match Percentage: ").append(String.format("%.2f", result.getMatchPercentage())).append("%\n");
                report.append("Perfect Match: ").append(result.isPerfectMatch()).append("\n\n");
                
                if (!result.getDifferences().isEmpty()) {
                    report.append("DIFFERENCES FOUND:\n");
                    report.append("------------------\n");
                    for (MfmValidationService.MfmValidationDifference diff : result.getDifferences()) {
                        report.append("Line: ").append(diff.getLine()).append("\n");
                        report.append("  Original Count: ").append(diff.getOriginalCount()).append("\n");
                        report.append("  Regenerated Count: ").append(diff.getRegeneratedCount()).append("\n");
                        report.append("  Issue: ").append(diff.getIssue()).append("\n\n");
                    }
                }
            } else {
                report.append("ERROR: Could not find original file for validation\n");
                report.append("Looked for files: ").append(Arrays.toString(possibleFiles)).append("\n");
            }
        } catch (Exception e) {
            report.append("ERROR: Could not run validation: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n");
    }
    
    private void appendSampleData(StringBuilder report, String version) {
        report.append("3. SAMPLE PARSED DATA\n");
        report.append("=====================\n");
        
        try {
            // This would need to be implemented to show sample parsed data
            report.append("Sample data extraction not yet implemented\n");
            report.append("TODO: Add sample faction, unit, and enhancement data\n");
        } catch (Exception e) {
            report.append("ERROR: Could not extract sample data: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n");
    }
    
    private void appendParserIssues(StringBuilder report, String version) {
        report.append("4. PARSER ISSUES & RECOMMENDATIONS\n");
        report.append("===================================\n");
        
        report.append("Common issues to check:\n");
        report.append("- Unit names with special characters\n");
        report.append("- Multi-line unit entries\n");
        report.append("- Point adjustments (e.g., (-15), (+10))\n");
        report.append("- Forge World vs Standard units\n");
        report.append("- Imperial Agents subsections\n");
        report.append("- Detachment enhancement parsing\n");
        report.append("- Faction name extraction\n");
        report.append("- Model count parsing\n");
        
        report.append("\nTo help debug:\n");
        report.append("1. Check the validation differences above\n");
        report.append("2. Look for patterns in missing/extra entries\n");
        report.append("3. Verify faction and detachment assignments\n");
        report.append("4. Check point values match exactly\n");
        
        report.append("\n");
    }
    
    /**
     * Generate a simple summary report with faction statistics
     */
    public void generateQuickSummary(String version) {
        if (!generateFeedback) {
            return;
        }
        
        try {
            StringBuilder summary = new StringBuilder();
            summary.append("=== MFM Parser Quick Summary ===\n");
            summary.append("Version: ").append(version).append("\n");
            summary.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            // Get version info
            Optional<MfmVersion> mfmVersion = mfmVersionRepository.findByVersion(version);
            if (mfmVersion.isPresent()) {
                summary.append("✓ Version imported successfully\n");
                summary.append("  Created: ").append(mfmVersion.get().getCreatedTimestamp()).append("\n\n");
                
                // Get faction statistics
                List<MfmFaction> factions = mfmFactionRepository.findByMfmVersion(mfmVersion.get());
                summary.append("FACTION STATISTICS:\n");
                summary.append("==================\n");
                summary.append(String.format("Total Factions: %d\n\n", factions.size()));
                
                for (MfmFaction faction : factions) {
                    // Count units for this faction
                    long unitCount = mfmUnitRepository.countByFaction(faction);
                    
                    // Count detachments for this faction
                    long detachmentCount = mfmDetachmentRepository.countByFaction(faction);
                    
                    // Count enhancements for this faction
                    long enhancementCount = mfmEnhancementRepository.countByDetachmentFaction(faction);
                    
                    summary.append(String.format("Faction: %s\n", faction.getName()));
                    summary.append(String.format("  Units: %d\n", unitCount));
                    summary.append(String.format("  Detachments: %d\n", detachmentCount));
                    summary.append(String.format("  Enhancements: %d\n", enhancementCount));
                    summary.append(String.format("  Supergroup: %s\n", faction.getSupergroup()));
                    if (faction.getAllyTo() != null) {
                        summary.append(String.format("  Ally To: %s\n", faction.getAllyTo()));
                    }
                    summary.append("\n");
                }
                
                // Generate regenerated file for manual review
                generateRegeneratedFile(version, mfmVersion.get());
                
            } else {
                summary.append("✗ Version not found in database\n");
            }
            
            // Save quick summary
            String fileName = String.format("mfm-quick-summary-%s.txt", version.replace(".", "_"));
            Path summaryPath = Paths.get("logs", fileName);
            Files.createDirectories(summaryPath.getParent());
            Files.write(summaryPath, summary.toString().getBytes());
            
            logger.info("Quick summary saved to: {}", summaryPath.toAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Error generating quick summary for version: {}", version, e);
        }
    }
    
    /**
     * Generate regenerated MFM file for manual review
     */
    private void generateRegeneratedFile(String version, MfmVersion mfmVersion) {
        try {
            logger.info("Generating regenerated MFM file for manual review");
            
            // Generate the regenerated content
            String regeneratedContent = mfmValidationService.regenerateMfmFileContent(version);
            
            // Save to logs directory
            String fileName = String.format("mfm-regenerated-%s.txt", version.replace(".", "_"));
            Path filePath = Paths.get("logs", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, regeneratedContent.getBytes());
            
            logger.info("Regenerated MFM file saved to: {}", filePath.toAbsolutePath());
            
        } catch (Exception e) {
            logger.error("Error generating regenerated file for version: {}", version, e);
        }
    }
}
