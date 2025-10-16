package com.warplay.dto;

public class CreateForceRequest {
    private Long clubId;
    private String name;
    private String faction;
    private String subFaction;
    private String detachment;
    private Integer supplyLimit;
    private Integer requisitionPoints;
    private String notes;
    private String imageUrl;
    
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

