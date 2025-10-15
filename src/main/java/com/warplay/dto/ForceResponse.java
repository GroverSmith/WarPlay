package com.warplay.dto;

import com.warplay.entity.Force;
import com.warplay.entity.User;

import java.time.LocalDateTime;

public class ForceResponse {
    private Long id;
    private Long clubId;
    private Long userId;
    private String playerName;
    private String name;
    private String faction;
    private String subFaction;
    private String detachment;
    private Integer supplyLimit;
    private Integer requisitionPoints;
    private String notes;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
    
    // Constructor from Force entity
    public ForceResponse(Force force) {
        this.id = force.getId();
        this.clubId = force.getClubId();
        this.userId = force.getUserId();
        this.name = force.getName();
        this.faction = force.getFaction();
        this.subFaction = force.getSubFaction();
        this.detachment = force.getDetachment();
        this.supplyLimit = force.getSupplyLimit();
        this.requisitionPoints = force.getRequisitionPoints();
        this.notes = force.getNotes();
        this.createdTimestamp = force.getCreatedTimestamp();
        this.updatedTimestamp = force.getUpdatedTimestamp();
    }
    
    // Constructor from Force and User
    public ForceResponse(Force force, User user) {
        this(force);
        this.playerName = user != null ? user.getName() : null;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClubId() {
        return clubId;
    }
    
    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFaction() {
        return faction;
    }
    
    public void setFaction(String faction) {
        this.faction = faction;
    }
    
    public String getSubFaction() {
        return subFaction;
    }
    
    public void setSubFaction(String subFaction) {
        this.subFaction = subFaction;
    }
    
    public String getDetachment() {
        return detachment;
    }
    
    public void setDetachment(String detachment) {
        this.detachment = detachment;
    }
    
    public Integer getSupplyLimit() {
        return supplyLimit;
    }
    
    public void setSupplyLimit(Integer supplyLimit) {
        this.supplyLimit = supplyLimit;
    }
    
    public Integer getRequisitionPoints() {
        return requisitionPoints;
    }
    
    public void setRequisitionPoints(Integer requisitionPoints) {
        this.requisitionPoints = requisitionPoints;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }
    
    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
}


