package com.warplay.service;

import com.warplay.dto.ArmyResponse;
import com.warplay.dto.CreateArmyRequest;
import com.warplay.entity.Army;
import com.warplay.entity.ArmyUnit;
import com.warplay.entity.Force;
import com.warplay.entity.Unit;
import com.warplay.repository.ArmyRepository;
import com.warplay.repository.ArmyUnitRepository;
import com.warplay.repository.ForceRepository;
import com.warplay.repository.UnitRepository;
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
public class ArmyService {
    
    private static final Logger logger = LoggerFactory.getLogger(ArmyService.class);
    
    @Autowired
    private ArmyRepository armyRepository;
    
    @Autowired
    private ArmyUnitRepository armyUnitRepository;
    
    @Autowired
    private ForceRepository forceRepository;
    
    @Autowired
    private UnitRepository unitRepository;
    
    @Autowired
    private LoggingService loggingService;
    
    /**
     * Create a new army
     */
    @Transactional
    public ArmyResponse createArmy(CreateArmyRequest request, Long userId) {
        logger.info("Creating army: {} for force: {}", request.getName(), request.getForceId());
        
        // Validate force exists and user has access
        Optional<Force> forceOpt = forceRepository.findById(request.getForceId());
        if (forceOpt.isEmpty()) {
            throw new IllegalArgumentException("Force not found");
        }
        
        Force force = forceOpt.get();
        if (!force.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User does not have access to this force");
        }
        
        // Create army entity
        Army army = new Army();
        army.setForceId(request.getForceId());
        army.setUserId(userId);
        army.setName(request.getName());
        army.setFaction(request.getFaction());
        army.setDetachment(request.getDetachment());
        army.setMfmVersion(request.getMfmVersion());
        army.setPoints(request.getPoints());
        army.setArmyType(request.getArmyType());
        army.setArmyText(request.getArmyText());
        army.setNotes(request.getNotes());
        army.setImageUrl(request.getImageUrl());
        
        // Save army
        army = armyRepository.save(army);
        logger.info("Army created with ID: {}", army.getId());
        
        // Handle build mode - add selected units
        if ("build".equals(request.getArmyType()) && request.getSelectedUnits() != null) {
            for (CreateArmyRequest.SelectedUnit selectedUnit : request.getSelectedUnits()) {
                // Validate unit exists and belongs to user
                Optional<Unit> unitOpt = unitRepository.findById(selectedUnit.getUnitId());
                if (unitOpt.isPresent() && unitOpt.get().getUserId().equals(userId)) {
                    ArmyUnit armyUnit = new ArmyUnit(army.getId(), selectedUnit.getUnitId());
                    armyUnitRepository.save(armyUnit);
                }
            }
        }
        
        // Log the creation
        loggingService.logUserAction(userId.toString(), "CREATE_ARMY", 
            String.format("Created army '%s' for force '%s'", army.getName(), force.getName()));
        
        return convertToResponse(army);
    }
    
    /**
     * Get army by ID
     */
    public ArmyResponse getArmyById(Long armyId, Long userId) {
        logger.info("Getting army: {} for user: {}", armyId, userId);
        
        Optional<Army> armyOpt = armyRepository.findByIdAndUserId(armyId, userId);
        if (armyOpt.isEmpty()) {
            throw new IllegalArgumentException("Army not found or access denied");
        }
        
        return convertToResponse(armyOpt.get());
    }
    
    /**
     * Get army by ID (public access - no authentication required)
     */
    public ArmyResponse getArmyByIdPublic(Long armyId) {
        logger.info("Getting army (public): {}", armyId);
        
        Optional<Army> armyOpt = armyRepository.findById(armyId);
        if (armyOpt.isEmpty()) {
            throw new IllegalArgumentException("Army not found");
        }
        
        return convertToResponse(armyOpt.get());
    }
    
