package com.warplay.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MfmFileParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmFileParserService.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Parse MFM units file and extract data
     */
    public MfmFileData parseUnitsFile(String filePath) throws IOException {
        logger.info("Parsing MFM units file: {}", filePath);
        
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        
        // Extract the JavaScript object from the file
        String jsonContent = extractJsonFromJsFile(content, "window.MFM_UNITS");
        
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        
        // Extract metadata
        JsonNode metadata = rootNode.get("metadata");
        String version = metadata.get("version").asText();
        String date = metadata.get("date").asText();
        
        logger.info("Parsed MFM units file - Version: {}, Date: {}", version, date);
        
        // Extract factions and units
        Map<String, MfmFactionData> factions = new HashMap<>();
        JsonNode factionsNode = rootNode.get("factions");
        
        if (factionsNode != null) {
            factionsNode.fields().forEachRemaining(entry -> {
                String factionKey = entry.getKey();
                JsonNode factionNode = entry.getValue();
                
                MfmFactionData factionData = parseFactionData(factionKey, factionNode);
                factions.put(factionKey, factionData);
            });
        }
        
        return new MfmFileData(version, date, "units", factions);
    }
    
    /**
     * Parse MFM detachments file and extract data
     */
    public MfmFileData parseDetachmentsFile(String filePath) throws IOException {
        logger.info("Parsing MFM detachments file: {}", filePath);
        
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        
        // Extract the JavaScript object from the file
        String jsonContent = extractJsonFromJsFile(content, "window.MFM_DETACHMENTS");
        
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        
        // Extract metadata
        JsonNode metadata = rootNode.get("metadata");
        String version = metadata.get("version").asText();
        String date = metadata.get("date").asText();
        
        logger.info("Parsed MFM detachments file - Version: {}, Date: {}", version, date);
        
        // Extract factions and detachments
        Map<String, MfmFactionData> factions = new HashMap<>();
        JsonNode factionsNode = rootNode.get("factions");
        
        if (factionsNode != null) {
            factionsNode.fields().forEachRemaining(entry -> {
                String factionKey = entry.getKey();
                JsonNode factionNode = entry.getValue();
                
                MfmFactionData factionData = parseFactionDataForDetachments(factionKey, factionNode);
                factions.put(factionKey, factionData);
            });
        }
        
        return new MfmFileData(version, date, "detachments", factions);
    }
    
    /**
     * Parse MFM base file to get faction information
     */
    public Map<String, MfmFactionInfo> parseBaseFile(String filePath) throws IOException {
        logger.info("Parsing MFM base file: {}", filePath);
        
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        
        // Extract the JavaScript object from the file
        String jsonContent = extractJsonFromJsFile(content, "window.MFM_BASE");
        
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        
        Map<String, MfmFactionInfo> factionInfo = new HashMap<>();
        JsonNode factionsNode = rootNode.get("factions");
        
        if (factionsNode != null) {
            factionsNode.fields().forEachRemaining(entry -> {
                String factionKey = entry.getKey();
                JsonNode factionNode = entry.getValue();
                
                String name = factionNode.get("name").asText();
                String supergroup = factionNode.has("supergroup") ? factionNode.get("supergroup").asText() : null;
                String allyTo = factionNode.has("allyTo") ? factionNode.get("allyTo").asText() : null;
                
                factionInfo.put(factionKey, new MfmFactionInfo(name, supergroup, allyTo));
            });
        }
        
        logger.info("Parsed {} factions from base file", factionInfo.size());
        return factionInfo;
    }
    
    /**
     * Extract JSON content from JavaScript file
     */
    private String extractJsonFromJsFile(String content, String variableName) {
        // Find the variable assignment
        String pattern = variableName + "\\s*=\\s*";
        int startIndex = content.indexOf(pattern);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Could not find " + variableName + " in file");
        }
        
        startIndex += pattern.length();
        
        // Find the matching closing brace
        int braceCount = 0;
        int endIndex = startIndex;
        boolean inString = false;
        char stringChar = 0;
        
        for (int i = startIndex; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (!inString) {
                if (c == '"' || c == '\'') {
                    inString = true;
                    stringChar = c;
                } else if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        endIndex = i + 1;
                        break;
                    }
                }
            } else {
                if (c == stringChar && content.charAt(i - 1) != '\\') {
                    inString = false;
                }
            }
        }
        
        return content.substring(startIndex, endIndex);
    }
    
    /**
     * Parse faction data for units
     */
    private MfmFactionData parseFactionData(String factionKey, JsonNode factionNode) {
        String name = factionNode.get("name").asText();
        
        Map<String, MfmUnitData> units = new HashMap<>();
        JsonNode unitsNode = factionNode.get("units");
        
        if (unitsNode != null) {
            unitsNode.fields().forEachRemaining(entry -> {
                String unitKey = entry.getKey();
                JsonNode unitNode = entry.getValue();
                
                MfmUnitData unitData = parseUnitData(unitKey, unitNode);
                units.put(unitKey, unitData);
            });
        }
        
        return new MfmFactionData(name, units, null);
    }
    
    /**
     * Parse faction data for detachments
     */
    private MfmFactionData parseFactionDataForDetachments(String factionKey, JsonNode factionNode) {
        String name = factionNode.get("name").asText();
        
        Map<String, MfmDetachmentData> detachments = new HashMap<>();
        JsonNode detachmentsNode = factionNode.get("detachments");
        
        if (detachmentsNode != null) {
            detachmentsNode.fields().forEachRemaining(entry -> {
                String detachmentKey = entry.getKey();
                JsonNode detachmentNode = entry.getValue();
                
                MfmDetachmentData detachmentData = parseDetachmentData(detachmentKey, detachmentNode);
                detachments.put(detachmentKey, detachmentData);
            });
        }
        
        return new MfmFactionData(name, null, detachments);
    }
    
    /**
     * Parse unit data
     */
    private MfmUnitData parseUnitData(String unitKey, JsonNode unitNode) {
        String name = unitNode.get("name").asText();
        String unitType = unitNode.has("unitType") ? unitNode.get("unitType").asText() : null;
        
        List<MfmUnitVariantData> variants = new ArrayList<>();
        JsonNode variantsNode = unitNode.get("variants");
        
        if (variantsNode != null && variantsNode.isArray()) {
            for (JsonNode variantNode : variantsNode) {
                MfmUnitVariantData variant = parseUnitVariantData(variantNode);
                variants.add(variant);
            }
        }
        
        return new MfmUnitData(name, unitType, variants);
    }
    
    /**
     * Parse unit variant data
     */
    private MfmUnitVariantData parseUnitVariantData(JsonNode variantNode) {
        Integer modelCount = variantNode.get("modelCount").asInt();
        
        // Extract points for different versions
        Map<String, Integer> pointsByVersion = new HashMap<>();
        variantNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (key.startsWith("mfm_") && key.endsWith("_points")) {
                String version = key.substring(4, key.length() - 7).replace("_", ".");
                pointsByVersion.put(version, entry.getValue().asInt());
            }
        });
        
        return new MfmUnitVariantData(modelCount, pointsByVersion);
    }
    
    /**
     * Parse detachment data
     */
    private MfmDetachmentData parseDetachmentData(String detachmentKey, JsonNode detachmentNode) {
        String name = detachmentNode.get("name").asText();
        
        List<MfmEnhancementData> enhancements = new ArrayList<>();
        JsonNode enhancementsNode = detachmentNode.get("enhancements");
        
        if (enhancementsNode != null && enhancementsNode.isArray()) {
            for (JsonNode enhancementNode : enhancementsNode) {
                MfmEnhancementData enhancement = parseEnhancementData(enhancementNode);
                enhancements.add(enhancement);
            }
        }
        
        return new MfmDetachmentData(name, enhancements);
    }
    
    /**
     * Parse enhancement data
     */
    private MfmEnhancementData parseEnhancementData(JsonNode enhancementNode) {
        String name = enhancementNode.get("name").asText();
        
        // Extract points for different versions
        Map<String, Integer> pointsByVersion = new HashMap<>();
        enhancementNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (key.startsWith("mfm_") && key.endsWith("_points")) {
                String version = key.substring(4, key.length() - 7).replace("_", ".");
                pointsByVersion.put(version, entry.getValue().asInt());
            }
        });
        
        return new MfmEnhancementData(name, pointsByVersion);
    }
    
    // Data classes for parsed MFM data
    public static class MfmFileData {
        private final String version;
        private final String date;
        private final String type;
        private final Map<String, MfmFactionData> factions;
        
        public MfmFileData(String version, String date, String type, Map<String, MfmFactionData> factions) {
            this.version = version;
            this.date = date;
            this.type = type;
            this.factions = factions;
        }
        
        // Getters
        public String getVersion() { return version; }
        public String getDate() { return date; }
        public String getType() { return type; }
        public Map<String, MfmFactionData> getFactions() { return factions; }
    }
    
    public static class MfmFactionData {
        private final String name;
        private final Map<String, MfmUnitData> units;
        private final Map<String, MfmDetachmentData> detachments;
        
        public MfmFactionData(String name, Map<String, MfmUnitData> units, Map<String, MfmDetachmentData> detachments) {
            this.name = name;
            this.units = units;
            this.detachments = detachments;
        }
        
        // Getters
        public String getName() { return name; }
        public Map<String, MfmUnitData> getUnits() { return units; }
        public Map<String, MfmDetachmentData> getDetachments() { return detachments; }
    }
    
    public static class MfmUnitData {
        private final String name;
        private final String unitType;
        private final List<MfmUnitVariantData> variants;
        
        public MfmUnitData(String name, String unitType, List<MfmUnitVariantData> variants) {
            this.name = name;
            this.unitType = unitType;
            this.variants = variants;
        }
        
        // Getters
        public String getName() { return name; }
        public String getUnitType() { return unitType; }
        public List<MfmUnitVariantData> getVariants() { return variants; }
    }
    
    public static class MfmUnitVariantData {
        private final Integer modelCount;
        private final Map<String, Integer> pointsByVersion;
        
        public MfmUnitVariantData(Integer modelCount, Map<String, Integer> pointsByVersion) {
            this.modelCount = modelCount;
            this.pointsByVersion = pointsByVersion;
        }
        
        // Getters
        public Integer getModelCount() { return modelCount; }
        public Map<String, Integer> getPointsByVersion() { return pointsByVersion; }
    }
    
    public static class MfmDetachmentData {
        private final String name;
        private final List<MfmEnhancementData> enhancements;
        
        public MfmDetachmentData(String name, List<MfmEnhancementData> enhancements) {
            this.name = name;
            this.enhancements = enhancements;
        }
        
        // Getters
        public String getName() { return name; }
        public List<MfmEnhancementData> getEnhancements() { return enhancements; }
    }
    
    public static class MfmEnhancementData {
        private final String name;
        private final Map<String, Integer> pointsByVersion;
        
        public MfmEnhancementData(String name, Map<String, Integer> pointsByVersion) {
            this.name = name;
            this.pointsByVersion = pointsByVersion;
        }
        
        // Getters
        public String getName() { return name; }
        public Map<String, Integer> getPointsByVersion() { return pointsByVersion; }
    }
    
    public static class MfmFactionInfo {
        private final String name;
        private final String supergroup;
        private final String allyTo;
        
        public MfmFactionInfo(String name, String supergroup, String allyTo) {
            this.name = name;
            this.supergroup = supergroup;
            this.allyTo = allyTo;
        }
        
        // Getters
        public String getName() { return name; }
        public String getSupergroup() { return supergroup; }
        public String getAllyTo() { return allyTo; }
    }
}
