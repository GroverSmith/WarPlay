package com.warplay.service;

import com.warplay.dto.CreateUnitRequest;
import com.warplay.dto.UnitResponse;
import com.warplay.entity.Unit;
import com.warplay.entity.User;
import com.warplay.entity.Force;
import com.warplay.repository.UnitRepository;
import com.warplay.repository.UserRepository;
import com.warplay.repository.ForceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UnitService {
    
    private static final Logger logger = LoggerFactory.getLogger(UnitService.class);
    
    @Autowired
    private UnitRepository unitRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ForceRepository forceRepository;
    
    @Autowired
    private LoggingService loggingService;
    
    /**
     * Create a new unit
     */
    @Transactional
    public UnitResponse createUnit(CreateUnitRequest request, Long userId) {
        logger.info("Creating unit: {} for force: {}", request.getName(), request.getForceId());
        
        // Find user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate required fields
        if (request.getForceId() == null) {
            throw new IllegalArgumentException("Force ID is required");
        }
        
        // Validate that the force exists and user owns it
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(request.getForceId())
            .orElseThrow(() -> new IllegalArgumentException("Force not found"));
        
        if (!force.getUserId().equals(user.getId())) {
            logger.warn("Unauthorized unit creation attempt: User {} does not own force {}", user.getId(), request.getForceId());
            throw new IllegalArgumentException("You do not have permission to add units to this force. Only the force owner can add units.");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit name is required");
        }
        
        if (request.getDataSheet() == null || request.getDataSheet().trim().isEmpty()) {
            throw new IllegalArgumentException("Data sheet is required");
        }
        
        if (request.getModelCount() == null || request.getModelCount() < 1) {
            throw new IllegalArgumentException("Model count must be at least 1");
        }
        
        // Unit type is optional - no validation needed
        
        Unit unit = new Unit();
        unit.setForceId(request.getForceId());
        unit.setUserId(user.getId());
        unit.setName(request.getName());
        unit.setDataSheet(request.getDataSheet());
        unit.setModelCount(request.getModelCount());
        unit.setUnitType(request.getUnitType());
        unit.setPoints(request.getPoints() != null ? request.getPoints() : 0);
        unit.setCrusadePoints(request.getCrusadePoints() != null ? request.getCrusadePoints() : 0);
        unit.setWargear(request.getWargear());
        unit.setEnhancements(request.getEnhancements());
        unit.setRelics(request.getRelics());
        unit.setBattleTraits(request.getBattleTraits());
        unit.setBattleScars(request.getBattleScars());
        unit.setBattleCount(request.getBattleCount() != null ? request.getBattleCount() : 0);
        unit.setXp(request.getXp() != null ? request.getXp() : 0);
        unit.setKillCount(request.getKillCount() != null ? request.getKillCount() : 0);
        unit.setTimesKilled(request.getTimesKilled() != null ? request.getTimesKilled() : 0);
        unit.setDescription(request.getDescription());
        unit.setNotes(request.getNotes());
        unit.setNotableHistory(request.getNotableHistory());
        unit.setMfmVersion(request.getMfmVersion());
        unit.setRank(request.getRank() != null ? request.getRank() : "Battle-ready");
        unit.setImageUrl(request.getImageUrl());
        
        Unit savedUnit = unitRepository.save(unit);
        loggingService.logDatabaseOperation("units", "INSERT", true, 
            "Unit created with ID: " + savedUnit.getId() + " for force: " + request.getForceId());
        logger.info("Unit created successfully with ID: {}", savedUnit.getId());
        
        return toUnitResponse(savedUnit);
    }
    
    /**
     * Get all units for a force
     */
    public List<UnitResponse> getUnitsByForceId(Long forceId) {
        logger.debug("Fetching units for force: {}", forceId);
        List<Unit> units = unitRepository.findByForceIdAndDeletedTimestampIsNull(forceId);
        logger.info("Retrieved {} units for force: {}", units.size(), forceId);
        return units.stream()
            .map(this::toUnitResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all units for a user
     */
    public List<UnitResponse> getUnitsByUserId(Long userId) {
        logger.debug("Fetching units for user: {}", userId);
        List<Unit> units = unitRepository.findByUserIdAndDeletedTimestampIsNull(userId);
        logger.info("Retrieved {} units for user: {}", units.size(), userId);
        return units.stream()
            .map(this::toUnitResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get a unit by ID
     */
    public Optional<UnitResponse> getUnitById(Long id) {
        logger.debug("Fetching unit: {}", id);
        return unitRepository.findByIdAndDeletedTimestampIsNull(id)
            .map(this::toUnitResponse);
    }
    
    /**
     * Get all units
     */
    public List<UnitResponse> getAllUnits() {
        logger.debug("Fetching all units");
        List<Unit> units = unitRepository.findAllNonDeleted();
        logger.info("Retrieved {} total units", units.size());
        return units.stream()
            .map(this::toUnitResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert Unit entity to UnitResponse with player name and force name
     */
    private UnitResponse toUnitResponse(Unit unit) {
        User user = userRepository.findById(unit.getUserId())
            .orElse(null);
        Force force = forceRepository.findById(unit.getForceId())
            .orElse(null);
        return new UnitResponse(unit, user, force);
    }
    
    /**
     * Update a unit
     */
    @Transactional
    public UnitResponse updateUnit(Long id, CreateUnitRequest request, Long userId) {
        logger.info("Updating unit: {}", id);
        
        Unit unit = unitRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Unit not found"));
        
        // Find user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns this unit
        if (!unit.getUserId().equals(user.getId())) {
            logger.warn("User {} attempted to update unit {} owned by user {}", user.getId(), id, unit.getUserId());
            throw new RuntimeException("You do not have permission to update this unit");
        }
        
        // If force ID is being changed, verify user owns the new force
        if (request.getForceId() != null && !request.getForceId().equals(unit.getForceId())) {
            Force newForce = forceRepository.findByIdAndDeletedTimestampIsNull(request.getForceId())
                .orElseThrow(() -> new RuntimeException("Target force not found"));
            
            if (!newForce.getUserId().equals(user.getId())) {
                logger.warn("User {} attempted to move unit {} to force {} they don't own", user.getId(), id, request.getForceId());
                throw new RuntimeException("You do not have permission to move this unit to the specified force");
            }
        }
        
        // Update fields
        if (request.getForceId() != null) {
            unit.setForceId(request.getForceId());
        }
        if (request.getName() != null) {
            unit.setName(request.getName());
        }
        if (request.getDataSheet() != null) {
            unit.setDataSheet(request.getDataSheet());
        }
        if (request.getModelCount() != null) {
            unit.setModelCount(request.getModelCount());
        }
        if (request.getUnitType() != null) {
            unit.setUnitType(request.getUnitType());
        }
        if (request.getPoints() != null) {
            unit.setPoints(request.getPoints());
        }
        if (request.getCrusadePoints() != null) {
            unit.setCrusadePoints(request.getCrusadePoints());
        }
        if (request.getWargear() != null) {
            unit.setWargear(request.getWargear());
        }
        if (request.getEnhancements() != null) {
            unit.setEnhancements(request.getEnhancements());
        }
        if (request.getRelics() != null) {
            unit.setRelics(request.getRelics());
        }
        if (request.getBattleTraits() != null) {
            unit.setBattleTraits(request.getBattleTraits());
        }
        if (request.getBattleScars() != null) {
            unit.setBattleScars(request.getBattleScars());
        }
        if (request.getBattleCount() != null) {
            unit.setBattleCount(request.getBattleCount());
        }
        if (request.getXp() != null) {
            unit.setXp(request.getXp());
        }
        if (request.getKillCount() != null) {
            unit.setKillCount(request.getKillCount());
        }
        if (request.getTimesKilled() != null) {
            unit.setTimesKilled(request.getTimesKilled());
        }
        if (request.getDescription() != null) {
            unit.setDescription(request.getDescription());
        }
        if (request.getNotes() != null) {
            unit.setNotes(request.getNotes());
        }
        if (request.getNotableHistory() != null) {
            unit.setNotableHistory(request.getNotableHistory());
        }
        if (request.getMfmVersion() != null) {
            unit.setMfmVersion(request.getMfmVersion());
        }
        if (request.getRank() != null) {
            unit.setRank(request.getRank());
        }
        if (request.getImageUrl() != null) {
            unit.setImageUrl(request.getImageUrl());
        }
        
        Unit updatedUnit = unitRepository.save(unit);
        loggingService.logDatabaseOperation("units", "UPDATE", true, 
            "Unit updated: " + id);
        logger.info("Unit updated successfully: {}", id);
        
        return toUnitResponse(updatedUnit);
    }
    
    /**
     * Soft delete a unit
     */
    @Transactional
    public void deleteUnit(Long id, Long userId) {
        logger.info("Deleting unit: {}", id);
        
        Unit unit = unitRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Unit not found"));
        
        // Find user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns this unit
        if (!unit.getUserId().equals(user.getId())) {
            logger.warn("User {} attempted to delete unit {} owned by user {}", user.getId(), id, unit.getUserId());
            throw new RuntimeException("You do not have permission to delete this unit");
        }
        
        unit.setDeletedTimestamp(LocalDateTime.now());
        unitRepository.save(unit);
        
        loggingService.logDatabaseOperation("units", "DELETE", true, 
            "Unit soft deleted: " + id);
        logger.info("Unit deleted successfully: {}", id);
    }
}
