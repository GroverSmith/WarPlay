package com.warplay.service;

import com.warplay.dto.CreateForceRequest;
import com.warplay.dto.ForceResponse;
import com.warplay.entity.Force;
import com.warplay.entity.User;
import com.warplay.entity.Club;
import com.warplay.repository.ForceRepository;
import com.warplay.repository.UserRepository;
import com.warplay.repository.ClubRepository;
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
public class ForceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForceService.class);
    
    @Autowired
    private ForceRepository forceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    private UserClubService userClubService;
    
    
    /**
     * Create a new force
     */
    @Transactional
    public ForceResponse createForce(CreateForceRequest request, Long userId) {
        logger.info("Creating force: {} for club: {}", request.getName(), request.getClubId());
        
        // Find user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate required fields
        if (request.getClubId() == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        
        // Validate that the user is a member of the club
        if (!userClubService.isUserMemberOfClub(user.getId(), request.getClubId())) {
            logger.warn("Unauthorized force creation attempt: User {} is not a member of club {}", user.getId(), request.getClubId());
            throw new IllegalArgumentException("User is not a member of this club. Only club members can create forces.");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Force name is required");
        }
        
        if (request.getFaction() == null || request.getFaction().trim().isEmpty()) {
            throw new IllegalArgumentException("Faction is required");
        }
        
        Force force = new Force();
        force.setClubId(request.getClubId());
        force.setUserId(user.getId());
        force.setName(request.getName());
        force.setFaction(request.getFaction());
        force.setForceType(request.getForceType() != null ? request.getForceType() : "basic");
        force.setSubFaction(request.getSubFaction());
        force.setDetachment(request.getDetachment());
        force.setSupplyLimit(request.getSupplyLimit() != null ? request.getSupplyLimit() : 0);
        force.setRequisitionPoints(request.getRequisitionPoints() != null ? request.getRequisitionPoints() : 5);
        force.setNotes(request.getNotes());
        force.setImageUrl(request.getImageUrl());
        force.setMfmVersion(request.getMfmVersion());
        
        Force savedForce = forceRepository.save(force);
        loggingService.logDatabaseOperation("forces", "INSERT", true, 
            "Force created with ID: " + savedForce.getId() + " for club: " + request.getClubId());
        logger.info("Force created successfully with ID: {}", savedForce.getId());
        
        return toForceResponse(savedForce);
    }
    
    /**
     * Get all forces for a club with player names
     */
    public List<ForceResponse> getForcesByClubId(Long clubId) {
        logger.debug("Fetching forces for club: {}", clubId);
        List<Force> forces = forceRepository.findByClubIdAndDeletedTimestampIsNull(clubId);
        logger.info("Retrieved {} forces for club: {}", forces.size(), clubId);
        return forces.stream()
            .map(this::toForceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all forces for a user with player names
     */
    public List<ForceResponse> getForcesByUserId(Long userId) {
        logger.debug("Fetching forces for user: {}", userId);
        List<Force> forces = forceRepository.findByUserIdAndDeletedTimestampIsNull(userId);
        logger.info("Retrieved {} forces for user: {}", forces.size(), userId);
        return forces.stream()
            .map(this::toForceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get a force by ID with player name
     */
    public Optional<ForceResponse> getForceById(Long id) {
        logger.debug("Fetching force: {}", id);
        return forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .map(this::toForceResponse);
    }
    
    /**
     * Get all forces with player names
     */
    public List<ForceResponse> getAllForces() {
        logger.debug("Fetching all forces");
        List<Force> forces = forceRepository.findAllNonDeleted();
        logger.info("Retrieved {} total forces", forces.size());
        return forces.stream()
            .map(this::toForceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert Force entity to ForceResponse with player name and club name
     */
    private ForceResponse toForceResponse(Force force) {
        User user = userRepository.findById(force.getUserId())
            .orElse(null);
        Club club = clubRepository.findById(force.getClubId())
            .orElse(null);
        return new ForceResponse(force, user, club);
    }
    
    /**
     * Update a force
     */
    @Transactional
    public ForceResponse updateForce(Long id, CreateForceRequest request, Long userId) {
        logger.info("Updating force: {}", id);
        
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Force not found"));
        
        // Find user by ID
        User user = userRepository.findById(userId)
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
        if (request.getForceType() != null) {
            force.setForceType(request.getForceType());
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
        if (request.getRequisitionPoints() != null) {
            force.setRequisitionPoints(request.getRequisitionPoints());
        }
        if (request.getNotes() != null) {
            force.setNotes(request.getNotes());
        }
        
        // Handle force image URL if provided
        if (request.getImageUrl() != null) {
            force.setImageUrl(request.getImageUrl());
            logger.info("Force image updated for force {}: {}", id, request.getImageUrl());
        }
        if (request.getMfmVersion() != null) {
            force.setMfmVersion(request.getMfmVersion());
        }
        
        Force updatedForce = forceRepository.save(force);
        loggingService.logDatabaseOperation("forces", "UPDATE", true, 
            "Force updated: " + id);
        logger.info("Force updated successfully: {}", id);
        
        return toForceResponse(updatedForce);
    }
    
    /**
     * Soft delete a force
     */
    @Transactional
    public void deleteForce(Long id, Long userId) {
        logger.info("Deleting force: {}", id);
        
        Force force = forceRepository.findByIdAndDeletedTimestampIsNull(id)
            .orElseThrow(() -> new RuntimeException("Force not found"));
        
        // Find user by ID
        User user = userRepository.findById(userId)
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

