package com.warplay.service;

import com.warplay.entity.*;
import com.warplay.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MfmValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmValidationService.class);
    
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
    
    /**
     * Validate parsed data by regenerating the file and comparing with original
     */
    public MfmValidationResult validateMfmData(String version, String originalFilePath) throws IOException {
        logger.info("Starting validation for MFM version: {}", version);
        
        // Get the original file content
        String originalContent = Files.readString(Paths.get(originalFilePath));
        
        // Regenerate the file from database
        String regeneratedContent = regenerateMfmFile(version);
        
        // Compare the files
        MfmValidationResult result = compareMfmFiles(originalContent, regeneratedContent, version);
        
        logger.info("Validation completed. Matches: {}, Differences: {}", 
                   result.getMatches(), result.getDifferences().size());
        
        return result;
    }
    
    /**
     * Regenerate MFM file from database data
     */
    public String regenerateMfmFile(String version) {
        logger.info("Regenerating MFM file for version: {}", version);
        
        Optional<MfmVersion> mfmVersionOpt = mfmVersionRepository.findByVersion(version);
        if (mfmVersionOpt.isEmpty()) {
            throw new IllegalArgumentException("MFM version not found: " + version);
        }
        
        MfmVersion mfmVersion = mfmVersionOpt.get();
        StringBuilder content = new StringBuilder();
        
        // Add header
        content.append("MUNITORUM\n");
        content.append("FIELD MANUAL\n");
        content.append(" VERSION ").append(version).append("\n\n");
        
        // Get all factions for this version
        List<MfmFaction> factions = mfmFactionRepository.findByMfmVersion(mfmVersion);
        
        for (MfmFaction faction : factions) {
            // Add faction header
            content.append("CODEX: ").append(faction.getName()).append("\n");
            
            // Get units for this faction
            List<MfmUnit> units = mfmUnitRepository.findByFaction(faction);
            
            // Group units by detachment
            Map<String, List<MfmUnit>> unitsByDetachment = new HashMap<>();
            List<MfmUnit> unitsWithoutDetachment = new ArrayList<>();
            
            for (MfmUnit unit : units) {
                // For now, we'll treat all units as having no detachment
                // In a real implementation, you'd need to track detachment relationships
                unitsWithoutDetachment.add(unit);
            }
            
            // Add units without detachment
            for (MfmUnit unit : unitsWithoutDetachment) {
                addUnitToContent(content, unit);
            }
            
            // Add detachment enhancements
            List<MfmDetachment> detachments = mfmDetachmentRepository.findByFaction(faction);
            for (MfmDetachment detachment : detachments) {
                content.append("\n").append(detachment.getName()).append("\n");
                
                List<MfmEnhancement> enhancements = mfmEnhancementRepository.findByDetachment(detachment);
                for (MfmEnhancement enhancement : enhancements) {
                    addEnhancementToContent(content, enhancement);
                }
            }
            
            content.append("\n");
        }
        
        return content.toString();
    }
    
    /**
     * Add unit to content
     */
    private void addUnitToContent(StringBuilder content, MfmUnit unit) {
        List<MfmUnitVariant> variants = mfmUnitVariantRepository.findByUnit(unit);
        
        if (variants.isEmpty()) {
            return;
        }
        
        // Add unit name
        content.append(" ").append(unit.getName()).append("\n");
        
        // Add variants
        for (MfmUnitVariant variant : variants) {
            content.append(String.format("%d models ............................................................ %d pts\n", 
                                       variant.getModelCount(), variant.getPoints()));
        }
    }
    
    /**
     * Add enhancement to content
     */
    private void addEnhancementToContent(StringBuilder content, MfmEnhancement enhancement) {
        content.append(String.format("%s ..................................... %d pts\n", 
                                   enhancement.getName(), enhancement.getPoints()));
    }
    
    /**
     * Compare original and regenerated files
     */
    private MfmValidationResult compareMfmFiles(String originalContent, String regeneratedContent, String version) {
        String[] originalLines = originalContent.split("\n");
        String[] regeneratedLines = regeneratedContent.split("\n");
        
        List<MfmValidationDifference> differences = new ArrayList<>();
        int matches = 0;
        
        // Normalize and compare lines
        Map<String, Integer> originalNormalized = normalizeAndCountLines(originalLines);
        Map<String, Integer> regeneratedNormalized = normalizeAndCountLines(regeneratedLines);
        
        // Find differences
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(originalNormalized.keySet());
        allKeys.addAll(regeneratedNormalized.keySet());
        
        for (String key : allKeys) {
            int originalCount = originalNormalized.getOrDefault(key, 0);
            int regeneratedCount = regeneratedNormalized.getOrDefault(key, 0);
            
            if (originalCount != regeneratedCount) {
                differences.add(new MfmValidationDifference(
                    key, originalCount, regeneratedCount, 
                    originalCount > regeneratedCount ? "Missing in regenerated" : "Extra in regenerated"
                ));
            } else {
                matches += originalCount;
            }
        }
        
        return new MfmValidationResult(version, matches, differences);
    }
    
    /**
     * Normalize lines for comparison (remove dots, normalize spacing, etc.)
     */
    private Map<String, Integer> normalizeAndCountLines(String[] lines) {
        Map<String, Integer> normalized = new HashMap<>();
        
        for (String line : lines) {
            String normalizedLine = normalizeLine(line);
            if (!normalizedLine.isEmpty()) {
                normalized.put(normalizedLine, normalized.getOrDefault(normalizedLine, 0) + 1);
            }
        }
        
        return normalized;
    }
    
    /**
     * Normalize a single line for comparison
     */
    private String normalizeLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return "";
        }
        
        String normalized = line.trim();
        
        // Remove page numbers (just digits)
        if (normalized.matches("^\\d+$")) {
            return "";
        }
        
        // Normalize dots to a consistent pattern
        normalized = normalized.replaceAll("\\.+", ".");
        
        // Normalize multiple spaces to single space
        normalized = normalized.replaceAll("\\s+", " ");
        
        // Remove point adjustments like "(-15)" or "(+10)"
        normalized = normalized.replaceAll("\\([+-]\\d+\\)", "");
        
        // Normalize "pts" to consistent format
        normalized = normalized.replaceAll("\\s+pts", " pts");
        
        return normalized;
    }
    
    /**
     * Generate a detailed validation report
     */
    public String generateValidationReport(MfmValidationResult result) {
        StringBuilder report = new StringBuilder();
        
        report.append("MFM Validation Report\n");
        report.append("====================\n");
        report.append("Version: ").append(result.getVersion()).append("\n");
        report.append("Total Matches: ").append(result.getMatches()).append("\n");
        report.append("Total Differences: ").append(result.getDifferences().size()).append("\n\n");
        
        if (!result.getDifferences().isEmpty()) {
            report.append("Differences Found:\n");
            report.append("-----------------\n");
            
            for (MfmValidationDifference diff : result.getDifferences()) {
                report.append("Line: ").append(diff.getLine()).append("\n");
                report.append("  Original Count: ").append(diff.getOriginalCount()).append("\n");
                report.append("  Regenerated Count: ").append(diff.getRegeneratedCount()).append("\n");
                report.append("  Issue: ").append(diff.getIssue()).append("\n\n");
            }
        } else {
            report.append("✓ All data matches perfectly!\n");
        }
        
        return report.toString();
    }
    
    /**
     * Save validation report to file
     */
    public void saveValidationReport(MfmValidationResult result, String outputPath) throws IOException {
        String report = generateValidationReport(result);
        Path reportPath = Paths.get("logs", outputPath);
        Files.createDirectories(reportPath.getParent());
        Files.write(reportPath, report.getBytes());
        logger.info("Validation report saved to: {}", reportPath.toAbsolutePath());
    }
    
    // Data classes for validation results
    
    public static class MfmValidationResult {
        private final String version;
        private final int matches;
        private final List<MfmValidationDifference> differences;
        
        public MfmValidationResult(String version, int matches, List<MfmValidationDifference> differences) {
            this.version = version;
            this.matches = matches;
            this.differences = differences;
        }
        
        // Getters
        public String getVersion() { return version; }
        public int getMatches() { return matches; }
        public List<MfmValidationDifference> getDifferences() { return differences; }
        
        public boolean isPerfectMatch() {
            return differences.isEmpty();
        }
        
        public double getMatchPercentage() {
            int total = matches + differences.size();
            return total > 0 ? (double) matches / total * 100 : 100.0;
        }
    }
    
    public static class MfmValidationDifference {
        private final String line;
        private final int originalCount;
        private final int regeneratedCount;
        private final String issue;
        
        public MfmValidationDifference(String line, int originalCount, int regeneratedCount, String issue) {
            this.line = line;
            this.originalCount = originalCount;
            this.regeneratedCount = regeneratedCount;
            this.issue = issue;
        }
        
        // Getters
        public String getLine() { return line; }
        public int getOriginalCount() { return originalCount; }
        public int getRegeneratedCount() { return regeneratedCount; }
        public String getIssue() { return issue; }
    }
}
