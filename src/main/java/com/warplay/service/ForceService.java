package com.warplay.service;

import com.warplay.dto.CreateForceRequest;
import com.warplay.entity.Force;
import com.warplay.entity.User;
import com.warplay.repository.ForceRepository;
import com.warplay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForceService.class);
    
    @Autowired
    private ForceRepository forceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoggingService loggingService;
    
    /**
     * Create a new force
     */
    @Transactional
    public Force createForce(CreateForceRequest request, String googleUserId) {
        logger.info("Creating force: {} for club: {}", request.getName(), request.getClubId());
        
        // Find user by Google ID
        User user = userRepository.findByGoogleId(googleUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate required fields
        if (request.getClubId() == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Force name is required");
        }
        
        if (request.getFaction() == null || request.getFaction().trim().isEmpty()) {
            throw new IllegalArgumentException("Faction is required");
        }
        
        Force force = new Force();
        force.setClubId(request.getClubId());
        force.setCrusadeId(request.getCrusadeId());
        force.setUserId(user.getId());
        force.setName(request.getName());
        force.setFaction(request.getFaction());
        force.setSubFaction(request.getSubFaction());
        force.setDetachment(request.getDetachment());
        force.setSupplyLimit(request.getSupplyLimit() != null ? request.getSupplyLimit() : 0);
        force.setSupplyUsed(request.getSupplyUsed() != null ? request.getSupplyUsed() : 0);
        force.setRequisitionPoints(request.getRequisitionPoints() != null ? request.getRequisitionPoints() : 5);
        force.setBattlesWon(request.getBattlesWon() != null ? request.getBattlesWon() : 0);
        force.setBattlesLost(request.getBattlesLost() != null ? request.getBattlesLost() : 0);
        force.setBattlefieldRole(request.getBattlefieldRole());
        force.setNotes(request.getNotes());
        
        Force savedForce = forceRepository.save(force);
        loggingService.logDatabaseOperation("forces", "INSERT", true, 
            "Force created with ID: " + savedForce.getId() + " for club: " + request.getClubId());
        logger.info("Force created successfully with ID: {}", savedForce.getId());
        
        return savedForce;
    }
    
    /**
     * Get all forces for a club
     */
    public List<Force> getForcesByClubId(Long clubId) {
        logger.debug("Fetching forces for club: {}", clubId);
        List<Force> forces = forceRepository.findByClubIdAndDeletedTimestampIsNull(clubId);
        logger.info("Retrieved {} forces for club: {}", forces.size(), clubId);
        return forces;
    }
    
    /**
     * Get all forces for a crusade
     */
    public List<Force> getForcesByCrusadeId(Long crusadeId) {
        logger.debug("Fetching forces for crusade: {}", crusadeId);
        List<Force> forces = forceRepository.findByCrusadeIdAndDeletedTimestampIsNull(crusadeId);
        logger.info("Retrieved {} forces for crusade: {}", forces.size(), crusadeId);
        return forces;
    }
    
    /**
     * Get all forces for a user
     */
    public List<Force> getForcesByUserId(Long userId) {
        logger.debug("Fetching forces for user: {}", userId);
        List<Force> forces = forceRepository.findByUserIdAndDeletedTimestampIsNull(userId);
        logger.info("Retrieved {} forces for user: {}", forces.size(), userId);
        return forces;
    }
    
    /**
     * Get a force by ID
     */
    public Optional<Force> getForceById(Long id) {
        logger.debug("Fetching force: {}", id);
        return forceRepository.findByIdAndDeletedTimestampIsNull(id);
    }
    
    /**
     * Get all forces
     */
    public List<Force> getAllForces() {
        logger.debug("Fetching all forces");
        List<Force> forces = forceRepository.findAllNonDeleted();
        logger.info("Retrieved {} total forces", forces.size());
        return forces;
    }
    
    /**
     * Update a force
     */
    @Transactional
    public Force updateForce(Long id, CreateForceRequest request, String googleUserId) {
        logger.info("Updating force: {}", id);
        
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Force not found"));
        
        // Find user by Google ID
        User user = userRepository.findByGoogleId(googleUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns this force
        if (!force.getUserId().equals(user.getId())) {
            logger.warn("User {} attempted to update force {} owned by user {}", user.getId(), id, force.getUserId());
            throw new RuntimeException("You do not have permission to update this force");
        }
        
        // Update fields
        if (request.getName() != null) {
            force.setName(request.getName());
        }
        if (request.getFaction() != null) {
            force.setFaction(request.getFaction());
        }
        if (request.getSubFaction() != null) {
            force.setSubFaction(request.getSubFaction());
        }
        if (request.getDetachment() != null) {
            force.setDetachment(request.getDetachment());
        }
        if (request.getSupplyLimit() != null) {
            force.setSupplyLimit(request.getSupplyLimit());
        }
        if (request.getSupplyUsed() != null) {
            force.setSupplyUsed(request.getSupplyUsed());
        }
        if (request.getRequisitionPoints() != null) {
            force.setRequisitionPoints(request.getRequisitionPoints());
        }
        if (request.getBattlesWon() != null) {
            force.setBattlesWon(request.getBattlesWon());
        }
        if (request.getBattlesLost() != null) {
            force.setBattlesLost(request.getBattlesLost());
        }
        if (request.getBattlefieldRole() != null) {
            force.setBattlefieldRole(request.getBattlefieldRole());
        }
        if (request.getNotes() != null) {
            force.setNotes(request.getNotes());
        }
        if (request.getCrusadeId() != null) {
            force.setCrusadeId(request.getCrusadeId());
        }
        
        Force updatedForce = forceRepository.save(force);
        loggingService.logDatabaseOperation("forces", "UPDATE", true, 
            "Force updated: " + id);
        logger.info("Force updated successfully: {}", id);
        
        return updatedForce;
    }
    
    /**
     * Soft delete a force
     */
    @Transactional
    public void deleteForce(Long id, String googleUserId) {
        logger.info("Deleting force: {}", id);
        
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Force not found"));
        
        // Find user by Google ID
        User user = userRepository.findByGoogleId(googleUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns this force
        if (!force.getUserId().equals(user.getId())) {
            logger.warn("User {} attempted to delete force {} owned by user {}", user.getId(), id, force.getUserId());
            throw new RuntimeException("You do not have permission to delete this force");
        }
        
        force.setDeletedTimestamp(LocalDateTime.now());
        forceRepository.save(force);
        
        loggingService.logDatabaseOperation("forces", "DELETE", true, 
            "Force soft deleted: " + id);
        logger.info("Force deleted successfully: {}", id);
    }
}

