package com.warplay.controller;

import com.warplay.dto.*;
import com.warplay.service.MfmDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mfm")
@CrossOrigin(origins = "*")
public class MfmController {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmController.class);
    
    @Autowired
    private MfmDataService mfmDataService;
    
    // Version endpoints
    @GetMapping("/versions")
    public ResponseEntity<List<MfmVersionResponse>> getAllVersions() {
        logger.info("Getting all MFM versions");
        List<MfmVersionResponse> versions = mfmDataService.getAllVersions();
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/versions/latest")
    public ResponseEntity<MfmVersionResponse> getLatestVersion() {
        logger.info("Getting latest MFM version");
        Optional<MfmVersionResponse> version = mfmDataService.getLatestVersion();
        return version.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/versions/{version}")
    public ResponseEntity<MfmVersionResponse> getVersionByVersionString(@PathVariable String version) {
        logger.info("Getting MFM version: {}", version);
        Optional<MfmVersionResponse> versionResponse = mfmDataService.getVersionByVersionString(version);
        return versionResponse.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
    }
    
    // Faction endpoints
    @GetMapping("/factions")
    public ResponseEntity<List<MfmFactionResponse>> getFactions(@RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting factions for version: {}", version);
        List<MfmFactionResponse> factions;
        
        if ("latest".equals(version)) {
            factions = mfmDataService.getFactionsInLatestVersion();
        } else {
            factions = mfmDataService.getFactionsByVersion(version);
        }
        
        return ResponseEntity.ok(factions);
    }
    
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<MfmFactionResponse> getFaction(@PathVariable String factionName, 
                                                        @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting faction: {} for version: {}", factionName, version);
        Optional<MfmFactionResponse> faction;
        
        if ("latest".equals(version)) {
            faction = mfmDataService.getFactionByNameInLatestVersion(factionName);
        } else {
            faction = mfmDataService.getFactionByNameAndVersion(factionName, version);
        }
        
        return faction.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Unit endpoints
    @GetMapping("/units")
    public ResponseEntity<List<MfmUnitResponse>> getUnits(@RequestParam String faction, 
                                                         @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting units for faction: {} and version: {}", faction, version);
        List<MfmUnitResponse> units;
        
        if ("latest".equals(version)) {
            units = mfmDataService.getUnitsByFactionInLatestVersion(faction);
        } else {
            units = mfmDataService.getUnitsByFactionAndVersion(faction, version);
        }
        
        return ResponseEntity.ok(units);
    }
    
    @GetMapping("/units/{unitName}")
    public ResponseEntity<MfmUnitResponse> getUnit(@PathVariable String unitName,
                                                  @RequestParam String faction,
                                                  @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting unit: {} for faction: {} and version: {}", unitName, faction, version);
        Optional<MfmUnitResponse> unit;
        
        if ("latest".equals(version)) {
            unit = mfmDataService.getUnitByNameAndFactionInLatestVersion(unitName, faction);
        } else {
            unit = mfmDataService.getUnitByNameAndFactionAndVersion(unitName, faction, version);
        }
        
        return unit.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // Unit variant endpoints
    @GetMapping("/units/{unitName}/model-counts")
    public ResponseEntity<List<Integer>> getModelCounts(@PathVariable String unitName,
                                                       @RequestParam String faction,
                                                       @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting model counts for unit: {} in faction: {} and version: {}", unitName, faction, version);
        List<Integer> modelCounts;
        
        if ("latest".equals(version)) {
            modelCounts = mfmDataService.getModelCountsForUnitInLatestVersion(unitName, faction);
        } else {
            modelCounts = mfmDataService.getModelCountsForUnit(unitName, faction, version);
        }
        
        return ResponseEntity.ok(modelCounts);
    }
    
    @GetMapping("/units/{unitName}/points")
    public ResponseEntity<Integer> getUnitPoints(@PathVariable String unitName,
                                                @RequestParam String faction,
                                                @RequestParam Integer modelCount,
                                                @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting points for unit: {} in faction: {} with model count: {} and version: {}", unitName, faction, modelCount, version);
        Optional<Integer> points;
        
        if ("latest".equals(version)) {
            points = mfmDataService.getPointsForUnitVariantInLatestVersion(unitName, faction, modelCount);
        } else {
            points = mfmDataService.getPointsForUnitVariant(unitName, faction, version, modelCount);
        }
        
        return points.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    // Detachment endpoints
    @GetMapping("/detachments")
    public ResponseEntity<List<MfmDetachmentResponse>> getDetachments(@RequestParam String faction,
                                                                     @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting detachments for faction: {} and version: {}", faction, version);
        List<MfmDetachmentResponse> detachments;
        
        if ("latest".equals(version)) {
            detachments = mfmDataService.getDetachmentsByFactionInLatestVersion(faction);
        } else {
            detachments = mfmDataService.getDetachmentsByFactionAndVersion(faction, version);
        }
        
        return ResponseEntity.ok(detachments);
    }
    
    @GetMapping("/detachments/{detachmentName}")
    public ResponseEntity<MfmDetachmentResponse> getDetachment(@PathVariable String detachmentName,
                                                              @RequestParam String faction,
                                                              @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting detachment: {} for faction: {} and version: {}", detachmentName, faction, version);
        Optional<MfmDetachmentResponse> detachment;
        
        if ("latest".equals(version)) {
            detachment = mfmDataService.getDetachmentByNameAndFactionInLatestVersion(detachmentName, faction);
        } else {
            detachment = mfmDataService.getDetachmentByNameAndFactionAndVersion(detachmentName, faction, version);
        }
        
        return detachment.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // Enhancement endpoints
    @GetMapping("/enhancements")
    public ResponseEntity<List<MfmEnhancementResponse>> getEnhancements(@RequestParam String detachment,
                                                                       @RequestParam String faction,
                                                                       @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting enhancements for detachment: {} in faction: {} and version: {}", detachment, faction, version);
        List<MfmEnhancementResponse> enhancements;
        
        if ("latest".equals(version)) {
            enhancements = mfmDataService.getEnhancementsByDetachmentAndFactionInLatestVersion(detachment, faction);
        } else {
            enhancements = mfmDataService.getEnhancementsByDetachmentAndFactionAndVersion(detachment, faction, version);
        }
        
        return ResponseEntity.ok(enhancements);
    }
    
    @GetMapping("/enhancements/{enhancementName}/points")
    public ResponseEntity<Integer> getEnhancementPoints(@PathVariable String enhancementName,
                                                       @RequestParam String detachment,
                                                       @RequestParam String faction,
                                                       @RequestParam(defaultValue = "latest") String version) {
        logger.info("Getting points for enhancement: {} in detachment: {} in faction: {} and version: {}", enhancementName, detachment, faction, version);
        Optional<Integer> points;
        
        if ("latest".equals(version)) {
            points = mfmDataService.getPointsForEnhancementInLatestVersion(enhancementName, detachment, faction);
        } else {
            points = mfmDataService.getPointsForEnhancement(enhancementName, detachment, faction, version);
        }
        
        return points.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
}
