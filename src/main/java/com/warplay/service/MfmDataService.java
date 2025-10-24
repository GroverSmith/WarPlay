package com.warplay.service;

import com.warplay.entity.*;
import com.warplay.repository.*;
import com.warplay.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MfmDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfmDataService.class);
    
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
    
    // Version operations
    public List<MfmVersionResponse> getAllVersions() {
        logger.debug("Getting all MFM versions");
        List<MfmVersion> versions = mfmVersionRepository.findAllByOrderByVersionAsc();
        return versions.stream().map(MfmVersionResponse::new).collect(Collectors.toList());
    }
    
    public Optional<MfmVersionResponse> getLatestVersion() {
        logger.debug("Getting latest MFM version");
        return mfmVersionRepository.findByIsLatestTrue().map(MfmVersionResponse::new);
    }
    
    public Optional<MfmVersionResponse> getVersionByVersionString(String version) {
        logger.debug("Getting MFM version: {}", version);
        return mfmVersionRepository.findByVersion(version).map(MfmVersionResponse::new);
    }
    
    // Faction operations
    @Transactional(readOnly = true)
    public List<MfmFactionResponse> getFactionsByVersion(String version) {
        logger.debug("Getting factions for version: {}", version);
        List<MfmFaction> factions = mfmFactionRepository.findByMfmVersionVersion(version);
        return factions.stream().map(MfmFactionResponse::new).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MfmFactionResponse> getFactionsInLatestVersion() {
        logger.debug("Getting factions in latest version");
        List<MfmFaction> factions = mfmFactionRepository.findByMfmVersionVersion(
            mfmVersionRepository.findByIsLatestTrue().map(MfmVersion::getVersion).orElse("preset"));
        return factions.stream().map(MfmFactionResponse::new).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<MfmFactionResponse> getFactionByNameAndVersion(String factionName, String version) {
        logger.debug("Getting faction: {} for version: {}", factionName, version);
        return mfmFactionRepository.findByNameAndMfmVersionVersion(factionName, version)
            .map(MfmFactionResponse::new);
    }
    
    @Transactional(readOnly = true)
    public Optional<MfmFactionResponse> getFactionByNameInLatestVersion(String factionName) {
        logger.debug("Getting faction: {} in latest version", factionName);
        return mfmFactionRepository.findByNameInLatestVersion(factionName)
            .map(MfmFactionResponse::new);
    }
    
    // Unit operations
    @Transactional(readOnly = true)
    public List<MfmUnitResponse> getUnitsByFactionAndVersion(String factionName, String version) {
        logger.debug("Getting units for faction: {} and version: {}", factionName, version);
        List<MfmUnit> units = mfmUnitRepository.findByFactionNameAndVersion(factionName, version);
        return units.stream().map(MfmUnitResponse::new).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MfmUnitResponse> getUnitsByFactionInLatestVersion(String factionName) {
        logger.debug("Getting units for faction: {} in latest version", factionName);
        List<MfmUnit> units = mfmUnitRepository.findByFactionNameInLatestVersion(factionName);
        return units.stream().map(MfmUnitResponse::new).collect(Collectors.toList());
    }
    
    public Optional<MfmUnitResponse> getUnitByNameAndFactionAndVersion(String unitName, String factionName, String version) {
        logger.debug("Getting unit: {} for faction: {} and version: {}", unitName, factionName, version);
        return mfmUnitRepository.findByNameAndFactionAndVersion(unitName, factionName, version)
            .map(MfmUnitResponse::new);
    }
    
    public Optional<MfmUnitResponse> getUnitByNameAndFactionInLatestVersion(String unitName, String factionName) {
        logger.debug("Getting unit: {} for faction: {} in latest version", unitName, factionName);
        return mfmUnitRepository.findByNameAndFactionInLatestVersion(unitName, factionName)
            .map(MfmUnitResponse::new);
    }
    
    // Unit variant operations
    public List<Integer> getModelCountsForUnit(String unitName, String factionName, String version) {
        logger.debug("Getting model counts for unit: {} in faction: {} and version: {}", unitName, factionName, version);
        return mfmUnitVariantRepository.findModelCountsByUnitNameAndFactionAndVersion(unitName, factionName, version);
    }
    
    public List<Integer> getModelCountsForUnitInLatestVersion(String unitName, String factionName) {
        logger.debug("Getting model counts for unit: {} in faction: {} in latest version", unitName, factionName);
        return mfmUnitVariantRepository.findModelCountsByUnitNameAndFactionInLatestVersion(unitName, factionName);
    }
    
    public Optional<Integer> getPointsForUnitVariant(String unitName, String factionName, String version, Integer modelCount) {
        logger.debug("Getting points for unit: {} in faction: {} and version: {} with model count: {}", unitName, factionName, version, modelCount);
        return mfmUnitVariantRepository.findPointsByUnitNameAndFactionAndVersionAndModelCount(unitName, factionName, version, modelCount);
    }
    
    public Optional<Integer> getPointsForUnitVariantInLatestVersion(String unitName, String factionName, Integer modelCount) {
        logger.debug("Getting points for unit: {} in faction: {} in latest version with model count: {}", unitName, factionName, modelCount);
        return mfmUnitVariantRepository.findPointsByUnitNameAndFactionInLatestVersionAndModelCount(unitName, factionName, modelCount);
    }
    
    // Detachment operations
    @Transactional(readOnly = true)
    public List<MfmDetachmentResponse> getDetachmentsByFactionAndVersion(String factionName, String version) {
        logger.debug("Getting detachments for faction: {} and version: {}", factionName, version);
        List<MfmDetachment> detachments = mfmDetachmentRepository.findByFactionNameAndVersion(factionName, version);
        return detachments.stream().map(MfmDetachmentResponse::new).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MfmDetachmentResponse> getDetachmentsByFactionInLatestVersion(String factionName) {
        logger.debug("Getting detachments for faction: {} in latest version", factionName);
        List<MfmDetachment> detachments = mfmDetachmentRepository.findByFactionNameInLatestVersion(factionName);
        return detachments.stream().map(MfmDetachmentResponse::new).collect(Collectors.toList());
    }
    
    public Optional<MfmDetachmentResponse> getDetachmentByNameAndFactionAndVersion(String detachmentName, String factionName, String version) {
        logger.debug("Getting detachment: {} for faction: {} and version: {}", detachmentName, factionName, version);
        return mfmDetachmentRepository.findByNameAndFactionAndVersion(detachmentName, factionName, version)
            .map(MfmDetachmentResponse::new);
    }
    
    public Optional<MfmDetachmentResponse> getDetachmentByNameAndFactionInLatestVersion(String detachmentName, String factionName) {
        logger.debug("Getting detachment: {} for faction: {} in latest version", detachmentName, factionName);
        return mfmDetachmentRepository.findByNameAndFactionInLatestVersion(detachmentName, factionName)
            .map(MfmDetachmentResponse::new);
    }
    
    // Enhancement operations
    @Transactional(readOnly = true)
    public List<MfmEnhancementResponse> getEnhancementsByDetachmentAndFactionAndVersion(String detachmentName, String factionName, String version) {
        logger.debug("Getting enhancements for detachment: {} in faction: {} and version: {}", detachmentName, factionName, version);
        List<MfmEnhancement> enhancements = mfmEnhancementRepository.findByDetachmentNameAndFactionAndVersion(detachmentName, factionName, version);
        return enhancements.stream().map(MfmEnhancementResponse::new).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MfmEnhancementResponse> getEnhancementsByDetachmentAndFactionInLatestVersion(String detachmentName, String factionName) {
        logger.debug("Getting enhancements for detachment: {} in faction: {} in latest version", detachmentName, factionName);
        List<MfmEnhancement> enhancements = mfmEnhancementRepository.findByDetachmentNameAndFactionInLatestVersion(detachmentName, factionName);
        return enhancements.stream().map(MfmEnhancementResponse::new).collect(Collectors.toList());
    }
    
    public Optional<Integer> getPointsForEnhancement(String enhancementName, String detachmentName, String factionName, String version) {
        logger.debug("Getting points for enhancement: {} in detachment: {} in faction: {} and version: {}", enhancementName, detachmentName, factionName, version);
        return mfmEnhancementRepository.findPointsByNameAndDetachmentAndFactionAndVersion(enhancementName, detachmentName, factionName, version);
    }
    
    public Optional<Integer> getPointsForEnhancementInLatestVersion(String enhancementName, String detachmentName, String factionName) {
        logger.debug("Getting points for enhancement: {} in detachment: {} in faction: {} in latest version", enhancementName, detachmentName, factionName);
        return mfmEnhancementRepository.findPointsByNameAndDetachmentAndFactionInLatestVersion(enhancementName, detachmentName, factionName);
    }
}
