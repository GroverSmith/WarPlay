package com.warplay.service;

import com.warplay.entity.*;
import com.warplay.repository.*;
import com.warplay.service.MfmFileParserService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class MfmMigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmMigrationService.class);
    
    @Value("${mfm.migration.enabled:true}")
    private boolean migrationEnabled;
    
    @Value("${mfm.migration.directory:../gameSystems/40K/mfm}")
    private String mfmDirectory;
    
    @Value("${mfm.migration.overwrite-existing:false}")
    private boolean overwriteExisting;
    
    @Autowired
    private MfmFileParserService fileParserService;
    
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
     * Run MFM migration at startup
     */
    @Transactional
    public void runMigration() {
        if (!migrationEnabled) {
            logger.info("MFM migration is disabled");
            return;
        }
        
        logger.info("Starting MFM migration from directory: {}", mfmDirectory);
        
        try {
            Path mfmPath = Paths.get(mfmDirectory);
            if (!Files.exists(mfmPath)) {
                logger.warn("MFM directory does not exist: {}", mfmDirectory);
                return;
            }
            
            // Find all MFM files
            List<MfmFileInfo> mfmFiles = findMfmFiles(mfmPath);
            
            if (mfmFiles.isEmpty()) {
                logger.info("No MFM files found in directory: {}", mfmDirectory);
                return;
            }
            
            logger.info("Found {} MFM files to process", mfmFiles.size());
            
            // Process each version
            Map<String, MfmVersion> processedVersions = new HashMap<>();
            
            for (MfmFileInfo fileInfo : mfmFiles) {
                try {
                    processMfmVersion(fileInfo, processedVersions);
                } catch (Exception e) {
                    logger.error("Error processing MFM file: {}", fileInfo.getFilePath(), e);
                }
            }
            
            // Update latest version flags
            updateLatestVersionFlags(processedVersions);
            
            logger.info("MFM migration completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during MFM migration", e);
        }
    }
    
    /**
     * Find all MFM files in the directory
     */
    private List<MfmFileInfo> findMfmFiles(Path mfmPath) throws IOException {
        List<MfmFileInfo> files = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(mfmPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".js"))
                 .filter(path -> {
                     String fileName = path.getFileName().toString();
                     return fileName.startsWith("mfm-") && 
                            (fileName.contains("units") || fileName.contains("detachments") || fileName.contains("base"));
                 })
                 .forEach(path -> {
                     String fileName = path.getFileName().toString();
                     String type = determineFileType(fileName);
                     files.add(new MfmFileInfo(path.toString(), type));
                 });
        }
        
        return files;
    }
    
    /**
     * Determine the type of MFM file
     */
    private String determineFileType(String fileName) {
        if (fileName.contains("units")) return "units";
        if (fileName.contains("detachments")) return "detachments";
        if (fileName.contains("base")) return "base";
        return "unknown";
    }
    
    /**
     * Process a single MFM version
     */
    private void processMfmVersion(MfmFileInfo fileInfo, Map<String, MfmVersion> processedVersions) throws IOException {
        logger.info("Processing MFM file: {} (type: {})", fileInfo.getFilePath(), fileInfo.getType());
        
        MfmFileData fileData;
        
        switch (fileInfo.getType()) {
            case "units":
                fileData = fileParserService.parseUnitsFile(fileInfo.getFilePath());
                break;
            case "detachments":
                fileData = fileParserService.parseDetachmentsFile(fileInfo.getFilePath());
                break;
            case "base":
                // Base file is processed separately for faction info
                return;
            default:
                logger.warn("Unknown file type: {}", fileInfo.getType());
                return;
        }
        
        String version = fileData.getVersion();
        
        // Check if version already exists
        Optional<MfmVersion> existingVersion = mfmVersionRepository.findByVersion(version);
        
        if (existingVersion.isPresent()) {
            if (overwriteExisting) {
                logger.info("Overwriting existing MFM version: {}", version);
                deleteMfmVersion(existingVersion.get());
            } else {
                logger.info("MFM version {} already exists, skipping", version);
                return;
            }
        }
        
        // Create or get version entity
        MfmVersion mfmVersion = processedVersions.computeIfAbsent(version, v -> {
            MfmVersion newVersion = new MfmVersion();
            newVersion.setVersion(version);
            newVersion.setDate(fileData.getDate());
            newVersion.setIsLatest(false); // Will be updated later
            return mfmVersionRepository.save(newVersion);
        });
        
        // Process the data based on type
        if ("units".equals(fileInfo.getType())) {
            processUnitsData(fileData, mfmVersion);
        } else if ("detachments".equals(fileInfo.getType())) {
            processDetachmentsData(fileData, mfmVersion);
        }
        
        logger.info("Successfully processed MFM version: {}", version);
    }
    
    /**
     * Process units data
     */
    private void processUnitsData(MfmFileData fileData, MfmVersion mfmVersion) {
        logger.info("Processing units data for version: {}", mfmVersion.getVersion());
        
        // Get faction info from base file
        Map<String, MfmFactionInfo> factionInfo = getFactionInfoFromBaseFile();
        
        for (Map.Entry<String, MfmFactionData> entry : fileData.getFactions().entrySet()) {
            String factionKey = entry.getKey();
            MfmFactionData factionData = entry.getValue();
            
            // Create faction
            MfmFactionInfo info = factionInfo.get(factionKey);
            MfmFaction faction = createFaction(factionData.getName(), mfmVersion, info);
            
            // Process units
            if (factionData.getUnits() != null) {
                for (Map.Entry<String, MfmUnitData> unitEntry : factionData.getUnits().entrySet()) {
                    MfmUnitData unitData = unitEntry.getValue();
                    processUnit(unitData, faction, mfmVersion.getVersion());
                }
            }
        }
    }
    
    /**
     * Process detachments data
     */
    private void processDetachmentsData(MfmFileData fileData, MfmVersion mfmVersion) {
        logger.info("Processing detachments data for version: {}", mfmVersion.getVersion());
        
        // Get faction info from base file
        Map<String, MfmFactionInfo> factionInfo = getFactionInfoFromBaseFile();
        
        for (Map.Entry<String, MfmFactionData> entry : fileData.getFactions().entrySet()) {
            String factionKey = entry.getKey();
            MfmFactionData factionData = entry.getValue();
            
            // Find existing faction or create if needed
            MfmFaction faction = mfmFactionRepository.findByNameAndMfmVersionVersion(factionData.getName(), mfmVersion.getVersion())
                .orElseGet(() -> {
                    MfmFactionInfo info = factionInfo.get(factionKey);
                    return createFaction(factionData.getName(), mfmVersion, info);
                });
            
            // Process detachments
            if (factionData.getDetachments() != null) {
                for (Map.Entry<String, MfmDetachmentData> detachmentEntry : factionData.getDetachments().entrySet()) {
                    MfmDetachmentData detachmentData = detachmentEntry.getValue();
                    processDetachment(detachmentData, faction, mfmVersion.getVersion());
                }
            }
        }
    }
    
    /**
     * Get faction info from base file
     */
    private Map<String, MfmFactionInfo> getFactionInfoFromBaseFile() {
        try {
            Path baseFilePath = Paths.get(mfmDirectory, "mfm-base.js");
            if (Files.exists(baseFilePath)) {
                return fileParserService.parseBaseFile(baseFilePath.toString());
            }
        } catch (Exception e) {
            logger.warn("Could not parse base file for faction info", e);
        }
        return new HashMap<>();
    }
    
    /**
     * Create faction entity
     */
    private MfmFaction createFaction(String name, MfmVersion mfmVersion, MfmFactionInfo info) {
        MfmFaction faction = new MfmFaction();
        faction.setMfmVersion(mfmVersion);
        faction.setName(name);
        
        if (info != null) {
            faction.setSupergroup(info.getSupergroup());
            faction.setAllyTo(info.getAllyTo());
        }
        
        return mfmFactionRepository.save(faction);
    }
    
    /**
     * Process unit data
     */
    private void processUnit(MfmUnitData unitData, MfmFaction faction, String version) {
        MfmUnit unit = new MfmUnit();
        unit.setFaction(faction);
        unit.setName(unitData.getName());
        unit.setUnitType(unitData.getUnitType());
        
        MfmUnit savedUnit = mfmUnitRepository.save(unit);
        
        // Process variants
        for (MfmUnitVariantData variantData : unitData.getVariants()) {
            MfmUnitVariant variant = new MfmUnitVariant();
            variant.setUnit(savedUnit);
            variant.setModelCount(variantData.getModelCount());
            
            // Get points for this version
            Integer points = variantData.getPointsByVersion().get(version);
            if (points == null) {
                // Try to find points in any version
                points = variantData.getPointsByVersion().values().iterator().next();
            }
            
            variant.setPoints(points != null ? points : 0);
            mfmUnitVariantRepository.save(variant);
        }
    }
    
    /**
     * Process detachment data
     */
    private void processDetachment(MfmDetachmentData detachmentData, MfmFaction faction, String version) {
        MfmDetachment detachment = new MfmDetachment();
        detachment.setFaction(faction);
        detachment.setName(detachmentData.getName());
        
        MfmDetachment savedDetachment = mfmDetachmentRepository.save(detachment);
        
        // Process enhancements
        for (MfmEnhancementData enhancementData : detachmentData.getEnhancements()) {
            MfmEnhancement enhancement = new MfmEnhancement();
            enhancement.setDetachment(savedDetachment);
            enhancement.setName(enhancementData.getName());
            
            // Get points for this version
            Integer points = enhancementData.getPointsByVersion().get(version);
            if (points == null) {
                // Try to find points in any version
                points = enhancementData.getPointsByVersion().values().iterator().next();
            }
            
            enhancement.setPoints(points != null ? points : 0);
            mfmEnhancementRepository.save(enhancement);
        }
    }
    
    /**
     * Delete an existing MFM version and all its data
     */
    private void deleteMfmVersion(MfmVersion version) {
        logger.info("Deleting existing MFM version: {}", version.getVersion());
        
        // Delete in reverse order of dependencies
        mfmEnhancementRepository.deleteByDetachmentFactionMfmVersion(version);
        mfmDetachmentRepository.deleteByFactionMfmVersion(version);
        mfmUnitVariantRepository.deleteByUnitFactionMfmVersion(version);
        mfmUnitRepository.deleteByFactionMfmVersion(version);
        mfmFactionRepository.deleteByMfmVersion(version);
        mfmVersionRepository.delete(version);
    }
    
    /**
     * Update latest version flags
     */
    private void updateLatestVersionFlags(Map<String, MfmVersion> processedVersions) {
        if (processedVersions.isEmpty()) {
            return;
        }
        
        // Clear all latest flags
        mfmVersionRepository.findAll().forEach(version -> {
            version.setIsLatest(false);
            mfmVersionRepository.save(version);
        });
        
        // Set the highest version as latest
        String latestVersion = processedVersions.keySet().stream()
            .max(Comparator.naturalOrder())
            .orElse(null);
        
        if (latestVersion != null) {
            MfmVersion latest = processedVersions.get(latestVersion);
            latest.setIsLatest(true);
            mfmVersionRepository.save(latest);
            logger.info("Set latest MFM version to: {}", latestVersion);
        }
    }
    
    /**
     * File info class
     */
    private static class MfmFileInfo {
        private final String filePath;
        private final String type;
        
        public MfmFileInfo(String filePath, String type) {
            this.filePath = filePath;
            this.type = type;
        }
        
        public String getFilePath() { return filePath; }
        public String getType() { return type; }
    }
}
