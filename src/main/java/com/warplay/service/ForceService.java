package com.warplay.service;

import com.warplay.dto.CreateForceRequest;
import com.warplay.entity.Force;
import com.warplay.entity.User;
import com.warplay.repository.ForceRepository;
import com.warplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForceService {
    
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
        loggingService.log("ForceService.createForce", "Creating force: " + request.getName() + " for club: " + request.getClubId());
        
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
        force.setSupplyLimit(request.getSupplyLimit() != null ? request.getSupplyLimit() : 50);
        force.setSupplyUsed(request.getSupplyUsed() != null ? request.getSupplyUsed() : 0);
        force.setRequisitionPoints(request.getRequisitionPoints() != null ? request.getRequisitionPoints() : 5);
        force.setBattlesWon(request.getBattlesWon() != null ? request.getBattlesWon() : 0);
        force.setBattlesLost(request.getBattlesLost() != null ? request.getBattlesLost() : 0);
        force.setBattlefieldRole(request.getBattlefieldRole());
        force.setNotes(request.getNotes());
        
        Force savedForce = forceRepository.save(force);
        loggingService.log("ForceService.createForce", "Force created with ID: " + savedForce.getId());
        
        return savedForce;
    }
    
    /**
     * Get all forces for a club
     */
    public List<Force> getForcesByClubId(Long clubId) {
        loggingService.log("ForceService.getForcesByClubId", "Fetching forces for club: " + clubId);
        return forceRepository.findByClubIdAndDeletedTimestampIsNull(clubId);
    }
    
    /**
     * Get all forces for a crusade
     */
    public List<Force> getForcesByCrusadeId(Long crusadeId) {
        loggingService.log("ForceService.getForcesByCrusadeId", "Fetching forces for crusade: " + crusadeId);
        return forceRepository.findByCrusadeIdAndDeletedTimestampIsNull(crusadeId);
    }
    
    /**
     * Get all forces for a user
     */
    public List<Force> getForcesByUserId(Long userId) {
        loggingService.log("ForceService.getForcesByUserId", "Fetching forces for user: " + userId);
        return forceRepository.findByUserIdAndDeletedTimestampIsNull(userId);
    }
    
    /**
     * Get a force by ID
     */
    public Optional<Force> getForceById(Long id) {
        loggingService.log("ForceService.getForceById", "Fetching force: " + id);
        return forceRepository.findByIdAndDeletedTimestampIsNull(id);
    }
    
    /**
     * Get all forces
     */
    public List<Force> getAllForces() {
        loggingService.log("ForceService.getAllForces", "Fetching all forces");
        return forceRepository.findAllNonDeleted();
    }
    
    /**
     * Update a force
     */
    @Transactional
    public Force updateForce(Long id, CreateForceRequest request, String googleUserId) {
        loggingService.log("ForceService.updateForce", "Updating force: " + id);
        
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Force not found"));
        
        // Find user by Google ID
        User user = userRepository.findByGoogleId(googleUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns this force
        if (!force.getUserId().equals(user.getId())) {
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
        
        return forceRepository.save(force);
    }
    
    /**
     * Soft delete a force
     */
    @Transactional
    public void deleteForce(Long id, String googleUserId) {
        loggingService.log("ForceService.deleteForce", "Deleting force: " + id);
        
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Force not found"));
        
        // Find user by Google ID
        User user = userRepository.findByGoogleId(googleUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns this force
        if (!force.getUserId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this force");
        }
        
        force.setDeletedTimestamp(LocalDateTime.now());
        forceRepository.save(force);
        
        loggingService.log("ForceService.deleteForce", "Force deleted: " + id);
    }
}