    /**
     * Get all armies for a force
     */
    public List<ArmyResponse> getArmiesByForceId(Long forceId, Long userId) {
        logger.info("Getting armies for force: {} and user: {}", forceId, userId);
        
        // Validate force exists and user has access
        Optional<Force> forceOpt = forceRepository.findById(forceId);
        if (forceOpt.isEmpty()) {
            throw new IllegalArgumentException("Force not found");
        }
        
        Force force = forceOpt.get();
        if (!force.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User does not have access to this force");
        }
        
        List<Army> armies = armyRepository.findByForceId(forceId);
        return armies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all armies for a user
     */
    public List<ArmyResponse> getArmiesByUserId(Long userId) {
        logger.info("Getting armies for user: {}", userId);
        
        List<Army> armies = armyRepository.findByUserId(userId);
        return armies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update army
     */
    @Transactional
    public ArmyResponse updateArmy(Long armyId, CreateArmyRequest request, Long userId) {
        logger.info("Updating army: {} for user: {}", armyId, userId);
        
        Optional<Army> armyOpt = armyRepository.findByIdAndUserId(armyId, userId);
        if (armyOpt.isEmpty()) {
            throw new IllegalArgumentException("Army not found or access denied");
        }
        
        Army army = armyOpt.get();
        army.setName(request.getName());
        army.setFaction(request.getFaction());
        army.setDetachment(request.getDetachment());
        army.setMfmVersion(request.getMfmVersion());
        army.setPoints(request.getPoints());
        army.setArmyType(request.getArmyType());
        army.setArmyText(request.getArmyText());
        army.setNotes(request.getNotes());
        army.setImageUrl(request.getImageUrl());
        
        army = armyRepository.save(army);
        
        // Handle build mode - update selected units
        if ("build".equals(request.getArmyType()) && request.getSelectedUnits() != null) {
            // Remove existing units
            armyUnitRepository.deleteByArmyId(armyId);
            
            // Add new units
            for (CreateArmyRequest.SelectedUnit selectedUnit : request.getSelectedUnits()) {
                Optional<Unit> unitOpt = unitRepository.findById(selectedUnit.getUnitId());
                if (unitOpt.isPresent() && unitOpt.get().getUserId().equals(userId)) {
                    ArmyUnit armyUnit = new ArmyUnit(army.getId(), selectedUnit.getUnitId());
                    armyUnitRepository.save(armyUnit);
                }
            }
        }
        
        // Log the update
        loggingService.logUserAction(userId.toString(), "UPDATE_ARMY", 
            String.format("Updated army '%s'", army.getName()));
        
        return convertToResponse(army);
    }
    
    /**
     * Delete army (soft delete)
     */
    @Transactional
    public void deleteArmy(Long armyId, Long userId) {
        logger.info("Deleting army: {} for user: {}", armyId, userId);
        
        Optional<Army> armyOpt = armyRepository.findByIdAndUserId(armyId, userId);
        if (armyOpt.isEmpty()) {
            throw new IllegalArgumentException("Army not found or access denied");
        }
        
        Army army = armyOpt.get();
        army.setDeletedTimestamp(LocalDateTime.now());
        armyRepository.save(army);
        
        // Soft delete associated army units
        armyUnitRepository.deleteByArmyId(armyId);
        
        // Log the deletion
        loggingService.logUserAction(userId.toString(), "DELETE_ARMY", 
            String.format("Deleted army '%s'", army.getName()));
    }
    
    /**
     * Convert Army entity to ArmyResponse DTO
     */
    private ArmyResponse convertToResponse(Army army) {
        ArmyResponse response = new ArmyResponse();
        response.setId(army.getId());
        response.setForceId(army.getForceId());
        response.setUserId(army.getUserId());
        response.setName(army.getName());
        response.setFaction(army.getFaction());
        response.setDetachment(army.getDetachment());
        response.setMfmVersion(army.getMfmVersion());
        response.setPoints(army.getPoints());
        response.setArmyType(army.getArmyType());
        response.setArmyText(army.getArmyText());
        response.setNotes(army.getNotes());
        response.setImageUrl(army.getImageUrl());
        response.setCreatedTimestamp(army.getCreatedTimestamp());
        response.setUpdatedTimestamp(army.getUpdatedTimestamp());
        
        // Load units for build mode armies
        if ("build".equals(army.getArmyType())) {
            List<ArmyUnit> armyUnits = armyUnitRepository.findByArmyId(army.getId());
            List<ArmyResponse.ArmyUnitResponse> unitResponses = armyUnits.stream()
                    .map(au -> {
                        Optional<Unit> unitOpt = unitRepository.findById(au.getUnitId());
                        if (unitOpt.isPresent()) {
                            Unit unit = unitOpt.get();
                            return new ArmyResponse.ArmyUnitResponse(
                                unit.getId(),
                                unit.getName(),
                                unit.getDataSheet(),
                                unit.getModelCount(),
                                unit.getPoints()
                            );
                        }
                        return null;
                    })
                    .filter(unit -> unit != null)
                    .collect(Collectors.toList());
            response.setUnits(unitResponses);
        }
        
        return response;
    }
}
