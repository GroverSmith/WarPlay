package com.warplay.service;

import com.warplay.entity.*;
import com.warplay.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MfmRawTextParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmRawTextParserService.class);
    
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
    
    // Patterns for parsing
    private static final Pattern VERSION_PATTERN = Pattern.compile("VERSION\\s+(\\d+\\.\\d+)");
    private static final Pattern FACTION_HEADER_PATTERN = Pattern.compile("CODEX:\\s*(.+)|INDEX:\\s*(.+)|CODEX SUPPLEMENT:");
    private static final Pattern UNIT_WITH_POINTS_PATTERN = Pattern.compile("^\\s*(.+?)\\s+(\\d+)\\s+models?\\s+[.\\s]+\\s*(?:\\([+-]\\d+\\)\\s+)?(\\d+)\\s+pts$");
    private static final Pattern MODEL_COUNT_POINTS_PATTERN = Pattern.compile("^\\s*(\\d+)\\s+models?\\s+[.\\s]+\\s*(?:\\([+-]\\d+\\)\\s+)?(\\d+)\\s+pts");
    private static final Pattern ENHANCEMENT_PATTERN = Pattern.compile("^(.+?)[.\\s]+\\s*(?:\\([+-]\\d+\\)\\s+)?(\\d+)\\s+pts$");
    private static final Pattern FORGE_WORLD_PATTERN = Pattern.compile("FORGE WORLD POINTS VALUES");
    private static final Pattern ENHANCEMENT_SECTION_PATTERN = Pattern.compile("DETACHMENT ENHANCEMENTS");
    private static final Pattern IMPERIAL_AGENTS_PATTERN = Pattern.compile("CODEX: IMPERIAL AGENTS");
    private static final Pattern AGENTS_OF_IMPERIUM_PATTERN = Pattern.compile("AGENTS OF THE IMPERIUM");
    private static final Pattern EVERY_MODEL_HAS_PATTERN = Pattern.compile("EVERY MODEL HAS");
    private static final Pattern IMPERIUM_KEYWORD_PATTERN = Pattern.compile("IMPERIUM KEYWORD");
    
    /**
     * Parse a raw MFM text file and store the data in the database
     */
    @Transactional
    public MfmParseResult parseAndStoreMfmFile(String filePath) throws IOException {
        logger.info("Starting to parse raw MFM file: {}", filePath);
        
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        
        // Extract version from file
        String version = extractVersion(content);
        if (version == null) {
            throw new IllegalArgumentException("Could not extract version from MFM file");
        }
        
        // Create or get MFM version
        MfmVersion mfmVersion = createOrGetMfmVersion(version, extractDateFromFilename(filePath));
        
        // Parse the content
        MfmParseData parseData = parseMfmContent(content, mfmVersion);
        
        // Store in database
        storeParseData(parseData, mfmVersion);
        
        logger.info("Successfully parsed and stored MFM file. Version: {}, Units: {}, Enhancements: {}", 
                   version, parseData.getUnits().size(), parseData.getEnhancements().size());
        
        return new MfmParseResult(version, parseData.getUnits().size(), parseData.getEnhancements().size(), 
                                 parseData.getFactions().size(), parseData.getDetachments().size());
    }
    
    /**
     * Extract version from file content
     */
    private String extractVersion(String content) {
        Matcher matcher = VERSION_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * Extract date from filename (e.g., "RAW_MFM_3_2_Aug25.txt" -> "Aug 25")
     */
    private String extractDateFromFilename(String filePath) {
        String filename = Paths.get(filePath).getFileName().toString();
        // Extract date pattern like "Aug25" or "Sep25"
        Pattern datePattern = Pattern.compile("([A-Za-z]{3})(\\d{2})");
        Matcher matcher = datePattern.matcher(filename);
        if (matcher.find()) {
            return matcher.group(1) + " " + matcher.group(2);
        }
        return "Unknown";
    }
    
    /**
     * Create or get MFM version
     */
    private MfmVersion createOrGetMfmVersion(String version, String date) {
        Optional<MfmVersion> existing = mfmVersionRepository.findByVersion(version);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Mark all other versions as not latest
        List<MfmVersion> allVersions = mfmVersionRepository.findAll();
        for (MfmVersion v : allVersions) {
            v.setIsLatest(false);
            mfmVersionRepository.save(v);
        }
        
        MfmVersion newVersion = new MfmVersion(version, date, true);
        return mfmVersionRepository.save(newVersion);
    }
    
    /**
     * Parse MFM content into structured data
     */
    private MfmParseData parseMfmContent(String content, MfmVersion mfmVersion) {
        String[] lines = content.split("\n");
        
        MfmParseData parseData = new MfmParseData();
        MfmParseContext context = new MfmParseContext(mfmVersion);
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.isEmpty()) {
                continue;
            }
            
            // Check for faction headers
            if (isFactionHeader(line)) {
                handleFactionHeader(line, i, lines, context, parseData);
                continue;
            }
            
            // Check for Imperial Agents subsections
            if (context.isImperialAgents && isAgentsOfTheImperiumSubsection(line)) {
                context.imperialAgentsSubsection = "AGENTS_OF_THE_IMPERIUM";
                context.currentFaction = "IMPERIAL AGENTS";
                context.currentDetachment = null;
                context.isForgeWorld = false;
                context.isEnhancementSection = false;
                continue;
            }
            
            if (context.isImperialAgents && isEveryModelHasImperiumSubsection(line)) {
                if (i + 1 < lines.length && isImperiumKeywordLine(lines[i + 1].trim())) {
                    context.imperialAgentsSubsection = "EVERY_MODEL_HAS_IMPERIUM";
                    context.currentFaction = "IMPERIAL AGENTS (ALLIES)";
                    context.currentDetachment = null;
                    context.isForgeWorld = false;
                    context.isEnhancementSection = false;
                    i++; // Skip the next line
                    continue;
                }
            }
            
            // Check for Forge World section
            if (isForgeWorldSection(line)) {
                context.isForgeWorld = true;
                context.isEnhancementSection = false;
                continue;
            }
            
            // Check for enhancement section
            if (isEnhancementSectionHeader(line)) {
                context.isEnhancementSection = true;
                continue;
            }
            
            // Check for detachment headers
            if (isDetachmentHeader(line, context)) {
                context.currentDetachment = line;
                continue;
            }
            
            // Parse enhancement entries
            if (context.isEnhancementSection && isEnhancementEntry(line)) {
                MfmEnhancementData enhancement = parseEnhancementEntry(line, context);
                if (enhancement != null) {
                    parseData.addEnhancement(enhancement);
                }
            }
            // Parse unit entries
            else if (isUnitEntry(line)) {
                MfmUnitData unit = parseUnitEntry(line, i, lines, context);
                if (unit != null) {
                    parseData.addUnit(unit);
                }
            }
        }
        
        return parseData;
    }
    
    /**
     * Handle faction header parsing
     */
    private void handleFactionHeader(String line, int lineIndex, String[] lines, MfmParseContext context, MfmParseData parseData) {
        if (isImperialAgentsSection(line)) {
            context.isImperialAgents = true;
            context.imperialAgentsSubsection = null;
            context.currentFaction = null;
            context.currentDetachment = null;
            context.isForgeWorld = false;
            context.isEnhancementSection = false;
        } else {
            context.isImperialAgents = false;
            context.imperialAgentsSubsection = null;
            
            // Check if this is a CODEX SUPPLEMENT: with faction name on next line
            if (line.equals("CODEX SUPPLEMENT:") && lineIndex + 1 < lines.length) {
                String nextLine = lines[lineIndex + 1].trim();
                if (!nextLine.isEmpty() && !nextLine.contains(":") && !nextLine.contains("pts")) {
                    context.currentFaction = nextLine;
                } else {
                    context.currentFaction = extractFactionName(line);
                }
            } else {
                context.currentFaction = extractFactionName(line);
            }
            
            context.currentDetachment = null;
            context.isForgeWorld = false;
            context.isEnhancementSection = false;
        }
    }
    
    /**
     * Check if line is a faction header
     */
    private boolean isFactionHeader(String line) {
        return FACTION_HEADER_PATTERN.matcher(line).find();
    }
    
    /**
     * Check if line indicates Imperial Agents section
     */
    private boolean isImperialAgentsSection(String line) {
        return IMPERIAL_AGENTS_PATTERN.matcher(line).find();
    }
    
    /**
     * Check if line indicates Agents of the Imperium subsection
     */
    private boolean isAgentsOfTheImperiumSubsection(String line) {
        return AGENTS_OF_IMPERIUM_PATTERN.matcher(line).find();
    }
    
    /**
     * Check if line indicates Every Model Has Imperium subsection
     */
    private boolean isEveryModelHasImperiumSubsection(String line) {
        return EVERY_MODEL_HAS_PATTERN.matcher(line).find();
    }
    
    /**
     * Check if line completes the Imperium keyword subsection
     */
    private boolean isImperiumKeywordLine(String line) {
        return IMPERIUM_KEYWORD_PATTERN.matcher(line).find();
    }
    
    /**
     * Extract faction name from header
     */
    private String extractFactionName(String line) {
        return line.replaceAll("^(CODEX:|INDEX:|CODEX SUPPLEMENT:)\\s*", "").trim();
    }
    
    /**
     * Check if line indicates Forge World section
     */
    private boolean isForgeWorldSection(String line) {
        return FORGE_WORLD_PATTERN.matcher(line).find();
    }
    
    /**
     * Check if line is enhancement section header
     */
    private boolean isEnhancementSectionHeader(String line) {
        return ENHANCEMENT_SECTION_PATTERN.matcher(line).find();
    }
    
    /**
     * Check if line is a detachment header
     */
    private boolean isDetachmentHeader(String line, MfmParseContext context) {
        return !line.isEmpty() && 
               !line.contains("pts") && 
               !line.contains("models") && 
               !isFactionHeader(line) && 
               !isForgeWorldSection(line) &&
               !isEnhancementSectionHeader(line) &&
               !line.matches("^\\d+$"); // Not just a page number
    }
    
    /**
     * Check if line contains a unit entry
     */
    private boolean isUnitEntry(String line) {
        return line.contains("pts") && (line.contains("models") || line.contains("model"));
    }
    
    /**
     * Check if line contains an enhancement entry
     */
    private boolean isEnhancementEntry(String line) {
        return line.contains("pts") && !line.contains("models") && !line.contains("model");
    }
    
    /**
     * Parse a unit entry
     */
    private MfmUnitData parseUnitEntry(String line, int lineIndex, String[] lines, MfmParseContext context) {
        String unitName;
        Integer modelCount;
        Integer points;
        
        // Check if unit name and points are on the same line
        Matcher sameLineMatch = UNIT_WITH_POINTS_PATTERN.matcher(line);
        if (sameLineMatch.find()) {
            unitName = sameLineMatch.group(1).trim();
            modelCount = Integer.parseInt(sameLineMatch.group(2));
            points = Integer.parseInt(sameLineMatch.group(3));
        } else {
            // Extract model count and points from points-only line
            Matcher modelMatch = MODEL_COUNT_POINTS_PATTERN.matcher(line);
            if (!modelMatch.find()) {
                return null;
            }
            
            modelCount = Integer.parseInt(modelMatch.group(1));
            points = Integer.parseInt(modelMatch.group(2));
            
            // Look backwards to find the unit name
            unitName = findUnitName(lineIndex, lines);
            if (unitName == null) {
                return null;
            }
        }
        
        return new MfmUnitData(
            context.currentFaction,
            context.currentDetachment,
            unitName,
            modelCount,
            points,
            context.isForgeWorld,
            lineIndex + 1
        );
    }
    
    /**
     * Find unit name by looking backwards from points line
     */
    private String findUnitName(int lineIndex, String[] lines) {
        // Look backwards up to 5 lines to find the unit name
        for (int i = lineIndex - 1; i >= Math.max(0, lineIndex - 5); i--) {
            String line = lines[i].trim();
            
            // Skip empty lines, page numbers, and other non-unit lines
            if (line.isEmpty() || 
                line.matches("^\\d+$") || 
                line.contains("pts") || 
                line.contains("models") ||
                line.contains("DETACHMENT") ||
                line.contains("FORGE WORLD") ||
                line.startsWith("CODEX:") ||
                line.startsWith("INDEX:")) {
                continue;
            }
            
            // Check if this line contains a unit name with points on the same line
            Matcher sameLineMatch = UNIT_WITH_POINTS_PATTERN.matcher(line);
            if (sameLineMatch.find()) {
                return sameLineMatch.group(1).trim();
            }
            
            // Check if this line is just a unit name (no points)
            if (!line.matches(".*\\d.*") && 
                !line.contains("models") && 
                !line.contains("pts") &&
                !line.contains("DETACHMENT") &&
                !line.contains("FORGE WORLD") &&
                !line.startsWith("CODEX:") &&
                !line.startsWith("INDEX:")) {
                return line;
            }
        }
        
        return null;
    }
    
    /**
     * Parse an enhancement entry
     */
    private MfmEnhancementData parseEnhancementEntry(String line, MfmParseContext context) {
        Matcher enhancementMatch = ENHANCEMENT_PATTERN.matcher(line);
        if (!enhancementMatch.find()) {
            return null;
        }
        
        String enhancementName = enhancementMatch.group(1).trim();
        Integer points = Integer.parseInt(enhancementMatch.group(2));
        
        return new MfmEnhancementData(
            context.currentFaction,
            context.currentDetachment,
            enhancementName,
            points,
            context.lineNumber
        );
    }
    
    /**
     * Store parsed data in database
     */
    private void storeParseData(MfmParseData parseData, MfmVersion mfmVersion) {
        // Store factions
        Map<String, MfmFaction> factionMap = new HashMap<>();
        for (String factionName : parseData.getFactions()) {
            MfmFaction faction = createOrGetFaction(factionName, mfmVersion);
            factionMap.put(factionName, faction);
        }
        
        // Store detachments
        Map<String, MfmDetachment> detachmentMap = new HashMap<>();
        for (MfmDetachmentData detachmentData : parseData.getDetachments()) {
            MfmFaction faction = factionMap.get(detachmentData.getFaction());
            MfmDetachment detachment = createOrGetDetachment(detachmentData.getName(), faction);
            detachmentMap.put(detachmentData.getFaction() + ":" + detachmentData.getName(), detachment);
        }
        
        // Store units and variants
        for (MfmUnitData unitData : parseData.getUnits()) {
            MfmFaction faction = factionMap.get(unitData.getFaction());
            MfmUnit unit = createOrGetUnit(unitData.getName(), faction, unitData.getUnitType());
            
            // Create unit variant
            MfmUnitVariant variant = new MfmUnitVariant(unit, unitData.getModelCount(), unitData.getPoints());
            mfmUnitVariantRepository.save(variant);
        }
        
        // Store enhancements
        for (MfmEnhancementData enhancementData : parseData.getEnhancements()) {
            String detachmentKey = enhancementData.getFaction() + ":" + enhancementData.getDetachment();
            MfmDetachment detachment = detachmentMap.get(detachmentKey);
            if (detachment != null) {
                MfmEnhancement enhancement = new MfmEnhancement(detachment, enhancementData.getName(), enhancementData.getPoints());
                mfmEnhancementRepository.save(enhancement);
            }
        }
    }
    
    /**
     * Create or get faction
     */
    private MfmFaction createOrGetFaction(String name, MfmVersion mfmVersion) {
        Optional<MfmFaction> existing = mfmFactionRepository.findByNameAndMfmVersion(name, mfmVersion);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Determine supergroup and allyTo based on faction name
        String supergroup = determineSupergroup(name);
        String allyTo = determineAllyTo(name);
        
        MfmFaction faction = new MfmFaction(mfmVersion, name, supergroup, allyTo);
        return mfmFactionRepository.save(faction);
    }
    
    /**
     * Create or get detachment
     */
    private MfmDetachment createOrGetDetachment(String name, MfmFaction faction) {
        Optional<MfmDetachment> existing = mfmDetachmentRepository.findByNameAndFaction(name, faction);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        MfmDetachment detachment = new MfmDetachment(faction, name);
        return mfmDetachmentRepository.save(detachment);
    }
    
    /**
     * Create or get unit
     */
    private MfmUnit createOrGetUnit(String name, MfmFaction faction, String unitType) {
        Optional<MfmUnit> existing = mfmUnitRepository.findByNameAndFaction(name, faction);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        MfmUnit unit = new MfmUnit(faction, name, unitType);
        return mfmUnitRepository.save(unit);
    }
    
    /**
     * Determine supergroup based on faction name
     */
    private String determineSupergroup(String factionName) {
        if (factionName.contains("IMPERIUM") || factionName.contains("SPACE MARINES") || 
            factionName.contains("ADEPTA SORORITAS") || factionName.contains("ADEPTUS CUSTODES") ||
            factionName.contains("ADEPTUS MECHANICUS") || factionName.contains("ASTRA MILITARUM") ||
            factionName.contains("GREY KNIGHTS") || factionName.contains("DEATHWATCH") ||
            factionName.contains("IMPERIAL KNIGHTS") || factionName.contains("ADEPTUS TITANICUS")) {
            return "Imperium";
        } else if (factionName.contains("CHAOS") || factionName.contains("DEATH GUARD") ||
                   factionName.contains("THOUSAND SONS") || factionName.contains("WORLD EATERS") ||
                   factionName.contains("EMPEROR'S CHILDREN")) {
            return "Chaos";
        } else {
            return "Xenos";
        }
    }
    
    /**
     * Determine allyTo based on faction name
     */
    private String determineAllyTo(String factionName) {
        if (factionName.contains("CHAOS DAEMONS") || factionName.contains("CHAOS KNIGHTS") ||
            factionName.contains("ADEPTUS TITANICUS") || factionName.contains("IMPERIAL KNIGHTS") ||
            factionName.contains("IMPERIAL AGENTS (ALLIES)")) {
            return factionName.contains("CHAOS") ? "Chaos" : "Imperium";
        }
        return null;
    }
    
    // Inner classes for data structures
    
    private static class MfmParseContext {
        private final MfmVersion mfmVersion;
        private String currentFaction;
        private String currentDetachment;
        private boolean isForgeWorld = false;
        private boolean isEnhancementSection = false;
        private boolean isImperialAgents = false;
        private String imperialAgentsSubsection = null;
        private int lineNumber = 0;
        
        public MfmParseContext(MfmVersion mfmVersion) {
            this.mfmVersion = mfmVersion;
        }
    }
    
    public static class MfmParseData {
        private final Set<String> factions = new HashSet<>();
        private final List<MfmUnitData> units = new ArrayList<>();
        private final List<MfmEnhancementData> enhancements = new ArrayList<>();
        private final List<MfmDetachmentData> detachments = new ArrayList<>();
        
        public void addUnit(MfmUnitData unit) {
            units.add(unit);
            factions.add(unit.getFaction());
            if (unit.getDetachment() != null) {
                detachments.add(new MfmDetachmentData(unit.getFaction(), unit.getDetachment()));
            }
        }
        
        public void addEnhancement(MfmEnhancementData enhancement) {
            enhancements.add(enhancement);
            factions.add(enhancement.getFaction());
            if (enhancement.getDetachment() != null) {
                detachments.add(new MfmDetachmentData(enhancement.getFaction(), enhancement.getDetachment()));
            }
        }
        
        public Set<String> getFactions() { return factions; }
        public List<MfmUnitData> getUnits() { return units; }
        public List<MfmEnhancementData> getEnhancements() { return enhancements; }
        public List<MfmDetachmentData> getDetachments() { return detachments; }
    }
    
    public static class MfmUnitData {
        private final String faction;
        private final String detachment;
        private final String name;
        private final Integer modelCount;
        private final Integer points;
        private final boolean isForgeWorld;
        private final int lineNumber;
        private final String unitType;
        
        public MfmUnitData(String faction, String detachment, String name, Integer modelCount, 
                          Integer points, boolean isForgeWorld, int lineNumber) {
            this.faction = faction;
            this.detachment = detachment;
            this.name = name;
            this.modelCount = modelCount;
            this.points = points;
            this.isForgeWorld = isForgeWorld;
            this.lineNumber = lineNumber;
            this.unitType = isForgeWorld ? "Forge World" : "Standard";
        }
        
        // Getters
        public String getFaction() { return faction; }
        public String getDetachment() { return detachment; }
        public String getName() { return name; }
        public Integer getModelCount() { return modelCount; }
        public Integer getPoints() { return points; }
        public boolean isForgeWorld() { return isForgeWorld; }
        public int getLineNumber() { return lineNumber; }
        public String getUnitType() { return unitType; }
    }
    
    public static class MfmEnhancementData {
        private final String faction;
        private final String detachment;
        private final String name;
        private final Integer points;
        private final int lineNumber;
        
        public MfmEnhancementData(String faction, String detachment, String name, Integer points, int lineNumber) {
            this.faction = faction;
            this.detachment = detachment;
            this.name = name;
            this.points = points;
            this.lineNumber = lineNumber;
        }
        
        // Getters
        public String getFaction() { return faction; }
        public String getDetachment() { return detachment; }
        public String getName() { return name; }
        public Integer getPoints() { return points; }
        public int getLineNumber() { return lineNumber; }
    }
    
    public static class MfmDetachmentData {
        private final String faction;
        private final String name;
        
        public MfmDetachmentData(String faction, String name) {
            this.faction = faction;
            this.name = name;
        }
        
        // Getters
        public String getFaction() { return faction; }
        public String getName() { return name; }
    }
    
    public static class MfmParseResult {
        private final String version;
        private final int unitsCount;
        private final int enhancementsCount;
        private final int factionsCount;
        private final int detachmentsCount;
        
        public MfmParseResult(String version, int unitsCount, int enhancementsCount, int factionsCount, int detachmentsCount) {
            this.version = version;
            this.unitsCount = unitsCount;
            this.enhancementsCount = enhancementsCount;
            this.factionsCount = factionsCount;
            this.detachmentsCount = detachmentsCount;
        }
        
        // Getters
        public String getVersion() { return version; }
        public int getUnitsCount() { return unitsCount; }
        public int getEnhancementsCount() { return enhancementsCount; }
        public int getFactionsCount() { return factionsCount; }
        public int getDetachmentsCount() { return detachmentsCount; }
    }
}
