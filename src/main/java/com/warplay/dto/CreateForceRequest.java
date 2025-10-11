package com.warplay.dto;

public class CreateForceRequest {
    private Long clubId;
    private Long crusadeId;
    private String name;
    private String faction;
    private String subFaction;
    private String detachment;
    private Integer supplyLimit;
    private Integer supplyUsed;
    private Integer requisitionPoints;
    private Integer battlesWon;
    private Integer battlesLost;
    private String battlefieldRole;
    private String notes;
    
    // Constructors
    public CreateForceRequest() {
    }
    
    // Getters and Setters
    public Long getClubId() {
        return clubId;
    }
    
    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }
    
    public Long getCrusadeId() {
        return crusadeId;
    }
    
    public void setCrusadeId(Long crusadeId) {
        this.crusadeId = crusadeId;
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
    
    public Integer getSupplyUsed() {
        return supplyUsed;
    }
    
    public void setSupplyUsed(Integer supplyUsed) {
        this.supplyUsed = supplyUsed;
    }
    
    public Integer getRequisitionPoints() {
        return requisitionPoints;
    }
    
    public void setRequisitionPoints(Integer requisitionPoints) {
        this.requisitionPoints = requisitionPoints;
    }
    
    public Integer getBattlesWon() {
        return battlesWon;
    }
    
    public void setBattlesWon(Integer battlesWon) {
        this.battlesWon = battlesWon;
    }
    
    public Integer getBattlesLost() {
        return battlesLost;
    }
    
    public void setBattlesLost(Integer battlesLost) {
        this.battlesLost = battlesLost;
    }
    
    public String getBattlefieldRole() {
        return battlefieldRole;
    }
    
    public void setBattlefieldRole(String battlefieldRole) {
        this.battlefieldRole = battlefieldRole;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

